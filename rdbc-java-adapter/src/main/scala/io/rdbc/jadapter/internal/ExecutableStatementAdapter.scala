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

package io.rdbc.jadapter.internal

import java.lang
import java.time.Duration
import java.util.Optional
import java.util.concurrent.CompletionStage

import io.rdbc.japi._
import io.rdbc.japi.util.ThrowingFunction
import io.rdbc.jadapter.internal.Conversions._
import io.rdbc.sapi

import scala.compat.java8.FutureConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.ExecutionContext

private[jadapter] class ExecutableStatementAdapter(underlying: sapi.ExecutableStatement)
                                                  (implicit ec: ExecutionContext,
                                                   exConversion: ExceptionConversion)
  extends ExecutableStatement {

  import exConversion._

  def stream(timeout: Duration): RowPublisher = convertExceptions {
    underlying.stream()(timeout.asScala).asJava
  }

  def stream(): RowPublisher = stream(InfiniteDuration)

  def executeForSet(timeout: Duration): CompletionStage[ResultSet] = convertExceptionsFut {
    underlying.executeForSet()(timeout.asScala)
      .map(_.asJava).toJava
  }

  def executeForSet(): CompletionStage[ResultSet] = executeForSet(InfiniteDuration)

  def execute(timeout: Duration): CompletionStage[Void] = convertExceptionsFut {
    underlying.execute()(timeout.asScala)
      .map[Void](_ => null).toJava
  }

  def execute(): CompletionStage[Void] = execute(InfiniteDuration)

  def executeForRowsAffected(timeout: Duration): CompletionStage[java.lang.Long] = convertExceptionsFut {
    underlying.executeForRowsAffected()(timeout.asScala)
      .map[java.lang.Long](identity(_)).toJava
  }

  def executeForRowsAffected(): CompletionStage[lang.Long] = executeForRowsAffected(InfiniteDuration)

  def executeForFirstRow(timeout: Duration): CompletionStage[Optional[Row]] = convertExceptionsFut {
    underlying.executeForFirstRow()(timeout.asScala)
      .map(_.map(_.asJava).asJava).toJava
  }

  def executeForFirstRow(): CompletionStage[Optional[Row]] = executeForFirstRow(InfiniteDuration)

  def executeForValue[A](valExtractor: ThrowingFunction[Row, A],
                         timeout: Duration): CompletionStage[Optional[A]] = convertExceptionsFut {
    underlying.executeForValue { sapiRow =>
      valExtractor.apply(new RowAdapter(sapiRow))
    }(timeout.asScala).map(_.asJava).toJava
  }

  def executeForValue[T](valExtractor: ThrowingFunction[Row, T]): CompletionStage[Optional[T]] = {
    executeForValue(valExtractor, InfiniteDuration)
  }

  override def toString: String = underlying.toString
}
