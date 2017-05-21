package com.example.greetingstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.greetingstream.api.GreetingStreamService
import com.example.greeting.api.GreetingService

import scala.concurrent.Future

/**
  * Implementation of the GreetingStreamService.
  */
class GreetingStreamServiceImpl(greetingService: GreetingService) extends GreetingStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(greetingService.hello(_).invoke()))
  }
}
