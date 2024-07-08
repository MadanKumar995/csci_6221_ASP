ThisBuild / scalaVersion := "2.13.14"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """to-do""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
      "com.typesafe.play" %% "play-json" % "2.10.5",
      "org.playframework" %% "play-slick" % "6.1.1",
      "com.typesafe.slick" %% "slick" % "3.5.0",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
//      "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
      "org.postgresql" % "postgresql" % "42.7.3",
      "org.mindrot" % "jbcrypt" % "0.4",
      "org.playframework.anorm" %% "anorm" % "2.7.0",

//      "com.typesafe.play" %% "play-slick" % "5.3.0",
//      "com.typesafe.play" %% "play-slick-evolutions" % "5.3.0",

    )
  )