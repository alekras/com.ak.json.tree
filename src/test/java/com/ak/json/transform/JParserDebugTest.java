/**
 * Copyright (C) 2014-2020 by krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ak.json.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ak.json.JNode;
import com.ak.json.JParserException;
import com.ak.json.nodetree.JValueNode;

/**
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JParserDebugTest {
	/** */
	protected JParser parser = new JParser();
	/** */
	protected static String [] fileList;

	/**
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		fileList = new String [] {
				"src/test/resources/book_store_light.json"
//        "src/test/resources/1.json"
		};
	}

	/**
	 */
	@AfterClass
	public static void tearDownAfterClass() { /** */
	}

	/**
	 */
	@Before
	public void setUp() {
		System.out.println(" --- Start test.");
	}

	/**
	 */
	@After
	public void tearDown() { /** */
		System.out.println(" --- Stop test.");
	}

	/**
	 * Test method for {@link com.ak.json.transform.JParser#parse(java.lang.String)}.
	 * @throws IOException reading file exception
	 */
	@Test
	public void testParseFile() throws IOException {
		for (String fileName : fileList) {
			BufferedReader reader = null;
			try {
				JNode docTree = parser.parse(new File(fileName));
				String json = docTree.toJson();
				reader = new BufferedReader(new FileReader(fileName));
				StringBuffer sb = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					line = line.replaceAll(" |\\t", "");
					sb.append(line);
				}
				System.out.println(json.replaceAll(" |\\t", ""));
				System.out.println(sb.toString());

			} catch (IOException e) {
				e.printStackTrace();
				fail("");
			} catch (JParserException e) {
				e.printStackTrace();
				fail("");
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
	}

	@Test
	public void testParsingErrors() throws IOException {
//  String jsonText = "[{\"a\":true,\"c\":[1,2],\"b\":{\"d\":\"val\"}},0]";
//    String jsonText = "{\"a\":,\"b\":1}";
		String jsonText = "{\"a\":2,\"b\":1}}";
//    String jsonText = "{\"a\":{\"c\":7}],\"b\":1}";
//    String jsonText = "[[{\"a\":true,\"c\":[],\"b\":1}]";
		JNode docTree;
		try {
			docTree = parser.parse(jsonText);
//      fail("failed test #1 doc: " + docTree);
		} catch (JParserException e) {
			System.err.println(e.getMessage());
//      assertEquals("", "Json syntax : wrong position for ','", e.getMessage());
		}
	}

	@Test
	public void testValueNode() {
		JNode valueNode = new JValueNode<Boolean>(true);
		System.out.println("Boolean) " + valueNode.getValue(Boolean.class));
		assertEquals("", true, valueNode.getValue(Boolean.class));
		System.out.println("String ) " + valueNode.getValue(String.class));
		assertEquals("", "true", valueNode.getValue(String.class));
		System.out.println("Integer) " + valueNode.getValue(Integer.class));
		assertEquals("", null, valueNode.getValue(Integer.class));
		System.out.println("Long   ) " + valueNode.getValue(Long.class));
		assertEquals("", null, valueNode.getValue(Long.class));
		System.out.println("Float  ) " + valueNode.getValue(Float.class));
		assertEquals("", null, valueNode.getValue(Float.class));
		System.out.println("Double ) " + valueNode.getValue(Double.class));
		assertEquals("", null, valueNode.getValue(Double.class));
		System.out.println(" -------------- ");

		valueNode = new JValueNode<String>("string");
		System.out.println("Boolean) " + valueNode.getValue(Boolean.class));
		assertEquals("", false, valueNode.getValue(Boolean.class));
		System.out.println("String ) " + valueNode.getValue(String.class));
		assertEquals("", "string", valueNode.getValue(String.class));
		System.out.println("Integer) " + valueNode.getValue(Integer.class));
		assertEquals("", null, valueNode.getValue(Integer.class));
		System.out.println("Long   ) " + valueNode.getValue(Long.class));
		assertEquals("", null, valueNode.getValue(Long.class));
		System.out.println("Float  ) " + valueNode.getValue(Float.class));
		assertEquals("", null, valueNode.getValue(Float.class));
		System.out.println("Double ) " + valueNode.getValue(Double.class));
		assertEquals("", null, valueNode.getValue(Double.class));
		valueNode = new JValueNode<String>("true");
		System.out.println("Boolean) " + valueNode.getValue(Boolean.class));
		assertEquals("", true, valueNode.getValue(Boolean.class));
		valueNode = new JValueNode<String>("1");
		System.out.println("Integer) " + valueNode.getValue(Integer.class));
		assertEquals("", new Integer(1), valueNode.getValue(Integer.class));
		System.out.println("Long   ) " + valueNode.getValue(Long.class));
		assertEquals("", new Long(1), valueNode.getValue(Long.class));
		valueNode = new JValueNode<String>("1.0");
		System.out.println("Float  ) " + valueNode.getValue(Float.class));
		assertEquals("", new Float(1.0f), valueNode.getValue(Float.class));
		System.out.println("Double ) " + valueNode.getValue(Double.class));
		assertEquals("", new Double(1.0), valueNode.getValue(Double.class));
		System.out.println(" -------------- ");

		valueNode = new JValueNode<Integer>(15);
		System.out.println("Boolean) " + valueNode.getValue(Boolean.class));
		assertEquals("", null, valueNode.getValue(Boolean.class));
		System.out.println("String ) " + valueNode.getValue(String.class));
		assertEquals("", "15", valueNode.getValue(String.class));
		System.out.println("Integer) " + valueNode.getValue(Integer.class));
		assertEquals("", new Integer(15), valueNode.getValue(Integer.class));
		System.out.println("Long   ) " + valueNode.getValue(Long.class));
		assertEquals("", new Long(15), valueNode.getValue(Long.class));
		System.out.println("Float  ) " + valueNode.getValue(Float.class));
		assertEquals("", new Float(15), valueNode.getValue(Float.class));
		System.out.println("Double ) " + valueNode.getValue(Double.class));
		assertEquals("", new Double(15), valueNode.getValue(Double.class));
		System.out.println(" -------------- ");

		valueNode = new JValueNode<Long>(16l);
		System.out.println("Boolean) " + valueNode.getValue(Boolean.class));
		assertEquals("", null, valueNode.getValue(Boolean.class));
		System.out.println("String ) " + valueNode.getValue(String.class));
		assertEquals("", "16", valueNode.getValue(String.class));
		System.out.println("Integer) " + valueNode.getValue(Integer.class));
		assertEquals("", new Integer(16), valueNode.getValue(Integer.class));
		System.out.println("Long   ) " + valueNode.getValue(Long.class));
		assertEquals("", new Long(16l), valueNode.getValue(Long.class));
		System.out.println("Float  ) " + valueNode.getValue(Float.class));
		assertEquals("", new Float(16), valueNode.getValue(Float.class));
		System.out.println("Double ) " + valueNode.getValue(Double.class));
		assertEquals("", new Double(16), valueNode.getValue(Double.class));
		System.out.println(" -------------- ");

		valueNode = new JValueNode<Float>(16.2f);
		System.out.println("Boolean) " + valueNode.getValue(Boolean.class));
		assertEquals("", null, valueNode.getValue(Boolean.class));
		System.out.println("String ) " + valueNode.getValue(String.class));
		assertEquals("", "16.2", valueNode.getValue(String.class));
		System.out.println("Integer) " + valueNode.getValue(Integer.class));
		assertEquals("", new Integer(16), valueNode.getValue(Integer.class));
		System.out.println("Long   ) " + valueNode.getValue(Long.class));
		assertEquals("", new Long(16l), valueNode.getValue(Long.class));
		System.out.println("Float  ) " + valueNode.getValue(Float.class));
		assertEquals("", new Float(16.2f), valueNode.getValue(Float.class));
		System.out.println("Double ) " + valueNode.getValue(Double.class));
		assertEquals("", new Double(16.200000762939453), valueNode.getValue(Double.class));
		System.out.println(" -------------- ");

		valueNode = new JValueNode<Double>(18.2);
		System.out.println("Boolean) " + valueNode.getValue(Boolean.class));
		assertEquals("", null, valueNode.getValue(Boolean.class));
		System.out.println("String ) " + valueNode.getValue(String.class));
		assertEquals("", "18.2", valueNode.getValue(String.class));
		System.out.println("Integer) " + valueNode.getValue(Integer.class));
		assertEquals("", new Integer(18), valueNode.getValue(Integer.class));
		System.out.println("Long   ) " + valueNode.getValue(Long.class));
		assertEquals("", new Long(18l), valueNode.getValue(Long.class));
		System.out.println("Float  ) " + valueNode.getValue(Float.class));
		assertEquals("", new Float(18.2f), valueNode.getValue(Float.class));
		System.out.println("Double ) " + valueNode.getValue(Double.class));
		assertEquals("", new Double(18.2), valueNode.getValue(Double.class));
		System.out.println(" -------------- ");

//		System.err.println("1) " + valueNode.getType());
//		System.err.println("2) " + valueNode.getValue());
//		System.err.println("3) " + valueNode.getValue().getClass());
//
//		Class<Float> klass = Float.class;
//		System.err.println("4) " + klass.getSimpleName() + "; ");
	}

}
