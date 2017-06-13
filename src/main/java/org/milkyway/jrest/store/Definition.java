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
package org.milkyway.jrest.store;

import java.util.HashSet;

import org.milkyway.jrest.constant.Constants;

public class Definition {
  /**
   * 
   */
  public Definition() {
	msQuery = null;
	msetRoles = null;

	mcDelimiter = Constants.gcDefaultAuthDelimiter;
	mbUseResultFromBefore = false;
	msBeforeMethod = null;
	msAfterMethod = null;

	msFilePath = null;
	msFileType = null;
	mbGenerateFileName = false;
  }/* public Definition() */

  /**
   * Returns the SQL query string which was already set
   * 
   * @return null if nothing is set otherwise a valid SQL is returned
   */
  public String getQuery() {
	return msQuery;
  }/* public String getQuery() */

  public void setQuery(String sQuery) {
	msQuery = sQuery;
  }/* public void setQuery(String sQuery) */

  public char getDelimiter() {
	return mcDelimiter;
  }/* public char getDelimiter() */

  public void setDelimiter(char cDelimiter) {
	mcDelimiter = cDelimiter;
  }/* public void setDelimiter(char cDelimiter) */

  public HashSet< String > getRoles() {
	return msetRoles;
  }/* public Set<String> getRoles() */

  public void addRole(String sRole) {
	if( msetRoles == null ) {
	  msetRoles = new HashSet< String >();
	}

	msetRoles.add( sRole );
  }/* public void addRole(String sRole) */

  public boolean containsRole(String sRole) {
	return ( ( msetRoles != null ? msetRoles.contains( sRole ) : false ) );
  }/* public boolean containsRole(String sRole) */

  public void setFqcnAfter(String sAfter) {
	if( sAfter != null ) {
	  msFqcnAfter = sAfter;
	}
  }/* public void setFqcnAfter(String sAfter) */

  public String getFqcnAfter() {
	return msFqcnAfter;
  }/* public String getFqcnAfter() */

  public void setFqcnBefore(String sBefore) {
	if( sBefore != null ) {
	  msFqcnBefore = sBefore;
	}
  }/* public void setFqcnBefore(String sBefore) */

  public void setBeforeUsagePattern(boolean bConsumeOutptOfBefore) {
	mbUseResultFromBefore = bConsumeOutptOfBefore;
  }/* public void setBeforeUsagePattern(boolean bConsumeOutptOfBefore) */

  public boolean useResultFromBefore() {
	return mbUseResultFromBefore;
  }/* public boolean useResultFromBefore () */

  public String getFqcnBefore() {
	return msFqcnBefore;
  }/* public String getFqcnBefore() */

  public void setBeforeMethod(String sMethod) {
	msBeforeMethod = sMethod;
  }/* public void setBeforeMethod(String sMethod) */

  public String getBeforeMethod() {
	return msBeforeMethod;
  }/* public String getBeforeMethod() */

  public void setAfterMethod(String sMethod) {
	msAfterMethod = sMethod;
  }/* public void setAfterMethod(String sMethod) */

  public String getAfterMethod() {
	return msAfterMethod;
  }/* public String getAfterMethod() */

  public String getFilePath() {
	return msFilePath;
  }/* public String getFilePath() */

  public void setFilePath(String sFilePath) {
	msFilePath = sFilePath;
  }/* public void setFilePath(String sFilePath) */

  public String getFileType() {
	return msFileType;
  }/* public String getFileType() */

  public void setFileType(String sFileType) {
	msFileType = sFileType;
  }/* public void setFileType(String sFileType) */

  public boolean shouldGenerateFileName() {
	return mbGenerateFileName;
  }/* public boolean shouldGenerateFileName() */

  public void setGenerateFileName(boolean bGenerateFileName) {
	mbGenerateFileName = bGenerateFileName;
  }/* public void setGenerateFileName(boolean bGenerateFileName) */

  /**
   * String to which SQL query would be stored
   */
  private String msQuery;

  /**
   * Delimiting character used for parsing the SQL string
   */
  private char mcDelimiter;

  /**
   * Container for storing the Roles allowed to call on the Definition to which
   * this object is associated.
   */
  private HashSet< String > msetRoles;

  /**
   * String which would hold the Java API that must be called before we actually
   * make a call to the desired REST definition
   */
  private String msFqcnBefore;

  /**
   * String which would hold the Java API that must be called after we actually
   * make a call to the desired REST definition
   */
  private String msFqcnAfter;

  /**
   * 
   */
  private boolean mbUseResultFromBefore;

  /**
   * 
   */
  private String msBeforeMethod;

  /**
   * 
   */
  private String msAfterMethod;

  /**
   * 
   */
  private String msFilePath;

  /**
   * 
   */
  private String msFileType;

  /**
   * 
   */
  private boolean mbGenerateFileName;

}/* public class Definition */
