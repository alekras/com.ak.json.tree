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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ak.json.JInstrumentalNode;
import com.ak.json.JNode;
import com.ak.json.JParserException;
import com.ak.json.nodetree.JArrayNode;
import com.ak.json.nodetree.JObjectNode;
import com.ak.json.nodetree.JValueNode;
import com.ak.json.transform.JPath.Path;

/**
 *
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JTransformer {
  /** */
  private JNode originDoc;
  /** */
  private JNode template;
  /** */
  private static JParser parser = new JParser();
  /** */
  protected static final boolean DEBUG = false;

  /**
   *
   */
  public JTransformer() {
    super();
  }

  /**
   *
   * @param template document for transformation.
   */
  public JTransformer(final JNode template) {
    this.template = template;
  }

  /**
  *
  * @param templateFile file with template for transformation.
  */
  public JTransformer(final File templateFile) {
    try {
      this.template = parser.parse(templateFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param is input stream to template.
   */
  public JTransformer(final InputStream is) {
    try {
      this.template = parser.parse(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param jsonText template
   */
  public JTransformer(final String jsonText) {
    try {
      this.template = parser.parse(jsonText);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Transforms document.
   * @param jNode document to process.
   * @return JNode
   */
  public JNode process(final JNode jNode) {
    originDoc = jNode;
    return matchTemplate("/", originDoc);
  }

  /**
   * Transforms document.
   * @param documentFile document to process.
   * @return JNode
   * @throws IOException reading file exception
   * @throws JParserException parsing exception
   */
  public JNode process(final File documentFile) throws IOException, JParserException {
    originDoc = parser.parse(documentFile);
    return matchTemplate("/", originDoc);
  }

  /**
   * Transforms document.
   * @param is document to process.
   * @return JNode
   * @throws IOException reading file exception
   * @throws JParserException parsing exception
   */
  public JNode process(final InputStream is) throws IOException, JParserException {
    originDoc = parser.parse(is);
    return matchTemplate("/", originDoc);
  }

  /**
   * Transforms document.
   * @param jsonText document to process.
   * @return JNode
   * @throws IOException reading file exception
   * @throws JParserException parsing exception
   */
  public JNode process(final String jsonText) throws IOException, JParserException {
    originDoc = parser.parse(jsonText);
    return matchTemplate("/", originDoc);
  }

  /**
   *
   * @param source document to apply template
   * @param applayTemplateNode template value
   * @param var variables
   * @return JNode
   */
  @SuppressWarnings("unchecked")
  private JNode applyTemplate(final JNode source, final JNode applayTemplateNode, final JNode var) {
    JValueNode<Boolean> array = (JValueNode<Boolean>) applayTemplateNode.getNode("$array");
    Boolean isArray = (array != null) ? array.getValue() : true;

    List<JNode> nodes = processSelectNode(source, applayTemplateNode, var);
    JNode result;
//    for (JNode nd : nodes) {
//      System.out.println("   ### node :: " + nd);
//    }
    if (nodes.size() == 0) {
        result = new JValueNode<Object>();
        ((JValueNode<Object>) result).setValue(null);
    } else if (nodes.size() == 1 && (!isArray)) {
//        String [] path = ((JInstrumentalNode) nodes.get(0)).getPath();
//        System.out.println("   ### path :: " + Arrays.toString(path) + ", element :: \t\t" + path[path.length - 1] + ", " + ((JInstrumentalNode) nodes.get(0)).getKey());
        result = matchTemplate(((JInstrumentalNode) nodes.get(0)).getPath(), nodes.get(0));
    } else {
        result = new JArrayNode();
        for (JNode nd : nodes) {
          String [] path = ((JInstrumentalNode) nd).getPath();
//          System.out.println("   $$$ path :: " + Arrays.toString(path) + ", element :: \t\t" + path[path.length - 1] + ", " + ((JInstrumentalNode) nd).getKey());
          JNode generated = matchTemplate(path, nd);
//          System.out.println("   generated :: " + generated + ", path :: " + Arrays.toString(path) + ", node :: " + nd);
          result.addNode(generated);
        }
    }
    return result;
  }

  /**
   *
   * @param source document to apply template
   * @param value template value
   * @param var variables
   * @return List<JNode>
   */
  @SuppressWarnings("unchecked")
  private List<JNode> processSelectNode(final JNode source, final JNode value, final JNode var) {
    JValueNode<String> selector = (JValueNode<String>) value.getNode("$select");
    String select = selector.getValue();
    List<JNode> jnodes = new ArrayList<JNode>();
    JPath.jpath(jnodes, select, (select.startsWith("/")) ? originDoc : source, var);
    return jnodes;
  }

  /**
   *
   * @param source document to apply template
   * @param valueOf template value
   * @param var variables
   * @return JNode
   */
  @SuppressWarnings("unchecked")
  private JNode valueOf(final JNode source, final JNode valueOf, final JNode var) {
    List<JNode> list;
    switch (valueOf.getType()) {
      case OBJECT :
        list = processSelectNode(source, valueOf, var);
        if (list.size() == 0) {
          return null; // TODO
        } else {
          return list.get(0);
        }
      case ARRAY :
        StringBuffer sb = new StringBuffer();
        for (JNode nd : valueOf) {
          switch (nd.getType()) {
            case OBJECT :
              list = processSelectNode(source, nd, var);
              if (list.size() == 0) {
                sb.append("");
              } else {
                JValueNode<?> res = (JValueNode<?>) list.get(0);
                sb.append(res.getValue());
              }
              break;
            case VALUE :
              sb.append(((JValueNode<String>) nd).getValue());
              break;
              //$CASES-OMITTED$
            default:
              break;
          }
        }
        return (new JValueNode<String>(sb.toString()));
      //$CASES-OMITTED$
      default:
    }
    return null;
  }

  /**
   *
   * @param source document to apply template
   * @param value template value
   * @param var variables
   * @return JNode
   */
  private JNode copyOf(final JNode source, final JNode value, final JNode var) {
    return processSelectNode(source, value, var).get(0);
  }

  /**
   *
   * @param source document to apply template
   * @param value template value
   * @param var variables
   * @return JNode
   */
  private JNode generateContent(final JNode source, final JNode value, final JNode var) {
    if (DEBUG) {
      System.out.println(">>> generateContent()\n  source=" + source + "\n  value=" + value);
    }
    JNode result = null;
    JNode tempNode = null;
    switch (value.getType()) {
      case ARRAY :
        result = new JArrayNode();
        for (JNode node : value) {
          result.addNode(generateContent(source, node, var));
        }
        break;
      case OBJECT :
        tempNode = value.getNode("$apply-template");
        if (tempNode != null) {
          result = applyTemplate(source, tempNode, var);
        } else {
          tempNode = value.getNode("$value-of");
          if (tempNode != null) {
            result = valueOf(source, tempNode, var);
          } else {
            tempNode = value.getNode("$copy-of");
            if (tempNode != null) {
              result = copyOf(source, tempNode, var);
            } else {
              result = new JObjectNode();
              for (JNode nd : value) {
                result.addNode(generateContent(source, nd, var));
              }
            }
          }
        }
        break;
      case VALUE :
        result = value.clone();
        break;
      default:
        break;
    }
    ((JInstrumentalNode) result).setKey(((JInstrumentalNode) value).getKey());
    if (DEBUG) {
      System.out.println("<<< generateContent()\n  result=" + result + "\n");
    }
    return result;
  }

  /**
   *
   * @param path to process
   * @param source document to transform
   * @return JNode
   */
  private JNode matchTemplate(final String path, final JNode source) {
    return matchTemplate(new JPath.Path(path).getSegments(), source);
  }

  /**
   *
   * @param path to process
   * @param source document to transform
   * @return JNode
   */
  @SuppressWarnings("unchecked")
  private JNode matchTemplate(final String [] path, final JNode source) {
    if (DEBUG) {
      System.out.println(">>> matchTemplate() path= " + Arrays.toString(path) + "\n  source=" + source + "\n");
    }
    JNode matchedTemplate = null;
    for (JNode templ : template) {
      matchedTemplate = templ.getNode("$template");
      JValueNode<String> nd = (JValueNode<String>) matchedTemplate.getNode("$match");
      String match = nd.getValue();
      //      System.out.println("match = " + match);
      if (isMatch(match, path)) {
        break;
      }
    }
    if (DEBUG) {
      System.out.println("matched template = " + matchedTemplate);
    }
    if (matchedTemplate != null) {
      JNode var = matchedTemplate.getNode("$variable");
      if (var != null) {
        JNode val = processSelectNode(source, var, null).get(0).clone();
        String name = ((JValueNode<String>) var.getNode("$name")).getValue();

        var = new JObjectNode();
        ((JObjectNode) var).addNode(name, val);
//        System.out.println("var = " + var);
      }
      JNode value = matchedTemplate.getNode("$value");
      return generateContent(source, value.clone(), var); // TODO clone ???
    }
    return null;
  }

  private boolean isMatch(final String m, final String [] p) {
    if (DEBUG) {
      System.out.println(">>> isMatch(m=" + m + ", p=" + Arrays.toString(p) + ")");
    }
    boolean result = true;
    JPath.Path match = new Path(m);
    String [] matchSegments = match.getSegments();
    if (matchSegments.length > p.length) {
      result = false;
    } else {
      if (match.isAbsolute) {
        int i = 0;
        for (String ms : matchSegments) {
          if (!ms.equals(p[i++])) {
            result = false;
            break;
          }
        }
      } else {
        for (int i = matchSegments.length - 1, j = p.length - 1; i >= 0; j--, i--) {
          if (!matchSegments[i].equals(p[j])) {
            result = false;
            break;
          }
        }
      }
    }
    if (DEBUG) {
      System.out.println("<<< isMatch() returns: " + result);
    }
    return result;
  }
}
