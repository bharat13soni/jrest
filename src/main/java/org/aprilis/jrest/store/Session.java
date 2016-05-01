/*
 * Copyright 2013 JRest Foundation and other contributors
 *
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
package org.aprilis.jrest.store;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.constant.Exceptions;

public class Session {
  /**
   * 
   */
  private Session() {
	mbIsSystemReady = false;

	moLock = new Object();
	moStringWriter = new StringWriter();
	moPrintWriter = new PrintWriter( moStringWriter );
	moRandGenerator = new Random();
	mhmapSessions = new HashMap< String, Long >();
	mhmapSessionAndRoles = new HashMap< String, HashSet< String > >();
  }/* private Session() */

  /**
   * 
   * @return
   */
  public static Session instance() {
	if( __instance == null ) {
	  synchronized( Session.class ) {
		if( __instance == null ) {
		  __instance = new Session();
		} /* if (__instance == null) */
	  } /* synchronized (Session.class) */
	} /* if (__instance == null) */

	return __instance;
  }/* public static Session Instance() */

  /**
   * 
   * @return
   */
  public String registerSession(HashSet< String > hsetRoles) {
	synchronized( moLock ) {
	  try {
		String sessionKey = getMd5SessionKey();

		if( sessionKey != null ) {
		  mLogger.debug( String.format( Exceptions.gsSessionCreated, sessionKey ) );

		  mhmapSessions.put( sessionKey, System.currentTimeMillis() );
		  mhmapSessionAndRoles.put( sessionKey, hsetRoles );

		  return sessionKey;
		}// if( sessionKey != null)
	  } catch( Exception e ) {
		e.printStackTrace( moPrintWriter );

		mLogger.error( moStringWriter.toString() );
	  }// end of try ... catch block

	  return null;
	}// synchronized(moLock)
  }/* public String registerSession() */

  /**
   * 
   * @param sessionKey
   * @return
   */
  public boolean isSessionValid(String sessionKey) {
	synchronized( moLock ) {
	  Long lSessionTimeStamp = mhmapSessions.get( sessionKey );

	  if( ( lSessionTimeStamp != null ) && ( System.currentTimeMillis()
		  - lSessionTimeStamp ) <= Constants.glSessionIdleTime ) {
		mhmapSessions.put( sessionKey, System.currentTimeMillis() );

		return true;
	  }// if ((lSessionTimeStamp != null)

	  mLogger.debug( String.format( Exceptions.gsSessionTimeInfoIsNull, sessionKey ) );

	  /*
	   * Code would reach here if session has expired or if the session key
	   * doesnot exist.
	   */
	  mhmapSessions.remove( sessionKey );
	  mhmapSessionAndRoles.remove( sessionKey );

	  return false;
	}// synchronized(moLock)
  }/* public boolean isSessionValid(String sessionKey) */

  /**
   * 
   */
  public void removeExpiredSessions() {
	Iterator< Map.Entry< String, Long > > sessionIterator = mhmapSessions.entrySet()
		.iterator();

	mLogger.debug(
		String.format( Exceptions.gsPurgeExpiredSessionStarted, mhmapSessions.size() ) );

	short shExpireCounter = 0;
	synchronized( moLock ) {
	  while( sessionIterator.hasNext() ) {
		Entry< String, Long > sessionInfo = sessionIterator.next();

		if( ( System.currentTimeMillis()
			- sessionInfo.getValue() ) > Constants.glSessionIdleTime ) {
		  sessionIterator.remove();
		  mhmapSessionAndRoles.remove( sessionInfo.getKey() );

		  shExpireCounter++;
		}// if ((lSessionTimeStamp != null)
	  }// while (sessionIterator.hasNext())
	}// synchronized (moLock)

	mLogger
		.debug( String.format( Exceptions.gsPurgeExpiredSessionEnded, shExpireCounter ) );
  }/* public void removeExpiredSessions() */

  /**
   * 
   * @param sessionKey
   */
  public void deregisterSession(String sessionKey) {
	synchronized( moLock ) {
	  mhmapSessions.remove( sessionKey );
	  mhmapSessionAndRoles.remove( sessionKey );

	  mLogger.debug( String.format( Exceptions.gsSessionDeregistered, sessionKey ) );
	}// synchronized(moLock)
  }/* public void deregisterSession(String sessionKey) */

  /**
   * 
   * @param sessionKey
   * @param hsetRoles
   * @return
   */
  public boolean isRoleSetValid(String sessionKey, HashSet< String > hsetDefRoles) {
	/*
	 * This function does not check for the validity of the session key.
	 */
	HashSet< String > hsetUserRoles = mhmapSessionAndRoles.get( sessionKey );

	if( hsetUserRoles != null && hsetDefRoles != null ) {
	  mLogger.debug( String.format( Exceptions.gsCompareRoleSets,
		  hsetUserRoles.toString(), hsetDefRoles.toString() ) );

	  for( String roleName : hsetUserRoles ) {
		if( hsetDefRoles.contains( roleName ) ) {
		  return true;
		}
	  }// for (String roleName : hsetUserRoles)
	}// if (hsetUserRoles != null && hsetDefRoles != null)

	return false;
  }/*
    * public boolean isRoleSetValid(String sessionKey, HashSet<String>
    * hsetDefRoles)
    */

  /**
   * 
   * @return
   */
  public int getRegisteredSessionsCount() {
	return mhmapSessions.size();
  }/* public long getRegisteredSessionsCount() */

  /**
   * 
   * @return
   */
  public int getExactValidSessionsCount() {
	removeExpiredSessions();

	return getRegisteredSessionsCount();
  }/* public int getExactValidSessionsCount() */

  /**
   * Generates an MD5 key based on the data provided by the caller. If the user
   * likes to have multiple parts to the feeder information, he must concatenate
   * all of that into one single string and pass it the function.
   * 
   * To keep the information to less predictable, generator adds its own logical
   * information to feeder information before processing for the key
   * 
   * @param keyFeederInfo
   *          Information for which and MD5 key must be generated
   * @return A valid key if everything went fine otherwise a null is returned
   */
  private String getMd5SessionKey() {
	try {
	  String turningKey = Long.toString( System.currentTimeMillis() )
		  + Long.toString( moRandGenerator.nextLong() ) + UUID.randomUUID().toString();

	  MessageDigest md5Handle = MessageDigest.getInstance( "MD5" );

	  md5Handle.reset();
	  md5Handle.update( turningKey.getBytes() );

	  byte md5Digest[] = md5Handle.digest();

	  StringBuffer sessionKey = new StringBuffer();

	  for( int position = 0; position < md5Digest.length; position++ ) {
		sessionKey.append( Integer.toHexString( 0xFF & md5Digest[position] ) );
	  }// end of for loop

	  return ( sessionKey.toString().toUpperCase() );
	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );
	}// end of try ... catch

	return null;
  }/* public String getMd5KeyFor(String keyFeederInfo) */

  /**
   * 
   */
  public void setSystemToReadyState() {
	synchronized( moLock ) {
	  mbIsSystemReady = true;
	}
  }/* public void setSystemToReadyState() */

  /**
   * 
   */
  public void setSystemToHaltState() {
	synchronized( moLock ) {
	  mbIsSystemReady = false;
	}
  }/* public void setSystemToHaltState() */

  /**
   * 
   * @return
   */
  public boolean isSystemInReadyState() {
	return mbIsSystemReady;
  }/* public boolean isSystemInReadyState() */

  /**
   * Clones are not supported for this class so this prevents the users from
   * doing so.
   */
  public Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException( "This is a Singleton Ojbect; Buzz off" );
  }/* public Object clone() throws CloneNotSupportedException */

  /**
   *     
   */
  public void clearAllSessions() {
	mhmapSessions.clear();
	mhmapSessionAndRoles.clear();
  }/* public void clearAllSessions() */

  /**
   * Singleton instance object for the store cache. We must always have a single
   * instance of the object all the time.
   */
  private static Session __instance;

  /**
   * 
   */
  private HashMap< String, Long > mhmapSessions;

  /**
   * 
   */
  private HashMap< String, HashSet< String > > mhmapSessionAndRoles;

  /**
   * The synchronization object using which all the set operations on the
   * session store are done atomically.
   */
  private Object moLock;

  /**
   * The handle for the Random number generator, used while generating an MD5
   * key.
   */
  private Random moRandGenerator;

  /**
   * 
   */
  private boolean mbIsSystemReady;

  /*
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Session.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;
}/* public class Session */
