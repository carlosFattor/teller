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

package models.database

import com.github.tototoshi.slick.JodaSupport._
import models.{ ProductCategory, Product }
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

/**
 * `Product` database table mapping.
 */
private[models] object Products extends Table[Product]("PRODUCT") {

  implicit val productCategoryTypeMapper = MappedTypeMapper.base[ProductCategory.Value, String](
    { category ⇒ category.toString },
    { category ⇒ ProductCategory.withName(category) })

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def title = column[String]("TITLE")
  def subtitle = column[Option[String]]("SUBTITLE")
  def url = column[Option[String]]("URL")
  def description = column[Option[String]]("DESCRIPTION")
  def callToActionUrl = column[Option[String]]("CALL_TO_ACTION_URL")
  def callToActionText = column[Option[String]]("CALL_TO_ACTION_TEXT")
  def picture = column[Option[String]]("PICTURE")
  def category = column[Option[ProductCategory.Value]]("CATEGORY")
  def parentId = column[Option[Long]]("PARENT_ID")

  def created = column[DateTime]("CREATED")
  def createdBy = column[String]("CREATED_BY")

  def updated = column[DateTime]("UPDATED")
  def updatedBy = column[String]("UPDATED_BY")

  def parent = Products.where(_.id === parentId)

  def * = id.? ~ title ~ subtitle ~ url ~ description ~ callToActionUrl ~ callToActionText ~ picture ~
    category ~ parentId ~ created ~ createdBy ~ updated ~ updatedBy <> (Product.apply _, Product.unapply _)

  def forInsert = * returning id

  def forUpdate = title ~ subtitle ~ url ~ description ~ callToActionUrl ~ callToActionText ~ picture ~
    category ~ parentId ~ updated ~ updatedBy

}
