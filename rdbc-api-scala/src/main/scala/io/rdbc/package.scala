/*
 * Copyright 2016-2017 Krzysztof Pado
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

package io

import scala.collection.immutable

package object rdbc {

  /** An alias for Scala's immutable sequence trait */
  type ImmutSeq[A] = immutable.Seq[A]

  /** An immutable sequences factory */
  object ImmutSeq {

    /** An empty immutable sequence
      *
      * @tparam A the type of the sequence elements
      */
    def empty[A]: ImmutSeq[A] = immutable.Seq.empty[A]

    /** Creates an immutable sequence with the specified elements.
      *
      * @tparam A the type of the sequence elements
      * @param elems the elements of the created sequence
      */
    def apply[A](elems: Seq[A]): ImmutSeq[A] = immutable.Seq(elems: _*)
  }

  /** An alias for Scala's immutable indexed sequence trait */
  type ImmutIndexedSeq[A] = immutable.IndexedSeq[A]
}
