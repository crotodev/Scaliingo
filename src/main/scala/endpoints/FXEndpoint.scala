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

import JsonProtocol._
import models.APIConfig

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import akka.stream.Materializer
import io.github.crotodev.utils.HttpUtils._

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.{ExecutionContext, Future}

/**
 * The case class representing the data structure for the top forex data.
 *
 * @param ticker the forex ticker symbol.
 * @param timestamp the timestamp of the data.
 * @param midPrice the mid price of the forex.
 * @param bidSize the size of the bid.
 * @param bidPrice the price of the bid.
 * @param askSize the size of the ask.
 * @param askPrice the price of the ask.
 */
case class FXTopData(
  ticker: String,
  timestamp: LocalDateTime,
  midPrice: Double,
  bidSize: Double,
  bidPrice: Double,
  askSize: Double,
  askPrice: Double
)

/**
 * The case class representing the data structure for the intraday forex data.
 *
 * @param date the date of the data.
 * @param ticker the forex ticker symbol.
 * @param open the opening price of the forex.
 * @param high the highest price of the forex.
 * @param low the lowest price of the forex.
 * @param close the closing price of the forex.
 */
case class FXIntradayData(date: LocalDateTime, ticker: String, open: Double, high: Double, low: Double, close: Double)

/**
 * Trait for the Tiingo APIs Forex endpoint.
 */
trait FXEndpoint extends Endpoint {

  private val baseUrl = "https://api.tiingo.com/tiingo/fx"

  /**
   * Fetches top forex data from the Tiingo API.
   *
   * @param tickers the list of forex ticker symbols to fetch data for.
   * @return a future of the top forex data.
   */
  def fetchFXTopData(tickers: List[String]): Future[FXTopData] = {

    val url: Uri = s"$baseUrl/top"
    val urlWithQuery = url.withQuery(
      Uri.Query(
        "tickers" -> tickers.mkString(","),
        "token" -> key
      )
    )
    logger.debug(s"Sending request to $url")
    get[List[FXTopData]](urlWithQuery, config.headers, config.pause, config.timeout)
      .map(_.head)
  }

  /**
   * Fetches intraday forex data from the Tiingo API.
   *
   * @param ticker    the forex ticker symbol to fetch data for.
   * @param startDate the start date for fetching the data.
   * @param frequency the frequency of the data.
   * @return a future of the intraday forex data.
   */
  def fetchFXIntradayData(
    ticker: String,
    startDate: Option[LocalDate] = None,
    frequency: Option[String] = None
  ): Future[FXIntradayData] = {

    val url: Uri =
      s"$baseUrl/$ticker/prices"

    val params: Map[String, String] = startDate match {
      case Some(_) =>
        Map("startDate" -> startDate.get.toString, "resampleFreq" -> frequency.get, "token" -> key)
      case None => Map("resampleFreq" -> frequency.get, "token" -> key)
    }
    val urlWithQuery = url.withQuery(
      Uri.Query(
        params
      )
    )
    logger.debug(s"Sending request to $url")
    get[List[FXIntradayData]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    ).map(_.head)
  }
}

/**
 * The companion object for creating instances of FxEndpoint.
 */
object FXEndpoint {

  /**
   * Creates a new instance of the FxEndpoint.
   *
   * @param conf the configuration for the Tiingo API.
   * @param sys  the actor system for the client.
   * @return a new instance of the FxEndpoint.
   */
  def apply(conf: APIConfig)(implicit sys: ActorSystem): FXEndpoint =
    new FXEndpoint {
      override val config: APIConfig = conf
      val system: ActorSystem = sys
      implicit override val materializer: Materializer = Materializer(system)
      implicit override val ec: ExecutionContext =
        system.dispatcher
    }
}
