import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ClientNode {
	private List<Integer> data;
	private List<Edge> edges;
	private int nodeValue;
	private int timer;
	private int startTime;
	boolean freeloader;
	boolean debug;
	boolean marked;
	List<Edge> top4;
	public ClientNode(int val, int size, int startTime, boolean source, boolean free){
		nodeValue = val;
		debug = false;
		marked = false;
		this.startTime = startTime;
		top4 = new ArrayList<Edge>();
		data = new ArrayList<Integer>();
		timer = 0;
		if(source){
			for(int i=0; i<size; i++){
				data.add(i);
			}
		}else{
			for(int i=0; i<size; i++){
				data.add(-1);
			}
		}
		freeloader = free;
	}
	public boolean freeLoader(){return freeloader;}
	public void clearTop(){top4.clear(); debug = false;}
	public int getID(){return nodeValue;}
	//Start the recursive algorithum through the graph to build everyone's top4
	public void swapData(){
		//Sort our edge list by highest upload
		List<Edge> clients = new ArrayList<Edge>(edges);
		Collections.sort(clients);
		for(int i = clients.size() - 1; i >= 0 && top4.size() < 4; i--){
			//pick a random node to add to our top4
			if(timer % 3 == 0 && top4.size() == 0 && getFileIndicesAlreadyDLed().size() != 0){
				Edge temp = clients.get((int)(Math.random() * clients.size()));
				int j = 0;
				while(temp.getNode().donateTo(getFileIndicesAlreadyDLed()) && j < clients.size() * 2){
					temp = clients.get((int)(Math.random() * clients.size()));
					j++;
				}
				top4.add(temp);
			}
			//Pick our top4 (the recursive part)
			if(clients.get(i).getNode().haveWhatIwant(getFileIndicesNeeded(), nodeValue)){
				top4.add(clients.get(i));
			}
		}
		if(debug){
			System.out.println("Top4 completed for: " + nodeValue);
			debug = false;
		}
		timer++;
		for(int i = 0; i < top4.size(); i++){
			Edge temp = top4.get(i);
			List <Integer> indexes = new ArrayList<Integer>();
			List <Integer> values = new ArrayList<Integer>();
			if(temp != null){
				temp.getNode().exchangeData(getFileIndicesNeeded(), indexes, values, temp.getUpload());
			}
			for(int j = 0; j < indexes.size(); j++){
				data.set(indexes.get(j), values.get(j));
			}
		}
	}
	//Give a list of what we need, returns the values we want in indexes and values
	private void exchangeData(List<Integer> requested, List<Integer> indexes, List<Integer> values, int upload){
		List <Integer> request = new ArrayList<Integer>(requested);
		request.retainAll(getFileIndicesAlreadyDLed());
		for(int i = 0; i < request.size() && i < upload; i++){
			indexes.add(request.get(i));
			values.add(data.get(i));
		}
	}
	private boolean haveWhatIwant(List<Integer> requests, int node){
		//Check if we have what they are looking for
		List<Integer> requested = new ArrayList<Integer>(getFileIndicesAlreadyDLed());
		requested.retainAll(requests);
		if(requested.size() == 0){
			return false;
		}
		//Copy origional edges list, because we are making changes
		List<Edge> clients = new ArrayList<Edge>(edges);
		Collections.sort(clients);
		//Go through this sorted array and see who has the highest bandwidth with things I want 
		for(int i = clients.size() - 1; i >= 0 && top4.size() < 4; i--){
			if(clients.get(i).getNode().haveWhatIwant(getFileIndicesNeeded(), nodeValue)){
				top4.add(clients.get(i));
			}
		}
		//If there are not 4 nodes that have anything we want, pick random ones that need data we have
		if(top4.size() < 4 && getFileIndicesAlreadyDLed().size() != 0){
			int i = 0;
			while(top4.size() < 4 && i < clients.size() * 2){
				Edge temp = clients.get((int)(Math.random() * top4.size()));
				if(!top4.contains(temp) && temp.getNode().donateTo(getFileIndicesAlreadyDLed())){
					top4.add(temp);
				}
				i++;
			}
		}
		if(debug){
			System.out.println("Top4 completed for: " + nodeValue);
			debug = false;
		}
		//Check if the node requesting to trade with us is in our top 4
		for(int i = 0; i < top4.size(); i++){
			if(top4.get(i).getNode().getID() == node){
				return true;
			}
		}
		return false;
	}
	//Check if the client needs what I have, used if there aren't 4 desired clients
	public boolean donateTo(List<Integer> offer){
		List<Integer> requested = new ArrayList<Integer>(getFileIndicesNeeded());
		requested.retainAll(offer);
		if(requested.size() == 0){
			return false;
		}else{
			return true;
		}
	}
	//update edge values
	public void setEdgeValue(List<Edge> edgesIn){
		edges = edgesIn;
	}
	//Return a list of all the indexes we have already downloaded
	private List<Integer> getFileIndicesAlreadyDLed() {
		List<Integer> indexes = new ArrayList<Integer>();
		for(int i = 0; i < data.size(); i++)
		{
			if(data.get(i) != -1){
				indexes.add(i);
			}
		}
		return indexes; 
	}
	//Return a list of all the indexes we need to download
	private List<Integer> getFileIndicesNeeded() {
		List<Integer> indexes = new ArrayList<Integer>();
		for(int i = 0; i < data.size(); i++)
		{
			if(data.get(i) == -1){
				indexes.add(i);
			}
		}
		return indexes;
	}
	public int timeStarted(){return startTime;}
	//Helper method to keep lop level cleaner
	public int amountLeft(){
		return getFileIndicesNeeded().size();
	}
	public boolean marked(){return marked;}
	public void mark(){marked = true;}
}
