
public class Timeline implements Runnable{

	long anchor;
	long tic_size;
	long origin;

	private static Timeline time;
	
	private Timeline(){
		this.tic_size = 10;
		this.origin=System.currentTimeMillis();
	}
	
	public static Timeline getInstance(){
		if(time==null)
			time = new Timeline();
		
		return time;
	}
	
	@Override
	public void run() {
		while(true){
			anchor = System.currentTimeMillis();
		}
		
	}
	
	public boolean rightTime(){
		if((anchor-origin)>tic_size){
			origin=anchor;
			return true;
		}
		else return false;
	}
	
	public long getTimeDifference(){
		return anchor-origin;
	}

}
