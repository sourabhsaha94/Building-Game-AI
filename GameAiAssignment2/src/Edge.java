
public class Edge {
	Vertex one,two;	//store the ends of the edge

	public Edge(Vertex one,Vertex two){	//initialize the edge with the corresponding vertices
		this.one=one;
		this.two=two;
	}

	public Vertex getAdjacentVertex(Vertex v){	//return the other end of the edge for the input vertex
		return (v.equals(one))?two:one;
	}
}
