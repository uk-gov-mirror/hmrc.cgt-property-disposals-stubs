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

import java.time.{LocalDate, LocalDateTime}

import com.google.inject.{Inject, Singleton}
import org.scalacheck.Gen
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{DesReturnResponse, PPDReturnResponseDetails}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.GenUtils.sample
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

@Singleton
class ReturnController @Inject()(cc: ControllerComponents)
  extends BackendController(cc)
    with Logging {

  def submitReturn(cgtReferenceNumber: String): Action[JsValue] = Action(parse.json) { request =>
    val submittedReturn: JsResult[(BigDecimal, String)] = for {
      a <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "totalLiability").validate[BigDecimal]
      d <- (request.body \ "ppdReturnDetails" \ "returnDetails" \ "completionDate").validate[String]
    } yield (a, d)

    submittedReturn.fold({
      e =>
        logger.warn(s"Could not parse request body: $e")
        BadRequest
    }, { case (amountDue, completionDate) =>
      Ok(
        Json.toJson(prepareDesReturnRespose(cgtReferenceNumber, amountDue, completionDate))
      )
    })
  }

  private def prepareDesReturnRespose(
    cgtReferenceNumber: String,
    amountDue: BigDecimal,
    completionDate: String
  ): DesReturnResponse = {
    val ppdReturnResponseDetails = PPDReturnResponseDetails(
      chargeType = "Late Penalty",
      chargeReference = s"XCRG${nRandomDigits(10)}",
      amount = amountDue.toDouble,
      dueDate = dueDate(completionDate),
      formBundleNumber = nRandomDigits(12),
      cgtReferenceNumber = cgtReferenceNumber
    )
    DesReturnResponse(
      processingDate = LocalDateTime.now(),
      ppdReturnResponseDetails = ppdReturnResponseDetails
    )
  }

  private def dueDate(completionDate: String): LocalDate =
    LocalDate.parse(completionDate).plusDays(30)

  private def nRandomDigits(n: Int): String =
    List.fill(n)(sample(Gen.numChar)).mkString("")

}
