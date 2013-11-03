name := "rssreader"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.scala-tools.time" % "time_2.9.1" % "0.5"
)     

play.Project.playScalaSettings
