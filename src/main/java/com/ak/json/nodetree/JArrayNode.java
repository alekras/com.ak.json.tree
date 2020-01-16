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

package com.ak.json.nodetree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ak.json.JInstrumentalNode;
import com.ak.json.JNode;

/**
 * Class JArrayNode represents JSON ARRAY element.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JArrayNode extends AbstractJNode implements JNode {
  /** Container for array elements. */
  private List<JNode> elements;

  /**
   * Default Constructor for detached node.
   */
  public JArrayNode() {
    super(null);
    init();
  }

  /**
   * Constructor for node attached to tree.
   * @param fParent reference to parent node.
   */
  public JArrayNode(final JNode fParent) {
    super(fParent);
    init();
  }

  /**
   * Initiation of the node.
   */
  private void init() {
    elements = new ArrayList<JNode>();
    type = JNodeType.ARRAY;
  }

  @Override
  public <T> JNode getNode(final T idx) {
    return elements.get((Integer) idx);
  }

  @Override
  public <K, T extends JNode> void addNode(final K idx, final T node) {
    ((AbstractJNode) node).parent = this;
    ((JInstrumentalNode) node).setKey(idx.toString());
    elements.add(((Integer) idx).intValue(), node);
  }

  @Override
  public void addNode(final JNode node) {
    addNode(elements.size(), node);
  }

  @Override
  public <K> void removeNode(final K selector) {
    elements.remove(((Integer) selector).intValue());
  }

  @Override
  public <N extends JNode> void removeNode(final N node) {
    elements.remove(node);
  }

  @Override
  public Iterator<JNode> iterator() {
    return elements.iterator();
  }

  @Override
  public List<JNode> getChildren() {
    return elements;
  }

  @Override
  public JNode clone() {
    JArrayNode copy = new JArrayNode(parent);
    for (JNode child : elements) {
      copy.addNode(child.clone());
    }
    copy.setKey(getKey());
    return copy;
  }

  @Override
  public String toString() {
    return toString("\n  ", false, true);
  }

  @Override
  public String toString(final String indent, final boolean showPath, final boolean deep) {
    StringBuffer value = new StringBuffer();
    String delimiter = "";
    value.append("[");
    for (JNode n : elements) {
      value.append(delimiter + indent);
      if (deep) {
        value.append(n.toString(indent + "  ", showPath, deep));
      } else {
        switch (n.getType()) {
          case ARRAY :
            value.append("[...]");
            break;
          case OBJECT :
            value.append("{...}");
            break;
          case VALUE :
            value.append(n.toString(indent + "  ", showPath, deep));
            break;
          default:
            break;
        }
      }
      delimiter = ",";
    }
    value.append(((indent.length() > 2) ? indent.substring(0, indent.length() - 2) : indent) + "]");
    return value.toString();
  }

  @Override
  public String toJson() {
    StringBuffer value = new StringBuffer();
    String delimiter = "";
    value.append("[");
    for (JNode n : elements) {
      value.append(delimiter + n.toJson());
      delimiter = ",";
    }
    value.append("]");
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
    if (!(obj instanceof JArrayNode)) {
      return false;
    }
    JArrayNode other = (JArrayNode) obj;
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
