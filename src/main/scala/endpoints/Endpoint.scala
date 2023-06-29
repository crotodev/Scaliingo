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

import models.APIConfig

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

/**
 * The base trait for all endpoints.
 */
trait Endpoint {

  val config: APIConfig

  protected val key: String = config.apiKey.get

  implicit val system: ActorSystem

  implicit val materializer: Materializer

  implicit val ec: ExecutionContext

  protected val logger = LoggerFactory.getLogger(getClass)

  /**
   * Shuts down all connection pools and terminates the actor system.
   */
  def shutdown(): Unit =
    Http().shutdownAllConnectionPools().onComplete(_ => system.terminate())

}
