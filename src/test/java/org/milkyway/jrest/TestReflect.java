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

import org.milkyway.jrest.compile.Reflect;
import org.milkyway.jrest.store.Definition;
import org.junit.Test;

public class TestReflect {

    public TestReflect() {
	moReflect = new Reflect();
	moDefinition = new Definition();
	msBeforeMethodReturnValue = null;
	msAfterMethodReturnValue = null;
	msJsonData = "JSON_DATA";
    }

    /**
     * Test method for {@link org.milkyway.jrest.compile.Reflect#executeBeforeMethod()}.
     */
    @Test
    public void testExecuteReflectWithNoDefinition() {
	try {
	    msBeforeMethodReturnValue = moReflect.executeBeforeMethod();

	    if (msBeforeMethodReturnValue != null) {
		fail("Reflect executeBeforeMethod failed. Expected null. Found non-null result");
	    }

	    msAfterMethodReturnValue = moReflect.executeAfterMethod(msBeforeMethodReturnValue);

	    if (msAfterMethodReturnValue != null) {
		fail("Reflect executeAfterMethod failed. Expected null. Found non-null result");
	    }
	} catch (Exception e) {
	    e.printStackTrace();

	    fail("Reflect executeBeforeMethod threw an Exception");
	}
    }

    /**
     * Test method for {@link org.milkyway.jrest.compile.Reflect#executeBeforeMethod()}.
     */
    @Test
    public void testExecuteBeforeMethod() {
	moDefinition.setFqcnBefore("org.milkyway.sample.TestBeforeAfter");
	moDefinition.setBeforeMethod("sayHelloBefore");

	moReflect.setDefinition(moDefinition);
	moReflect.setRestJsonData(msJsonData);

	try {
	    msBeforeMethodReturnValue = moReflect.executeBeforeMethod();

	    if (msBeforeMethodReturnValue == null) {
		fail("Reflect executeBeforeMethod failed. Expected non-null. Found null result");
	    } else if (msBeforeMethodReturnValue.equals(msJsonData) == false) {
		fail(String.format("Reflect executeBeforeMethod failed. Expected [%s]. Found [%s]",
			msJsonData, msBeforeMethodReturnValue));
	    }
	} catch (Exception e) {

	    e.printStackTrace();

	    fail("testExecuteBeforeMethod threw an Exception");
	}
    }

    /**
     * Test method for
     * {@link org.milkyway.jrest.compile.Reflect#executeAfterMethod(java.lang.String)}.
     */
    @Test
    public void testExecuteAfterMethod() {
	moDefinition.setFqcnAfter("org.milkyway.sample.TestBeforeAfter");
	moDefinition.setAfterMethod("sayHelloAfter");

	moReflect.setDefinition(moDefinition);
	moReflect.setRestJsonData(msJsonData);

	try {
	    msAfterMethodReturnValue = moReflect.executeAfterMethod("JSON_RESULT");

	    if (msAfterMethodReturnValue == null) {
		fail("Reflect executeAfterMethod failed. Expected non-null. Found null result");
	    } else if (msAfterMethodReturnValue.equals("JSON_RESULT") == false) {
		fail(String.format("Reflect executeAfterMethod failed. Expected [%s]. Found [%s]",
			msJsonData, msAfterMethodReturnValue));
	    }
	} catch (Exception e) {

	    e.printStackTrace();

	    fail("testExecuteAfterMethod threw an Exception");
	}
    }

