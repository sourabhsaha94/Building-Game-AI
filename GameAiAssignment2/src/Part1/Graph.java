package Part1;
import java.util.ArrayList;

public class Graph {
	  ArrayList <Vertex> Vlist;	//list of all the vertices
	    ArrayList<Edge> Elist;	//list of all the edges
		
	    public Graph(ArrayList<Vertex> v){	//initialize the graph with the list of vertices given by the user
		this.Vlist = new ArrayList<Vertex>();
		this.Elist = new ArrayList<Edge>();
			
		for(Vertex vert:v){
		    this.Vlist.add(vert);
		}
	    }
		
	    public void addEdge(Vertex one, Vertex two,int weight){	//insert a particular edge into the edge list with vertex references
		Edge e = new Edge(one,two,weight);
		for(Edge x:Elist){
		    if(e.one.label.equalsIgnoreCase(x.one.label)&&e.two.label.equalsIgnoreCase(x.two.label))
			return;
		    else if(e.one.label.equalsIgnoreCase(x.two.label)&&e.two.label.equalsIgnoreCase(x.one.label))
			return;
		}
		this.Elist.add(e);
		one.addEdge(e);
		two.addEdge(e);
	    }
		
	    public void addEdge(String s1, String s2, int weight){	//insert call to the above method by passing only the labels as input
		addEdge(getVertexFromLabel(s1),getVertexFromLabel(s2),weight);
	    }
		
	    public Vertex getVertexFromLabel(String label){	//get vertex from the list of vertices matching input label
			
		for(Vertex v:this.Vlist){
		    if(v.label.equalsIgnoreCase(label))
			return v;
		}
		return null;
	    }
}

