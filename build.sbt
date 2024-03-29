lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion = "2.5.22"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "sk.samuel",
      scalaVersion := "2.12.8"
    )),
    name := "OpenLawAssignment",
    libraryDependencies ++= Seq(
      // @formatter:off
      "com.typesafe.akka"           %% "akka-http"                    % akkaHttpVersion,
      "com.typesafe.akka"           %% "akka-http-spray-json"         % akkaHttpVersion,
      "com.typesafe.akka"           %% "akka-stream"                  % akkaVersion,
      "com.typesafe"                % "config"                        % "1.3.4",
      "com.typesafe.scala-logging"  %% "scala-logging"                % "3.9.2",
      "ch.qos.logback"              % "logback-classic"               % "1.2.1",

      "com.typesafe.akka" %% "akka-http-testkit"            % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"                 % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"          % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                    % "3.0.5"         % Test,
      // @formatter:on
    )
  )
