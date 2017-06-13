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

public class ConnectionDetails {

  public ConnectionDetails() {
	msHostName = null;
	msDatabaseName = null;
	msPassWord = null;
	msPortNumber = null;
	msDatabaseType = null;
	msUserName = null;
  }

  public String getHostName() {
	return msHostName;
  }

  public void setHostName(String sHostName) {
	msHostName = sHostName;
  }

  public String getPortNumber() {
	return msPortNumber;
  }

  public void setPortNumber(String sPortNumber) {
	msPortNumber = sPortNumber;
  }

  public String getUserName() {
	return msUserName;
  }

  public void setUserName(String sUserName) {
	msUserName = sUserName;
  }

  public String getPassWord() {
	return msPassWord;
  }

  public void setPassWord(String sPassWord) {
	msPassWord = sPassWord;
  }

  public String getDatabaseName() {
	return msDatabaseName;
  }

  public void setDatabaseName(String sName) {
	msDatabaseName = sName;
  }

  public String getDatabaseType() {
	return msDatabaseType;
  }

  public void setDatabaseType(String sType) {
	msDatabaseType = sType;
  }

  private String msHostName;
  private String msPortNumber;
  private String msUserName;
  private String msPassWord;
  private String msDatabaseName;
  private String msDatabaseType;
}/* public class Connection */
