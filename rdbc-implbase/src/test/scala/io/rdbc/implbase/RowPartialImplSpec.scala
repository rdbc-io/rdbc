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

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}
import java.util.UUID

import io.rdbc.api.exceptions.{ColumnIndexOutOfBoundsException, ConversionException, MissingColumnException}
import io.rdbc.sapi.{SqlNumeric, TypeConverter, TypeConverterRegistry}
import org.scalamock.scalatest.MockFactory

import scala.reflect.ClassTag

class RowPartialImplSpec
  extends RdbcImplbaseSpec
    with MockFactory {

  "RowPartialImplSpec" when {
    "null-unsafe getters are used" when {
      "named values are used" should {

        "get values without conversion if no conversion is necessary" in {
          val colName = "col"
          val colVal: Any = "0"
          val row = new TstRow(named = Map(colName -> Some(colVal)))

          row.col[String](colName) shouldBe colVal
        }

        "get and convert values when there is a converter available" in {
          val colName = "col"
          val colVal: Any = 0
          val row = new TstRow(named = Map(colName -> Some(colVal)))

          val convertedColVal = "0"
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).returning(convertedColVal)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          row.col[String](colName) shouldBe convertedColVal
        }

        "throw conversion exception when there is no converter available" in {
          val colName = "col"
          val colVal: Any = 0
          val row = new TstRow(named = Map(colName -> Some(colVal)))


          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(None)

          val ex = the[ConversionException] thrownBy row.col[String](colName)
          ex.targetType shouldBe classOf[String]
          ex.value shouldBe colVal
        }

        "propagate conversion errors" in {
          val colName = "col"
          val colVal: Any = 0
          val row = new TstRow(named = Map(colName -> Some(colVal)))
          val conversionEx = new ConversionException(colVal, classOf[String])
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).throws(conversionEx)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          the[ConversionException] thrownBy (row.col[String](colName))
            .shouldBe(theSameInstanceAs(conversionEx))
        }

        "fail for SQL NULL values" in {
          val colName = "col"
          val row = new TstRow(named = Map(colName -> Option.empty[Int]))

          val ex = the[ConversionException] thrownBy row.col[String](colName)
          ex.targetType shouldBe classOf[String]
          ex.value shouldBe None
        }
      }

      "positional values are used" should {

        "get values without conversion if no conversion is necessary" in {
          val colIdx = 1
          val colVal: Any = "0"
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))

          row.col[String](colIdx) shouldBe colVal
        }

        "get and convert values when there is a converter available" in {
          val colIdx = 1
          val colVal: Any = 0
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))

          val convertedColVal = "0"
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).returning(convertedColVal)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          row.col[String](colIdx) shouldBe convertedColVal
        }

        "throw conversion exception when there is no converter available" in {
          val colIdx = 1
          val colVal: Any = 0
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))


          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(None)

          val ex = the[ConversionException] thrownBy row.col[String](colIdx)
          ex.targetType shouldBe classOf[String]
          ex.value shouldBe colVal
        }

        "propagate conversion errors" in {
          val colIdx = 1
          val colVal: Any = 0
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))
          val conversionEx = new ConversionException(colVal, classOf[String])
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).throws(conversionEx)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          the[ConversionException] thrownBy (row.col[String](colIdx))
            .shouldBe(theSameInstanceAs(conversionEx))
        }

        "fail for SQL NULL values" in {
          val colIdx = 1
          val row = new TstRow(positional = Map(colIdx -> Option.empty[Int]))

          val ex = the[ConversionException] thrownBy row.col[String](colIdx)
          ex.targetType shouldBe classOf[String]
          ex.value shouldBe None
        }
      }
    }

    "null-safe getters are used" when {
      "named values are used" should {

        "get values without conversion if no conversion is necessary" in {
          val colName = "col"
          val colVal: Any = "0"
          val row = new TstRow(named = Map(colName -> Some(colVal)))

          row.colOpt[String](colName) shouldBe Some(colVal)
        }

        "get and convert values when there is a converter available" in {
          val colName = "col"
          val colVal: Any = 0
          val row = new TstRow(named = Map(colName -> Some(colVal)))

          val convertedColVal = "0"
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).returning(convertedColVal)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          row.colOpt[String](colName) shouldBe Some(convertedColVal)
        }

        "throw conversion exception when there is no converter available" in {
          val colName = "col"
          val colVal: Any = 0
          val row = new TstRow(named = Map(colName -> Some(colVal)))

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(None)

          val ex = the[ConversionException] thrownBy row.colOpt[String](colName)
          ex.targetType shouldBe classOf[String]
          ex.value shouldBe colVal
        }

        "propagate conversion errors" in {
          val colName = "col"
          val colVal: Any = 0
          val row = new TstRow(named = Map(colName -> Some(colVal)))
          val conversionEx = new ConversionException(colVal, classOf[String])
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).throws(conversionEx)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          the[ConversionException] thrownBy (row.colOpt[String](colName))
            .shouldBe(theSameInstanceAs(conversionEx))
        }

        "return None for SQL NULL values" in {
          val colName = "col"
          val row = new TstRow(named = Map(colName -> Option.empty[Int]))

          row.colOpt[String](colName) shouldBe None
        }
      }

      "positional values are used" should {

        "get values without conversion if no conversion is necessary" in {
          val colIdx = 1
          val colVal: Any = "0"
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))

          row.colOpt[String](colIdx) shouldBe Some(colVal)
        }

        "get and convert values when there is a converter available" in {
          val colIdx = 1
          val colVal: Any = 0
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))

          val convertedColVal = "0"
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).returning(convertedColVal)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          row.colOpt[String](colIdx) shouldBe Some(convertedColVal)
        }

        "throw conversion exception when there is no converter available" in {
          val colIdx = 1
          val colVal: Any = 0
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))


          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(None)

          val ex = the[ConversionException] thrownBy row.colOpt[String](colIdx)
          ex.targetType shouldBe classOf[String]
          ex.value shouldBe colVal
        }

        "propagate conversion errors" in {
          val colIdx = 1
          val colVal: Any = 0
          val row = new TstRow(positional = Map(colIdx -> Some(colVal)))
          val conversionEx = new ConversionException(colVal, classOf[String])
          val converter = mock[TypeConverter[String]]

          (converter.fromAny _).expects(colVal).throws(conversionEx)

          (row.typeConvRegistryMock.getByClass[String] _)
            .expects(classOf[String])
            .returning(Some(converter))

          the[ConversionException] thrownBy (row.colOpt[String](colIdx))
            .shouldBe(theSameInstanceAs(conversionEx))
        }

        "return None for SQL NULL values" in {
          val colIdx = 1
          val row = new TstRow(positional = Map(colIdx -> Option.empty[Int]))

          row.colOpt[String](colIdx) shouldBe None
        }
      }
    }

    class TstRow(named: Map[String, Option[Any]] = Map.empty,
                 positional: Map[Int, Option[Any]] = Map.empty)
      extends RowPartialImpl {

      val typeConvRegistryMock = mock[TypeConverterRegistry]

      protected val typeConverters: TypeConverterRegistry = typeConvRegistryMock
      protected def any(name: String): Option[Any] = {
        named.getOrElse(name, throw new MissingColumnException(name))
      }
      protected def any(idx: Int): Option[Any] = {
        positional.getOrElse(idx, throw new ColumnIndexOutOfBoundsException(idx, 0))
      }
    }
  }

  "RowPartialImplSpec" should {
    "delegate typed requests to col" when {
      "using named arguments" when {
        "using null-unsafe methods" in {
          delegateTest("val", classOf[String], _.str)
          delegateTest(true, classOf[Boolean], _.bool)
          delegateTest('c', classOf[Char], _.char)
          delegateTest(10.toShort, classOf[Short], _.short)
          delegateTest(10, classOf[Int], _.int)
          delegateTest(10L, classOf[Long], _.long)
          delegateTest(BigDecimal(10), classOf[BigDecimal], _.bigDecimal)
          delegateTest(SqlNumeric.Val(10), classOf[SqlNumeric], _.numeric)
          delegateTest(10.0d, classOf[Double], _.double)
          delegateTest(10.0f, classOf[Float], _.float)
          delegateTest(Instant.MIN, classOf[Instant], _.instant)
          delegateTest(LocalDateTime.MIN, classOf[LocalDateTime], _.localDateTime)
          delegateTest(LocalDate.MIN, classOf[LocalDate], _.localDate)
          delegateTest(LocalTime.MIDNIGHT, classOf[LocalTime], _.localTime)
          delegateTest(Array(0.toByte), classOf[Array[Byte]], _.bytes)
          delegateTest(UUID.fromString("6213f244-7ad1-11e7-91e2-531545edd352"), classOf[UUID], _.uuid)
        }
        "using null-safe methods" in {
          delegateTestOpt("val", classOf[String], _.strOpt)
          delegateTestOpt(true, classOf[Boolean], _.boolOpt)
          delegateTestOpt('c', classOf[Char], _.charOpt)
          delegateTestOpt(10.toShort, classOf[Short], _.shortOpt)
          delegateTestOpt(10, classOf[Int], _.intOpt)
          delegateTestOpt(10L, classOf[Long], _.longOpt)
          delegateTestOpt(BigDecimal(10), classOf[BigDecimal], _.bigDecimalOpt)
          delegateTestOpt(SqlNumeric.Val(10), classOf[SqlNumeric], _.numericOpt)
          delegateTestOpt(10.0d, classOf[Double], _.doubleOpt)
          delegateTestOpt(10.0f, classOf[Float], _.floatOpt)
          delegateTestOpt(Instant.MIN, classOf[Instant], _.instantOpt)
          delegateTestOpt(LocalDateTime.MIN, classOf[LocalDateTime], _.localDateTimeOpt)
          delegateTestOpt(LocalDate.MIN, classOf[LocalDate], _.localDateOpt)
          delegateTestOpt(LocalTime.MIDNIGHT, classOf[LocalTime], _.localTimeOpt)
          delegateTestOpt(Array(0.toByte), classOf[Array[Byte]], _.bytesOpt)
          delegateTestOpt(UUID.fromString("6213f244-7ad1-11e7-91e2-531545edd352"), classOf[UUID], _.uuidOpt)
        }
      }
      "using positional arguments" when {
        "using null-unsafe methods" in {
          delegateTestIdx("val", classOf[String], _.str)
          delegateTestIdx(true, classOf[Boolean], _.bool)
          delegateTestIdx('c', classOf[Char], _.char)
          delegateTestIdx(10.toShort, classOf[Short], _.short)
          delegateTestIdx(10, classOf[Int], _.int)
          delegateTestIdx(10L, classOf[Long], _.long)
          delegateTestIdx(BigDecimal(10), classOf[BigDecimal], _.bigDecimal)
          delegateTestIdx(SqlNumeric.Val(10), classOf[SqlNumeric], _.numeric)
          delegateTestIdx(10.0d, classOf[Double], _.double)
          delegateTestIdx(10.0f, classOf[Float], _.float)
          delegateTestIdx(Instant.MIN, classOf[Instant], _.instant)
          delegateTestIdx(LocalDateTime.MIN, classOf[LocalDateTime], _.localDateTime)
          delegateTestIdx(LocalDate.MIN, classOf[LocalDate], _.localDate)
          delegateTestIdx(LocalTime.MIDNIGHT, classOf[LocalTime], _.localTime)
          delegateTestIdx(Array(0.toByte), classOf[Array[Byte]], _.bytes)
          delegateTestIdx(UUID.fromString("6213f244-7ad1-11e7-91e2-531545edd352"), classOf[UUID], _.uuid)
        }
        "using null-safe methods" in {
          delegateTestIdxOpt("val", classOf[String], _.strOpt)
          delegateTestIdxOpt(true, classOf[Boolean], _.boolOpt)
          delegateTestIdxOpt('c', classOf[Char], _.charOpt)
          delegateTestIdxOpt(10.toShort, classOf[Short], _.shortOpt)
          delegateTestIdxOpt(10, classOf[Int], _.intOpt)
          delegateTestIdxOpt(10L, classOf[Long], _.longOpt)
          delegateTestIdxOpt(BigDecimal(10), classOf[BigDecimal], _.bigDecimalOpt)
          delegateTestIdxOpt(SqlNumeric.Val(10), classOf[SqlNumeric], _.numericOpt)
          delegateTestIdxOpt(10.0d, classOf[Double], _.doubleOpt)
          delegateTestIdxOpt(10.0f, classOf[Float], _.floatOpt)
          delegateTestIdxOpt(Instant.MIN, classOf[Instant], _.instantOpt)
          delegateTestIdxOpt(LocalDateTime.MIN, classOf[LocalDateTime], _.localDateTimeOpt)
          delegateTestIdxOpt(LocalDate.MIN, classOf[LocalDate], _.localDateOpt)
          delegateTestIdxOpt(LocalTime.MIDNIGHT, classOf[LocalTime], _.localTimeOpt)
          delegateTestIdxOpt(Array(0.toByte), classOf[Array[Byte]], _.bytesOpt)
          delegateTestIdxOpt(UUID.fromString("6213f244-7ad1-11e7-91e2-531545edd352"), classOf[UUID], _.uuidOpt)
        }
      }
    }

    def delegateTest[A](colVal: A, cls: Class[A], testedMethod: RowPartialImpl => (String) => A): Unit = {
      val colName = "col"
      val colOps = mock[ColOps]
      val row = new TstRow(colOps)

      (colOps.col(_: String, _: Class[A])).expects(colName, cls).once().returning(colVal)

      testedMethod(row)(colName) shouldBe colVal
    }

    def delegateTestOpt[A](colVal: A, cls: Class[A], testedMethod: RowPartialImpl => (String) => Option[A]): Unit = {
      val colName = "col"
      val colOps = mock[ColOps]
      val row = new TstRow(colOps)

      (colOps.colOpt(_: String, _: Class[A])).expects(colName, cls).once().returning(Some(colVal))

      testedMethod(row)(colName) shouldBe Some(colVal)
    }

    def delegateTestIdx[A](colVal: A, cls: Class[A], testedMethod: RowPartialImpl => (Int) => A): Unit = {
      val colIdx = 1
      val colOps = mock[ColOps]
      val row = new TstRow(colOps)

      (colOps.col(_: Int, _: Class[A])).expects(colIdx, cls).once().returning(colVal)

      testedMethod(row)(colIdx) shouldBe colVal
    }

    def delegateTestIdxOpt[A](colVal: A, cls: Class[A], testedMethod: RowPartialImpl => (Int) => Option[A]): Unit = {
      val colIdx = 1
      val colOps = mock[ColOps]
      val row = new TstRow(colOps)

      (colOps.colOpt(_: Int, _: Class[A])).expects(colIdx, cls).once().returning(Some(colVal))

      testedMethod(row)(colIdx) shouldBe Some(colVal)
    }

    trait ColOps {
      def colOpt[A](idx: Int, cls: Class[A]): Option[A]
      def colOpt[A](name: String, cls: Class[A]): Option[A]
      def col[A](idx: Int, cls: Class[A]): A
      def col[A](name: String, cls: Class[A]): A
    }

    class TstRow(colOps: ColOps)
      extends RowPartialImpl {


      override def colOpt[A: ClassTag](idx: Int): Option[A] = {
        val cls = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]
        colOps.colOpt(idx, cls)
      }

      override def colOpt[A: ClassTag](name: String): Option[A] = {
        val cls = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]
        colOps.colOpt(name, cls)
      }

      override def col[A: ClassTag](idx: Int): A = {
        val cls = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]
        colOps.col(idx, cls)
      }

      override def col[A: ClassTag](name: String): A = {
        val cls = implicitly[ClassTag[A]].runtimeClass.asInstanceOf[Class[A]]
        colOps.col(name, cls)
      }

      protected def typeConverters = ???
      protected def any(name: String) = ???
      protected def any(idx: Int) = ???
    }
  }
}
