package com.example.greeting.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.datastax.driver.core.{PreparedStatement, Row}
import com.example.greeting.api.{ Guest}

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by kiki on 5/21/17.
  */
private[impl] class WhosThereRepository(session: CassandraSession)(implicit ec:ExecutionContext) {

  def getAllGuests : Future[Seq[Guest]] = {
    session.selectAll("""SELECT * FROM lagom_guests""").map{ row =>
      row.map(convertGuest)
    }
  }

  def addGuest(guest: String, message: String) = for {
    _ <- session.executeWrite("INSERT INTO lagom_guests (guest, message) VALUES (?, ?)", guest, message)
  } yield Done

  private def convertGuest(guest: Row): Guest = {
    Guest(guest.getString("guest"), guest.getString("message") )
  }

}

private[impl] class GreetingEventProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[com.example.greeting.impl.GreetingEvent] {

  def buildHandler = {
    readSide.builder[GreetingEvent]("greetingEventOffset")
      .setGlobalPrepare(createTables)
      .build
  }

  def aggregateTags = GreetingEvent.Tag.allTags

  def createTables() = for {
    _ <- session.executeCreateTable(
      """
      CREATE TABLE IF NOT EXISTS lagom_guests (guest text PRIMARY KEY, message text)

    """)
  } yield Done

}
