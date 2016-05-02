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
package org.aprilis.jrest.pull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.aprilis.jrest.compile.QueryBinder;
import org.aprilis.jrest.compile.Reflect;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.constant.Exceptions;
import org.aprilis.jrest.constant.HttpCodes;
import org.aprilis.jrest.execute.ExecutionEngine;
import org.aprilis.jrest.execute.Executor;
import org.aprilis.jrest.store.Definition;
import org.aprilis.jrest.store.Session;
import org.aprilis.jrest.store.Store;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("/pull")
public class Pull {
  /**
   * 
   */
  public Pull() {
	moExecutor = null;
	msSqlQuery = null;
	moResultSet = null;
	moResultMetaData = null;

	moReflect = new Reflect();
	moQueryBinder = new QueryBinder();
	moStringWriter = new StringWriter();
	moPrintWriter = new PrintWriter( moStringWriter );

	moStore = Store.instance();
	moSessionStore = Session.instance();
	moExecutionEngine = ExecutionEngine.instance();
  }/* public Pull() */

  /**
   * 
   * @param sessionKey
   * @param jrestKey
   * @param jsonData
   * @return
   */
  @SuppressWarnings("unchecked")
  @GET
  public Response executePull(@HeaderParam(Constants.SESSION_KEY) String sessionKey,
	  @HeaderParam(Constants.JREST_KEY) String jrestKey,
	  @DefaultValue(Constants.DEFAULT_JSON_DATA) @HeaderParam(Constants.JSON_DATA) String jsonData) {
	/*
	 * 
	 */
	if( moSessionStore.isSystemInReadyState() == false ) {
	  mLogger.fatal( Exceptions.gsSystemInHaltState );

	  return Response.status( HttpCodes.SERVICE_UNAVAILABLE )
		  .entity( Exceptions.gsSystemInHaltState ).build();
	} // if (moSessionStore.isSystemInReadyState() == false)

	try {
	  // 1. Check all the parameter except jsonData - Done
	  // 2. Verify session - Done
	  // 3. Verify jrest key permission by user - Done
	  // 4. Build query - Done
	  // 5. Acquire executor - Done
	  // 6. Execute - Done
	  // 7. Release executor - Done
	  // 8. Form response base on execute state and return - Done
	  //
	  mLogger.debug(
		  String.format( Exceptions.gsInfoSessionAndJrestKey, jrestKey, sessionKey ) );

	  if( sessionKey != null && jrestKey != null ) {
		if( moSessionStore.isSessionValid( sessionKey ) ) {
		  Definition jrestDefinition = moStore.getDefinition( jrestKey, true );

		  if( jrestDefinition != null ) {
			HashSet< String > hsetApiRoles = moStore.getDefinition( jrestKey, true )
				.getRoles();

			mLogger.debug( String.format( Exceptions.gsSessionIsValid, sessionKey ) );

			moExecutor = null;
			msSqlQuery = null;
			moResultSet = null;
			moResultMetaData = null;

			if( moSessionStore.isRoleSetValid( sessionKey, hsetApiRoles ) ) {
			  mLogger.debug( String.format( Exceptions.gsRolesVerificationPassed,
				  sessionKey, jrestKey ) );

			  moReflect.setDefinition( jrestDefinition );
			  moReflect.setRestJsonData( jsonData );

			  if( jrestDefinition.getFqcnBefore() != null ) {
				/*
				 * Execute Before method if it has been configured. If the
				 * before method is not successful, we expect it to throw an
				 * exception which we catch and halt processing of the request.
				 */
				try {
				  String sBeforeMethodResult = moReflect.executeBeforeMethod();

				  /*
				   * If before method is configured and if its results is to be
				   * consumed by JRest consume the result of beforeMethod and
				   * pass it to the QueryBinder class.
				   */
				  if( jrestDefinition.useResultFromBefore() == true ) {
					if( sBeforeMethodResult != null ) {
					  msSqlQuery = moQueryBinder.buildQueryForKey( jrestKey,
						  sBeforeMethodResult, Constants.gshDefTypeGet );
					} else {
					  mLogger.error( Exceptions.gsBeforeMethodOutputIsNull );

					  return Response.status( HttpCodes.PRECONDITION_FAILURE )
						  .entity( Exceptions.gsBeforeMethodOutputIsNull ).build();
					} // if (sBeforeMethodResult != null)
				  } else {
					// Do not consume output of Before method
					msSqlQuery = moQueryBinder.buildQueryForKey( jrestKey, jsonData,
						Constants.gshDefTypeGet );
				  } // if (jrestDefinition.useResultFromBefore() == true)
				} catch( Exception e ) {
				  mLogger.error( Exceptions.gsBeforeMethodFailed );

				  e.printStackTrace( moPrintWriter );
				  mLogger.error( moStringWriter.toString() );

				  return Response.status( HttpCodes.PRECONDITION_FAILURE )
					  .entity( Exceptions.gsBeforeMethodFailed ).build();
				} // end of try .. catch block
			  } else {
				/*
				 * If before method was not configured, the original Json data
				 * is passed to the QueryBinder.
				 */
				msSqlQuery = moQueryBinder.buildQueryForKey( jrestKey, jsonData,
					Constants.gshDefTypeGet );
			  } // if (jrestDefinition.getFqcnBefore() != null)

			  if( msSqlQuery == null ) {
				mLogger.error( Exceptions.gsUnProcessableQuery );

				return Response.status( HttpCodes.UNPROCESSABLE_ENTITY )
					.entity( Exceptions.gsUnProcessableQuery ).build();
			  } // if (msSqlQuery == null)

			  mLogger.debug( msSqlQuery );
			  
			  // Acquire executor handle from the pool engine
			  moExecutor = moExecutionEngine.acquireExecutorFromPool();

			  if( moExecutor != null ) {
				// Trigger the query and check whether it was successful or not
				moResultSet = moExecutor.executeQuery( msSqlQuery );

				mLogger.debug( String.format( Exceptions.gsFormedSqlQuery, msSqlQuery ) );

				if( moResultSet != null && moResultSet.isBeforeFirst() ) {
				  JSONArray jsonResultSet = new JSONArray();

				  moResultMetaData = moResultSet.getMetaData();

				  mLogger.debug( String.format( Exceptions.gsResultColumnCount,
					  moResultMetaData.getColumnCount() ) );

				  while( moResultSet.next() ) {
					JSONObject jsonRow = new JSONObject();

					for( short columnIndex = Constants.gshColumnStartIndex; columnIndex <= moResultMetaData
						.getColumnCount(); columnIndex++ ) {
					  /*
					   * mLogger.trace( String.format(
					   * Exceptions.gsResultSetRowInfo,
					   * moResultMetaData.getColumnName( columnIndex ),
					   * moResultSet.getString( columnIndex ) ) );
					   */

					  jsonRow.put( moResultMetaData.getColumnName( columnIndex ),
						  moResultSet.getString( columnIndex ) );
					} // for (short columnIndex = ... )

					jsonResultSet.add( jsonRow );
				  }// while( moResultSet.next() )

				  moResultSet.close();
				  moExecutionEngine.releaseExecutorToPool( moExecutor );

				  moResultMetaData = null;
				  moResultSet = null;
				  moExecutor = null;
				  msSqlQuery = null;

				  /*
				   * Execute After method if it has been configured. If the
				   * after method is not successful, we expect it to throw an
				   * exception which we catch and return error.
				   */
				  if( jrestDefinition.getFqcnAfter() != null ) {
					try {
					  String sAfterMethodResult = moReflect
						  .executeAfterMethod( jsonResultSet.toJSONString() );

					  return Response.status( HttpCodes.OK ).entity( sAfterMethodResult )
						  .build();

					} catch( Exception e ) {
					  mLogger.error( Exceptions.gsAfterMethodFailed );

					  e.printStackTrace( moPrintWriter );
					  mLogger.error( moStringWriter.toString() );

					  return Response.status( HttpCodes.EXPECTATION_FAILED )
						  .entity( Exceptions.gsAfterMethodFailed ).build();
					} // end of try .. catch block

				  } // if (jrestDefinition.getFqcnAfter() != null)

				  return Response.status( HttpCodes.OK )
					  .entity( jsonResultSet.toJSONString() ).build();
				} else {
				  mLogger.error( Exceptions.gsQueryResultedInNullSetMessage );

				  return Response.status( HttpCodes.EXPECTATION_FAILED )
					  .entity( Exceptions.gsQueryResultedInNullSet ).build();
				} // if (moResultSet != null)
			  } else {
				return Response.status( HttpCodes.SERVICE_UNAVAILABLE )
					.entity( Exceptions.gsNoFreeExecutorsAvailable ).build();
			  } // if (moExecutor != null)
			} // if (moSessionStore.isRoleSetValid(sessionKey, hsetApiRoles))

			mLogger.error( String.format( Exceptions.gsRolesVerificationFailed,
				sessionKey, jrestKey ) );

			return Response.status( HttpCodes.FORBIDDEN ).entity( String
				.format( Exceptions.gsRolesVerificationFailed, sessionKey, jrestKey ) )
				.build();
		  }// if (jrestDefinition != null)

		  mLogger.error( String.format( Exceptions.gsNoDefinitionFound, jrestKey ) );

		  return Response.status( HttpCodes.NOT_FOUND )
			  .entity( String.format( Exceptions.gsNoDefinitionFound, jrestKey ) )
			  .build();
		} // if (moSessionStore.isSessionValid(sessionKey))

		mLogger.error( String.format( Exceptions.gsSessionIsInValid, sessionKey ) );

		return Response
			.status( HttpCodes.FORBIDDEN ).entity( String
				.format( Exceptions.gsSessionIsInValidMessage, sessionKey ).toString() )
			.build();
	  } // if (sessionKey != null && jrestKey != null)
	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );
	} finally {
	  if( moExecutor != null ) {
		moExecutionEngine.releaseExecutorToPool( moExecutor );
	  }

	  if( moResultSet != null ) {
		try {
		  moResultSet.close();
		} catch( SQLException e ) {
		  e.printStackTrace( moPrintWriter );
		  mLogger.error( moStringWriter.toString() );
		}
	  }

	  msSqlQuery = null;
	  moExecutor = null;
	  moResultSet = null;
	  moResultMetaData = null;
	} // end of try .. catch .. finally section

	return Response.status( HttpCodes.FORBIDDEN ).build();
  }/*
    * public Response executePull(...)
    */

  /**
   * 
   */
  private Reflect moReflect;

  /**
   * 
   */
  private Store moStore;

  /**
   * 
   */
  private String msSqlQuery;
  /**
   * 
   */
  private QueryBinder moQueryBinder;

  /**
   * 
   */
  private Executor moExecutor;

  /**
   * 
   */
  private ExecutionEngine moExecutionEngine;

  /**
   * 
   */
  private Session moSessionStore;

  /**
   * 
   */
  private ResultSet moResultSet;

  /**
   * 
   */
  private ResultSetMetaData moResultMetaData;

  /*
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Pull.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;
}/* public class Pull */
