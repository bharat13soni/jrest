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
package org.aprilis.jrest.constant;

import java.util.Arrays;
import java.util.HashSet;

public class Constants {
  /**
   * The environment variable name which points JRest to the location of
   * application definition files.
   */
  public static final String gsPathVariable = "JREST_DEFINITION_PATH";

  /**
   * The environment variable name which informs the JRest running language how
   * often it should look at JREST_DEFINITION_PATH for newer or modified
   * definition files.
   */
  public static final String gsRefreshInterval = "JREST_REFRESH_INTERVAL";

  /**
   * The environment variable name which informs the JRest running language how
   * many connections that it can hold at most to the associated database.
   */
  public static final String gsDbMaxConnections = "JREST_DB_MAX_CONNECTIONS";

  /**
   * The permitted extension for the JRest definition files. JRest simply
   * ignores files without this extension from processing.
   */
  public static final String gsDefFileExtension = ".json";

  /**
   * The delimiter character that can be used to separate multiple definitions
   * within a single file. JRest will continue to parse a definition file till
   * the time EOF is reached and considers '!' as median between two
   * definitions.
   * 
   * This is particularly helpful in keeping the definition related to a
   * particular module/screen grouped into a single file.
   */
  public static final String gsJsonDefinitionDelimiter = "!";

  /**
   * The definition key value for the JDBC connection type definition.
   */
  public static final String gsJdbcDefinitionKey = "JDBC";

  /**
   * The definition key value for the Authentication type of the definition
   */
  public static final String gsAuthDefinitionKey = "AUTH";

  /**
   * The keyword Query which is one among the seven keywords of JRest language.
   * This keyword is used while expressing the SQL statement in a definition
   * file.
   */
  public static final String gsLangTokenQuery = "Query";

  /**
   * The keyword Delim represents Delimiter and is part of the seven keywords of
   * JRests grammer set. Delim is permitted to be used within an Authentication
   * type of the definition file. However, the JRest interpreting compiler
   * second guesses the oddity if used with other definition files and ignores
   * this value.
   * 
   * This delimiter is used for parsing the resultset of authentication process.
   * The JRest mandates that RS of an authentication always return a set of
   * roles associated for the user. If the authentication results in a single
   * row that contains contactenated values of roles, then this delimiter is
   * used for parsing that string to obtain all the roles individually.
   */
  public static final String gsLangTokenDelim = "Delim";

  /**
   * The keyword Type represents the kind of call the user is trying to
   * accomplish through the definition file. JRest maps RESTs GET to JRESTs GET
   * and RESTs PUT, POST to SET.
   * 
   * Authentication definition is an exception to this and JRest binds it POST
   * type on the REST. Which in other words mean that one must use the ajax call
   * type to POST.
   */
  public static final String gsLangTokenType = "Type";

  /**
   * The keyword Roles is used for setting the access privileges to a definition
   * on the webserver. JRest implements a simple role based system and mandates
   * the same in the implementation.
   * 
   * There could be many roles assigned for a particular definition, the only
   * mandation is that they should be comma seperated.
   */
  public static final String gsLangTokenRoles = "Roles";

  /**
   * The keyword Conn is used for letting the JRest know the connection string
   * for communicating to the database. JRest assumes that the Connection string
   * given is well formed and the required JDBC driver for the target database
   * is present in the CLASSPATH.
   */
  public static final String gsLangTokenConnection = "Conn";

  /**
   * The keyword Before is one among the two special keywords that JRest
   * implements at the core to help in the process of prefix execution to the
   * actual work. In other words, one can place on any valid public Java API as
   * value to the keyword "Before" in the definition file. If that particular
   * class is available for JRest in the CLASSPATH it will execute that function
   * before making the actual REST call.
   * 
   * If the Before call results in any sort of failure then the REST call would
   * be skipped and error information from Before is returned to the caller
   * instead; otherwise the result from Before is passed to the REST call along
   * with the original JSON data that was passed to Before API.
   * 
   * JRest also would pass the JSON_DATA that is marked with "Before" to the API
   * mentioned for the "Before" keyword.
   */
  public static final String gsLangTokenBefore = "Before";

  /**
   * The keyword After is the last among the two special keywords that JRest
   * implements at the core to help in the process of postfix execution to the
   * actual work. The behavior of After is very similar to that of Before, the
   * only difference is that the API given as part of the After is executed once
   * the REST is successfully executed.
   * 
   * If the REST call results in any failure then the function mentioned as part
   * of After would be skipped and error information is returned to the call.
   * 
   * JRest also would pass the result set in JSON format from the REST call to
   * the function mentioned in After; it is upto it to synthesize the input
   * further.
   * 
   * When a After function is noticed JRest would return the result produced
   * from that function to the caller instead of result set from REST call.
   */
  public static final String gsLangTokenAfter = "After";

