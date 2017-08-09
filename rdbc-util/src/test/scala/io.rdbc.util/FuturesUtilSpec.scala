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

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class FuturesUtilSpec
  extends RdbcUtilSpec
    with MockFactory
    with Matchers {

  implicit val ec = ExecutionContext.global

  "Futures.andThenF" should {

    import Futures._

    "apply side effecting function before continuing" when {
      "future is successful" in {
        val sideEffect = mockFunction[Unit]
        val res = 10

        val transformed = Future.successful(res).andThenF {
          case Success(`res`) => Future {
            Thread.sleep(1000L)
            sideEffect()
          }
        }

        sideEffect.expects().once()

        transformed.get shouldBe res
      }

      "future is failed" in {
        val failure = new RuntimeException
        val sideEffect = mockFunction[Unit]

        val transformed = Future.failed[Int](failure).andThenF {
          case Failure(`failure`) => Future {
            Thread.sleep(1000L)
            sideEffect()
          }
        }

        sideEffect.expects().once()

        the[RuntimeException] thrownBy {
          transformed.get
        } shouldBe theSameInstanceAs(failure)
      }
    }

    "not cause the future to fail if side-effecting function fails" in {
      val res = 10
      val transformed = Future.successful(res).andThenF {
        case _ => Future {
          Thread.sleep(1000L)
          throw new RuntimeException
        }
      }

      transformed.get shouldBe res
    }

  }

}
