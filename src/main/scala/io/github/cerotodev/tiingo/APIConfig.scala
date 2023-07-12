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

package io.github.cerotodev.tiingo

import akka.http.scaladsl.model.HttpHeader
import scala.concurrent.duration.FiniteDuration
import com.typesafe.config.ConfigFactory

/**
 * Configuration for the Tiingo API.
 *
 * @param apiKey   The Tiingo API key.
 * @param headers  Additional HTTP headers to include in the API requests.
 * @param pause    The duration to pause between consecutive API requests.
 * @param timeout  The maximum duration to wait for an API request to complete.
 */
final case class APIConfig(
                            apiKey: String,
                            headers: List[HttpHeader],
                            pause: FiniteDuration,
                            timeout: FiniteDuration
                          )

/**
 * Factory for creating APIConfig instances.
 */
object APIConfig {

  import cats.data.Validated

  private val config = ConfigFactory.load()

  /**
   * Creates an APIConfig instance.
   *
   * @param _apiKey  Optional API key. If not provided, the key will be retrieved from the configuration file.
   * @return         The created APIConfig instance.
   * @throws         Exception if the API key is not provided or is set to "your-api-key-here".
   */
  def apply(_apiKey: Option[String] = None): APIConfig = {
    val apiKey = _apiKey.getOrElse(config.getString("api.key"))
    val key = checkAPIKey(apiKey) match {
      case Validated.Valid(key)   => key
      case Validated.Invalid(err) => throw new Exception(err.message)
    }

    val headers = List.empty[HttpHeader]
    val pause = FiniteDuration(config.getLong("api.pause"), "seconds")
    val timeout = FiniteDuration(config.getLong("api.timeout"), "seconds")

    APIConfig(key, headers, pause, timeout)
  }

  private def checkAPIKey(apiKey: String): Validated[APIError, String] = {
    if (apiKey.isEmpty) Validated.invalid(APIKeyNotProvided)
    else if (apiKey.equals("your-api-key-here"))
      Validated.invalid(APIKeyNotProvided)
    else Validated.valid(apiKey)
  }
}

/**
 * Represents an error related to the Tiingo API.
 */
sealed trait APIError {
  /**
   * The error message.
   */
  def message: String
}

/**
 * Error indicating that the API key was not provided.
 */
case object APIKeyNotProvided extends APIError {
  val message = "API key not provided"
}
