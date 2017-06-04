package com.example.greeting.impl

import com.example.greeting.api.{GreetingService, WhosThereService}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomServerComponents, _}
import com.softwaremill.macwire.wire
import play.api.Environment
import play.api.libs.ws.ahc.AhcWSComponents

import scala.concurrent.ExecutionContext

/**
  * Created by kiki on 5/21/17.
  */
trait WhosThereComponents extends LagomServerComponents
  with CassandraPersistenceComponents {

  implicit def executionContext: ExecutionContext
  def environment: Environment



  // Bind the repository
  lazy val whosThereRepository = wire[WhosThereRepository]


  readSide.register(wire[GreetingEventProcessor])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = WhosThereSerializerRegistry

}

class WhosThereLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new WhosThereApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new WhosThereApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[WhosThereService]
  )
}

abstract class WhosThereApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with LagomKafkaComponents
    with WhosThereComponents {

  // Bind the GreetingService client
  lazy val greetingService = serviceClient.implement[GreetingService]

  wire[WhosThereSubscriber]

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[WhosThereService](wire[WhosThereServiceImpl])

}




