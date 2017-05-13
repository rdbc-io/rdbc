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

package io.rdbc.util

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Futures {

  private val SuccessfulFuture: Future[Any] = Future.successful(())

  implicit class FutureOps[+T](underlying: Future[T]) {

    /** Applies the side-effecting future-returning function to the result of
      * this future, and returns a new future with the result of this future.
      *
      * The chain will continue only after the future returned by side-effecting's
      * function completes. If said future completes with a failure it will be
      * reported to the execution context and won't fail entire chain.
      *
      * @tparam U only used to accept any return type of the given `PartialFunction`
      * @param pf a `PartialFunction` which will be conditionally applied to the outcome of this `Future`
      * @return a `Future` which will be completed with the exact same outcome as
      *         this `Future` but after the `PartialFunction` has been executed.
      */
    def andThenF[U](pf: PartialFunction[Try[T], Future[U]])(implicit ec: ExecutionContext): Future[T] = {
      underlying.transformWith[T] { result =>
        pf.applyOrElse[Try[T], Future[Any]](result, _ => SuccessfulFuture).transform {
          case Success(_) => result
          case Failure(ex) =>
            ec.reportFailure(ex)
            result
        }
      }
    }
  }

}
