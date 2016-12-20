package io.rdbc.sapi

/** General unbounded numeric type that extends [[BigDecimal]] to be able to represent NaN,
  * positive infitnity and negative infinity. */
sealed trait Numeric
object Numeric {
  /** Not-a-number */
  case object NaN extends Numeric

  /** Positive infinity */
  case object PosInfinity extends Numeric

  /** Negative infinity */
  case object NegInfinity extends Numeric

  /** Decimal value representable with a [[BigDecimal]] */
  case class Val(bigDecimal: BigDecimal) extends Numeric
}
