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

package io.rdbc.implbase

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import io.rdbc.sapi.Timeout

class CompatSpec extends RdbcImplbaseSpec {

  implicit private val timeout = Timeout.Inf

  "FutureCompat.transformWith" should {
    "transform a successful future" in {

      import Compat._

      implicit val ec = ExecutionContext.global

      val target = "10"
      val future = Future.successful(10)
      val transformed = future.transformWith {
        case Success(_) => Future.successful(target)
        case Failure(ex) => Future.failed(ex)
      }

      transformed.get shouldBe target
    }

    "transform a failed future" in {

      import Compat._

      implicit val ec = ExecutionContext.global

      val target = "10"
      val future = Future.failed(new RuntimeException)
      val transformed = future.transformWith {
        case Failure(ex) => Future.successful(target)
        case Success(_) => Future.failed(new RuntimeException)
      }

      transformed.get shouldBe target
    }
  }

  "FutureObjectCompat" should {
    "provide Future.unit" in {

      import Compat._

      implicit val ec = ExecutionContext.global

      val unit = Future.unit
      unit shouldBe 'completed
      unit.foreach { unitVal =>
        unitVal shouldBe (())
      }
    }
  }

}
