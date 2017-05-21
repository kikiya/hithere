package com.example.greeting.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

/**
  * The greeting service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the GreetingService.
  */
trait GreetingService extends Service {

  /**
    * Example: curl http://localhost:9000/api/hello/Alice
    */
  def hello(id: String): ServiceCall[NotUsed, String]

  /**
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
    * "Hi"}' http://localhost:9000/api/hello/Alice
    */
  def useGreeting(id: String): ServiceCall[GreetingMessage, Done]

  /**
    * This  gets published to Kafka
    *
    */
  def greetingEvents() : Topic[GreetingEvent]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("greeting").withCalls(
      pathCall("/api/hello/:id", hello _),
      pathCall("/api/hello/:id", useGreeting _)
    ).withTopics(
      topic("greeting-events", greetingEvents)
        // Kafka partitions messages, messages within the same partition will
        // be delivered in order, to ensure that all messages for the same user
        // go to the same partition (and hence are delivered in order with respect
        // to that user), we configure a partition key strategy that extracts the
        // name as the partition key.
      .addProperty(KafkaProperties.partitionKeyStrategy,
      PartitionKeyStrategy[GreetingEvent](_.name))
    ).withAutoAcl(true)
    // @formatter:on
  }
}

/**
  * The greeting message class.
  */
case class GreetingMessage(message: String)

object GreetingMessage {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[GreetingMessage] = Json.format[GreetingMessage]
}

/**
  * This interface defines all the events that the GreetingEntity supports.
  */
sealed trait GreetingEvent{
  val name: String
}

object GreetingEvent{
  implicit val format: Format[GreetingEvent] = Json.format
}

/**
  * An event that represents a change in greeting message.
  */
case class GreetingMessageChanged(name: String, message: String) extends GreetingEvent

object GreetingMessageChanged {

  /**
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[GreetingMessageChanged] = Json.format
}

