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

package com.ak.json.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ak.json.JInstrumentalNode;
import com.ak.json.JNode;
import com.ak.json.nodetree.JNodeType;
import com.ak.json.nodetree.JValueNode;

/**
 * Class JPath for JSON is analog XML xpath framework.
 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
 *
 */
public final class JPath {
	/** For debug purpose. */
	private static final boolean DEBUG = false;
	/** Constant for path processing.*/
	private static Pattern pathPattern = Pattern.compile(
			"[/]?"
					+ "([^\\[\\]/]*"
					+ "(\\[[^\\]]*\\])?"
					+ ")"
			);
	/** Constant for path processing.*/
	private static Pattern stepPattern = Pattern.compile(
			"^(([^\\[\\]\\:]*)\\:\\:)?"
					+ "([^\\[\\]\\:]*)?"
					+ "("
					+ "\\[((not)\\()?"
					+ "([^!=<>\\(\\)]+)"
					+ "(=|!=|>|<|>=|<=| eq | ne | lt | le | gt | ge )"
					+ "([^!=<>\\(\\)]+)(\\))?\\]"
					+ ")?$"
			);
	/** Test for integer numbers. */
	private static String integerPattern = "^[\\-\\+]?\\d+$";
	/** Test for numbers. */
	private static String numberPattern = "^[\\-\\+]?\\d*\\.?\\d+([eE][-+]?\\d+)?$";

	/** Constructor avoids instantiation. */
	private JPath() {
	}

	/**
	 * Apply jpath to json document.
	 * @param pPath path to apply.
	 * @param doc target json document.
	 * @return List&lt;JNode&gt;
	 */
	public static List<JNode> jpath(final String pPath, final JNode doc) {
		List<JNode> selectedNodes = new ArrayList<JNode>();
		jpath(selectedNodes, pPath, doc, null);
		return selectedNodes;
	}

	/**
	 *
	 * @param selectedNodes nodes chosen from doc.
	 * @param pPath path
	 * @param doc source doc
	 * @param var variables
	 */
	protected static void jpath(final List<JNode> selectedNodes, final String pPath, final JNode doc, final JNode var) {
		jpath(selectedNodes, null, new Path(pPath), doc, var);
	}

	/**
	 *
	 * @param selectedNodes nodes chosen from doc
	 * @param pred predicate of previous path segment
	 * @param path path
	 * @param currentNode source doc
	 * @param var variables
	 */
	private static void jpath(final List<JNode> selectedNodes, final Predicate pred, final Path path, final JNode currentNode, final JNode var) {
		if (predicateCheck(selectedNodes, pred, currentNode, var)) {
			jpath(selectedNodes, path, currentNode, var);
		}
	}

