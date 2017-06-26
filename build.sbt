lazy val akkaHttpVersion = "10.0.6"
lazy val akkaVersion    = "2.5.1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "minesweeper",
      scalaVersion    := "2.12.2"
    )),
    name := "SolveService",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"         % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"       % akkaVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"         % "3.0.1"         % Test
    )
  )

enablePlugins(JavaAppPackaging)
