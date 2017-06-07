/*
 * Copyright 2016-2017 Krzysztof Pado
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

package io.rdbc.util.scheduler

import java.util.concurrent.ScheduledExecutorService

import io.rdbc.util.Logging

import scala.concurrent.duration.FiniteDuration

class JdkScheduler(executorService: ScheduledExecutorService)
  extends TaskScheduler
    with Logging {

  def schedule(delay: FiniteDuration)(action: () => Unit): ScheduledTask = traced {
    logger.debug(s"Scheduling a task to run in ${delay.length} ${delay.unit} using $executorService")
    val fut = executorService.schedule(runnable(action), delay.length, delay.unit)
    new JdkScheduledTask(fut)
  }

  def shutdown(): Unit = {
    executorService.shutdownNow()
    ()
  }

  /* Scala 2.11 compat */
  private def runnable(action: () => Unit): Runnable = {
    new Runnable() {
      def run(): Unit = action()
    }
  }
}
