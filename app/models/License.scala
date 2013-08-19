package models

import com.github.tototoshi.slick.JodaSupport._
import models.database.{ Addresses, People, Brands, Licenses }
import org.joda.money.{ CurrencyUnit, Money }
import org.joda.time.{ Interval, LocalDate }
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current

/**
 * A content license - a person’s agreement with Happy Melly to use a `Brand`.
 */
case class License(
  id: Option[Long],
  licenseeId: Long,
  brandId: Long,
  version: String,
  signed: LocalDate,
  start: LocalDate,
  end: LocalDate,
  confirmed: Boolean,
  fee: Money,
  feePaid: Option[Money])

case class LicenseView(brand: Brand, license: License) {
  def active: Boolean = new Interval(license.start.toDateMidnight, license.end.toDateMidnight).containsNow
}

case class LicenseLicenseeBrandView(license: License, brand: Brand, licensee: Person)

object License {

  def delete(id: Long): Unit = withSession { implicit session ⇒
    Licenses.filter(_.id === id).mutate(_.delete)
  }

  /**
   * Returns a blanks license with default values, for the given licensee, for editing.
   */
  def blank(personId: Long) = {
    License(None, 0, personId, "", LocalDate.now, LocalDate.now, LocalDate.now.plusYears(1), false,
      Money.of(CurrencyUnit.EUR, 0f), Some(Money.of(CurrencyUnit.EUR, 0f)))
  }

  /**
   * Finds a license by ID.
   */
  def find(id: Long): Option[License] = withSession { implicit session ⇒
    Query(Licenses).filter(_.id === id).firstOption
  }

  /**
   * Finds a license by ID, joined with brand and licensee.
   */
  def findWithBrandAndLicensee(id: Long): Option[LicenseLicenseeBrandView] = withSession { implicit session ⇒
    val query = for {
      license ← Licenses if license.id === id
      brand ← license.brand
      licensee ← license.licensee
    } yield (license, brand, licensee)

    query.mapResult {
      case (license, brand, licensee) ⇒
        LicenseLicenseeBrandView(license, brand, licensee)
    }.firstOption
  }

  def insert(license: License) = withSession { implicit session ⇒
    val id = Licenses.forInsert.insert(license)
    license.copy(id = Some(id))
  }

  /** Finds a licensee by license ID **/
  def licensee(licenseId: Long): Option[Person] = withSession { implicit session ⇒
    val query = for {
      license ← Licenses if license.id === licenseId
      licensee ← license.licensee
    } yield licensee
    query.firstOption
  }

  /**
   * Returns a list of people who are licensed for the given brand on the given date, usually today.
   */
  def licensees(brandCode: String, date: LocalDate = LocalDate.now()): List[Person] = withSession { implicit session ⇒
    val query = for {
      license ← Licenses if license.start <= date && license.end >= date
      brand ← license.brand if brand.code === brandCode
      licensee ← license.licensee
    } yield licensee
    query.sortBy(_.lastName.toLowerCase).list
  }

  /**
   * Returns a list of content licenses for the given person.
   */
  def licenses(personId: Long): List[LicenseView] = withSession { implicit session ⇒

    val query = for {
      license ← Licenses if license.licenseeId === personId
      brand ← license.brand
    } yield (license, brand)

    query.sortBy(_._2.name.toLowerCase).list.map {
      case (license, brand) ⇒ LicenseView(brand, license)
    }
  }

  /**
   * Updates this license in the database.
   */
  def update(license: License): Unit = withSession { implicit session ⇒
    license.id.map { id ⇒
      Query(Licenses).filter(_.id === id).update(license)
    }
  }

}