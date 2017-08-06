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
package org.milkyway.jrest.pull;

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
import org.milkyway.jrest.compile.QueryBinder;
import org.milkyway.jrest.compile.Reflect;
import org.milkyway.jrest.constant.Constants;
import org.milkyway.jrest.constant.Exceptions;
import org.milkyway.jrest.constant.HttpCodes;
import org.milkyway.jrest.execute.ExecutionEngine;
import org.milkyway.jrest.execute.Executor;
import org.milkyway.jrest.store.Definition;
import org.milkyway.jrest.store.Session;
import org.milkyway.jrest.store.Store;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("/pull/adhoc")
public class AdhocPull {
  /**
   * 
   */
  public AdhocPull() {
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
  }/* public AdhocPull() */

  /**
   * 
   * @param sessionKey
   * @param adhocSql
   * @param jsonData
   * @return
   */
  @SuppressWarnings("unchecked")
  @GET
  public Response executePull(@HeaderParam(Constants.SESSION_KEY) String sessionKey,
	  @HeaderParam(Constants.JREST_KEY) String jrestKey,
	  @HeaderParam(Constants.ADHOC_SQL) String adhocSql,
	  @DefaultValue(Constants.DEFAULT_JSON_DATA) @HeaderParam(Constants.JSON_DATA) String jsonData) {
	/*
	 * Evaluate the system readiness, if not send the response back.
	 */
	if( moSessionStore.isSystemInReadyState() == false ) {
	  mLogger.fatal( Exceptions.gsSystemInHaltState );

	  return Response.status( HttpCodes.SERVICE_UNAVAILABLE )
		  .entity( Exceptions.gsSystemInHaltState ).build();
	} // if (moSessionStore.isSystemInReadyState() == false)

	try {
	  mLogger.debug(
		  String.format( Exceptions.gsInfoAdhocSqlAndKey, sessionKey, adhocSql ) );

	  if( sessionKey != null && adhocSql != null ) {
		if( moSessionStore.isSessionValid( sessionKey ) ) {
		  Definition jrestDefinition = null;

		  /*
		   * If the definition is given we inherit it, this enables us to retain
		   * the before and after methods for the adhoc sql. if not then it
		   * simply is just a adhoc query that doesn't need any before or after
		   * methods. Doing this also enables us to inherit any JREST definiton
		   * and change the SQL dynamically.
		   */
		  if( jrestKey != null && jrestKey != Constants.NULL ) {
			jrestDefinition = moStore.getDefinition( jrestKey, true );
		  } else {
			jrestDefinition = new Definition();
			jrestDefinition.addRole( Constants.gsDefaultRoleId );
		  }

		  if( jrestDefinition != null ) {
			HashSet< String > hsetApiRoles = jrestDefinition.getRoles();

			mLogger.debug( String.format( Exceptions.gsSessionIsValid, sessionKey ) );

			moExecutor = null;
			msSqlQuery = null;
			moResultSet = null;
			moResultMetaData = null;

			jrestDefinition.setQuery( adhocSql );

			if( moSessionStore.isRoleSetValid( sessionKey, hsetApiRoles ) ) {
			  mLogger.debug( String.format( Exceptions.gsRolesVerificationPassed,
				  sessionKey, Constants.ADHOC_SQL ) );

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
					  msSqlQuery = moQueryBinder.buildQueryForKey( jrestDefinition,
						  sBeforeMethodResult );
					} else {
					  mLogger.error( Exceptions.gsBeforeMethodOutputIsNull );

					  return Response.status( HttpCodes.PRECONDITION_FAILURE )
						  .entity( Exceptions.gsBeforeMethodOutputIsNull ).build();
					} // if (sBeforeMethodResult != null)
				  } else {
					// Do not consume output of Before method
					msSqlQuery = moQueryBinder.buildQueryForKey( jrestDefinition,
						jsonData );
				  } // if(jrestDefinition.useResultFromBefore() == true)
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
				msSqlQuery = moQueryBinder.buildQueryForKey( jrestDefinition, jsonData );
			  } // if (jrestDefinition.getFqcnBefore() != null)

			  if( msSqlQuery == null ) {
				mLogger.error( Exceptions.gsUnProcessableQuery );

				return Response.status( HttpCodes.UNPROCESSABLE_ENTITY )
					.entity( Exceptions.gsUnProcessableQuery ).build();
			  }

			  // Acquire executor handle from the pool engine
			  moExecutor = moExecutionEngine.acquireExecutorFromPool();

			  if( moExecutor != null ) {
				// Trigger the query and check whether it was
				// successful or
				// not
				mLogger.debug( String.format( Exceptions.gsFormedSqlQuery, msSqlQuery ) );
				moResultSet = moExecutor.executeQuery( msSqlQuery );

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
				  } // while( moResultSet.next() )

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

				  } // if (jrestDefinition.getFqcnAfter() !=
				    // null)

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
			} // if (moSessionStore.isRoleSetValid(sessionKey,
			  // hsetApiRoles))

			mLogger.error( String.format( Exceptions.gsRolesVerificationFailed,
				sessionKey, jrestKey ) );

			return Response.status( HttpCodes.FORBIDDEN ).entity( String
				.format( Exceptions.gsRolesVerificationFailed, sessionKey, jrestKey ) )
				.build();
		  } // if (jrestDefinition != null)

		  // Mark for GC pro-actively
		  jrestDefinition = null;

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
	  } // if( sessionKey != null && adhocSql != null )
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
  private static Logger mLogger = Logger.getLogger( AdhocPull.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;
}/* public class Pull */
