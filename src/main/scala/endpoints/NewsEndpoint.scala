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
 * The case class representing the data structure for news data.
 *
 * @param id the id of the news.
 * @param title the title of the news.
 * @param url the url of the news.
 * @param description the description of the news.
 * @param publishedDate the published date of the news.
 * @param crawlDate the crawl date of the news.
 * @param source the source of the news.
 * @param tickers the list of tickers associated with the news.
 * @param tags the list of tags associated with the news.
 */
case class News(
  id: Int,
  title: String,
  url: String,
  description: String,
  publishedDate: LocalDateTime,
  crawlDate: LocalDateTime,
  source: String,
  tickers: List[String],
  tags: List[String]
)

/**
 * The case class representing the data structure for the bulk download data.
 *
 * @param id the id of the bulk download.
 * @param url the url of the bulk download.
 * @param filename the filename of the bulk download.
 * @param batchType the batch type of the bulk download.
 * @param startDate the start date of the bulk download.
 * @param endDate the end date of the bulk download.
 * @param fileSizeCompressed the file size compressed of the bulk download.
 * @param fileSizeUncompressed the file size uncompressed of the bulk download.
 */
case class BulkDownload(
  id: Int,
  url: String,
  filename: String,
  batchType: String,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  fileSizeCompressed: Int,
  fileSizeUncompressed: Int
)

/**
 * Trait for the Tiingo APIs News endpoint.
 */
trait NewsEndpoint extends Endpoint {

  private val baseUrl = "https://api.tiingo.com/tiingo/news"

  /**
   * Fetches the latest news data from the Tiingo API.
   *
   * @param tickers the list of ticker symbols to fetch news for.
   * @param tags    the list of tags to fetch news for.
   * @return a future of the list of news data.
   */
  def fetchLatestNews(
    tickers: Option[List[String]] = None,
    tags: Option[List[String]] = None
  ): Future[List[News]] = {
    val url: Uri = baseUrl

    val urlWithQuery = (tickers, tags) match {
      case (Some(tickers), Some(tags)) =>
        url.withQuery(
          Uri.Query(
            "tickers" -> tickers.mkString(","),
            "tags" -> tags.mkString(","),
            "token" -> key
          )
        )
      case (Some(tickers), None) =>
        url.withQuery(Uri.Query("tickers" -> tickers.mkString(","), "token" -> key))
      case (None, Some(tags)) =>
        url.withQuery(Uri.Query("tags" -> tags.mkString(","), "token" -> key))
      case (None, None) => url.withQuery(Uri.Query("token" -> key))
    }
    logger.debug(s"Sending request to $url")
    get[List[News]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }

  /**
   * Fetches the bulk download data from the Tiingo API.
   *
   * @param id the id of the bulk download to fetch.
   * @return a future of the list of bulk download data.
   */
  def fetchBulkDownload(id: Option[String] = None): Future[List[BulkDownload]] = {
    val url: Uri = s"$baseUrl/bulk_download"

    val urlWithQuery = id match {
      case Some(id) => url.withQuery(Uri.Query("id" -> id, "token" -> key))
      case None     => url.withQuery(Uri.Query("token" -> key))
    }
    logger.debug(s"Sending request to $url")
    get[List[BulkDownload]](
      urlWithQuery,
      config.headers,
      config.pause,
      config.timeout
    )
  }
}

/**
 * The companion object for creating instances of NewsEndpoint.
 */
object NewsEndpoint {

  /**
   * Creates a new instance of the NewsEndpoint.
   *
   * @param conf the configuration for the Tiingo API.
   * @param sys  the actor system for the client.
   * @return a new instance of the NewsEndpoint.
   */
  def apply(conf: APIConfig)(implicit sys: ActorSystem): NewsEndpoint =
    new NewsEndpoint {
      override val config: APIConfig = conf

      val system: ActorSystem = sys

      implicit override val materializer: Materializer = Materializer(system)

      implicit override val ec: ExecutionContext =
        system.dispatcher
    }
}
