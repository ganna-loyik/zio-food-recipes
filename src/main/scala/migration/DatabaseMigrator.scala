package migration

import configuration.Configuration.DbConfig
import org.flywaydb.core.Flyway

object DatabaseMigrator:
  def migrate(config: DbConfig) = {
    Flyway
      .configure()
      .validateMigrationNaming(true)
      .dataSource(config.url, config.user, config.password)
      .load()
      .migrate()
  }
