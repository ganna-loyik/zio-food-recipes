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
            ZIO.attempt(ConfigFactory.defaultApplication().resolve())
          )
        )
      )
    )

  final case class DbConfig(url: String, user: String, password: String)

  object DbConfig:

    private val dbConfigDescription =
      (
        nested("postgres-db")(nested("dataSource")(string("url"))) <*>
          nested("postgres-db")(nested("dataSource")(string("user"))) <*>
          nested("postgres-db")(nested("dataSource")(string("password")))
      ).to[DbConfig]

    val layer = ZLayer(
      read(
        dbConfigDescription.from(
          TypesafeConfigSource.fromTypesafeConfig(
            ZIO.attempt(ConfigFactory.defaultApplication().resolve())
          )
        )
      )
    )
