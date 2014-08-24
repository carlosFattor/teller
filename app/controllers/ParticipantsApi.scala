/*
 * Happy Melly Teller
 * Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com
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
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */
package controllers

import models._
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json._
import views.Countries

/**
 * Participants API
 */
object ParticipantsApi extends ApiAuthentication {

  def participantForm(account: UserAccount, userName: String) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "event_id" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
      "first_name" -> nonEmptyText,
      "last_name" -> nonEmptyText,
      "birthday" -> optional(jodaLocalDate),
      "email" -> email,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText.verifying(
        "error.unknown_country",
        (country: String) ⇒ Countries.all.exists(_._1 == country)),
      "street_1" -> optional(nonEmptyText),
      "street_2" -> optional(nonEmptyText),
      "postcode" -> optional(nonEmptyText),
      "province" -> optional(nonEmptyText),
      "organisation" -> optional(nonEmptyText),
      "comment" -> optional(nonEmptyText)) ({
        (id, event_id, first_name, last_name, birthday, email, city, country, street_1, street_2, postcode, province,
        organisation, comment) ⇒
          ParticipantData(id, event_id, first_name, last_name, birthday, email,
            Address(None, street_1, street_2, Some(city), province, postcode, country), organisation, comment,
            DateTime.now(), userName, DateTime.now(), userName)
      }) ({
        (p: ParticipantData) ⇒
          Some((p.id, p.eventId, p.firstName, p.lastName, p.birthday, p.emailAddress,
            p.address.city.getOrElse(""), p.address.countryCode, p.address.street1, p.address.street2,
            p.address.postCode, p.address.province, p.organisation, p.comment))
      }))
  }

  def existingPersonForm(account: UserAccount) = {
    Form(mapping(
      "id" -> ignored(Option.empty[Long]),
      "event_id" -> longNumber.verifying(
        "error.event.invalid",
        (eventId: Long) ⇒ Event.canManage(eventId, account)),
      "person_id" -> longNumber.verifying(
        "error.person.notExist",
        (participantId: Long) ⇒ Person.find(participantId).nonEmpty))({
        (id, event_id, person_id) ⇒
          Participant(id = None, event_id, person_id, evaluationId = None, organisation = None, comment = None)
      })({
        (p: Participant) ⇒ Some(p.id, p.eventId, p.participantId)
      }))
  }

  /**
   * Create a participant through API call
   */
  def create = TokenSecuredActionWithIdentity { (request: Request[AnyContent], identity: LoginIdentity) ⇒
    val person = identity.person

    val testFrom = existingPersonForm(identity.userAccount).bindFromRequest()(request)
    if (testFrom.data.contains("person_id")) {
      val form: Form[Participant] = existingPersonForm(identity.userAccount).bindFromRequest()(request)
      form.fold(
        formWithErrors ⇒ {
          val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
          BadRequest(Json.prettyPrint(json))
        },
        participant ⇒ {
          val createdParticipant = Participant.insert(participant)
          val activityObject = Messages("activity.participant.create",
            participant.participant.get.fullName, participant.event.get.title)
          Activity.insert(person.fullName, Activity.Predicate.Created, activityObject)
          Ok(Json.prettyPrint(Json.obj("participant_id" -> createdParticipant.participantId)))
        })
    } else {
      val form: Form[ParticipantData] = participantForm(identity.userAccount, person.fullName).bindFromRequest()(request)

      form.fold(
        formWithErrors ⇒ {
          val json = Json.toJson(APIError.formValidationError(formWithErrors.errors))
          BadRequest(Json.prettyPrint(json))
        },
        data ⇒ {
          val participant = Participant.create(data)
          val activityObject = Messages("activity.participant.create", data.firstName + " " + data.lastName, data.event.get.title)
          Activity.insert(person.fullName, Activity.Predicate.Created, activityObject)
          Ok(Json.prettyPrint(Json.obj("participant_id" -> participant.participantId)))
        })
    }

  }
}