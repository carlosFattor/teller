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
package controllers.apiv2

import models.Brand
import models.brand.EventType
import models.service.Services
import play.api.libs.json._
import play.mvc.Controller

/**
 * Event types API
 */
trait EventTypesApi extends Controller with ApiAuthentication with Services {

  implicit val eventTypeWrites = new Writes[EventType] {
    def writes(eventType: EventType): JsValue = {
      Json.obj(
        "id" -> eventType.id,
        "brand" -> eventType.brand.code,
        "name" -> eventType.name,
        "title" -> eventType.defaultTitle)
    }
  }

  /**
   * Returns a list of event types for the given brand in JSON format
   * @param code Brand code
   */
  def types(code: String) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        val brandId = Brand.find(code) map (_.brand.id.get) getOrElse 0L
        jsonOk(Json.toJson(eventTypeService.findByBrand(brandId)))
  }
}

object EventTypesApi extends EventTypesApi with ApiAuthentication
