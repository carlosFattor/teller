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
package controllers.api

import models.Brand
import models.brand.EventType
import models.service.Services
import play.api.libs.json._
import play.mvc.Controller

/**
 * Event types API
 */
object EventTypesApi extends Controller with ApiAuthentication with Services {

  implicit val eventTypeWrites = new Writes[EventType] {
    def writes(eventType: EventType): JsValue = {
      Json.obj(
        "id" -> eventType.id,
        "brand" -> eventType.brand.code,
        "name" -> eventType.name,
        "defaultTitle" -> eventType.defaultTitle)
    }
  }

  /**
   * Event types list API.
   */
  def types(brandCode: String) = TokenSecuredAction { implicit request ⇒
    Ok(Json.prettyPrint(Json.toJson(eventTypeService.findByBrand(Brand.find(brandCode).map(_.brand.id.get).getOrElse(0)))))
  }
}
