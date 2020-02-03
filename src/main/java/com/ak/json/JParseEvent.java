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

/**
 * JParseEvent enumeration represents events that are happened during parsing of JSON document.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public enum JParseEvent {
  /**
   * Parser reader fires the event START_OBJECT when has meet '{' symbol in a parsing stream.
   */
  START_OBJECT,

  /**
   * Parser reader fires the event START_ARRAY when has meet '[' symbol in a parsing stream.
   */
  START_ARRAY,

  /**
   * Parser reader fires the event END_OBJECT when has meet '}' symbol in a parsing stream.
   */
  END_OBJECT,

  /**
   * Parser reader fires the event END_ARRAY when has meet ']' symbol in a parsing stream.
   */
  END_ARRAY,

  /**
   * Parser reader fires the event FIELD_NAME when has found field name (name of object property) in a parsing stream.
   */
  FIELD_NAME,

  /**
   * Parser reader fires the event VALUE when has found value after ':' in a parsing stream that is not OBJECT or ARRAY.
   */
  VALUE,

  /**
   * Parser reader fires the event VALUE_STRING when has found value after ':' in a parsing stream and this value is String.
   */
  VALUE_STRING,

  /**
   * Parser reader fires the event DELIMITER when has meet ',' symbol in a parsing stream.
   */
  DELIMITER
}
