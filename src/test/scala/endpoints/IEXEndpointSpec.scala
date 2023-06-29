/*
 * Copyright 2023 crotodev
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

package io.github.crotodev.tiingo
package endpoints

import akka.actor.ActorSystem
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class IEXEndpointSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  import models.APIConfig

  implicit val system: ActorSystem = ActorSystem("EODClientSpec")
  implicit val ec: ExecutionContext = system.dispatcher

  val config = APIConfig(sys.env.get("TIINGO_API_KEY"), timeout = 30.seconds)
  val client = IEXEndpoint(config)

  val ticker = "GOOGL"

//  override def afterAll(): Unit =
//    client.shutdown()

  behavior.of("IEXClientSpec")

  it should "fetchIEXLastPriceData" in {
    client.fetchIEXLastPriceData(Some(ticker)).onComplete(println)
  }
}
