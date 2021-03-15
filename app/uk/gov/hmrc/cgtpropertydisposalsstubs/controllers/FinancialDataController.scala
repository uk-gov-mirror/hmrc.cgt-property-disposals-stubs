/*
 * Copyright 2021 HM Revenue & Customs
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

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.FinancialDataResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.{Logging, TimeUtils}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class FinancialDataController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def getFinancialData(
    idType: String,
    idNumber: String,
    regimeType: String,
    dateFrom: String,
    dateTo: String
  ): Action[AnyContent] = Action { _ =>
    logger.info(s"""
        Get financial data called with parameters: idType = $idType, idNumber = $idNumber,
        regimeType = $regimeType, dateFrom = $dateFrom, dateTo = $dateTo
      """)

    val dates = TimeUtils.withFromAndToDate(dateFrom, dateTo)

    Ok(
      Json.toJson(
        FinancialDataResponse(
          ReturnAndPaymentProfiles
            .getProfile(idNumber)
            .map(_.returns.filter(p => !p.returnSummary.submissionDate.isBefore(dates._1) && !p.returnSummary.submissionDate.isAfter(dates._2)).flatMap(_.financialData))
            .getOrElse(List.empty)
        )
      )
    )
  }

}
