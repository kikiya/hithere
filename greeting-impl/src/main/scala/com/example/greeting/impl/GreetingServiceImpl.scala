package com.example.greeting.impl

import com.example.greeting.api
import com.example.greeting.api.GreetingService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.collection.immutable
import scala.concurrent.Future

/**
  * Implementation of the GreetingService.
  */
class GreetingServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends GreetingService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the greeting entity for the given ID.
    val ref = persistentEntityRegistry.refFor[GreetingEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id, None))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the greeting entity for the given ID.
    val ref = persistentEntityRegistry.refFor[GreetingEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }

  /**
    * This  gets published to Kafka
    *
    */
  override def greetingEvents(): Topic[api.GreetingEvent] = TopicProducer.taggedStreamWithOffset(GreetingEvent.Tag.allTags.to[immutable.Seq]) { (tag, offset) =>
    persistentEntityRegistry.eventStream(tag, offset).filter(e =>
      e.event.isInstanceOf[GreetingMessageChanged]
    ).mapAsync(1) {
      event => {
        event.event match {
          case GreetingMessageChanged(name, message) =>
            val toPublish = new com.example.greeting.api.GreetingMessageChanged(name,message)
            Future.successful(toPublish, event.offset)
        }
        }
      }
    }

}
