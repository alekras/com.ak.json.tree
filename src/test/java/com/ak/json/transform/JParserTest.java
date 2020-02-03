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
//CSOFF: Magic
package com.ak.json.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ak.json.JInstrumentalNode;
import com.ak.json.JNode;
import com.ak.json.JParserException;
import com.ak.json.nodetree.JArrayNode;
import com.ak.json.nodetree.JObjectNode;
import com.ak.json.nodetree.JValueNode;

/**
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JParserTest {
  /** */
  protected JParser parser = new JParser();
  /** */
  protected static String [] fileList;

  /**
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    fileList = new String [] {
        "src/test/resources/1/source.json",
        "src/test/resources/1/template.json",
        "src/test/resources/2/source.json",
        "src/test/resources/2/template.json",
        "src/test/resources/3/source.json",
        "src/test/resources/3/template.json",
        "src/test/resources/sf.json",
        "src/test/resources/value.json"
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
  public void setUp() { /** */
  }

  /**
   */
  @After
  public void tearDown() { /** */
  }

  /**
   * Test method for {@link com.ak.json.transform.JParser#parse(java.lang.String)}.
   * @throws IOException while parsing
   */
  @Test
  public void testParseFile() throws IOException {
    for (String fileName : fileList) {
      BufferedReader reader = null;
      try {
        System.out.println(fileName + " :");
        JNode docTree = parser.parse(new File(fileName));
        String json = docTree.toJson();
        reader = new BufferedReader(new FileReader(fileName));
//        System.out.println(json);
//        System.out.println(compact(reader));

        assertEquals("failed: " + fileName, compact(reader), json);

      } catch (IOException e) {
        fail("IOException: " + fileName);
      } catch (JParserException e) {
        e.printStackTrace();
        fail("JParserException: " + fileName);
      } finally {
        if (reader != null) {
          reader.close();
        }
      }
    }
  }

  /**
   *
   * @param reader for json file
   * @return string
   * @throws IOException if IO error
   */
  private String compact(final BufferedReader reader) throws IOException {
    StringBuffer sb = new StringBuffer();
    int code;
    char symbol;
    boolean insideString = false;
    boolean escape = false;
    while ((code = reader.read()) >= 0) {
      symbol = (char) code;
      if (insideString) {
        switch (symbol) {
        case '\n':
        case '\r':
          break;
        case '\\':
          escape = true;
          break;
        case '"':
          if (!escape) {
            insideString = false;
          }
        default:
          sb.append(symbol);
          escape = false;
        }
      } else {
        switch (symbol) {
          case ' ':
          case '\n':
          case '\r':
          case '\t':
            break;
          case '"':
            insideString = true;
          default:
            sb.append(symbol);
        }
      }
    }
    return sb.toString();
  }

  /**
   * Test method for {@link com.ak.json.transform.JParser#parse(java.lang.String)}.
   * @throws IOException  while parsing
   */
  @Test
  public void testParsingErrors() throws IOException {
    JNode docTree;
    try {
      docTree = parser.parse("{\"a\":,\"b\":1}");
      fail("failed test #1 doc: " + docTree);
    } catch (JParserException e) {
    	System.err.println(e.getMessage());
      assertEquals("", "Json syntax : wrong position for ','", e.getMessage());
    }

    try {
      docTree = parser.parse("{\"a\":2,\"b\":1}}");
      fail("failed test #2 doc: " + docTree);
    } catch (JParserException e) {
      System.err.println(e.getMessage());
      assertEquals("", "Unexpected symbol(s) near end of document '}}'.", e.getMessage());
    }

    try {
      docTree = parser.parse("{\"a\":{\"c\":7}],\"b\":1}");
      fail("failed test #3 doc: " + docTree);
    } catch (JParserException e) {
      System.err.println(e.getMessage());
//      assertEquals("", "Unexpected symbol(s) near end of document '],\"b\":1}'.", e.getMessage());
      assertEquals("", "Json syntax : wrong symbol ] after {\"a\":{\"c\":7}}]", e.getMessage());
    }

    try {
      docTree = parser.parse("[[{\"a\":true,\"c\":[],\"b\":1}]");
      fail("failed test #4 doc: " + docTree);
    } catch (JParserException e) {
      assertEquals("", "Json syntax : opened parentheses do not match closed parentheses. Difference: '{' = 0 '[' = 1", e.getMessage());
    }
  }

  /**
   *
   * @throws IOException while parsing
   */
  @Test
  public void testGetValue() throws IOException {
    try {
      JNode docTree = parser.parse(testDoc());
      assertEquals("", new Integer(25), docTree.getNode("age").getValue(Integer.class));
      assertEquals("", new Long(4143456798L), docTree.getNode("phones").getNode(0).getValue(Long.class));
      assertEquals("", parser.parse("{\"street\": \"15, Main St.\",\"city\": \"Blo\\\"omington\",\"state\": \"Texas\"}"), docTree.getNode("address"));
      assertEquals("", new JValueNode<String>("15, Main St."), docTree.getNode("address").getNode("street"));
      assertEquals("", "15, Main St.", docTree.getNode("address").getNode("street").getValue(String.class));
    } catch (JParserException e) {
      fail(e.getMessage());
    }
  }

  /**
  *
  * @throws IOException while parsing
  */
  @SuppressWarnings("unchecked")
  @Test
  public void testChangeValue() throws IOException {
    try {
      JNode docTree = parser.parse(testDoc());
      JValueNode<Object> changedNode = (JValueNode<Object>) docTree.getNode("age");
      changedNode.setValue(new Integer(27));
      assertEquals("", new Integer(27), docTree.getNode("age").getValue(Integer.class));
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
  }

  /**
  *
  * @throws IOException while parsing
  */
  @Test
  public void testAddValue() throws IOException {
    try {
      JNode docTree = parser.parse(testDoc());
      JArrayNode changedNode = (JArrayNode) docTree.getNode("phones");
      changedNode.addNode(new JValueNode<Long>(4043242668L));
      assertEquals("", new Long(4043242668L), docTree.getNode("phones").getNode(4).getValue(Long.class));
      JObjectNode changedNode1 = (JObjectNode) docTree.getNode("address");
      changedNode1.addNode("zip code", new JValueNode<Integer>(33024));
      assertEquals("", new Integer(33024), docTree.getNode("address").getNode("zip code").getValue(Integer.class));
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
  }

  /**
  *
  * @throws IOException while parsing
  */
  @Test
  public void testRemoveValue() throws IOException {
    try {
      JNode docTree = parser.parse(testDoc());
      ((JObjectNode) docTree).removeNode("name");
      assertEquals("", null, docTree.getNode("name"));
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
  }

  /**
   * Test value parsing.
   * @throws IOException  while parsing
   */
  @Test
  public void testValueParsing() throws IOException {
    JNode docTree;
    try {
      docTree = parser.parse("2");
      assertEquals("Expects value node 2", new JValueNode<Integer>(2), docTree);
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
    try {
      docTree = parser.parse("2.5");
      assertEquals("Expects value node 2.5", new JValueNode<Float>(2.5f), docTree);
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
    try {
      docTree = parser.parse("true");
      assertEquals("Expects value node true", new JValueNode<Boolean>(true), docTree);
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
    try {
      docTree = parser.parse("false");
      assertEquals("Expects value node false", new JValueNode<Boolean>(false), docTree);
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
    try {
      docTree = parser.parse("null");
      assertEquals("Expects value node null", new JValueNode<Object>(null), docTree);
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
    try {
      docTree = parser.parse("\"'String'\"");
      assertEquals("Expects value node 'String'", new JValueNode<String>("'String'"), docTree);
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
  }

  /**
  *
  * @throws IOException while parsing
  */
  @Test
  public void testGetPath() throws IOException {
    try {
      JNode docTree = parser.parse(testDoc());
      JNode n = docTree.getNode("address").getNode("street");
      assertEquals("", "[address, street]", Arrays.toString(((JInstrumentalNode) n).getPath()));
      n = docTree.getNode("phones").getNode(0);
      assertEquals("", "[phones, 0]", Arrays.toString(((JInstrumentalNode) n).getPath()));
      n = docTree.getNode("phones").getNode(2).getNode("type");
      assertEquals("", "[phones, 2, type]", Arrays.toString(((JInstrumentalNode) n).getPath()));
    } catch (JParserException e) {
      System.out.println(e.getMessage());
      fail("1");
    }
  }

  /**
   *
   * @return String
   */
  private String testDoc() {
    return "{"
     + "\"name\": \"John\","
     + "\"age\": 25,"
     + "\"address\": {"
     + "    \"street\": \"15, Main St.\","
     + "    \"city\": \"Blo\\\"omington\","
     + "    \"state\": \"Texas\""
     + "},"
     + "\"phones\": ["
     + "    4143456798,"
     + "    7189151248,"
     + "    {"
     + "        \"type\": \"office\","
     + "        \"number\": 7189257336"
     + "    },"
     + "    {"
     + "        \"type\": \"\","
     + "        \"number\": 7189257336"
     + "    }"
     + "  ]"
     + "}";
  }
}
