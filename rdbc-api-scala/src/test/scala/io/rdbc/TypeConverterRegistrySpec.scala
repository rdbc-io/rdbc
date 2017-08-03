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

package io.rdbc

import io.rdbc.sapi.{TypeConverter, TypeConverterRegistry}
import org.scalamock.scalatest.MockFactory
import org.scalatest.Inside

class TypeConverterRegistrySpec
  extends RdbcSpec
    with Inside
    with MockFactory {

  "TypeConverterRegistry" should {
    "return existing converter by class" in {

      val intConverter = mock[TypeConverter[Int]]
      val strConverter = mock[TypeConverter[String]]

      val registry = new TypeConverterRegistry(
        Map(
          classOf[Int] -> intConverter,
          classOf[String] -> strConverter
        )
      )

      inside(registry.getByClass(classOf[Int])) { case Some(conv) =>
        conv shouldBe theSameInstanceAs(intConverter)
      }

      inside(registry.getByClass(classOf[String])) { case Some(conv) =>
        conv shouldBe theSameInstanceAs(strConverter)
      }
    }

    "return None for non-existing converter" in {
      val intConverter = mock[TypeConverter[Int]]

      val registry = new TypeConverterRegistry(
        Map(classOf[Int] -> intConverter)
      )

      registry.getByClass(classOf[String]) shouldBe empty
    }
  }

  "TypeConverterRegistry factory" should {
    "build the registry from converters list" in {

      val intConv = new DummyIntTypeConverter
      val strConv = new DummyStringTypeConverter

      val registry = TypeConverterRegistry(Vector(intConv, strConv))

      inside(registry.getByClass(classOf[Int])) { case Some(conv) =>
        conv shouldBe theSameInstanceAs(intConv)
      }

      inside(registry.getByClass(classOf[String])) { case Some(conv) =>
        conv shouldBe theSameInstanceAs(strConv)
      }
    }
  }

  class DummyIntTypeConverter extends TypeConverter[Int] {
    override def cls: Class[Int] = classOf[Int]

    override def fromAny(any: Any): Int = 0
  }

  class DummyStringTypeConverter extends TypeConverter[String] {
    override def cls: Class[String] = classOf[String]

    override def fromAny(any: Any): String = ""
  }

}
