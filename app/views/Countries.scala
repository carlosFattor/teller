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

package views

object Countries {

  /**
   * Returns country name by its code
   * @param code Country code
   */
  def name(code: String): String = all.find(_._1 == code) map { _._2 } getOrElse ""

  val all =
    List(("AF", "Afghanistan"),
      ("AL", "Albania"),
      ("DZ", "Algeria"),
      ("AS", "American Samoa"),
      ("AD", "Andorra"),
      ("AO", "Angola"),
      ("AI", "Anguilla"),
      ("AQ", "Antarctica"),
      ("AG", "Antigua and Barbuda"),
      ("AR", "Argentina"),
      ("AM", "Armenia"),
      ("AW", "Aruba"),
      ("AC", "Ascension Island"),
      ("AU", "Australia"),
      ("AT", "Austria"),
      ("AZ", "Azerbaijan"),
      ("BS", "Bahamas"),
      ("BH", "Bahrain"),
      ("BD", "Bangladesh"),
      ("BB", "Barbados"),
      ("BY", "Belarus"),
      ("BE", "Belgium"),
      ("BZ", "Belize"),
      ("BJ", "Benin"),
      ("BM", "Bermuda"),
      ("BT", "Bhutan"),
      ("BO", "Bolivia"),
      ("BQ", "Bonaire, Saint Eustatius, and Saba"),
      ("BA", "Bosnia and Herzegovina"),
      ("BW", "Botswana"),
      ("BV", "Bouvet Island"),
      ("BR", "Brazil"),
      ("VG", "British Virgin Islands"),
      ("BN", "Brunei"),
      ("BG", "Bulgaria"),
      ("BF", "Burkina Faso"),
      ("BI", "Burundi"),
      ("KH", "Cambodia"),
      ("CM", "Cameroon"),
      ("CA", "Canada"),
      ("IC", "Canary Islands"),
      ("CT", "Canton and Enderbury Islands"),
      ("CV", "Cape Verde"),
      ("KY", "Cayman Islands"),
      ("CF", "Central African Republic"),
      ("EA", "Ceuta and Melilla"),
      ("TD", "Chad"),
      ("CL", "Chile"),
      ("CN", "China"),
      ("CX", "Christmas Island"),
      ("CP", "Clipperton Island"),
      ("CC", "Cocos (Keeling) Islands"),
      ("CO", "Colombia"),
      ("KM", "Comoros"),
      ("CG", "Congo - Brazzaville"),
      ("CD", "Congo - Kinshasa"),
      ("CK", "Cook Islands"),
      ("CR", "Costa Rica"),
      ("HR", "Croatia"),
      ("CU", "Cuba"),
      ("CW", "Curaçao"),
      ("CY", "Cyprus"),
      ("CZ", "Czech Republic"),
      ("CI", "Côte d’Ivoire"),
      ("DK", "Denmark"),
      ("DG", "Diego Garcia"),
      ("DJ", "Djibouti"),
      ("DM", "Dominica"),
      ("DO", "Dominican Republic"),
      ("NQ", "Dronning Maud Land"),
      ("DD", "East Germany"),
      ("EC", "Ecuador"),
      ("EG", "Egypt"),
      ("SV", "El Salvador"),
      ("GQ", "Equatorial Guinea"),
      ("ER", "Eritrea"),
      ("EE", "Estonia"),
      ("ET", "Ethiopia"),
      ("EU", "European Union"),
      ("FK", "Falkland Islands"),
      ("FO", "Faroe Islands"),
      ("FJ", "Fiji"),
      ("FI", "Finland"),
      ("FR", "France"),
      ("GF", "French Guiana"),
      ("PF", "French Polynesia"),
      ("GA", "Gabon"),
      ("GM", "Gambia"),
      ("GE", "Georgia"),
      ("DE", "Germany"),
      ("GH", "Ghana"),
      ("GI", "Gibraltar"),
      ("GR", "Greece"),
      ("GL", "Greenland"),
      ("GD", "Grenada"),
      ("GP", "Guadeloupe"),
      ("GU", "Guam"),
      ("GT", "Guatemala"),
      ("GG", "Guernsey"),
      ("GN", "Guinea"),
      ("GW", "Guinea-Bissau"),
      ("GY", "Guyana"),
      ("HT", "Haiti"),
      ("HN", "Honduras"),
      ("HK", "Hong Kong SAR China"),
      ("HU", "Hungary"),
      ("IS", "Iceland"),
      ("IN", "India"),
      ("ID", "Indonesia"),
      ("IR", "Iran"),
      ("IQ", "Iraq"),
      ("IE", "Ireland"),
      ("IL", "Israel"),
      ("IT", "Italy"),
      ("JM", "Jamaica"),
      ("JP", "Japan"),
      ("JE", "Jersey"),
      ("JT", "Johnston Island"),
      ("JO", "Jordan"),
      ("KZ", "Kazakhstan"),
      ("KE", "Kenya"),
      ("KI", "Kiribati"),
      ("KW", "Kuwait"),
      ("KG", "Kyrgyzstan"),
      ("LA", "Laos"),
      ("LV", "Latvia"),
      ("LB", "Lebanon"),
      ("LS", "Lesotho"),
      ("LR", "Liberia"),
      ("LY", "Libya"),
      ("LI", "Liechtenstein"),
      ("LT", "Lithuania"),
      ("LU", "Luxembourg"),
      ("MO", "Macau SAR China"),
      ("MK", "Macedonia"),
      ("MG", "Madagascar"),
      ("MW", "Malawi"),
      ("MY", "Malaysia"),
      ("MV", "Maldives"),
      ("ML", "Mali"),
      ("MT", "Malta"),
      ("MH", "Marshall Islands"),
      ("MQ", "Martinique"),
      ("MR", "Mauritania"),
      ("MU", "Mauritius"),
      ("YT", "Mayotte"),
      ("MX", "Mexico"),
      ("FM", "Micronesia"),
      ("MI", "Midway Islands"),
      ("MD", "Moldova"),
      ("MC", "Monaco"),
      ("MN", "Mongolia"),
      ("ME", "Montenegro"),
      ("MS", "Montserrat"),
      ("MA", "Morocco"),
      ("MZ", "Mozambique"),
      ("MM", "Myanmar (Burma)"),
      ("NA", "Namibia"),
      ("NR", "Nauru"),
      ("NP", "Nepal"),
      ("NL", "Netherlands"),
      ("NT", "Neutral Zone"),
      ("NC", "New Caledonia"),
      ("NZ", "New Zealand"),
      ("NI", "Nicaragua"),
      ("NE", "Niger"),
      ("NG", "Nigeria"),
      ("NU", "Niue"),
      ("NF", "Norfolk Island"),
      ("KP", "North Korea"),
      ("VD", "North Vietnam"),
      ("MP", "Northern Mariana Islands"),
      ("NO", "Norway"),
      ("OM", "Oman"),
      ("QO", "Outlying Oceania"),
      ("PK", "Pakistan"),
      ("PW", "Palau"),
      ("PS", "Palestinian Territories"),
      ("PA", "Panama"),
      ("PZ", "Panama Canal Zone"),
      ("PG", "Papua New Guinea"),
      ("PY", "Paraguay"),
      ("YD", "Democratic Republic of Yemen"),
      ("PE", "Peru"),
      ("PH", "Philippines"),
      ("PN", "Pitcairn Islands"),
      ("PL", "Poland"),
      ("PT", "Portugal"),
      ("PR", "Puerto Rico"),
      ("QA", "Qatar"),
      ("RO", "Romania"),
      ("RU", "Russia"),
      ("RW", "Rwanda"),
      ("RE", "Réunion"),
      ("BL", "Saint Barthélemy"),
      ("SH", "Saint Helena"),
      ("KN", "Saint Kitts and Nevis"),
      ("LC", "Saint Lucia"),
      ("MF", "Saint Martin"),
      ("PM", "Saint Pierre and Miquelon"),
      ("VC", "Saint Vincent and the Grenadines"),
      ("WS", "Samoa"),
      ("SM", "San Marino"),
      ("SA", "Saudi Arabia"),
      ("SN", "Senegal"),
      ("RS", "Serbia"),
      ("CS", "Serbia and Montenegro"),
      ("SC", "Seychelles"),
      ("SL", "Sierra Leone"),
      ("SG", "Singapore"),
      ("SX", "Sint Maarten"),
      ("SK", "Slovakia"),
      ("SI", "Slovenia"),
      ("SB", "Solomon Islands"),
      ("SO", "Somalia"),
      ("ZA", "South Africa"),
      ("KR", "South Korea"),
      ("ES", "Spain"),
      ("LK", "Sri Lanka"),
      ("SD", "Sudan"),
      ("SR", "Suriname"),
      ("SZ", "Swaziland"),
      ("SE", "Sweden"),
      ("CH", "Switzerland"),
      ("SY", "Syria"),
      ("ST", "São Tomé and Príncipe"),
      ("TW", "Taiwan"),
      ("TJ", "Tajikistan"),
      ("TZ", "Tanzania"),
      ("TH", "Thailand"),
      ("TL", "Timor-Leste"),
      ("TG", "Togo"),
      ("TK", "Tokelau"),
      ("TO", "Tonga"),
      ("TT", "Trinidad and Tobago"),
      ("TA", "Tristan da Cunha"),
      ("TN", "Tunisia"),
      ("TR", "Turkey"),
      ("TM", "Turkmenistan"),
      ("TC", "Turks and Caicos Islands"),
      ("TV", "Tuvalu"),
      ("UG", "Uganda"),
      ("UA", "Ukraine"),
      ("AE", "United Arab Emirates"),
      ("GB", "United Kingdom"),
      ("US", "United States"),
      ("ZZ", "Unknown Region"),
      ("UY", "Uruguay"),
      ("UZ", "Uzbekistan"),
      ("VU", "Vanuatu"),
      ("VA", "Vatican City"),
      ("VE", "Venezuela"),
      ("VN", "Vietnam"),
      ("WK", "Wake Island"),
      ("WF", "Wallis and Futuna"),
      ("EH", "Western Sahara"),
      ("YE", "Yemen"),
      ("ZM", "Zambia"),
      ("ZW", "Zimbabwe"))

