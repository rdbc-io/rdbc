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

package io.rdbc.core.typeconv

import io.rdbc.core.api.exceptions.ResultProcessingException.ConversionException
import io.rdbc.core.sapi.TypeConverter

object DoubleConverter extends TypeConverter[Double] {
  val cls = classOf[Double]

  override def fromAny(any: Any): Double = any match {
    case jn: java.lang.Number => jn.doubleValue()
    case _ => throw ConversionException(any, classOf[Double])
  }
}