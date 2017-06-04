package com.example.greeting.impl

import akka.NotUsed
import com.example.greeting.api.{Guest, WhosThereService}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import scala.concurrent.ExecutionContext

/**
  * Created by kiki on 5/21/17.
  */
class WhosThereServiceImpl  (repository: WhosThereRepository) (implicit ec: ExecutionContext) extends WhosThereService {

  override def whosThere: ServiceCall[NotUsed, Seq[Guest]] = ServiceCall {  _ => repository.getAllGuests }

}
