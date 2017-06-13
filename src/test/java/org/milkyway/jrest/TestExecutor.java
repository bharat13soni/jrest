/*
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
package org.milkyway.jrest;

import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.milkyway.jrest.db.ConnectionDetails;
import org.milkyway.jrest.execute.Executor;
import org.milkyway.jrest.store.Store;
import org.junit.Test;

public class TestExecutor {

    public TestExecutor() {
	
	moExecutor = new Executor((short) 0);

	moDefinitionStore = Store.instance();
	moDefinitionStore.clearAllDefinitions();
	
	moConnDetails = new ConnectionDetails();
	moConnDetails.setDatabaseType("MySql");
	moConnDetails.setHostName("localhost");
	moConnDetails.setPortNumber("3306");
	moConnDetails.setDatabaseName("darwin");
	moConnDetails.setUserName("root");
	moConnDetails.setPassWord("xmc4vhcf");
	
	msQuery = "SELECT 'Hello' FROM DUAL;";
	msHelloString = "Hello";
    }

    /**
     * Test method for {@link org.milkyway.jrest.execute.Executor#initialize()}.
     */
    @Test
    public void testInitialize() {
	if (moExecutor.initialize() == true) {
	    fail("Executor initialize expected to fail, but it passed");
	}

	moDefinitionStore.setJdbcConnectionDetails(moConnDetails);
	
	if (moExecutor.initialize() == false) {
	    fail("Executor initialize expected to pass. But it failed. ");
	}
	
	moExecutor.unInitialize();
    }

    /**
     * Test method for {@link org.milkyway.jrest.execute.Executor#executeQuery(java.lang.String)}.
     */
    @Test
    public void testExecuteQuery() {
	moDefinitionStore.setJdbcConnectionDetails(moConnDetails);
	
	if (moExecutor.initialize() == false) {
	    fail("Executor initialize failed");
	}

	ResultSet rsQueryResult = moExecutor.executeQuery(msQuery);
	try {
	    if (rsQueryResult.next()) {
		String rsValue = rsQueryResult.getString(1);

		if (rsValue.equals(msHelloString) == false) {
		    fail(String
			    .format("Executor executeQuery return value mismatch. Expected [%s] Found [%s]",
				    msHelloString, rsValue));
		}
	    } else {
		fail("Executor could not execute query");
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	    fail("Executor resultset threw an exception");

	}

	moExecutor.unInitialize();
    }

    /**
     * Test method for {@link org.milkyway.jrest.execute.Executor#execute(java.lang.String)}.
     */
    @Test
    public void testExecute() {
	moDefinitionStore.setJdbcConnectionDetails(moConnDetails);
	
	if (moExecutor.initialize() == false) {
	    fail("Executor initialize failed");
	}

	moExecutor.execute("CREATE TABLE darwin.TEMPTABLE (COLUMN_ONE TEXT, COLUMN_TWO TEXT);");
	msQuery = "INSERT INTO TEMPTABLE VALUES('1', 'One');";
	if (moExecutor.execute(msQuery) == false) {
	    fail("testExecute failed");
	}

	moExecutor.execute("DROP TABLE TEMPTABLE;");
	moExecutor.unInitialize();
    }
    
    @Test
    public void testDisConnect() {
	moDefinitionStore.setJdbcConnectionDetails(moConnDetails);
	
	if (moExecutor.initialize() == false) {
	    fail("Executor initialize failed");
	}

	moExecutor.execute("CREATE TABLE darwin.TEMPTABLE (COLUMN_ONE TEXT, COLUMN_TWO TEXT);");
	msQuery = "INSERT INTO TEMPTABLE VALUES('1', 'One');";
	if (moExecutor.execute(msQuery) == false) {
	    fail("testDisConnect failed");
	}

	moExecutor.execute("DROP TABLE TEMPTABLE;");

	moExecutor.unInitialize();
	
	if (moExecutor.execute(msQuery) == true) {
	    fail("Executor execute after disconnect expected to fail. But it passed");
	}
    }

    private Store moDefinitionStore;
    private ConnectionDetails moConnDetails;
    private Executor moExecutor;
    private String msQuery;
    private String msHelloString;

}
