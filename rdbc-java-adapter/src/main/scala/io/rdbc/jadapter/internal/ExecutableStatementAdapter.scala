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
import io.rdbc.util.Preconditions.checkNotNull

import scala.compat.java8.FutureConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

private[jadapter] class ExecutableStatementAdapter(val underlying: sapi.ExecutableStatement)
                                                  (implicit ec: ExecutionContext,
                                                   exConversion: ExceptionConversion)
  extends ExecutableStatement {

  import exConversion._

  def stream(timeout: Duration): RowPublisher = {
    checkNotNull(timeout)
    convertExceptions {
      underlying.stream()(timeout.asScala).asJava
    }
  }

  def stream(): RowPublisher = stream(InfiniteTimeout)

  def executeForSet(timeout: Duration): CompletionStage[ResultSet] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.executeForSet()(timeout.asScala)
        .map(_.asJava).toJava
    }
  }

  def executeForSet(): CompletionStage[ResultSet] = executeForSet(InfiniteTimeout)

  def execute(timeout: Duration): CompletionStage[Void] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.execute()(timeout.asScala)
        .map[Void](_ => null).toJava
    }
  }

  def execute(): CompletionStage[Void] = execute(InfiniteTimeout)

  def executeForRowsAffected(timeout: Duration): CompletionStage[java.lang.Long] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.executeForRowsAffected()(timeout.asScala)
        .map[java.lang.Long](identity(_)).toJava
    }
  }

  def executeForRowsAffected(): CompletionStage[lang.Long] = executeForRowsAffected(InfiniteTimeout)

  def executeForFirstRow(timeout: Duration): CompletionStage[Optional[Row]] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.executeForFirstRow()(timeout.asScala)
        .map(_.map(_.asJava).asJava).toJava
    }
  }

  def executeForFirstRow(): CompletionStage[Optional[Row]] = executeForFirstRow(InfiniteTimeout)

  def executeForValue[A](valExtractor: ThrowingFunction[Row, A],
                         timeout: Duration): CompletionStage[Optional[A]] = {
    checkNotNull(valExtractor)
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.executeForValue { sapiRow =>
        valExtractor.apply(new RowAdapter(sapiRow))
      }(timeout.asScala).map(_.asJava).toJava
    }
  }

  def executeForValue[T](valExtractor: ThrowingFunction[Row, T]): CompletionStage[Optional[T]] = {
    executeForValue(valExtractor, InfiniteTimeout)
  }

  def executeForKey[T](keyType: Class[T], timeout: Duration): CompletionStage[T] = {
    checkNotNull(keyType)
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.executeForKey()(ClassTag(keyType), timeout.asScala).toJava
    }
  }

  def executeForKey[T](keyType: Class[T]): CompletionStage[T] = {
    executeForKey(keyType, InfiniteTimeout)
  }

  override def toString: String = underlying.toString
}
