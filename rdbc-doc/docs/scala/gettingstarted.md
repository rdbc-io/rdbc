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
!!! warning
    rdbc project and this documentation is still a work in progress.
    It's not ready yet for production use.
    
## Adding rdbc to your project

rdbc is just an API — to actually use it as a database client you need
an implementation called a driver. So — to use rdbc in your project you need
to include *two* dependencies, one for the API, and one for a driver. This 
documentation doesn't describe any particular driver — you should be able to
find artifact names you need to include in your build definition in the driver's
documentation. For your convenience, however, [Drivers](../drivers.md) page lists available
drivers grouped by a database engine they support.

rdbc JARs are published to
[Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.rdbc%22%20api)
repository. The API is currently available for Scala 2.11 and 2.12 and requires
Java 8 runtime.

### SBT
For sbt projects, add the following to `build.sbt`:
```scala
libraryDependencies ++= Vector(
  "io.rdbc" %% "rdbc-api-scala" % "{{version}}",
  //here goes the driver dependency
)
```

### Gradle
For Gradle projects, add the following to the `dependencies` section of `build.gradle`:

Scala 2.12
```groovy
compile group: 'io.rdbc', name: 'rdbc-api-scala_2.12', version: '{{version}}'
compile //here goes the driver dependency
```

Scala 2.11
```groovy
compile group: 'io.rdbc', name: 'rdbc-api-scala_2.11', version: '{{version}}'
compile //here goes the driver dependency
```

### Maven
For Maven projects, add the following to the `dependencies` element of `pom.xml`:

Scala 2.12
```xml
<dependency>
  <groupId>io.rdbc</groupId>
  <artifactId>rdbc-api-scala_2.12</artifactId>
  <version>{{version}}</version>
</dependency>
<dependency>
  <!-- here goes the driver dependency -->
</dependency>
```

Scala 2.11
```xml
<dependency>
  <groupId>io.rdbc</groupId>
  <artifactId>rdbc-api-scala_2.11</artifactId>
  <version>{{version}}</version>
</dependency>
<dependency>
  <!-- here goes the driver dependency -->
</dependency>
```

## Working with Scala `Future`s

Since all rdbc API methods that perform I/O return Scala's `Future`s you'll
need a knowledge on how to write asynchronous code using them. Throughout this 
documentation it is assumed that the reader has a basic knowledge about `Future`
trait. If you're new to this concept you may find these resources useful:

*  [The Neophyte's Guide to Scala](http://danielwestheide.com/blog/2013/01/09/the-neophytes-guide-to-scala-part-8-welcome-to-the-future.html)
*  [Official documentation](http://docs.scala-lang.org/overviews/core/futures.html)

## A "Hello world" application

It's time for a small but complete example of rdbc API usage. A snippet below
inserts a "Hello world!" greeting into a `messages` database table. 

A table with the following definition is assumed to exist:
```sql
create table messages(txt varchar(100))
```

And here's the actual code snippet:
```
#!scala
import io.rdbc.sapi._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object HelloRdbc extends App {

  val connFactory: ConnectionFactory = ???

  val greeting = "Hello World!"
  val insertFut: Future[Long] = connFactory.withConnection { conn =>
    conn
      .statement(sql"insert into messages(txt) values ($greeting)")
      .executeForRowsAffected()
  }.andThen {
    case Success(count) => println(s"inserted $count greeting(s)")
    case Failure(ex) => ex.printStackTrace()
  }

  Await.ready(
    insertFut.transformWith(_ => connFactory.shutdown()),
    10.seconds
  )
}
```
In this very simple application:

*  At line `10` a
   [`ConnectionFactory`]({{scaladocRoot}}/io/rdbc/sapi/ConnectionFactory.html)
   coming from a driver package should be instantiated. Each rdbc driver provides
   an implementation of this trait that allows to estabilish a connection to a database.
   `ConnectionFactory` implementation and classes needed for its configuration should
   be the only classes directly used from the driver package.

*  At line `13` a connection to the database is requested to be estabilished - 
   when that happens, a function passed as a code block is executed.

*  In this code block, at line `15` a prepared statement is requested to be
   created using a
   [`sql`]({{scaladocRoot}}/io/rdbc/sapi/SqlInterpolatorTrait$SqlInterpolator.html#sql(args:Any*):io.rdbc.sapi.SqlWithParams)
   [string interpolator](http://docs.scala-lang.org/overviews/core/string-interpolation.html)
   which passes a `greeting` string argument to it.

*  The statement is requested to be executed at line `16` and
   to return number of affected rows.
  
*  When the database operation finishes and the connection is released, at line
   `17` the `Future`'s result is handled: number of affected rows is printed in
   case of a success or a stack trace is printed in case of an error.
   
*  None of the database calls block the executing thread - all of them return
   `Future`s that are then chained. The only statement that blocks starts at line
   `22` when the application waits for operations requested earlier to complete.
   Because this demo is just a simple console application, we need to block the
   thread somewhere to wait for a moment that the application can exit.

## Scaladoc

You can browse Scaladoc using javadoc.io site 
[here](https://javadoc.io/doc/io.rdbc/rdbc-api-scala_2.12/{{version}}). The site
allows to switch between Scala versions and rdbc versions.
