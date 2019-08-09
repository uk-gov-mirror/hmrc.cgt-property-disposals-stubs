/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cgtpropertydisposalsstubs.controllers

import cats.data.Validated._
import cats.data.{NonEmptyList, ValidatedNel}
import cats.syntax.apply._

import com.google.inject.Inject
import play.api.libs.json.{JsArray, JsObject, Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.AddressLookupController._
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

class AddressLookupController @Inject()(cc: ControllerComponents) extends BackendController(cc) with Logging {

  val statusRegex = """E(\d\d \d)RR""".r

  def lookupAddresses(postcode: String): Action[AnyContent] = Action { implicit request =>
    val userAgent = request.headers.get("User-Agent").getOrElse("-")
    logger.info(s"Request for address lookup made for postcode $postcode with user agent $userAgent")

    validatePostcode(postcode).fold({
      errors =>
        logger.warn(s"Returning bad request: ${errors.toList.mkString("; ")}")
        BadRequest
    },{ p =>
      // put a space before the last three characters
      val formattedPostcode = {
        val (firstPart, secondPart) = p.splitAt(p.length - 3)
        s"$firstPart $secondPart"
      }

      formattedPostcode match {
        case statusRegex(statusCodeString) =>
          val statusCode = statusCodeString.replaceAllLiterally(" ", "").toInt
          logger.info("Returning with status")
          Status(statusCode)

        case _ =>
          // if all the digits are zero in the postcode, return an empty array
          val response = if (p.filter(_.isDigit).exists(_ != '0')) {
            JsArray(randomAddresses(formattedPostcode).map(a => JsObject(Map("address" -> Json.toJson(a)))))
          } else {
            JsArray()
          }

          Ok(response)
      }
    })
  }

  def randomAddresses(postcode: String): List[Address] =
    (1 to 10)
      .map(i => Address(List(s"$i the Street"), "Townsville", Some("Countyshire"), postcode, Country("GB")))
      .toList



  def validatePostcode(postcode: String): ValidatedNel[String,String] = {
    def validatedFromBoolean[A](a: A)(predicate: A => Boolean, ifInvalid: => String): ValidationResult[A] =
      if(predicate(a)) Valid(a) else Invalid(NonEmptyList.one(ifInvalid))

    val cleanedPostcode = postcode.replaceAllLiterally(" ", "")
    val lengthCheck: ValidationResult[String] = validatedFromBoolean(cleanedPostcode)(_.length < 3, "postcode should have more than three characters in it")
    val lowerCaseLetterCheck: ValidationResult[String] = validatedFromBoolean(cleanedPostcode)(_.exists(_.isLower), "postcode should only have upper case letters")

    (lengthCheck, lowerCaseLetterCheck).mapN{ case _ => cleanedPostcode}
  }

}

object AddressLookupController {

  type ValidationResult[A] = ValidatedNel[String, A]

  final case class Country(code: String)

  final case class Address(
                               lines: List[String],
                               town: String,
                               county: Option[String],
                               postcode: String,
                               country: Country
                             )

  final case class AddressLookupResponse(addresses: List[Address])

  implicit val countryWrites: Writes[Country] = Json.writes[Country]

  implicit val addressWrites: Writes[Address] = Json.writes[Address]

  implicit val addressLookupResponseWrites: Writes[AddressLookupResponse] = Json.writes[AddressLookupResponse]

}