  val gdp = Map("QA" -> 1,
    "LU" -> 2,
    "SG" -> 3,
    "BN" -> 4,
    "NO" -> 5,
    "CH" -> 6,
    "HK" -> 7,
    "SA" -> 8,
    "US" -> 9,
    "NL" -> 10,
    "IE" -> 11,
    "BH" -> 12,
    "AT" -> 13,
    "DE" -> 14,
    "AU" -> 15,
    "SE" -> 16,
    "CA" -> 17,
    "DK" -> 18,
    "IS" -> 19,
    "BE" -> 20,
    "FI" -> 21,
    "GB" -> 22,
    "FR" -> 23,
    "JP" -> 24,
    "IT" -> 25,
    "NZ" -> 26,
    "KR" -> 27,
    "GQ" -> 28,
    "ES" -> 29,
    "IL" -> 30,
    "TT" -> 31,
    "MT" -> 32,
    "SI" -> 33,
    "CZ" -> 34,
    "CY" -> 35,
    "SK" -> 36,
    "PT" -> 37,
    "LT" -> 38,
    "EE" -> 39,
    "GR" -> 40,
    "SC" -> 41,
    "RU" -> 42,
    "BS" -> 43,
    "PL" -> 44,
    "MY" -> 45,
    "HU" -> 46,
    "KZ" -> 47,
    "LV" -> 48,
    "CL" -> 49,
    "AG" -> 50,
    "KN" -> 51,
    "HR" -> 52,
    "UY" -> 53,
    "GA" -> 54,
    "PA" -> 55,
    "TR" -> 56,
    "RO" -> 57,
    "LY" -> 58,
    "BY" -> 59,
    "VE" -> 60,
    "AZ" -> 61,
    "MU" -> 62,
    "MX" -> 63,
    "LB" -> 64,
    "SR" -> 65,
    "BG" -> 66,
    "BW" -> 67,
    "IR" -> 68,
    "PW" -> 69,
    "BR" -> 70,
    "ME" -> 71,
    "IQ" -> 72,
    "TH" -> 73,
    "CR" -> 74,
    "TM" -> 75,
    "DZ" -> 76,
    "CO" -> 77,
    "ZA" -> 78,
    "RS" -> 79,
    "DO" -> 80,
    "CN" -> 81,
    "MK" -> 82,
    "PE" -> 83,
    "MV" -> 84,
    "JO" -> 85,
    "GD" -> 86,
    "TN" -> 87,
    "EC" -> 88,
    "EG" -> 89,
    "LC" -> 90,
    "VC" -> 91,
    "AL" -> 92,
    "DM" -> 93,
    "NA" -> 94,
    "LK" -> 95,
    "ID" -> 96,
    "AE" -> 97,
    "MN" -> 98,
    "BA" -> 99,
    "JM" -> 100,
    "UA" -> 101,
    "PY" -> 102,
    "KW" -> 103,
    "BZ" -> 104,
    "FJ" -> 105,
    "AO" -> 106,
    "SV" -> 107,
    "AM" -> 108,
    "MA" -> 109,
    "BT" -> 110,
    "SZ" -> 111,
    "GE" -> 112,
    "GT" -> 113,
    "GY" -> 114,
    "PH" -> 115,
    "OM" -> 116,
    "CV" -> 117,
    "CG" -> 118,
    "BO" -> 119,
    "NG" -> 120,
    "IN" -> 121,
    "WS" -> 122,
    "AR" -> 123,
    "VN" -> 124,
    "TO" -> 125,
    "UZ" -> 126,
    "LA" -> 127,
    "MD" -> 128,
    "NI" -> 129,
    "PK" -> 130,
    "HN" -> 131,
    "BB" -> 132,
    "GH" -> 133,
    "ZM" -> 134,
    "YE" -> 135,
    "SD" -> 136,
    "TV" -> 137,
    "MH" -> 138,
    "KG" -> 139,
    "FM" -> 140,
    "MR" -> 141,
    "KH" -> 142,
    "BD" -> 143,
    "CI" -> 144,
    "ST" -> 145,
    "DJ" -> 146,
    "KE" -> 147,
    "CM" -> 148,
    "VU" -> 149,
    "LS" -> 150,
    "TJ" -> 151,
    "PG" -> 152,
    "NP" -> 153,
    "TD" -> 154,
    "SN" -> 155,
    "SS" -> 156,
    "AF" -> 157,
    "SB" -> 158,
    "ZW" -> 159,
    "TZ" -> 160,
    "SL" -> 161,
    "BJ" -> 162,
    "HT" -> 163,
    "KI" -> 164,
    "GM" -> 165,
    "TL" -> 166,
    "BF" -> 167,
    "MM" -> 168,
    "UG" -> 169,
    "RW" -> 170,
    "ML" -> 171,
    "KM" -> 172,
    "MG" -> 173,
    "GW" -> 174,
    "TG" -> 175,
    "ET" -> 176,
    "GN" -> 177,
    "ER" -> 178,
    "MZ" -> 179,
    "NE" -> 180,
    "LR" -> 181,
    "BI" -> 182,
    "CD" -> 183,
    "MW" -> 184,
    "CF" -> 185,
    "SM" -> 186,
    "SY" -> 187)
}
