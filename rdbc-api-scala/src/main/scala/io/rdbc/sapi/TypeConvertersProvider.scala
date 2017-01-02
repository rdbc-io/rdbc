package io.rdbc.sapi

import io.rdbc.ImmutSeq

trait TypeConvertersProvider {
  def typeConverters: ImmutSeq[TypeConverter[_]]
}