	/**
	 *
	 * @param selectedNodes nodes chosen from doc.
	 * @param path path
	 * @param currentNode source doc
	 * @param var variables
	 */
	private static void jpath(final List<JNode> selectedNodes, final Path path, final JNode currentNode, final JNode var) {
		if (DEBUG) {
			System.out.println(">>> jpath( '" + path + "' )" + "\n   curr node=" + currentNode.toJson() + "\n   var=" + var);
		}

		if (path.steps.length > 0) {
			Path nextPath = path.shift();
			Step step = path.steps[0];
			String segment = step.segment;
// Axis processing:
			List<JNode> axisNodes = getAxisNodes(step, currentNode);
// Segment processing:
			switch (segment) {
			case "." :
				for (JNode axisNode : axisNodes) {
					jpath(selectedNodes, step.pred, nextPath, axisNode, var);
				}
				break;

			case ".." :
				JNode parent = currentNode.getParent();
				jpath(selectedNodes, step.pred, nextPath, parent, var);
				break;

			case "*" :
				for (JNode nd : currentNode) {
					jpath(selectedNodes, step.pred, nextPath, nd, var);
				}
				break;

			case "" : // looks like we catch '//'
				Step nextStep = path.steps[1];
				for (JNode nd : searchAllDescendant(nextStep.segment, currentNode)) {
					jpath(selectedNodes, nextStep.pred, nextPath.shift(), nd, var);
				}
				break;

			default :
				for (JNode axisNode : axisNodes) {
					int idx = 0;
					switch (axisNode.getType()) {
					case OBJECT :  // segment is an node name or int index in object
						JNode node = null;
						if (segment.matches(integerPattern)) {
							idx = Integer.parseInt(segment);
							node = axisNode.getChildren().get(idx);
						} else {
							node = axisNode.getNode(segment);
						}
						if (node != null) {
							jpath(selectedNodes, step.pred, nextPath, node, var);
						}
						break;

					case ARRAY :  // segment is an index of array
						try {
							idx = Integer.parseInt(segment);
							JNode jnode = axisNode.getNode(idx);
							if (jnode != null) {
								jpath(selectedNodes, step.pred, nextPath, jnode, var);
							}
						} catch (NumberFormatException nfe) {
							// wrong path !!!
						}
						break;
						//$CASES-OMITTED$
					default:
					}
				}
			}
		} else {
			selectedNodes.add(currentNode);  // end of path
		}

		if (DEBUG) {
			for (JNode jnode : selectedNodes) {
				System.out.println("     selected node: " + jnode.toJson());
			}
			System.out.println("<<< jpath( '" + path + "' )\n");
		}
		return;
	}

	/**
	 * Generates list of axis nodes for current node.
	 * @param step current step
	 * @param currentNode node
	 * @return list of JNode
	 */
	private static List<JNode> getAxisNodes(final Step step, final JNode currentNode) {
		List<JNode> axisNodes =  new ArrayList<JNode>();
		switch (step.axis) {
		case "ancestor-or-self" :
			axisNodes.add(currentNode);
			//$FALL-THROUGH$
		case "ancestor" :
			JNode ancestor = currentNode.getParent();
			while (ancestor != null) {
				axisNodes.add(ancestor);
				ancestor = ancestor.getParent();
			}
			break;

		case "descendant-or-self" :
			axisNodes.add(currentNode);
			//$FALL-THROUGH$
		case "descendant" :
			axisNodes.addAll(getAllDescendant(currentNode));
			break;

		case "child" :
			if (currentNode.getType() == JNodeType.OBJECT || currentNode.getType() == JNodeType.ARRAY) {
				for (JNode nd : currentNode) {
					axisNodes.add(nd);
				}
			}
			break;

		case "parent" :
			axisNodes.add(currentNode.getParent());
			break;

		case "" :
		case "self" :
			axisNodes.add(currentNode);
			break;

		case "preceding-sibling" :
			for (JNode nd : currentNode.getParent()) {
				if (nd == currentNode) {
					break;
				}
				axisNodes.add(nd);
			}
			break;

		case "following-sibling" :
			boolean skip = true;
			for (JNode nd : currentNode.getParent()) {
				if (!skip) {
					axisNodes.add(nd);
				}
				if (nd == currentNode) {
					skip = false;
				}
			}
			break;
		default:
			break;
		}

		if (DEBUG) {
			for (JNode jnode : axisNodes) {
				System.out.println(" #    axis node: " + jnode.toJson());
			}
		}
		return axisNodes;
	}

	/**
	 *
	 * @param node name of searching node.
	 * @param currentNode source doc
	 * @return List<JNode>
	 */
	private static List<JNode> searchAllDescendant(final String node, final JNode currentNode) {
		List<JNode> foundNodes = new ArrayList<JNode>();
		search(foundNodes, node, currentNode);
		return foundNodes;
	}

	/**
	 *
	 * @param foundNodes nodes that matched search criteria.
	 * @param node name of searching node.
	 * @param currentNode source doc
	 */
	private static void search(final List<JNode> foundNodes, final String node, final JNode currentNode) {
		switch (currentNode.getType()) {
		case OBJECT:
			JNode testNode = currentNode.getNode(node);
			if (testNode != null) {
				foundNodes.add(testNode);
			}
			//$FALL-THROUGH$
		case ARRAY:
			for (JNode nd : currentNode) {
				search(foundNodes, node, nd);
			}
			return;
		case VALUE:
		default:
			return;
		}
	}

