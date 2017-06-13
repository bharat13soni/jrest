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
package org.milkyway.jrest.compile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.milkyway.jrest.store.Store;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {

  static Logger mLogger = Logger.getLogger( Test.class.getName() );

  /**
   * @param args
   */
  public static void main(String[] args) {
	// TODO Auto-generated method stub
	String json = "{\"JDBC\":{" + "\"Query\":\"SELECT * FROM Darwin.User\", "
		+ "\"Type\":\"GET\", " + "\"Roles\":[\"\", \"\", \"3\"]" + "}}\n";

	String sampleQuery = "SELECT ? FROM ? USERS ? Me too ? howdy stoya";
	String splitArray[] = sampleQuery.split( "\\?" );

	// long x = Long.parseLong(System.getenv("JREST_REFRESH_INTERVAL"));
	// System.out.println(x);

	InputStream stream = Test.class.getResourceAsStream( "/jdbc.json" );
	System.out.println( stream != null );
	stream = Store.class.getClassLoader().getResourceAsStream( "jdbc.json" );
	System.out.println( stream != null );

	System.out.println( sampleQuery );

	for( short i = 0; i < splitArray.length; i++ )
	  System.out.println( splitArray[i] );

	System.out.println( json );

	JSONParser parser = new JSONParser();

	try {
	  // JSONObject table = (JSONObject) parser.parse(json);
	  @SuppressWarnings("unchecked")
	  HashMap< String, HashMap< String, Object > > jTable = (JSONObject) parser
		  .parse( json );
	  ;
	  // HashMap<String, Object> jEntries;

	  for( Map.Entry< String, HashMap< String, Object > > entry : jTable.entrySet() ) {
		System.out.println( entry.getKey() );
		System.out.println( entry.getValue().getClass() );

		// jEntries = (HashMap<String, Object>) entry.getValue();
		for( Map.Entry< String, Object > values : entry.getValue().entrySet() ) {
		  System.out.println( "'" + values.getKey() + "'" );
		  // System.out.println(values.getValue().getClass().getName());

		  if( values.getValue() instanceof JSONArray ) {
			JSONArray ja = (JSONArray) values.getValue();

			for( short i = 0; i < ja.size(); i++ )
			  System.out.println( "\t" + ja.get( i ).toString() );
		  } else {
			System.out.println( values.getValue().toString() );
		  }
		}
	  }
	} catch( ParseException e ) {
	  // TODO Auto-generated catch block
	  e.printStackTrace();
	}

  }
}
