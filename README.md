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

* Project is available at Github: https://github.com/guilhermeginezsilva/bank-account-transfer-api

### Important:
1. Git is necessary if you want to clone the project to your computer, if you don't want to install git, consider downloading the project in the github repository;

2. To run tests it's mandatory to have gradle 4 installed in your computer, instructions to download and install can be found here:
https://gradle.org/

3. Java 8+ is mandatory to run the project, so to run it you must install Java 8+ in your computer;

#### Clone project

    ###To clone it, just type in your console the command below on you prefered directory:
    git clone https://github.com/guilhermeginezsilva/bank-account-transfer-api.git

#### Tests
This project contains 109 tests to test all the three layers: 

Controllers, Services and DAOs

    ###In the console on the root directory of the project, just type:
    gradle test
    
#### Running
The first version of the executable was built and is available in a directory inside the project:

    ###Just navigate to the releases directory inside the project and run the command below:
    ###On windows:
    start.bat
    
    ###On linux:
    ./start.sh
    
### It's Running!! And now?
Well, you can check that the application is working correctly running the unit and component tests, as already explained above, but we know that there isn't nothing better than having some fun with these projects, so I did some helpful tools:

* You can use Postman application; a script with the request structures is also available in the releases directory, just import in your postman application and have fun;


    ###You can find postman installation instructions here:
    https://www.getpostman.com/
    
    ###After download, you may find a "import" button in the top of the screen. Just import the script and you will be available to make the requests.


* To test performance and multi threading safe mechanism, I've created a html page to make a stress test, it's simple, but powerful. With this tool I could get very good performance results from the application.


    ###In the releases directory just open the 
    stress-test.html and run it on your browser, I've just tested on Chrome, but it may work on other ones;
	
**Important:** 
1. If you start receiving error transactions after a lot of good transactions, probably that's because the account balance is insufficient, just restart the application, it will reset the balances. 

2. Thist page already comes with 2 default accounts filled in the fields, these accounts have a balance of:
Account1(7e75dff6-05b9-4cf6-aaa1-701007d6c09c): £ 100,000.00
Account2(7e75dff6-05b9-4cf6-aaa1-701007d6c09c): £ 50,000.00

3. These data are always created when the application starts, so if you restart the application it will recreate all the data:

Customer (ddcdfb95-2a6e-4156-b295-f01516cd3b5c): Customer 1

Customer (7d788634-1cbf-4286-ad8f-60289621341c): Customer 2

Account (7e75dff6-05b9-4cf6-aaa1-701007d6c09c): £ 100,000.00 -> Account Of Customer ( Customer 1 )

Account (7e75dff6-05b9-4cf6-aaa1-701007d6c09c): £ 50,000.00 -> Account Of Customer ( Customer 2 )
