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
//CSOFF: Magic
package com.ak.json.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ak.json.transform.JPath.Predicate;
import com.ak.json.transform.JPath.Step;

/**
 *
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public class Test {

  /**
   *
   */
  protected Test() { }

  /**
   * @param args optional parameters
   */
//  public static void main(String[] args) {
//    Pattern patt = Pattern.compile("^([^\\[\\]]*)?(\\[([^!=<>]+)(=|!=|>|<|>=|<=)([^!=<>]+)\\])?$");
//
//    for (String input : new String [] {"a/b/c", "a/b/c[author=$author]", "", "."}) {
//      Matcher m = patt.matcher(input);
//      while (m.find()) {
//        System.out.println("..segm= " + m.group(1));
//        System.out.println("  [...]= " + m.group(2));
//        System.out.println("  path= " + m.group(3));
//        System.out.println("  oper= " + m.group(4));
//        System.out.println("  var = " + m.group(5));
//      }
//      System.out.println("-------------------");
//    }
//  }
  public static void main(final String[] args) {
//    String patInteger = "^[\\-\\+]?\\d+$";
//    System.out.println("12 " + "12".matches(patInteger));
//    System.out.println("-19 " + "-19".matches(patInteger));
//    System.out.println("+18 " + "+18".matches(patInteger));
//    System.out.println("1.6 " + "1.6".matches(patInteger));
//    System.out.println("1e-9 " + "1e-9".matches(patInteger));
//    System.out.println("-1.6 " + "-1.6".matches(patInteger));
//    System.out.println("1.4e-9 " + "1.4e-9".matches(patInteger));
//    System.out.println("1.6 " + "1.6".matches(patInteger));
//    System.out.println("1E9 " + "1E9".matches(patInteger));
//    String patNumber = "^[\\-\\+]?\\d*\\.?\\d+([eE][-+]?+)?$";
//    System.out.println("12 " + "12".matches(patNumber));
//    System.out.println("-19 " + "-19".matches(patNumber));
//    System.out.println("+18 " + "+18".matches(patNumber));
//    System.out.println("1.6 " + "1.6".matches(patNumber));
//    System.out.println("1e-9 " + "1e-9".matches(patNumber));
//    System.out.println("-1.6 " + "-1.6".matches(patNumber));
//    System.out.println("1.4e-9 " + "1.4e-9".matches(patNumber));
//    System.out.println("1.6 " + "1.6".matches(patNumber));
//    System.out.println("1E9 " + "1E9".matches(patNumber));

    for (String input : new String [] {
        "a/b/c",
        "//a/b/c",
        "/a//b/c",
        "a[c/d > $e]/b/c[author=$author]",
        "a[$e > c/d]/b/c[$author!=author]",
        "/a/b/c",
        ".",
        "/a/*/",
        "a",
        "",
        "pr::a[c/d > $e]/b/c[pr::author=$author]",
        "pr::a[c/d > $e]/b/c[not(pr::author=$author)]",
        "parentNode/node[title eq \"Value\"]/title",
        "\"Value\"",
        "177"
      }) {
      System.out.println(" >>> input: " + input + " == ");
      int i = 1;
      for (String s : splitPath(input)) {
        Step step = splitPathStep(s);
        System.out.print(" (path-" + (i++) + ") " + s + " -> "
            + "{axis}:" + step.axis
            + ", {segment}:" + step.segment);
        if (step.pred != null) {
          System.out.print(
              ", {predicate}: [fun:" + step.pred.fun
            + ", leftOpd:" + step.pred.left
            + ", Oper:" + step.pred.oper
            + ", rightOpd:" + step.pred.right);
        }
        System.out.println();
      }
      System.out.println(" <<<\n");
    }
  }

  /**
   *
   * @param pathStep path string
   * @return Step
   */
  private static Step splitPathStep(final String pathStep) {
    Pattern patt = Pattern.compile("^(([^\\[\\]\\:]*)\\:\\:)?([^\\[\\]\\:]*)?(\\[((not)\\()?([^!=<>\\(\\)]+)(=|!=|>|<|>=|<=| eq | ne | lt | le | gt | ge )([^!=<>\\(\\)]+)(\\))?\\])?$");
    Matcher m = patt.matcher(pathStep);

    int axis = 2;
    int segment = 3;
    int predicate = 4;
    int fun = 6;
    int leftOperand = 7;
    int operation = 8;
    int rightOperand = 9;
    int closeFun = 10;
    if (m.find()) {
      System.out.print("  Groups[" + m.groupCount() + "] := ");
      for (int i = 1; i <= m.groupCount(); i++) {
        System.out.print(" | " + m.group(i));
      }
      System.out.println(" |");

//      if (m.group(predicate) == null) {
//        return new Step(m.group(axis), m.group(segment), null);
//      }
//      if (m.group(fun) == null) {
//        return new Step(m.group(axis), m.group(segment), new Predicate(m.group(leftOperand), m.group(operation), m.group(rightOperand)));
//      }
//      if (m.group(fun) != null && m.group(closeFun).trim().equals(")")) {
//        return new Step(m.group(axis), m.group(segment), new Predicate(m.group(fun), m.group(leftOperand), m.group(operation), m.group(rightOperand)));
//      }
//      return new Step(m.group(axis), m.group(segment), null);
    }
    System.out.print("not matched: " + pathStep);
    return null;
  }

  /**
   *
   * @param path string
   * @return String
   */
  private static String [] splitPath(final String path) {
    Pattern patt = Pattern.compile(
        "([/]?"
      + "[^\\[\\]/]*"
      + "(\\["
      + "[^\\]]*"
      + "\\])?"
      + ")"
    );
    Matcher m = patt.matcher(path);
    List<String> result = new ArrayList<String>();
    while (m.find()) {
      System.out.println(" --- " + m.group(1));
      result.add(m.group(1));
    }
    result.remove(result.size() - 1);
    return result.toArray(new String [] {});
  }

}
