organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "3.3"

lazy val `greeting` = (project in file("."))
  .aggregate(`greeting-api`, `greeting-impl`, `whosthere-api`, `whosthere-impl`)

lazy val `greeting-api` = (project in file("greeting-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )

lazy val `greeting-impl` = (project in file("greeting-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`greeting-api`)

lazy val `whosthere-api` = (project in file("whosthere-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  )

lazy val `whosthere-impl` = (project in file("whosthere-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      lagomScaladslKafkaBroker,
      "com.datastax.cassandra" % "cassandra-driver-extras" % "3.0.0",
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`greeting-api`, `whosthere-api`)


