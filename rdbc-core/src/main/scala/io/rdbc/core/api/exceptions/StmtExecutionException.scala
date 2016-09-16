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

package io.rdbc.core.api.exceptions

sealed abstract class StmtExecutionException(msg: String) extends RdbcException(msg)

object StmtExecutionException {

  case class ConnectionBrokenException(msg: String) extends StmtExecutionException(msg)

  case class IllegalSessionStateException(msg: String) extends StmtExecutionException(msg)

  case class UnauthorizedException(msg: String) extends StmtExecutionException(msg)

  case class InvalidQueryException(msg: String, errorPosition: Option[Int]) extends StmtExecutionException(msg)

  object ConstraintViolationException {
    def apply(schema: String, table: String, constraint: String): ConstraintViolationException = ConstraintViolationException(schema, table, constraint, s"Constraint $constraint violation on table $schema.$table")
  }

  case class ConstraintViolationException(schema: String, table: String, constraint: String, msg: String) extends StmtExecutionException(msg)

  object InactiveTxException {
    def apply(): InactiveTxException = InactiveTxException("Current transaction is not active")
  }

  case class InactiveTxException(msg: String) extends StmtExecutionException(msg)

  object UncategorizedExecutionException {
    def apply(msg: String): UncategorizedExecutionException = UncategorizedExecutionException(msg, None)
  }

  case class ExecuteTimeoutException(msg: String) extends StmtExecutionException(msg)

  case class UncategorizedExecutionException(msg: String, detail: Option[String]) extends StmtExecutionException(msg)
}