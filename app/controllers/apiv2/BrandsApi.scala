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

import models.{ Brand, BrandView }
import play.api.libs.json._
import play.mvc.Controller

/**
 * Brands API
 */
trait BrandsApi extends Controller with ApiAuthentication {

  implicit val brandWrites = new Writes[Brand] {
    def writes(brand: Brand): JsValue = {
      Json.obj(
        "code" -> brand.code,
        "unique_name" -> brand.uniqueName,
        "name" -> brand.name)
    }
  }

  implicit val brandViewWrites = new Writes[BrandView] {
    def writes(brandView: BrandView): JsValue = {
      Json.obj(
        "unique_name" -> brandView.brand.uniqueName,
        "name" -> brandView.brand.name,
        "image" -> brandView.brand.picture.map(picture ⇒
          controllers.routes.Brands.picture(brandView.brand.code).url),
        "tagline" -> brandView.brand.tagLine,
        "products" -> brandView.brand.products.length)
    }
  }
  import PeopleApi.personWrites
  import ProductsApi.productWrites

  val brandViewDetailsWrites = new Writes[BrandView] {
    def writes(brandView: BrandView): JsValue = {
      Json.obj(
        "code" -> brandView.brand.code,
        "unique_name" -> brandView.brand.uniqueName,
        "name" -> brandView.brand.name,
        "tagline" -> brandView.brand.tagLine,
        "description" -> brandView.brand.description,
        "image" -> brandView.brand.picture.map(picture ⇒
          controllers.routes.Brands.picture(brandView.brand.code).url),
        "coordinator" -> brandView.coordinator,
        "contact_info" -> Json.obj(
          "email" -> brandView.brand.socialProfile.email,
          "skype" -> brandView.brand.socialProfile.skype,
          "phone" -> brandView.brand.socialProfile.phone),
        "social_profile" -> Json.obj(
          "facebook" -> brandView.brand.socialProfile.facebookUrl,
          "twitter" -> brandView.brand.socialProfile.twitterHandle,
          "google_plus" -> brandView.brand.socialProfile.googlePlusUrl,
          "linkedin" -> brandView.brand.socialProfile.linkedInUrl),
        "website" -> brandView.brand.webSite,
        "blog" -> brandView.brand.blog,
        "products" -> brandView.brand.products)
    }
  }

  /**
   * Returns brand in JSON format if the brand exists, otherwise - Not Found
   * @param code Brand code
   */
  def brand(code: String) = TokenSecuredAction(readWrite = false) {
    implicit request ⇒
      implicit token ⇒
        Brand.find(code) map { data ⇒
          jsonOk(Json.toJson(data)(brandViewDetailsWrites))
        } getOrElse {
          Brand.findByName(code) map { data ⇒
            jsonOk(Json.toJson(data)(brandViewDetailsWrites))
          } getOrElse jsonNotFound("Unknown brand")
        }
  }

  /**
   * Returns a list of brands in JSON format
   */
  def brands = TokenSecuredAction(readWrite = false) { implicit request ⇒
    implicit token ⇒
      jsonOk(Json.toJson(Brand.findAllWithCoordinator))
  }
}

object BrandsApi extends BrandsApi