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

import org.aprilis.jrest.compile.QueryBinder;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.store.Definition;
import org.aprilis.jrest.store.Store;
import org.junit.Test;

public class TestQueryBinder {

    public TestQueryBinder() {
	moDefinitionStore = Store.instance();
	moDefinitionStore.clearAllDefinitions();
	
	moQueryBinder = new QueryBinder();
	msBuiltQuery = null;

	Definition oDefinition = new Definition();
	oDefinition.setQuery("Get Query with no bind params;");
	moDefinitionStore.addGetDefinition("GetQueryWithNoBindParams", oDefinition);

	oDefinition = null;
	oDefinition = new Definition();
	oDefinition.setQuery("Get Query with 3 bind params. One = ? Two = ? Three = ?;");
	moDefinitionStore.addGetDefinition("GetQueryWithBindParams", oDefinition);

	oDefinition = null;
	oDefinition = new Definition();
	oDefinition.setQuery("Set Query with 2 bind params. One = ? Two = ? ;");
	moDefinitionStore.addSetDefinition("SetQueryWithBindParams", oDefinition);

	moDefinitionStore.addAuthenticationQuery("Auth query with no bind params");
    }

    @Test
    public void testBuildQueryForInvalidKey() {
	msBuiltQuery = moQueryBinder.buildQueryForKey(null, null, Constants.gshDefTypeGet);
	if (msBuiltQuery != null) {
	    fail(String.format("Expected BuildQuery for null key to return null. Returned [%s]",
		    msBuiltQuery));
	}

	msBuiltQuery = moQueryBinder.buildQueryForKey("InvalidKey", null, Constants.gshDefTypeGet);
	if (msBuiltQuery != null) {
	    fail(String.format("Expected BuildQuery for null key to return null. Returned [%s]",
		    msBuiltQuery));
	}
    }

    @Test
    public void testBuildQueryForKeyWithNullJsonData() {
	msBuiltQuery = moQueryBinder.buildQueryForKey("GetQueryWithNoBindParams", null, Constants.gshDefTypeGet);

	if (msBuiltQuery == null) {
	    fail("Expected non-null built query. Found null");
	} else if (msBuiltQuery.equals(moDefinitionStore.getDefinition("GetQueryWithNoBindParams",
		true).getQuery()) == false) {
	    fail(String.format("Build Query with no bind params failed. Expected [%s] Found [%s]",
		    moDefinitionStore.getDefinition("GetQueryWithNoBindParams", true).getQuery(),
		    msBuiltQuery));
	}
    }

    @Test
    public void testBuildQueryForKeyWithEmptyJsonData() {
	msBuiltQuery = moQueryBinder.buildQueryForKey("GetQueryWithNoBindParams", "", Constants.gshDefTypeGet);
	if (msBuiltQuery == null) {
	    fail("Expected non-null built query. Found null");
	} else if (msBuiltQuery.equals(moDefinitionStore.getDefinition("GetQueryWithNoBindParams",
		true).getQuery()) == false) {
	    fail(String.format("Build Query with no bind params failed. Expected [%s] Found [%s]",
		    moDefinitionStore.getDefinition("GetQueryWithNoBindParams", true).getQuery(),
		    msBuiltQuery));
	}
    }

    @Test
    public void testBuildQueryForKeyWithNoBindParams() {
	msBuiltQuery = moQueryBinder.buildQueryForKey("GetQueryWithNoBindParams", " DJD ", Constants.gshDefTypeGet);
	if (msBuiltQuery == null) {
	    fail("Expected non-null built query. Found null");
	} else if (msBuiltQuery.equals(moDefinitionStore.getDefinition("GetQueryWithNoBindParams",
		true).getQuery()) == false) {
	    fail(String.format("Build Query with no bind params failed. Expected [%s] Found [%s]",
		    moDefinitionStore.getDefinition("GetQueryWithNoBindParams", true).getQuery(),
		    msBuiltQuery));
	}
    }

