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

/** Represents a SQL statement of any type */
trait Statement extends Bindable[ParametrizedStatement] {

  /** A native vendor-specific SQL statement string */
  def nativeSql: String

  /** Streams statement parameters to a database.
    *
    * This method can be used to repeatedly execute this statement with published parameters
    * by leveraging Reactive Streams specification's `Publisher` with a backpressure. Each published element
    * is a map containing all parameters required by this statement.
    *
    * Resulting future can fail with:
    *  - [[io.rdbc.api.exceptions.BindException#MissingParamValException MissingParamValException]] when some parameter value was not provided
    *  - [[io.rdbc.api.exceptions.NoSuitableConverterFoundException NoSuitableConverterFoundException]] when some parameter value's type is not convertible to a database type
    */
  def streamParams(paramsPublisher: Publisher[Map[String, Any]]): Future[Unit]
}
