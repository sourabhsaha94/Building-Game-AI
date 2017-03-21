import java.util.ArrayList;

public class Vertex {
	
	float x;
	float y;

	
	ArrayList <Edge> Elist;	//this stores number of outgoing and incoming edges
	ArrayList<Vertex> Path; //this stores the vertices in previous iterations


	public Vertex(float x,float y){	//initialize vertex with own label and list of edges
		this.x = x;
		this.y = y;
		this.Elist = new ArrayList<Edge>();		
		this.Path = new ArrayList<Vertex>();
		this.Path.add(this);	//add the current node to its own path
	}

	public void addEdge(Edge e){	//add an edge to the list of the current vertex
		if(this.Elist.contains(e))
			return;
		else
			this.Elist.add(e);
	}

	public boolean containsEdge(Edge e){	//check if the list contains a particular edge
		return this.Elist.contains(e);
	}

	public ArrayList<Edge> getElist(){	//return all the edges associated with the vertex
		return Elist;
	}

	public int getEdgeCount(){	//return the total count of the edges
		return Elist.size();
	}

	public ArrayList<Vertex> getAdjacentVertexList(){	//get the list of vertices on the other end of all the connected edges

		ArrayList<Vertex> v = new ArrayList<Vertex>();

		for(Edge e:Elist){
			v.add(e.getAdjacentVertex(this));
		}

		return v;
	}

}
