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

Statement rows are represented in rdbc as
[`Row`]({{scaladocRoot}}/io/rdbc/sapi/Row.html) trait. This chapter describes
this trait and methods it provides that give access column values.

## Generic methods

`Row` trait declares a family of `col` methods that allow accessing column
values of any Scala type. The methods accept a single type parameter telling
rdbc what Scala type should be used to represent a database value &mdash; see
the list below for their simplified declarations.

* `:::scala def col[A](idx: Int): A`
* `:::scala def colOpt[A](idx: Int): Option[A]`
* `:::scala def col[A](name: String): A`
* `:::scala def colOpt[A](name: String): Option[A]`

The methods differ from one another in two areas:
 
*    **Named vs unnamed columns**

     To fetch column value by name use method that accepts a `String`;
     to fetch value by column's index, use the version that accepts an `Int`.
     Index is 0 based.
     
*    **Null-safety**

     `colOpt` methods are null-safe, and `col` methods aren't. You can't use
     plain `col` method to get SQL `NULL` values. If you try that,
     [`ConversionException`]({{scaladocRoot}}/io/rdbc/api/exceptions/ConversionException.html)
     will be thrown. `colOpt` returns `Option` so it's fit for handling `NULL`s
     &mdash; it represents them by `None`. `col` method is intended to be used
     only for columns that can't hold `NULL` values, for example because there is
     a not-null constraint defined for them.

Examples below show how to use `col` methods on rows produced by 

```sql
select first_name, last_name, age from persons
```

statement.

```scala
/* fetches first_name. If value is NULL ConversionException will be thrown */
row.col[String](0) 

/* fetches last_name. If value is NULL ConversionException will be thrown */
row.col[String]("last_name")

/* fetches age. If value is NULL, None will be returned */
row.colOpt[Int](2)
```

## Type-specific methods

`Row` trait, for convenience, also provides methods that aren't parametrized
by type and their names reflect the type they return. They are simply shortcuts
for calling generic `col` methods described earlier. For instance, there is a
`str` method that is a shortcut for calling `col[String]` method. See the 
[`Row` Scaladoc]({{scaladocRoot}}/io/rdbc/sapi/Row.html) for a complete list.

Previous example could be rewritten as follows:

```scala
/* fetches first_name. If value is NULL ConversionException will be thrown */
row.str(0) 

/* fetches last_name. If value is NULL ConversionException will be thrown */
row.str("last_name")

/* fetches age. If value is NULL, None will be returned */
row.intOpt(2)
```

If you want to use types supported by the particular driver but not supported
by default by rdbc, you must always use generic `col` methods.
