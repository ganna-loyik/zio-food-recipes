FROM sbtscala/scala-sbt:eclipse-temurin-focal-17.0.5_8_1.8.2_3.2.1

COPY build.sbt build.sbt
COPY project project

RUN sbt update

COPY src src

RUN sbt clean stage

EXPOSE 9000

CMD ./target/universal/stage/bin/zio-food-recipes