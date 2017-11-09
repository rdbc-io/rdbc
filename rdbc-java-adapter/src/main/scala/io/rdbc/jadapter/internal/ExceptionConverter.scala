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

import java.util.concurrent.{CompletionException, CompletionStage}
import java.util.function.BiFunction

import io.rdbc.japi.{exceptions => japi}
import io.rdbc.sapi.{exceptions => sapi}

import scala.compat.java8.OptionConverters._

private[jadapter] object ExceptionConverter {

  def convertExceptions[A](block: => A): A = {
    try {
      block
    } catch {
      case ex: sapi.RdbcException => throw convertException(ex)
    }
  }

  def convertExceptionsFut[A](block: => CompletionStage[A]): CompletionStage[A] = {
    convertExceptions {
      block.handle(new BiFunction[A, Throwable, A] {
        def apply(value: A, failure: Throwable): A = {
          if (value != null) {
            value
          } else {
            unwrapFutureFailure(failure) match {
              case ex: sapi.RdbcException => throw convertException(ex)
              case ex => throw ex
            }
          }
        }
      })
    }
  }

  private def unwrapFutureFailure(failure: Throwable): Throwable = {
    if (failure.isInstanceOf[CompletionException]) {
      failure.getCause
    } else {
      failure
    }
  }

  def convertException(sapiEx: sapi.RdbcException): japi.RdbcException = {
    val japiEx = sapiEx match {
      case ex: sapi.AuthFailureException =>
        new japi.AuthFailureException(ex.getMessage, ex.getCause)

      case ex: sapi.BeginTxException =>
        new japi.BeginTxException(ex.getMessage, ex.getCause)

      case ex: sapi.ColumnIndexOutOfBoundsException =>
        new japi.ColumnIndexOutOfBoundsException(ex.idx, ex.columnCount)

      case ex: sapi.CommitTxException =>
        new japi.CommitTxException(ex.getMessage, ex.getCause)

      case ex: sapi.ConnectionClosedException =>
        new japi.ConnectionClosedException(ex.getMessage, ex.getCause)

      case ex: sapi.ConnectionReleaseException =>
        new japi.ConnectionReleaseException(ex.getMessage, ex.getCause)

      case ex: sapi.ConnectionValidationException =>
        new japi.ConnectionValidationException(ex.getMessage, ex.getCause)

      case ex: sapi.ConstraintViolationException =>
        new japi.ConstraintViolationException(ex.schema, ex.table, ex.constraint, ex.getMessage)

      case ex: sapi.ConversionException =>
        new japi.ConversionException(ex.value, ex.targetType, ex.getCause)

      case ex: sapi.IllegalSessionStateException =>
        new japi.IllegalSessionStateException(ex.getMessage, ex.getCause)

      case ex: sapi.InactiveTxException =>
        new japi.InactiveTxException(ex.getMessage)

      case ex: sapi.InvalidQueryException =>
        new japi.InvalidQueryException(ex.msg, ex.errorPosition.map(i => i: java.lang.Integer).asJava)

      case ex: sapi.MissingColumnException =>
        new japi.MissingColumnException(ex.column)

      case ex: sapi.MissingParamValException =>
        new japi.MissingParamValException(ex.missingParam)

      case _: sapi.MixedParamTypesException =>
        new japi.MixedParamTypesException()

      case ex: sapi.NoKeysReturnedException =>
        new japi.NoKeysReturnedException(ex.getMessage, ex.getCause)

      case ex: sapi.NoSuchParamException =>
        new japi.NoSuchParamException(ex.param)

      case ex: sapi.NoSuitableConverterFoundException =>
        new japi.NoSuitableConverterFoundException(ex.value)

      case ex: sapi.RollbackTxException =>
        new japi.RollbackTxException(ex.getMessage, ex.getCause)

      case ex: sapi.TimeoutException =>
        new japi.TimeoutException(ex.getMessage)

      case ex: sapi.TooManyParamsException =>
        new japi.TooManyParamsException(ex.provided, ex.expected)

      case ex: sapi.UnauthorizedException =>
        new japi.UnauthorizedException(ex.getMessage)

      case ex: sapi.UncategorizedRdbcException =>
        new japi.UncategorizedRdbcException(ex.getMessage, ex.getCause)

      case ex: sapi.UnsupportedDbTypeException =>
        new japi.UnsupportedDbTypeException(ex.dbTypeDesc)
    }

    japiEx.setStackTrace(sapiEx.getStackTrace)
    japiEx
  }

}
