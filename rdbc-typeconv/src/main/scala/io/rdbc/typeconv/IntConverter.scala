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

package io.rdbc.typeconv

import io.rdbc.sapi
import io.rdbc.sapi.TypeConverter
import io.rdbc.sapi.exceptions.ConversionException

object IntConverter extends TypeConverter[Int] {
  val cls = classOf[Int]

  override def fromAny(any: Any): Int = any match {
    case f: Float if f.isNaN || f.isInfinite => throw new ConversionException(any, cls)
    case d: Double if d.isNaN || d.isInfinite => throw new ConversionException(any, cls)
    case jn: java.lang.Number => jn.intValue()
    case sapi.SqlNumeric.Val(bd) => bd.intValue()
    case _ => throw new ConversionException(any, cls)
  }
}
