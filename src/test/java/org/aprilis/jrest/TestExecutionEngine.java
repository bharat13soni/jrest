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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.db.ConnectionDetails;
import org.aprilis.jrest.execute.ExecutionEngine;
import org.aprilis.jrest.execute.Executor;
import org.aprilis.jrest.store.Store;
import org.junit.Test;

public class TestExecutionEngine {

    public TestExecutionEngine() {
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

	moExecutionEngine = ExecutionEngine.instance();

	msQuery = "SELECT 'Hello' FROM DUAL;";
	msHelloString = "Hello";
    }

    /**
     * Test method for {@link org.aprilis.jrest.execute.ExecutionEngine#instance()}.
     */
    @Test
    public void testInstance() {
	if (moExecutionEngine == null) {
	    fail("Execution engine instance could not be created");
	}
    }

    /**
     * Test method for {@link org.aprilis.jrest.execute.ExecutionEngine#clone()}.
     */
    @Test
    public void testClone() {
	try {
	    @SuppressWarnings("unused")
	    ExecutionEngine cloneExeEngine = (ExecutionEngine) moExecutionEngine.clone();
	    fail("ExecutionEngine should not be cloneable, but has been cloned");
	} catch (Exception e) {
	}
    }

    /**
     * Test method for {@link org.aprilis.jrest.execute.ExecutionEngine#acquireExecutorFromPool()}.
     */
    @Test
    public void testAcquireExecutorFromPool() {
	moExecutor = moExecutionEngine.acquireExecutorFromPool();

	if (moExecutor == null) {
	    fail("acquireExecutorFromPool failed. Expected non-null executor. Found null");
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

	moExecutionEngine.releaseExecutorToPool(moExecutor);
    }

    @Test
    public void testAcquireAllExecutorsFromPool() {
	/*
	 * Execution engine creates a default pool of total size of
	 * Constants.gshDefaultMaxDbConnections. Acquire all the objects from the execution engine
	 * pool in which case the next call would result in a null object.
	 */

	LinkedList<Executor> listExecutors = new LinkedList<Executor>();
	for (short exeIndex = 0; exeIndex <= Constants.gshDefaultMaxDbConnections; exeIndex++) {
	    listExecutors.add(moExecutionEngine.acquireExecutorFromPool());
	}

	moExecutor = null;
	moExecutor = moExecutionEngine.acquireExecutorFromPool();
	if (moExecutor != null) {
	    fail("Executor object not null after exhausting all the executor objects from the pool");
	}

	moExecutionEngine.releaseExecutorToPool(moExecutor);
	while (!listExecutors.isEmpty()) {
	    moExecutionEngine.releaseExecutorToPool(listExecutors.remove());
	}
    }

    /**
     * Test method for {@link org.aprilis.jrest.execute.ExecutionEngine#releaseExecutorToPool()}.
     */
    @Test
    public void testReleaseExecutorToPool() {
	LinkedList<Executor> listExecutors = new LinkedList<Executor>();
	for (short exeIndex = 0; exeIndex <= Constants.gshDefaultMaxDbConnections; exeIndex++) {
	    listExecutors.add(moExecutionEngine.acquireExecutorFromPool());
	}

	moExecutor = null;
	moExecutor = moExecutionEngine.acquireExecutorFromPool();
	if (moExecutor != null) {
	    fail("Executor object not null after exhausting all the executor objects from the pool");
	}

	moExecutionEngine.releaseExecutorToPool(listExecutors.remove());
	moExecutor = null;
	moExecutor = moExecutionEngine.acquireExecutorFromPool();
	if (moExecutor == null) {
	    fail("Executor object expected NOT to be null");
	}

	moExecutionEngine.releaseExecutorToPool(moExecutor);
	while (!listExecutors.isEmpty()) {
	    moExecutionEngine.releaseExecutorToPool(listExecutors.remove());
	}
    }

    private ExecutionEngine moExecutionEngine;
    private Executor moExecutor;
    private Store moDefinitionStore;
    private String msQuery;
    private String msHelloString;
}
