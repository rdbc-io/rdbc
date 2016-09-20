package io.rdbc.api.exceptions

case class ConversionException(any: Any, targetType: Class[_]) extends RdbcException(s"Value '$any' could not be converted to ${targetType.getCanonicalName}")
