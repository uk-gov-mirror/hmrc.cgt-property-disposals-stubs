/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import cats.syntax.eq._
import cats.instances.bigDecimal._
import com.google.inject.{Inject, Singleton}
import org.scalacheck.Gen
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesListReturnsResponse.{Charge, ReturnSummary}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.DesReturn._
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesAddressDetails, DesListReturnsResponse, DesReturn, DesReturnResponse, DesReturnType, PPDReturnResponseDetails}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.GenUtils.sample
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.util.Try

@Singleton
class ReturnController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def submitReturn(cgtReferenceNumber: String): Action[JsValue] = Action(parse.json) { request =>
    val submittedReturn: JsResult[(BigDecimal, LocalDate)] = for {
      a <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "totalYTDLiability").validate[BigDecimal]
      d <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "completionDate").validate[LocalDate]
    } yield (a, d)

    submittedReturn.fold(
      { e =>
        logger.warn(s"Could not parse request body: $e")
        BadRequest
      }, {
        case (ytdLiability, completionDate) =>
          Ok(
            Json.toJson(prepareDesSubmitReturnResponse(cgtReferenceNumber, ytdLiability, completionDate))
          )
      }
    )
  }

  def listReturns(cgtReference: String, fromDate: String, toDate: String): Action[AnyContent] = Action {
    implicit request =>
      withFromAndToDate(fromDate, toDate) {
        case (_, _) =>
          Ok(
            Json.toJson(
              DesListReturnsResponse(LocalDateTime.now(), ReturnAndPaymentProfiles.profiles.map(_.returnSummary))
            )
          )
      }
  }

  def displayReturn(cgtReference: String, submissionId: String): Action[AnyContent] = Action { implicit request =>
    Ok(Json.toJson(dummyReturn))
  }

  val dummyReturn = DesReturn(
    DesReturnType(
      "self digital",
      "New",
      None
    ),
    ReturnDetails(
      "individual",
      LocalDate.of(2020, 1, 10),
      false,
      1,
      BigDecimal(100),
      BigDecimal(100),
      BigDecimal(100),
      false,
      false,
      false,
      true,
      Some("HK"),
      None,
      None,
      Some(
        List(
          ValueAtTaxBandDetails(BigDecimal(100), BigDecimal(35)),
          ValueAtTaxBandDetails(BigDecimal(50), BigDecimal(70))
        )
      ),
      None,
      None
    ),
    None,
    List(
      DisposalDetails(
        LocalDate.of(2020, 1, 5),
        DesAddressDetails("You know that place", None, None, None, "ZZ0 0ZZ", "GB"),
        "residential",
        "bought",
        false,
        BigDecimal(50),
        false,
        BigDecimal(150),
        true,
        Some(73.32),
        Some(LocalDate.of(2000, 1, 1)),
        None,
        Some("sold"),
        Some(1),
        Some(3),
        Some(3),
        Some(54),
        Some(0)
      )
    ),
    LossSummaryDetails(true, true, Some(BigDecimal(23)), Some(BigDecimal(6))),
    IncomeAllowanceDetails(BigDecimal(2.34), Some(BigDecimal(379)), Some(BigDecimal(5)), None),
    ReliefDetails(
      true,
      Some(BigDecimal(6.73)),
      Some(1.23),
      None,
      Some("A Totally Real Relief"),
      Some(BigDecimal(1023.43))
    )
  )

  private def prepareDesSubmitReturnResponse(
    cgtReferenceNumber: String,
    ytdLiability: BigDecimal,
    completionDate: LocalDate
  ): DesReturnResponse = {
    val ppdReturnResponseDetails = if (ytdLiability =!= BigDecimal(0)) {
      PPDReturnResponseDetails(
        None,
        Some(randomChargeReference()),
        Some(ytdLiability.toDouble),
        Some(dueDate(completionDate)),
        Some(randomFormBundleId()),
        Some(cgtReferenceNumber)
      )
    } else {
      PPDReturnResponseDetails(
        None,
        None,
        Some(BigDecimal(0)),
        None,
        Some(randomFormBundleId()),
        Some(cgtReferenceNumber)
      )
    }
    DesReturnResponse(
      processingDate           = LocalDateTime.now(),
      ppdReturnResponseDetails = ppdReturnResponseDetails
    )
  }

  private def dueDate(completionDate: LocalDate): LocalDate =
    completionDate.plusDays(30)

  private def randomFormBundleId(): String =
    nRandomDigits(12)

  private def randomChargeReference(): String =
    s"XCRG${nRandomDigits(10)}"

  private def nRandomDigits(n: Int): String =
    List.fill(n)(sample(Gen.numChar)).mkString("")

  private def withFromAndToDate(fromDate: String, toDate: String)(f: (LocalDate, LocalDate) => Result): Result = {
    def parseDate(string: String): Option[LocalDate] =
      Try(LocalDate.parse(string, DateTimeFormatter.ISO_DATE)).toOption

    parseDate(fromDate) -> parseDate(toDate) match {
      case (None, None) =>
        logger.warn(s"Could not parse fromDate ('$fromDate') or toDate ('$toDate') ")
        BadRequest

      case (None, Some(_)) =>
        logger.warn(s"Could not parse fromDate ('$fromDate')")
        BadRequest

      case (Some(_), None) =>
        logger.warn(s"Could not parse toDate ('$toDate')")
        BadRequest

      case (Some(from), Some(to)) =>
        f(from, to)
    }
  }
}
