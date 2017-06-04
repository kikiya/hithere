package com.example.greeting.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}

/**
  * Created by kiki on 6/4/17.
  */
/**
  * This interface defines all the events that the GreetingEntity supports.
  */
sealed trait GreetingEvent extends AggregateEvent[GreetingEvent]{
  override def aggregateTag: AggregateEventShards[GreetingEvent] = GreetingEvent.Tag

}

object GreetingEvent {
  val NumShards = 4
  val Tag = AggregateEventTag.sharded[GreetingEvent](4)
}
