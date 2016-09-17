package io.rdbc.core.sapi

import io.rdbc.core._

case class RowMetadata(columns: ImmutIndexedSeq[ColumnMetadata])
