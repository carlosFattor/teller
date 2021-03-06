/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2015, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
 * or in writing
 * Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package models.service

import com.github.tototoshi.slick.JodaSupport._
import models._
import models.database.{ Evaluations, EventFacilitators, EventInvoices, Events }
import org.joda.time.LocalDate
import play.api.Play
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import services.integrations.Integrations

import scala.language.postfixOps
import scala.slick.lifted.Query

class EventService extends Integrations with Services {

  /**
   * Returns event if it exists, otherwise - None
   *
   * @param id Event identifier
   */
  def find(id: Long): Option[Event] = DB.withSession {
    implicit session: Session ⇒
      Query(Events).filter(_.id === id).firstOption
  }

  /**
   * Return event if it exists related to the given evaluation
   * @param evaluationId Evaluation id
   */
  def findByEvaluation(evaluationId: Long): Option[Event] = DB.withSession {
    implicit session ⇒
      val query = for {
        x ← Evaluations if x.id === evaluationId
        y ← Events if y.id === x.eventId
      } yield y
      query.firstOption
  }

  /**
   * Returns a list of events based on several parameters
   *
   * @param brandId Only events of this brand
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   * @param confirmed Only confirmed events
   * @param country Only events in this country
   * @param eventType Only events of this type
   */
  def findByParameters(
    brandId: Option[Long],
    future: Option[Boolean] = None,
    public: Option[Boolean] = None,
    archived: Option[Boolean] = None,
    confirmed: Option[Boolean] = None,
    country: Option[String] = None,
    eventType: Option[Long] = None): List[Event] = DB.withSession {
    implicit session: Session ⇒
      val baseQuery = Query(Events)

      val brandQuery = brandId map {
        v ⇒ baseQuery filter (_.brandId === v)
      } getOrElse baseQuery

      val timeQuery = applyTimeFilter(future, brandQuery)
      val publicityQuery = applyPublicityFilter(public, timeQuery)
      val archivedQuery = applyArchivedFilter(archived, publicityQuery)

      val confirmedQuery = confirmed map { value ⇒
        archivedQuery filter (_.confirmed === value)
      } getOrElse archivedQuery

      val countryQuery = country map { value ⇒
        confirmedQuery filter (_.countryCode === value)
      } getOrElse confirmedQuery

      val typeQuery = eventType map { value ⇒
        countryQuery filter (_.eventTypeId === value)
      } getOrElse countryQuery

      typeQuery sortBy (_.start) list
  }

  /**
   * Return a list of events for a given facilitator
   *
   * @param facilitatorId Only events facilitated by this facilitator
   * @param brandId Only events of this brand
   * @param future Only future and current events
   * @param public Only public events
   * @param archived Only archived events
   */
  def findByFacilitator(
    facilitatorId: Long,
    brandId: Option[Long] = None,
    future: Option[Boolean] = None,
    public: Option[Boolean] = None,
    archived: Option[Boolean] = None): List[Event] = DB.withSession {
    implicit session: Session ⇒

      val baseQuery = brandId map { value ⇒
        for {
          entry ← EventFacilitators if entry.facilitatorId === facilitatorId
          event ← Events if event.id === entry.eventId && event.brandId === value
        } yield event
      } getOrElse {
        for {
          entry ← EventFacilitators if entry.facilitatorId === facilitatorId
          event ← Events if event.id === entry.eventId
        } yield event
      }

      val timeQuery = applyTimeFilter(future, baseQuery)
      val publicityQuery = applyPublicityFilter(public, timeQuery)
      val archivedQuery = applyArchivedFilter(archived, publicityQuery)

      archivedQuery sortBy (_.start) list
  }

  /** Returns list with active events */
  def findActive: List[Event] = DB.withSession { implicit session: Session ⇒
    findByParameters(brandId = None, archived = Some(false)).
      sortBy(_.title.toLowerCase)
  }

  /** Returns list with all events */
  def findAll: List[Event] = DB.withSession { implicit session: Session ⇒
    findByParameters(brandId = None)
  }

  /**
   * Adds event and related objects to database
   *
   * @param event Event object
   * @return Updated event object with id
   */
  def insert(event: Event): Event = DB.withTransaction { implicit session: Session ⇒
    val insertTuple = (event.eventTypeId, event.brandId, event.title,
      event.language.spoken, event.language.secondSpoken, event.language.materials,
      event.location.city, event.location.countryCode, event.details.description,
      event.details.specialAttention, event.details.webSite,
      event.details.registrationPage, event.schedule.start, event.schedule.end,
      event.schedule.hoursPerDay, event.schedule.totalHours, event.notPublic,
      event.archived, event.confirmed, event.free)
    val id = Events.forInsert.insert(insertTuple)
    event.facilitatorIds.distinct.foreach(facilitatorId ⇒
      EventFacilitators.insert((id, facilitatorId)))
    EventInvoices.forInsert.insert(event.invoice.copy(eventId = Some(id)))
    event.copy(id = Some(id))
  }

