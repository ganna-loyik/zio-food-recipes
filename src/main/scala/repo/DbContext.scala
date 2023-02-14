package repo

import io.getquill.{Literal, PostgresZioJdbcContext}

object DbContext extends PostgresZioJdbcContext(Literal) {}
