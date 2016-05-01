/*
 * Copyright 2013 Aprilis Design Studios
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

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.constant.Exceptions;
import org.aprilis.jrest.db.ConnectionDetails;
import org.aprilis.jrest.execute.ExecutionEngine;
import org.aprilis.jrest.store.Definition;
import org.aprilis.jrest.store.Session;
import org.aprilis.jrest.store.Store;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author h & rk
 * 
 */
public class Compile extends Thread {
  /**
   * 
   * @throws Exception
   */
  private Compile() {
	mbJrestRepositoryRead = false;
	mbInitExecutionEngine = false;

	mhmapDefinitionDetails = null;
	msJrestSweepInterval = null;
	msPathToJrestRepo = null;
	mfDefinitionFile = null;

	msPathToDefinitionFiles = Exceptions.gsPathToDefinitionFilesYetToBeSet;
	mlJrestSweepInterval = Constants.glDefaultRefreshInterval;

	moJsonParser = new JSONParser();
	moStringWriter = new StringWriter();
	moPrintWriter = new PrintWriter( moStringWriter );

	moStore = Store.instance();
	moSessionStore = Session.instance();
	moExecutionEngine = ExecutionEngine.instance();
  }/* private Compile() */

  /**
   * Clones are not supported for this class so this prevents the users from
   * doing so.
   */
  public Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException( "This is a Singleton Ojbect; Buzz off" );
  }/* public Object clone() throws CloneNotSupportedException */

  /**
   * 
   * @return
   * @throws Exception
   */
  public static Compile instance() {
	if( __instance == null ) {
	  synchronized( Compile.class ) {
		if( __instance == null ) {
		  __instance = new Compile();
		} /* if (__instance == null) */
	  } /* synchronized (Compile.class) */
	} /* if (__instance == null) */

	return __instance;
  }/* public static Compile Instance() */

  /**
   * 
   * @return
   */
  private boolean initialize() {
	try {
	  if( msPathToDefinitionFiles
		  .equals( System.getenv( Constants.gsPathVariable ) ) == false ) {
		msPathToDefinitionFiles = System.getenv( Constants.gsPathVariable );

		if( ( msPathToDefinitionFiles == null )
			|| ( msPathToDefinitionFiles.length() == 0 ) ) {
		  msPathToDefinitionFiles = Exceptions.gsPathToDefinitionFilesYetToBeSet;

		  mLogger.fatal( Exceptions.gsPathNotDefined );

		  return false;
		} // if ((msPathToDefinitionFiles == null) ||
		  // (msPathToDefinitionFiles.length()

		mfDefinitionFile = null;
		mfDefinitionFile = new File( msPathToDefinitionFiles );

		if( mfDefinitionFile.isDirectory() == false ) {
		  mLogger.fatal(
			  String.format( Exceptions.gsPathNotFound, msPathToDefinitionFiles ) );

		  return false;
		} // if (mfDefinitionFile.isDirectory() == false)

		mfDefinitionFile = null;
		mfDefinitionFile = new File( msPathToDefinitionFiles,
			Constants.gsJrestRepository );

		if( ( mfDefinitionFile.exists() == false )
			|| ( mfDefinitionFile.isDirectory() == false ) ) {
		  if( mfDefinitionFile.mkdirs() == false ) {
			mLogger.error( Exceptions.gsCantCreateJrestRepository );

			return false;
		  } // if (mfDefinitionFile.mkdirs() == false )
		} // if( (mfDefinitionFile.exists() == false ) ||
		  // (mfDefinitionFile.isDirectory() ==
		  // false))

		msPathToJrestRepo = mfDefinitionFile.getAbsolutePath();
	  } // if(msPathToDefinitionFiles.equals(System.getenv(Constants.gsPathVariable))

	  String msDownloadPath = System.getenv( Constants.gsDownloadPathVariable );
	  moStore.setDownloadPath( msDownloadPath );

	  msJrestSweepInterval = System.getenv( Constants.gsRefreshInterval );

	  if( msJrestSweepInterval != null ) {
		mlJrestSweepInterval = Long.parseLong( msJrestSweepInterval );
		mlJrestSweepInterval *= Constants.gshMillisInSecond;

		if( mlJrestSweepInterval <= Constants.gshZero
			|| mlJrestSweepInterval <= Constants.glDefaultRefreshInterval ) {
		  mLogger.warn( Exceptions.gsLowSweepInterval );

		  mlJrestSweepInterval = Constants.glDefaultRefreshInterval;
		} // if (mlJrestSweepInterval <= 0 ... )
	  } // if (msJrestSweepInterval != null)
	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );

	  return false;
	} // end of try ... catch

	return true;
  }/* private boolean initialize() */

  /**
   * 
   */
  private void iterateOverDefinitionDirectories() {
	for( short repoIndex = Constants.gshMaxRepoDepth; repoIndex > Constants.gshZero; repoIndex-- ) {
	  if( repoIndex == Constants.gshMaxRepoDepth ) {
		if( mbJrestRepositoryRead == false ) {
		  mfDefinitionFile = new File( msPathToJrestRepo );

		  mLogger.debug( String.format( Exceptions.gsJrestCurrentScanningFolder,
			  msPathToJrestRepo ) );

		  /*
		   * loadDefinitions to be executed here only in the case of
		   * mbJrestRepositoryRead being false and (repoIndex ==
		   * Constants.gshMaxRepoDepth).
		   */
		  loadDefinitions( repoIndex );
		} // if (mbJrestRepositoryRead == false)
	  } else {
		mLogger.debug( String.format( Exceptions.gsJrestCurrentScanningFolder,
			msPathToDefinitionFiles ) );

		mfDefinitionFile = new File( msPathToDefinitionFiles );

		loadDefinitions( repoIndex );
	  } // if (mbJrestRepositoryRead == false)
	} // for (short repoIndex = Constants.gshMaxRepoDepth; ... )
  }/* private iterateOverDefinitionDirectories() */

  /**
   * 
   */
  private void loadDefinitions(short shRepositoryDepth) {
	try {
	  for( File definitionFile : mfDefinitionFile.listFiles() ) {
		if( definitionFile.getName().endsWith( Constants.gsDefFileExtension ) ) {
		  // Read the contents of the definition file only upto the
		  // first "!" character is noticed; which would be
		  // corresponding to a definition, and repeat the same until
		  // the time EOF is reached
		  //
		  boolean hasEncounteredErrors = false;
		  FileInputStream fInputStream = new FileInputStream( definitionFile );
		  Scanner defFileScanner = new Scanner( fInputStream );

		  defFileScanner.useDelimiter( Constants.gsJsonDefinitionDelimiter );

		  if( definitionFile.getName()
			  .equalsIgnoreCase( Constants.gsJrestDefinitionFileName ) == false ) {

			while( defFileScanner.hasNext() ) {
			  String jsonContent = defFileScanner.next();

			  if( loadJsonDefinition( jsonContent ) == false ) {
				// Something went wrong with the JSON we attempted
				// to parse, so, tell the user that JSON is screwed
				mLogger.error( String.format( Exceptions.gsParseError,
					definitionFile.getName(), jsonContent ) );

				hasEncounteredErrors = true;
			  } // if(loadJsonDefinition(jsonContent) == false)
			} // while(defFileScanner.hasNext())
		  } else {
			while( defFileScanner.hasNext() ) {
			  String jsonContent = defFileScanner.next();

			  if( loadJrestDefinition( jsonContent ) == false ) {
				// Something went wrong with the JSON we attempted
				// to parse, so, tell the user that JSON is screwed
				mLogger.error( String.format( Exceptions.gsParseError,
					definitionFile.getName(), jsonContent ) );

				hasEncounteredErrors = true;
			  } // if(loadJrestDefinition(jsonContent) == false)
			} // while(defFileScanner.hasNext())
		  } // if(parseJsonDefinition(jsonContent) == false)

		  defFileScanner.close();
		  fInputStream.close();

		  if( shRepositoryDepth == Constants.gshMinRepoDepth ) {
			if( hasEncounteredErrors == false ) {
			  File repoFile = new File( msPathToJrestRepo, definitionFile.getName() );

			  if( ( repoFile.exists() == true ) && ( repoFile.isDirectory() == false ) ) {
				repoFile.delete();
			  } // if ((repoFile.exists() == true) && (repoFile.isDirectory() ==
			    // false))

			  definitionFile
				  .renameTo( new File( msPathToJrestRepo, definitionFile.getName() ) );
			} else {
			  definitionFile.renameTo( new File(
				  definitionFile.getAbsoluteFile() + Constants.gsJsonDefErrorFileExtn ) );
			} // if (hasEncounteredErrors == false)
		  } // if (shRepositoryDepth == Constants.gshMinRepoDepth)
		} // if(definitionFile.getName().endsWith(..)
	  } // for(File definitionFile : mfDefinitionFile.listFiles())

	  if( shRepositoryDepth == Constants.gshMaxRepoDepth ) {
		mbJrestRepositoryRead = true;
	  }
	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );
	} // end of try ... catch block
  }/* private void loadDefinitions() */

  /**
   * 
   * @param sJsonDef
   * @return
   */
  @SuppressWarnings("unchecked")
  private boolean loadJrestDefinition(String sJsonDef) {
	try {
	  String newLineTrimmedJson = sJsonDef.replaceAll( Constants.gsTrimFindeString,
		  Constants.gsEmptyString );
	  String spaceRemovedJson = newLineTrimmedJson
		  .replaceAll( Constants.gsRemoveSpacesExcludingQuotes, Constants.gsEmptyString );

	  mLogger.info( String.format( Exceptions.gsTrimmedJsonString, spaceRemovedJson ) );

	  mhmapDefinitionDetails = (JSONObject) moJsonParser.parse( spaceRemovedJson );

	  for( Map.Entry< String, HashMap< String, Object > > jsonEntry : mhmapDefinitionDetails
		  .entrySet() ) {
		if( jsonEntry.getKey() != null
			&& jsonEntry.getKey().length() >= Constants.gshMinDefinitionKeyLength
			&& jsonEntry.getValue() != null ) {
		  /*
		   * Check whether the definition we are parsing is of the type
		   * Authentication, if so, set the store with authentication details.
		   */
		  if( jsonEntry.getKey().equals( Constants.gsAuthDefinitionKey ) ) {

			return loadAuthDefinition( jsonEntry.getValue() );
		  } // if(jsonEntry.getKey().equals(Constants.gsAuthDefinitionKey))

		  /*
		   * Check whether the definition that is being parsed is of the type
		   * JDBC connection string, if so, set the store with the connection
		   * string
		   */
		  if( jsonEntry.getKey().equals( Constants.gsJdbcDefinitionKey ) ) {
			HashMap< String, String > oJsonData = (JSONObject) jsonEntry.getValue();

			return loadJDBCDefinition( oJsonData );

		  } // if(jsonEntry.getKey().equals(Constants.gsJdbcDefinitionKey))
		} // if (jsonEntry.getValue() != null)
	  } // for (Map.Entry<String, HashMap<String, Object>> jsonEntry
	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );
	} // end of try ... catch block

	return false;
  }/* private boolean loadJrestDefinition(String sJsonDef) */

  /**
   * 
   * @param hmapJsonAuth
   * @return
   */
  private boolean loadAuthDefinition(HashMap< String, Object > hmapJsonAuthDef) {
	// 1. Check for presence of Query keyword - Done
	// 2. Check for not null or empty value for Query keyword - Done
	//
	if( hmapJsonAuthDef.containsKey( Constants.gsLangTokenQuery ) ) {
	  String sQuery = hmapJsonAuthDef.get( Constants.gsLangTokenQuery ).toString().trim();

	  if( sQuery != null && sQuery.length() > Constants.gshMinQueryLength ) {
		if( sQuery.charAt(
			sQuery.length() - Constants.gshOne ) == Constants.gcDelimSemiColon ) {
		  moStore.addAuthenticationQuery( sQuery );
		} else {
		  mLogger.error( Exceptions.gsQuerySyntaxError );

		  return false;
		} // if (sQuery.charAt(sQuery.length() - Constants.gshOne) ==
		  // Constants.gcDelimSemiColon)

	  } else {
		mLogger.error( Exceptions.gsEmptyOrInvalidQueryGiven );

		return false;
	  } // if (sQuery != null && sQuery.length() > Constants.gshMinQueryLength)
	} else {
	  mLogger.error( Exceptions.gsTokenQueryMissingInAuth );

	  return false;
	} // end of if (hmapJsonAuthDef.containsKey(Constants.gsLangTokenQuery))

	// 3. Check for presence of Delim keyword - Done
	// 4. Check for not null or empty value for Delim keyword - Done
	// 5. Check for valid delimiter set - Done
	if( hmapJsonAuthDef.containsKey( Constants.gsLangTokenDelim ) ) {
	  String sDelimiter = hmapJsonAuthDef.get( Constants.gsLangTokenDelim ).toString();

	  if( sDelimiter != null && sDelimiter.length() == Constants.gshMinDelimiterLength ) {
		char cDelimiter = sDelimiter.charAt( 0 );

		switch( cDelimiter ) {
		  case Constants.gcDelimColon:
		  case Constants.gcDelimComma:
		  case Constants.gcDelimHash:
		  case Constants.gcDelimSemiColon:
			moStore.addAuthenticationDelimiter( hmapJsonAuthDef
				.get( Constants.gsLangTokenDelim ).toString().charAt( 0 ) );
			break;

		  default:
			mLogger.error( Exceptions.gsInvalidAuthTokenDelimUsed );

			return false;
		}// end of switch (cDelimiter)
	  } else {
		mLogger.error( Exceptions.gsEmptyOrTooLongDelimGiven );
	  } // end of if(sDelimiter != null && ... )

	} // if (hmapJsonAuthDef.containsKey(Constants.gsLangTokenDelim))

	return true;
  }/* private boolean loadAuthDefinition(String sJsonDef) */

  /**
   * 
   * @param hmapJsonJDBCDef
   * @return
   */
  private boolean loadJDBCDefinition(HashMap< String, String > hmapJsonJDBCDef) {
	// 1. Check for presence of JDBC keyword - Done
	// 2. Check for presence of all the connection keywords - Done
	//
	if( hmapJsonJDBCDef != null
		&& hmapJsonJDBCDef.size() == Constants.gshConnectionTokenCount ) {
	  if( hmapJsonJDBCDef.containsKey( Constants.gsConnDatabase )
		  && hmapJsonJDBCDef.containsKey( Constants.gsConnDbType )
		  && hmapJsonJDBCDef.containsKey( Constants.gsConnHostName )
		  && hmapJsonJDBCDef.containsKey( Constants.gsConnPassWord )
		  && hmapJsonJDBCDef.containsKey( Constants.gsConnPortNumber )
		  && hmapJsonJDBCDef.containsKey( Constants.gsConnUserName ) ) {
		ConnectionDetails oConnection = new ConnectionDetails();

		oConnection.setHostName( hmapJsonJDBCDef.get( Constants.gsConnHostName ) );
		oConnection.setDatabaseName( hmapJsonJDBCDef.get( Constants.gsConnDatabase ) );
		oConnection.setDatabaseType( hmapJsonJDBCDef.get( Constants.gsConnDbType ) );
		oConnection.setPassWord( hmapJsonJDBCDef.get( Constants.gsConnPassWord ) );
		oConnection.setPortNumber( hmapJsonJDBCDef.get( Constants.gsConnPortNumber ) );
		oConnection.setUserName( hmapJsonJDBCDef.get( Constants.gsConnUserName ) );

		moStore.setJdbcConnectionDetails( oConnection );
	  } else {
		mLogger.error( Exceptions.gsMissingConnectionParameters );

		return false;
	  } // if (hmapJsonJDBCDef.containsKey(Constants.gsConnDatabase)
	} else {
	  mLogger.error( Exceptions.gsEmptyOrInvalidConnectionGiven );

	  return false;
	} // if (hmapJsonJDBCDef != null && hmapJsonJDBCDef.size()

	return true;
  }/*
    * private boolean loadJDBCDefinition(HashMap<String, String>
    * hmapJsonJDBCDef)
    */

  /**
   * 
   * @param sJsonDef
   * @return
   */
  @SuppressWarnings("unchecked")
  private boolean loadJsonDefinition(String sJsonDef) {
	try {
	  String commentsStrippedJson = sJsonDef
		  .replaceAll( Constants.gsStripCommentLineRegEx, Constants.gsEmptyString );
	  String newLineTrimmedJson = commentsStrippedJson
		  .replaceAll( Constants.gsTrimFindeString, Constants.gsEmptyString );
	  String spaceRemovedJson = newLineTrimmedJson
		  .replaceAll( Constants.gsRemoveSpacesExcludingQuotes, Constants.gsEmptyString );

	  mLogger.info( String.format( Exceptions.gsTrimmedJsonString, spaceRemovedJson ) );

	  mhmapDefinitionDetails = (JSONObject) moJsonParser.parse( spaceRemovedJson );

	  for( Map.Entry< String, HashMap< String, Object > > jsonEntry : mhmapDefinitionDetails
		  .entrySet() ) {
		// 1. Check whether definition key is valid (not null or empty) - Done
		// 2. Check whether value associated to key is valid (not null, since
		// its an
		// object) - Done
		//
		if( jsonEntry.getKey() != null
			&& jsonEntry.getKey().length() >= Constants.gshMinDefinitionKeyLength
			&& jsonEntry.getValue() != null ) {

		  String sJsonKey = jsonEntry.getKey().toUpperCase();
		  if( ( sJsonKey.equals( Constants.gsAuthDefinitionKey ) == true )
			  || ( sJsonKey.equals( Constants.gsJdbcDefinitionKey ) == true ) ) {
			mLogger.warn( Exceptions.gsReservedKeywordNotAllowed );

			continue;
		  } // if ((sJsonKey.equals(Constants.gsAuthDefinitionKey) == true) ...
		    // )

		  // 3. Check for mandatory keywords Type - Done
		  //
		  // if ( jsonEntry.getValue().containsKey(Constants.gsLangTokenQuery)
		  // &&
		  if( jsonEntry.getValue().containsKey( Constants.gsLangTokenType ) ) {

			Definition apiDefinition = new Definition();

			String sQueryType = jsonEntry.getValue().get( Constants.gsLangTokenType )
				.toString().trim();

			String sQueryValue = (String) jsonEntry.getValue()
				.get( Constants.gsLangTokenQuery );
			if( ( sQueryType.equals( Constants.gsLangDefTypeGet )
				|| sQueryType.equals( Constants.gsLangDefTypeSet ) )
				&& sQueryValue == null ) {
			  mLogger.error( Exceptions.gsMissingMandatoryKeywordsInDefFile );

			  return false;
			}

			if( sQueryValue != null ) {
			  sQueryValue = jsonEntry.getValue().get( Constants.gsLangTokenQuery )
				  .toString().trim();

			  // 5. Check for value of keyword Query is not null or empty - Done
			  // 6. Check if Query value is terminated with a semicolon - Done
			  //
			  if( sQueryValue.length() > Constants.gshMinQueryLength ) {
				if( sQueryValue.charAt( sQueryValue.length()
					- Constants.gshOne ) == Constants.gcDelimSemiColon ) {
				  apiDefinition.setQuery( sQueryValue );
				} else {
				  mLogger.error( Exceptions.gsQuerySyntaxError );

				  return false;
				} // if (sQuery.charAt(sQuery.length() - Constants.gshOne) ==
				  // Constants.gcDelimSemiColon)
			  } else {
				mLogger.error( Exceptions.gsEmptyOrInvalidQueryGiven );

				return false;
			  } // end of if (sQueryValue != null && ... )
			} // if (jsonEntry.getValue().get(Constants.gsLangTokenQuery) !=
			  // null)

			// 9. Check for values of Before and After if they are mentioned -
			// Done
			//
			if( jsonEntry.getValue().containsKey( Constants.gsLangTokenBefore ) ) {
			  try {
				HashMap< String, String > hmapBeforeMapping = (JSONObject) jsonEntry
					.getValue().get( Constants.gsLangTokenBefore );

				if( loadJsonBeforeTagInfo( jsonEntry.getKey(), hmapBeforeMapping,
					apiDefinition ) == false ) {
				  return false;
				} // if (loadJsonBeforeTagInfo(jsonEntry.getKey(), ... )
			  } catch( ClassCastException e ) {
				mLogger.error( String.format( Exceptions.gsMalformedDefinition,
					Constants.gsLangTokenBefore, jsonEntry.getKey() ) );

				return false;
			  } // end of try ... catch block

			} // end of
			  // if(jsonEntry.getValue().containsKey(Constants.gsLangTokenBefore))

			if( jsonEntry.getValue().containsKey( Constants.gsLangTokenAfter ) ) {
			  try {
				HashMap< String, String > hmapAfterMapping = (JSONObject) jsonEntry
					.getValue().get( Constants.gsLangTokenAfter );

				if( loadJsonAfterTagInfo( jsonEntry.getKey(), hmapAfterMapping,
					apiDefinition ) == false ) {
				  return false;
				} // if (loadJsonAfterTagInfo(jsonEntry.getKey(), ... )
			  } catch( ClassCastException e ) {
				mLogger.error( String.format( Exceptions.gsMalformedDefinition,
					Constants.gsLangTokenAfter, jsonEntry.getKey() ) );

				return false;
			  } // end of try ... catch block
			} // end of if
			  // (jsonEntry.getValue().containsKey(Constants.gsLangTokenAfter))

			loadRoles( jsonEntry.getKey(), (JSONObject) jsonEntry.getValue(),
				apiDefinition );

			/*
			 * Upload and download related values handling
			 */
			if( jsonEntry.getValue().containsKey( Constants.gsLangFileType ) ) {

			  String fileType = (String) jsonEntry.getValue()
				  .get( Constants.gsLangFileType );
			  if( sQueryType.equals( Constants.gsLangDefTypeUpload ) ) {
				if( fileType.equals( Constants.gsImageType ) ) {
				  apiDefinition.setFileType( fileType );
				} else {
				  mLogger.error( Exceptions.gsInvalidUploadFileType );

				  return false;
				} // if (fileType.equals(Constants.gsImageType))
			  } else if( sQueryType.equals( Constants.gsLangDefTypeDownload ) ) {
				if( fileType.equals( Constants.gsPDFType )
					|| ( fileType.equals( Constants.gsEPubType ) ) ) {
				  apiDefinition.setFileType( fileType );
				} else {
				  mLogger.error( Exceptions.gsInvalidDownloadFileType );

				  return false;
				} // if (fileType.equals(Constants.gsPDFType) || ...)
			  } // if (sQueryType.equals(Constants.gsLangDefTypeUpload))

			} // if (jsonEntry.getValue().containsKey(Constants.gsLangFileType))

			if( jsonEntry.getValue().containsKey( Constants.gsLangFilePath ) ) {
			  apiDefinition.setFilePath(
				  (String) jsonEntry.getValue().get( Constants.gsLangFilePath ) );
			} // if (jsonEntry.getValue().containsKey(Constants.gsLangFilePath))

			if( jsonEntry.getValue().containsKey( Constants.gsLangGenerateFileName ) ) {

			  String strGenerateFileNameValue = (String) jsonEntry.getValue()
				  .get( Constants.gsLangGenerateFileName );

			  if( strGenerateFileNameValue.equals( Constants.gsYes ) ) {
				apiDefinition.setGenerateFileName( true );
			  } else if( strGenerateFileNameValue.equals( Constants.gsNo ) ) {
				apiDefinition.setGenerateFileName( false );
			  } else {
				mLogger.error( Exceptions.gsInvalidGenerateValue );

				return false;
			  } // if (strGenerateFileNameValue == Constants.gsYes)
			} // if
			  // (jsonEntry.getValue().containsKey(Constants.gsLangGenerateFileName))

			/*
			 * Download type definitions processing considers following keys:
			 * FileType key should be present. Query is optional. If query is
			 * present, filename header param will not be considered. Query
			 * should return a string filename. FileName header param will be
			 * considered only when NO Query is configured for a download
			 * definition. Downloadable File should be present in the
			 * $JREST_DOWNLOAD_PATH. Roles is mandatory.
			 */

			// 6. Check for value of keyword Type is either GET or SET or Upload
			// or
			// Download - Done
			//
			if( sQueryType != null ) {
			  if( sQueryType.equals( Constants.gsLangDefTypeGet ) ) {
				moStore.addGetDefinition( jsonEntry.getKey(), apiDefinition );
			  } else if( sQueryType.equals( Constants.gsLangDefTypeSet ) ) {
				moStore.addSetDefinition( jsonEntry.getKey(), apiDefinition );
			  } else if( sQueryType.equals( Constants.gsLangDefTypeUpload ) ) {
				moStore.addUploadDefinition( jsonEntry.getKey(), apiDefinition );
			  } else if( sQueryType.equals( Constants.gsLangDefTypeDownload ) ) {
				moStore.addDownloadDefinition( jsonEntry.getKey(), apiDefinition );
			  } else {
				mLogger.error( Exceptions.gsInvalidDefinitionTypeGiven );

				return false;
			  } // end of if (defType.equals(Constants.gsLangDefTypeGet))

			  return true;
			} // if(jsonEntry.getValue().containsKey(Constants.gsLangTokenType))
		  } else {
			mLogger.error( Exceptions.gsMissingMandatoryKeywordsInDefFile );
		  } // end of if
		    // (jsonEntry.getValue().containsKey(Constants.gsLangTokenQuery)
		} else {
		  mLogger.error( Exceptions.gsEmptyDefinition );
		} // end of if (jsonEntry.getKey() != null && .... )
	  } // for (Map.Entry<String, HashMap<String, Object>> jsonEntry
	} catch( Exception e ) {
	  e.printStackTrace();

	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );
	} // end of try ... catch block

	return false;
  }/* private boolean loadJsonDefinition(String sJsonDef) */

  /**
   * 
   * @param jRestKey
   * @param hmapBeforeMapping
   * @param apiDefinition
   * @return
   */
  private boolean loadJsonBeforeTagInfo(String jRestKey,
	  HashMap< String, String > hmapBeforeMapping, Definition apiDefinition) {
	System.out.println( "Here is the Map" + hmapBeforeMapping.toString() );

	if( hmapBeforeMapping.containsKey( Constants.gsLangDefKeywordClass )
		&& hmapBeforeMapping.containsKey( Constants.gsLangDefKeywordMethod )
		&& hmapBeforeMapping.containsKey( Constants.gsLangDefKeywordConsume ) ) {
	  String sClassName = hmapBeforeMapping.get( Constants.gsLangDefKeywordClass );
	  String sMethodName = hmapBeforeMapping.get( Constants.gsLangDefKeywordMethod );
	  String sResultUsage = hmapBeforeMapping.get( Constants.gsLangDefKeywordConsume );

	  if( sClassName != null && sMethodName != null && sResultUsage != null ) {
		if( sClassName.length() >= Constants.gshMinFunctionLength ) {
		  apiDefinition.setFqcnBefore( sClassName );
		} else {
		  mLogger.error( Exceptions.gsInvalidFunctionNameGiven );

		  return false;
		} // if (sClassName.length() >= Constants.gshMinFunctionLength)

		if( sMethodName.length() >= Constants.gshMinFunctionLength ) {
		  apiDefinition.setBeforeMethod( sMethodName );
		} else {
		  // Method name too small error
		  mLogger.error( Exceptions.gsInvalidMethodNameGiven );

		  return false;
		} // if (sMethodName.length() >= Constants.gshMinFunctionLength)

		if( sResultUsage.equals( Constants.gsConsumeBeforeFalse ) ) {
		  apiDefinition.setBeforeUsagePattern( false );
		} else if( sResultUsage.equals( Constants.gsConsumeBeforeTrue ) ) {
		  apiDefinition.setBeforeUsagePattern( true );
		} else {
		  mLogger.error( Exceptions.gsUnknownConsumeValue );

		  return false;
		} // if (sResultUsage.equals(Constants.gsConsumeBeforeFalse))
	  } else {
		// Empty values supplied for the keywords Method or Consume
		mLogger.error(
			String.format( Exceptions.gsMissingMandatoryValuesInBefore, jRestKey ) );

		return false;
	  } // if (sClassName != null && sMethodName != null && sResultUsage !=
	    // null)
	} else {
	  // Syntax error
	  mLogger.error(
		  String.format( Exceptions.gsMissingMandatoryKeywordsInBefore, jRestKey ) );

	  return false;
	} // if (hmapBeforeMapping.containsKey(.... )

	return true;
  }/*
    * private boolean loadJsonBeforeTagInfo(String jRestKey, HashMap<String,
    * String> ... )
    */

  /**
   * 
   * @param jRestKey
   * @param hmapAfterMapping
   * @param apiDefinition
   * @return
   */
  private boolean loadJsonAfterTagInfo(String jRestKey,
	  HashMap< String, String > hmapAfterMapping, Definition apiDefinition) {
	if( hmapAfterMapping.containsKey( Constants.gsLangDefKeywordClass )
		&& hmapAfterMapping.containsKey( Constants.gsLangDefKeywordMethod ) ) {
	  String sClassName = hmapAfterMapping.get( Constants.gsLangDefKeywordClass );
	  String sMethodName = hmapAfterMapping.get( Constants.gsLangDefKeywordMethod );

	  if( sClassName != null && sMethodName != null ) {
		if( sClassName.length() >= Constants.gshMinFunctionLength ) {
		  apiDefinition.setFqcnAfter( sClassName );
		} else {
		  mLogger.error( Exceptions.gsInvalidFunctionNameGiven );

		  return false;
		} // if (sClassName.length() >= Constants.gshMinFunctionLength)

		if( sMethodName.length() >= Constants.gshMinFunctionLength ) {
		  apiDefinition.setAfterMethod( sMethodName );
		} else {
		  mLogger.error( Exceptions.gsInvalidMethodNameGiven );

		  return false;
		} // if(sMethodName.length() >= Constants.gshMinFunctionLength)
	  } else {
		// throw missing values error
		mLogger.error(
			String.format( Exceptions.gsMissingMandatoryValuesInAfter, jRestKey ) );
		return false;
	  } // if (sClassName != null && sMethodName != null)

	} else {
	  mLogger.error(
		  String.format( Exceptions.gsMissingMandatoryKeywordsInAfter, jRestKey ) );
	  return false;
	} // if (hmapAfterMapping.containsKey(Constants.gsLangDefKeywordClass)

	return true;
  }/*
    * private boolean loadJsonAfterTagInfo(String jRestKey, HashMap<String,
    * String> ... )
    */

  private void loadRoles(String jRestKey, HashMap< String, Object > hmapRoles,
	  Definition apiDefinition) {
	// 1. If Roles keyword is present then check for emptyness
	// or null of its value -Done
	// 2. Automatically substitute -3022 if no roles are present - Done
	// 3. If value for Roles is not JSON array catch exception and inform - Done
	//
	if( hmapRoles.containsKey( Constants.gsLangTokenRoles ) ) {
	  JSONArray defRoles = (JSONArray) hmapRoles.get( Constants.gsLangTokenRoles );

	  if( defRoles != null && defRoles.size() >= Constants.gshMinNumberOfRoles ) {
		for( short shRoleIndex = 0; shRoleIndex < defRoles.size(); shRoleIndex++ ) {
		  String sRole = defRoles.get( shRoleIndex ).toString().trim();
		  if( sRole.length() > 0 ) {
			apiDefinition.addRole( sRole );
		  } // if (sRole.length() > 0)
		} // for (short shRoleIndex = 0; shRoleIndex < defRoles.size();
		  // shRoleIndex++)

		if( apiDefinition.getRoles().size() == 0 ) {
		  apiDefinition.addRole( String.valueOf( Constants.gshDefaultRoleId ) );
		} // if (apiDefinition.getRoles().size() == 0)
	  } else {
		mLogger.error( Exceptions.gsNullSetOfRolesGiven );
	  } // if (defRoles != null && defRoles.size() ... )
	} else {
	  apiDefinition.addRole( String.valueOf( Constants.gshDefaultRoleId ) );

	  mLogger.debug( String.format( Exceptions.gsDefaultRoleAdded,
		  apiDefinition.getRoles().toString(), jRestKey ) );
	} // end of if(jsonEntry.getValue().containsKey(Constants.gsLangTokenRoles))
  }/*
    * private void loadRoles(String jRestKey, HashMap<String, Object> hmapRoles,
    * ... )
    */

  /**
   * 
   */
  public void run() {
	try {
	  for( ;; ) {
		if( initialize() ) {
		  /*
		   * Load all the new/old definitions from the file system to the memory
		   */
		  iterateOverDefinitionDirectories();
		} /* if (initialize()) */

		/*
		 * Do sanity check on the ready state of the JREST
		 */
		isSystemReady();

		if( moSessionStore.isSystemInReadyState() == false ) {
		  mLogger.fatal( String.format( Exceptions.gsHaltSleepIntervalMessage,
			  Exceptions.gsSystemInHaltState,
			  ( Constants.glHaltSleepInterval / 1000 ) ) );

		  sleep( Constants.glHaltSleepInterval );

		  continue;
		} // if (moSessionStore.isSystemInReadyState() == false)

		if( mbInitExecutionEngine == false ) {
		  moExecutionEngine.initialize();
		  mbInitExecutionEngine = true;
		} // if (mbInitExecutionEngine == false)

		/*
		 * Free up any unused database connection object held via Executor
		 */
		moExecutionEngine.freeIdleExecutorSlot();

		/*
		 * Cleanup any or all the sessions that have expired
		 */
		moSessionStore.removeExpiredSessions();

		sleep( mlJrestSweepInterval );
	  } // end of for (;;)
	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );

	  mLogger.error( moStringWriter.toString() );
	} // end of try ... catch block
  }/* public void run() */

  /**
   * 
   * @return
   */
  private void isSystemReady() {
	if( ( moStore.getAuthenticationDefinition().getQuery() == null )
		|| moStore.getJdbcConnectionDetails() == null ) {
	  moSessionStore.setSystemToHaltState();

	  return;
	} // if ((moStore.getAuthenticationDefinition().getQuery() == null)

	moSessionStore.setSystemToReadyState();
  }/* private void isSystemReady() */

  /**
   * 
   * @return
   */
  public String getPathToDefinitionFiles() {
	return msPathToDefinitionFiles;
  }/* public String getPathToDefinitionFiles() */

  /**
   * Singleton instance object for the store cache. We must always have a single
   * instance of the object all the time.
   */
  private static Compile __instance;

  /**
   * Instance level JSON Parser object that is used for parsing the JSON
   * definition file.
   */
  private JSONParser moJsonParser;

  /**
   * Instance level nested hash map which will be used in parsing the definition
   * file contents
   */
  private HashMap< String, HashMap< String, Object > > mhmapDefinitionDetails;

  /**
   * Handle to the global Store object, where all the definition are parsed and
   * kept for execution level access.
   */
  private Store moStore;

  /**
   * Instance level string which would hold the information about the path on
   * which definition files can be observed.
   */
  private String msPathToDefinitionFiles;

  /**
   * Interval at which the Compiler should sweep the file system for any new
   * definition files to be taken for parsing and constructing the in-memory
   * definition for execution.
   */
  private long mlJrestSweepInterval;

  /**
   * Instance level file descriptor that would used for reading the file
   * contents.
   */
  private File mfDefinitionFile;

  /**
   * Handle to the ExecutionEngine, so that the sweeper can initiate any unused
   * database connection objects held via Executor class objects.
   */
  private ExecutionEngine moExecutionEngine;

  /**
   * Handle to the Session repository; this handle is used to invoke the cleanup
   * function on the session store along with the compilers sweeper.
   */
  private Session moSessionStore;

  /*
   * 
   */
  private String msJrestSweepInterval;

  /*
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Compile.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;

  /**
   * 
   */
  private boolean mbJrestRepositoryRead;

  /**
   * 
   */
  private boolean mbInitExecutionEngine;

  /**
   * 
   */
  private String msPathToJrestRepo;
}/* public class Compile */
