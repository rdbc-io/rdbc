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

import io.rdbc.api.exceptions.ResultProcessingException.ConversionException
import io.rdbc.sapi.TypeConverter

object BoolConverter extends TypeConverter[Boolean] {
  val cls = classOf[Boolean]

  override def fromAny(any: Any): Boolean = any match {
    case bool: Boolean => bool

    case jn: java.lang.Number =>
      val long = jn.longValue()
      if (long == 1L) true
      else if (long == 0L) false
      else throw ConversionException(jn, classOf[Boolean])

    case char: Char => char2Bool(char)

    case str: String =>
      if (str.length == 1) char2Bool(str.head)
      else throw ConversionException(str, classOf[Boolean])

    case _ => throw ConversionException(any, classOf[Boolean])
  }

  private def char2Bool(char: Char): Boolean = {
    if (char == '1' || char == 'T' || char == 'Y') true
    else if (char == '0' || char == 'F' || char == 'N') false
    else throw ConversionException(char, classOf[Boolean])
  }
}
