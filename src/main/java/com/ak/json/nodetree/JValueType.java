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

/**
 * JValueType enumeration represents value types of JValueNode.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public enum JValueType {
  /**
   * Value type is String class instance.
   */
  STRING,

  /**
   * Value type is Integer class instance.
   */
  INTEGER,

  /**
   * Value type is Long class instance.
   */
  LONG,

  /**
   * Value type is Double class instance.
   */
  DOUBLE,

  /**
   * Value type is Float class instance.
   */
  FLOAT,

  /**
   * Value type is Boolean class instance.
   */
  BOOLEAN,

  /**
   * Value type is Object class instance with value == null.
   */
  NULL
}
