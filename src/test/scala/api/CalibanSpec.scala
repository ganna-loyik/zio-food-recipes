package api

import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*
import zio.*
import zio.json.*
import zio.http.{Client as HttpClient, *}
import repo.RecipeTagRepositoryLive
import repo.postgresql.*
import domain.*
import graphql.client.Client
import graphql.schemas.RecipeTagSchema
import service.RecipeTagServiceLive
import sttp.client3.*
import sttp.client3.httpclient.zio.*
import sttp.model.Uri

object CalibanSpec extends ZIOSpecDefault:

  /*val containerLayer = ZLayer.scoped(PostgresContainer.make())

  val dataSourceLayer =
    DataSourceBuilderLive.layer.flatMap(builder => ZLayer.fromFunction(() => builder.get.dataSource))

  val repoLayer = RecipeTagRepositoryLive.layer
  val serviceLayer = RecipeTagServiceLive.layer*/

  val serverUrl = Uri.safeApply("localhost", 9000, Seq("graphql")).toOption.get
  val loginUrl = "http://localhost:9000/login"

  override def spec =
    suite("GraphQL API test (with login)")(
      test("get tags") {
        for {
          token   <- HttpClient.request(s"$loginUrl/me/test").flatMap(_.body.asString)
          backend <- HttpClientZioBackend()
          request  = Client.Queries.recipeTags(Client.RecipeTag.name).toRequest(serverUrl)
          data    <- request.header("Authorization", s"Bearer $token").send(backend).map(_.body).absolve
        } yield assert(data.toSet)(equalTo(Set("meat", "breakfast", "drink")))
      }
    ).provideLayer(HttpClient.default) @@ /*sequential*/ TestAspect.ignore
