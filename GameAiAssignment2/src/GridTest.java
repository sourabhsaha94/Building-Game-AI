
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.*;

class Character {

    PShape pointer;

    PVector position;
    float orientation;
    PVector velocity;
    double rotation;
    PVector acceleration;
    float angular_acceleration = 0;
    float max_velocity = (float) 0.5;
    float max_acceleration = (float) 0.5;

    int radius_of_satisfaction = 5, radius_of_deceleration = 100;
    double time_to_target = 0.25;

    double max_rotation = 0.1;
    double max_angular_acceleration = 0.1;

    public Character(int x, int y) {
        this.position = new PVector(x, y);
        this.velocity = new PVector((float) 0.1, (float) 0.1);
        orientation = 0;
        rotation = 0;
        this.acceleration = new PVector(0, 0);
    }
}

public class GridTest extends PApplet {

    float gridMatrix[][];

    Character person;
    boolean personDefined = false;

    int red, green, blue;

    FileReader finput;
    BufferedReader bufferedReader;

    int grid_width = 80;
    String currentLine;
    String matrixArray[];

    Graph graph;
    ArrayList<Vertex> vList = new ArrayList<Vertex>();
    ArrayList<Vertex> tempVList = new ArrayList<Vertex>();
    int i = 0;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        background(200);

        gridMatrix = new float[Math.floorDiv(height, grid_width)][Math.floorDiv(width, grid_width)];

        try {
            this.finput = new FileReader("gridMatrix.txt");
            this.bufferedReader = new BufferedReader(finput);
            int j = 0;

            while ((currentLine = this.bufferedReader.readLine()) != null) {
                matrixArray = currentLine.split(" ");
                for (int i = 0; i < matrixArray.length; i++) {
                    gridMatrix[i][j] = Float.parseFloat(matrixArray[i]);
                }
                j++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread timeline = new Thread(Timeline.getInstance());
        timeline.start();

        createGraph();

    }

    public void createGraph() {

        for (int i = 0; i < Math.floorDiv(height, grid_width); i++) {
            for (int j = 0; j < Math.floorDiv(height, grid_width); j++) {

                if (gridMatrix[i][j] == 0) {
                    Vertex v = new Vertex(i * grid_width + grid_width / 2, j * grid_width + grid_width / 2);
                    vList.add(v);
                }
            }
        }

        System.out.println(vList.size());

    }

    public void draw() {

        noStroke();

        for (int i = 0; i < Math.floorDiv(height, grid_width); i++) {
            for (int j = 0; j < Math.floorDiv(height, grid_width); j++) {

                if (gridMatrix[i][j] == 0) {
                    red = 255;
                    green = 255;
                    blue = 255;
                } else if (gridMatrix[i][j] == 1) {//wall red
                    red = 255;
                    green = 0;
                    blue = 0;
                } else if (gridMatrix[i][j] == 2) {//obstacle green
                    red = 0;
                    green = 255;
                    blue = 0;
                }
                fill(red, green, blue);
                rect(i * grid_width, j * grid_width, grid_width, grid_width);
            }
        }

        if (person != null) {
            pushMatrix();
            translate(person.position.x, person.position.y);
            rotate((float) (-(float) atan2(-person.velocity.y, person.velocity.x) + Math.PI / 2));
            scale((float) 0.5);
            shape(person.pointer);
            popMatrix();
        }

        if (Timeline.getInstance().rightTime()) {
            ellipseMode(CENTER);
            fill(200,10);
            if (i < vList.size()) {
                tempVList.add(vList.get(i));
                i++;
            }

        }
        
        for(Vertex v:tempVList){
            ellipseMode(CENTER);
            fill(200);
            ellipse(v.x,v.y,10,10);
        }

    }

    public void mousePressed() {

        if (!personDefined) {
            person = getNewBoid(mouseX, mouseY);
            personDefined = true;
        }

    }

    public static void main(String args[]) {
        PApplet.main("GridTest");
    }

    public Character getNewBoid(int x, int y) {

        Character c = new Character(x, y);

        PShape pointer = createShape(GROUP);
        PShape head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

        head.setFill(color(255, 0, 0));
        head.setStroke(false);
        PShape body = createShape(ARC, 0, 0, 36, 36, 0, PI);
        body.setStroke(false);
        body.setFill(color(255, 0, 0));

        // Add the two "child" shapes to the parent group
        pointer.addChild(body);
        pointer.addChild(head);

        c.pointer = pointer;
        return c;
    }
}
