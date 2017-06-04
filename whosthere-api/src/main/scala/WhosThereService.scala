package com.example.greeting.api

import akka.NotUsed
import com.fasterxml.jackson.databind.JsonSerializer
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

//import scala.collection.immutable.Seq

/**
  * Created by kiki on 5/21/17.
  */
trait WhosThereService extends Service {

  def whosThere: ServiceCall[NotUsed, Seq[Guest]]

  final override def descriptor = {
    import Service._

    named("whosthere").withCalls(
      pathCall("/api/whosthere", whosThere _)
    )

  }
}

case class Guest(name: String, customGreeting: String)

object Guest {
  implicit val format: Format[Guest] = Json.format
}


