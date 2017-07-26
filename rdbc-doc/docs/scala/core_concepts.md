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

There are a couple of core concepts that the API is built upon:

*    **Asynchronous processing**

     Every operation that involves I/O with the database is asynchronous. Methods
     return `Future` or Reactive Stream's `Publisher` instances.

*    **Thread safety**

     rdbc API requires that its implementations must be thread-safe. You are free
     to share class instances among threads, even those that are mutable.

*    **Null safety**

     SQL `NULL` values are represented as `Option` instances. Scala `null` doesn't
     have to be used when working with the API.

*    **Limiting execution time**

     So that application doesn't "hang" forever, operations that interact with
     the database accept a mandatory `implicit` [`Timeout`]() instance which
     controls maximum processing time.
