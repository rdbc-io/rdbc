package io.rdbc.core.sapi

trait TypeConverter[T] {
  def cls: Class[T]

  def fromAny(any: Any): T
}
