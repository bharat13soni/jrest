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
package org.milkyway.jrest.execute;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.milkyway.jrest.constant.Constants;
import org.milkyway.jrest.constant.Exceptions;

/**
 * @author h & rk
 * 
 */
public class ExecutionEngine {
  /**
   * 
   */
  @SuppressWarnings("unchecked")
  private ExecutionEngine() {
	mqaExecutorPool = (LinkedList< Executor >[]) new LinkedList[Constants.gshExecutorPoolSlotSize];
	mlaExecutorPoolTouchTime = new Long[Constants.gshExecutorPoolSlotSize];

	String sMaxConnections = System.getenv( Constants.gsDbMaxConnections );

	if( sMaxConnections != null && sMaxConnections.length() > Constants.gshZero ) {
	  mshMaxDbConnections = Short.parseShort( sMaxConnections );
	} else {
	  mshMaxDbConnections = Constants.gshDefaultMaxDbConnections;
	}// if(sMaxConnections != null && sMaxConnections.length() >
	 // Constants.gshZero)

	for( short poolIndex = 0; poolIndex < Constants.gshExecutorPoolSlotSize; poolIndex++ ) {
	  mqaExecutorPool[poolIndex] = new LinkedList< Executor >();
	  mlaExecutorPoolTouchTime[poolIndex] = 0L;
	}// for (short poolIndex = 0; poolIndex < Constants.gshExecutorPoolSlotSize;
	 // poolIndex++)

	mshExecutorSlotCount = (short) ( mshMaxDbConnections
		/ Constants.gshExecutorPoolSlotSize );
  }/* private ExecutionEngine() */

  /**
   * 
   * @return
   */
  public static ExecutionEngine instance() {
	if( __instance == null ) {
	  synchronized( ExecutionEngine.class ) {
		if( __instance == null ) {
		  __instance = new ExecutionEngine();
		} /* if (__instance == null) */
	  } /* synchronized (ExecutionEngine.class) */
	} /* if (__instance == null) */

	return __instance;
  }/* public static ExecutionEngine Instance() */

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
  synchronized public void initialize() {
	createPoolSlot( Constants.gshZero );
  }/* public boolean initialize() */

  /**
   * 
   * @param shSlotNumber
   */
  private void createPoolSlot(short shSlotNumber) {
	mLogger.debug( String.format( Exceptions.gsCreateExecutorPool, shSlotNumber ) );

	for( short executorIndex = 0; executorIndex < mshExecutorSlotCount; executorIndex++ ) {
	  Executor executor = new Executor( shSlotNumber );

	  if( executor != null ) {
		if( executor.initialize() == true ) {
		  mqaExecutorPool[shSlotNumber].add( executor );
		} else {
		  /*
		   * Executor initialize failed. This is either due to connection
		   * details error or error in establishing connectivity to database. We
		   * rollback the creation of the executor objects for this particular
		   * slot.
		   */
		  while( !mqaExecutorPool[shSlotNumber].isEmpty() ) {
			mqaExecutorPool[shSlotNumber].remove();
		  }// while(!mqaExecutorPool[shSlotNumber].isEmpty())

		  mLogger.error( String.format( Exceptions.gsPoolExtensionFailed,
			  ( executorIndex > 0 ? executorIndex - 1 : 0 ),
			  ( ( shSlotNumber > 0 ? ( shSlotNumber + 1 ) : 0 )
				  * mshExecutorSlotCount ) ) );

		  return;
		}// if (executor.initialize() == true)

	  }// if ((executor != null) && executor.initialize())
	}// for (short executorIndex = 0; executorIndex < mshExecutorSlotCount;
	 // executorIndex++)

	mlaExecutorPoolTouchTime[shSlotNumber] = System.currentTimeMillis();
  }/* private void createPoolSlot(short shSlotNumber) */

  /**
   * 
   * @return
   */
  synchronized public Executor acquireExecutorFromPool() {
	mLogger.debug( Exceptions.gsAcquireExecutorMsg );

	for( short poolIndex = 0; poolIndex < Constants.gshExecutorPoolSlotSize; poolIndex++ ) {
	  if( mlaExecutorPoolTouchTime[poolIndex] == 0L ) {
		createPoolSlot( poolIndex );
	  }

	  if( mqaExecutorPool[poolIndex].size() != 0 ) {
		mlaExecutorPoolTouchTime[poolIndex] = System.currentTimeMillis();

		return mqaExecutorPool[poolIndex].remove();
	  }// if (mqaExecutorPool[poolIndex].size() != 0)
	}// for(short poolIndex = 0; poolIndex < Constants.gshExecutorPoolSlotSize;
	 // poolIndex++)

	mLogger.debug( Exceptions.gsNoFreeExecutorsAvailable );

	return null;
  }/* public Executor acquireExecutorFromPool() */

  /**
   * 
   * @param oExecutor
   */
  synchronized public void releaseExecutorToPool(Executor oExecutor) {
	if( oExecutor != null ) {
	  oExecutor.releaseStatement();
	  mqaExecutorPool[oExecutor.getPoolIndex()].add( oExecutor );
	  oExecutor = null;
	}// if (oExecutor != null)
  }/* public void releaseExecutorToPool(Executor oExecutor) */

  /**
   * 
   */
  synchronized public void freeIdleExecutorSlot() {
	/*
	 * We ignore the first slot of the executor pool because it is the mandatory
	 * slot and evaluate only other slots
	 */
	long lCurrentTime = System.currentTimeMillis();

	for( short poolIndex = 1; poolIndex < Constants.gshExecutorPoolSlotSize; poolIndex++ ) {
	  long lSlotTouchIdleTime = Constants.glSessionIdleTime
		  + mlaExecutorPoolTouchTime[poolIndex];

	  if( ( lSlotTouchIdleTime < lCurrentTime )
		  && ( mqaExecutorPool[poolIndex].size() == mshExecutorSlotCount ) ) {

		while( mqaExecutorPool[poolIndex].isEmpty() == false ) {
		  mqaExecutorPool[poolIndex].remove().unInitialize();
		}// while (mqaExecutorPool[poolIndex].isEmpty() == false)
	  }// if ((mlaExecutorPoolTouchTime[poolIndex] < lIdleCheckTime)
	}// for (short poolIndex = 0; poolIndex <
  }/* public void freeIdleExecutorSlot() */

  /**
   * Disconnect all the executors from the database and empty all the resources
   * of the pool
   */
  synchronized public void freePool() {
	for( short poolIndex = 0; poolIndex < Constants.gshExecutorPoolSlotSize; poolIndex++ ) {
	  while( mqaExecutorPool[poolIndex].isEmpty() == false ) {
		mqaExecutorPool[poolIndex].remove().unInitialize();
	  }// while (mqaExecutorPool[poolIndex].isEmpty() == false)

	  mlaExecutorPoolTouchTime[poolIndex] = 0L;
	}// for (short poolIndex = 0; poolIndex < ...
  }// synchronized public void freePool()

  /**
   * Singleton instance object for the ExecutionEngine. We must always have a
   * single instance of the object all the time.
   */
  private static ExecutionEngine __instance;

  /**
   * 
   */
  private LinkedList< Executor >[] mqaExecutorPool;

  /**
   * 
   */
  private Long[] mlaExecutorPoolTouchTime;

  /**
   * 
   */
  private short mshMaxDbConnections;

  /**
   * 
   */
  private short mshExecutorSlotCount;

  /**
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger
	  .getLogger( ExecutionEngine.class.getCanonicalName() );
}/* public class ExecutionEngine */
