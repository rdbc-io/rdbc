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

import org.reactivestreams.Publisher

import scala.concurrent.Future

/**
  * Represent a SQL statement
  *
  * Methods of this trait allow to bind argument values to parameters
  * either by name or index. Classes extending this trait can be used
  * as an alternative to `sql` string interpolator - [[SqlInterpolator]].
  *
  * @groupname primary Primary methods
  * @groupprio primary 0
  *
  * @groupname fut Future utils
  * @groupprio fut 10
  *
  * @define bindExceptions
  *  - [[io.rdbc.api.exceptions.MissingParamValException MissingParamValException]]
  *  when some parameter value was not provided
  *  - [[io.rdbc.api.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]]
  *  when some parameter value's type is not convertible to a database type
  */
trait Statement {

  /** Binds each parameter by name.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    * insert.map(_.bind("p1" -> "str", "p2" -> 10L)).foreach(_.execute())
    * }}}
    *
    * Throws:
    * $bindExceptions
    *
    * @group primary
    */
  def bind(params: (String, Any)*): ParametrizedStatement

  /** Binds each parameter by name and wraps a result in a Future.
    *
    * This is not an asynchronous operation, it's only an utility method
    * that wraps `bind` result with a [[scala.concurrent.Future Future]]
    * to allow chaining bind operations with asynchronous operations.
    *
    * Example:
    * {{{
    * for {
    *   insert <- conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    *   bound <- insert.bindF("p1" -> "str", "p2" -> 10L)
    *   _ <- bound.execute()
    * } yield ()
    * }}}
    *
    * Resulting future can fail with:
    * $bindExceptions
    *
    * @group fut
    */
  def bindF(params: (String, Any)*): Future[ParametrizedStatement]

  /** Binds each parameter by index.
    *
    * Parameters are ordered, each value in `params` sequence will be bound
    * to the corresponding parameter.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    * insert.map(_.bind("str", 10L)).foreach(_.execute())
    * }}}
    *
    * Throws:
    * $bindExceptions
    *
    * @group primary
    */
  def bindByIdx(params: Any*): ParametrizedStatement

  /** Binds each parameter by index and wraps a result in a Future.
    *
    * Parameters are ordered, each value in `params` sequence will be bound
    * to the corresponding parameter.
    *
    * This is not an asynchronous operation, it's only an utility method
    * that wraps `bindByIdx` result with a [[scala.concurrent.Future Future]]
    * to allow chaining bind operations with asynchronous operations.
    *
    * Example:
    * {{{
    * for {
    *   insert <- conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    *   bound <- insert.bindByIdxF("str", 10L)
    *   _ <- bound.execute()
    * } yield ()
    * }}}
    *
    * Resulting future can fail with:
    * $bindExceptions
    *
    * @group fut
    */
  def bindByIdxF(params: Any*): Future[ParametrizedStatement]

  /** Returns a parametrized version of the bindable object without
    * providing any parameters.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values ('str', 10)")
    * insert.map(_.noParams).foreach(_.execute())
    * }}}
    *
    * @group primary
    */
  def noParams: ParametrizedStatement

  /** Returns a parametrized version of the bindable object without providing
    * any parameters and wraps a result in a future
    *
    * This is not an asynchronous operation, it's only an utility method
    * that wraps `noParams` result with a [[scala.concurrent.Future Future]]
    * to allow chaining bind operations with asynchronous operations.
    *
    * Example:
    * {{{
    * for {
    *   insert <- conn.insert("insert into table(p1, p2) values ('str', 10)")
    *   bound <- insert.noParamsF
    *   _ <- bound.execute()
    * } yield ()
    * }}}
    *
    * @group fut
    */
  def noParamsF: Future[ParametrizedStatement]

  /** Streams statement parameters to a database.
    *
    * This method can be used to repeatedly execute this statement with
    * published parameters by leveraging Reactive Streams specification's
    * `Publisher` with a backpressure. Each published element is a map
    * containing all parameters required by this statement.
    *
    * Resulting future can fail with:
    *  - [[io.rdbc.api.exceptions.MissingParamValException MissingParamValException]]
    * when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]]
    * when some parameter value's type is not convertible to a database type
    */
  def streamParams(paramsPublisher: Publisher[Map[String, Any]]): Future[Unit]

  /** Deallocates database resources related to this statement.
    *
    * This method is safe to call multiple times on the same instance.
    */
  def deallocate(): Future[Unit]
}
