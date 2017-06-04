package com.example.greeting.impl

import akka.Done
import akka.stream.scaladsl.Flow
import com.example.greeting.api.{GreetingMessageChanged, GreetingService, GreetingEvent => GreetingEventA}

import scala.concurrent.Future


/**
  * Created by kiki on 6/4/17.
  */
class WhosThereSubscriber(greetingService: GreetingService, whosThereRepository: WhosThereRepository) {

  greetingService.greetingEvents.subscribe.atLeastOnce(Flow[GreetingEventA].mapAsync(1) {

    case GreetingMessageChanged(name, message) => whosThereRepository.addGuest(name, message)

    case other => Future.successful(Done)

  })

}
