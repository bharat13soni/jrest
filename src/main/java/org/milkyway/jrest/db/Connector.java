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
package org.milkyway.jrest.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.milkyway.jrest.constant.Constants;
import org.milkyway.jrest.constant.Exceptions;

public class Connector {
  /**
   * 
   */
  public Connector() {
	moConnection = null;
	moStringWriter = new StringWriter();
	moPrintWriter = new PrintWriter( moStringWriter );
  }/* public Connector() */

  /**
   * 
   * @param connectionDetails
   * @return
   */
  protected boolean connect(ConnectionDetails connectionDetails) {
	try {
	  if( connectionDetails != null ) {
		if( connectionDetails.getDatabaseType().equals( Constants.gsConnTypeMySql ) ) {
		  Class.forName( Constants.gsMysqlDriverClass );
		} else if( connectionDetails.getDatabaseType()
			.equals( Constants.gsConnTypePostgre ) ) {
		  Class.forName( Constants.gsPostgreDriverClass );
		} else if( connectionDetails.getDatabaseType()
			.equals( Constants.gsConnTypeSqlServer ) ) {
		  Class.forName( Constants.gsSqlServerDriverClass );
		} else {
		  System.out.println( Exceptions.gsUnknownDbTypeGiven );

		  return false;
		}// if(connectionDetails.getDatabaseType().equals(Constants.gsConnTypeMySql))

		String sConnectionInfo = String.format(
			( ( connectionDetails.getDatabaseType()
				.equalsIgnoreCase( Constants.gsConnTypeSqlServer ) )
					? Constants.gsSqlServerConnectionStringFormat
					: Constants.gsConnectionStringFormat ),
			connectionDetails.getDatabaseType().toLowerCase(),
			connectionDetails.getHostName(), connectionDetails.getPortNumber(),
			connectionDetails.getDatabaseName(), connectionDetails.getUserName(),
			connectionDetails.getPassWord() );

		moConnection = DriverManager.getConnection( sConnectionInfo );
	  } else {
		mLogger.error( Exceptions.gsDBConnectionDetailsNotSet );

		return false;
	  }

	} catch( Exception e ) {
	  e.printStackTrace( moPrintWriter );
	  mLogger.error( moStringWriter.toString() );

	  return false;
	}// end of try ... catch block

	return true;
  }/* protected boolean connect(ConnectionDetails connectionDetails) */

  /**
   * 
   */
  protected void disConnect() {
	try {
	  if( moConnection != null ) {
		moConnection.close();

		moConnection = null;
	  }// if(moConnection != null)
	} catch( Exception e ) {
	  /*
	   * Turn a blind eye towards all the errors. We don't expect any here
	   */
	}// end of try ... catch block
  }/* protected void disConnect() */

  /**
   * 
   */
  protected Connection moConnection;

  /**
   * The logging handle for the system get the log files done.
   */
  private static Logger mLogger = Logger.getLogger( Connector.class.getCanonicalName() );

  /**
   * 
   */
  private StringWriter moStringWriter;

  /**
   * 
   */
  private PrintWriter moPrintWriter;
}/* public class Connector */
