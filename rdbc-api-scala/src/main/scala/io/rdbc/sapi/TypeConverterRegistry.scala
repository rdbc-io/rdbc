/*
 * Copyright 2016 Krzysztof Pado
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

package io.rdbc.sapi

import io.rdbc.ImmutSeq

/**
  * A registry holding a map of type converters.
  *
  * @param converters type converters. Map keys are conversion target classes.
  */
class TypeConverterRegistry(converters: Map[Class[_], TypeConverter[_]]) {

  /** Gets a converter by a given class. */
  def getByClass[T](cls: Class[T]): Option[TypeConverter[T]] = {
    converters.get(cls).asInstanceOf[Option[TypeConverter[T]]]
  }
}

/** Factory of TypeConverterRegistry */
object TypeConverterRegistry {

  /** Returns a type converter registry from converters given. */
  def apply(converters: TypeConverter[_]*): TypeConverterRegistry = {
    val registry: Map[Class[_], TypeConverter[_]] = Map(
      converters.map(conv => conv.cls -> conv): _*
    )
    new TypeConverterRegistry(registry)
  }

  /** Returns a type converter registry from converters given. */
  def apply(converters: ImmutSeq[TypeConverter[_]]): TypeConverterRegistry = {
    apply(converters: _*)
  }
}
