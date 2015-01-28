/*
* Happy Melly Teller
* Copyright (C) 2013 - 2015, Happy Melly http -> //www.happymelly.com
*
* This file is part of the Happy Melly Teller.
*
* Happy Melly Teller is free software ->  you can redistribute it and/or modify
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
* along with Happy Melly Teller.  If not, see <http -> //www.gnu.org/licenses/>.
*
* If you have questions concerning this license or the applicable additional
* terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com
* or in writing Happy Melly One, Handelsplein 37, Rotterdam,
* The Netherlands, 3071 PR
*/
package acceptance

import controllers._
import integration.PlayAppSpec
import models.Member
import org.joda.money.Money
import org.joda.time.{ DateTime, LocalDate }
import org.scalamock.specs2.MockContext
import org.specs2.matcher._
import play.api.db.slick._
import play.api.cache.Cache
import play.api.mvc.SimpleResult
import play.api.test.FakeRequest
import play.api.Play.current
import stubs.{ FakeMemberService, StubLoginIdentity, FakeServices }

import scala.concurrent.Future
import scala.slick.jdbc.{ StaticQuery ⇒ Q }
import scala.slick.session.Session

class TestMembers() extends Members with Security with FakeServices

class MembersSpec extends PlayAppSpec with DataTables {
  def setupDb() {}
  def cleanupDb() {}

  override def is = s2"""

  Page with a list of members should
    not be visible to unauthorized user                  $e1
    and be visible to authorized user                    $e2
    show all members sorted by names                     $e3

  Add Fee form should
    not be accessible to Viewers                         $e4
    be accessible to Editors                             $e5

  Editor should
    not be able to add new member with wrong parameters            $e6
    get a correct error message if membership date is too early    $e7
    get a correct error message if membership date is too late     $e8
    be redirected to 'New Organisation' form if he chose 'Org'     $e9
    be redirected to 'New Person' form if he chose 'Person'        $e10
    be redirected to 'Existing Org' form if he chose 'Org'         $e11
    be redirected to 'Existing Person' form if he chose 'Person'   $e12

  If an editor tries to create an organisation without creating membership fee first then
    she should get an error message                                $e14

  Add new organisation form should
    not be accessible to Viewers                         $e15
    be accessible to Editors                             $e16

  Add new person form should
    not be accessible to Viewers                         $e17
    be accessible to Editors                             $e18

  If an editor tries to create a person without creating membership fee first then
    she should get an error message                                       $e19

  Add existing organisation form should
    not be accessible to Viewers                         $e20
    be accessible to Editors                             $e21

  Add existing person form should
    not be accessible to Viewers                         $e22
    be accessible to Editors                             $e23
  """

  def e1 = {
    val controller = new TestMembers()
    val result: Future[SimpleResult] = controller.index().apply(FakeRequest())

    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome.which(_.contains("login"))
  }

  def e2 = {
    new MockContext {
      val controller = new TestMembers()
      val identity = StubLoginIdentity.viewer
      val request = prepareSecuredGetRequest(identity, "/members/")

      val service = mock[FakeMemberService]
      (service.findAll _).expects().returning(List()).once()
      controller.memberService_=(service)

      val result: Future[SimpleResult] = controller.index().apply(request)
      status(result) must equalTo(OK)
    }
  }

  def e3 = {
    //@TODO use FakeSecurity here
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/members")

    val controller = new TestMembers()
    val result: Future[SimpleResult] = controller.index().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Members")
    contentAsString(result) must contain("/member/1")
    contentAsString(result) must contain("/member/2")
    contentAsString(result) must contain("/member/3")
    contentAsString(result) must contain("/member/4")
    //@TODO finish multiple checks
  }

  def e4 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/member/")

    val result: Future[SimpleResult] = controller.add().apply(request)
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e5 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/member/")

