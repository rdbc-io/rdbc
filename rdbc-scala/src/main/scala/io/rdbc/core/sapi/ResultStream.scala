package io.rdbc.core.sapi

import io.rdbc.core._
import org.reactivestreams.Publisher

import scala.concurrent.Future

trait ResultStream {
  def rowsAffected: Future[Long] //TODO note that this future will never complete if publisher doesn't complete
  def warnings: Future[ImmutSeq[Warning]]
  def metadata: RowMetadata
  def rows: Publisher[Row]
}
