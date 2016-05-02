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
package org.aprilis.jrest.constant;

public class Exceptions {
  public static final String gsPathNotDefined = "JREST_DEFINITION_PATH environmental variable is not set";
  public static final String gsPathNotFound = "JREST_DEFINITION_PATH [%s] does not exist";
  public static final String gsLowSweepInterval = "Sweep time is too short; might affect the performance";
  public static final String gsParseError = "**** Definition Excluded ****\n"
	  + "Invalid JSON Definition found in File [%s]"
	  + "\nJSON Definition Below\n%s\n **** Excluded Info Ends****";
  public static final String gsSessionIsValid = "The session is still valid [%s]";
  public static final String gsSessionIsInValid = "Invalid session identified [%s]";
  public static final String gsRolesVerificationPassed = "Roles verification passed for [%s] against [%s]";
  public static final String gsRolesVerificationFailed = "Roles verification failed for [%s] against [%s]";
  public static final String gsFormedSqlQuery = "Generated SQL Query is [%s]";
  public static final String gsDefinitionQuery = "Definition SQL Query is [%s]";
  public static final String gsExecutingQuery = "Executing SQL Statement [%s]";

  public static final String gsResultColumnCount = "Number of columns in RS are [%d]";
  public static final String gsResultSetRowInfo = "Row Info --> Column Name [%s] its Value [%s]";
  public static final String gsNoDefinitionFound = "No definition was found for given JREST_KEY [%s]";
  public static final String gsInfoSessionAndJrestKey = "Given Jrest Key is [%s] and Session Key is [%s]";
  public static final String gsGivenToExpectedDataRatio = "Expected number of data fields is "
	  + "[%d] against Given is [%d]";
  public static final String gsBinderMissingDataIndex = "Found missing index [%d] and its value in "
	  + "given JSON data for Jrest Key [%s] ";
  public static final String gsDmlResultedInVoid = "The DML [%s] issued on the DB had no effect on it.";
  public static final String gsExecutorLeakFixed = "JRest found a leaked Executor and released back, "
	  + "reason is given below.";
  public static final String gsJrestInitialized = "JRest Successfully Initialized";
  public static final String gsJrestInitFailed = "JRest Failed to Initialize";
  public static final String gsDefinitionStoreInitialized = "Definitions Store Instance Initialized Successfully";
  public static final String gsCompilerInitialized = "Compiler Instance Initialized Successfully";
  public static final String gsSessionStoreInitialized = "Session Store Instance Initialized Successfully";
  public static final String gsExecutionEngineInitialized = "Execution Engine Store Instance Initialized Successfully";
  public static final String gsSweeperStarted = "JRest WatchDog [Definition Monitor, Session Monitor, "
	  + "Database Resource Monitor] Service Started Successfully";
  public static final String gsSessionCreated = "New JRest Session [%s] Created";
  public static final String gsSessionTimeInfoIsNull = "Session time of this [%s] key is null or it has expired";
  public static final String gsPurgeExpiredSessionStarted = "Purging of expired sessions started. Scanning [%d] sessions";
  public static final String gsPurgeExpiredSessionEnded = "Purging of expired sessions completed. Purged [%d] sessions";
  public static final String gsSessionDeregistered = "Session [%s] deregistered from JRest System";
  public static final String gsCompareRoleSets = "Comparing User Roles [%s] against JRest Key Roles [%s]";
  public static final String gsDefinitionReplaced = "Details of the Definition [%s] were replaced";
  public static final String gsDefaultRoleAdded = "Default role value [%s] added to JRest Key [%s]";
  public static final String gsTrimmedJsonString = "Trimmed JSON string is [%s]";
  public static final String gsQueryResultedInError = "Query statement associated with JRest Key [%s] resulted in error";

  /**
   * The initial state of the string taken by the JRest compiler before it reads
   * the environment variable for the actual path.
   */
  public static final String gsPathToDefinitionFilesYetToBeSet = "Path To Definition Files Yet To Be Set";
  public static final String gsInvalidAuthTokenDelimUsed = "Invalid delimiter given valid set is "
	  + "',' '#', ':', ';'";
  public static final String gsTokenQueryMissingInAuth = "Mandatory keyword 'Query' missing "
	  + "from 'AUTH' type definition file";

  public static final String gsTokenConnectionMissingInJdbc = "Mandatory keyword 'Conn' missing "
	  + "from 'JDBC' type definition file";

  public static final String gsEmptyOrInvalidQueryGiven = "Found either an empty or invalid "
	  + "value for Query keyword in definition file";

  public static final String gsEmptyOrTooLongDelimGiven = "Found either an empty delimiter or "
	  + "a delimiter longer than one character; "
	  + "the valid delimiter set is ',' '#', ':', ';'";

  public static final String gsEmptyOrInvalidConnectionGiven = "Found either an empty or invalid "
	  + "value for Conn keyword in JDBC definition file";

  public static final String gsEmptyDefinition = "Found either an empty definition or definition body";

