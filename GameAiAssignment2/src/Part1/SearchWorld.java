package Part1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SearchWorld {

	public static void main(String args[]) throws IOException{

		ArrayList<Vertex> vList = new ArrayList<Vertex>();

		FileReader finput,finput2;
		BufferedReader bufferedReader,bufferedReader2;

		String line,splitLine[];

		Random rand = new Random();

		try {
			finput = new FileReader("BigCityData.txt");
			bufferedReader = new BufferedReader(finput);

			while((line = bufferedReader.readLine())!=null){
				splitLine = line.split(",");
				Vertex v = new Vertex(splitLine[0]);
				v.lat = Double.valueOf(splitLine[1]);
				v.lon = Double.valueOf(splitLine[2]);
				vList.add(v);                    
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Graph g = new Graph(vList);

		System.out.println("Creating Graph..");

		try {
			finput2 = new FileReader("WorldRoads.txt");
			bufferedReader2 = new BufferedReader(finput2);

			while((line = bufferedReader2.readLine())!=null){
				splitLine = line.split(",");
				g.addEdge(splitLine[0], splitLine[1], Integer.parseInt(splitLine[2]));                    
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String start="Dubai";
		String goal="Vatican City";

		String algorithm = "astar";

		switch(algorithm){
		case "astar":	System.out.println("Excecuting Astar");
		Astar astar = new Astar(g,start,goal);
		astar.executeAlgo();
		break;
		case "djikstra":System.out.println("Excecuting Djikstra");
		Djikstra djikstra = new Djikstra(g,start,goal);
		djikstra.executeAlgo();
		break;
		}
	}
}
