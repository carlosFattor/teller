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
package helpers

import models.{ DateStamp, Organisation }
import org.joda.time.DateTime
import stubs.FakeServices

class TestOrganisation(
  id: Option[Long],
  name: String,
  street1: Option[String],
  street2: Option[String],
  city: Option[String],
  province: Option[String],
  postCode: Option[String],
  countryCode: String,
  vatNumber: Option[String],
  registrationNumber: Option[String],
  webSite: Option[String],
  blog: Option[String],
  customerId: Option[String] = None,
  active: Boolean = true,
  dateStamp: DateStamp) extends Organisation(id, name, street1, street2, city,
  province, postCode, countryCode, vatNumber, registrationNumber, webSite,
  blog, customerId, active, dateStamp) with FakeServices

object OrganisationHelper {

  def make(id: Option[Long] = None,
    name: String,
    street1: Option[String] = None,
    street2: Option[String] = None,
    city: Option[String] = None,
    province: Option[String] = None,
    postCode: Option[String] = None,
    countryCode: String = "RU",
    vatNumber: Option[String] = None,
    registrationNumber: Option[String] = None,
    webSite: Option[String] = None,
    blog: Option[String] = None,
    active: Boolean = true,
    created: DateTime = DateTime.now(),
    createdBy: String = "Sergey Kotlov",
    updated: DateTime = DateTime.now(),
    updatedBy: String = "Sergey Kotlov"): TestOrganisation = {
    new TestOrganisation(id, name, street1, street2, city, province, postCode,
      countryCode, vatNumber, registrationNumber, webSite, blog, Some(""),
      active, DateStamp(created, createdBy, updated, updatedBy))
  }

  def one: TestOrganisation = make(id = Some(1L), name = "One")

  def two: TestOrganisation = make(id = Some(2L), name = "Two")
}
