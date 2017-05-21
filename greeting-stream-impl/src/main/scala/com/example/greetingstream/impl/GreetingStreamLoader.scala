package com.example.greetingstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.greetingstream.api.GreetingStreamService
import com.example.greeting.api.GreetingService
import com.softwaremill.macwire._

class GreetingStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new GreetingStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new GreetingStreamApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[GreetingStreamService]
  )
}

abstract class GreetingStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[GreetingStreamService](wire[GreetingStreamServiceImpl])

  // Bind the GreetingService client
  lazy val greetingService = serviceClient.implement[GreetingService]
}
