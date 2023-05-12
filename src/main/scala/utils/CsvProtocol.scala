package io.github.crotodev.tiingo

import endpoints.{EODPriceData, News, TickerMeta}
import utils.ClientUtils

import akka.http.scaladsl.unmarshalling._

import scala.util.{Failure, Success, Try}

/**
 * Object containing implicit CSV formats for the Tiingo API endpoint case classes.
 */
object CsvProtocol {

  implicit val tickerMetaCsvFormat: FromEntityUnmarshaller[TickerMeta] =
    PredefinedFromEntityUnmarshallers.byteStringUnmarshaller.map { byteString =>
      val csv   = byteString.utf8String
      val rows  = csv.split("\n")
      val row   = rows(1)
      val cells = row.split(",")

      Try(
        TickerMeta(
          cells(0),
          cells(1),
          cells(2),
          ClientUtils.parseDateTime(cells(3)) match {
            case Right(startDate) => startDate
            case Left(error)      => throw new Exception(error)
          },
          ClientUtils.parseDateTime(cells(4)) match {
            case Right(endDate) => endDate
            case Left(error)    => throw new Exception(error)
          },
          cells(5)
        )
      ) match {
        case Success(tickerMeta) => tickerMeta
        case Failure(exception)  => throw exception
      }
    }

  implicit val eodPriceDataCsvFormat: FromEntityUnmarshaller[EODPriceData] =
    PredefinedFromEntityUnmarshallers.byteStringUnmarshaller.map { byteString =>
      val csv   = byteString.utf8String
      val rows  = csv.split("\n")
      val row   = rows(1)
      val cells = row.split(",")

      Try(
        EODPriceData(
          ClientUtils.parseDateTime(cells(0)) match {
            case Right(date) => date
            case Left(error) => throw new Exception(error)
          },
          cells(1).toDouble,
          cells(2).toDouble,
          cells(3).toDouble,
          cells(4).toDouble,
          cells(5).toLong,
          cells(6).toDouble,
          cells(7).toDouble,
          cells(8).toDouble,
          cells(9).toDouble,
          cells(10).toLong,
          cells(11).toDouble,
          cells(12).toDouble
        )
      ) match {
        case Success(eodPriceData) => eodPriceData
        case Failure(exception)    => throw exception
      }
    }

  implicit val newsCsvFormat: FromEntityUnmarshaller[News] =
    PredefinedFromEntityUnmarshallers.byteStringUnmarshaller.map { byteString =>
      val csv   = byteString.utf8String
      val rows  = csv.split("\n")
      val row   = rows(1)
      val cells = row.split(",")

      Try {
        News(
          cells(0).toInt,
          cells(1),
          cells(2),
          cells(3),
          ClientUtils.parseDateTime(cells(4)) match {
            case Right(date) => date
            case Left(error) => throw new Exception(error)
          },
          ClientUtils.parseDateTime(cells(5)) match {
            case Right(date) => date
            case Left(error) => throw new Exception(error)
          },
          cells(6),
          List(cells(7)),
          List(cells(8))
        )
      } match {
        case Success(news)      => news
        case Failure(exception) => throw exception
      }
    }

}
