val zioVersion = "2.0.5"
val zioHttpVersion = "0.0.5"
val zioJsonVersion = "0.4.2"
//val zioActorVersion = "0.1.0"
val logbackVersion = "1.4.5"
val testcontainersVersion = "1.17.6"
val testcontainersScalaVersion = "0.40.12"
val quillVersion = "4.6.0.1"
val postgresqlVersion = "42.5.1"
val calibanVersion = "2.1.0"
val tapiZioJsonVersion = "1.2.11"
val flywayVersion = "9.11.0"
val zioConfigVersion = "3.0.6"
val zioMockVersion = "1.0.0-RC8"
val jwtCoreVersion = "9.2.0"
val akkaVersion = "2.7.0"
val akkaPersistenceJdbc = "5.2.1"
val jacksonVersion = "2.14.0"
val scalaTestVersion = "3.2.15"

scalaVersion := "3.2.2"
name         := "zio-food-recipes"
version      := "0.0.1"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      ("io.getquill"                %% "quill-jdbc-zio"         % quillVersion).excludeAll(
        ExclusionRule(organization = "org.scala-lang.modules")
      ),
      "org.postgresql"               % "postgresql"             % postgresqlVersion,
      "com.github.ghostdogpr"       %% "caliban"                % calibanVersion,
      "com.github.ghostdogpr"       %% "caliban-zio-http"       % calibanVersion,
      "com.github.ghostdogpr"       %% "caliban-client"         % calibanVersion,
      "org.flywaydb"                 % "flyway-core"            % flywayVersion,
      "dev.zio"                     %% "zio"                    % zioVersion,
      "dev.zio"                     %% "zio-streams"            % zioVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"         % tapiZioJsonVersion,
      "dev.zio"                     %% "zio-http"               % zioHttpVersion,
      "dev.zio"                     %% "zio-config"             % zioConfigVersion,
      "dev.zio"                     %% "zio-config-typesafe"    % zioConfigVersion,
      "com.github.jwt-scala"        %% "jwt-core"               % jwtCoreVersion,
      "ch.qos.logback"               % "logback-classic"        % logbackVersion,
      "dev.zio"                     %% "zio-json"               % zioJsonVersion,
      ("com.typesafe.akka"          %% "akka-persistence-typed" % akkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.lightbend.akka"         %% "akka-persistence-jdbc"  % akkaPersistenceJdbc).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka"            %% "akka-serialization-jackson" % akkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.fasterxml.jackson.module" %% "jackson-module-scala"       % jacksonVersion).cross(CrossVersion.for3Use2_13),
      "dev.zio"                       %% "zio-test"                   % zioVersion     % Test,
      "dev.zio"                       %% "zio-test-sbt"               % zioVersion     % Test,
      "dev.zio"                       %% "zio-test-junit"             % zioVersion     % Test,
      "dev.zio"                       %% "zio-mock"                   % zioMockVersion % Test,
      "com.dimafeng"      %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % Test,
      "org.testcontainers" % "testcontainers"                  % testcontainersVersion      % Test,
      "org.testcontainers" % "database-commons"                % testcontainersVersion      % Test,
      "org.testcontainers" % "postgresql"                      % testcontainersVersion      % Test,
      "org.testcontainers" % "jdbc"                            % testcontainersVersion      % Test,
      "dev.zio"           %% "zio-test-magnolia"               % zioVersion                 % Test,
      ("com.typesafe.akka" %% "akka-testkit"             % akkaVersion      % Test).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-persistence-testkit" % akkaVersion      % Test).cross(CrossVersion.for3Use2_13),
      // "org.scalactic"      %% "scalactic"                % scalaTestVersion % Test,
      "org.scalatest"      %% "scalatest"                % scalaTestVersion % Test
    ),
    excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13",
    excludeDependencies += "com.lihaoyi"            % "geny_2.13",
    testFrameworks                                 := Seq(
      new TestFramework("zio.test.sbt.ZTestFramework"),
      TestFrameworks.ScalaTest
    )
  )

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(CalibanPlugin)

dockerExposedPorts := Seq(9000)
dockerBaseImage    := "eclipse-temurin:11"
dockerUsername     := sys.props.get("docker.username")
dockerRepository   := sys.props.get("docker.registry")
