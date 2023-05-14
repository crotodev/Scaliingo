package io.github.crotodev.tiingo
package models

/**
 * Represents an error that can occur while interacting with the Tiingo API.
 */
sealed trait APIError extends Throwable

/**
 * An error indicating that the provided frequency is invalid.
 */
case object InvalidFrequencyError extends APIError {
  override def toString: String = "Invalid frequency"
}

/**
 * An error indicating that the API key is missing or not provided.
 */
case object MissingApiKeyError extends APIError {
  override def toString: String = "Missing API key"
}

/**
 * An error indicating that the provided format is invalid.
 */
case object InvalidFormatError extends APIError {
  override def toString: String = "Invalid format"
}
