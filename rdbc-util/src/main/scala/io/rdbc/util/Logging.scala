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

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, ThreadFactory}

import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


object Logging {
  private val reqCounter = new AtomicInteger(0)
  @volatile private lazy val tracingEc = {
    ExecutionContext.fromExecutorService(Executors.newCachedThreadPool(new ThreadFactory {
      def newThread(r: Runnable): Thread = {
        val thread = Executors.defaultThreadFactory().newThread(r)
        thread.setDaemon(true)
        thread
      }
    }))
  }
}

trait Logging extends StrictLogging {

  import Logging._

  protected def traced[A](body: => Future[A])(
    implicit enclosing: sourcecode.Enclosing, args: sourcecode.Args): Future[A] = {
    if (logger.underlying.isTraceEnabled) {
      val reqId = newReqId()
      val resultFuture = doTrace(reqId, body)
      resultFuture.andThen { case futureValue =>
        logger.trace(s"[$reqId] Future returned by ${enclosing.value} completed with value '$futureValue'")
      }(tracingEc)
    } else {
      body
    }
  }

  protected def traced[A](body: => A)(implicit enclosing: sourcecode.Enclosing, args: sourcecode.Args): A = {
    if (logger.underlying.isTraceEnabled) {
      doTrace(newReqId(), body)
    } else {
      body
    }
  }

  private def doTrace[A](reqId: String, body: => A)(
                         implicit enclosing: sourcecode.Enclosing, args: sourcecode.Args): A = {
      try {
        logger.trace(s"[$reqId] Entering ${enclosing.value}${formatArgs(args)}")
        val result = body
        logger.trace(s"[$reqId] Exiting ${enclosing.value} returning '$result'")
        result
      } catch {
        case NonFatal(ex) =>
          logger.trace(s"[$reqId] Exiting ${enclosing.value} with exception '$ex'")
          throw ex
      }
  }

  private def newReqId(): String = {
    reqCounter.incrementAndGet().toString
  }

  private def formatArgs(args: sourcecode.Args): String = {
    args.value.flatten.map { arg =>
      s"${arg.source}=${arg.value}"
    }.mkString("(", ",", ")")
  }
}