  public static final String gsMissingMandatoryKeywordsInDefFile = "Found a definition with "
	  + "missing mandatory keywords 'Query' or 'Type'";

  public static final String gsInvalidDefinitionTypeGiven = "Found wrong access type in the definition file."
	  + " Valid access types are 'GET' or 'SET'";

  public static final String gsInvalidFunctionNameGiven = "Very short function found; please specify a fully "
	  + "qualified function name e.g. org.aprilis.jrest.Compiler.PrepareLexTree";

  public static final String gsNullSetOfRolesGiven = "No roles were given for the definition";

  public static final String gsMissingConnectionParameters = "Missing one or more mandatory Connection "
	  + "parameters. Valid parameters are (Host, Port, User, Pass, Db, Type).";

  public static final String gsUnknownDbTypeGiven = "Unknown database type specified as part of Connection "
	  + "valid set is (Mysql, Postgre, SQLServer).  These keywords are case sensitive.";

  public static final String gsAttemptDbReInitialization = "Executor re-attempting database connection";

  public static final String gsDbReInitializeFailed = "An attempt(s) to re-initialize the database "
	  + "connection failed.  Check the connectivity from webserver to database server.";

  public static final String gsInsufficientQueryParamValues = "Insufficient number of parameter "
	  + "values passed to %s - Expected: %d v/s Actual: %d";

  public static final String gsMalformedJsonData = "Unable to process the request. Malformed JSON data"
	  + " supplied --> %s";

  public static final String gsBadQueryOrDatabaseGone = "Unable to execute the Query; either your SQL query [%s] "
	  + "has syntactical error or the destination database is not reachable";

  public static final String gsSystemInHaltState = "Mandatory definitions AUTH or JDBC are not yet loaded/defined. "
	  + "For JREST to continue define the missing definition as part of jrest.json";

  public static final String gsHaltSleepIntervalMessage = "%s. Will rescan the definition folder after %d seconds.";

  public static final String gsNoRolesAssignedToUser = "Invalid user or user has no roles assigned. Rejecting authorization.";

  public static final String gsAcquireExecutorMsg = "Acquiring executor object from pool";

  public static final String gsNoFreeExecutorsAvailable = "No free executor objects available in the pool to "
	  + "service the request. Try increasing JREST_DB_MAX_CONNECTIONS environment variable value if problem persists.";

  public static final String gsCantCreateJrestRepository = "Could not create internal JRest repository; Check the filesystem "
	  + "on which JRest is running has Sufficient space and necessary Permission.";

  public static final String gsJrestCurrentScanningFolder = "JRest is currently scanning Folder [%s]";

  public static final String gsUnProcessableQuery = "Query for the provided JRest key cannot be processed. Please check the "
	  + "specified query or the JSON_DATA passed.";

  public static final String gsQueryResultedInNullSetMessage = "JRest Key access resulted in a empty result set";
  public static final String gsQueryResultedInNullSet = "[]";
  public static final String gsSessionIsInValidMessage = "{\"code\": \"403\",\"sessionkey\": \"%s\"}";
  public static final String gsUnknownConsumeValue = "Unknown Use value given. Valid set is t or f";

  public static final String gsInvalidMethodNameGiven = "Very short method name provided";

  public static final String gsMissingMandatoryValuesInBefore = "Values for mandatory keywords FQCN or Method or Consume are not defined for JRest Key [%s]";

  public static final String gsMissingMandatoryKeywordsInBefore = "Missing mandatory keywords FQCN or Method or Consume for JRest Key [%s]";

  public static final String gsMissingMandatoryKeywordsInAfter = "Missing mandatory keywords FQCN or Method for JRest Key [%s]";

  public static final String gsMissingMandatoryValuesInAfter = "Values for mandatory keywords FQCN or Method are not defined for JRest Key [%s]";

  public static final String gsBeforeMethodFailed = "Execution of configured Before method failed";

  public static final String gsAfterMethodFailed = "Execution of configured After method failed";

  public static final String gsBeforeMethodOutputIsNull = "Execution of configured Before method resulted in null value that cannot be consumed ";

  public static final String gsMalformedDefinition = "Found Malformed JSON Definition Clause or Tag [%s] in JRestKey [%s] ";

  public static final String gsQuerySyntaxError = "Query syntax error. Query not terminated with semicolon";

  public static final String gsReservedKeywordNotAllowed = "Reserved keyword AUTH or JDBC not allowed to be used as JRest keys";

  public static final String gsPoolExtensionFailed = "Execution engine pool extension failed. "
	  + "Please check DB connection parameters or database level configurations. Rolling back [%d] pool objects. Valid number of objects in the pool - [%d]";

  public static final String gsDBConnectionDetailsNotSet = "Database connection details not set";

  public static final String gsReflectNullDefinition = "Definition details of Before and After methods not set";

  public static final String gsCreateExecutorPool = "Creating executor pool on slot [%d]";

  public static final String gsInvalidGenerateValue = "Invalid value given for Generate clause. Valid set is y or n";
}/* public class Exceptions */
