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

import com.google.inject.Inject
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.EmailVerificationController.EmailVerificationRequest
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.util.matching.Regex

class EmailVerificationController @Inject()(cc: ControllerComponents) extends BackendController(cc) with Logging {

  val statusRegex: Regex = "status(\\d{3})@email\\.com".r

  def verifyEmail(): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.fold{
      logger.warn("No JSON found in body")
      BadRequest
    }{ json =>
      json.validate[EmailVerificationRequest].fold(
        { errors =>
          logger.warn(s"Could not read body of email verification request: $errors")
          BadRequest
        },{ request =>
          request.email match {
            case statusRegex(status) =>
              logger.info(s"Returning status $status to email verification request: $request")
              Status(status.toInt)

            case _ =>
              logger.info(s"Returning status 201 to email verification request: $request")
              Created
          }
        }
      )

    }
  }

}


object EmailVerificationController {

  final case class EmailVerificationRequest(
                                             email: String,
                                             templateId: String,
                                             linkExpiryDuration: String,
                                             continueUrl: String,
                                             templateParameters: Map[String, String]
                                           )

  implicit val reads: Reads[EmailVerificationRequest] = Json.reads[EmailVerificationRequest]

}