  /**
   * This reserved word represents the type of action that call is supposed to
   * make in the definition file on the JRest. JRest maps RESTs GET to JRESTs
   * GET and RESTs PUT, POST to SET.
   * 
   * If the definition is making a call to the JRest for querying a data set
   * then the Type keyword should be populated with GET reserved word.
   */
  public static final String gsLangDefTypeGet = "GET";

  /**
   * This reserved word represents the type of action that call is supposed to
   * make in the definition file on JRest. JRest maps RESTs GET to JRESTs GET
   * and RESTs PUT, POST to SET.
   * 
   * If the definition is intending to make modification to the database then
   * the Type keyword in the definition file must specify SET as the value.
   * Internally all the data manipulation calls are mapped to the PUT of REST.
   * However, the JavaScript which calls the push method on JRest must use the
   * type PUT while making the ajax call.
   */
  public static final String gsLangDefTypeSet = "SET";

  public static final String gsLangDefKeywordClass = "FQCN";
  public static final String gsLangDefKeywordMethod = "Method";
  public static final String gsLangDefKeywordConsume = "Consume";
  public static final String gsConsumeBeforeTrue = "t";
  public static final String gsConsumeBeforeFalse = "f";

  /**
   * Default refresh or lookup time on the file-system for the definition files
   * by Application developers. This value can be changed with
   * JREST_REFRESH_INTERVAL set on the machine environment.
   */
  public static final long glDefaultRefreshInterval = 30000;

  /**
   * The permitted delimiters for JREST, these are used while parsing the result
   * set of authentication scheme.
   */
  public static final char gcDelimComma = ',';
  public static final char gcDelimHash = '#';
  public static final char gcDelimColon = ':';
  public static final char gcDelimSemiColon = ';';

  /**
   * The name of the base definition file in which JREST looks for
   * authentication and JDBC related information. If those details are not
   * mentioned in this file but elsewhere, JREST will never know how to connect
   * to a database or authenticate the user; which also puts the system to
   * shutdown mode.
   */
  public static final String gsJrestDefinitionFileName = "jrest.json";

  /**
   * Minimum length of any Query string given within the definition file.
   */
  public static final short gshMinQueryLength = 5;

  /**
   * Mimimum length of the delimiter value. Delimiters are generally used within
   * the jrest.json file, as part of AUTH definition.
   */
  public static final short gshMinDelimiterLength = 1;

  /**
   * Minimum acceptable length of any JREST_KEY which will be used to identify a
   * definition file.
   */
  public static final short gshMinDefinitionKeyLength = 1;

  /**
   * Expected length of definition type; JREST at present has only SET and GET
   * as definition types
   */
  public static final short gshDefinitionTypeLength = 3;

  /**
   * Minimum expected length of a function, which one might want to get executed
   * either before or after making call to the REST part.
   */
  public static final short gshMinFunctionLength = 3;

  /**
   * Minimum number of expected roles to be associated for a definition.
   */
  public static final short gshMinNumberOfRoles = 1;

  /**
   * Expected number of keywords within the Connection string given as part of
   * the JDBC definition
   */
  public static final short gshConnectionTokenCount = 6;

  /**
   * Desired or expected number of internal slots on the executor engine pool.
   */
  public static final short gshExecutorPoolSlotSize = 4;

  /**
   * Just a constant for Zero
   */
  public static final short gshZero = 0;

  /**
   * Constant for One
   */
  public static final short gshOne = 1;
  
  /**
   * Default number of max allowed database connections that the JREST is
   * allowed to pool itself with, if the guidline from developer is not set as
   * part of JREST_DB_MAX_CONNECTIONS environment variable.
   */
  public static final short gshDefaultMaxDbConnections = 100;

  /**
   * The default role id to be assumed when jrest is put in no role mode of
   * execution. This value will be associated with all the users and the
   * definitions in such mode.
   */
  public static final short gshDefaultRoleId = -3022;

  /**
   * Maximum allowed idle time of any resource within the jrest execution
   * environment. This value is consumed by Compiler, Interpreter and
   * ExecutionEngine for evaluating resource locking.
   */
  public static final long glSessionIdleTime = ( 30 * 60 * 1000 );

  /**
   * Keyword definition for hostname as part of connection json string within
   * JDBC definition
   */
  public static final String gsConnHostName = "Host";

  /**
   * Keyword definition for port number as part of connection json string within
   * JDBC definition
   */
  public static final String gsConnPortNumber = "Port";

  /**
   * Keyword definition for user name as part of connection json string within
   * JDBC definition
   */
  public static final String gsConnUserName = "User";

  /**
   * Keyword definition for password as part of connection json string within
   * JDBC definition
   */
  public static final String gsConnPassWord = "Pass";

