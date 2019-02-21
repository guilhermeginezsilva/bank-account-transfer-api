# Bank Account Transfer RESTful API

**Challenge:** Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

**Requirements:**
1.  You can use Java, Scala or Kotlin:
    * Java 8

2. Keep it simple and to the point (e.g. no need to implement any authentication).

3. Assume the API is invoked by multiple systems and services on behalf of end users.
    * Multi Threading supported by ReentrantLock control

4.  You can use frameworks/libraries if you like (except Spring), but don't forget about requirement #2 – keep it simple and avoid heavy frameworks.
    * Jersey: REST support
    * Undertow: Very lightweight embedded container

5. The datastore should run in-memory for the sake of this test.
    * In Memory DAO controller -> Extremely fast

6. The final result should be executable as a standalone program (should not require a pre-installed container/server).
    * Undertow as embedded container
    * Gradle fat jar

7. Demonstrate with tests that the API works as expected.
    * Unit tests JUnit 4 + Mockito + PowerMock
    
## Instructions

* Project is available at Github: 
