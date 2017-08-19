/*
 * Copyright 2016 rdbc contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.util

import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers

import scala.concurrent.Future

class LoggingSpec
  extends RdbcUtilSpec
    with MockFactory
    with Matchers {

  "Logging's trace functionality" should {

    "not alter traced block result" when {
      "tracing is enabled" when {
        runTests(enableTracing = true)
      }

      "tracing is disabled" when {
        runTests(enableTracing = false)
      }
    }
  }

  private def runTests(enableTracing: Boolean): Unit = {

    val tester = new Logging {
      def tstFut(res: () => Future[String]): Future[String] = traced {
        res()
      }

      def tstNoFut(res: () => String): String = traced {
        res()
      }

      override protected val traceEnabled: Boolean = enableTracing
    }

    "result type is Future" when {

      "Future is successful" in {
        val res = "str"
        tester.tstFut(() => Future.successful(res)).get shouldBe res
      }

      "Future is failed" in {
        val ex = new RuntimeException
        the[RuntimeException] thrownBy {
          tester.tstFut(() => Future.failed(ex)).get
        } shouldBe theSameInstanceAs(ex)
      }
    }

    "result type is not Future" in {
      val res = "str"
      tester.tstNoFut(() => res) shouldBe theSameInstanceAs(res)
    }

    "block throws exception" in {
      val ex = new RuntimeException
      the[RuntimeException] thrownBy {
        tester.tstNoFut(() => throw ex)
      } shouldBe theSameInstanceAs(ex)
    }
  }

}
