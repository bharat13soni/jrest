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
package org.milkyway.sample;

public class TestBeforeAfter {
  public TestBeforeAfter() {
	/*
	 * Empty CTOR
	 */
  }/* public TestBeforeAfter() */

  public String sayHelloBefore(String jsonData) {
	System.out.println( "MESSAGE FROM sayHelloBefore [" + jsonData + "]" );

	return jsonData;
  }/* public String sayHelloBefore(String jsonData) */

  public String sayHelloAfter(String restJsonData, String queryResultJson,
	  String beforeMethodResult) {
	System.out.println( "MESSAGE FROM sayHelloAfter [" + restJsonData + "] ["
		+ queryResultJson + "] [" + beforeMethodResult + "]" );

	return queryResultJson;
  }/* public String sayHelloAfter(....) */

  public String wrongMethod() {
	return "Wrong method";
  }

  public String beforeThrowsException(String jsonData) throws Exception {
	throw new Exception( "Throwing exception for testing" );
  }

  public String afterThrowsException(String restJsonData, String queryResultJson,
	  String beforeMethodResult) throws Exception {
	throw new Exception( "Throwing exception for testing" );
  }

}/* public class TestBeforeAfter */
