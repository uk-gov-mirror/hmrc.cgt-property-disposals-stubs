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

import cats.data.EitherT
import cats.instances.option._
import com.google.inject.{Inject, Singleton}
import org.scalacheck.Gen
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.SubscriptionController.SubscriptionResponse
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.smartstub._

import scala.concurrent.ExecutionContext

@Singleton
class SubscriptionController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def subscribe(): Action[AnyContent] = Action { implicit request =>
    request.body.asJson.fold[Result] {
      logger.warn("Could not find JSON in body for subscribe request")
      BadRequest
    } { json =>
      (json \ "sapNumber")
        .validate[String]
        .fold[Result](
          { e =>
            logger.warn(s"Could not find sap number in json for subscribe request: $e")
            BadRequest
          }, { sapNumber =>
            val result =
              EitherT(SubscriptionProfiles.getProfile(Right(sapNumber)).flatMap(_.subscriptionResponse))
                .map(subscriptionResponse => Ok(Json.toJson(subscriptionResponse)))
                .merge
                .getOrElse(Ok(Json.toJson(SubscriptionResponse(randomCgtReferenceId(sapNumber)))))

            logger.info(s"Returning result $result to subscribe request for sap number $sapNumber")
            result
          }
        )
    }
  }

  val cgtReferenceIdGen = for {
    letter <- Gen.alphaUpperChar
    digits <- Gen.listOfN(9, Gen.numChar)
  } yield s"X${letter}CGTP${digits.mkString("")}"

  // sap numbers should be a series of digits so toLong should be ok
  implicit val sapNumberToLong: ToLong[String] = new ToLong[String] {
    override def asLong(i: String): Long = i.toLong
  }

  def randomCgtReferenceId(sapNumber: String): String =
    cgtReferenceIdGen.seeded(sapNumber).get

}

object SubscriptionController {

  case class SubscriptionResponse(cgtReferenceNumber: String)

  object SubscriptionResponse {

    implicit val write: Writes[SubscriptionResponse] = Json.writes[SubscriptionResponse]

  }
}
