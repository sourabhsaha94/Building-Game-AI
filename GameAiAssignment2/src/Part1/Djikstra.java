package Part1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

class Djikstra {

	Graph gh;

	String s, g;

	PriorityQueue<LinkedList<Vertex>> pq;
	
	ArrayList<String> Visited, Expanded; // expanded is open list, visited is
	// closed list

	Djikstra(Graph gh, String start, String goal) {
		this.gh = gh;
		this.s = start;
		this.g = goal;
		Comparator<LinkedList<Vertex>> comparator = new VertexComparator();
		pq = new PriorityQueue<LinkedList<Vertex>>(comparator);
		Visited = new ArrayList<String>();
		Expanded = new ArrayList<String>();
	}

	void executeAlgo() {

		ArrayList<Vertex> succList = new ArrayList<Vertex>();

		boolean minFlag; // true if there is value in queue lower than current
		// value

		pq.add(gh.getVertexFromLabel(this.s).Path); // add start state path to
		// priority queue

		Expanded.add(this.s); // add start state to list of expanded nodes

		while (!pq.isEmpty()) {

			LinkedList<Vertex> temp = pq.poll(); // get top path with minimum
			// cost from queue

			Vertex curr = temp.peek(); // get path head


			if(!itContains(curr.label,Visited))
				Visited.add(curr.label);	//add path head to visited array

			if(curr.label.equalsIgnoreCase(g))	//goal check
			{

				System.out.println();
				for (String s:Visited) {
					System.out.print(s+" ,");
				}
				System.out.println("");
				System.out.println("Expanded nodes:" +Visited.size());
				System.out.println("");
				System.out.println("goal reached(End->Start)");
				System.out.println("");
				int count=printPath(gh.getVertexFromLabel(g),0);
				System.out.println("");				
				System.out.println("Actual path nodes:" +count);				
				System.out.println("Actual distance from start to goal: " +curr.path_cost);	
				System.out.println("");									
				break;
			}

			else {

				minFlag = true;

				succList = curr.getAdjacentVertexList(); // generate successor
				// list for path
				// head
				for (Vertex v : succList) {

					minFlag=true;

					//Checks if path with same destination but more cost present in priority queue
					for(LinkedList<Vertex> l:pq){
						if(v.label.equalsIgnoreCase(l.peek().label)){
							if((curr.path_cost + v.getEdge(curr).weight)>l.peek().priority)
								minFlag=false;	
						}

					}


					// check if node has been visited by a path
					if (!itContains(v.label, Visited)) {

						if (minFlag) {

							v.Path.add(curr);

							v.path_cost = curr.path_cost + v.getEdge(curr).weight;

							// check if node has been previously expanded by
							// others
							if (!itContains(v.label, Expanded)) {

								Expanded.add(v.label);
							}

							v.priority = (double) v.path_cost;

							pq.add(v.Path);
						}
					}
				}
			}
		}
		System.out.println();
	}

	boolean itContains(String s, ArrayList<String> list) {

		if (list.contains(s))
			return true;

		return false;
	}

	int printPath(Vertex v, int count) {
		System.out.print(v.label + " ,");
		count = count + 1;
		if (!v.label.equalsIgnoreCase(s))
			count = printPath(v.Path.removeLast(), count);
		return count;
	}

}