	/**
	 *
	 * @param currentNode node to process
	 * @return List<JNode>
	 */
	private static List<JNode> getAllDescendant(final JNode currentNode) {
		List<JNode> foundNodes = new ArrayList<JNode>();
		getDescendants(foundNodes, currentNode);
		return foundNodes;
	}

	/**
	 *
	 * @param foundNodes nodes accumulator
	 * @param currentNode node to process
	 */
	private static void getDescendants(final List<JNode> foundNodes, final JNode currentNode) {
		if (currentNode.getType() == JNodeType.OBJECT || currentNode.getType() == JNodeType.ARRAY) {
			for (JNode nd : currentNode) {
				foundNodes.add(nd);
				getDescendants(foundNodes, nd);
			}
		}
		return;
	}

	/**
	 * Check predicate.
	 * @param selectedNodes node selected in previous processing.
	 * @param pred predicate
	 * @param currentNode current processing node
	 * @param var variables
	 * @return boolean
	 */
	private static boolean predicateCheck(final List<JNode> selectedNodes, final Predicate pred, final JNode currentNode, final JNode var) {
		if (DEBUG) {
			System.out.println(">>> predicateCheck(" + pred + ",\n" + currentNode.toJson() + ",\n" + var + ")");
			for (JNode jnode : selectedNodes) {
				System.out.println("     precedingNodes: " + jnode.toString());
			}
		}
		if (pred == null) {
			return true;
		}
		List<JNode> operandNodesL = getNodesFromOperand(pred.left, currentNode, var);
		List<JNode> operandNodesR = getNodesFromOperand(pred.right, currentNode, var);
//    if (DEBUG) System.out.println(" Compare " + operandNodeL + " with " + operandNodeR + " = " + operandNodeL.equals(operandNodeR));
		boolean r = applyFun(pred.fun, compareNodes(operandNodesL, operandNodesR, pred.oper));
		if (DEBUG) {
			System.out.println("<<< predicateCheck returns " + r);
		}
		return r;
	}

	/**
	 * Compare two node lists.
	 * @param firstList first argument
	 * @param secondList second argument
	 * @param operation compare operation
	 * @return boolean
	 */
	private static boolean compareNodes(final List<JNode> firstList, final List<JNode> secondList, final String operation) {
		if (operation.equalsIgnoreCase("eq")
				|| operation.equalsIgnoreCase("ne")
				|| operation.equalsIgnoreCase("lt")
				|| operation.equalsIgnoreCase("le")
				|| operation.equalsIgnoreCase("gt")
				|| operation.equalsIgnoreCase("ge")
				) {
			if (firstList.size() == 1 && secondList.size() == 1) {
				JValueNode<?> node1 = (JValueNode<?>) firstList.get(0);
				JValueNode<?> node2 = (JValueNode<?>) secondList.get(0);
				Object val1 = node1.getValue();
				Object val2 = node2.getValue();
				int comparison = 0;
				if (val1 instanceof String && val2 instanceof String) {
					comparison = ((String) val1).compareTo((String) val2);
				} else if (val1 instanceof Number && val2 instanceof Number) {
//          if (val1 instanceof Integer && val2 instanceof Integer) {
//            comparison = ((Integer) val1).compareTo((Integer) val2);
//          } else if ((val1 instanceof Float || val1 instanceof Double) && (val2 instanceof Float || val2 instanceof Double)) {
//            comparison = ((Double) val1).compareTo((Double) val2);
//          } else if (val1 instanceof Integer && (val2 instanceof Float || val2 instanceof Double)) {
//            comparison = new Double(((Integer) val1).doubleValue()).compareTo((Double) val2);
//          } else if ((val1 instanceof Float || val1 instanceof Double) && val2 instanceof Integer) {
//            comparison = ((Double) val1).compareTo(new Double(((Integer) val2).doubleValue()));
//          }
					comparison = convert2Double(val1).compareTo(convert2Double(val2));
				} else {
					// TODO throw exception
				}
				switch (operation) {
				case "eq" :
					return val1.equals(val2);
				case "ne" :
					return !val1.equals(val2);
				case "lt" :
					return comparison < 0;
				case "le" :
					return comparison <= 0;
				case "gt" :
					return comparison > 0;
				case "ge" :
					return comparison >= 0;
				default:
					// TODO throw exception
					break;
				}
			} else {
				// TODO throw exception
			}
		} else {
			for (JNode f : firstList) {
				for (JNode s : secondList) {
					switch (operation) {
					case "=" :
						if (f.equals(s)) {
							return true;
						}
						break;
					case "!=" :
						if (!f.equals(s)) {
							return true;
						}
						break;
					case ">"  :
					case "<"  :
					case ">=" :
					case "<=" :
						break;
					default:
						// TODO throw exception
						break;
					}
				}
			}
		}
		return false;
	}

