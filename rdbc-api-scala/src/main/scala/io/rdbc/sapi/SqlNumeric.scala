package io.rdbc.sapi

/** General unbounded numeric type that extends [[BigDecimal]] to be able to represent NaN,
  * positive infitnity and negative infinity. */
sealed trait SqlNumeric
object SqlNumeric {
  /** Not-a-number */
  case object NaN extends SqlNumeric

  /** Positive infinity */
  case object PosInfinity extends SqlNumeric

  /** Negative infinity */
  case object NegInfinity extends SqlNumeric

  /** Decimal value representable with a [[BigDecimal]] */
  case class Val(bigDecimal: BigDecimal) extends SqlNumeric
}
