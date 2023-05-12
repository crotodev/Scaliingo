package io.github.crotodev.tiingo

sealed trait APIError extends Throwable

case object InvalidFrequencyError extends APIError {
  override def toString: String = "Invalid frequency"
}

case object MissingApiKeyError extends APIError {
  override def toString: String = "Missing API key"
}

case object InvalidFormatError extends APIError {
  override def toString: String = "Invalid format"
}