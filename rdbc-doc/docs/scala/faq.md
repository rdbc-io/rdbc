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

Here are some questions that have a potential to become frequently asked.

1.    **I have plenty of time, I don't want to use any timeout for any of my queries. What can I do?**

      Import `Timeout` instance of infinite duration either by importing `io.rdbc.sapi._`
      or directly `io.rdbc.sapi.Timeout.Implicits.inf`.

      ---

2.    **How do I build SQL dynamically using `sql` interpolator?**

      Use special `#$` prefix for the dynamic parts. See [this]()
      paragraph for details.

      ---
      
3.   **How to pass `Timeout` explicitly to `ExecutableStatement`'s `executeForKey` method? I keep getting compilation errors.**

     `executeForKey` method's declaration is
     
        :::scala
        def executeForKey[K: ClassTag]()(implicit timeout: Timeout): Future[K]
        
     Under the hood, this declaration really means this:
     
        :::scala
        def executeForKey[K]()(implicit classTag: ClassTag[K], timeout: Timeout): Future[K]

     So you can't really execute this method like this: 
     
        :::scala
        executeForKey[Int]()(timeout)
     
     Instead, you have to do this:
    
        :::scala
        executeForKey()(timeout, classOf[Int])

      ---

4.    **I'm executing insert statement and trying to get auto-generated key from the DB but nothing is returned. What am I doing wrong?**

      You probably forgot to set a `generatedKeyCols` statement option accordingly.
      See [this]() paragraph for details.