	private static Double convert2Double(final Object v) {
		if (v instanceof Integer) {
			return new Double(((Integer) v).doubleValue());
		} else if (v instanceof Float) {
			return new Double(((Float) v).doubleValue());
		} else if (v instanceof Double) {
			return (Double) v;
		} else {
			return new Double(((Number)v).doubleValue());
		}
	}
	/**
	 *
	 * @param fun function
	 * @param arg argument
	 * @return boolean
	 */
	private static boolean applyFun(final String fun, final boolean arg) {
		switch (fun) {
		case "not" : return !arg;
		default: return arg;
		}
	}

	/**
	 * Get nodes from operand.
	 * @param operand operand
	 * @param currentNode current document node.
	 * @param var variables
	 * @return List<JNode>
	 */
	private static List<JNode> getNodesFromOperand(final String operand, final JNode currentNode, final JNode var) {
		if (DEBUG) {
			System.out.println(">>> getNodesFromOperand(" + operand + ",\n" + currentNode.toJson() + ",\n" + var + ")");
		}

		List<JNode> nodes = new ArrayList<JNode>();
		if (operand.startsWith("$")) {                                                      // this is variable
			nodes.add(var.getNode(operand.substring(1)));
		} else if (operand.startsWith("\"") && operand.endsWith("\"")) {                    // this is a String value
			nodes.add(new JValueNode<String>(operand.substring(1, operand.length() - 1)));
		} else if (operand.matches(integerPattern)) {                                        // this is integer
			nodes.add(new JValueNode<Integer>(Integer.parseInt(operand)));
		} else if (operand.matches(numberPattern)) {                                        // this is number
			nodes.add(new JValueNode<Float>(Float.parseFloat(operand)));
		} else {                                                                            // this is path
			Path path = new Path(operand); // TODO: ...parsePathStep(splitPath(operand)[0])
			JNode startingNode = currentNode;
			if (path.isAbsolute) {
				startingNode = ((JInstrumentalNode) currentNode).getDocRoot();
			}
			jpath(nodes, path, startingNode, null);
		}
		if (DEBUG) {
			for (JNode jnode : nodes) {
				System.out.println("    operand node: " + jnode.toString());
			}
			System.out.println("<<< getNodesFromOperand(" + operand + ",\n" + currentNode.toJson() + ",\n" + var + ")");
		}
		return nodes;
	}

	/**
	 * Helper class Predicate. Defines predicate structure.
	 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
	 *
	 */
	static class Predicate {
		/** Predicate components: function, left operand, operation and right operand. */
		protected String fun, left, oper, right;

		/**
		 * Constructor.
		 * @param left left operand
		 * @param oper operation
		 * @param right right operand
		 */
		public Predicate(final String left, final String oper, final String right) {
			this(null, left, oper, right);
		}

