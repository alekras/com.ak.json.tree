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

package com.ak.json;

import java.util.Iterator;
import java.util.List;

import com.ak.json.nodetree.JNodeType;

/**
 * Interface JNode represents operation are common for all types of node of tree.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public interface JNode extends Iterable<JNode> {
  /**
   * Returns a type of the node.
   * @return type of the node.
   */
  JNodeType getType();

  /**
   * Returns child node of this node by selector. If type of this node is JNodeType.ARRAY a selector has to be Integer type
   * and represents index of the array member. If type of this node is JNodeType.OBJECT a selector has to be String and
   * represents key of object property.
   * @param selector is index or key.
   * @param <K> type of the selector: Integer or String.
   * @return JNode
   */
  <K> JNode getNode(K selector);

  /**
   * Add element to composite node (without name).
   * @param node to be added.
   * @param <T> class of added node.
   */
  <T extends JNode> void addNode(T node); // TODO void ???

  /**
   * Add element to composite node.
   * @param key name of the field
   * @param node to be added.
   * @param <T> class of added node.
   * @param <K> class of key (String for field name, Integer for node in JArray).
   */
  <K, T extends JNode> void addNode(K key, T node); // TODO void ???

  /**
   * Remove child node of this node by selector. If type of this node is JNodeType.ARRAY a selector has to be Integer type
   * and represents index of the array member. If type of this node is JNodeType.OBJECT a selector has to be String and
   * represents key of object property..
   * @param selector is index or key.
   * @param <K> type of the selector: Integer or String.
   */
  <K> void removeNode(K selector); // TODO void ???

  /**
   * Remove child node of this node.
   * @param node to remove
   * @param <N> type of node
   */
  <N extends JNode> void removeNode(N node); // TODO void ???

  /**
   * Return value of JValueNode.
   * @return value of V type.
   * @param <V> type of value
   */
  <V> V getValue();

  /**
   * Set value of V type for this node.
   * @param value to set for the node.
   * @param <V> type of value
   */
  <V> void setValue(V value);

  /**
   * Return value of JValueNode.
   * @param type class that represents returned value.
   * @param <V> type of returned value: Integer, Boolean, Float, Double or String.
   * @return T
   */
  <V> V getValue(Class<V> type);

  /**
   * Return parent node of this node in node tree.
   * @return JNode
   */
  JNode getParent();

  /**
   * Return printable (well formatted) text represents this node.
   * @param indent represent indentation for formatting text.
   * @param showPath if true then the method insert path information in output.
   * @param deep if true then the method will output children recursively.
   * @return String
   */
  String toString(String indent, boolean showPath, boolean deep);

  /**
   * Return this node as a JSON document.
   * @return String
   */
  String toJson();

  /**
   * Returns iterator over elements of composite node.
   * @see Iterable
   * @return Iterator&lt;JNode&gt; iterator
   */
  @Override
  Iterator<JNode> iterator();

  /**
   * Returns set of all children of composite (Object or Array) node.
   * @return List&lt;JNode&gt;
   */
  List<JNode> getChildren();

  /**
   * Return copy of this object, detached from node tree.
   * @return JNode
   */
  JNode clone();

}
