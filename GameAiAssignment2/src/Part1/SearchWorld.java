package Part1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SearchWorld {

	public static void main(String args[]) throws IOException{

		ArrayList<Vertex> vList = new ArrayList<Vertex>();
		
		FileReader finput;
		BufferedReader bufferedReader;

		String line;

		Random rand = new Random();
		
		try {
			finput = new FileReader("BigCityData.txt");
			bufferedReader = new BufferedReader(finput);

			while((line = bufferedReader.readLine())!=null){
				Vertex v = new Vertex(line);
				v.lat = (double) (rand.nextInt(1000)+1);
				v.lon = (double) (rand.nextInt(1000)+1);
				vList.add(v);					
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Graph g = new Graph(vList);
	
		for(int i=0;i<15000;i++){
			g.addEdge(vList.get(rand.nextInt(vList.size())), vList.get(rand.nextInt(vList.size())), rand.nextInt(100));
		}
		
		String start="Rybnik";
		String goal="Opole";
			

		Astar astar = new Astar(g,start,goal);
		astar.executeAlgo();
	}
}
