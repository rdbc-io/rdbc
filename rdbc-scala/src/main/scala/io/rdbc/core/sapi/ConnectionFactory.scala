package io.rdbc.core.sapi

import scala.concurrent.Future

trait ConnectionFactory {
  def connection(): Future[Connection]
}
