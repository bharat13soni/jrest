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
package org.aprilis.jrest.execute;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.constant.Exceptions;
import org.aprilis.jrest.db.Connector;
import org.aprilis.jrest.store.Store;

/**
 * @author h & rk
 * 
 */
public class Executor extends Connector {
  public Executor(short poolIndex) {
	super();

	moStatement = null;

	moStore = Store.instance();
	mshPoolIndex = poolIndex;
	mbPreviousExecutionState = true;
	moStringWriter = new StringWriter();
	moPrintWriter = new PrintWriter( moStringWriter );
  }/* public Executor() */

  /**
   * 
   * @return
   */
  public boolean initialize() {
	return connect( moStore.getJdbcConnectionDetails() );
  }/* public boolean initialize() */

  /**
   * 
   * @return
   */
  private boolean reInitialize() {
	try {
	  if( moStatement.isClosed() == false ) {
		moStatement.close();
		moStatement = null;
	  }

	  disConnect();
	} catch( Exception e ) {
	  // Do not act on this exception
	}

	return ( connect( moStore.getJdbcConnectionDetails() ) );
  }/* private boolean reInitialize() */

  /**
   * 
   * @param sqlStatement
   * @return
   */
  public ResultSet executeQuery(String sqlStatement) {
	try {
	  if( mbPreviousExecutionState == false ) {
		mLogger.debug( Exceptions.gsAttemptDbReInitialization );

		mbPreviousExecutionState = reInitialize();

		if( mbPreviousExecutionState == false ) {
		  mLogger.error( Exceptions.gsDbReInitializeFailed );

		  return null;
		}// if(mbPreviousExecutionState == false)
	  }// if(mbPreviousExecutionState == false)

	  mLogger.debug( String.format( Exceptions.gsExecutingQuery, sqlStatement ) );

	  moStatement = moConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
		  ResultSet.CONCUR_READ_ONLY );

	  return moStatement.executeQuery( sqlStatement );
	} catch( SQLException e ) {
	  /*
	   * Logic to reinitialize connection when we catch a SQLException related
	   * to DB Connection failure
	   */
	  if( Constants.ghsetConnectionErrorCodes.contains( e.getSQLState() ) ) {
		mbPreviousExecutionState = false;
		/*
		 * Recursively call executeQuery() which will reinitialize DB on its
		 * next invocation
		 */
		return executeQuery( sqlStatement );
	  }

	  e.printStackTrace( moPrintWriter );
	  mLogger.error( moStringWriter.toString() );
	}

	return null;
  }/* public ResultSet executeQuery(String sqlStatement) */

  /**
   * 
   */
  public void releaseStatement() {
	try {
	  if( moStatement != null && !moStatement.isClosed() ) {
		moStatement.close();
	  }

	  moStatement = null;
	} catch( Exception e ) {
	  // Nothing much can be done now, so ignore it
	}// end of try ... catch block
  }// public void releaseStatement()

  /**
   * 
   * @param sqlStatement
   * @return
   */
  public boolean execute(String sqlStatement) {
	try {
	  moStatement = moConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
		  ResultSet.CONCUR_READ_ONLY );

	  moStatement.execute( sqlStatement );

	  return ( moStatement.getUpdateCount() > 0 );

	} catch( Exception e ) {
	  if( !reInitialize() ) {
		e.printStackTrace( moPrintWriter );
		mLogger.error( moStringWriter.toString() );

		return false;
	  }

	  return execute( sqlStatement );
	}// end of try .. catch block

  }/* public boolean execute(String sqlStatement) */

  /**
   * 
   * @return
   */
  public short getPoolIndex() {
	return mshPoolIndex;
  }/* public short getPoolIndex() */

  /**
   * 
   */
  public void unInitialize() {
	disConnect();
  }

  /**
   * 
   */
  private Statement moStatement;

  /**
   * 
   */
  private Store moStore;

  /**
   * 
   */
  private boolean mbPreviousExecutionState;

  /**
   * 
   */
  private short mshPoolIndex;

  /**
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Executor.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;

}/* public class Executor */
