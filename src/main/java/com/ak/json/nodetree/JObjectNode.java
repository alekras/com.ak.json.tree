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

package com.ak.json.nodetree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ak.json.JInstrumentalNode;
import com.ak.json.JNode;

/**
 * Class JObjectNode represents JSON OBJECT element.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JObjectNode extends AbstractJNode implements JNode {
  /** Map Container keeps properties of the node.*/
  private Map<String, JNode> elements;

  /**
   * Default constructor for detached node.
   */
  public JObjectNode() {
    super(null);
    init();
  }

  /**
   * Constructor for node attached to tree.
   * @param fParent reference to parent node.
   */
  public JObjectNode(final JNode fParent) {
    super(fParent);
    init();
  }

  /**
   * Initiation of the node.
   */
  private void init() {
    elements = new LinkedHashMap<String, JNode>();
    type = JNodeType.OBJECT;
  }

  @Override
  public <T> JNode getNode(final T name) {
    return elements.get(name);
  }

  @Override
  public List<JNode> getChildren() {
    return Arrays.asList(elements.values().toArray(new JNode[] {}));
  }

  @Override
  public JNode clone() {
    JObjectNode copy = new JObjectNode(parent);
    for (JNode jnode : this) {
      copy.addNode(jnode.clone());
    }
    return copy.setKey(getKey());
  }

  @Override
  public <T extends JNode> void addNode(final T jnode) {
    String key = ((AbstractJNode) jnode).getKey();
    if (key != null) {
      ((AbstractJNode) jnode).parent = this;
      elements.put(key, jnode);
    } else {
        throw new RuntimeException("Add wrong element " + jnode + " without key.");
    }
  }

  @Override
  public <K, T extends JNode> void addNode(final K pKey, final T jnode) {
    String key = (String) pKey;
    ((JInstrumentalNode) jnode).setKey(key);
    addNode(jnode);
  }

  @Override
  public <K> void removeNode(final K selector) {
    elements.remove(selector);
  }

  @Override
  public <N extends JNode> void removeNode(final N node) {
    elements.remove(((JInstrumentalNode) node).getKey());
  }

  @Override
  public Iterator<JNode> iterator() {
    return elements.values().iterator();
  }

  @Override
  public Set<String> fieldNames() {
    return elements.keySet();
  }

   @Override
  public String toString() {
    return toString("\n  ", false, true);
  }

  @Override
  public String toString(final String indent, final boolean showPath, final boolean deep) {
    StringBuffer value = new StringBuffer();
    String delimiter = "";
    value.append("{");
    for (Entry<String, JNode> n : elements.entrySet()) {
      JNode jn = n.getValue();
      value.append(delimiter + indent + "\"" + n.getKey() + "\":");
      if (deep) {
        value.append(jn.toString(indent + "  ", showPath, deep));
      } else {
        switch (jn.getType()) {
          case ARRAY :
            value.append("[...]");
            break;
          case OBJECT :
            value.append("{...}");
            break;
          case VALUE :
            value.append(jn.toString(indent + "  ", showPath, deep));
            break;
          default:
            break;
        }
      }
      delimiter = ",";
    }
    value.append(((indent.length() > 2) ? indent.substring(0, indent.length() - 2) : indent) + "}");
    return value.toString();
  }

  @Override
  public String toJson() {
    StringBuffer value = new StringBuffer();
    String delimiter = "";
    value.append("{");
    for (Entry<String, JNode> n : elements.entrySet()) {
      value.append(delimiter + "\"" + n.getKey() + "\":" + n.getValue().toJson());
      delimiter = ",";
    }
    value.append("}");
    return value.toString();
  }

 @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((elements == null) ? 0 : elements.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof JObjectNode)) {
      return false;
    }
    JObjectNode other = (JObjectNode) obj;
    if (elements == null) {
      if (other.elements != null) {
        return false;
      }
    } else if (!elements.equals(other.elements)) {
      return false;
    }
    return true;
  }

}
