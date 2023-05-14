# Scaliingo

Scaliingo is a Scala client for interfacing with the Tiingo API. This robust library is designed to connect effortlessly with the Tiingo financial data platform and supports various Tiingo's API endpoints such as end-of-day data, news, cryptocurrency, forex, and IEX data.

## Description

Scaliingo offers a user-friendly and straightforward interface to retrieve market data, making it convenient for users to integrate Tiingo's financial data into their Scala applications. It incorporates support for asynchronous programming using Futures, ensuring non-blocking operations and enhancing the overall performance of applications.

For more details about the Tiingo API, please refer to its [official documentation](https://api.tiingo.com/documentation).

## Key Features

- Support all major Tiingo API endpoints: end-of-day, news, cryptocurrency, forex, fundamentals, and IEX data.
- Asynchronous programming support with Futures for non-blocking operations.
- Robust error handling and request retries.
- Customizable configurations include API keys, request headers, request timeout, and pause duration between requests.
- Strongly-typed data models for ease of use and compile-time type checking.

Scaliingo is an excellent resource for developers seeking reliable and current financial information from the Tiingo platform. This open-source tool is available under the Apache License, Version 2.0, and welcomes contributions from developers. Whether you're creating an algorithmic trading bot, a financial data analytics tool, or a basic stock tracking app, the client is an ideal choice for your needs. With its user-friendly design and comprehensive features, Scaliingo is a must-have resource for any developer looking to stay ahead in finance.

## Usage

```scala
import akka.actor.ActorSystem
import com.crotodev.tiingo.TiingoAPI

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("scaliingo-demo")

  // Replace "your-api-key" with your actual Tiingo API key
  val client = TiingoAPI(apiKey = Some("your-api-key"))

  client.getLatestTickerData("AAPL").onComplete { response =>
    response match {
      case Success(eodData) =>
        println("End-of-Day Data for AAPL:")
        eodData.foreach(println)

      case Failure(exception) =>
        println(s"Error fetching EOD data: ${exception.getMessage}")
    }
    system.terminate()
  }
}

```

## License

This project is licensed under the Apache License, Version 2.0. For more information, please refer to the `LICENSE` file in the project root.

## Contributions

We welcome contributions from the community. Please refer to the `CONTRIBUTING.md` file for more information.

## Disclaimer

Please note that this project, Scaliingo, is an independent project developed for interacting with the Tiingo API and is not officially affiliated with, endorsed by, or directly related to Tiingo Inc. or their official API. The use of this project is at your own discretion and risk. Please ensure you abide by Tiingo's API usage policies when using Scaliingo.
