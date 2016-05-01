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
package org.aprilis.jrest.boot;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.aprilis.jrest.compile.Compile;
import org.aprilis.jrest.constant.Constants;
import org.aprilis.jrest.constant.Exceptions;
import org.aprilis.jrest.execute.ExecutionEngine;
import org.aprilis.jrest.store.Session;
import org.aprilis.jrest.store.Store;

/**
 * The Bootstrap class initializes JREST. JREST is made up of several components
 * that need to be initialized when the webserver starts. The components include
 * <br/>
 * <br/>
 * <b>Log4J logger -</b> JREST logging system that logs debug messages depending
 * on the log configurations <br/>
 * <br/>
 * <b>Definition store -</b> JREST loads and maintains all the configured JREST
 * APIs in a definition store. When a HTTP request with a JREST key is executed,
 * the store is referred for the details of the JREST key to execute the API
 * <br/>
 * <br/>
 * <b>Compile -</b>
 * 
 * <br>
 * <br>
 * <b>Session store -</b>
 * 
 * <br>
 * <br>
 * <b>Execution engine - </b>
 * 
 * 
 */
public class Bootstrap implements ServletContextListener {
  /**
   * The default constructor for the Bootstrap class. Here we only initialize
   * the Log5j things as it should be available for all before initialization
   * happens.
   */
  public Bootstrap() {
	// Get the log4j configuration from the OS environment
	String log4jConfigFile = System.getenv( Constants.gsLog4jPropertiesFile );

	// Check if exists a user supplied configuration file, if not use the
	// default
	// one that comes bundled with the JRest.
	if( log4jConfigFile != null && log4jConfigFile.length() > Constants.gshZero ) {
	  PropertyConfigurator.configure( log4jConfigFile );
	} else {
	  PropertyConfigurator.configure( Constants.gsDefaultLog4jFileName );
	}// if (log4jConfigFile != null && log4jConfigFile.length() >
	 // Constants.gshZero)
  }/* public Bootstrap() */

  /**
   * Overrider API from ServletContextListener and acts like a dtor for the
   * bootstrap. Here every component of the JRest is shutdown completely and
   * their handles are set to null to be garbage collected.<br/>
   * <br/>
   * 
   * This API is called before the Servelet listener is destroyed.
   */
  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
	if( moExecutionEngine != null ) {
	  moExecutionEngine.freePool();
	}// if (moExecutionEngine != null)

	moStore = null;
	moCompile = null;
	moSessionStore = null;
	moExecutionEngine = null;
  }/* public void contextDestroyed(ServletContextEvent arg0) */

  /**
   * Is a overrider API that is called once the ServeletContextListener is
   * initialized. Once the listener is up, we initialize the JRest runtime
   * subsystem, completely!.
   */
  @Override
  public void contextInitialized(ServletContextEvent arg0) {
	initJrest();
  }/* public void contextInitialized(ServletContextEvent arg0) */

  /**
   * The initializer function for the JRest and its subsystems. This API makes
   * sure that Store, Compiler, Session, ExecutionEngine, ConnectorPool and a
   * JRest Listener, are initialized properly.
   */
  private void initJrest() {
	try {
	  // Initialize the store instance - This is where all the definitions go in
	  // the runtime and boot time.
	  moStore = Store.instance();
	  mLogger.debug( Exceptions.gsDefinitionStoreInitialized );

	  // Initialize the compiler instance - this is an intention driven
	  // interpretive
	  // compiler which works based on the definitions given to it.
	  moCompile = Compile.instance();
	  mLogger.debug( Exceptions.gsCompilerInitialized );

	  // Initialize the session store - where all the session objects are kept
	  // and used
	  // for providing access to user queries.
	  moSessionStore = Session.instance();
	  mLogger.debug( Exceptions.gsSessionStoreInitialized );

	  // Initialize the execution engine - which is needed to get the desired
	  // data from
	  // the database and other biz logic classes
	  moExecutionEngine = ExecutionEngine.instance();
	  mLogger.debug( Exceptions.gsExecutionEngineInitialized );

	  // Perform a final check to see whether store, session and compiler
	  // classses
	  // are initialized properly.
	  if( moSessionStore != null && moStore != null && moExecutionEngine != null
		  && moCompile != null ) {
		// Fork off the worker thread to run the compiler inline
		mthWorker = new Thread( moCompile );

		mLogger.debug( Exceptions.gsJrestInitialized );

		mthWorker.start();

		mLogger.debug( Exceptions.gsSweeperStarted );
	  } else {
		mLogger.debug( Exceptions.gsJrestInitFailed );
	  }// if (moSessionStore != null && moStore != null && ... )
	} catch( Exception e ) {
	  e.printStackTrace();
	}// end of try ... catch block
  }/* private void initJrest() */

  /**
   * Handler to the store object, store is used for keeping all the JRest
   * definitions cached for the faster access in the runtime.
   */
  private Store moStore;

  /**
   * Handle to the compiler. Compiler does perform the schematic checks on the
   * definitions for their correctness and pushes them to the store for
   * persistence.
   */
  private Compile moCompile;

  /**
   * Handle to the session store. All authenticated session would be stored
   * here. They also have the timer driven mechanism to cleanup the stale
   * session keys.
   */
  private Session moSessionStore;

  /**
   * Handle to the execution engine, which is responsible for all the query
   * bound executions. EE also performs the chaining of before and after events.
   */
  private ExecutionEngine moExecutionEngine;

  /**
   * The worker thread handle, used for starting the compiler daemon
   * independently.
   */
  private Thread mthWorker;

  /**
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Bootstrap.class.getCanonicalName() );

}/* public class Bootstrap implements ServletContextListener */
