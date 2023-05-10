/*
 * Copyright 2023 fd33v
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

import endpoints._
import utils.ClientUtils

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpHeader
import akka.stream.Materializer
import io.github.crotodev.utils.HttpUtils._

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.ExecutionContext

/**
 * The case class representing the configuration for the Tiingo API.
 *
 * @param apiKey the API key for the Tiingo API.
 * @param headers the HTTP headers to be sent with the requests.
 * @param pause the duration to pause between requests.
 * @param timeout the duration to wait for a response before timing out.
 */
case class TiingoConfig(
    apiKey: Option[String] = None,
    headers: List[HttpHeader] = Nil,
    pause: FiniteDuration = 1.seconds,
    timeout: FiniteDuration = 15.seconds
)

/**
 * The case class representing the Tiingo client used for fetching various data from the Tiingo API.
 *
 * @param config the configuration for the Tiingo API.
 */
case class TiingoAPI(config: TiingoConfig)(implicit val system: ActorSystem)
    extends EODEndpoint
    with NewsEndpoint
    with CryptoEndpoint
    with FXEndpoint
    with IEXEndpoint {

  /**
   * The implicit materializer used for materializing streams.
   */
  override implicit val materializer: Materializer = Materializer(system)

  /**
   * The implicit execution context used for executing futures.
   */
  override implicit val ec: ExecutionContext = system.dispatcher
}

/**
 * The companion object for the TiingoAPI.
 */
object TiingoAPI {

  /**
   * Creates a new instance of the TiingoAPI.
   *
   * @param apiKey  the API key for the Tiingo API.
   * @param headers the HTTP headers to be sent with the requests.
   * @return a new instance of the TiingoAPI.
   */
  def apply(apiKey: Option[String] = None, headers: Map[String, String] = Map.empty)(
      implicit system: ActorSystem
  ): TiingoAPI = {
    val heads = parseHeaders(headers)
    val key   = ClientUtils.sanitizeApiKey(apiKey)
    TiingoAPI(TiingoConfig(Some(key), heads))
  }
}
