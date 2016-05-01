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
package org.aprilis.jrest.compile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.constant.Exceptions;
import org.aprilis.jrest.store.Definition;
import org.aprilis.jrest.store.Store;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class QueryBinder {
  /**
   * 
   */
  public QueryBinder() {
	moDefinition = null;
	msDefinitionQuery = null;

	moJsonParser = new JSONParser();
	moStringWriter = new StringWriter();
	moPrintWriter = new PrintWriter( moStringWriter );

	moDefinitionStore = Store.instance();
  }/* public QueryBinder() */

  /**
   * 
   * @param sJrestKey
   * @param sJsonData
   * @param isTypeGet
   * @return
   */
  public String buildQueryForKey(String sJrestKey, String sJsonData, short defType) {
	switch( defType ) {
	  case Constants.gshDefTypeSet:
		moDefinition = moDefinitionStore.getDefinition( sJrestKey, false );
		break;
	  case Constants.gshDefTypeGet:
		moDefinition = moDefinitionStore.getDefinition( sJrestKey, true );
		break;
	  case Constants.gshDefTypeDownload:
		moDefinition = moDefinitionStore.getDownloadDefinition( sJrestKey );
		break;
	}

	if( moDefinition != null ) {
	  msDefinitionQuery = moDefinition.getQuery();

	  mLogger.debug( String.format( Exceptions.gsDefinitionQuery, msDefinitionQuery ) );

	  if( sJsonData != null ) {
		String sTrimmedJsonData = sJsonData.trim();

		if( ( sTrimmedJsonData.length() > 0 )
			&& ( sTrimmedJsonData.equals( Constants.DEFAULT_JSON_DATA ) == false ) ) {
		  return bindParamsAndBuildQuery( sTrimmedJsonData );
		}// if ((sTrimmedJsonData.length() > 0) ... )
	  }// if (sJsonData != null)

	  return msDefinitionQuery;
	}// if (moDefinition != null)

	mLogger.error( String.format( Exceptions.gsNoDefinitionFound, sJrestKey ) );

	return null;
  }/*
    * public String buildQueryForKey(String sJrestKey, String sJsonData, boolean
    * isTypeGet)
    */

  /**
   * 
   * @param sJsonData
   * @return
   */
  @SuppressWarnings("unchecked")
  private String bindParamsAndBuildQuery(String sJsonData) {
	try {
	  // Buffer on which query is built
	  StringBuilder sbSqlQuery = new StringBuilder();

	  // Delim split representation of the defined query
	  String sSplitQuery[] = msDefinitionQuery.split( Constants.gsQueryBindDelimiter );

	  // Data supplied by user for substituting with query
	  HashMap< String, String > hmapJsonData = (JSONObject) moJsonParser
		  .parse( sJsonData );

	  // Length of the split array; size has been realigned
	  short splitLength = (short) ( sSplitQuery.length - 1 );

	  mLogger.debug( String.format( Exceptions.gsGivenToExpectedDataRatio, splitLength,
		  hmapJsonData.size() ) );

	  // Binder just checks whether there are sufficient number of parameters to
	  // supply for the query that it is about to build. The user may pass more
	  // number
	  // of parameters to the REST call, so that they can be consumed by Before
	  // or
	  // After calls. As long as Binder finds the parameters it is looking for
	  // it
	  // should be fine.
	  //
	  if( splitLength <= hmapJsonData.size() ) {
		for( short splitIndex = 0; splitIndex < splitLength; splitIndex++ ) {
		  // Check whether the key we are looking for is actually present
		  // otherwise we have an opportunity to break early, as this
		  // framed query would never execute, due to null being appended
		  // as part of the filler.
		  //
		  if( hmapJsonData.containsKey( String.valueOf( splitIndex + 1 ) ) ) {
			sbSqlQuery.append( sSplitQuery[splitIndex] );
			sbSqlQuery.append( Constants.gcQueryParamEnclosure );
			sbSqlQuery.append( hmapJsonData.get( String.valueOf( splitIndex + 1 ) ) );
			sbSqlQuery.append( Constants.gcQueryParamEnclosure );

			continue;
		  }// if (hmapJsonData.containsKey(String.valueOf(splitIndex + 1)))

		  return null;
		}// for (short splitIndex = 0; splitIndex < splitLength; splitIndex++)

		sbSqlQuery.append( sSplitQuery[splitLength] );

		return sbSqlQuery.toString();
	  } else {
		mLogger.error( String.format( Exceptions.gsInsufficientQueryParamValues,
			msDefinitionQuery, splitLength, hmapJsonData.size() ) );
	  }// if( sSplitQuery.length == hmapJsonData.size())

	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( String.format( Exceptions.gsMalformedJsonData, sJsonData ) );
	  mLogger.error( moStringWriter.toString() );
	}// end of try ... catch block

	return null;
  }/* private String bindParamsAndBuildQuery(String sJsonData) */

  /**
   * 
   * @param sJsonData
   * @return
   */
  public String buildQueryForAuth(String sJsonData) {
	moDefinition = moDefinitionStore.getAuthenticationDefinition();
	msDefinitionQuery = moDefinition.getQuery();

	if( sJsonData != null ) {
	  String sTrimmedJsonData = sJsonData.trim();

	  if( ( sTrimmedJsonData.length() > 0 )
		  && ( sTrimmedJsonData.equals( Constants.DEFAULT_JSON_DATA ) == false ) ) {
		return bindParamsAndBuildQuery( sTrimmedJsonData );
	  }// if ((sTrimmedJsonData.length() > 0) ... )
	}// if (sJsonData != null)

	return msDefinitionQuery;
  }/*
    * public String buildQueryForKey(String sJrestKey, String sJsonData, boolean
    * isTypeGet)
    */

  /**
   * Handle to the definition store where all the definitions are store by JREST
   */
  private Store moDefinitionStore;

  /**
   * Instance level handle to the JSON parser, which is used for parsing the
   * JSON data supplied by the user for formulating the query.
   */
  private JSONParser moJsonParser;

  /**
   * Handle or reference to the Definition object, for which QueryBinder would
   * construct the SQL statement
   */
  private Definition moDefinition;

  /**
   * Place where the query associated with the definition is stored.
   */
  private String msDefinitionQuery;

  /*
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger
	  .getLogger( QueryBinder.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;
}/* public class QueryBinder */
