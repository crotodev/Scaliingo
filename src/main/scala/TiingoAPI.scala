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
import models.APIConfig

import akka.actor.ActorSystem
import akka.stream.Materializer

import scala.concurrent.ExecutionContext

/**
 * The case class representing the Tiingo client used for fetching various data from the Tiingo API.
 *
 * @param config the configuration for the Tiingo API.
 */
case class TiingoAPI(config: APIConfig)(implicit val system: ActorSystem)
    extends EODEndpoint
    with NewsEndpoint
    with CryptoEndpoint
    with FXEndpoint
    with IEXEndpoint {

  /**
   * The implicit materializer used for materializing streams.
   */
  implicit override val materializer: Materializer = Materializer(system)

  /**
   * The implicit execution context used for executing futures.
   */
  implicit override val ec: ExecutionContext = system.dispatcher
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
  def apply(apiKey: Option[String] = None, headers: Map[String, String] = Map.empty)(implicit
    system: ActorSystem
  ): TiingoAPI = {
    TiingoAPI(APIConfig(apiKey, headers))
  }
}
