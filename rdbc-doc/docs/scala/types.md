<!---
 ! Copyright 2016-2017 rdbc contributors
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
| NUMERIC                   | [`io.rdbc.sapi.DecimalNumber`]({{scaladocRoot}}/io/rdbc/sapi/DecimalNumber$.html)|
| DECIMAL                   | [`io.rdbc.sapi.DecimalNumber`]({{scaladocRoot}}/io/rdbc/sapi/DecimalNumber$.html)|
| REAL                      | `Float`|
| DOUBLE                    | `Double`|
| SMALLINT                  | `Int`|
| INTEGER                   | `Int`|
| BIGINT                    | `Long`|
| DATE                      | `java.time.LocalDate`|
| TIME                      | `java.time.LocalTime`|
| TIMESTAMP                 | `java.time.LocalDateTime` |
| TIMESTAMP WITH TIME ZONE  | `java.time.ZonedDateTime`|

Following table lists mapping between Scala and database types not defined
by the SQL standard.

| SQL type | Scala type        |
|----------|-------------------|
| UUID     | `java.util.UUID`  |


## Result type conversions

SQL types listed in [Type mapping](#type-mapping) paragraph can be represented 
not only by their direct Scala counterparts. For example, NUMERIC can also be
converted to Scala's `Float`. If user requests NUMERIC column value as a `Float`,
`Float` will be returned, but only if the NUMERIC value is representable by
exact `Float`. The table below lists possible conversions.

| Source SQL type | Target Scala type | Notes |
|-----------------|-------------------|-------|
| CHAR, NCHAR | `Boolean` | `'1'`, `'T'`, `'Y'` converts to `true`.<br>`'0'`, `'F'`, `'N'` converts to `false`.
| DECIMAL, NUMERIC, REAL, DOUBLE, INTEGER, SMALLINT, BIGINT | `Boolean` | 1 converts to `true`.<br>0 converts to `false`.
| DECIMAL, NUMERIC, REAL, DOUBLE, INTEGER, SMALLINT, BIGINT | `Float`, `Double`, `Byte`, `Short`, `Int`, `Long` | Only if exact conversion is possible.
| INTEGER, SMALLINT, BIGINT  | `BigDecimal` |
| DECIMAL, NUMERIC, REAL, DOUBLE  | `BigDecimal` | Infinity and NaN are not convertible.

If client requests to convert between inconvertible types,
[`ConversionException`]({{scaladocRoot}}/io/rdbc/sapi/exceptions/ConversionException.html)
is thrown.

## Explicitly setting database type in statements

rdbc defines a set of case classes that directly represent SQL types. When setting
statement arguments, in case when a given bare Scala type maps to more
than one database type (such as `String` maps to both `CLOB` and `VARCHAR`) and
you want to enforce this argument to be represented as a specific database type,
a case class representing SQL type can be used. 

For example, if you want to pass `:::scala "my text"` as a `CLOB`, instead of passing bare `"my text"`, 
pass `:::scala io.rdbc.sapi.SqlClob("my text")`.

## Setting typed SQL NULL values

Normally, to set a statement argument as `NULL` you can Scala's `None`. `None`
value doesn't carry any information that would allow the rdbc driver to tell
what is a SQL type of the `NULL` value. For most cases the type of the `NULL`
value doesn't matter but in cases it does you can use rdbc's `SqlNull` that
contains the type information.

For example, to pass a `NULL` value typed as `NVARCHAR`, use `:::scala SqlNull.of[SqlNVarchar]`.

## Vendor specific types

rdbc driver implementing support for a particular database vendor may provide
additional type mappings and conversions. Consult the driver's documentation
on this topic.
