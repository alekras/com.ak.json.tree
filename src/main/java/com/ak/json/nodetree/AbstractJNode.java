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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ak.json.JInstrumentalNode;
import com.ak.json.JNode;

/**
 * Class AbstractJNode represents common methods set for different types of JNode.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public abstract class AbstractJNode implements JNode, JInstrumentalNode, Cloneable {
  /** Parent node in node tree. */
  protected JInstrumentalNode parent;
  /** Key of given node. Can be null for root node or elements of ARRAY node.
   *  TODO avoid duplication of this field (Map of JObjectNode already keep it) although it convenient for parsing and transform. */
  private String key;
  /** Type of the node. */
  protected JNodeType type;
  /** Empty set. */
  private static final Set<String> EMPTY_SET = new HashSet<String>();
  /** Empty list. */
  private static final List<JNode> EMPTY_LIST = new ArrayList<JNode>();

  /**
   * Constructor.
   */
  protected AbstractJNode() {
  }

  /**
   * Constructor.
   * @param fParent reference to parent node.
   */
  protected AbstractJNode(final JNode fParent) {
    parent = (JInstrumentalNode) fParent;
  }

  @Override
  public JNodeType getType() {
    return type;
  }

  @Override
  public <V> void setValue(final V v) {
    throw new RuntimeException("Try to set value " + v + " to node type " + this.type);
  }

  @Override
  public <V> V getValue() {
    throw new RuntimeException("Try to get value from node type " + this.type);
  }

  @Override
  public <T> T getValue(final Class<T> pType) {
    throw new RuntimeException("Try to get value from node type " + this.type);
  }

  @Override
  public JNode getParent() {
    return parent;
  }

  @Override
  public JNode getDocRoot() {
    JNode parnt = parent;
    while (parnt.getParent() != null) {
      parnt = parnt.getParent();
    }
    return parnt;
  }

  @Override
  public String [] getPath() {
    List<String> path = new ArrayList<String>();
    path.add(0, key);
    JNode parnt = parent;
    while (parnt.getParent() != null) {
      path.add(0, ((JInstrumentalNode) parnt).getKey());
      parnt = parnt.getParent();
    }
    return path.toArray(new String [] {});   
  }

  @Override
  public Iterator<JNode> iterator() {
    return EMPTY_LIST.iterator();
  }

  @Override
  public List<JNode> getChildren() {
    return EMPTY_LIST;
  }

  @Override
  public Set<String> fieldNames() {
    return EMPTY_SET;
  }

  @Override
  public abstract JNode clone();

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public JNode setKey(final String pKey) {
    this.key = pKey;
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AbstractJNode)) {
      return false;
    }
    AbstractJNode other = (AbstractJNode) obj;
    if (type != other.type) {
      return false;
    }
    return true;
  }

}