  /**
   * Keyword definition for database name as part of connection json string
   * within JDBC definition
   */
  public static final String gsConnDatabase = "Db";

  /**
   * Keyword definition for database type as part of connection json string
   * within JDBC definition. For now JREST supports MySql, PostgreSql, and
   * SQLServer.
   */
  public static final String gsConnDbType = "Type";

  /**
   * Constant string for loading MySql driver
   */
  public static final String gsMysqlDriverClass = "com.mysql.jdbc.Driver";

  /**
   * Constant string for loading PostgreSql driver
   */
  public static final String gsPostgreDriverClass = "org.postgresql.Driver";

  /**
   * Constant string for loading SQLServer driver
   */
  public static final String gsSqlServerDriverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

  /**
   * String constant which represents MySql database. Beware jrest is case
   * sensitive.
   */
  public static final String gsConnTypeMySql = "MySql";

  /**
   * String constant which represents PostgreSql database. Beware jrest is case
   * sensitive.
   */
  public static final String gsConnTypePostgre = "PostgreSql";

  /**
   * String constant which represents SQLServer database. Beware jrest is case
   * sensitive.
   */
  public static final String gsConnTypeSqlServer = "SQLServer";

  /**
   * Formatted string that is used in creating connection to the given or
   * supported database
   */
  public static final String gsConnectionStringFormat = "jdbc:%s://%s:%s/%s?user=%s&password=%s&"
	  + "failOverReadOnly=false&maxReconnects=10000&useSSL=false&userUnicode=yes&allowMultiQueries=true&"
	  + "useFastDateParsing=true&useFastIntParsing=true&useJvmCharsetConverters=true&useDirectRowUnpack=true";

  /**
   * SQL Server connection string is different than that of standard JDBC one,
   * so a separate one
   */
  public static final String gsSqlServerConnectionStringFormat = "jdbc:%s://%s:%s;databaseName=%s;user=%s;password=%s;";

  /**
   * SQL Exception state thrown when database is not reacheable for any reason
   * the state would match 08001, 08S01, 08006, 0A000, HY010, HY000, HY096,
   * 08003, on such ill fated occations we catch the error and let know the
   * Executor that it must reinitialize the connection to the server next time
   * when it attempts.
   */
  public static final String[] gsDatabaseConnectionErrors = new String[] { "08001",
	  "08S01", "08006", "0A000", "HY010", "HY000", "HY096", "08003" };

  /**
   * Statically typed hash set of error codes which indicate that the database
   * associated with jrest is taking a nap or network got reset. We cleverly
   * make use of this to avoid a real long if else statement.
   */
  public static final HashSet< String > ghsetConnectionErrorCodes = new HashSet< String >(
	  Arrays.asList( gsDatabaseConnectionErrors ) );

  /**
   * 
   */
  public static final String gsQueryBindDelimiter = "\\?";

  /**
   * 
   */
  public static final char gcQueryParamEnclosure = '\'';

  /**
   * 
   */
  public static final short gshMinJsonDataLength = 9;

  /**
   * 
   */
  public static final char gcDefaultAuthDelimiter = '0';

  /**
   * 
   */
  public static final long glHaltSleepInterval = ( 30000 );

  public static final short gshColumnStartIndex = 1;
  public static final short gshMillisInSecond = 1000;

  /*
   * Header Parameter definitions
   */

  public static final String JSON_DATA = "JSON_DATA";
  public static final String SESSION_KEY = "SESSION_KEY";
  public static final String JREST_KEY = "JREST_KEY";
  public static final String DEFAULT_JSON_DATA = "DJD";
  public static final String NULL = "";

  public static final String gsRenameDefinitionFile = "%s%s%";
  public static final String gsTrimFindeString = "\\n";
  public static final String gsStripCommentLineRegEx = "(?m)^//.*";
  public static final String gsSessionKeyJsonFormat = "{\"sessionkey\" : \"%s\" }";
  public static final String gsEmptyString = "";
  public static final String gsRemoveSpacesExcludingQuotes = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";

  public static final String gsJrestRepository = ".jrest";
  public static final short gshMaxRepoDepth = 2;
  public static final short gshMinRepoDepth = 1;

  public static final String gsLog4jPropertiesFile = "JREST_LOG4J_PROP_FILE";
  public static final String gsDefaultLog4jFileName = "log4j.properties";

  public static final short gshBeforeMethodParamCount = 1;
  public static final short gshAfterMethodParamCount = 3;

  public static final String gsJsonDefErrorFileExtn = ".err";

  public static final String gsFileName = "FILENAME";
  public static final String gsFile = "FILE";

  public static final short gshBufferSize = 4096;

  public static final short gshDefTypeSet = 0;
  public static final short gshDefTypeGet = 1;
}/* public class Constants */
