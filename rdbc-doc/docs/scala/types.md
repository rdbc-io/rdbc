<!---
 ! Copyright 2016-2017 Krzysztof Pado
 !
 ! Licensed under the Apache License, Version 2.0 (the "License");
 ! you may not use this file except in compliance with the License.
 ! You may obtain a copy of the License at
 !
 !     http://www.apache.org/licenses/LICENSE-2.0
 !
 ! Unless required by applicable law or agreed to in writing, software
 ! distributed under the License is distributed on an "AS IS" BASIS,
 ! WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ! See the License for the specific language governing permissions and
 ! limitations under the License. 
 -->
!!! warning
    rdbc project and this documentation is still a work in progress.
    It's not ready yet for production use.

Both when passing arguments to statements and when processing resulting rows
there is a need to convert values between database data types and Scala types.
Paragraphs below describe the mapping and possible conversions.

## Type mapping

Following table lists mapping between Scala and SQL types.

| SQL type                  | Scala type   |
|---------------------------|--------------|
| CHAR                      | `String`     |
| VARCHAR                   | `String`     |
| NCHAR                     | `String`     |
| NVARCHAR                  | `String`     |
| CLOB                      | `String`     |
| NCLOB                     | `String`     |
| BINARY                    | `Array[Byte]`|
| VARBINARY                 | `Array[Byte]`|
| BLOB                      | `Array[Byte]`|
| BOOLEAN                   | `Boolean`|
| NUMERIC                   | [`io.rdbc.sapi.SqlNumeric`]()|
| DECIMAL                   | [`io.rdbc.sapi.SqlNumeric`]()|
| REAL                      | `Float`|
| FLOAT                     | `Double`|
| DOUBLE                    | `Double`|
| SMALLINT                  | `Short`|
| INTEGER                   | `Int`|
| BIGINT                    | `Long`|
| DATE                      | `java.time.LocalDate`|
| TIME                      | `java.time.LocalTime`|
| TIMESTAMP                 | `java.time.LocalDateTime` |
| TIMESTAMP WITH TIME ZONE  | `java.time.Instant`|

Following table lists mapping between Scala and database types not defined
by the SQL standard.

| SQL type | Scala type        |
|----------|-------------------|
| UUID     | `java.util.UUID`  |


## Result type conversions

SQL types listed in [Type mapping](#type-mapping) paragraph can be represented 
not only by their direct Scala counterparts. For example, `NUMERIC` can also be
converted to Scala's `Float`. If user requests `NUMERIC` column value as a `Float`,
`Float` will be returned, possibly losing precision. The table below lists possible
conversions.

| Source SQL type | Target Scala type | Notes |
|-----------------|-------------------|-------|
| `CHAR`, `NCHAR` | `Boolean` | `'1'`, `'T'`, `'Y'` converts to `true`. `'0'`, `'F'`, `'N'` converts to `false`.
| `DECIMAL`, `NUMERIC`, `REAL`, `FLOAT`, `DOUBLE`, `INTEGER`, `SMALLINT`, `BIGINT` | `Boolean` | 1 converts to `true`. 0 converts to `false`.
| `VARCHAR`, `NVARCHAR`,<br>`CLOB`, `NCLOB` | `BigDecimal`, `SqlNumeric` | Textual value is converted by rules defined [here](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#BigDecimal-java.lang.String-).
| `VARCHAR`, `NVARCHAR`,<br>`CLOB`, `NCLOB` | `UUID` | Textual value is converted by rules defined [here](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html#fromString-java.lang.String-).
| `DECIMAL`, `NUMERIC`, `REAL`, `FLOAT`, `DOUBLE`, `INTEGER`, `SMALLINT`, `BIGINT` | `Float`, `Double`, `Byte`, `Short`, `Int`, `Long` | Value may be rounded and/or truncated.
| `INTEGER`, `SMALLINT`, `BIGINT`  | `BigDecimal` |
| `DECIMAL`, `NUMERIC`, `REAL`, `FLOAT`, `DOUBLE`  | `BigDecimal` | Infinity and NaN are not convertible.
| `VARCHAR`, `NVARCHAR`,<br>`CLOB`, `NCLOB` | `Char` | Values containing single character are convertible.
| `TIMESTAMP` | `LocalDate` | Time part is truncated.
| `TIMESTAMP` | `LocalTime` | Date part is truncated.

If client requests to convert between inconvertible types, [`ConversionException`]()
is thrown.

## Statement argument type conversions

Not only Scala types listed in [Type mapping](#type-mapping) paragraph can be used
as statement arguments. For example, `SqlNumeric` doesn't have to be used every
time for `NUMERIC` parameters - user can choose to set `NUMERIC` parameters as
`Float` values. The table below lists possible type conversions.

| Source Scala type | Target SQL type | Notes |
|-------------------|-----------------|-------|
| `Float`, `Double` `Byte`, `Short`, `Int`, `Long`, `BigDecimal` | `NUMERIC`, `DECIMAL` |
| `Float`, `Double` `Byte`, `Short`, `Int`, `Long`, `BigDecimal` | `REAL`, `FLOAT`, `DOUBLE` | Value may be rounded and/or truncated.
| `Float`, `Double` `Byte`, `Short`, `Int`, `Long`, `BigDecimal` | `SMALLINT`, `INTEGER`, `BIGINT` | Value may be rounded and/or truncated.

## Vendor specific types

rdbc driver implementing support for a particular database vendor may provide
additional type mappings and conversions. Consult the driver's documentation
on this topic.
