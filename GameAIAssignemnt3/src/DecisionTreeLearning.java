import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class Data{
	String sameRoom;
	String distance;
	String wander;
	Data(String _s1,String _s2,String _s3){
		sameRoom = _s1;
		distance = _s2;
		wander = _s3;
	}
}


public class DecisionTreeLearning {

	FileReader fileReader;
	BufferedReader bufferedReader;

	ArrayList<Data> dataset;
	HashMap<String,String[]> attributes;
	ArrayList<String> attr_list;
	ArrayList<String> actions;

	DecisionTreeLearning() throws FileNotFoundException {
		fileReader = new FileReader("dataset.txt");
		bufferedReader = new BufferedReader(fileReader);
		dataset = new ArrayList<>();
		attributes = new HashMap<>();
		attr_list = new ArrayList<>();
		actions = new ArrayList<>();
	}

	public static void main(String args[]){
		String line = "";
		String[] contents;

		try {
			DecisionTreeLearning treeLearning = new DecisionTreeLearning();

			while((line = treeLearning.bufferedReader.readLine())!=null){
				contents = line.split(",");
				treeLearning.dataset.add(new Data(contents[0],contents[1],contents[2]));
			}

			treeLearning.buildAttributeTable();

			treeLearning.learnTree(treeLearning.dataset,treeLearning.attributes);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void buildAttributeTable(){
		attributes.put("sameRoom", new String[]{"true","false"});
		attributes.put("distance", new String[]{"near","far"});
		attr_list.add("sameRoom");
		attr_list.add("distance");
		/*fill action values*/
		actions.add("false");	//false means follow character with increased speed
		actions.add("true");	//true means wander
	}


	void learnTree(ArrayList<Data> dataset, HashMap<String,String[]> attributes){

		double max=-1,infoGain=0;
		String root = "",attribute="";

		//HashMap<String,ArrayList<Data>> splitSet = new HashMap<>();

		for(int i=0;i<attr_list.size();i++)
		{	
			attribute = attr_list.get(i);
			
			infoGain = calculateInformationGain(attribute,dataset);
			if(max <= infoGain){
				max = infoGain;
				root = attribute;
			}
		}
		
		System.out.println("Root is "+root);
		
		splitByRoot(root, dataset, attributes);


	}

	/*Attribute set description
	 * 0 - sameRoom -> 0 true 1 false
	 * 1 - distance -> 0 near 1 far
	 * */
	
	void splitByRoot(String root, ArrayList<Data> dataset, HashMap<String,String[]> attributes){
		
		for(int i=0;i<attributes.get(root).length;i++){

			if(calculateEntropyGivenValue(root,attributes.get(root)[i],dataset)==(double)0){
				System.out.println("child of "+root+" given "+attributes.get(root)[i]+" is: "+getActionFromDataSet(root, attributes.get(root)[i], dataset));
			}else{
				attr_list.remove(root);
				if(attr_list.size()!=0){
					System.out.println(attributes.get(root)[i]+ " continue split");
					learnTree(filterDataSet(root,attributes.get(root)[i],dataset),attributes);
				}else{
					System.out.println("child of "+root+" given "+attributes.get(root)[i]+" is: "+getActionFromDataSet(root, attributes.get(root)[i], filterDataSet(root,attributes.get(root)[i],dataset)));
				}
				
			}
		}

		
	}
	
	String getActionFromDataSet(String attribute, String value, ArrayList<Data> dataset){
		
		String action="";
		
		switch(attribute){
		case "sameRoom":
			for(int i=0;i<dataset.size();i++)
			if(dataset.get(i).sameRoom.equalsIgnoreCase(value)){
				action = dataset.get(i).wander;
			}
			break;
		case "distance":
			for(int i=0;i<dataset.size();i++)
			if(dataset.get(i).distance.equalsIgnoreCase(value)){
				action = dataset.get(i).wander;
			}
			break;
		}
		
		return action;
	}
	double calculateEntropyOfDataSet(ArrayList<Data> dataset){

		double count=0;
		double[] probability = new double[actions.size()];
		double entropy=0;
		for(int j=0;j<actions.size();j++){
			for(int i=0;i<dataset.size();i++){
				if(actions.get(j).equalsIgnoreCase(dataset.get(i).wander)){
					count++;
				}
			}
			probability[j] = count/(dataset.size());

			
			entropy -= (probability[j]*(Math.log10(probability[j])/Math.log10(2.)));
			
			count=0;
		}
		
		return entropy;
	}
	double calculateEntropyGivenAttribute(String attr_name,ArrayList<Data> dataset){

		double entropy=0;

		for(int i=0;i<attributes.get(attr_name).length;i++){

			entropy += (calculateProbability(attr_name,attributes.get(attr_name)[i],dataset)*calculateEntropyGivenValue(attr_name,attributes.get(attr_name)[i],dataset));
		}

		return entropy;
	}
	double calculateEntropyGivenValue(String attr_name, String value, ArrayList<Data> _dataset){

		double count=0;
		double[] probability = new double[actions.size()];

		double entropy=0;

		ArrayList<Data> dataset = filterDataSet(attr_name,value,_dataset);

		for(int j=0;j<actions.size();j++){

			for(int i=0;i<dataset.size();i++){
				if(actions.get(j).equalsIgnoreCase(dataset.get(i).wander)){
					count++;
				}
			}

			probability[j] = count/(dataset.size());

			if(probability[j]>0.0)
			entropy -= (probability[j]*(Math.log10(probability[j])/Math.log10(2.)));
			
			count=0;
		}
		return entropy;

	}

	ArrayList<Data> filterDataSet(String attr_name, String value, ArrayList<Data> dataset){

		ArrayList<Data> filteredSet = new ArrayList<>();

		for(int i=0;i<dataset.size();i++){
			switch(attr_name){
			case "sameRoom":
				if(dataset.get(i).sameRoom.equalsIgnoreCase(value)){
					filteredSet.add(dataset.get(i));
				}
				break;
			case "distance":
				if(dataset.get(i).distance.equalsIgnoreCase(value)){
					filteredSet.add(dataset.get(i));
				}
				break;
			}
		}



		return filteredSet;
	}

	double calculateProbability(String attr_name, String value, ArrayList<Data> dataset){

		double count =0;

		switch(attr_name){
		case "sameRoom":
			for(int j=0;j<dataset.size();j++){
				if(dataset.get(j).sameRoom.equalsIgnoreCase(value)){
					count++;
				}
			}
			break;
		case "distance":
			for(int j=0;j<dataset.size();j++){
				if(dataset.get(j).distance.equalsIgnoreCase(value)){
					count++;
				}
			}
			break;
		}

		return count/dataset.size();
	}

	double calculateInformationGain(String attr_name,ArrayList<Data> dataset){
		return calculateEntropyOfDataSet(dataset) - calculateEntropyGivenAttribute(attr_name,dataset);
	}
}
