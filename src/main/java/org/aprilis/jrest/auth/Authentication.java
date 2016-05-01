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
package org.aprilis.jrest.auth;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.HashSet;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.aprilis.jrest.compile.QueryBinder;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.constant.Exceptions;
import org.aprilis.jrest.constant.HttpCodes;
import org.aprilis.jrest.execute.ExecutionEngine;
import org.aprilis.jrest.execute.Executor;
import org.aprilis.jrest.store.Session;
import org.aprilis.jrest.store.Store;

@Path("/")
public class Authentication {
  /**
   * 
   */
  public Authentication() {
	moExecutor = null;

	moStore = Store.instance();
	moSessionStore = Session.instance();
	moExecutionEngine = ExecutionEngine.instance();

	moQueryBinder = new QueryBinder();
	moStringWriter = new StringWriter();
	moPrintWriter = new PrintWriter( moStringWriter );
  }/* public Authentication() */

  /**
   * 
   * @param jsonData
   * @return
   */
  @Path("login")
  @POST
  public Response login(@HeaderParam(Constants.JSON_DATA) String jsonData) {
	if( moSessionStore.isSystemInReadyState() == false ) {
	  mLogger.fatal( Exceptions.gsSystemInHaltState );

	  return Response.status( HttpCodes.SERVICE_UNAVAILABLE )
		  .entity( Exceptions.gsSystemInHaltState ).build();
	}// if (moSessionStore.isSystemInReadyState() == false)

	try {
	  // 1. Check all the parameters - Done
	  // 2. Get query from Binder - Done
	  // 3. Get exeuctor object from pool - Done
	  // 4. Execute query and validate result - Done
	  // 5. If valid user create session with roles - Done
	  // 6. Release executor back - Done
	  // 7. Return session key - Done
	  //
	  if( jsonData != null && jsonData.length() > Constants.gshMinJsonDataLength ) {
		String sqlQuery = moQueryBinder.buildQueryForAuth( jsonData );

		if( sqlQuery == null ) {
		  mLogger.error( Exceptions.gsUnProcessableQuery );

		  return Response.status( HttpCodes.UNPROCESSABLE_ENTITY )
			  .entity( Exceptions.gsUnProcessableQuery ).build();
		}// if (sqlQuery == null)

		mLogger.debug( sqlQuery );

		moExecutor = moExecutionEngine.acquireExecutorFromPool();

		if( moExecutor != null ) {
		  ResultSet rsRoles = moExecutor.executeQuery( sqlQuery );

		  if( rsRoles == null ) {
			mLogger
				.fatal( String.format( Exceptions.gsBadQueryOrDatabaseGone, sqlQuery ) );

			moExecutionEngine.releaseExecutorToPool( moExecutor );

			return Response.status( HttpCodes.INTERNAL_SERVER_ERROR )
				.entity( String.format( Exceptions.gsBadQueryOrDatabaseGone, sqlQuery ) )
				.build();
		  }// if (rsRoles == null)

		  HashSet< String > hsetRoles = new HashSet< String >();

		  if( moStore.getAuthenticationDefinition()
			  .getDelimiter() != Constants.gcDefaultAuthDelimiter ) {
			/*
			 * We have a delimiter on which we need to parse over the result set
			 */
			while( rsRoles.next() ) {
			  // Split the zeroth index column on the set delimiter and
			  String roles[] = rsRoles.getString( Constants.gshColumnStartIndex )
				  .split( String
					  .valueOf( moStore.getAuthenticationDefinition().getDelimiter() ) );

			  mLogger.debug( roles );

			  for( short roleIndex = 0; roleIndex < roles.length; roleIndex++ ) {
				hsetRoles.add( roles[roleIndex].trim() );
			  }// for (short roleIndex = 0; roleIndex < roles.length;
			   // roleIndex++)
			}// while(rsRoles.next())
		  } else {
			/*
			 * We dont have a delimiter which means either user has selected no
			 * role based authentication or multiple roles in the form of
			 * individual rows.
			 */
			while( rsRoles.next() ) {
			  hsetRoles.add( rsRoles.getString( Constants.gshColumnStartIndex ).trim() );
			}// while (rsRoles.next())
		  }// if (moStore.getAuthenticationDefinition().getDelimiter()

		  rsRoles.close();
		  moExecutionEngine.releaseExecutorToPool( moExecutor );
		  
		  rsRoles = null;
		  moExecutor = null;

		  if( hsetRoles.size() > Constants.gshZero ) {
			String sessionKey = moSessionStore.registerSession( hsetRoles );

			return Response
				.status( HttpCodes.OK ).entity( String
					.format( Constants.gsSessionKeyJsonFormat, sessionKey ).toString() )
				.build();
		  } else {
			mLogger.debug( Exceptions.gsNoRolesAssignedToUser );

			return Response.status( HttpCodes.UNAUTHORIZED )
				.entity( Exceptions.gsNoRolesAssignedToUser ).build();
		  }// if (hsetRoles.size() > Constants.gshZero)
		}// if (moExecutor != null)
	  }// if (jsonData != null && jsonData.length() >
	   // Constants.gshMinJsonDataLength)
	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );
	} finally {
	  if( moExecutor != null ) {
		moExecutionEngine.releaseExecutorToPool( moExecutor );
		
		moExecutor = null;
	  }// if(moExecutor != null)
	}// end of try .. catch block

	return Response.status( HttpCodes.UNAUTHORIZED ).build();
  }/*
    * public Response login(String sessionKey, String jrestKey, String jsonData)
    */

  /**
   * 
   * @param sessionKey
   * @return
   */
  @Path("logoff")
  @POST
  public Response logoff(@HeaderParam(Constants.SESSION_KEY) String sessionKey) {
	if( moSessionStore.isSystemInReadyState() == false ) {
	  mLogger.fatal( Exceptions.gsSystemInHaltState );

	  return Response.status( HttpCodes.SERVICE_UNAVAILABLE )
		  .entity( Exceptions.gsSystemInHaltState ).build();
	}// if (moSessionStore.isSystemInReadyState() == false)

	moSessionStore.deregisterSession( sessionKey );

	return Response.status( HttpCodes.OK ).build();
  }/*
    * public Response logoff(@HeaderParam(Constants.SESSION_KEY) String
    * sessionKey)
    */

  /**
   * 
   */
  private Store moStore;

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

  /*
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger
	  .getLogger( Authentication.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;
}/* public class Authentication */
