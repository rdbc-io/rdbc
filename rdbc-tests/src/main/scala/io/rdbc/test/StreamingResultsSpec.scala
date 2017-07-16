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

package io.rdbc.test

import io.rdbc.ImmutSeq
import io.rdbc.sapi._
import io.rdbc.test.util.Subscribers
import org.reactivestreams.Subscriber

import scala.concurrent.Promise

trait StreamingResultsSpec
  extends RdbcSpec
    with TableSpec
    with TxSpec {

  protected def intDataTypeName: String
  protected def intDataTypeId: String
  private def columnsDefinition = s"col $intDataTypeName"

  //TODO if any test fails next tests fail too - fix this
  "Streaming results feature should" - {
    "work on empty tables" - {
      withAndWithoutTx(columnsDefinition) { (c, t) =>
        subscribe(c.statement(sql"select col from #$t"), Subscribers.eager()).rows.get shouldBe empty
      }
    }

    "be able to fetch all rows at once" - {
      withAndWithoutTx(columnsDefinition) { (c, t) =>
        val range = 1 to 10
        for {i <- range} yield {
          c.statement(sql"insert into #$t(col) values ($i)").execute().get
        }
        val rows = subscribe(c.statement(sql"select col from #$t order by col"), Subscribers.eager()).rows.get
        rows should have size range.size.toLong
        rows.map(_.int("col")) should contain theSameElementsInOrderAs range
      }
    }

    "be able to fetch rows in chunks" - {
      withAndWithoutTx(columnsDefinition) { (c, t) =>
        val range = 1 to 10
        for {i <- range} yield {
          c.statement(sql"insert into #$t(col) values ($i)").execute().get
        }
        val subscriber = subscribe(c.statement(sql"select col from #$t order by col"), Subscribers.chunk)

        val first5 = Promise[ImmutSeq[Row]]
        subscriber.request(5L, first5)
        first5.future.get should have size 5
        first5.future.get.map(_.int("col")) should contain theSameElementsInOrderAs range.take(5)

        val last5 = Promise[ImmutSeq[Row]]
        subscriber.request(10L, last5)
        last5.future.get should have size 5
        last5.future.get.map(_.int("col")) should contain theSameElementsInOrderAs range.drop(5)

        subscriber.completion.get
      }
    }

    "return metadata" - {
      "about rows affected" - {
        withAndWithoutTx(columnsDefinition) { (c, t) =>
          val range = 1 to 10
          for {i <- range} yield {
            c.statement(sql"insert into #$t(col) values ($i)").execute().get
          }
          val rs = c.statement(sql"update #$t set col = null where col >= 6").stream()
          val subscriber = Subscribers.eager()
          rs.subscribe(subscriber)
          rs.rowsAffected.get shouldBe 5L

          subscriber.rows.get
        }
      }

      "about columns" - {
        withAndWithoutTx(columnsDefinition) { (c, t) =>
          val range = 1 to 10
          for {i <- range} yield {
            c.statement(sql"insert into #$t(col) values ($i)").execute().get
          }
          val rs = c.statement(sql"select col from #$t").stream()
          val subscriber = Subscribers.eager()
          rs.subscribe(subscriber)

          rs.metadata.get.columns should have size 1

          val colMetadata = rs.metadata.get.columns.head
          colMetadata.name shouldBe "col"
          colMetadata.dbTypeId shouldBe intDataTypeId
          colMetadata.cls should contain(classOf[Int])

          subscriber.rows.get
        }
      }
    }
  }

  //TODO how to test subscription cancellation?

  private def subscribe[S <: Subscriber[Row]](
                                               statement: ExecutableStatement,
                                               subscriber: S
                                             ): subscriber.type = {
    val rs = statement.stream()
    rs.subscribe(subscriber)
    subscriber
  }
}
