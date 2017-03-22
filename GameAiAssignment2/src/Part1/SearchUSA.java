package Part1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class SearchUSA {

    public static void main(String[] args) throws Exception{
		
	ArrayList<Vertex> Vlist = new ArrayList<Vertex>();	//create new list of vertex
	ArrayList<Vertex> Temp = new ArrayList<Vertex>();
		
	FileReader f = new FileReader("roads.txt");	//new reader for getting city names
	BufferedReader bufferedReader1 = new BufferedReader(f);
		
		
	String line;
	String[] splitString;
	boolean flag=true;	//to mark duplicate values
		
	while((line = bufferedReader1.readLine())!= null){	//to fill temp array with all city names including duplicates
	    splitString = line.split(",");
	    Temp.add(new Vertex(splitString[0]));
	    Temp.add(new Vertex(splitString[1]));
	}
		
	bufferedReader1.close();
		
	Vlist.add(Temp.get(0));
		
	for(Vertex v : Temp){	//to remove duplicate entries from temp into vlist
	    flag=true;
	    for(Vertex v1:Vlist){
		if(v.label.equalsIgnoreCase(v1.label)){
		    flag=true;
		    break;
		}else
		    {
			flag=false;
		    }
	    }
	    if(!flag){
		Vlist.add(v);
	    }
	}
		
		
	Graph g = new Graph(Vlist);
		
	FileReader f1 = new FileReader("roads.txt");	//new reader required for getting edges
		
	BufferedReader bufferedReader2 = new BufferedReader(f1);
		
	while((line = bufferedReader2.readLine())!= null){	//to fill temp array with all city names including duplicates
	    splitString = line.split(",");
	    g.addEdge(splitString[0], splitString[1], new Integer(splitString[2]));
	}
		
	bufferedReader2.close();
		
	FileReader f2 = new FileReader("cities.txt");	//new reader required for getting edges
		
	BufferedReader bufferedReader3 = new BufferedReader(f2);
		
	while((line = bufferedReader3.readLine())!= null){	//to fill temp array with all city names including duplicates
	    splitString = line.split(",");
	    for(Vertex v:g.Vlist){
		if(v.label.equalsIgnoreCase(splitString[0])){
		    v.lat = Double.parseDouble(splitString[1]);
		    v.lon = Double.parseDouble(splitString[2]);
		}
	    }
	}
		
	bufferedReader3.close();
		
	String start="atlanta";
	String goal="yuma";
		

	Astar astar = new Astar(g,start,goal);
	astar.executeAlgo();
		
    }
}

