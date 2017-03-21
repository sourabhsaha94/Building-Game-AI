
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class GridTest extends PApplet {
	
	float gridMatrix[][];
	
	PImage bg;
	int red,green,blue;
	
	FileReader finput;
	BufferedReader bufferedReader;
	
	int grid_width = 80;
	String currentLine;
	String matrixArray[];
	
	Graph graph;
	
	public void settings(){
		size(800,800);
	}
	
	public void setup(){
		background(200);
		

		gridMatrix = new float[Math.floorDiv(height, grid_width)][Math.floorDiv(width, grid_width)];
		
		
		try {
			this.finput = new FileReader("gridMatrix.txt");
			this.bufferedReader = new BufferedReader(finput);
			int j=0;
			
			while((currentLine=this.bufferedReader.readLine())!=null){
				matrixArray = currentLine.split(" ");
				for(int i=0;i<matrixArray.length;i++){
					gridMatrix[i][j]= Float.parseFloat(matrixArray[i]);
				}
				j++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		createGraph();
		
	}
	
	public void createGraph(){
		
		ArrayList<Vertex> vList = new ArrayList<Vertex>();
		
		for(int i=0;i<Math.floorDiv(height, grid_width);i++){
			for(int j=0;j<Math.floorDiv(height, grid_width);j++){
				
				if( gridMatrix[i][j]==0){
					Vertex v = new Vertex(i*grid_width+grid_width/2,j*grid_width+grid_width/2);
					vList.add(v);
				}
			}
		}
		
		
	}
	
	public void draw(){
		
		noStroke();
		
		for(int i=0;i<Math.floorDiv(height, grid_width);i++){
			for(int j=0;j<Math.floorDiv(height, grid_width);j++){
				
				if( gridMatrix[i][j]==0){
					ellipseMode(CENTER);  // Set ellipseMode to CENTER
					fill(100);  // Set fill to gray
					ellipse(i*grid_width+grid_width/2, j*grid_width+grid_width/2, 5, 5);  // Draw gray ellipse using CENTER mode
				}
				else{
					
					if(gridMatrix[i][j]==1){//wall red
						red=255;
						green=0;
						blue=0;
					}
					else if(gridMatrix[i][j]==2){//obstacle green
						red=0;
						green=255;
						blue=0;
					}
					else if(gridMatrix[i][j]==3){//obstacle blue
						red=0;
						green=0;
						blue=255;
					}
					fill(red,green,blue);
					rect(i*grid_width,j*grid_width,grid_width,grid_width);
				}
			}
		}
		
	}
	
	public void mousePressed(){
		
		gridMatrix[Math.floorDiv(mouseX, grid_width)][Math.floorDiv(mouseY, grid_width)]++;
	}
	
	public static void main(String args[]){
		PApplet.main("GridTest");
	}
}
