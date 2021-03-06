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
 * terms, you may contact by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package controllers.acceptance

import _root_.integration.PlayAppSpec
import controllers.apiv2.ApiAuthentication
import models.admin.ApiToken
import models.service.admin.ApiTokenService
import org.scalamock.specs2.IsolatedMockFactory
import play.api.mvc.Results._
import play.api.mvc.{ AnyContent, Request, Result, SimpleResult }
import play.api.test.FakeRequest
import stubs.FakeServices

class ApiAuthenticationSpec extends PlayAppSpec with IsolatedMockFactory {
  override def is = s2"""

  A user should
    get Unauthorized if api token is not present in a query string $e1
    get Unauthorized if token does not exists                      $e2
    get Unauthorized if token is unauthorized to run the action    $e3
    get OK if token is authorized to run the action                $e4
    get OK if token is found in db and is authorized to run the action $e5
  """

  class TestApiAuthentication extends ApiAuthentication with FakeServices {

    def readOnly = TokenSecuredAction(readWrite = false) { implicit request ⇒
      implicit token ⇒
        Ok("ok")
    }

    def readWrite = TokenSecuredAction(readWrite = true) { implicit request ⇒
      implicit token ⇒
        Ok("ok")
    }

    def callAuthorize(readWrite: Boolean,
      token: ApiToken)(f: Request[AnyContent] ⇒ ApiToken ⇒ Result)(implicit request: Request[AnyContent]): Result =
      authorize(readWrite, token)(f)(request)
  }

  val controller = new TestApiAuthentication
  val apiTokenService = mock[ApiTokenService]
  controller.apiTokenService_=(apiTokenService)

  def e1 = {
    val res = controller.readOnly().apply(FakeRequest())
    status(res) must_== UNAUTHORIZED
  }

  def e2 = {
    val req = FakeRequest("GET", "readonly?api_token=test")
    (apiTokenService.find(_: String)).expects("test").returning(None)
    val res = controller.readOnly().apply(req)
    status(res) must_== UNAUTHORIZED
  }

  def e3 = {
    val req = FakeRequest("GET", "readwrite?api_token=test")
    val token = ApiToken(None, "test", "test", "", None, readWrite = false)
    val res: Result = controller.callAuthorize(readWrite = true, token)(_ ⇒ token ⇒ Ok("ok"))(req)
    res must beAnInstanceOf[SimpleResult]
    res.asInstanceOf[SimpleResult].header.status must_== UNAUTHORIZED
  }

  def e4 = {
    val req = FakeRequest("GET", "readwrite?api_token=test")
    val token = ApiToken(None, "test", "test", "", None, readWrite = true)
    val res: Result = controller.callAuthorize(readWrite = false, token)(_ ⇒ token ⇒ Ok("ok"))(req)
    res must beAnInstanceOf[SimpleResult]
    res.asInstanceOf[SimpleResult].header.status must_== OK
  }

  def e5 = {
    val req = FakeRequest("GET", "readwrite?api_token=test")
    val token = Some(ApiToken(None, "test", "test", "", None, readWrite = true))
    (apiTokenService.find(_: String)).expects("test").returning(token)
    val res = controller.readOnly().apply(req)
    status(res) must_== OK
  }
}
