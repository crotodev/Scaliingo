package io.github.crotodev.tiingo
package endpoints

import akka.actor.ActorSystem
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class IEXEndpointSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  import models.APIConfig

  implicit val system: ActorSystem = ActorSystem("EODClientSpec")
  implicit val ec: ExecutionContext = system.dispatcher

  val config = APIConfig(sys.env.get("TIINGO_API_KEY"), timeout = 30.seconds)
  val client = IEXEndpoint(config)

  val ticker = "GOOGL"

//  override def afterAll(): Unit =
//    client.shutdown()

  behavior.of("IEXClientSpec")

  it should "fetchIEXLastPriceData" in {
    client.fetchIEXLastPriceData(Some(ticker)).onComplete(println)
  }
}
