/*
 *
 *  * ******************************************************************************
 *  *  * *
 *  *  *  *
 *  *  *  * Copyright (c) 2023 Christian Rotondo
 *  *  *  *
 *  *  *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  *  *  * of this software and associated documentation files (the "Software"), to deal
 *  *  *  * in the Software without restriction, including without limitation the rights
 *  *  *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  *  *  * copies of the Software, and to permit persons to whom the Software is
 *  *  *  * furnished to do so, subject to the following conditions:
 *  *  *  *
 *  *  *  * The above copyright notice and this permission notice shall be included in all
 *  *  *  * copies or substantial portions of the Software.
 *  *  *  *
 *  *  *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  *  *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  *  *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  *  *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  *  *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  *  *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  *  *  * SOFTWARE.
 *  *  *
 *  *  *
 *  *  *****************************************************************************
 *
 */

package io.github.crotodev.tiingo
package endpoints

import akka.actor.ActorSystem
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class EODEndpointSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  import models.APIConfig

  implicit val system: ActorSystem = ActorSystem("EODClientSpec")
  implicit val ec: ExecutionContext = system.dispatcher

  val config = APIConfig(sys.env.get("TIINGO_API_KEY"), timeout = 30.seconds)
  val client = EODEndpoint(config)

  val ticker = "GOOGL"

  behavior.of("EODClientSpec")

  it should "fetchTickerMeta" in {
    client.fetchTickerMeta(ticker).onComplete(println)
  }

}
