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

package io.rdbc.sapi

/** A SQL type */
trait SqlType

/** SQL ARRAY type */
case object SqlArray extends SqlType

/** SQL MULTISET type */
case object SqlMultiset extends SqlType

/** SQL CHARACTER */
case object SqlChar extends SqlType

/** SQL CHARACTER VARYING */
case object SqlVarchar extends SqlType

/** SQL CHARACTER LARGE OBJECT */
case object SqlClob extends SqlType

/** SQL NATIONAL CHARACTER */
case object SqlNChar extends SqlType

/** SQL NATIONAL CHARACTER VARYING */
case object SqlNVarchar extends SqlType

/** SQL NATIONAL CHARACTER LARGE OBJECT */
case object SqlNClob extends SqlType

/** SQL BINARY */
case object SqlBinary extends SqlType

/** SQL BINARY VARYING */
case object SqlVarbinary extends SqlType

/** SQL BINARY LARGE OBJECT */
case object SqlBlob extends SqlType

/** SQL NUMERIC type */
case object SqlNumeric extends SqlType

/** SQL DECIMAL type */
case object SqlDecimal extends SqlType

/** SQL SMALLINT type */
case object SqlSmallInt extends SqlType

/** SQL INTEGER type */
case object SqlInteger extends SqlType

/** SQL BIGINT type */
case object SqlBigInt extends SqlType

/** SQL FLOAT type */
case object SqlFloat extends SqlType

/** SQL REAL type */
case object SqlReal extends SqlType

/** SQL DOUBLE PRECISION type */
case object SqlDouble extends SqlType

/** SQL BOOLEAN type */
case object SqlBoolean extends SqlType

/** SQL DATE type */
case object SqlDate extends SqlType

/** SQL TIME WITHOUT TIME ZONE type */
case object SqlTime extends SqlType

/** SQL TIME WITH TIME ZONE type */
case object SqlTimeTz extends SqlType

/** SQL TIMESTAMP type */
case object SqlTimestamp extends SqlType

/** SQL TIMESTAMP WITH TIME ZONE type */
case object SqlTimestampTz extends SqlType

/** SQL INTERVAL type */
case object SqlInterval extends SqlType
