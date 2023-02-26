val zioVersion = "2.0.5"
val zioHttpVersion = "2.0.0-RC10"
val zioJsonVersion = "0.4.2"
//val zioActorVersion = "0.1.0"
val logbackVersion = "1.4.5"
val testcontainersVersion = "1.17.6"
val testcontainersScalaVersion = "0.40.12"
val quillVersion = "4.6.0"
val postgresqlVersion = "42.5.1"
val calibanVersion = "2.0.2"
val flywayVersion = "9.11.0"
val zioConfigVersion = "3.0.6"
val zioMockVersion = "1.0.0-RC8"
val jwtCoreVersion = "9.2.0"
val akkaVersion = "2.7.0"

scalaVersion := "3.2.2"
name         := "zio-food-recipes"
version      := "0.0.1"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      ("io.getquill"          %% "quill-jdbc"                      % quillVersion).excludeAll(
        ExclusionRule(organization = "org.scala-lang.modules")
      ),
      ("io.getquill"          %% "quill-jdbc-zio"                  % quillVersion).excludeAll(
        ExclusionRule(organization = "org.scala-lang.modules")
      ),
      ("io.getquill"          %% "quill-jasync-postgres"           % quillVersion).excludeAll(
        ExclusionRule(organization = "org.scala-lang.modules")
      ),
      "org.postgresql"         % "postgresql"                      % postgresqlVersion,
      "com.github.ghostdogpr" %% "caliban"                         % calibanVersion,
      "com.github.ghostdogpr" %% "caliban-zio-http"                % calibanVersion,
      "org.flywaydb"           % "flyway-core"                     % flywayVersion,
      "dev.zio"               %% "zio"                             % zioVersion,
      "dev.zio"               %% "zio-streams"                     % zioVersion,
      "io.d11"                %% "zhttp"                           % zioHttpVersion,
      "dev.zio"               %% "zio-config"                      % zioConfigVersion,
      "dev.zio"               %% "zio-config-typesafe"             % zioConfigVersion,
      "com.github.jwt-scala"  %% "jwt-core"                        % jwtCoreVersion,
      "ch.qos.logback"         % "logback-classic"                 % logbackVersion,
      "dev.zio"               %% "zio-json"                        % zioJsonVersion,
      "com.typesafe.akka"     %% "akka-persistence-typed"          % akkaVersion,
      "dev.zio"               %% "zio-test"                        % zioVersion                 % Test,
      "dev.zio"               %% "zio-test-sbt"                    % zioVersion                 % Test,
      "dev.zio"               %% "zio-test-junit"                  % zioVersion                 % Test,
      "dev.zio"               %% "zio-mock"                        % zioMockVersion             % Test,
      "com.dimafeng"          %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % Test,
      "org.testcontainers"     % "testcontainers"                  % testcontainersVersion      % Test,
      "org.testcontainers"     % "database-commons"                % testcontainersVersion      % Test,
      "org.testcontainers"     % "postgresql"                      % testcontainersVersion      % Test,
      "org.testcontainers"     % "jdbc"                            % testcontainersVersion      % Test,
      "dev.zio"               %% "zio-test-magnolia"               % zioVersion                 % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerExposedPorts := Seq(9000)
dockerBaseImage    := "eclipse-temurin:11"
dockerUsername     := sys.props.get("docker.username")
dockerRepository   := sys.props.get("docker.registry")
