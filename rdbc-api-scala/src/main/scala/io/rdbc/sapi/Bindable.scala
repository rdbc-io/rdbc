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

import scala.concurrent.Future
import scala.util.Try

/** A trait for objects (typically statements) that can be parametrized.
  *
  * Methods of this trait allow to bind argument values to parameters either by name or index.
  *
  * @tparam T a type of a parametrized object returned after the binding
  */
trait Bindable[T] {

  /** Binds each parameter by name.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    * insert.map(_.bind("p1" -> "str", "p2" -> 10L)).foreach(_.execute())
    * }}}
    *
    * Exceptions thrown:
    *  - [[io.rdbc.api.exceptions.BindException#MissingParamValException MissingParamValException]] when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.BindException#NoSuitableConverterFoundException NoSuitableConverterFoundException]] when some parameter value's type is not convertible to a database type
    */
  def bind(params: (String, Any)*): T

  /** Binds each parameter by name and wraps a result in a Future.
    *
    * This is not an asynchronous operation, it's only an utility method that wraps `bind` result with a [[scala.concurrent.Future Future]]
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
    *  - [[io.rdbc.api.exceptions.BindException#MissingParamValException MissingParamValException]] when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.BindException#NoSuitableConverterFoundException NoSuitableConverterFoundException]] when some parameter value's type is not convertible to a database type
    */
  def bindF(params: (String, Any)*): Future[T] = Future.fromTry(Try(bind(params: _*)))

  /** Binds each parameter by index.
    *
    * Parameters are ordered, each value in `params` sequence will be bound to the corresponding parameter.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values (:p1, :p2)")
    * insert.map(_.bind("str", 10L)).foreach(_.execute())
    * }}}
    *
    * Exceptions thrown:
    *  - [[io.rdbc.api.exceptions.BindException#MissingParamValException MissingParamValException]] when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.BindException#NoSuitableConverterFoundException NoSuitableConverterFoundException]] when some parameter value's type is not convertible to a database type
    */
  def bindByIdx(params: Any*): T

  /** Binds each parameter by index and wraps a result in a Future.
    *
    * Parameters are ordered, each value in `params` sequence will be bound to the corresponding parameter.
    *
    * This is not an asynchronous operation, it's only an utility method that wraps `bindByIdx` result with a [[scala.concurrent.Future Future]]
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
    *  - [[io.rdbc.api.exceptions.BindException#MissingParamValException MissingParamValException]] when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.BindException#NoSuitableConverterFoundException NoSuitableConverterFoundException]] when some parameter value's type is not convertible to a database type
    */
  def bindByIdxF(params: Any*): Future[T] = Future.fromTry(Try(bindByIdx(params: _*)))

  /** Returns a parametrized version of the bindable object without providing any parameters.
    *
    * Example:
    * {{{
    * val insert = conn.insert("insert into table(p1, p2) values ('str', 10)")
    * insert.map(_.noParams).foreach(_.execute())
    * }}}
    * */
  def noParams: T

  /** Returns a parametrized version of the bindable object without providing any parameters and wraps a result in a future
    *
    * This is not an asynchronous operation, it's only an utility method that wraps `noParams` result with a [[scala.concurrent.Future Future]]
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
    */
  def noParamsF: Future[T] = Future.fromTry(Try(noParams))
}