    val result: Future[SimpleResult] = controller.add().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
  }

  def e6 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor

    "objectId" || "person" | "funder" | "currency" | "amount" | "since" | "existingObject" |
      // empty currency
      "0" !! "1" ! "false" ! "" ! "100" ! "2015-01-01" ! "true" |
      // unknown currency
      "0" !! "1" ! "false" ! "TERES" ! "100" ! "2015-01-01" ! "true" |
      // negative amount
      "0" !! "1" ! "false" ! "EUR" ! "-100" ! "2015-01-01" ! "true" |
      // zero amount
      "0" !! "1" ! "false" ! "EUR" ! "0.00" ! "2015-01-01" ! "false" |
      // empty since
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "" ! "false" |
      // wrong since
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "31-312-321" ! "true" |
      // since earlier than 2015-01-01
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "2014-12-31" ! "false" |
      // since later than today
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! LocalDate.now().plusDays(1).toString ! "false" |
      // empty 'funder'
      "0" !! "1" ! "" ! "EUR" ! "105.05" ! "2015-01-01" ! "true" |
      // non-boolean 'funder'
      "0" !! "1" ! "1.00" ! "EUR" ! "105.05" ! "2015-01-01" ! "false" |
      // empty 'existingObject'
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "2015-01-01" ! "" |
      // non-boolean 'existingObject'
      "0" !! "1" ! "false" ! "EUR" ! "105.05" ! "2015-01-01" ! "1.00" |> {
        (objectId, person, funder, currency, amount, since, existingObject) ⇒
          {
            val data = Seq()
            val request = prepareSecuredPostRequest(identity, "/member/new").
              withFormUrlEncodedBody(("objectId", objectId), ("person", person), ("funder", funder),
                ("fee.currency", currency), ("fee.amount", amount), ("since", since),
                ("existingObject", existingObject))
            val result: Future[SimpleResult] = controller.create().apply(request)
            status(result) must equalTo(BAD_REQUEST)
          }
      }
  }

  def e7 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/new").
      withFormUrlEncodedBody(("objectId", "0"), ("person", "1"), ("funder", "false"),
        ("fee.currency", "EUR"), ("fee.amount", "100"), ("since", "2014-01-01"))
    val result: Future[SimpleResult] = controller.create().apply(request)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Membership date cannot be earlier than 2015-01-01")
  }

  def e8 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/new").
      withFormUrlEncodedBody(("objectId", "0"), ("person", "1"), ("funder", "false"),
        ("fee.currency", "EUR"), ("fee.amount", "100"),
        ("since", LocalDate.now().plusDays(3).toString))
    val result: Future[SimpleResult] = controller.create().apply(request)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("Membership date cannot be later than today")
  }

  def e9 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val person = "0"
    val request = prepareSecuredPostRequest(identity, "/member/new").
      withFormUrlEncodedBody(("objectId", "0"), ("person", person),
        ("fee.currency", "EUR"), ("fee.amount", "100"),
        ("since", "2015-01-03"))
    val result: Future[SimpleResult] = controller.create().apply(request)
    // we can already clean up
    Cache.remove(controller.cacheId(1L))
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/new/organisation"
  }

  def e10 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val person = "1"
    val request = prepareSecuredPostRequest(identity, "/member/new").
      withFormUrlEncodedBody(("objectId", "0"), ("person", person),
        ("fee.currency", "EUR"), ("fee.amount", "100"),
        ("since", "2015-01-03"))
    val result: Future[SimpleResult] = controller.create().apply(request)
    status(result) must equalTo(SEE_OTHER)
    // we can already clean up
    Cache.remove(controller.cacheId(1L))
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/new/person"
  }

  def e11 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val person = "0"
    val request = prepareSecuredPostRequest(identity, "/member/new").
      withFormUrlEncodedBody(("objectId", "0"), ("person", person),
        ("fee.currency", "EUR"), ("fee.amount", "100"),
        ("existingObject", "true"), ("since", "2015-01-03"))
    val result: Future[SimpleResult] = controller.create().apply(request)
    // we can already clean up
    Cache.remove(controller.cacheId(1L))
    status(result) must equalTo(SEE_OTHER)
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/existing/organisation"
  }

  def e12 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val person = "1"
    val request = prepareSecuredPostRequest(identity, "/member/new").
      withFormUrlEncodedBody(("objectId", "0"), ("person", person),
        ("fee.currency", "EUR"), ("fee.amount", "100"),
        ("existingObject", "true"), ("since", "2015-01-03"))
    val result: Future[SimpleResult] = controller.create().apply(request)
    status(result) must equalTo(SEE_OTHER)
    // we can already clean up
    Cache.remove(controller.cacheId(1L))
    headers(result).get("Location").nonEmpty must_== true
    headers(result).get("Location").get must_== "/member/existing/person"
  }

  def e14 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/organisation").
      withFormUrlEncodedBody(("name", "Test"), ("country", "RU"))
    val result = controller.createNewOrganisation().apply(request)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("You are trying to complete step 2 while adding new member without completing step 1")
  }

  def e15 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addOrganisation().apply(request)
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e16 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addOrganisation().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: New organisation")
  }

  def e17 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addOrganisation().apply(request)
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e18 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addPerson().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: New person")
  }

  def e19 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredPostRequest(identity, "/member/person").
      withFormUrlEncodedBody(("emailAddress", "ttt@ttt.ru"), ("address.country", "RU"),
        ("firstName", "Test"), ("lastName", "Test"), ("signature", "false"),
        ("role", "0"))
    val result = controller.createNewPerson().apply(request)
    status(result) must equalTo(BAD_REQUEST)
    contentAsString(result) must contain("You are trying to complete step 2 while adding new member without completing step 1")
  }

  def e20 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addExistingOrganisation().apply(request)
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e21 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addExistingOrganisation().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: Existing organisation")
  }

  def e22 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.viewer
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addExistingPerson().apply(request)
    status(result) must equalTo(SEE_OTHER)
    header("Location", result) must beSome("/")
  }

  def e23 = {
    val controller = new TestMembers()
    val identity = StubLoginIdentity.editor
    val request = prepareSecuredGetRequest(identity, "/")

    val result: Future[SimpleResult] = controller.addExistingPerson().apply(request)
    status(result) must equalTo(OK)
    contentAsString(result) must contain("Add member")
    contentAsString(result) must contain("Step 2: Existing person")
  }
}

