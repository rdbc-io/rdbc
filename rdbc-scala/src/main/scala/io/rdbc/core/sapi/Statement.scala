package io.rdbc.core.sapi

import org.reactivestreams.Publisher

import scala.concurrent.Future

trait Statement extends Bindable[ParametrizedStatement] {
  def nativeSql: String

  def streamParams(paramsPublisher: Publisher[Map[String, Any]]): Future[Unit]
}
