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

package io.rdbc.sapi

import scala.concurrent.Future
import scala.reflect.ClassTag

/** Represents an executable statement.
  *
  * Executable statement is a statement that has all parameters
  * provided and is ready to be executed.
  *
  * @define timeoutInfo
  *  After the operation takes longer time than `timeout`, operation
  *  will be aborted. Note however, that it may not be feasible
  *  to abort the operation immediately.
  * @define exceptions
  *  Returned future can fail with:
  *  - [[io.rdbc.sapi.exceptions.UnauthorizedException UnauthorizedException]]
  *  when client is not authorized to perform the action
  *  - [[io.rdbc.sapi.exceptions.InvalidQueryException InvalidQueryException]]
  *  when query is rejected by a database engine as invalid
  *  - [[io.rdbc.sapi.exceptions.InactiveTxException InactiveTxException]]
  *  when transaction is in progress but is in inactive state
  *  - [[io.rdbc.sapi.exceptions.ConstraintViolationException ConstraintViolationException]]
  *  when operation results in an integrity constraint violation
  */
trait ExecutableStatement {

  /** Returns a [[RowPublisher]] instance that can be used to stream rows from
    * the database leveraging Reactive Streams specification's `Publisher` with
    * backpressure.
    *
    * The statement is not executed until the stream is subscribed to.
    *
    * $timeoutInfo
    *
    * This method doesn't throw exceptions on failures. Failures are reported
    * by the returned publisher.
    */
  def stream()(implicit timeout: Timeout): RowPublisher

  /** Executes this statement and returns a [[ResultSet]] instance.
    *
    * After execution all resulting rows will be pulled from a database
    * and buffered in the resulting object.  If expected result set is
    * very big this may cause out of memory errors.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeForSet()(implicit timeout: Timeout): Future[ResultSet]

  /** Executes this statement ignoring any resulting information.
    *
    * $timeoutInfo
    * $exceptions
    */
  def execute()(implicit timeout: Timeout): Future[Unit]

  /** Executes this statement returning a number of rows that were affected.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeForRowsAffected()(implicit timeout: Timeout): Future[Long]

  /** Executes this statement and returns the first row returned by a database engine.
    *
    * If no rows are found, [[scala.None None]] will be returned.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeForFirstRow()(implicit timeout: Timeout): Future[Option[Row]]

  /** Executes this statement and returns a single column value from the
    * first row returned by a database engine.
    *
    * If no rows are found, [[scala.None None]] will be returned.
    *
    * Example:
    * {{{
    * for {
    *   val id = 10
    *   select <- conn.statement(sql"select name from users where id = \$id")
    *   name <- select.executeForValue(_.str("name"))
    * } yield name
    * }}}
    * $timeoutInfo
    * $exceptions
    *
    * @param valExtractor function used to extract value from the returned row
    */
  def executeForValue[A](valExtractor: Row => A)
                        (implicit timeout: Timeout): Future[Option[A]]

  /** Executes this statement and returns a single generated key.
    *
    * $timeoutInfo
    * $exceptions
    *  - [[io.rdbc.sapi.exceptions.NoKeysReturnedException NoKeysReturnedException]]
    *  when no keys were returned by the database engine
    *  - [[io.rdbc.sapi.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]]
    *  when no suitable converter was found to transform key value to desired class instance
    *
    * @tparam K type of the returned key
    *
    * @usecase def executeForKey[K: ClassTag]()(implicit timeout: Timeout): Future[K]
    *  @inheritdoc
    */
  def executeForKey[K: ClassTag]()(implicit timeout: Timeout): Future[K]

}
