package io.rdbc.sapi

import io.rdbc.ImmutIndexedSeq

case class SqlAndParams(sql: String, params: ImmutIndexedSeq[Any])
