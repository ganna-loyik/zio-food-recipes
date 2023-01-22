package configuration

import com.typesafe.config.ConfigFactory
import zio.*
import zio.config.*
import zio.config.ConfigDescriptor.*
import zio.config.typesafe.TypesafeConfigSource

object Configuration:

  final case class ServerConfig(port: Int)

  object ServerConfig:

    private val serverConfigDescription =
      nested("server-config") {
        int("port").default(9000)
      }.to[ServerConfig]

    val layer = ZLayer(
      read(
        serverConfigDescription.from(
          TypesafeConfigSource.fromTypesafeConfig(
            ZIO.attempt(ConfigFactory.defaultApplication())
          )
        )
      )
    )
