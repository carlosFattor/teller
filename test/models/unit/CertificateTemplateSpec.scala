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
package models.unit

import models.Activity
import models.brand.CertificateTemplate
import org.specs2.mutable._

class CertificateTemplateSpec extends Specification {

  "Certificate template" should {
    "have well-formed activity attributes" in {
      val tpl = new CertificateTemplate(Some(1L), 1L, "EN", Array(), Array())
      tpl.objectType must_== Activity.Type.CertificateTemplate
      tpl.identifier must_== 1
      tpl.humanIdentifier must_== "for brand 1 and lang EN"
      val tpl2 = new CertificateTemplate(Some(2L), 2L, "RU", Array(), Array())
      tpl2.objectType must_== Activity.Type.CertificateTemplate
      tpl2.identifier must_== 2
      tpl2.humanIdentifier must_== "for brand 2 and lang RU"
    }
  }

}
