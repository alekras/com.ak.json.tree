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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import com.ak.json.JInstrumentalNode;
import com.ak.json.JNode;
import com.ak.json.JParseEvent;
import com.ak.json.JParserException;
import com.ak.json.nodetree.JArrayNode;
import com.ak.json.nodetree.JObjectNode;
import com.ak.json.nodetree.JValueNode;

//CSOFF: Magic
/**
 * Class JParser converts textual representation of a Json document to tree of
 * JNode nodes.
 *
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JParser {
  /**
   * The matrix allows to check: is it possible to have sequence of two events
   * during parsing? If checkEvents[previusEvent][currentEvent] == true then the
   * test is pass.
   */
  private static boolean[][] checkEvents;
  static {
    checkEvents = new boolean[][] {
        // START_OBJECT START_ARRAY END_OBJECT END_ARRAY FIELD_NAME VALUE
        // VALUE_STRING DELIMITER
        // [0] [1] [2] [3] [4] [5] [6] [7]
        /* START_OBJECT [0] */ { false, false, true, false, true, false, false,
            false },
        /* START_ARRAY [1] */ { true, true, false, true, false, true, true,
            false },
        /* END_OBJECT [2] */ { false, false, true, true, false, false, false,
            true },
        /* END_ARRAY [3] */ { false, false, true, true, false, false, false,
            true },
        /* FIELD_NAME [4] */ { true, true, false, false, false, true, true,
            false },
        /* VALUE [5] */ { true, true, true, true, true, true, true, false },
        /* VALUE_STRING [6] */ { true, true, true, true, true, true, true,
            false },
        /* DELIMITER [7] */ { true, true, false, false, true, true, true,
            false } };
  }

  /**
   * Default constructor.
   */
  public JParser() {
  }

  /**
   * Parse file.
   *
   * @param file
   *          contains JSON document as a text.
   * @return JNode tree representation of JSON document (root node of tree).
   * @throws IOException
   *           while reading file.
   * @throws JParserException
   *           while parsing text.
   */
  public JNode parse(final File file) throws IOException, JParserException {
    return parse(new BufferedReader(
        new InputStreamReader(new FileInputStream(file), "utf-8")));
  }

  /**
   * Parse document from stream.
   *
   * @param inputStream
   *          contains JSON document as a text.
   * @return JNode tree representation of JSON document (root node of tree).
   * @throws IOException
   *           while reading file.
   * @throws JParserException
   *           while parsing text.
   */
  public JNode parse(final InputStream inputStream)
      throws IOException, JParserException {
    return parse(
        new BufferedReader(new InputStreamReader(inputStream, "utf-8")));
  }

  /**
   * Parse string.
   *
   * @param jsonText
   *          contains JSON document as a text.
   * @return JNode tree representation of JSON document (root node of tree).
   * @throws IOException
   *           while reading file.
   * @throws JParserException
   *           while parsing text.
   */
  public JNode parse(final String jsonText)
      throws JParserException, IOException {
    return parse(new BufferedReader(new StringReader(jsonText)));
  }

  /**
   * Parse document from buffered reader.
   *
   * @param reader
   *          contains JSON document as a text.
   * @return JNode tree representation of JSON document (root node of tree).
   * @throws IOException
   *           while reading file.
   * @throws JParserException
   *           while parsing text.
   */
  private JNode parse(final BufferedReader reader)
      throws JParserException, IOException {
    try {
      Counters levels = new Counters();
      recurLvl = 0;
      JNode result = parse(null, null, reader, levels, null);
      if (levels.getArrayLevel() != 0 || levels.getObjectLevel() != 0) {
        throw new JParserException(
            "Json syntax : opened parentheses do not match closed parentheses. Difference: "
                + "'{' = " + levels.getObjectLevel() + " '[' = "
                + levels.getArrayLevel());
      }
      return result;
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  /**
   * Extract from reader stream sequence of symbols that represents JSON string
   * term.
   *
   * @param reader
   *          is stream of symbols.
   * @return String
   * @throws IOException
   *           while reading from stream.
   */
  private String readString(final BufferedReader reader) throws IOException {
    int code;
    char symbol;
    boolean escape = false;
    StringBuffer sb = new StringBuffer();
    while ((code = reader.read()) >= 0) {
      symbol = (char) code;
      switch (symbol) {
      case '"':
        if (escape) {
          sb.append(symbol);
          escape = false;
        } else {
          return sb.toString();
        }
        break;
      case '\\':
        sb.append(symbol);
        escape = true;
        break;
      case '\n':
      case '\r':
        break; // TODO throw exception
      default:
        sb.append(symbol);
        escape = false;
        break;
      }
    }
    return sb.toString();
  }

  /**
   * After parsing the method checks a tail of file/stream/string for unexpected
   * symbols and returns it.
   *
   * @param reader
   *          is stream of symbols.
   * @return String
   * @throws IOException
   *           while reading from stream.
   */
  private String flushStream(final BufferedReader reader) throws IOException {
    int code;
    char symbol;
    StringBuffer sb = new StringBuffer();
    while ((code = reader.read()) >= 0) {
      symbol = (char) code;
      switch (symbol) {
      case '\n':
      case '\r':
      case '\t':
      case ' ':
        break;
      default:
        sb.append(symbol);
        break;
      }
    }
    return sb.toString();
  }

  /**
   * Read one or more symbols from reader, then generates and returns parser
   * event as JsonPace object.
   *
   * @param reader
   *          is stream of symbols.
   * @return JsonPace
   * @throws IOException
   *           while reading from stream.
   * @throws JParserException
   *           while parsing document.
   */
  private JsonPace read(final BufferedReader reader)
      throws IOException, JParserException {
    char symbol;
    boolean isArrayEnd;
    JParseEvent predictedEvent = null;
    StringBuffer value = new StringBuffer();
    while (true) {
      reader.mark(100);
      int s = reader.read();
      if (s < 0) {
        break;
      }
      symbol = (char) s;
      isArrayEnd = false;
      switch (symbol) {
      case '[':
        return new JsonPace(JParseEvent.START_ARRAY, "[");
      case '{':
        return new JsonPace(JParseEvent.START_OBJECT, "{");
      case ']':
        isArrayEnd = true;
        //$FALL-THROUGH$
      case '}':
        if (!(value.length() == 0
            && predictedEvent != JParseEvent.VALUE_STRING)) {
          reader.reset();
          return new JsonPace(predictedEvent, value.toString());
        }
        return (isArrayEnd) ? (new JsonPace(JParseEvent.END_ARRAY, "]"))
            : (new JsonPace(JParseEvent.END_OBJECT, "}"));
      case '"':
        value.append(readString(reader));
        predictedEvent = JParseEvent.VALUE_STRING;
        break;
      case ',':
        if (predictedEvent == JParseEvent.VALUE_STRING
            || (predictedEvent == JParseEvent.VALUE && value.length() != 0)) {
          return new JsonPace(predictedEvent, value.toString()); // temporarily
        }
        return new JsonPace(JParseEvent.DELIMITER, ",");
      case ':':
        if (value.length() != 0) {
          return new JsonPace(JParseEvent.FIELD_NAME, value.toString());
        }
        throw new JParserException(
            "Json syntax : unexpected symbol '" + symbol + "'");
      case ' ':
      case '\n':
      case '\r':
      case '\t':
      case '\b':
      case '\f':
        break;
      default:
        value.append(symbol);
        predictedEvent = JParseEvent.VALUE;
      }
    }
    if (predictedEvent != null && (predictedEvent == JParseEvent.VALUE_STRING
        || predictedEvent == JParseEvent.VALUE)) {
      return new JsonPace(predictedEvent, value.toString());
    }
    return null;
  }

  private int recurLvl = 0;

  /**
   * The method parse() is recursive function that processes one parsing event.
   * Recursion stops when it completes parsing all child of root JSON element.
   *
   * @param current
   *          node of tree generated from parsed text.
   * @param savedKey
   *          key saved during FIELD_NAME event to use it create current node.
   * @param reader
   *          is stream of symbols.
   * @param levels
   *          is Counters object that counts brackets in parsed JSON document.
   * @param lastEvent
   *          previously processed parsing event.
   * @return JNode root node of generated tree.
   * @throws JParserException
   *           during parsing step.
   */
  private JNode parse(
      JNode current,
      final String savedKeyPar,
      final BufferedReader reader,
      final Counters levels,
      final JsonPace lastJsonPacePar
    ) throws JParserException {
    JsonPace jp = null;
    String savedKey = savedKeyPar;
    JsonPace lastJsonPace = lastJsonPacePar;
    JNode returnNode = null;

//    System.out.println(">>> parse() recurLvl:" + (recurLvl++));

    do {
      try {
        jp = read(reader);
      } catch (IOException e) {
        e.printStackTrace(); // TODO replace with exception.
      }
/*
      System.out.println("--> parse.LOOP\n current jp:" + jp + "\n lastJsonPace:" + lastJsonPace + "\n counters:" + levels + "\n current node: " + current);
      if (current != null && current.getParent() != null) {
        System.out.println("   parent: " + current.getParent());
      } else {
        System.out.println("   parent: null");
      }
      System.out.println();
*/
      if (jp != null) {
        JParseEvent jtoken = jp.event;
        if (!checkPrecedent(jtoken, (lastJsonPace != null) ? lastJsonPace.event : null)) {
          String message = "";
          if (current != null && current.getParent() != null) {
            message = " near " + current.getParent().toJson();
          }
          if (jtoken == JParseEvent.DELIMITER) {
            throw new JParserException(
                "Json syntax : wrong position for ','" + message);
          }
          throw new JParserException("Json syntax : wrong format" + message
              + " <" + lastJsonPace.event + "," + jtoken + ">");
        }

        switch (jtoken) {
        case START_ARRAY:
          JNode newArrayNode = new JArrayNode();
          if (current != null) {
            current.addNode(((JInstrumentalNode) newArrayNode).setKey(savedKey));
          }
          returnNode = parse(newArrayNode, null, reader, levels.startArr(), jp);
          lastJsonPace = new JsonPace(JParseEvent.END_ARRAY, "]");
//          if (returnNode != null && returnNode.getParent() != null) {
          if (levels.getLevel() > 0) {
            continue;
          }
          break;
        case START_OBJECT:
          JNode newObjectNode = new JObjectNode();
          if (current != null) {
            current.addNode(((JInstrumentalNode) newObjectNode).setKey(savedKey));
          }
          returnNode = parse(newObjectNode, null, reader, levels.startObj(), jp);
          lastJsonPace = new JsonPace(JParseEvent.END_OBJECT, "}");
//          if (returnNode != null && returnNode.getParent() != null) {
          if (levels.getLevel() > 0) {
            continue;
          }
          break;
        case END_ARRAY:
          levels.endArr();
          if (levels.getArrayLevel() < 0) {
            throw new JParserException(
                "Json syntax : wrong symbol ] after " + current.toJson() + jp.value);
          }
//          System.out.println("<<< parse(.) recurLvl:" + (--recurLvl));
          return current;
        case END_OBJECT:
          levels.endObj();
          if (levels.getObjectLevel() < 0) {
            throw new JParserException(
                "Json syntax : wrong symbol } after " + current.toJson() + jp.value);
          }
//          System.out.println("<<< parse(..) recurLvl:" + (--recurLvl));
          return current;
        case FIELD_NAME:
          lastJsonPace = jp;
          savedKey = jp.value;
          continue;
        case VALUE_STRING:
        case VALUE:
          if (current != null) {
            current.addNode(((JInstrumentalNode) valueParse(jp)).setKey(savedKey));
          } else {
            current = returnNode = valueParse(jp);
          }
          lastJsonPace = jp;
          continue;
        case DELIMITER:
          lastJsonPace = jp;
          continue;
        default:
          String message = "";
          if (current != null && current.getParent() != null) {
            message = " near " + current.getParent().toJson();
          }
          throw new JParserException("Unknown parsing event" + message);
        }
        current = returnNode;
        break;
      } else {
        current = returnNode;
        break;
      }
    } while (true);

    try {
      String message = flushStream(reader);
      if (message.length() == 0) {
//        System.out.println("<<< parse(...) recurLvl:" + (--recurLvl));
        return current;
      }
      throw new JParserException(
          "Unexpected symbol(s) near end of document '" + lastJsonPace.value + message + "'.");
    } catch (IOException e) {
      throw new JParserException(e);
    }
  }

  /**
   * Parse value of OBJECT property.
   *
   * @param jp
   *          represents current parsing event
   * @return JNode
   */
  private JNode valueParse(final JsonPace jp) {
    try {
      JNode value = new JValueNode<Object>();
      if (jp.event == JParseEvent.VALUE_STRING) {
        JValueNode<String> v = new JValueNode<String>();
        v.setValue(jp.value);
        value = v;
      } else {
        switch (jp.value) {
        case "null":
          break;
        case "true":
        case "false":
          JValueNode<Boolean> v = new JValueNode<Boolean>();
          v.setValue(new Boolean(jp.value));
          value = v;
          break;
        default:
          if (jp.value.matches("^[\\-\\+]?\\d+$")) {
            try {
              JValueNode<Integer> vv = new JValueNode<Integer>();
              vv.setValue(Integer.valueOf(jp.value));
              value = vv;
            } catch (NumberFormatException e) {
              try {
                JValueNode<Long> vv = new JValueNode<Long>();
                vv.setValue(Long.valueOf(jp.value)); // TODO BigInteger
                value = vv;
              } catch (NumberFormatException e1) {
                throw new JParserException(e1);
              }
            }
          } else {
            try {
              JValueNode<Float> vv = new JValueNode<Float>();
              vv.setValue(Float.valueOf(jp.value)); // TODO check for Double,
                                                    // Big Number
              value = vv;
            } catch (NumberFormatException e) {
              throw new JParserException(e);
            }
          }
          break;
        }
      }
      return value;
    } catch (Exception e) {
      e.printStackTrace(); // TODO replace with exception.
    }
    return null;
  }

  /**
   * Check if two events are happened in right sequence.
   *
   * @param currentEvent
   *          current event.
   * @param lastEvent
   *          previous event.
   * @return true if sequence is allowed.
   */
  private boolean checkPrecedent(
      final JParseEvent currentEvent,
      final JParseEvent lastEvent
    ) {
    if (lastEvent == null) {
      return true;
    }
    return checkEvents[lastEvent.ordinal()][currentEvent.ordinal()];
  }

  /**
   * Helper class JsonPace represents a pair of parsing event and corresponded
   * value if applicable.
   *
   * @author alexeikrasnopolski
   *
   */
  private class JsonPace {
    /** Parsing event. */
    protected JParseEvent event;
    /** Value associated with the event. (if applicable) */
    protected String value;

    /**
     * Constructor.
     *
     * @param pEvent
     *          parsing event.
     * @param pValue
     *          associated value.
     */
    public JsonPace(final JParseEvent pEvent, final String pValue) {
      this.event = pEvent;
      this.value = pValue;
    }

    @Override
    public String toString() {
      return "Pace: event=" + event.toString() + ", value='" + value + "'";
    }
  }

  /**
   * Helper class Counters counts number of brackets in JSON document.
   *
   * @author alexeikrasnopolski
   *
   */
  private class Counters {
    /** Difference between opened and closed curly brackets. */
    private Counter curly;
    /** Difference between opened and closed square brackets. */
    private Counter square;

    /**
     * Default constructor.
     */
    Counters() {
      curly = new Counter();
      square = new Counter();
    }

    /**
     * Action when OBJECT starts.
     *
     * @return this
     */
    Counters startObj() {
      curly.increment();
      return this;
    }

    /**
     * Action when OBJECT ends.
     *
     * @return this
     */
    Counters endObj() {
      curly.decrement();
      return this;
    }

    /**
     * Action when ARRAY starts.
     *
     * @return this
     */
    Counters startArr() {
      square.increment();
      return this;
    }

    /**
     * Action when ARRAY ends.
     *
     * @return this
     */
    Counters endArr() {
      square.decrement();
      return this;
    }

    /**
     * Get nesting value for objects.
     *
     * @return int
     */
    int getObjectLevel() {
      return curly.getCount();
    }

    /**
     * Get nesting value for arrays.
     *
     * @return int
     */
    int getArrayLevel() {
      return square.getCount();
    }

    int getLevel() {
      return square.getCount() + curly.getCount();
    }

    /**
     *@return
     *@see java.lang.Object#toString()
     */

    @Override
    public String toString() {
      return "Counters [curly=" + curly + ", square=" + square + "]";
    }

  }

  /**
   * Helper class Counter keeps difference between opened and closed brackets in
   * JSON document.
   *
   * @author alexeikrasnopolski
   *
   */
  private class Counter {
    /** Counter of brackets. */
    private int counter = 0;

    Counter() {
    }

    /**
     * Get count.
     *
     * @return int
     */
    int getCount() {
      return counter;
    }

    /**
     * Increment counter.
     */
    void increment() {
      counter++;
    }

    /**
     * Decrement counter.
     */
    void decrement() {
      counter--;
    }

    /**
     *@return
     *@see java.lang.Object#toString()
     */

    @Override
    public String toString() {
      return "Counter {" + counter + "}";
    }

  }
}