		/**
		 * Constructor.
		 * @param fun function
		 * @param left left operand
		 * @param oper operation
		 * @param right right operand
		 */
		public Predicate(final String fun, final String left, final String oper, final String right) {
			this.fun = (fun == null) ? "" : fun.trim();
			this.left = (left == null) ? "" : left.trim();
			this.oper = (oper == null) ? "" : oper.trim();
			this.right = (right == null) ? "" : right.trim();
		}

		@Override
		public String toString() {
			return "[fun=" + fun + "; left=" + left + "; oper=" + oper + "; right=" + right + "]";
		}
	}

	/**
	 * Helper class Step. Defines step structure.
	 * @author krasnop@bellsouth.net (Alexei Krasnopolski)
	 *
	 */
	static class Step {
		/** Step components: axis and segment. */
		protected String axis, segment;
		/** Step component predicate. */
		protected Predicate pred;

		/** */
		static final int AXIS = 2;
		/** */
		static final int SEGMENT = 3;
		/** */
		static final int PREDICATE = 4;
		/** */
		static final int FUN = 6;
		/** */
		static final int LEFT_OPERAND = 7;
		/** */
		static final int OPERATION = 8;
		/** */
		static final int RIGHT_OPERAND = 9;
		/** */
		static final int CLOSE_FUN = 10;

		public Step(final String stepString) {
			Matcher m = stepPattern.matcher(stepString);

			if (m.find()) {
				if (m.group(PREDICATE) == null) {
					init(m.group(AXIS), m.group(SEGMENT), null);
					return;
				}
				if (m.group(FUN) == null) {
					init(m.group(AXIS), m.group(SEGMENT), new Predicate(m.group(LEFT_OPERAND), m.group(OPERATION), m.group(RIGHT_OPERAND)));
					return;
				}
				if (m.group(FUN) != null && m.group(CLOSE_FUN).trim().equals(")")) {
					init(m.group(AXIS), m.group(SEGMENT), new Predicate(m.group(FUN), m.group(LEFT_OPERAND), m.group(OPERATION), m.group(RIGHT_OPERAND)));
					return;
				}
				init(m.group(AXIS), m.group(SEGMENT), null);
				return;
			}
			init(null, null, null);
		}

		/**
		 * Initializer.
		 * @param axis axis
		 * @param segment segment
		 * @param pred predicate
		 */
		public void init(final String axis, final String segment, final Predicate pred) {
			this.axis = (axis == null) ? "" : axis.trim();
			this.segment = (segment == null) ? "" : segment.trim();
			this.pred = pred;
		}

		@Override
		public String toString() {
			return "{Step: axis=" + axis + "; segment=" + segment + "; predicate=" + pred + "}";
		}
	}

	static class Path {
		boolean isAbsolute;
		Step [] steps;

		public Path(final String pathString) {
			isAbsolute = pathString.startsWith("/");
			String [] stepsAsString = splitPath(pathString);
			steps = new Step[stepsAsString.length];
			for (int i = 0; i < stepsAsString.length; i++) {
				steps[i] = new Step(stepsAsString[i]);
			}
		}

		public Path(final Step [] steps) {
			isAbsolute = false;
			this.steps = steps;
		}

		/**
		 * Returns split path.
		 * @param path path
		 * @return String []
		 */
		private String [] splitPath(final String path) {
			Matcher m = pathPattern.matcher(path);
			List<String> result = new ArrayList<String>();
			while (m.find()) {
				result.add(m.group(1));
			}
			result.remove(result.size() - 1);
			return result.toArray(new String [] {});
		}

		public Path shift() {
			return new Path(Arrays.copyOfRange(steps, 1, steps.length));
		}

		public String [] getSegments() {
			String [] segments = new String [steps.length];
			int i = 0;
			for (Step step : steps) {
				segments[i++] = step.segment;
			}
			return segments;
		}

		@Override
		public String toString() {
			return Arrays.toString(steps);
		}
	}

}
