package io.rdbc.core.sapi

import scala.concurrent.Future

trait Bindable[T] {
  def bind(params: (String, Any)*): T

  def bindF(params: (String, Any)*): Future[T] = Future.successful(bind(params: _*))

  def bindByIdx(params: Any*): T

  def bindByIdxF(params: Any*): Future[T] = Future.successful(bindByIdx(params: _*))

  def noParams: T
}
