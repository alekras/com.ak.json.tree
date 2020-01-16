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

import com.ak.json.JNode;

/**
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 * @param V - Java type of value
 *
 */
public class JValueNode<V> extends AbstractJNode {
  /** Value of this simple node. */
  private V primitiveValue;

  /**
   * Default constructor for detached node.
   */
  public JValueNode() {
    super(null);
    init();
  }

  /**
   * Constructor for detached node.
   * @param val value of the node.
   */
  public JValueNode(final V val) {
    this();
    setValue(val);
  }

  /**
   * Constructor for node attached to tree.
   * @param fParent parent node in tree.
   */
  public JValueNode(final JNode fParent) {
    super(fParent);
    init();
  }

  /**
   * Constructor for node attached to tree.
   * @param fParent parent node in tree.
   * @param val value of the node.
   */
  public JValueNode(final JNode fParent, final V val) {
    this(fParent);
    setValue(val);
  }

  /**
   * Initiation of the node.
   */
  private void init() {
    type = JNodeType.VALUE;
  }

  @Override
  public JNode clone() {
    JValueNode<V> copy = new JValueNode<V>(parent);
    copy.setValue(primitiveValue);
    copy.setKey(getKey());
    return copy;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V getValue() {
    return primitiveValue;
  }

  @Override
  public <V1> V1 getValue(final Class<V1> pType) {
    return pType.cast(primitiveValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V1> void setValue(final V1 value) {
    this.primitiveValue = (V) value;
  }

  @Override
  public <T> JNode getNode(final T selector) {
    throw new RuntimeException("Try to get node from " + this.type + " node.");
  }

  @Override
  public <K, T extends JNode> void addNode(final K idx, final T node) {
    throw new RuntimeException("Try to add node to " + this.type + " node.");
  }

  @Override
  public void addNode(final JNode node) {
    throw new RuntimeException("Try to add node to " + this.type + " node.");
  }

  @Override
  public <T> void removeNode(final T selector) {
    throw new RuntimeException("Try to remove node from " + this.type + " node.");
  }

  @Override
  public <N extends JNode> void removeNode(final N node) {
    throw new RuntimeException("Try to remove node from " + this.type + " node.");
  }

  @Override
  public String toString() {
    return toString("", false, true);
  }

  @Override
  public String toString(final String indent, final boolean showPath, final boolean deep) {
    return toJson();
  }

  @Override
  public String toJson() {
    if (primitiveValue == null) {
      return "null";
    }
    if (primitiveValue instanceof String) {
      return "\"" + primitiveValue.toString() + "\"";
    }
    return String.valueOf(primitiveValue);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((primitiveValue == null) ? 0 : primitiveValue.hashCode());
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
    if (!(obj instanceof JValueNode)) {
      return false;
    }
    @SuppressWarnings("unchecked")
    JValueNode<V> other = (JValueNode<V>) obj;
    if (primitiveValue == null) {
      if (other.primitiveValue != null) {
        return false;
      }
    } else if (!primitiveValue.equals(other.primitiveValue)) {
      return false;
    }
    return true;
  }

}
