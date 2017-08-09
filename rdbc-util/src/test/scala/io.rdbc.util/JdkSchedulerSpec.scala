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

import java.util
import java.util.concurrent.{Executors, ScheduledExecutorService}

import io.rdbc.util.scheduler.JdkScheduler
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class JdkSchedulerSpec
  extends RdbcUtilSpec
    with MockFactory
    with Matchers {

  private implicit val ec = ExecutionContext.global

  "JdkScheduler" should {
    "shutdown underlying executor service" in {
      val es = mock[ScheduledExecutorService]
      (es.shutdownNow _).expects().once().returning(new util.ArrayList[Runnable])

      new JdkScheduler(es).shutdown().get shouldBe (())
    }

    "schedule tasks using underlying executor service" in {
      val delay = 1.second
      val es = Executors.newSingleThreadScheduledExecutor()
      val actionMock = mockFunction[Unit, Unit]

      actionMock.expects(()).once()

      new JdkScheduler(es).schedule(delay) { () =>
        actionMock(())
      }

      Thread.sleep(2000L)
    }

    "not execute tasks before configured delay" in {
      val delay = 5.seconds
      val es = Executors.newSingleThreadScheduledExecutor()
      val actionMock = mockFunction[Unit, Unit]

      actionMock.expects(()).never()

      val task = new JdkScheduler(es).schedule(delay) { () =>
        actionMock(())
      }

      Thread.sleep(1000L)

      task.cancel()
    }
  }
}
