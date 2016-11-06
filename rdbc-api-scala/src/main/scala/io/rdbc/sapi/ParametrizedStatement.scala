/*
 * Copyright 2016 Krzysztof Pado
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

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

/** Represents a parametized statement.
  *
  * Parametrized statement is a statement that has all parameters provided and is ready to be executed.
  *
  * @define timeoutInfo
  *  After the operation takes longer time than `timeout`, operation will be aborted. Note however, that it may not
  *  be feasible to abort the operation immediately.
  * @define exceptions
  *  Returned future can fail with:
  *  - [[io.rdbc.api.exceptions.UnauthorizedException UnauthorizedException]] when client is not authorized to perform the action
  *  - [[io.rdbc.api.exceptions.InvalidQueryException InvalidQueryException]] when query is rejected by a database engine as invalid
  *  - [[io.rdbc.api.exceptions.InactiveTxException InactiveTxException]] when transaction is in progress but is in inactive state
  *  - [[io.rdbc.api.exceptions.ConstraintViolationException ConstraintViolationException]] when operation results in an integrity constraint violation
  *  - [[io.rdbc.api.exceptions.UncategorizedRdbcException UncategorizedRdbcException]] when a general statement execution error occurs //TODO should UncategorizedRdbcException throwing be even documented?
  */
trait ParametrizedStatement {

  /** Executes this statement and returns a [[ResultStream]] instance that can be used to stream
    * rows from the database leveraging Reactive Streams specification's `Publisher`
    * with backpressure.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeForStream()(implicit timeout: FiniteDuration): Future[ResultStream]

  /** Executes this statement and returns a [[ResultSet]] instance.
    *
    * After execution all resulting rows will be pulled from a database and buffered in the resulting object.
    * If expected result set is very big this may cause out of memory errors.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeForSet()(implicit timeout: FiniteDuration): Future[ResultSet]

  /** Executes this statement ignoring any resulting information.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeIgnoringResult()(implicit timeout: FiniteDuration): Future[Unit]

  /** Executes this statement returning a number of rows that were affected.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeForRowsAffected()(implicit timeout: FiniteDuration): Future[Long]

  /** Executes this statement and returns the first row returned by a database engine.
    *
    * If no rows are found, [[scala.None None]] will be returned.
    *
    * $timeoutInfo
    * $exceptions
    */
  def executeForFirstRow()(implicit timeout: FiniteDuration): Future[Option[Row]]

  /** Executes this statement and returns a single column value from the first row returned by a database engine.
    *
    * If no rows are found, [[scala.None None]] will be returned.
    *
    * If extracted value has SQL `null` value, a [[scala.Some Some]] instance containing a `null` value will be returned.
    *
    * This method is not intended to be used for returning values from columns that can have a SQL `null` value. Use
    * `executeForValueOpt` for such columns.
    *
    * Example:
    * {{{
    * for {
    *   select <- conn.statement("select name from users where id = :id")
    *   parametrized <- statement.bindF(id -> 1)
    *   name <- parametrized.executeForValue(_.str("name"))
    * } yield name
    * }}}
    * $timeoutInfo
    * $exceptions
    *
    * @param valExtractor function used to extract value from the returned row
    */
  def executeForValue[A](valExtractor: Row => A)(implicit timeout: FiniteDuration): Future[Option[A]]

  /** Executes this statement and returns a single column value from the first row returned by a database engine.
    *
    * If no rows are found, [[scala.None None]] will be returned.
    *
    * If row was found but extracted value has SQL `null` value, a [[scala.Some Some]] instance will be returned containing a [[scala.None None]].
    *
    * Example:
    * {{{
    * for {
    *   select <- conn.select("select name from users where id = :id")
    *   parametrized <- select.bindF(id -> 1)
    *   name <- parametrized.executeForValueOpt(_.str("name"))
    * } yield name
    * }}}
    * $timeoutInfo
    * $exceptions
    *
    * @param valExtractor function used to extract value from the returned row
    */
  def executeForValueOpt[A](valExtractor: Row => Option[A])(implicit timeout: FiniteDuration): Future[Option[Option[A]]]
}
