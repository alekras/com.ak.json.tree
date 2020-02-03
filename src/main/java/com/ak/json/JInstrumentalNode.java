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

import java.util.Set;

/**
 * Interface JInstrumentalNode represents operation are common for all types of node of tree.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public interface JInstrumentalNode extends JNode {
  /**
   * Return key of JNode (JObjectNode or JArrayNode).
   * @return String
   */
  String getKey();

  /**
   * Set key for JNode.
   * @param pKey the key to set
   * @return JNode
   */
  JNode setKey(String pKey);

  /**
   * Returns root node of the tree.
   * @return JNode
   */
  JNode getDocRoot();

  /**
   * Returns set of field names.
   * @return Set&lt;String&gt;
   */
  Set<String> fieldNames();

    /**
   * Return path of this node.
   * @return String []
   */
  String [] getPath();

}
