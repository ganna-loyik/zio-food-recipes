package healthcheck

import zio.http.*
import zio.http.model.*
import zio.test.*
import zio.test.Assertion.*

object HealthcheckSpec extends ZIOSpecDefault:
  def spec = suite("http")(
    suite("health check")(
      test("ok status") {
        val request = Request.default(method = Method.GET, url = URL(!! / "health"))
        val actual = Healthcheck.routes.runZIO(request)
        assertZIO(actual)(equalTo(Response(Status.NoContent)))
      }
    )
  )