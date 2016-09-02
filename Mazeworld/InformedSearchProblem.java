//autjhor Vivek Sah
package assignment_mazeworld;

import java.util.*;

public class InformedSearchProblem extends SearchProblem {


	public List<SearchNode> astarSearch() {
		resetStats();
		PriorityQueue<SearchNode> frontier = new PriorityQueue<SearchNode>();

		// map to store backchaining information
		HashMap<SearchNode, SearchNode> reachedFrom = new HashMap<SearchNode, SearchNode>();

		// startNode must be set by the constructor of the particular
		// search problem, since a UUSearchNode is an interface and can't
		// be instantiated directly by the search

		reachedFrom.put(startNode, null); // startNode was not reached from any
		// other node
		frontier.add(startNode); //we will ad the startNode tp frontier

		// current depth of the search; useful for debugging.
		int currentDepth = 0;

		while (!frontier.isEmpty()) {
			incrementNodeCount();
			updateMemory(frontier.size() + reachedFrom.size());
			SearchNode currentNode = frontier.remove(); //pop the highest priority element from priorityQueue
			if (reachedFrom.containsKey(currentNode) && reachedFrom.get(currentNode) != null){ //if this node is already visited
				if (currentNode.getCost() > reachedFrom.get(currentNode).getCost() + 1 ) {//if current node's cost is greater than the one in the hashmap
//					System.out.println("found duplicate, skipping");
					continue; //skip the rest
				}
			}
//			reachedFrom.put(, currentNode);
			if (currentNode.goalTest()) {
				return  backchain(currentNode, reachedFrom);
			}
			ArrayList<SearchNode> successors = currentNode.getSuccessors(); //get successors

			for (SearchNode node : successors) { //go thorugh the successors
				// if not visited
				if (!reachedFrom.containsKey(node) ) { //if not visited, add to the priorityQueue
					reachedFrom.put(node, currentNode);
					frontier.add(node);
				} else {
					if ( reachedFrom.get(node) != null && node.getCost() < reachedFrom.get(node).getCost()+1 ) { //if visited heck its cist
						reachedFrom.put(node, currentNode);
						frontier.add(node);
					}
				}

			}
		}
		return null;
	}

}
