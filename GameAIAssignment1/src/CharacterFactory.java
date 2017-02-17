import processing.core.PApplet;
import processing.core.PShape;


public class CharacterFactory extends PApplet{
	
	PShape pointer;
	
	static CharacterFactory c;
	
	private CharacterFactory(){
		pointer = createShape(GROUP);
		PShape head = createShape(TRIANGLE, -18, 0, 0, -30, 18, 0);

		head.setFill(color(255,0,0));
		head.setStroke(false);
		PShape body = createShape(ARC, 0,0, 36, 36, 0, PI);
		body.setStroke(false);
		body.setFill(color(255,0,0));


		// Add the two "child" shapes to the parent group
		pointer.addChild(body);
		pointer.addChild(head);

	}
	
	public static CharacterFactory getInstance(){		//singleton pattern
		if(c==null){
			c = new CharacterFactory();
		}
		return c;
	}
	
	public Character getNewCharacter(int x,int y){		//factory pattern
		return new Character(pointer,x,y); 
	}
}
