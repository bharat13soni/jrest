/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.milkyway.jrest.compile;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.milkyway.jrest.constant.Constants;
import org.milkyway.jrest.constant.Exceptions;
import org.milkyway.jrest.store.Definition;

public class Reflect {
  /**
   * 
   */
  public Reflect() {
	moMethod = null;
	moFQClassName = null;
	moDefinition = null;
	msRestJsonData = null;
	moClassInstance = null;
	msAfterMethodResult = null;
	msBeforeMethodResult = null;

	maBeforeMethodParamTypes = new Class[Constants.gshBeforeMethodParamCount];
	maAfterMethodParamTypes = new Class[Constants.gshAfterMethodParamCount];

	for( short paramIndex = Constants.gshZero; paramIndex < Constants.gshBeforeMethodParamCount; paramIndex++ ) {
	  maBeforeMethodParamTypes[paramIndex] = String.class;
	} // for (short paramIndex = Constants.gshZero; ... )

	for( short paramIndex = Constants.gshZero; paramIndex < Constants.gshAfterMethodParamCount; paramIndex++ ) {
	  maAfterMethodParamTypes[paramIndex] = String.class;
	} // for (short paramIndex = Constants.gshZero; ... )
  }/* public Reflect() */

  /**
   * 
   * @param jrestDefinition
   */
  public void setDefinition(Definition jrestDefinition) {
	moDefinition = jrestDefinition;
  }/* public void setDefinition(Definition jrestDefinition ) */

  /**
   * 
   * @param jsonData
   */
  public void setRestJsonData(String jsonData) {
	msRestJsonData = jsonData;
  }/* public void setRestJsonData(String jsonData) */

  /**
   * 
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String executeBeforeMethod() throws Exception {
	if( moDefinition != null ) {
	  moFQClassName = Class.forName( moDefinition.getFqcnBefore() );
	  moClassInstance = moFQClassName.newInstance();

	  moMethod = moFQClassName.getMethod( moDefinition.getBeforeMethod(),
		  maBeforeMethodParamTypes );

	  msBeforeMethodResult = (String) moMethod.invoke( moClassInstance, msRestJsonData );

	  return msBeforeMethodResult;
	} else {
	  mLogger.error( Exceptions.gsReflectNullDefinition );
	} // if (moDefinition != null)

	return null;
  }/* public String executeBeforeMethod() */

  /**
   * 
   * @param restJsonResult
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String executeAfterMethod(String restJsonResult) throws Exception {
	if( moDefinition != null ) {
	  moFQClassName = Class.forName( moDefinition.getFqcnAfter() );
	  moClassInstance = moFQClassName.newInstance();

	  moMethod = moFQClassName.getMethod( moDefinition.getAfterMethod(),
		  maAfterMethodParamTypes );

	  msAfterMethodResult = (String) moMethod.invoke( moClassInstance, msRestJsonData,
		  restJsonResult, msBeforeMethodResult );

	  return msAfterMethodResult;
	} else {
	  mLogger.error( Exceptions.gsReflectNullDefinition );
	} // if (moDefinition != null)

	return null;
  }/* public String executeAfterMethod(String restJsonResult) */

  /**
   * 
   */
  private String msRestJsonData;

  /**
   * 
   */
  private Definition moDefinition;

  /**
   * 
   */
  private Method moMethod;

  /**
   * 
   */
  @SuppressWarnings("rawtypes")
  private Class[] maBeforeMethodParamTypes;

  /**
   * 
   */
  @SuppressWarnings("rawtypes")
  private Class[] maAfterMethodParamTypes;

  /**
   * 
   */
  private String msBeforeMethodResult;

  /**
   * 
   */
  private String msAfterMethodResult;

  /**
   * 
   */
  @SuppressWarnings("rawtypes")
  private Class moFQClassName;

  /**
   * 
   */
  private Object moClassInstance;

  /**
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Reflect.class.getCanonicalName() );

}/* public class Reflect */
