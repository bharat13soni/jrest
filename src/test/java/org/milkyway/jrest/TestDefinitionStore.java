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

import org.milkyway.jrest.store.Definition;
import org.milkyway.jrest.store.Store;
import org.junit.Test;

public class TestDefinitionStore {

    public TestDefinitionStore() {
	moDefinition = new Definition();
	moDefinitionStore = Store.instance();
	moDefinitionStore.clearAllDefinitions();
    }

    /**
     * Test method for {@link org.milkyway.jrest.store.Store#instance()}.
     */
    @Test
    public void testInstance() {
	if (moDefinitionStore == null) {
	    fail("Definition store instance could not be created");
	}
    }

    /**
     * Test method for {@link org.milkyway.jrest.store.Store#clone()}.
     */
    @Test
    public void testClone() {
	try {
	    @SuppressWarnings("unused")
	    Store cloneDefStore = (Store) moDefinitionStore.clone();
	    fail("Definition Store should not be cloneable, but has been cloned");
	} catch (Exception e) {
	}

    }

    /**
     * 
     */
    @Test
    public void testSetDefinition() {
	String sQuery = "Test Set Query";
	String sDefName = "TestSetDef";

	Definition testDefinition = moDefinitionStore.getDefinition(sDefName, false);
	if (testDefinition != null) {
	    fail("testSetDefinition failed. Return value expected to be null");
	}

	moDefinition.setQuery(sQuery);

	moDefinitionStore.addSetDefinition(sDefName, moDefinition);
	testDefinition = moDefinitionStore.getDefinition(sDefName, false);

	if (testDefinition.getQuery().equals(sQuery) == false) {
	    fail(String
		    .format("testSetDefinition failed. getQuery return value mismatch. Expected [%s] Found [%s]",
			    sQuery, testDefinition.getQuery()));
	}

	/*
	 * Definition of the same name should not exist for Type Get.
	 * Passing true as the value of the second argument to getDefinition.
	 */
	testDefinition = moDefinitionStore.getDefinition(sDefName, true);

	if (testDefinition != null) {
	    fail("testSetDefinition failed. Definition with same name should not exist for Type GET. But It exists");
	}

	/* This should replace the previous set definition */
	testDefinition = null;
	sQuery = "Updated Set Query";
	moDefinition.setQuery(sQuery);
	moDefinitionStore.addSetDefinition(sDefName, moDefinition);
	testDefinition = moDefinitionStore.getDefinition(sDefName, false);

	if (testDefinition.getQuery().equals(sQuery) == false) {
	    fail(String
		    .format("testSetDefinition failed. getQuery return value mismatch. Expected [%s] Found [%s]",
			    sQuery, testDefinition.getQuery()));
	}
    }

    /**
     * 
     */
    @Test
    public void testGetDefinition() {
	String sQuery = "Test Get Query";
	String sDefName = "TestGetDef";

	Definition testDefinition = moDefinitionStore.getDefinition(sDefName, true);
	if (testDefinition != null) {
	    fail("testGetDefinition failed. Return value expected to be null");
	}
	moDefinition.setQuery(sQuery);

	moDefinitionStore.addGetDefinition(sDefName, moDefinition);
	testDefinition = moDefinitionStore.getDefinition(sDefName, true);

	if (testDefinition.getQuery().equals(sQuery) == false) {
	    fail(String
		    .format("testGetDefinition failed. getQuery return value mismatch. Expected [%s] Found [%s]",
			    sQuery, testDefinition.getQuery()));
	}

	/*
	 * Definition of the same name should not exist for Type Set Passing false as the value of
	 * the second argument to getDefinition.
	 */
	testDefinition = moDefinitionStore.getDefinition(sDefName, false);

	if (testDefinition != null) {
	    fail(String
		    .format("testGetDefinition failed. Definition with same name should not exist for Type SET. But It exists. Def query = [%s]",
			    testDefinition.getQuery()));
	}

	/* This should replace the previous set definition */
	testDefinition = null;
	sQuery = "Updated Get Query";
	moDefinition.setQuery(sQuery);
	moDefinitionStore.addGetDefinition(sDefName, moDefinition);
	testDefinition = moDefinitionStore.getDefinition(sDefName, true);

	if (testDefinition.getQuery().equals(sQuery) == false) {
	    fail(String
		    .format("testGetDefinition failed. getQuery return value mismatch. Expected [%s] Found [%s]",
			    sQuery, testDefinition.getQuery()));
	}
    }

    /**
     * 
     */
    @Test
    public void testGetAuthenticationDefinition() {
	Definition authDefinition = moDefinitionStore.getAuthenticationDefinition();

	if (authDefinition.getQuery() != null) {
	    fail("Auth definition query Expected to be null. Found " + authDefinition.getQuery());
	}

	moDefinitionStore.addAuthenticationQuery("Auth Query");
	if (authDefinition.getQuery() == null) {
	    fail("Auth definition query Expected to be null");
	}

    }

    private Store moDefinitionStore;
    private Definition moDefinition;
}
