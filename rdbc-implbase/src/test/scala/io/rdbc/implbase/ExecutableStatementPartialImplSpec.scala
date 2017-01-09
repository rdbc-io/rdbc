/*
 * Copyright 2016 rdbc contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.implbase

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import io.rdbc.ImmutSeq
import io.rdbc.sapi.exceptions.{ColumnIndexOutOfBoundsException, NoKeysReturnedException}
import io.rdbc.sapi.{Row, RowMetadata, RowPublisher, Timeout, Warning}
import org.reactivestreams.Subscriber
import org.scalamock.scalatest.MockFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class ExecutableStatementPartialImplSpec
  extends RdbcImplbaseSpec
    with MockFactory {

  private implicit val actorSystem: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val timeout: Timeout = Timeout.Inf
  private val publisherFailure = new RuntimeException

  "ExecutableStatementPartialImpl" when {

    "executed for the first row" should {
      "return first row if there are rows returned" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        new TestStmt(rows).executeForFirstRow().get shouldBe Some(rows.head)
      }

      "return None if there are no rows returned" in {
        new TestStmt(Vector.empty).executeForFirstRow().get shouldBe empty
      }

      "fail if source fails" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        the[RuntimeException] thrownBy {
          new TestStmt(rows, failOn = Some(0)).executeForFirstRow().get
        }.shouldBe(theSameInstanceAs(publisherFailure))
      }
    }

    "executed for value should" should {
      "return first row's value if there are rows returned" in {
        val firstRow = mock[Row]
        val firstRowVal = "val"
        val valIndex = 0
        (firstRow.str(_: Int)).expects(valIndex).returning(firstRowVal)
        val rows = Vector(firstRow, mock[Row], mock[Row])

        new TestStmt(rows).executeForValue(_.str(valIndex)).get shouldBe Some(firstRowVal)
      }

      "return None if there are no rows returned" in {
        new TestStmt(Vector.empty).executeForValue(_.str(0)).get shouldBe empty
      }

      "fail if source fails before the first element" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        the[RuntimeException] thrownBy {
          new TestStmt(rows, failOn = Some(0)).executeForValue(_.str(0)).get
        }.shouldBe(theSameInstanceAs(publisherFailure))
      }
    }

    "executed for key" should {
      "return first row's value if there are rows returned" in {
        val firstRow = mock[Row]
        val firstRowVal = 0
        (firstRow.colOpt[Int](_: Int)(_: ClassTag[Int])).expects(0, ClassTag.Int).returning(Some(firstRowVal))
        val rows = Vector(firstRow, mock[Row], mock[Row])

        new TestStmt(rows).executeForKey[Int]().get shouldBe firstRowVal
      }

      "fail with NoKeysReturnedException if there are no rows" in {
        assertThrows[NoKeysReturnedException] {
          new TestStmt(Vector.empty).executeForKey[String]().get
        }
      }

      "fail with NoKeysReturnedException if row has no values" in {
        val firstRow = mock[Row]
        (firstRow.colOpt[Int](_: Int)(_: ClassTag[Int]))
          .expects(0, ClassTag.Int)
          .throwing(new ColumnIndexOutOfBoundsException(0, 0))
        val rows = Vector(firstRow, mock[Row], mock[Row])

        assertThrows[NoKeysReturnedException] {
          new TestStmt(rows).executeForKey[Int]().get
        }
      }

      "fail with NoKeysReturnedException if row has a null value" in {
        val firstRow = mock[Row]
        (firstRow.colOpt[Int](_: Int)(_: ClassTag[Int]))
          .expects(0, ClassTag.Int)
          .returning(None)
        val rows = Vector(firstRow, mock[Row], mock[Row])

        assertThrows[NoKeysReturnedException] {
          new TestStmt(rows).executeForKey[Int]().get
        }
      }

      "fail if source fails before the first element" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        the[RuntimeException] thrownBy {
          new TestStmt(rows, failOn = Some(0)).executeForKey[String]().get
        }.shouldBe(theSameInstanceAs(publisherFailure))
      }
    }

    "executed for set" should {
      "return all rows" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        val set = new TestStmt(rows).executeForSet().get
        set.rows shouldBe rows
      }

      "fail if source fails" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        the[RuntimeException] thrownBy {
          new TestStmt(rows, failOn = Some(0)).executeForSet().get
        }.shouldBe(theSameInstanceAs(publisherFailure))
      }

      "return rows affected" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])
        new TestStmt(rows).executeForSet().get.rowsAffected shouldBe rows.size.toLong
      }

      "return source's metadata" in {
        val metadata = mock[RowMetadata]
        new TestStmt(
          rows = Vector(mock[Row], mock[Row], mock[Row]),
          metadata = metadata
        ).executeForSet().get.metadata shouldBe theSameInstanceAs(metadata)
      }

      "return source's warnings" in {
        val warnings = Vector(Warning("msg1", "code1"), Warning("msg2", "code2"))
        new TestStmt(
          rows = Vector(mock[Row], mock[Row], mock[Row]),
          warnings = warnings
        ).executeForSet().get.warnings shouldBe warnings
      }
    }

    "executed for rows affected" should {
      "return number of affected rows" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        new TestStmt(rows).executeForRowsAffected().get shouldBe rows.size
      }

      "fail if source fails" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        the[RuntimeException] thrownBy {
          new TestStmt(rows, failOn = Some(0)).executeForRowsAffected().get
          new AnyRef //to satisfy scalatest
        }.shouldBe(theSameInstanceAs(publisherFailure))
      }
    }

    "executed ignoring result" should {
      "succeed if source succeeds" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        noException should be thrownBy new TestStmt(rows).execute().get
      }

      "fail if source fails" in {
        val rows = Vector(mock[Row], mock[Row], mock[Row])

        the[RuntimeException] thrownBy {
          new TestStmt(rows, failOn = Some(0)).execute().get
          new AnyRef //to satisfy scalatest
        }.shouldBe(theSameInstanceAs(publisherFailure))
      }
    }

  }

  class TestStmt(rows: Vector[Row],
                 failOn: Option[Int] = None,
                 warnings: Vector[Warning] = Vector.empty,
                 metadata: RowMetadata = RowMetadata(Vector.empty)) extends ExecutableStatementPartialImpl {
    implicit protected val ec: ExecutionContext = ExecutionContext.global

    def stream()(implicit timeout: Timeout): RowPublisher = {
      new TestRowPublisher(rows, failOn, warnings, metadata)
    }
  }

  class TestRowPublisher(rows: Vector[Row],
                         failOn: Option[Int],
                         warns: Vector[Warning],
                         mdata: RowMetadata) extends RowPublisher {

    private val publisher = {
      Source(rows).zipWithIndex.map { case (row, idx) =>
        failOn match {
          case Some(`idx`) => throw publisherFailure
          case _ => row
        }
      }.runWith(Sink.asPublisher(fanout = false))
    }

    val rowsAffected: Future[Long] = Future.successful(rows.length.toLong)

    val warnings: Future[ImmutSeq[Warning]] = Future.successful(warns)

    val metadata: Future[RowMetadata] = Future.successful(mdata)

    def done: Future[Unit] = ???

    def subscribe(s: Subscriber[_ >: Row]): Unit = {
      publisher.subscribe(s)
    }
  }

}
