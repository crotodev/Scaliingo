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
 * Represents the metadata of a ticker.
 *
 * @param ticker The ticker symbol.
 * @param name The name of the ticker.
 * @param exchangeCode The code of the exchange.
 * @param startDate The start date of the ticker.
 * @param endDate The end date of the ticker.
 * @param description The description of the ticker.
 */
case class TickerMeta(
  ticker: String,
  name: String,
  exchangeCode: String,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  description: String
)

/**
 * Represents the end of day price data.
 *
 * @param date The date of the data.
 * @param close The closing price.
 * @param high The highest price.
 * @param low The lowest price.
 * @param open The opening price.
 * @param volume The volume of the trades.
 * @param adjClose The adjusted closing price.
 * @param adjHigh The adjusted highest price.
 * @param adjLow The adjusted lowest price.
 * @param adjOpen The adjusted opening price.
 * @param adjVolume The adjusted volume of the trades.
 * @param divCash The dividend cash.
 * @param splitFactor The split factor.
 */
case class EODPriceData(
  date: LocalDateTime,
  close: Double,
  high: Double,
  low: Double,
  open: Double,
  volume: Long,
  adjClose: Double,
  adjHigh: Double,
  adjLow: Double,
  adjOpen: Double,
  adjVolume: Long,
  divCash: Double,
  splitFactor: Double
)

/**
 *  Trait for the Tiingo APIs End-of-Day endpoint.
 */
trait EODEndpoint extends Endpoint {

  private val baseUrl = "https://api.tiingo.com/tiingo/daily"

  def fetchTickerMeta(
    ticker: String
  ): Future[TickerMeta] = {
    val url: Uri = s"$baseUrl/$ticker"
    val urlWithQuery = url.withQuery(
      Uri.Query(
        "token" -> key
      )
    )
    logger.debug(s"Sending request to $url")
    get[TickerMeta](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }

  /**
   * Fetches the metadata of a ticker.
   *
   * @param ticker The ticker symbol.
   * @return The future of the ticker metadata.
   */
  def fetchLatestTickerData(ticker: String): Future[EODPriceData] = {
    val url: Uri = s"$baseUrl/$ticker/prices"
    val urlWithQuery = url.withQuery(
      Uri.Query(
        "token" -> key
      )
    )
    logger.info(s"Sending request to $url")
    get[List[EODPriceData]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    ).map(_.head)
  }

  /**
   * Fetches the historical end of day price data for a ticker.
   *
   * @param ticker    The ticker symbol.
   * @param startDate The optional start date.
   * @param endDate   The optional end date.
   * @return The future of the list of end of day price data.
   */
  def fetchHistoricalTickerData(
    ticker: String,
    startDate: Option[LocalDate] = None,
    endDate: Option[LocalDate] = None
  ): Future[List[EODPriceData]] = {

    val url: Uri =
      s"$baseUrl/$ticker/prices?startDate=${startDate.get}&endDate=${endDate.get}"
    val urlWithQuery = url.withQuery(
      Uri.Query(
        "token" -> key
      )
    )
    logger.debug(s"Sending request to $url")
    get[List[EODPriceData]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }

}

/**
 * The companion object for creating instances of EODEndpoint.
 */
object EODEndpoint {

  /**
   * Creates a new instance of EODEndpoint.
   *
   * @param conf The configuration for Tiingo API.
   * @param sys  The ActorSystem instance.
   * @return The new instance of EODEndpoint.
   */
  def apply(conf: APIConfig)(implicit sys: ActorSystem): EODEndpoint =
    new EODEndpoint {
      override val config: APIConfig = conf
      val system: ActorSystem = sys
      implicit override val materializer: Materializer = Materializer(system)
      implicit override val ec: ExecutionContext =
        system.dispatcher
    }
}
