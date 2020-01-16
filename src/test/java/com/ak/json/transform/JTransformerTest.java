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

/**
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JTransformerTest {
  /** */
  protected JTransformer transformer;
  /** */
  protected static String [][] fileList;

  /**
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    fileList = new String [][] {
//        {"src/test/resources/1/source.json", "src/test/resources/1/template.json", "src/test/resources/1/result.json"},
        {"src/test/resources/2/source.json", "src/test/resources/2/template.json", "src/test/resources/2/result.json"},
        {"src/test/resources/3/source.json", "src/test/resources/3/template.json", "src/test/resources/3/result.json"}
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
   * Test method for {@link com.ak.json.transform.JTransformer#process(java.lang.String)}.
   * @throws IOException while reading file
   */
  @Test
  public void testProcess() throws IOException {
    for (String [] fileNames : fileList) {
      BufferedReader reader = null;
      try {
        transformer = new JTransformer(new File(fileNames[1]));
        JNode docTree = transformer.process(new File(fileNames[0]));
        String json = docTree.toJson();
        System.out.println(docTree.toString());

        reader = new BufferedReader(new FileReader(fileNames[2]));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
          line = line.replaceAll(" ", "");
          sb.append(line);
        }
        System.out.println(json.replaceAll(" ", ""));
        System.out.println(sb.toString());

        assertEquals("failed: " + fileNames[1], sb.toString(), json.replaceAll(" ", ""));

      } catch (Exception e) {
        e.printStackTrace();
        fail("");
      } finally {
        reader.close();
      }
    }
  }

}
