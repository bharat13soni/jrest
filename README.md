# JSON + RESTful = JRest 
Is a Meta programming language which converts the intentions of data access and manipulation against a database table in the form of SQL statement into key based RESTful service.  JRest makes a complete departure from the standards of the RESTful services, as there are no URI to publish by the user! Entire JRest revolves around these types of URIs, /login, /pull, /push, /pull/adhoc and /push/adhoc.

RESTful services paradigm stands out in the current trend and many a frameworks have been put in place for the same.  In a typical application or service, 70 to 80 percent of the code is written just to make IO operations to one or other database; this leads into very tedious and monotonus development process often time consuming and error prone. Using JRest, the time taken to publish a RESTful web service is pretty much equal to the time taken to write the SQL query for the same.

Further, JRest is offers inline updation to RESTful services.  What this means is that you as a developer, don't have to restart the webserver everytime you add or modify a REST API!

# Release 1.1.0
NEW! overriding definition and adhoc/anonymous querying facility added based on the feedback/request/idea by @twoxfh.  With v1.1 it is possible to override SQL statement submitted via definition file ADHOC_SQL paramerter momentarily.  This feature is extended to both pull and push services, to access them just add /adhoc to the end of pull or push (e.g. http://localhost:8080/jrest/pull/adhoc or http://localhost:8080/jrest/push/adhoc).
    Sample GET call header parameters are below, the same is true for a POST (aka SET type of call too)

    SESSION_KEY : 1C79B373DC35B732048DD66FC21669F
    JREST_KEY   : get_all_groups_for_user
    JSON_DATA   : {"1":"66e64f0f-9e88-11e6-957c-fc3fdbf9364f"}
    ADHOC_SQL   : SELECT id, name, members, membercount, createdon, modifiedon FROM groups WHERE uuid = ?;

Overriding helps in replacing the default SQL statement associated with the JREST_KEY momentarily, yet without compromising on the before or after facilities.  Adhoc call does not mandate that there must be a JREST_KEY associated or to be used for the call.  One can simply supply null for the call and get their statement executed on the webserver. An example is listed below, however, remember that supplying JREST_KEY without any value is must otherwise call will return 413 as status code.

    SESSION_KEY : 1C79B373DC35B732048DD66FC21669F
    JREST_KEY   : <empty-string>
    JSON_DATA   : {"1":"66e64f0f-9e88-11e6-957c-fc3fdbf9364f"}
    ADHOC_SQL   : SELECT id, name, members, membercount, createdon, modifiedon FROM groups WHERE uuid = ?;

By allowing to overload SQL associated with JREST_KEY now developers can take advantage of any FCQN java methods associated with a JREST_KEY without ever publishing a definition.  This is great for getting ideas tested quickly. JRest may never publish a override to adhoc in future to support custom Java FCQN function support via before or after due the security implications they pose for the entire system.

> PS: PLEASE DO NOT FORGET TO ADD ; (SEMICOLON) AT THE END OF YOUR ADHOC_SQL STATEMENT.

One more thing! Now to use JRest simply download the jrest.war file under the war directory of the repository and place it under your webapps directory of tomcat.

# The Problem 
In a typical web based application development, much of the time is spent in developing CRUD (Create, Read, Update, Delete) APIs for the database tables.  This itself amounts to almost eighty percent of the code base for any application, quite often a repeatative task, however a great deal of time would be spent in publishing a stable code base for the same.  On the other hand, providing these RESTful Web Service (RWS) securely and implementing a role based access takes a great deal of time.
As result, majority of the projects, instead of solving the actual customer problem or offering innovative solution, spends great deal of time in battling out the publishing of RWS itself. 

# JRest Solution
We have developed a meta language with compiler called JRest that can be started with any webserver as a container, onto which webservice definitions can be submitted using definition files written in JSON. JSON as definition language came from the simplicity and its inherent roots to JavaScript. JRest (JR) classifies the RESTful interactions into auth, pull and push types, whereby, auth is used for authentication, pull is used for all the GET type of calls and push is used for POST and PUT type.  All RWS interactions would eventually be structured into these three types of calls. JRest produces all the response results in only JSON format as of now and can be easily extended to XML too.  All interactions with the JRest services rely on the HEADER parameters of the HTTP request alone. 

JRest has mainly two flavors, full version i.e. authentication and session subsystem built into it and lite version without the session management; lite being a great choice if an application has its own mechanism of authentication and session management. 

# 5 mins guide to lift off!
1. Download the source code
2. Stop your web server now, if you are running one! we will start it back in 5 minutes
3. To compile the source, you must have Maven already installed along with Java
4. To generate the war file from source code, go to the j-rest folder and execute mvn install. This should generate the war file which you need to place it under the webapps folder of your webserver.
5. Set an environment variable by the name <b>JREST_DEFINITION_PATH</b> to any of your favorite path. Depending on your platform you may need to reopen/restart the shell/command prompt
6. To work with Oracle and Sql Server you need to have their jdbc drivers installed and accessible on <b>CLASSPATH</b>.
7. Make sure you have/create a table called User on your database, with username and password columns present in them.
8. Now move into JREST_DEFINITION_PATH, and open a new file jrest.json in edit mode, and fill in the details given below (replace the values accordingly)

		{
			"AUTH" : {
				"Query":"Select -3022 From User Where username = ? and password = PASSWORD(?);"
			}
		}
		!
		{
			"JDBC" : {
				"Host" : "<hostname>",
				"Port" : "<database port>",
				"User" : "<username>",
				"Pass" : "<password>",
				"Db"   : "<database/schema name>",
				"Type" : "MySql/PostgreSql/SQLServer/Oracle"
			}
		}
9. Now open another file users.json in edit mode in JREST_DEFINITION_PATH and put the contents given below

		{
	        "Users" : {
				"Query" : "Select username, name, password From User;",
				"Type" : "GET"
    	    }
   		}
10. Now start your web server or execute mvn jetty:run on the shell prompt (you must be inside the j-rest directory where you have uncompressed the JRest source) <br>
11. Observe the output of web server; your definition files must have loaded successfully. Your output should be something similar to following
	
		2013-02-10 16:23:00,705 [Thread-6] DEBUG org.milkyway.jrest.compile.Compile - Default role value [[-3022]] added to JRest Key [UA4]
		2013-02-10 16:23:00,707 [Thread-6] INFO  org.milkyway.jrest.compile.Compile - Trimmed JSON string is[{"AUTH":{"Query":"Select -3022 From Darwin.User Where username = ? and password = PASSWORD(?);"}}]
		2013-02-10 16:23:00,708 [Thread-6] DEBUG org.milkyway.jrest.store.Store - Definition SQL Query is [Select -3022 From Darwin.User Where username = ? and password = PASSWORD(?);]
		2013-02-10 16:23:00,709 [Thread-6] INFO  org.milkyway.jrest.compile.Compile - Trimmed JSON string is[{"JDBC":{"Host":"localhost","Port":"3306","User":"root","Pass":"xmc4vhcf","Db":"Darwin","Type":"MySql"}}]
		2013-02-10 16:23:00,732 [Thread-6] INFO  org.milkyway.jrest.compile.Compile - Trimmed JSON string is[{"Users":{"Query":"Select username, name, password From Darwin.User;","Type":"GET"}}]
		2013-02-10 16:23:00,733 [Thread-6] DEBUG org.milkyway.jrest.compile.Compile - Default role value [[-3022]] added to JRest Key [Users]
		Feb 10, 2013 4:23:01 PM com.sun.jersey.api.core.PackagesResourceConfig init
		INFO: Scanning for root resource and provider classes in the packages:
		org.milkyway.jrest 
		Feb 10, 2013 4:23:02 PM com.sun.jersey.api.core.ScanningResourceConfig logClasses
		INFO: Root resource classes found:
		class org.milkyway.jrest.pull.Pull
		class org.milkyway.jrest.push.Push 
		class org.milkyway.jrest.auth.Authentication 
	
12. Make sure you have Postman plugin for Google Chrome or REST Client extension for Firefox; this is needed to test the REST service. <br>
13. Create a HTTP POST request with the URL http://localhost:8080/jrest/login and pass the authentication details in Header params with JSON_DATA as the key and {"1":"d", "2":"d"} as the value. <br>
14. Pay attention to the header parameters; we have placed {"1":"d", "2":"d"} as JSON_DATA for the call. 1 and 2 in the actual JSON data represents the positions on the Query given as part of AUTH string ("Select -3022 From Darwin.User Where username = ? and password = PASSWORD(?);". The value "d" of key "1" is supplemented to username and "d" (another 'd') of key "2" is supplemented password of the SQL statement. <br>
15. You should also get a session key of length 32 bytes as a reply to login; we need that key for every other call that we are going to make for JRest, keep it copied to some place.  <br>
16. There you go! You have successfully interacted with your webserver using j-rest.  <br>
17. On the same lines, execute other definitions. The information needed in case of definitions other than AUTH is that the HTTP request should contain the following information in the Header params <br>

		JREST_KEY : Definition key
		SESSION_KEY : Session key received from login HTTP request
		JSON_DATA : Any JSON data required by the definition
	
# Design and Philosophy 
JRest consists of a meta language compiler, session manager, execution engine, definition store and offers three types of HTTP interactions (auth, pull and push). JRest is case sensitive just like C or C++, hence definitions can mean differently with case changed.  Successfully compiled definition files are stored in memory within definition store and also a copy is maintained on the file system which is guided by $JREST_DEFINITION_PATH env variable. 

A permanent copy is maintained within .jrest directory inside $JREST_DEFINITION_PATH. This path is used for reloading the definitions upon webserver restarts.  Internally definitions of type push and pull are stored within the definition store on separate caches allowing the user to have same definition name for both the types. JRest also monitors the definition path for any new definitions that are to be uploaded at intervals set on $JREST_REFRESH_INTERVAL. The number of database connections that JRest opens can be limited through $JREST_DB_MAX_CONNECTIONS.  However, apart from JREST_DEFINITION_PATH rest all are optional. Loaded definitions can be updated by placing new definition files in JREST_DEFINITION_PATH.  Changes are reflected within ($JREST_REFRESH_INTERVAL * 2) seconds automatically.

JRest depends on a mandatory definition file jrest.json for authentication and database connectivity; is reserved for only AUTH and JDBC definitions only, any other definitions found in this file are ignored by default.  The adjacent figure shows a sample jrest.json file.

The AUTH query is expected to return roles that are assigned to the user either in CSV or single value in multiple rows.  These role values are later used to grant or restrict the access to a particular JRest definition.  Incase the user is not interested in role based authentication then it is expected that the query returns -3022 as a value.  A bang (!) is used for separating two definitons within a file.

Once the authentication and database access is specified for JRest, publishing the RWS is quite easy. The following table summarizes two such sample definitions.  The positional parameters specified by “?” are replaced with the values supplied through request header when a JRest RWS is called.  These values must be passed as JSON strings with position numbers and values for the header param key JSON_DATA within header param.  For example  “get_user_details” definitions JSON_DATA must be {“1”:”xys”}. 

Logging in JRest is through log4j, enabling the users to integrate a common logging framework across the containers if desired. 

# Custom business logic!
Not everything in web application is SQL and GET/SET operations on the table. To support custom business logic code in any application, JRest offers two execution hooks, Before and After; user can call a regular Java public method of a class before or after or both, while invoking JRest RWS. The extra data that are to be passed to these APIs can packed on to same JSON_DATA as extra offsets.

Before API consumes a request parameter’s data and may produce a result that JRest RWS may consume. “Consume” key set to true for Before implies that JRest RWS should consume the output of the Java API configured in “Before”, else original data is passed as is to RWS. “After” API receives 3 set of data for its operation; the original JSON_DATA, output from Before call if used, and output of JRest RWS. 

The final response JSON is emitted by “After” API.  This mandates the API design for Before call to have single String parameter and After API design three String parameters. The following figure shows the internal execution path taken by JRest, wherein, ip represents input data and RS represents result set.
Since JRest ties all the definitions to a single database given in the jrest.json, different projects having varied databases or access rights must host JRest in separate instances of webserver.

	{
		"UA3" : {
			"Query" : "Select username, name, password From Darwin.User;",
			"Type" : "GET",
			"After" : {
				"FQCN" : "org.milkyway.sample.TestBeforeAfter",
				"Method" : "sayHelloAfter"
			}
		}
	}
	!
	{
		"UA4" : {
			"Query" : "Select username, name, password From Darwin.User;",
			"Type" : "GET",
			"Before" : {
				 "FQCN" : "org.milkyway.sample.TestBeforeAfter",
				 "Method" : "sayHelloBefore",
				 "Consume" : "t"
			},
			"After" : {
				 "FQCN" : "org.milkyway.sample.TestBeforeAfter",
				 "Method" : "sayHelloAfter"
			}
		}
	}


# License
JRest has been open sourced under MIT license.

# Benefits
The biggest advantage with JRest against any other solution is developers don't have to write RESTful services spending loads of time. Other benefits include its low RAM footprint and O(1) load and execute time for a definition. In a typical RWS environment, all the class files that are loaded to the webserver occupy GBs of memory thus increasing the GC activity; on the other hand JRest uses execution pool and executors to which requests are dynamically injected with the definition that lower RAM need.  Our early assessment project that was ported from traditional Jersey RWS to JRest lowered the memory requirement from ~1.7GB to 128MB, and super low GC activity.

# Competitive approaches
Most of the competitive approaches are developed in a style to auto generate the necessary RWS code. Such solutions tend to become complex over a period of time.  JRest offers extremely simple solution in this angle. 
