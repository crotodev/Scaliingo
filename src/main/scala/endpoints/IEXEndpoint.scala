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

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

/**
 * Case class representing the last price data from IEX.
 *
 * @param ticker the ticker symbol.
 * @param timestamp the timestamp of the data.
 * @param quoteTimestamp the timestamp of the quote.
 * @param lastSaleTimestamp the timestamp of the last sale.
 * @param last the last sale price.
 * @param lastSize the size of the last sale.
 * @param tngoLast the last price from Tiingo.
 * @param prevClose the previous closing price.
 * @param open the opening price.
 * @param high the highest price.
 * @param low the lowest price.
 * @param mid the midpoint price.
 * @param volume the volume of trades.
 * @param bidSize the size of the bid.
 * @param bidPrice the bid price.
 * @param askSize the size of the ask.
 * @param askPrice the ask price.
 */
case class IEXLastPriceData(
  ticker: String,
  timestamp: LocalDateTime,
  quoteTimestamp: LocalDateTime,
  lastSaleTimestamp: LocalDateTime,
  last: Double,
  lastSize: Option[Int],
  tngoLast: Double,
  prevClose: Double,
  open: Double,
  high: Double,
  low: Double,
  mid: Double,
  volume: Int,
  bidSize: Option[Int],
  bidPrice: Double,
  askSize: Option[Int],
  askPrice: Double
)

/**
 * Case class representing the historical price data from IEX.
 *
 * @param date the date of the data.
 * @param open the opening price.
 * @param high the highest price.
 * @param low the lowest price.
 * @param close the closing price.
 * @param volume the volume of trades.
 */
case class IEXHistoricalPriceData(
  date: LocalDateTime,
  open: Double,
  high: Double,
  low: Double,
  close: Double,
  volume: Int
)

/**
 * Trait for the Tiingo APIs IEX endpoint.
 */
trait IEXEndpoint extends Endpoint {

  private val baseUrl = "https://api.tiingo.com/iex"

  /**
   * Retrieves the last price data for the given ticker.
   *
   * @param ticker the ticker symbol. If not provided, data for all tickers will be retrieved.
   * @return a future list of IEXLastPriceData.
   */
  def fetchIEXLastPriceData(
    ticker: Option[String] = None
  ): Future[List[IEXLastPriceData]] = {
    val url: Uri = ticker match {
      case Some(t) => s"$baseUrl/$t"
      case None    => baseUrl
    }
    val urlWithQuery = url.withQuery(
      Uri.Query(
        "token" -> key
      )
    )
    logger.debug(s"Sending request to $url")
    get[List[IEXLastPriceData]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }

  /**
   * Retrieves the historical price data for the given ticker.
   *
   * @param ticker    the ticker symbol.
   * @param startDate the start date for the historical data. If not provided, data from the beginning will be retrieved.
   * @param endDate   the end date for the historical data. If not provided, data till the present will be retrieved.
   * @param frequency the frequency of the historical data. If not provided, the default frequency will be used.
   * @return a future list of IEXHistoricalPriceData.
   */
  def fetchIEXHistoricalPriceData(
    ticker: String,
    startDate: Option[String] = None,
    endDate: Option[String] = None,
    frequency: Option[String] = None
  ): Future[List[IEXHistoricalPriceData]] = {
    val url: Uri = s"$baseUrl/$ticker/prices"
    val urlWithQuery = url.withQuery(
      Uri.Query(
        "token" -> key,
        "startDate" -> startDate.getOrElse(""),
        "endDate" -> endDate.getOrElse(""),
        "resampleFreq" -> frequency.getOrElse("")
      )
    )
    logger.debug(s"Sending request to $url")
    get[List[IEXHistoricalPriceData]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }
}

/**
 * The companion object for creating instances of IEXEndpoint.
 */
object IEXEndpoint {

  /**
   * Creates a new instance of IEXEndpoint.
   *
   * @param conf configuration for the Tiingo API.
   * @param sys  the implicit actor system.
   * @return a new IEXEndpoint instance.
   */
  def apply(conf: APIConfig)(implicit sys: ActorSystem): IEXEndpoint =
    new IEXEndpoint {
      override val config: APIConfig = conf
      val system: ActorSystem = sys
      implicit override val materializer: Materializer = Materializer(system)
      implicit override val ec: ExecutionContext =
        system.dispatcher
    }
}
