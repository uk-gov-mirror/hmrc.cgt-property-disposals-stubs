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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{FinancialDataResponse, FinancialTransaction}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class FinancialDataController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends BackendController(cc)
    with Logging {

  def getFinancialData(idType: String, idNumber: String, regimeType: String,dateFrom:String,dateTo:String): Action[AnyContent] = Action { _ =>
    Ok(
      Json.toJson(
        prepareFinancialDataResponse(idType, idNumber, regimeType)
      )
    )
  }

  private def prepareFinancialDataResponse(idType: String, idNumber: String, regimeType: String): FinancialDataResponse = {
    val financialTransactions = List(
      FinancialTransaction(outstandingAmount = BigDecimal(10000d)),
      FinancialTransaction(outstandingAmount = BigDecimal(20000d)),
      FinancialTransaction(outstandingAmount = BigDecimal(30000d))
    )
    FinancialDataResponse(
      idType = idType,
      idNumber = idNumber,
      regimeType = regimeType,
      processingDate = LocalDateTime.now(),
      financialTransactions = financialTransactions
    )
  }
}
