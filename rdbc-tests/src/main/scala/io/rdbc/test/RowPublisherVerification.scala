package io.rdbc.test

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import io.rdbc.sapi._
import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatest.testng.TestNGSuiteLike

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class RowPublisherVerification(env: TestEnvironment, publisherShutdownTimeout: Long)
  extends PublisherVerification[Row](env, publisherShutdownTimeout)
    with TestNGSuiteLike {

  private implicit val system = ActorSystem("RowPublisherVerification")
  private implicit val materializer = ActorMaterializer()

  protected implicit def ec: ExecutionContext = ExecutionContext.global

  protected implicit def waitAtMost: Duration = 10.seconds

  protected implicit def timeout = Timeout(waitAtMost)

  protected def connection(): Connection

  protected def intDataType: String
  protected def varcharDataType: String
  protected def castVarchar2Int(colName: String): String

  private val idx = new AtomicInteger(0)

  override def maxElementsFromPublisher(): Long = 100L

  override def createPublisher(elements: Long): Publisher[Row] = {
    val conn = connection()
    val currIdx = idx.incrementAndGet()
    conn.ddl(s"create table tbl$currIdx (col $intDataType)").get.execute().get
    conn.beginTx().get
    for (i <- 1L to elements) yield {
      conn.insert(s"insert into tbl$currIdx(col) values(:v)").get.bind("v" -> i).execute().get
    }
    conn.commitTx().get
    val pub = conn.select(s"select col from tbl$currIdx").get.noParams.executeForStream().get.rows
    Source.fromPublisher(pub)
      .watchTermination() { (_, doneFut) =>
        doneFut.onComplete { _ =>
          conn.forceRelease().get
        }
      }
      .runWith(Sink.asPublisher(false))
  }

  override def createFailedPublisher(): Publisher[Row] = {
    val conn = connection()
    val currIdx = idx.incrementAndGet()
    conn.ddl(s"create table tbl$currIdx (col $varcharDataType)").get.execute().get
    conn.insert(s"insert into tbl$currIdx(col) values('text')").get.noParams.execute().get
    val pub = conn.select(s"select ${castVarchar2Int("col")} from tbl$currIdx").get
      .noParams
      .executeForStream().get
      .rows
    Source.fromPublisher(pub)
      .watchTermination() { (_, doneFut) =>
        doneFut.onComplete { _ =>
          conn.forceRelease().get
        }
      }
      .runWith(Sink.asPublisher(false))
  }

}
