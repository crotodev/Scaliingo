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

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

/**
 * Represents the crypto price data.
 *
 * @param date The date of the crypto price.
 * @param open The opening price.
 * @param high The highest price.
 * @param low The lowest price.
 * @param close The closing price.
 * @param tradesDone The number of trades done.
 * @param volume The volume of the trades.
 * @param volumeNotional The notional volume of the trades.
 */
case class CryptoPriceData(
  date: LocalDateTime,
  open: Double,
  high: Double,
  low: Double,
  close: Double,
  tradesDone: Double,
  volume: Double,
  volumeNotional: Double
)

/**
 * Represents the crypto price.
 *
 * @param ticker The ticker symbol.
 * @param baseCurrency The base currency.
 * @param quoteCurrency The quote currency.
 * @param priceData The list of crypto price data.
 */
case class CryptoPrice(
  ticker: String,
  baseCurrency: String,
  quoteCurrency: String,
  priceData: List[CryptoPriceData]
)

/**
 * Represents the metadata of a crypto currency.
 *
 * @param ticker The ticker symbol.
 * @param name The name of the crypto currency.
 * @param baseCurrency The base currency.
 * @param quoteCurrency The quote currency.
 * @param description The description of the crypto currency.
 */
case class CryptoMeta(
  ticker: String,
  name: Option[String],
  baseCurrency: String,
  quoteCurrency: String,
  description: Option[String]
)

/**
 * Trait for the Tiingo APIs Crypto endpoint.
 */
trait CryptoEndpoint extends Endpoint {

  private val baseUrl = "https://api.tiingo.com/tiingo/crypto"

  /**
   * Fetches the latest crypto currency data.
   *
   * @param tickers The optional list of tickers to fetch data for.
   * @return The future of the list of crypto prices.
   */
  def fetchLatestCryptoData(
    tickers: Option[List[String]] = None
  ): Future[List[CryptoPrice]] = {

    val url: Uri = s"$baseUrl/prices"

    val urlWithQuery = tickers match {
      case Some(t) =>
        url.withQuery(Uri.Query("tickers" -> t.mkString(","), "token" -> key))
      case None => url.withQuery(Uri.Query("token" -> key))
    }
    logger.debug(s"Sending request to $url")
    get[List[CryptoPrice]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }

  /**
   * Fetches the metadata of crypto currencies.
   *
   * @param tickers The optional list of tickers to fetch metadata for.
   * @return The future of the list of crypto metadata.
   */
  def fetchCryptoMeta(
    tickers: Option[List[String]] = None
  ): Future[List[CryptoMeta]] = {
    val url: Uri = baseUrl

    val urlWithQuery = tickers match {
      case Some(t) =>
        url.withQuery(Uri.Query("tickers" -> t.mkString(","), "token" -> key))
      case None =>
        url.withQuery(Uri.Query("token" -> key))
    }
    logger.debug(s"Sending request to $url")
    get[List[CryptoMeta]](urlWithQuery, config.headers, config.pause, config.timeout)
  }

}

/**
 * The companion object for creating instances of CryptoEndpoint.
 */
object CryptoEndpoint {

  /**
   * Creates a new instance of CryptoEndpoint.
   *
   * @param conf The configuration for Tiingo API.
   * @param sys The ActorSystem instance.
   * @return The new instance of CryptoEndpoint.
   */
  def apply(conf: APIConfig)(implicit sys: ActorSystem): CryptoEndpoint =
    new CryptoEndpoint {
      override val config: APIConfig = conf
      val system: ActorSystem = sys
      implicit override val materializer: Materializer = Materializer(system)
      implicit override val ec: ExecutionContext =
        system.dispatcher
    }
}
