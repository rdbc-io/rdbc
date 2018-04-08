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

import io.rdbc.jadapter.ExceptionConverter
import io.rdbc.japi.{exceptions => japi}
import io.rdbc.sapi.{exceptions => sapi}

import scala.compat.java8.OptionConverters._

private[jadapter] class ExceptionConversion(converter: ExceptionConverter) {

  def convertExceptions[A](block: => A): A = {
    try {
      block
    } catch {
      case ex: sapi.RdbcException => throw convertException(ex)
    }
  }

  def convertExceptionsFut[A](block: => CompletionStage[A]): CompletionStage[A] = {
    convertExceptions {
      block.exceptionally(new java.util.function.Function[Throwable, A] {
        def apply(ex: Throwable): Nothing = {
          unwrapFutureFailure(ex) match {
            case ex: sapi.RdbcException => throw convertException(ex)
            case ex => throw ex
          }
        }
      })
    }
  }

  private def unwrapFutureFailure(failure: Throwable): Throwable = {
    if (failure.isInstanceOf[CompletionException] && failure.getCause != null) {
      failure.getCause
    } else {
      failure
    }
  }

  def convertException(sapiEx: sapi.RdbcException): japi.RdbcException = {
    val japiEx = converter
      .orElse(fallbackConverter)
      .applyOrElse(
        sapiEx,
        (_: sapi.RdbcException) => new japi.UncategorizedRdbcException(sapiEx.getMessage, sapiEx)
      )

    japiEx
  }

  private val fallbackConverter: PartialFunction[sapi.RdbcException, japi.RdbcException] = {
    case ex: sapi.AuthFailureException =>
      new japi.AuthFailureException(ex.getMessage, ex)

    case ex: sapi.BeginTxException =>
      new japi.BeginTxException(ex.getMessage, ex)

    case ex: sapi.ColumnIndexOutOfBoundsException =>
      new japi.ColumnIndexOutOfBoundsException(ex.idx, ex.columnCount, ex)

    case ex: sapi.CommitTxException =>
      new japi.CommitTxException(ex.getMessage, ex)

    case ex: sapi.ConnectionClosedException =>
      new japi.ConnectionClosedException(ex.getMessage, ex)

    case ex: sapi.ConnectionReleaseException =>
      new japi.ConnectionReleaseException(ex.getMessage, ex)

    case ex: sapi.ConnectionValidationException =>
      new japi.ConnectionValidationException(ex.getMessage, ex)

    case ex: sapi.ConstraintViolationException =>
      new japi.ConstraintViolationException(ex.schema, ex.table, ex.constraint, ex.getMessage, ex)

    case ex: sapi.ConversionException =>
      new japi.ConversionException(ex.value, ex.targetType, ex)

    case ex: sapi.IllegalSessionStateException =>
      new japi.IllegalSessionStateException(ex.getMessage, ex)

    case ex: sapi.InactiveTxException =>
      new japi.InactiveTxException(ex.getMessage, ex)

    case ex: sapi.InvalidQueryException =>
      new japi.InvalidQueryException(ex.msg, ex.errorPosition.map(i => i: java.lang.Integer).asJava, ex)

    case ex: sapi.MissingColumnException =>
      new japi.MissingColumnException(ex.column, ex)

    case ex: sapi.MissingParamValException =>
      new japi.MissingParamValException(ex.missingParam, ex)

    case ex: sapi.MixedParamTypesException =>
      new japi.MixedParamTypesException(ex)

    case ex: sapi.NoKeysReturnedException =>
      new japi.NoKeysReturnedException(ex.getMessage, ex)

    case ex: sapi.NoSuchParamException =>
      new japi.NoSuchParamException(ex.param, ex)

    case ex: sapi.NoSuitableConverterFoundException =>
      new japi.NoSuitableConverterFoundException(ex.value, ex)

    case ex: sapi.RollbackTxException =>
      new japi.RollbackTxException(ex.getMessage, ex)

    case ex: sapi.TimeoutException =>
      new japi.TimeoutException(ex.getMessage, ex)

    case ex: sapi.TooManyParamsException =>
      new japi.TooManyParamsException(ex.provided, ex.expected, ex)

    case ex: sapi.UnauthorizedException =>
      new japi.UnauthorizedException(ex.getMessage, ex)

    case ex: sapi.UncategorizedRdbcException =>
      new japi.UncategorizedRdbcException(ex.getMessage, ex)

    case ex: sapi.UnsupportedDbTypeException =>
      new japi.UnsupportedDbTypeException(ex.dbTypeDesc, ex)
  }

}