  /**
   * Fill events with facilitators (using only one query to database)
   * TODO: Cover with tests
   * @param events List of events
   * @return
   */
  def applyFacilitators(events: List[Event]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = events.map(_.id.get).distinct.toList
    val query = for {
      facilitation ← EventFacilitators if facilitation.eventId inSet ids
      person ← facilitation.facilitator
    } yield (facilitation.eventId, person)
    val facilitationData = query.list
    val facilitators = facilitationData.map(_._2).distinct
    PeopleCollection.addresses(facilitators)
    facilitationData.foreach(f ⇒ f._2.address_=(facilitators.find(_.id == f._2.id).get.address))
    val groupedFacilitators = facilitationData.groupBy(_._1).map(f ⇒ (f._1, f._2.map(_._2)))
    events.foreach(e ⇒ e.facilitators_=(groupedFacilitators.getOrElse(e.id.get, List())))
  }

  /**
   * Sends email notifications to facilitators asking to confirm or delete
   *  past events which are unconfirmed
   */
  def sendConfirmationAlert() = brandService.findAll.foreach { brand ⇒
    findByParameters(
      brandId = brand.id,
      future = Some(false),
      confirmed = Some(false)).foreach { event ⇒
        val subject = "Confirm your event " + event.title
        val url = Play.configuration.getString("application.baseUrl").getOrElse("")
        val body = mail.html.confirm(event, brand, url).toString()
        email.send(
          event.facilitators.toSet,
          None,
          None,
          subject,
          body,
          richMessage = true)
        val msg = "confirmation email for event %s (id = %s)".format(
          event.title,
          event.id.get.toString)
        Activity.insert("Teller", Activity.Predicate.Sent, msg)
      }
  }

  /**
   * Fill events with invoices (using only one query to database)
   * @todo Cover with tests
   * @param events List of events
   * @return
   */
  def applyInvoices(events: List[Event]): Unit = DB.withSession { implicit session: Session ⇒
    val ids = events.map(_.id.get).distinct.toList
    val query = for {
      invoice ← EventInvoices if invoice.eventId inSet ids
    } yield invoice
    val invoices = query.list
    events.foreach(e ⇒
      e.invoice_=(invoices
        .find(_.eventId == e.id)
        .getOrElse(EventInvoice(None, None, 0, None, None))))
  }

  /**
   * Updates event and related objects in database
   *
   * @param event Event
   * @return Updated event object with id
   */
  def update(event: Event): Event = DB.withSession { implicit session: Session ⇒
    val updateTuple = (event.eventTypeId, event.brandId, event.title,
      event.language.spoken, event.language.secondSpoken, event.language.materials,
      event.location.city, event.location.countryCode, event.details.description,
      event.details.specialAttention, event.details.webSite,
      event.details.registrationPage, event.schedule.start, event.schedule.end,
      event.schedule.hoursPerDay, event.schedule.totalHours, event.notPublic,
      event.archived, event.confirmed, event.free)

    val updateQuery = Events.filter(_.id === event.id).map(_.forUpdate)
    updateQuery.update(updateTuple)
    EventFacilitators.where(_.eventId === event.id).mutate(_.delete())
    event.facilitatorIds.distinct.foreach(facilitatorId ⇒
      EventFacilitators.insert((event.id.get, facilitatorId)))
    EventInvoice._update(event.invoice)

    event
  }

  /**
   * Updates rating for the given event
   * @param eventId Event id
   * @param rating New rating
   */
  def updateRating(eventId: Long, rating: Float): Unit = DB.withSession {
    implicit session: Session ⇒
      Query(Events).filter(_.id === eventId).map(_.rating).update(rating)
  }

  /**
   * Applies time filter on query
   *
   * @param future Defines if time filter should be applied
   * @param parentQuery Query to apply this filter to
   * @return returns updated query if 'future' flag is defined
   */
  private def applyTimeFilter(
    future: Option[Boolean],
    parentQuery: Query[Events.type, Event]) = {
    future map { value ⇒
      val now = LocalDate.now
      val today = new LocalDate(
        now.getValue(0),
        now.getValue(1),
        now.getValue(2))
      if (value)
        parentQuery.filter(_.start > today)
      else
        parentQuery.filter(_.end <= today)
    } getOrElse parentQuery
  }

  /**
   * Applies publicity filter on query
   *
   * @param public Defines if publicity filter should be applied
   * @param parentQuery Query to apply this filter to
   * @return returns updated query if 'public' flag is defined
   */
  private def applyPublicityFilter(
    public: Option[Boolean],
    parentQuery: Query[Events.type, Event]) = {
    public map { value ⇒
      parentQuery.filter(_.notPublic === !value)
    } getOrElse parentQuery
  }

  /**
   * Applies archived filter on query
   *
   * @param archived Defines if archived filter should be applied
   * @param parentQuery Query to apply this filter to
   * @return returns updated query if 'archived' flag is defined
   */
  private def applyArchivedFilter(
    archived: Option[Boolean],
    parentQuery: Query[Events.type, Event]) = {
    archived map { value ⇒
      parentQuery.filter(_.archived === value)
    } getOrElse parentQuery
  }
}

object EventService {
  private val instance = new EventService()

  def get: EventService = instance

}
