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

import java.util.HashSet;

import org.aprilis.jrest.store.Session;
import org.junit.Test;

public class TestSessionStore {

    public TestSessionStore() {
	moSessionStore = Session.instance();
	moSessionStore.clearAllSessions();
	
	mhsetRoles = new HashSet<String>();
	msSessionKey = null;

	mhsetRoles.add("Role1");
	mhsetRoles.add("Role2");
    }

    @Test
    public void testInstance() {
	if (moSessionStore == null) {
	    fail("Session store instance could not be created");
	}
    }

    @Test
    public void testClone() {
	try {
	    @SuppressWarnings("unused")
	    Session cloneSesStore = (Session) moSessionStore.clone();
	    fail("Session should not be cloneable, but has been cloned");
	} catch (Exception e) {
	}
    }

    @Test
    public void testRegisterSession() {
	msSessionKey = moSessionStore.registerSession(mhsetRoles);

	if (msSessionKey == null) {
	    fail("Register session failed. Returned null");
	}
	moSessionStore.deregisterSession(msSessionKey);
    }

    @Test
    public void testIsSessionValid() {
	if (moSessionStore.isSessionValid(null)) {
	    fail("Expected session to be invalid for null session key. Found valid session");
	}

	if (moSessionStore.isSessionValid("dummy")) {
	    fail("Expected session to be invalid for dummy session key. Found valid session");
	}

	msSessionKey = moSessionStore.registerSession(mhsetRoles);

	if (moSessionStore.isSessionValid(msSessionKey) == false) {
	    fail(String
		    .format("Expected session to be valid for valid session key [%s]. Found an Invalid session",
			    msSessionKey));
	}
	moSessionStore.deregisterSession(msSessionKey);
    }

    /*
     * Cannot be tested since session idle time is a constant set to 30 minutes. Cannot modify this
     * constant and neither can change the session start time. Has to be tested externally.
     * 
     * @Test public void testRemoveExpiredSessions() {
     * 
     * }
     */

    @Test
    public void testDeregisterSession() {

	moSessionStore.deregisterSession(null);
	if (moSessionStore.isSessionValid(null)) {
	    fail("Expected session to be invalid for null session key. Found a valid session");
	}

	msSessionKey = moSessionStore.registerSession(mhsetRoles);
	if (moSessionStore.isSessionValid(msSessionKey) == false) {
	    fail(String
		    .format("Expected session to be valid for valid session key [%s]. Found an Invalid session",
			    msSessionKey));
	}

	moSessionStore.deregisterSession(msSessionKey);
	if (moSessionStore.isSessionValid(msSessionKey) == true) {
	    fail(String
		    .format("Expected session to be invalid for deregistered session key [%s]. Found a valid session",
			    msSessionKey));
	}
    }

    @Test
    public void testIsRoleSetValid() {
	msSessionKey = moSessionStore.registerSession(mhsetRoles);

	HashSet<String> hsetTestRoles = new HashSet<String>();
	hsetTestRoles.add(null);
	if (moSessionStore.isRoleSetValid(msSessionKey, hsetTestRoles) == true) {
	    fail("Roleset containing null roles is valid. Expected invalid return value");
	}

	hsetTestRoles.remove(null);

	hsetTestRoles.add("InvalidRole");
	if (moSessionStore.isRoleSetValid(msSessionKey, hsetTestRoles) == true) {
	    fail("Roleset containing invalid roles is valid. Expected invalid return value");
	}

	hsetTestRoles.remove("InvalidRole");

	hsetTestRoles.add("Role1");
	if (moSessionStore.isRoleSetValid(msSessionKey, hsetTestRoles) == false) {
	    fail("Roleset containing valid roles is Invalid. Expected valid response");
	}
	moSessionStore.deregisterSession(msSessionKey);
    }

    @Test
    public void testGetRegisteredSessionsCount() {
	if (moSessionStore.getRegisteredSessionsCount() != 0) {
	    fail(String.format("Initial Expected registerd session count to be 0. Found [%d]",
		    moSessionStore.getRegisteredSessionsCount()));
	}
	
	msSessionKey = moSessionStore.registerSession(mhsetRoles);
	if (moSessionStore.getRegisteredSessionsCount() != 1) {
	    fail(String.format("Expected registerd session count to be 1. Found [%d]",
		    moSessionStore.getRegisteredSessionsCount()));
	}

	moSessionStore.deregisterSession(msSessionKey);
	if (moSessionStore.getRegisteredSessionsCount() != 0) {
	    fail(String.format("Expected registerd session count to be 0. Found [%d]",
		    moSessionStore.getRegisteredSessionsCount()));
	}
    }

    private Session moSessionStore;
    private HashSet<String> mhsetRoles;
    private String msSessionKey;

}