    @Test
    public void testBuildQueryForGetKeyWithInsufficientBindParams() {
	String sJsonData = "{ \"1\" : \"One\"}";

	/* Get query with bind params. Last argument is set to true */
	msBuiltQuery = moQueryBinder.buildQueryForKey("GetQueryWithBindParams", sJsonData, Constants.gshDefTypeGet);
	if (msBuiltQuery != null) {
	    fail(String.format(
		    "Insufficient bind params passed to buildQuery. Expected null. Found [%s]",
		    msBuiltQuery));
	}
    }

    @Test
    public void testBuildQueryForSetKeyWithInsufficientBindParams() {
	String sJsonData = "{ \"1\" : \"One\"}";

	/* Set query with bind params. Last argument is set to false */
	msBuiltQuery = moQueryBinder.buildQueryForKey("SetQueryWithBindParams", sJsonData, Constants.gshDefTypeSet);
	if (msBuiltQuery != null) {
	    fail(String.format(
		    "Insufficient bind params passed to buildQuery. Expected null. Found [%s]",
		    msBuiltQuery));
	}
    }

    @Test
    public void testBuildQueryForGetKeyWithValidBindParams() {
	String sJsonData = "{ \"1\" : \"One\", \"2\" : \"Two\", \"3\" : \"Three\", \"4\" : \"Four\" }";

	/* Get query with bind params. Last argument is set to true */
	msBuiltQuery = moQueryBinder.buildQueryForKey("GetQueryWithBindParams", sJsonData, Constants.gshDefTypeGet);
	if (msBuiltQuery == null) {
	    fail("Valid bind params passed to buildQuery. Expected valid query. Found null");
	} else {
	    String sBoundQuery = "Get Query with 3 bind params. One = \'One\' Two = \'Two\' Three = \'Three\';";
	    if (msBuiltQuery.equals(sBoundQuery) == false) {
		fail(String.format("Bind Query params failed Expected [%s] Found [%s]",
			sBoundQuery, msBuiltQuery));
	    }
	}
    }

    @Test
    public void testBuildQueryForSetKeyWithValidBindParams() {
	String sJsonData = "{ \"1\" : \"One\", \"2\" : \"Two\", \"3\" : \"Three\", \"4\" : \"Four\" }";

	/* Set query with bind params. Last argument is set to false */
	msBuiltQuery = moQueryBinder.buildQueryForKey("SetQueryWithBindParams", sJsonData, Constants.gshDefTypeSet);
	if (msBuiltQuery == null) {
	    fail("Valid bind params passed to buildQuery. Expected valid query. Found null");
	} else {
	    String sBoundQuery = "Set Query with 2 bind params. One = \'One\' Two = \'Two\' ;";
	    if (msBuiltQuery.equals(sBoundQuery) == false) {
		fail(String.format("Bind Query params failed Expected [%s] Found [%s]",
			sBoundQuery, msBuiltQuery));
	    }
	}
    }

    @Test
    public void testBuildQueryForAuthWithNoBindParams() {
	msBuiltQuery = moQueryBinder.buildQueryForAuth(null);

	String sBoundQuery = "Auth query with no bind params";
	if (msBuiltQuery == null) {
	    fail(String.format(
		    "Build Query for auth with no bind params failed. Expected [%s]. Found null",
		    sBoundQuery));
	} else if (msBuiltQuery.equals(sBoundQuery) == false) {
	    fail(String.format(
		    "Build Query for auth with no bind params failed. Expected [%s]. Found [%s]",
		    sBoundQuery, msBuiltQuery));
	}
    }

    @Test
    public void testBuildQueryForAuthWithBindParams() {
	moDefinitionStore.addAuthenticationQuery("Auth query with 1 bind param = ?;");

	String sJsonData = "{ \"1\" : \"One\", \"2\" : \"Two\"}";
	String sBoundQuery = "Auth query with 1 bind param = 'One';";

	msBuiltQuery = moQueryBinder.buildQueryForAuth(sJsonData);

	if (msBuiltQuery == null) {
	    fail(String.format("Build Query for auth failed. Expected [%s]. Found null",
		    sBoundQuery));
	} else if (msBuiltQuery.equals(sBoundQuery) == false) {
	    fail(String.format("Build Query for auth failed. Expected [%s]. Found [%s]",
		    sBoundQuery, msBuiltQuery));
	}
    }

    private QueryBinder moQueryBinder;
    private Store moDefinitionStore;
    private String msBuiltQuery;

}
