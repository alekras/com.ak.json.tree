/**
 * Copyright (C) 2014-2014 by krasnop@bellsouth.net (Alexei Krasnopolski)
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
//CSOFF: Inner Assignment
package com.ak.json.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ak.json.JNode;
import com.ak.json.JParserException;

/**
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JPathTest {
  /** */
  protected static JParser parser = new JParser();
  /** */
  protected static String [] [] fixture;
  /** */
  protected static JNode bookStore;


  /**
   * @throws JParserException while parsing test doc.
   * @throws IOException while reading file.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws IOException, JParserException {
    bookStore = parser.parse(new File("src/test/resources/book_store.json"));
    fixture = new String [] [] {
/* Node name*/
      {"/books/Martin Eden/amount", "r0.txt"},
/* / absolute and relative path */
      {"/book-store/*/book/title", "r1.txt"},
      {"/books/Martin Eden[/books/Martin Eden/amount eq 0]/price", "r10.txt"},
      {"/book-store/*/book[author/last-name eq \"King\"]", "r4.txt"},
      {"/book-store/*/book/author/last-name[. eq \"King\"]/../../year", "r9.txt"},
      {"/book-store/*/book/author", "r2.txt"},
      {"/book-store/*/book/author[last-name eq \"London\"]", "r3.txt"},
/* // */
      {"//price", "r7.txt"},
      {"book-store//price", "r8.txt"},
/* integer number as array index */
      {"/book-store/3/book/author/*", "r5.txt"},
      {"/book-store/*/book[price ge 8]", "r6.txt"},
/* axis */
      {"/book-store/2/book/author/ancestor-or-self::.", "r12.txt"},
      {"/book-store/2/book/author/ancestor::.", "r13.txt"},
      {"/book-store/4/book/child::.", "r14.txt"},
      {"/book-store/3/book/descendant-or-self::.", "r15.txt"},
      {"/book-store/3/book/descendant::.", "r16.txt"},
      {"/book-store/1/book/title/parent::.", "r17.txt"},
      {"/book-store/5/book/author/self::first-name", "r20.txt"},
      {"/book-store/*[not(book/author/last-name = preceding-sibling::book/author/last-name)]/book/author", "r11.txt"},
      {"/books/*[not(./author = preceding-sibling::./author)]/price", "r18.txt"},
      {"/books/*[not(./author = following-sibling::./author)]/price", "r19.txt"},
      {"/*[Roadwork/price = 7.19]/*[amount = 15]", "r21.txt"}
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
   * @throws IOException while reading file.
   */
  @Test
  public void testJpath() throws IOException {
    for (String [] testEntry : fixture) {
      System.out.println(">>>>>>>>>>>> jpath: " + testEntry[0] + "  result in file: " + testEntry[1]);
      List<JNode> result = JPath.jpath(testEntry[0], bookStore);
      for (JNode nd : result) {
        System.out.println(nd.toJson());
      }
      compareWithFile(result, "src/test/resources/jpath/" + testEntry[1]);
      System.out.println("<<<<<<<<<<<<\n");
    }
  }

  /**
   *
   * @param nodes tested document
   * @param fileName name of result file
   * @throws IOException while reading file
   */
  private void compareWithFile(final List<JNode> nodes, final String fileName) throws IOException {
      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        Iterator<JNode> iter = nodes.iterator();
        while ((line = reader.readLine()) != null && iter.hasNext()) {
          assertEquals("does not match " + fileName + ":", line, iter.next().toJson());
        }
        if (line != null || iter.hasNext()) {
          fail("number of entries does not match " + fileName);
        }
      } catch (IOException e) {
        e.printStackTrace();
        fail("because io failure");
      } finally {
        if (reader != null) {
          reader.close();
        }
      }
  }

  /**
   *
   * @return String
   */
  @SuppressWarnings("unused")
  private String testDoc() {
    return "{"
     + "\"name\": \"John\","
     + "\"age\": 25,"
     + "\"address\": {"
     + "    \"street\": \"15, Main St.\","
     + "    \"city\": \"Bloomington\","
     + "    \"state\": \"Texas\""
     + "},"
     + "\"phones\": ["
     + "    4143456798,"
     + "    7189151248,"
     + "    {"
     + "        \"type\": \"office\","
     + "        \"number\": 7189257336"
     + "    }"
     + "  ]"
     + "}";
  }
}
