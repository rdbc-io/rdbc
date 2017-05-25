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

package io.rdbc.sapi

import scala.concurrent.Future

/** Provides access to a database [[Connection]].
  *
  * Implementors must make the implementation of this trait thread-safe.
  *
  * @define withTransaction
  * Executes a function (which can be passed as a code block) in a context
  * of a connection obtained via `connection` method. Before the function is
  * executed, transaction is started. After the function finishes, transaction
  * is committed in case of a success and rolled back in case of a
  * failure - after that, the connection is released.
  *
  * Because managing transaction state requires invoking functions that
  * require specifying a timeout, this function requires an implicit timeout
  * instance.
  *
  * @define withConnection
  * Executes a function (which can be passed as a code block) in a context of
  * a connection obtained via `connection` method, and releases
  * the connection afterwards.
  */
trait ConnectionFactory {

  /** Returns a future of a [[Connection]].
    */
  def connection(): Future[Connection]

  /** Executes a function in a context of a connection.
    *
    * $withConnection
    */
  def withConnection[A](body: Connection => A): Future[A]

  /** Executes a future-returning function in a context of a connection.
    *
    * $withConnection
    */
  def withConnectionF[A](body: Connection => Future[A]): Future[A]

  /** Executes a function in a context of a transaction.
    *
    * $withTransaction
    */
  def withTransaction[A](body: Connection => A)
                        (implicit timeout: Timeout): Future[A]

  /** Executes a future-returning function in a context of a transaction.
    *
    * $withTransaction
    */
  def withTransactionF[A](body: Connection => Future[A])
                         (implicit timeout: Timeout): Future[A]

  /** Shuts down this connection factory.
    *
    * Returned future never fails - it completes on finished shutdown attempt.
    * After the factory is shut down it is illegal to request new connections from it.
    */
  def shutdown(): Future[Unit]
}
