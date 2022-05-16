/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers

import controllers.actions.AuthActionFilter
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class SubscriptionController @Inject()(cc: ControllerComponents, authFilter: AuthActionFilter) extends BackendController(cc) with StubResource {

  def readSubscription(): Action[JsValue] = Action(parse.json) {
    implicit request =>
      val json     = request.body
      val idNumber = (json \ "displaySubscriptionForCBCRequest" \ "requestDetail" \ "IDNumber").as[String]

      idNumber match {
        case "XACBC0000123777" =>
          Ok(
            findResource(s"/resources/v1/safe/cbc/displayExistingUserSubscription.json")
              .map(
                r => replaceSubscriptionId(r, "XACBC0000123777")
              )
              .get
          )
        case "XACBC0000123778" =>
          Ok(
            findResource(s"/resources/v1/safe/cbc/displaySubscription.json")
              .map(
                r => replaceSubscriptionId(r, "XACBC0000123778")
              )
              .get
          )
        case _ => ServiceUnavailable(findResource(s"/resources/error/ServiceUnavailable.json").get)
      }
  }

  private def replaceSubscriptionId(response: String, subscriptionId: String): String =
    response.replace("[subscriptionId]", subscriptionId)


  def updateSubscription(): Action[JsValue] = (Action(parse.json) andThen authFilter).async { implicit request =>
    val json     = request.body
    val idNumber = (json \ "updateSubscriptionForCBCRequest" \ "requestDetail" \ "IDNumber").as[String]

    idNumber match {
      case "XACBC0000123777" => jsonAsyncResourceResponse(s"/resources/subscription/updateSubscriptionResponseXACBC0000123777.json")
      case "XACBC0000123778" => jsonAsyncResourceResponse(s"/resources/subscription/updateSubscriptionResponseXACBC0000123778.json")
      case _                 => Future.successful(NotFound)
    }
  }
}
