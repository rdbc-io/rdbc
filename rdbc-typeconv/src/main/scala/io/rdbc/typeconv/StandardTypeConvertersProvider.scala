package io.rdbc.typeconv

import io.rdbc.sapi.TypeConvertersProvider

class StandardTypeConvertersProvider extends TypeConvertersProvider {
  val typeConverters = {
    Vector(
      BigDecimalConverter,
      BoolConverter,
      ByteConverter,
      CharConverter,
      DoubleConverter,
      FloatConverter,
      InstantConverter,
      IntConverter,
      LocalDateConverter,
      LocalDateTimeConverter,
      LocalTimeConverter,
      LongConverter,
      NumericConverter,
      ShortConverter,
      StringConverter,
      UuidConverter
    )
  }
}
