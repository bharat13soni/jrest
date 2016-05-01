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
import org.aprilis.jrest.store.Session;
import org.aprilis.jrest.store.Store;
import org.junit.Test;

public class TestAuthentication {

    public TestAuthentication() {
	moDefinitionStore = Store.instance();
	moDefinitionStore.clearAllDefinitions();

//	try {
//	    InputStream stream = Store.class.getResourceAsStream("/jdbc.json");
//	    if (stream == null)
//		stream = Store.class.getClassLoader().getResourceAsStream("jdbc.json");
//	    Scanner defFileScanner = new Scanner(stream);
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}

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

	moAuthentication = new Authentication();
	moResponse = null;
	msSessionKey = null;

	msJsonData = "{ \"1\" : \"One\", \"2\" : \"Two\"}";
    }

    /**
     * Test method for {@link org.aprilis.jrest.auth.Authentication#login(java.lang.String)}.
     */
    @Test
    public void testLoginWithSessionStore() {
	moSessionStore.setSystemToHaltState();

	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.SERVICE_UNAVAILABLE) {
	    fail("testLoginWithSessionStore failed. Expected status "
		    + HttpCodes.SERVICE_UNAVAILABLE + " Found " + moResponse.getStatus());
	}

	moSessionStore.setSystemToReadyState();
	moResponse = moAuthentication.login(msJsonData);
	if (moResponse.getStatus() == HttpCodes.SERVICE_UNAVAILABLE) {
	    fail("testLoginWithSessionStore failed. Expected any other status than "
		    + HttpCodes.SERVICE_UNAVAILABLE + " But found the same.");
	}
    }

    @Test
    public void testLoginWithNullAuthQuery() {

	moDefinitionStore.addAuthenticationQuery("SELECT -3022 FROM dual WHERE 'One' = ?;");
	msJsonData = "{ \"Hi\" : \"Hello\"}";
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.UNPROCESSABLE_ENTITY) {
	    fail("testLoginWithNullAuthQuery failed. Expected status "
		    + HttpCodes.UNPROCESSABLE_ENTITY + " Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testLoginQuerySyntaxError() {
	moDefinitionStore.addAuthenticationQuery("SELECT -3022 FROM dual WHERE 'One = ?;");
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.INTERNAL_SERVER_ERROR) {
	    fail("testLoginQuerySyntaxError failed. Expected " + HttpCodes.INTERNAL_SERVER_ERROR
		    + " return status. Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testLoginQueryWithNoBindParams() {
	moDefinitionStore.addAuthenticationQuery("SELECT -3022 FROM dual;");
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testLoginQueryWithNoBindParams failed. Expected " + HttpCodes.OK
		    + " return status. Found " + moResponse.getStatus());
	} else {
	    msSessionKey = moResponse.getEntity().toString();
	    if (msSessionKey == null) {
		fail("testLoginQueryWithNoBindParams failed. Session key is null");
	    }
	}
    }

    @Test
    public void testLoginQueryWithBindParams() {
	moDefinitionStore.addAuthenticationQuery("SELECT -3022 FROM dual WHERE 'One' = ?;");
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testLoginQueryWithBindParams failed. Expected " + HttpCodes.OK
		    + " return status. Found " + moResponse.getStatus());
	} else {
	    msSessionKey = moResponse.getEntity().toString();
	    if (msSessionKey == null) {
		fail("testLoginQueryWithBindParams failed. Session key is null");
	    }
	}
    }

    @Test
    public void testLoginQueryWithMoreBindParams() {
	moDefinitionStore
		.addAuthenticationQuery("SELECT -3022 FROM dual WHERE 'One' = ? AND 'Two' = ?;");
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testLoginQueryWithMoreBindParams failed. Expected " + HttpCodes.OK
		    + " return status. Found " + moResponse.getStatus());
	} else {
	    msSessionKey = moResponse.getEntity().toString();
	    if (msSessionKey == null) {
		fail("testLoginQueryWithMoreBindParams failed. Session key is null");
	    }
	}
    }

    @Test
    public void testLoginQueryDelimiter() {
	moDefinitionStore.addAuthenticationDelimiter('#');
	moDefinitionStore
		.addAuthenticationQuery("SELECT '1#2' FROM dual WHERE 'One' = ? AND 'Two' = ?;");
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testLoginQueryDelimiter failed. Expected " + HttpCodes.OK
		    + " return status. Found " + moResponse.getStatus());
	} else {
	    msSessionKey = moResponse.getEntity().toString();
	    if (msSessionKey == null) {
		fail("testLoginQueryDelimiter failed. Session key is null");
	    }
	}
    }

    @Test
    public void testGarbageJsonData() {
	moDefinitionStore
		.addAuthenticationQuery("SELECT -3022 FROM dual WHERE 'One' = ? AND 'Two' = ?;");

	msJsonData = "{ \"Dummy\" : \"dummay\", \"Test\" : \"test\"}";
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.UNPROCESSABLE_ENTITY) {
	    fail("testGarbageJsonData failed. Expected " + HttpCodes.UNPROCESSABLE_ENTITY
		    + " return status. Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testLoginUnAuthorizedUser() {

	moDefinitionStore.addAuthenticationQuery("SELECT -3022 FROM dual WHERE 'Won' = ?;");
	moResponse = moAuthentication.login(msJsonData);

	if (moResponse.getStatus() != HttpCodes.UNAUTHORIZED) {
	    fail("testLoginUnAuthorizedUser failed. Expected " + HttpCodes.UNAUTHORIZED
		    + " return status. Found " + moResponse.getStatus());
	}
    }

    /**
     * Test method for {@link org.aprilis.jrest.auth.Authentication#logoff(java.lang.String)}.
     */
    @Test
    public void testLogoffInHaltSystemState() {
	moSessionStore.setSystemToHaltState();

	moResponse = moAuthentication.logoff(msSessionKey);

	if (moResponse.getStatus() != HttpCodes.SERVICE_UNAVAILABLE) {
	    fail("testLogoffInHaltSystemState failed. Expected status "
		    + HttpCodes.SERVICE_UNAVAILABLE + " Found " + moResponse.getStatus());
	}
    }

    @Test
    public void testLogoffWithNullSessionKey() {
	moResponse = moAuthentication.logoff(null);

	if (moResponse.getStatus() != HttpCodes.OK) {
	    fail("testLogoffInHaltSystemState failed. Expected status " + HttpCodes.OK + " Found "
		    + moResponse.getStatus());
	}
    }

    private Authentication moAuthentication;
    private Store moDefinitionStore;
    private Session moSessionStore;
    private String msSessionKey;
    private Response moResponse;
    private String msJsonData;

}
