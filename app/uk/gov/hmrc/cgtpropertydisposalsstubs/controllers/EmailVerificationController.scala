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

import java.time.{Instant, LocalDateTime}

import akka.actor.{Actor, ActorSystem, Cancellable, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import play.api.libs.json.{Format, Json, Reads}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.EmailVerificationController.VerificationManager.{EmailVerificationRequestedAck, GetEmailVerificationRequestResponse}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.EmailVerificationController.{EmailVerificationRequest, VerificationManager}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.matching.Regex

class EmailVerificationController @Inject()(cc: ControllerComponents,
                                            system: ActorSystem
                                           )(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  val statusRegex: Regex = "status(\\d{3})@email\\.com".r

  val verificationManager = system.actorOf(VerificationManager.props())

  implicit val askTimeout: Timeout = Timeout(5.seconds)

  implicit def toFuture[A](a: A): Future[A] = Future.successful(a)

  def verifyEmail(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson.fold[Future[Result]]{
      logger.warn("No JSON found in body")
      BadRequest
    }{ json =>
      json.validate[EmailVerificationRequest].fold(
        { errors =>
          logger.warn(s"Could not read body of email verification request: $errors")
          BadRequest
        },{ request =>
          (verificationManager ? VerificationManager.EmailVerificationRequested(request)).mapTo[EmailVerificationRequestedAck].map{
            _ =>
              request.email match {
                case statusRegex(status) =>
                  logger.info(s"Returning status $status to email verification request: $request")
                  Status(status.toInt)

                case _ =>
                  logger.info(s"Returning status 201 to email verification request: $request")
                  Created
              }
          }
        }
      )

    }
  }


  def getEmailVerificationRequest(email: String) = Action.async { implicit request =>
    (verificationManager ? VerificationManager.GetEmailVerificationRequest(email))
      .mapTo[GetEmailVerificationRequestResponse]
      .map{ response => Ok(Json.toJson(response.request)) }
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

  implicit val format: Format[EmailVerificationRequest] = Json.format[EmailVerificationRequest]


  // Actor which stores verification requests so that the verification requests details can be
  // retrieved back. Verification request are only stored for a finite amount of time and are
  // cleared out periodically
  class VerificationManager extends Actor {
    import context.dispatcher
    import VerificationManager._

    override def preStart(): Unit = {
      super.preStart()
      cleanJob = Some(context.system.scheduler.schedule(0.seconds, cleanFrequency, self, CleanData))
    }

    override def postStop(): Unit = {
      super.postStop()
      cleanJob.foreach(_.cancel())
      cleanJob = None
    }

    val (cleanFrequency, ttlMillis) =  5.minutes -> 30.minutes.toMillis

    var cleanJob: Option[Cancellable] = None

    def now(): Long = Instant.now().toEpochMilli

    def receive: Receive = active(Map.empty)

    def active(requests: Map[String, EmailVerificationRequestWithTimestamp]): Receive = {

      case EmailVerificationRequested(r) =>
        context become active(requests.updated(r.email, EmailVerificationRequestWithTimestamp(r, now())))
        sender() ! EmailVerificationRequestedAck()

      case GetEmailVerificationRequest(e) =>
        sender() ! GetEmailVerificationRequestResponse(requests.get(e).map(_.request))

      case CleanData =>
        context become active(requests.filter(_._2.timestamp > (now() - ttlMillis)))

    }


  }

  object VerificationManager {

    def props(): Props = Props(new VerificationManager)

    case class EmailVerificationRequested(request: EmailVerificationRequest)

    case class EmailVerificationRequestedAck()

    case class GetEmailVerificationRequest(email: String)

    case class GetEmailVerificationRequestResponse(request: Option[EmailVerificationRequest])

    private case class EmailVerificationRequestWithTimestamp(request: EmailVerificationRequest, timestamp: Long)

    private case object CleanData
  }

}