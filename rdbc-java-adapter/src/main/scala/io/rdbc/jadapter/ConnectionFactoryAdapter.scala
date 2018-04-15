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

package io.rdbc.jadapter

import java.time.Duration
import java.util.concurrent.CompletionStage

import io.rdbc.japi.util.ThrowingFunction
import io.rdbc.jadapter.internal.Conversions._
import io.rdbc.jadapter.internal.{ExceptionConversion, InfiniteTimeout}
import io.rdbc.util.Preconditions.checkNotNull
import io.rdbc.{japi, sapi}

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext

class ConnectionFactoryAdapter(val underlying: sapi.ConnectionFactory,
                               exceptionConverter: ExceptionConverter)
                              (implicit ec: ExecutionContext)
  extends japi.ConnectionFactory {

  private type ConnLoanFun[T] = ThrowingFunction[japi.Connection, CompletionStage[T]]

  private implicit val exConversion: ExceptionConversion = new ExceptionConversion(exceptionConverter)

  import exConversion._

  def getConnection(timeout: Duration): CompletionStage[japi.Connection] = {
    checkNotNull(timeout)
    convertExceptionsFut {
      underlying.connection()(timeout.asScala).map(_.asJava).toJava
    }
  }

  def getConnection: CompletionStage[japi.Connection] = convertExceptionsFut {
    getConnection(InfiniteTimeout)
  }

  def withConnection[T](body: ConnLoanFun[T]): CompletionStage[T] = {
    checkNotNull(body)
    convertExceptionsFut {
      withConnection(InfiniteTimeout, body)
    }
  }

  def withConnection[T](timeout: Duration, body: ConnLoanFun[T]): CompletionStage[T] = {
    checkNotNull(timeout)
    checkNotNull(body)
    convertExceptionsFut {
      underlying.withConnection { sapiConn =>
        body.apply(sapiConn.asJava).toScala
      }(timeout.asScala).toJava
    }
  }

  def withTransaction[T](body: ConnLoanFun[T]): CompletionStage[T] = {
    checkNotNull(body)
    convertExceptionsFut {
      withConnection(InfiniteTimeout, body)
    }
  }

  def withTransaction[T](timeout: Duration, body: ConnLoanFun[T]): CompletionStage[T] = {
    checkNotNull(timeout)
    checkNotNull(body)
    convertExceptionsFut {
      underlying.withTransaction { sapiConn =>
        body.apply(sapiConn.asJava).toScala
      }(timeout.asScala).toJava
    }
  }

  def shutdown(): CompletionStage[Void] = convertExceptionsFut {
    underlying.shutdown().map[Void](_ => null).toJava
  }

  override def toString: String = underlying.toString
}
