package io.github.crotodev.tiingo
package endpoints

import akka.actor.ActorSystem
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class FXEndpointSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  import models.APIConfig

  implicit val system: ActorSystem  = ActorSystem("FXEndpointSpec")
  implicit val ec: ExecutionContext = system.dispatcher

  val config = APIConfig(sys.env.get("TIINGO_API_KEY"), timeout = 30.seconds)
  val client = FXEndpoint(config)

  val ticker = "audusd"

  override def afterAll(): Unit =
    client.shutdown()

  "the ticker of the fetched data" should "be the same as the requested ticker" in {
    val intraday = client.getFXIntradayData(ticker)
    intraday.map(_.ticker shouldBe ticker)
    intraday.onComplete(println)
  }
}
