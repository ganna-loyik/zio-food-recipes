package healthcheck

import zhttp.http.*
import zio.test.*
import zio.test.Assertion.*
import com.example.healthcheck.*

object HealthcheckSpec extends ZIOSpecDefault:
  def spec = suite("http")(
    suite("health check")(
      test("ok status") {
        val actual = Healthcheck.routes(Request(method = Method.GET, url = URL(!! / "health")))
        assertZIO(actual)(equalTo(Response(Status.NoContent, Headers.empty, HttpData.empty)))
      }
    )
  )