package io.rdbc.api.exceptions

case class NoSuitableConverterFoundException(any: Any)
  extends RdbcException(s"No suitable converter was found for value '$any' of type ${any.getClass}")
