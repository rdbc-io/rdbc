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

/** Represents a database connection (session).
  *
  * Instances of implementations of this trait can be obtained using a
  * [[ConnectionFactory]]. When clients are done with the connection, they are
  * required to call a `release` method co clean up resources such as open sockets.
  *
  * Invoking any method of this trait when any previous operation has not
  * completed yet is not allowed. Operation is considered complete when a resulting
  * [[scala.concurrent.Future Future]] completes.
  *
  * Transaction management has to be done using `beginTx`, `commitTx` and
  * `rollbackTx` methods. Using SQL statements to manage transaction state is
  * not allowed.
  *
  * [[SqlWithParams]] instances passed to `Connection`'s methods can be created
  * using `sql` string interpolator, for example:
  * {{{
  *   import io.rdbc.sapi._
  *
  *   val conn: Connection = ???
  *   val login = "jdoe"
  *   conn.statement(sql"select * from users where login = \$login").executeForStream()
  * }}}
  *
  * Alternatively, when bare Strings are used as SQL statements, parameters
  * are specified by name. Parameter name is an alphanumeric string starting
  * with a letter, prefixed with a colon. Example:
  *
  * {{{
  *   import io.rdbc.sapi._
  *
  *   val conn: Connection = ???
  *   val login = "jdoe"
  *   conn.statement(sql"select * from users where login = :login")
  *       .bind("login" -> login)
  *       .executable.executeForStream()
  * }}}
  *
  * @groupname tx Transaction management
  * @groupprio tx 10
  * @groupname stmtInter Statement producers (string interpolation)
  * @groupprio stmtInter 20
  * @groupname stmtBare Statement producers (bare strings)
  * @groupprio stmtBare 30
  * @define timeoutInfo
  *  After the operation takes longer time than `timeout`, operation will be
  *  aborted. Note however, that it may not be feasible to abort the operation
  *  immediately.
  * @define statementExceptions
  * Throws:
  *  - [[io.rdbc.api.exceptions.MixedParamTypesException MixedParamTypesException]]
  *  when statement uses both positional and named parameters
  *  - [[io.rdbc.api.exceptions.UncategorizedRdbcException UncategorizedRdbcException]]
  *  when general error occurs
  * @define timeoutException
  *  - [[io.rdbc.api.exceptions.TimeoutException TimeoutException]]
  *  when maximum operation time has been exceeded
  * @define bindExceptions
  *  - [[io.rdbc.api.exceptions.MissingParamValException MissingParamValException]]
  *  when some parameter value was not provided
  *  - [[io.rdbc.api.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]]
  *  when some parameter value's type is not convertible to a database type
  * @define statementParametrization
  *  For syntax of statement parametrization see a [[Connection]] documentation.
  * @define interpolatorExample
  *  [[SqlWithParams]] parameter instance is meant to be constructed using `sql`
  *  string interpolator, for example:
  *  {{{
  *      import io.rdbc.sapi.Interpolators._
  *      val x = 1
  *      val y = 10
  *      val stmt = conn.statement(sql"select * from table where colx > \$x and coly < \$y")
  *  }}}
  * @define withTransaction
  * Executes a function (which can be passed as a code block) in a context
  * of a transaction. Before the function is executed, transaction is started.
  * After the function finishes, transaction is committed in case of a success
  * and rolled back in case of a failure.
  *
  * Because managing transaction state requires invoking functions that
  * require specifying a timeout, this function requires an implicit timeout
  * instance.
  */
trait Connection {

  /** Begins a database transaction.
    *
    * Using this method is a preferred way of starting a transaction, using SQL
    * statements to manage transaction state may lead to undefined behavior.
    *
    * $timeoutInfo
    *
    * Returned future can fail with:
    *  - [[io.rdbc.api.exceptions.BeginTxException BeginTxException]]
    * when general error occurs
    * $timeoutException
    *
    * @group tx
    */
  def beginTx()(implicit timeout: Timeout): Future[Unit]

  /** Commits a database transaction.
    *
    * Using this method is a preferred way of committing a transaction, using
    * SQL statements to manage transaction state may lead to undefined behavior.
    *
    * $timeoutInfo
    *
    * Returned future can fail with:
    *  - [[io.rdbc.api.exceptions.BeginTxException CommmitTxException]]
    * when general error occurs
    * $timeoutException
    *
    * @group tx
    */
  def commitTx()(implicit timeout: Timeout): Future[Unit]

  /** Rolls back a database transaction.
    *
    * Using this method is a preferred way of rolling back a transaction, using
    * SQL statements to manage transaction state may lead to undefined behavior.
    *
    * $timeoutInfo
    *
    * Returned future can fail with:
    *  - [[io.rdbc.api.exceptions.BeginTxException RollbackTxException]]
    * when general error occurs
    * $timeoutException
    *
    * @group tx
    */
  def rollbackTx()(implicit timeout: Timeout): Future[Unit]

  /** Executes a function in a context of a transaction.
    *
    * $withTransaction
    */
  def withTransaction[A](body: => Future[A])
                        (implicit timeout: Timeout): Future[A]

  /** Releases the connection and underlying resources.
    *
    * Only idle connections can be released using this method. To forcibly
    * release the connection use [[forceRelease]] method.
    *
    * After calling this method no future operations on the instance are allowed.
    *
    * Returned future can fail with:
    *  - [[io.rdbc.api.exceptions.ConnectionReleaseException ConnectionReleaseException]]
    * when general error occurs
    */
  def release(): Future[Unit]

  /** Releases the connection and underlying resources regardless of whether
    * the connection is currently in use or not.
    *
    * After calling this method no future operations on the instance are allowed.
    *
    * Returned future can fail with:
    *  - [[io.rdbc.api.exceptions.ConnectionReleaseException ConnectionReleaseException]]
    * when general error occurs
    */
  def forceRelease(): Future[Unit]

  /** Checks whether the connection is still usable.
    *
    * If checking takes longer than `timeout`, connection is considered unusable.
    *
    * @return Future of `true` iff connection is usable, `false` otherwise
    */
  def validate()(implicit timeout: Timeout): Future[Boolean]

  /** Returns a [[Statement]] instance bound to this connection that
    * represents any SQL statement.
    *
    * $statementParametrization
    *
    * $statementExceptions
    *
    * @group stmtBare
    */
  def statement(sql: String, statementOptions: StatementOptions): Statement

  /** Returns a [[Statement]] instance bound to this connection that
    * represents any SQL statement.
    *
    * $statementParametrization
    *
    * $statementExceptions
    *
    * @group stmtBare
    */
  def statement(sql: String): Statement

  /** Returns a [[ExecutableStatement]] instance bound to this connection
    * that represents any parametrized SQL statement.
    *
    * It's a shortcut for calling `statement` and then `bind`.
    *
    * $interpolatorExample
    *
    * $statementExceptions
    * $bindExceptions
    *
    * @group stmtInter
    */
  def statement(sqlWithParams: SqlWithParams, statementOptions: StatementOptions): ExecutableStatement

  /** Returns a [[ExecutableStatement]] instance bound to this connection
    * that represents any parametrized SQL statement.
    *
    * It's a shortcut for calling `statement` and then `bind`.
    *
    * $interpolatorExample
    *
    * $statementExceptions
    * $bindExceptions
    *
    * @group stmtInter
    */
  def statement(sqlWithParams: SqlWithParams): ExecutableStatement

  /** Returns a future that is complete when this connection is idle and ready
    * for accepting queries. */
  def watchForIdle: Future[this.type]
}