    @Test
    public void testWrongBeforeMethod() {
	moDefinition.setFqcnBefore("org.milkyway.sample.TestBeforeAfter");
	moDefinition.setBeforeMethod("wrongMethod");

	moReflect.setDefinition(moDefinition);
	moReflect.setRestJsonData(msJsonData);

	try {
	    msBeforeMethodReturnValue = moReflect.executeBeforeMethod();

	    if (msBeforeMethodReturnValue != null) {
		fail("testWrongMethod failed. Expected to throw an excpetion. Found non-null result");
	    }
	} catch (NoSuchMethodException e) {
	    moDefinition.setFqcnAfter("org.milkyway.sample.TestBeforeAfter");
	    moDefinition.setAfterMethod("WrongMethod");

	    moReflect.setDefinition(moDefinition);
	    moReflect.setRestJsonData(msJsonData);

	    try {
		msAfterMethodReturnValue = moReflect.executeAfterMethod(null);

		if (msAfterMethodReturnValue != null) {
		    fail("testWrongMethod failed. Expected to throw an excpetion. Found non-null result");
		}
	    } catch (NoSuchMethodException ee) {

	    } catch (Exception ee1) {
		ee1.printStackTrace();
		fail("testWrongMethod failed. Expected NoSuchMethodException");
	    }
	} catch (Exception e1) {
	    fail("testWrongMethod failed. Expected NoSuchMethodException");
	}
    }

    @Test
    public void testWrongClass() {
	moDefinition.setFqcnBefore("WrongClass");
	moDefinition.setBeforeMethod("wrongMethod");

	moReflect.setDefinition(moDefinition);

	try {
	    msBeforeMethodReturnValue = moReflect.executeBeforeMethod();

	    if (msBeforeMethodReturnValue != null) {
		fail("testWrongClass failed. Expected to throw an excpetion. Found non-null result");
	    }
	} catch (ClassNotFoundException e) {
	    moDefinition.setFqcnAfter("WrongClass");
	    moDefinition.setAfterMethod("wrongMethod");

	    moReflect.setDefinition(moDefinition);
	    moReflect.setRestJsonData(msJsonData);

	    try {
		msAfterMethodReturnValue = moReflect.executeAfterMethod(null);

		if (msAfterMethodReturnValue != null) {
		    fail("testWrongClass failed. Expected to throw an excpetion. Found non-null result");
		}
	    } catch (ClassNotFoundException ee) {

	    } catch (Exception ee1) {
		ee1.printStackTrace();
		fail("testWrongClass failed. Expected ClassNotFoundException");
	    }
	} catch (Exception e1) {
	    fail("testWrongClass failed. Expected ClassNotFoundException");
	}
    }

    @Test
    public void testBeforeMethodThrowsException() {
	moDefinition.setFqcnBefore("org.milkyway.sample.TestBeforeAfter");
	moDefinition.setBeforeMethod("beforeThrowsException");

	moReflect.setDefinition(moDefinition);
	moReflect.setRestJsonData(msJsonData);

	try {
	    msBeforeMethodReturnValue = moReflect.executeBeforeMethod();

	    fail("testBeforeMethodThrowsException failed. Expected to throw an exception");
	} catch (Exception e) {
	    if (e.getCause().getMessage().endsWith("testing") == false) {
		fail("testBeforeMethodThrowsException failed. Expected exception message to end with testing. Found "
			+ e.getCause().getMessage());
	    }
	}
    }

    @Test
    public void testAfterMethodThrowsException() {
	moDefinition.setFqcnAfter("org.milkyway.sample.TestBeforeAfter");
	moDefinition.setAfterMethod("afterThrowsException");

	moReflect.setDefinition(moDefinition);
	moReflect.setRestJsonData(msJsonData);

	try {
	    msAfterMethodReturnValue = moReflect.executeAfterMethod("JSON_RESULT");

	    fail("testAfterMethodThrowsException failed. Expected to throw an exception");
	} catch (Exception e) {
	    if (e.getCause().getMessage().endsWith("testing") == false) {
		fail("testAfterMethodThrowsException failed. Expected exception message to end with testing. Found "
			+ e.getCause().getMessage());
	    }
	}
    }

    private Reflect moReflect;
    private Definition moDefinition;
    private String msBeforeMethodReturnValue;
    private String msAfterMethodReturnValue;
    private String msJsonData;

}
