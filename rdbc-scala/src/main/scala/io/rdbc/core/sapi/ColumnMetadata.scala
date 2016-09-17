package io.rdbc.core.sapi

case class ColumnMetadata(name: String, dbTypeId: String, cls: Option[Class[_]])
