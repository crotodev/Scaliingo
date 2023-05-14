package io.github.crotodev.tiingo
package models

import akka.http.scaladsl.model.HttpHeader
import cats.data.Validated
import cats.implicits._
import com.typesafe.config.ConfigFactory
import io.github.crotodev.utils.{Buildable, HttpUtils}

import scala.concurrent.duration._

/**
 * The case class representing the configuration for the Tiingo API.
 *
 * @param apiKey the API key for the Tiingo API.
 * @param headers the HTTP headers to be sent with the requests.
 * @param pause the duration to pause between requests.
 * @param timeout the duration to wait for a response before timing out.
 */
final case class APIConfig(
  apiKey: Option[String] = None,
  headers: List[HttpHeader] = Nil,
  pause: FiniteDuration = 1.seconds,
  timeout: FiniteDuration = 15.seconds
)

/**
 * The companion object for the API configuration.
 */
object APIConfig {

  private val config = ConfigFactory.load()

  /**
   * Creates a new instance of the API configuration.
   *
   * @param apiKey the API key for the Tiingo API.
   * @param headers the HTTP headers to be sent with the requests.
   * @return a new instance of the API configuration.
   */
  def apply(
    apiKey: Option[String],
    headers: Map[String, String]
  ): APIConfig = {
    builder().withApiKey(apiKey).withHeaders(headers).build()
  }

  /**
   * @return a new instance of the builder for the API configuration.
   */
  def builder(): Builder = new Builder()

  /**
   *  Resolves the API key to be used for the requests.
   *
   * @param providedApiKey the API key provided by the user.
   * @return the API key to be used for the requests.
   */
  private def resolveApiKey(
    providedApiKey: Option[String]
  ): Validated[APIError, String] =
    providedApiKey
      .orElse {
        val configApiKey = config.getString("api.key")
        if (configApiKey.equals("YOUR_API_KEY_HERE")) sys.env.get("TIINGO_API_KEY")
        else Some(configApiKey)
      }
      .toValid(MissingApiKeyError)

  class Builder() extends Buildable[APIConfig] {
    private var apiKey: Option[String] = None
    private var headers: Map[String, String] = Map.empty

    /**
     *  Sets the API key for the Tiingo API.
     *
     * @param apiKey the API key for the Tiingo API.
     * @return  the builder.
     */
    def withApiKey(apiKey: Option[String]): Builder = {
      this.apiKey = apiKey
      this
    }

    /**
     *  Sets the HTTP headers to be sent with the requests.
     *
     * @param headers the HTTP headers to be sent with the requests.
     * @return the builder.
     */
    def withHeaders(headers: Map[String, String]): Builder = {
      this.headers = headers
      this
    }

    /**
     * Build the type T
     *
     * @return an instance of T
     */
    override def build(): APIConfig = {
      val resolve = resolveApiKey(apiKey).map { resolvedApiKey =>
        val updatedHeaders = headers ++ Map(
          "Authorization" -> resolvedApiKey
        )
        val parsedHeaders = HttpUtils.parseHeaders(updatedHeaders)
        APIConfig(Some(resolvedApiKey), parsedHeaders)
      }
      resolve.fold(
        error => throw new IllegalArgumentException(error.toString),
        identity
      )
    }
  }
}
