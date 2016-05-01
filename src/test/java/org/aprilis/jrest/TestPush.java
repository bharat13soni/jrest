/*
 * Copyright 2013 JRest Foundation and other contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.aprilis.jrest;

import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;

import org.aprilis.jrest.auth.Authentication;
import org.aprilis.jrest.constant.HttpCodes;
import org.aprilis.jrest.db.ConnectionDetails;
import org.aprilis.jrest.execute.Executor;
import org.aprilis.jrest.push.Push;
import org.aprilis.jrest.store.Definition;
import org.aprilis.jrest.store.Session;
import org.aprilis.jrest.store.Store;
import org.junit.Test;

public class TestPush {

    public TestPush() {
	moDefinitionStore = Store.instance();
	moDefinitionStore.clearAllDefinitions();
	
	ConnectionDetails dbConnDetails = new ConnectionDetails();
	dbConnDetails.setDatabaseType("MySql");
	dbConnDetails.setHostName("localhost");
	dbConnDetails.setPortNumber("3306");
	dbConnDetails.setDatabaseName("darwin");
	dbConnDetails.setUserName("root");
	dbConnDetails.setPassWord("xmc4vhcf");

	moDefinitionStore.setJdbcConnectionDetails(dbConnDetails);

	moSessionStore = Session.instance();
	moSessionStore.setSystemToReadyState();

	moPush = new Push();
	moResponse = null;
	msSessionKey = null;

	msJsonData = "{ \"1\" : \"One\", \"2\" : \"Two\"}";
	msJrestKey = "TEST_PUSH";
	moPushDefinition = null;
	moAuthentication = null;
	msDefaultRole = "-3022";
    }

    private void authenticate() {
	moAuthentication = new Authentication();
	moDefinitionStore.addAuthenticationQuery("SELECT '-3022' FROM dual;");
	moResponse = moAuthentication.login(msJsonData);
	msSessionKey = moResponse.getEntity().toString();
    }

    private void createTempTable() {

	Executor executor = new Executor((short) 0);
	executor.initialize();

	executor.execute("CREATE TABLE darwin.TEMPTABLE (COLUMN_ONE TEXT, COLUMN_TWO TEXT);");
	executor.unInitialize();
    }

    private void dropTempTable() {

	Executor executor = new Executor((short) 0);
	executor.initialize();

	executor.execute("DROP TABLE TEMPTABLE;");
	executor.unInitialize();
    }

    @Test
    public void testInvalidSession() {
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);

	if (moResponse.getStatus() != HttpCodes.FORBIDDEN) {
	    fail("testInvalidSession failed. Expected " + HttpCodes.FORBIDDEN + " Found "
		    + moResponse.getStatus());
	}
    }

    @Test
    public void testNonExistentKey() {
	authenticate();
	msJrestKey = "NON_EXISTENT_KEY";
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);

	if (moResponse.getStatus() != HttpCodes.NOT_FOUND) {
	    fail("testNonExistentKey failed. Expected " + HttpCodes.NOT_FOUND + " Found "
		    + moResponse.getStatus());
	}
    }

    @Test
    public void testRoleVerification() {
	authenticate();
	createTempTable();

	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1, 'One'");
	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);

	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.FORBIDDEN) {
	    fail("testRoleVerification failed. Expected " + HttpCodes.FORBIDDEN + " Found "
		    + moResponse.getStatus());
	}
    }

    @Test
    public void testQuerySyntaxError() {
	authenticate();

	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1, 'One'");
	moPushDefinition.addRole(msDefaultRole);
	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);

	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.UNPROCESSABLE_ENTITY) {
	    fail("testQuerySyntaxError failed. Expected " + HttpCodes.UNPROCESSABLE_ENTITY
		    + " Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testQueryNoBindParam() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', 'One');");
	moPushDefinition.addRole(msDefaultRole);
	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);

	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testQueryNoBindParam failed. Expected " + HttpCodes.OK + " Found "
		    + moResponse.getStatus());
	}
    }

    @Test
    public void testQueryWithBindParam() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', ?);");
	moPushDefinition.addRole(msDefaultRole);
	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);

	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testQueryWithBindParam failed. Expected " + HttpCodes.OK + " Found "
		    + moResponse.getStatus());
	}

    }

    @Test
    public void testQueryWithBindParamMismatch() {

	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', ?);");
	moPushDefinition.addRole(msDefaultRole);
	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);

	msJsonData = "{ \"2\" : \"Hello\"}";
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.UNPROCESSABLE_ENTITY) {
	    fail("testQueryWithBindParamMismatch failed. Expected "
		    + HttpCodes.UNPROCESSABLE_ENTITY + " Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testBeforeOnly() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', ?);");
	moPushDefinition.addRole(msDefaultRole);
	moPushDefinition.setFqcnBefore("org.aprilis.sample.TestBeforeAfter");
	moPushDefinition.setBeforeMethod("sayHelloBefore");

	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testBeforeOnly failed. Expected " + HttpCodes.OK + " Found "
		    + moResponse.getStatus());
	}
    }

    @Test
    public void testBeforeThrowsException() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', ?);");
	moPushDefinition.addRole(msDefaultRole);
	moPushDefinition.setFqcnBefore("org.aprilis.sample.TestBeforeAfter");
	moPushDefinition.setBeforeMethod("beforeThrowsException");

	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.PRECONDITION_FAILURE) {
	    fail("testBeforeThrowsException failed. Expected return status "
		    + HttpCodes.PRECONDITION_FAILURE + " Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testAfterThrowsException() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', ?);");
	moPushDefinition.addRole(msDefaultRole);
	moPushDefinition.setFqcnAfter("org.aprilis.sample.TestBeforeAfter");
	moPushDefinition.setAfterMethod("afterThrowsException");

	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.EXPECTATION_FAILED) {
	    fail("testAfterThrowsException failed. Expected return status "
		    + HttpCodes.EXPECTATION_FAILED + " Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testAfterOnly() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', ?);");
	moPushDefinition.addRole(msDefaultRole);
	moPushDefinition.setFqcnAfter("org.aprilis.sample.TestBeforeAfter");
	moPushDefinition.setAfterMethod("sayHelloAfter");

	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testAfterOnly failed. Expected " + HttpCodes.OK + " Found "
		    + moResponse.getStatus());
	}
    }

    @Test
    public void testBeforeAndAfter() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES(?, ?);");
	moPushDefinition.addRole(msDefaultRole);
	moPushDefinition.setFqcnBefore("org.aprilis.sample.TestBeforeAfter");
	moPushDefinition.setBeforeMethod("sayHelloBefore");
	moPushDefinition.setFqcnAfter("org.aprilis.sample.TestBeforeAfter");
	moPushDefinition.setAfterMethod("sayHelloAfter");

	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testBeforeAndAfter failed. Expected " + HttpCodes.OK + " Found "
		    + moResponse.getStatus());
	}
    }

    @Test
    public void testGarbageJsonData() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("INSERT INTO TEMPTABLE VALUES('1', ?);");
	moPushDefinition.addRole(msDefaultRole);
	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);

	msJsonData = "{ \"Hi\" : \"Hello\"}";
	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.UNPROCESSABLE_ENTITY) {
	    fail("testQueryWithBindParamMismatch failed. Expected "
		    + HttpCodes.UNPROCESSABLE_ENTITY + " Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testUpdateZeroRows() {
	authenticate();
	moPushDefinition = new Definition();
	moPushDefinition.setQuery("UPDATE TEMPTABLE SET COLUMN_TWO = ? WHERE COLUMN_ONE = ?;");
	moPushDefinition.addRole(msDefaultRole);
	moDefinitionStore.addSetDefinition(msJrestKey, moPushDefinition);

	moResponse = moPush.executePush(msSessionKey, msJrestKey, msJsonData);
	if (moResponse.getStatus() != HttpCodes.UNPROCESSABLE_ENTITY) {
	    fail("testQueryWithBindParamMismatch failed. Expected "
		    + HttpCodes.UNPROCESSABLE_ENTITY + " Found " + moResponse.getStatus());
	} else if (moResponse.getEntity() != null) {
	    System.out
		    .println("testUpdateZeroRows response = " + moResponse.getEntity().toString());
	}

	/*
	 * NOTE: To be added in the last test case to drop the temp table
	 */
	dropTempTable();
    }

    private Push moPush;
    private Definition moPushDefinition;
    private String msJrestKey;
    private Authentication moAuthentication;
    private Store moDefinitionStore;
    private Session moSessionStore;
    private String msSessionKey;
    private Response moResponse;
    private String msJsonData;
    private String msDefaultRole;
}
