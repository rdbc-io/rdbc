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

package io.rdbc.typeconv

import java.time.{Instant, ZonedDateTime}

import io.rdbc.api.exceptions.ConversionException
import io.rdbc.sapi.TypeConverter

object InstantConverter extends TypeConverter[Instant] {
  val cls = classOf[Instant]

  override def fromAny(any: Any): Instant = any match {
    case i: Instant => i
    case zdt: ZonedDateTime => zdt.toInstant
    case _ => throw new ConversionException(any, classOf[Instant])
  }
}
