package io.rdbc.core.sapi

import io.rdbc.core.{CancellingSubscriber, HeadSubscriber}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

trait ParametrizedStatement {
  implicit def ec: ExecutionContext

  def executeForStream()(implicit timeout: FiniteDuration): Future[ResultStream]

  def executeForSet()(implicit timeout: FiniteDuration): Future[ResultSet] = {
    executeForStream().flatMap { resultStream =>
      val subscriber = new HeadSubscriber(None)
      resultStream.rows.subscribe(subscriber)
      for {
        rowsAffected <- resultStream.rowsAffected
        warnings <- resultStream.warnings
        rows <- subscriber.rows
      } yield {
        new ResultSet(
          rowsAffected = rowsAffected,
          warnings = warnings,
          metadata = resultStream.metadata,
          rows = rows
        )
      }
    }
  }

  def executeIgnoringResult()(implicit timeout: FiniteDuration): Future[Unit] = {
    executeForRowsAffected().map(_ => ())
  }

  def executeForRowsAffected()(implicit timeout: FiniteDuration): Future[Long] = {
    executeForStream().flatMap { source =>
      source.rows.subscribe(new CancellingSubscriber[Row])
      source.rowsAffected
    }
  }

  def executeForFirstRow()(implicit timeout: FiniteDuration): Future[Option[Row]] = {
    executeForStream().flatMap { resultStream =>
      val subscriber = new HeadSubscriber(Some(1L))
      resultStream.rows.subscribe(subscriber)
      subscriber.rows.map(_.headOption)
    }
  }

  def executeForValue[A](valExtractor: Row => A)(implicit timeout: FiniteDuration): Future[Option[A]] = {
    executeForValueOpt(row => Some(valExtractor(row)))
  }

  def executeForValueOpt[A](valExtractor: Row => Option[A])(implicit timeout: FiniteDuration): Future[Option[A]] = {
    executeForFirstRow().map(maybeRow => maybeRow.flatMap(valExtractor))
  }
}
