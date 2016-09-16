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

import scala.concurrent.duration.FiniteDuration

sealed abstract class ParseException(msg: String) extends RdbcException(msg: String)

object ParseException {

  case class SyntaxErrorException(msg: String, errorPosition: Option[Int]) extends ParseException(msg)

  case class UncategorizedParseException(msg: String, detail: Option[String]) extends ParseException(msg)

  case class ParseTimeoutException(duration: FiniteDuration) extends ParseException(s"Timeout occurred after waiting for configured time of $duration")

}