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

import java.util.concurrent.TimeUnit

import io.rdbc.japi.{DecimalNumber, Row}
import io.rdbc.sapi.DecimalNumber.{NaN, NegInfinity, PosInfinity, Val}
import io.rdbc.sapi.Timeout
import io.rdbc.{japi, sapi}

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

private[jadapter] object Conversions {

  implicit class SqlNumericToJava(val value: sapi.DecimalNumber) extends AnyVal {
    def asJava: DecimalNumber = {
      value match {
        case NaN => japi.DecimalNumber.NAN
        case NegInfinity => japi.DecimalNumber.NEG_INFINITY
        case PosInfinity => japi.DecimalNumber.POS_INFINITY
        case Val(bigDec) => japi.DecimalNumber.of(bigDec.bigDecimal)
      }
    }
  }

  implicit class JavaDurationToTimeout(val value: java.time.Duration) extends AnyVal {
    def asScala: Timeout = {
      throwOnFailure {
        Try(value.toNanos).map { nanos =>
          Timeout(FiniteDuration(nanos, TimeUnit.NANOSECONDS).toCoarsest)
        }.recover {
          case _: ArithmeticException => Timeout.Inf
        }
      }
    }
  }

  implicit class WarningToJava(val value: sapi.Warning) extends AnyVal {
    def asJava: japi.Warning = {
      japi.Warning.of(value.msg, value.code)
    }
  }

  implicit class ColumnMetadataToJava(val value: sapi.ColumnMetadata) extends AnyVal {
    def asJava: japi.ColumnMetadata = {
      value.cls.map { cls =>
        japi.ColumnMetadata.of(value.name, value.dbTypeId, cls)
      }.getOrElse(japi.ColumnMetadata.of(value.name, value.dbTypeId))
    }
  }

  implicit class RowMetadataToJava(val value: sapi.RowMetadata) extends AnyVal {
    def asJava: japi.RowMetadata = {
      japi.RowMetadata.of(value.columns.map(_.asJava).asJava)
    }
  }

  implicit class ResultSetToJava(val value: sapi.ResultSet) extends AnyVal {
    def asJava(implicit exConversion: ExceptionConversion): japi.ResultSet = {
      japi.ResultSet.of(
        value.rowsAffected,
        value.warnings.map(_.asJava).asJava,
        value.metadata.asJava,
        value.rows.map[Row, Seq[Row]](new RowAdapter(_)).asJava
      )
    }
  }

  implicit class RowToJava(val value: sapi.Row) extends AnyVal {
    def asJava(implicit exConversion: ExceptionConversion): japi.Row = {
      new RowAdapter(value)
    }
  }

  implicit class ConnectionToJava(val value: sapi.Connection) extends AnyVal {
    def asJava(implicit ec: ExecutionContext,
               exConversion: ExceptionConversion): japi.Connection = {
      new ConnectionAdapter(value)
    }
  }

  implicit class StatementToJava(val value: sapi.Statement) extends AnyVal {
    def asJava(implicit ec: ExecutionContext,
               exConversion: ExceptionConversion): japi.Statement = {
      new StatementAdapter(value)
    }
  }

  implicit class StatementOptionsToScala(val value: japi.StatementOptions) extends AnyVal {
    def asScala: sapi.StatementOptions = {
      val keyColumns = value.getGeneratedKeyCols.getType match {
        case japi.KeyColumns.Type.ALL => sapi.KeyColumns.All
        case japi.KeyColumns.Type.NONE => sapi.KeyColumns.None
        case japi.KeyColumns.Type.COLUMNS =>
          sapi.KeyColumns.named(value.getGeneratedKeyCols.getColumns.asScala.toVector: _*)
      }
      sapi.StatementOptions(keyColumns)
    }
  }

  implicit class RowPublisherToJava(val value: sapi.RowPublisher) extends AnyVal {
    def asJava(implicit ec: ExecutionContext,
               exConversion: ExceptionConversion): japi.RowPublisher = {
      new RowPublisherAdapter(value)
    }
  }

  implicit class ExecutableStatementToJava(val value: sapi.ExecutableStatement) extends AnyVal {
    def asJava(implicit ec: ExecutionContext,
               exConversion: ExceptionConversion): japi.ExecutableStatement = {
      new ExecutableStatementAdapter(value)
    }
  }

}
