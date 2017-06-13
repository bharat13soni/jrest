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
package org.milkyway.jrest.store;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.milkyway.jrest.constant.Constants;
import org.milkyway.jrest.constant.Exceptions;
import org.milkyway.jrest.db.ConnectionDetails;

public class Store {
  /**
   * 
   */
  private Store() {
	moLock = new Object();

	moAuthenticationDefinition = new Definition();
	moConnectionDefinition = null;

	mhmapGetDefinitions = new HashMap< String, Definition >();
	mhmapSetDefinitions = new HashMap< String, Definition >();
  }/* private Store() */

  /**
   * 
   * @return
   */
  public static Store instance() {
	if( __instance == null ) {
	  synchronized( Store.class ) {
		if( __instance == null ) {
		  __instance = new Store();
		} /* if (__instance == null) */
	  } /* synchronized (Store.class) */
	} /* if (__instance == null) */

	return __instance;
  }/* public static Store Instance() */

  /**
   * Clones are not supported for this class so this prevents the users from
   * doing so.
   */
  public Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException( "This is a Singleton Ojbect; Buzz off" );
  }/* public Object clone() throws CloneNotSupportedException */

  /**
   * 
   * @param sDefinitionName
   * @param oDefinition
   */
  public void addSetDefinition(String sDefinitionName, Definition oDefinition) {
	synchronized( moLock ) {
	  if( sDefinitionName != null && oDefinition != null ) {
		if( mhmapSetDefinitions.containsKey( sDefinitionName ) ) {
		  mLogger
			  .debug( String.format( Exceptions.gsDefinitionReplaced, sDefinitionName ) );
		  mLogger
			  .warn( String.format( Exceptions.gsDefinitionReplaced, sDefinitionName ) );
		}// if (mhmapSetDefinitions.containsKey(sDefinitionName))

		mhmapSetDefinitions.put( sDefinitionName, oDefinition );
	  }// if (sDefinitionName != null && oDefinition != null)
	}// synchronized (moLock) {
  }/* public void addSetDefinition(...) */

  /**
   * 
   * @param sDefinitionName
   * @param isTypeGet
   * @return
   */
  public Definition getDefinition(String sDefinitionName, boolean isTypeGet) {
	if( isTypeGet ) {
	  return mhmapGetDefinitions.get( sDefinitionName );
	} else {
	  return mhmapSetDefinitions.get( sDefinitionName );
	}
  }/* public Definition getDefinition(...) */

  /**
   * 
   * @param sDefinitionName
   * @param oDefinition
   */
  public void addGetDefinition(String sDefinitionName, Definition oDefinition) {
	synchronized( moLock ) {
	  if( sDefinitionName != null && oDefinition != null ) {
		if( mhmapGetDefinitions.containsKey( sDefinitionName ) ) {
		  mLogger
			  .debug( String.format( Exceptions.gsDefinitionReplaced, sDefinitionName ) );
		  mLogger
			  .warn( String.format( Exceptions.gsDefinitionReplaced, sDefinitionName ) );
		}// if (mhmapGetDefinitions.containsKey(sDefinitionName))

		mhmapGetDefinitions.put( sDefinitionName, oDefinition );
	  }// if (sDefinitionName != null && oDefinition != null)
	}// synchronized (moLock)
  }/* public void addGetDefinition(...) */

  /**
   * 
   * @param sConnectionString
   */
  public void setJdbcConnectionDetails(ConnectionDetails oConnection) {
	synchronized( moLock ) {
	  if( oConnection != null ) {
		moConnectionDefinition = oConnection;
	  }
	}
  }/* public void setJdbcConnectionDetails(ConnectionDetails oConnection) */

  /**
   * 
   * @return
   */
  public ConnectionDetails getJdbcConnectionDetails() {
	return moConnectionDefinition;
  }/* public ConnectionDetails getJdbcConnectionDetails() */

  /**
   * 
   * @param sSqlStatement
   */
  public void addAuthenticationQuery(String sSqlStatement) {
	synchronized( moLock ) {
	  if( sSqlStatement != null ) {
		mLogger.debug( String.format( Exceptions.gsDefinitionQuery, sSqlStatement ) );

		moAuthenticationDefinition.setQuery( sSqlStatement );
	  }// if (sSqlStatement != null)
	}// synchronized (moLock)
  }/* public void addAuthenticationQuery(...) */

  /**
   * 
   * @param cDelimiter
   */
  public void addAuthenticationDelimiter(char cDelimiter) {
	synchronized( moLock ) {
	  moAuthenticationDefinition.setDelimiter( cDelimiter );
	}
  }/* public void addAuthenticationDelimiter(...) */

  /**
   * 
   * @return
   */
  public Definition getAuthenticationDefinition() {
	return moAuthenticationDefinition;
  }/* public Definition getAuthenticationDefinition() */

  /**
   * 
   */
  public void clearAllDefinitions() {
	mhmapGetDefinitions.clear();
	mhmapSetDefinitions.clear();

	moAuthenticationDefinition.setQuery( null );
	moAuthenticationDefinition.setDelimiter( Constants.gcDefaultAuthDelimiter );

	moConnectionDefinition = null;
  }/* public void clearAllDefinitions() */

  /**
   * Singleton instance object for the store cache. We must always have a single
   * instance of the object all the time.
   */
  private static Store __instance;

  /**
   * Container into which all the GET type of requests are stored
   */
  private HashMap< String, Definition > mhmapGetDefinitions;

  /**
   * Container into which all the PUT/POST type of request are stored
   */
  private HashMap< String, Definition > mhmapSetDefinitions;

  /**
   * Class object into which the Authentication definition JSON is stored
   */
  private Definition moAuthenticationDefinition;

  /**
   * The global JDBC connection string. This will be populated using the JDBC
   * type of definition file.
   */
  private ConnectionDetails moConnectionDefinition;

  /**
   * The synchronization object using which all the set operations on the Store
   * are done atomically.
   */
  private Object moLock;

  /*
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Store.class.getCanonicalName() );
}/* public class Store */
