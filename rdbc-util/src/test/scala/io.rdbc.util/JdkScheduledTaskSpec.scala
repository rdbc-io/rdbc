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

import java.util.concurrent.ScheduledFuture

import io.rdbc.util.scheduler.JdkScheduledTask
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers

class JdkScheduledTaskSpec
  extends RdbcUtilSpec
    with MockFactory
    with Matchers {

  "JdkScheduledTask" should {
    "return done status consistent with its underlying future" when {
      "underlying future is done" in {
        val underlying = mock[ScheduledFuture[Unit]]

        (underlying.isDone _).expects().returning(true)

        val task = new JdkScheduledTask(underlying)
        task.done shouldBe true
      }

      "underlying future is not done" in {
        val underlying = mock[ScheduledFuture[Unit]]

        (underlying.isDone _).expects().returning(false)

        val task = new JdkScheduledTask(underlying)
        task.done shouldBe false
      }
    }

    "cancel underlying future without interrupting it" in {
      val underlying = mock[ScheduledFuture[Unit]]

      (underlying.cancel _).expects(false).returning(true)

      new JdkScheduledTask(underlying).cancel()
    }
  }
}
