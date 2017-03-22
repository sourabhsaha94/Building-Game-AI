package Part1;

import java.util.Comparator;
import java.util.LinkedList;

public class VertexComparator implements Comparator<LinkedList<Vertex>> {

    @Override
    public int compare(LinkedList<Vertex> o1, LinkedList<Vertex> o2) {
	return o1.peek().priority.compareTo(o2.peek().priority);
    }	
}
