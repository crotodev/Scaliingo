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

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

import java.time.{LocalDate, LocalDateTime}

/**
 * Object containing implicit JSON formats for the Tiingo API endpoint case classes.
 */
object JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object LocalDateJsonFormat extends RootJsonFormat[LocalDate] {

    override def read(json: JsValue): LocalDate =
      ClientUtils.parseDate(json.convertTo[String]) match {
        case Left(error) => throw new Exception(error)
        case Right(date) => date
      }

    override def write(obj: LocalDate): JsValue = JsString(obj.toString)
  }

  implicit object LocalDateTimeJsonFormat extends RootJsonFormat[LocalDateTime] {

    override def read(json: JsValue): LocalDateTime =
      ClientUtils.parseDateTime(json.convertTo[String]) match {
        case Left(error) => throw new Exception(error)
        case Right(date) => date
      }

    override def write(obj: LocalDateTime): JsValue = JsString(obj.toString)
  }

  implicit val tickerMetaJsonFormat: RootJsonFormat[TickerMeta] = jsonFormat(
    TickerMeta,
    "ticker",
    "name",
    "exchangeCode",
    "startDate",
    "endDate",
    "description"
  )

  implicit val eodPriceDataJsonFormat: RootJsonFormat[EODPriceData] = jsonFormat(
    EODPriceData,
    "date",
    "close",
    "high",
    "low",
    "open",
    "volume",
    "adjClose",
    "adjHigh",
    "adjLow",
    "adjOpen",
    "adjVolume",
    "divCash",
    "splitFactor"
  )

  implicit val newsJsonFormat: RootJsonFormat[News] = jsonFormat(
    News,
    "id",
    "title",
    "url",
    "description",
    "publishedDate",
    "crawlData",
    "source",
    "tickers",
    "tags"
  )

  implicit val bulkDownloadJsonFormat: RootJsonFormat[BulkDownload] = jsonFormat(
    BulkDownload,
    "id",
    "url",
    "filename",
    "batchType",
    "startDate",
    "endDate",
    "fileSizeCompressed",
    "fileSizeUncompressed"
  )

  implicit val cryptoPriceDataJsonFormat: RootJsonFormat[CryptoPriceData] = jsonFormat(
    CryptoPriceData,
    "date",
    "close",
    "high",
    "low",
    "open",
    "volume",
    "volumeNotional",
    "numberOfTrades"
  )

  implicit val cryptoPriceJsonFormat: RootJsonFormat[CryptoPrice] = {
    jsonFormat(
      CryptoPrice,
      "ticker",
      "baseCurrency",
      "quoteCurrency",
      "priceData"
    )
  }
  implicit val cryptoMetaJsonFormat: RootJsonFormat[CryptoMeta] = jsonFormat(
    CryptoMeta,
    "ticker",
    "name",
    "baseCurrency",
    "quoteCurrency",
    "description"
  )

  implicit val fxTopDataJsonFormat: RootJsonFormat[FXTopData] = jsonFormat(
    FXTopData,
    "ticker",
    "timestamp",
    "midPrice",
    "bidSize",
    "bidPrice",
    "askSize",
    "askPrice"
  )

  implicit val fxIntradayDataJsonFormat: RootJsonFormat[FXIntradayData] = jsonFormat(
    FXIntradayData,
    "date",
    "ticker",
    "open",
    "high",
    "low",
    "close"
  )

  implicit val iexLastPriceJsonFormat: RootJsonFormat[IEXLastPriceData] = jsonFormat(
    IEXLastPriceData,
    "ticker",
    "timestamp",
    "quoteTimestamp",
    "lastSaleTimestamp",
    "last",
    "lastSize",
    "tngoLast",
    "prevClose",
    "open",
    "high",
    "low",
    "mid",
    "volume",
    "bidSize",
    "bidPrice",
    "askSize",
    "askPrice"
  )

  implicit val iexHistoricalJsonPriceData: RootJsonFormat[IEXHistoricalPriceData] =
    jsonFormat(
      IEXHistoricalPriceData,
      "date",
      "open",
      "high",
      "low",
      "close",
      "volume"
    )

  implicit val definitionJsonFormat: RootJsonFormat[Definition] =
    jsonFormat(
      Definition,
      "dataCode",
      "name",
      "description",
      "statementType",
      "units"
    )

  implicit val statementDataFieldJsonFormat: RootJsonFormat[StatementDataField] =
    jsonFormat(
      StatementDataField,
      "dataCode",
      "value"
    )

  implicit val statementDataJsonFormat: RootJsonFormat[StatementData] = jsonFormat(
    StatementData,
    "date",
    "quarter",
    "year",
    "statementData"
  )

  implicit val dailyMetricsJsonFormat: RootJsonFormat[DailyMetrics] =
    jsonFormat(
      DailyMetrics,
      "date",
      "marketCap",
      "enterpriseVal",
      "peRatio",
      "pbRatio",
      "trailingPEG1Y"
    )

  implicit val fundamentalsMetaJsonFormat: RootJsonFormat[FundamentalsMeta] =
    jsonFormat(
      FundamentalsMeta,
      "permaTicker",
      "ticker",
      "name",
      "isActive",
      "isADR",
      "sector",
      "industry",
      "sicCode",
      "sicSector",
      "sicIndustry",
      "reportingCurrency",
      "location",
      "companyWebsite",
      "secFilingWebsite",
      "statementLastUpdated",
      "dailyLastUpdated"
    )

}
