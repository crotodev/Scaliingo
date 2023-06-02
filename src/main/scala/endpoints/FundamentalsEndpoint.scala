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
 * Case class representing the definition of a financial metric.
 *
 * @param dataCode The code of the data.
 * @param name The name of the data.
 * @param description The description of the data.
 * @param statementType The type of the statement the data belongs to.
 * @param units The units of the data.
 */
case class Definition(
  dataCode: String,
  name: String,
  description: String,
  statementType: String,
  units: String
)

/**
 * Case class representing a data field in a financial statement.
 *
 * @param dataCode The code of the data.
 * @param value The value of the data.
 */
case class StatementDataField(dataCode: String, value: Double)

/**
 * Case class representing data from a financial statement.
 *
 * @param date The date of the data.
 * @param quarter The quarter of the data.
 * @param year The year of the data.
 * @param statementData The statement data.
 */
case class StatementData(
  date: LocalDateTime,
  quarter: Int,
  year: Int,
  statementData: StatementDataField
)

/**
 * Case class representing daily metrics for a stock.
 *
 * @param date The date of the data.
 * @param marketCap The market capitalization.
 * @param enterpriseVal The enterprise value.
 * @param peRatio The price-to-earnings ratio.
 * @param pbRatio The price-to-book ratio.
 * @param trailingPEG1Y The trailing PEG over 1 year.
 */
case class DailyMetrics(
  date: LocalDateTime,
  marketCap: Double,
  enterpriseVal: Double,
  peRatio: Double,
  pbRatio: Double,
  trailingPEG1Y: Double
)

/**
 * Case class representing meta data for a company's fundamentals.
 *
 * @param permaTicker The permanent ticker symbol for the company.
 * @param ticker The ticker symbol for the company.
 * @param name The name of the company.
 * @param isActive Whether the company is active.
 * @param isADR Whether the company is an ADR.
 * @param sector The sector of the company.
 * @param industry The industry of the company.
 * @param sicCode The SIC code of the company.
 * @param sicSector The SIC sector of the company.
 * @param sicIndustry The SIC industry of the company.
 * @param reportingCurrency The currency the company reports in.
 * @param location The location of the company.
 * @param companyWebsite The website of the company.
 * @param secFilingWebsite The SEC filing website of the company.
 * @param statementLastUpdated The date when the company's statements were last updated.
 * @param dailyLastUpdated The date when the company's daily data was last updated.
 */
case class FundamentalsMeta(
  permaTicker: String,
  ticker: String,
  name: String,
  isActive: Boolean,
  isADR: Boolean,
  sector: String,
  industry: String,
  sicCode: Int,
  sicSector: String,
  sicIndustry: String,
  reportingCurrency: String,
  location: String,
  companyWebsite: String,
  secFilingWebsite: String,
  statementLastUpdated: LocalDateTime,
  dailyLastUpdated: LocalDateTime
)

/**
 * Trait for the Tiingo APIs Fundamentals endpoint.
 */
trait FundamentalsEndpoint extends Endpoint {

  private val baseUrl = "https://api.tiingo.com/tiingo/fundamentals"

  /**
   * Fetches the definitions of financial metrics.
   *
   * @return A future of a list of definitions.
   */
  def fetchDefinitions: Future[List[Definition]] = {
    val url: Uri = s"$baseUrl/definitions"
    val urlWithQuery = url.withQuery(
      Uri.Query(
        "token" -> config.apiKey.get
      )
    )
    logger.debug(s"Sending request to $url")
    get[List[Definition]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }

  /**
   * Fetches financial statement data for a given ticker symbol.
   *
   * @param ticker    The ticker symbol.
   * @param startDate The start date for the data.
   * @param endDate   The end date for the data.
   * @return A future of a list of statement data.
   */
  def fetchStatementData(
    ticker: String,
    startDate: Option[LocalDate] = None,
    endDate: Option[LocalDate] = None
  ): Future[List[StatementData]] = {
    val url: Uri = s"$baseUrl/$ticker/statements"
    val key = config.apiKey.get
    val urlWithQuery = (startDate, endDate) match {
      case (Some(start), Some(end)) =>
        url.withQuery(
          Uri.Query(
            "token" -> key,
            "startDate" -> start.toString,
            "endDate" -> end.toString
          )
        )
      case (Some(start), None) =>
        url.withQuery(
          Uri.Query(
            "token" -> key,
            "startDate" -> start.toString
          )
        )
      case (None, Some(end)) =>
        url.withQuery(
          Uri.Query(
            "token" -> key,
            "endDate" -> end.toString
          )
        )
      case (None, None) => url.withQuery(Uri.Query("token" -> key))
    }
    logger.debug(s"Sending request to $url")
    get[List[StatementData]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }

  /**
   * Fetches daily financial metrics for a given ticker symbol.
   *
   * @param ticker The ticker symbol.
   * @return A future of daily metrics.
   */
  def fetchDailyMetrics(ticker: String): Future[DailyMetrics] = {
    val url: Uri = s"$baseUrl/$ticker/daily"
    val key = config.apiKey.get
    val urlWithQuery = url.withQuery(
      Uri.Query("token" -> key)
    )
    logger.debug(s"Sending request to $url")
    get[List[DailyMetrics]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    ).map(_.head)
  }

  /**
   * Fetches meta data for a company's fundamentals.
   *
   * @return A future of a list of fundamentals meta data.
   */
  def fetchFundamentalsMeta: Future[List[FundamentalsMeta]] = {
    val url: Uri = s"$baseUrl/meta"
    val key = config.apiKey.get
    val urlWithQuery = url.withQuery(
      Uri.Query("token" -> key)
    )
    logger.debug(s"Sending request to $url")
    get[List[FundamentalsMeta]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }
}

/**
 * The companion object for creating instances of FundamentalsEndpoint.
 */
object FundamentalsEndpoint {

  /**
   * The companion object for creating instances of FundamentalsEndpoint.
   *
   * @param conf The configuration.
   * @param sys  The actor system.
   * @return A new FundamentalsEndpoint.
   */
  def apply(conf: APIConfig)(implicit sys: ActorSystem): FundamentalsEndpoint =
    new FundamentalsEndpoint {
      override val config: APIConfig = conf
      val system: ActorSystem = sys
      implicit override val materializer: Materializer = Materializer(system)
      implicit override val ec: ExecutionContext =
        system.dispatcher
    }
}
