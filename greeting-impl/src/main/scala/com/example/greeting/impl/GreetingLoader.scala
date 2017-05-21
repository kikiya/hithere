package com.example.greeting.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.greeting.api.GreetingService
import com.softwaremill.macwire._

class GreetingLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new GreetingApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new GreetingApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[GreetingService]
  )
}

abstract class GreetingApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[GreetingService](wire[GreetingServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = GreetingSerializerRegistry

  // Register the greeting persistent entity
  persistentEntityRegistry.register(wire[GreetingEntity])
}
