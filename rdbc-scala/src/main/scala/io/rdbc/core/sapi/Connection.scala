package io.rdbc.core.sapi

import org.reactivestreams.Publisher

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

/**
  * Represents a database connection (session).
  *
  */
trait Connection {

  implicit def ec: ExecutionContext

  /**
    * Starts a database transaction.
    *
    * @param timeout dupa
    */
  def beginTx()(implicit timeout: FiniteDuration): Future[Unit]

  def commitTx()(implicit timeout: FiniteDuration): Future[Unit]

  def rollbackTx()(implicit timeout: FiniteDuration): Future[Unit]

  def release(): Future[Unit]

  def validate(): Future[Boolean]

  def select(sql: String): Future[Select] = statement(sql).map(new Select(_))

  def update(sql: String): Future[Update] = statement(sql).map(new Update(_))

  def insert(sql: String): Future[Insert] = statement(sql).map(new Insert(_))

  def returningInsert(sql: String): Future[ReturningInsert]

  def returningInsert(sql: String, keyColumns: String*): Future[ReturningInsert]

  def delete(sql: String): Future[Delete] = statement(sql).map(new Delete(_))

  def statement(sql: String): Future[Statement]

  def streamIntoTable(sql: String, paramsPublisher: Publisher[Map[String, Any]]): Future[Unit]
}
