/*
 * Copyright 2016-2017 Krzysztof Pado
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

package io.rdbc.sapi

/** General unbounded numeric type that extends [[scala.math.BigDecimal BigDecimal]] to be able to
  * represent NaN, positive infitnity and negative infinity. */
sealed trait SqlNumeric
object SqlNumeric {
  /** Not-a-number */
  case object NaN extends SqlNumeric

  /** Positive infinity */
  case object PosInfinity extends SqlNumeric

  /** Negative infinity */
  case object NegInfinity extends SqlNumeric

  /** Decimal value representable with a [[scala.math.BigDecimal BigDecimal]] */
  case class Val(bigDecimal: BigDecimal) extends SqlNumeric
}
