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
package utils

import io.github.crotodev.utils.DateTimeUtils
import com.typesafe.config.ConfigFactory

/**
 * An object containing utility methods for the Tiingo client.
 */
object ClientUtils extends DateTimeUtils {

  import java.time.LocalDate

  /**
   * The default frequency used for sanitization, set to "5Min".
   */
  protected val defaultFrequency: String = "5Min"

  /**
   * Sanitizes an API key. If the given API key is None, tries to get it from the application configuration.
   * If the key in the configuration is set to "YOUR_API_KEY_HERE", throws an exception.
   *
   * @param apiKey The API key to sanitize.
   * @return The sanitized API key.
//   * @throws java.lang.Exception if no valid API key is provided or found in the configuration.
   */
  def sanitizeApiKey(apiKey: Option[String]): String = {
    val config = ConfigFactory.load()
    apiKey match {
      case Some(key) if key.nonEmpty => key
      case Some(_)                   => throw new Exception("Invalid API key provided")
      case None =>
        config.getString("api.key") match {
          case key if key.nonEmpty => key
          case _                   => throw new Exception("No API key provided")
        }
    }
  }

  /**
   * Sanitizes a frequency. If the given frequency is None or empty, returns the default frequency ("5min").
   *
   * @param freq The frequency to sanitize.
   * @return The sanitized frequency.
   */
  def sanitizeFreq(freq: Option[String]): String =
    freq match {
      case Some(f) if f.isEmpty => defaultFrequency
      case Some(f)              => f
      case None                 => defaultFrequency
    }

  /**
   *
   * Given an optional start date, this method returns either the provided start date
   * if it is present or the default start date if it is not. If the start date is not present,
   * the method will return the default start date.
   *
   * @param startDate An optional LocalDate representing the start date.
   * @return A LocalDate representing the sanitized start date.
   */
  def sanitizeStartDate(startDate: Option[LocalDate]): LocalDate =
    startDate match {
      case Some(d) => d
      case None    => defaultStartDate
    }

}
