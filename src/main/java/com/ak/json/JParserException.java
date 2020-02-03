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
 * JParser can throw JParserException during processing.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class JParserException extends Exception {

  /** */
  private static final long serialVersionUID = -898932151669161747L;

  /**
   * Default constructor.
   */
  public JParserException() {
  }

  /**
   * Constructor.
   * @param message description of the exception.
   */
  public JParserException(final String message) {
    super(message);
  }

  /**
   * @param cause underlying exception.
   */
  public JParserException(final Throwable cause) {
    super(cause);
  }

  /**
   * @param message description of the exception.
   * @param cause underlying exception.
   */
  public JParserException(final String message, final Throwable cause) {
    super(message, cause);
  }

 }
