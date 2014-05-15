import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainSim {

	/**
	 * @param args
	 */
	final static boolean debug = false;
	//Paramaters to use for testing
	final static int size = 1500;
	final static int runTime = 50;
	final static int exchangeRandom = 30;
	final static int exchangeBase = 20;
	final static double addRatio = .1;
	final static double freeLoadRatio = .2;
	final static double straightRemoveRatio = .0;
	final static double fullRemoveRatio = .8;
	final static double semiRemoveRatio = .1;
	final static int startingNodes = 10;
	final static int initFreeLoaders = 3;
	final static int fullNodes = 3;
	
	static int nodeCount = -1;
	static List<List<Edge>> edges;
	static List<ClientNode> nodes;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		nodes = new ArrayList<ClientNode>();
		edges = new ArrayList<List<Edge>>();
		//Source nodes
		for(int i = 0; i < fullNodes; i++){
			nodeCount++;
			nodes.add(new ClientNode(nodeCount, size, 0, true, false));
		}
		//Add empty nodes
		for(int i = 0; i < startingNodes; i++){
			nodeCount++;
			nodes.add(new ClientNode(nodeCount, size, 0, false, false));
		}
		//Add good for nothing freeloaders
		for(int i = 0; i < initFreeLoaders; i++){
			nodeCount++;
			nodes.add(new ClientNode(nodeCount, size, 0, false, true));
		}
		//Add edges for every single node in the graph, 2XN nodes  This is for different speeds for different node pairs
		for(int i = 0; i < nodes.size(); i++){
			edges.add(new ArrayList<Edge>());
			for(int j = 0; j < nodes.size(); j++){
				edges.get(i).add(new Edge(nodes.get(j)));
			}
		}
		int i = 0;
		System.out.println("Starting program");
		while(!done()){
			//Large swap data method
			performSwap();
			//Print if a new node 
			printDone(i);
			//If we are within the run time, still add nodes
			if(i < runTime){
				addNodes(i);
			}
			//Perform remove algorithm
			removeNodes();
			//info for debugging
			//infoDump(i);
			i++;
		}
	}
	//check if there are nodes that stil need data
	private static boolean done(){
		for(ClientNode temp: nodes){
			if(!temp.marked()){
				return false;
			}
		}
		return true;
	}
	//Swap the data of the nodes
	private static void performSwap(){
		//Clear the top4 lists to renew their top4
		for(ClientNode temp : nodes){
			temp.clearTop();
		}
		//reroll all the edge weights for the non freeloaders
		updateWeights();
		//Tell all to swap
		for(ClientNode temp : nodes){
			temp.swapData();
		}
		if(debug){
			System.out.print("Done Swapping");
			
		}
	}
	//add nodes to the system
	private static void addNodes(int time){
		//Percent chance to happen
		if(Math.random() < addRatio){
			ClientNode temp; 
			nodeCount++;
			System.out.print("Adding Node: " + nodeCount);
			//Chance to be a freeloader
			if(Math.random() < freeLoadRatio){
				temp = new ClientNode(nodeCount, size, time, false, true);
				System.out.println("(Freeloader)");
			}else{
				temp = new ClientNode(nodeCount, size, time, false, false);
				System.out.println();
			}
			nodes.add(temp);
			//Add his edges into the system
			for(List<Edge> tempEdge: edges){
				tempEdge.add(new Edge(temp));
			}
			ArrayList<Edge> tempList = new ArrayList<Edge>();
			for(ClientNode node : nodes){
				tempList.add(new Edge(node));
			}
			edges.add(tempList);
		}
	}
	
	private static void removeNodes(){
		//chance to happen, nodes that are done have a higher chance of leaving
		if(Math.random() < straightRemoveRatio){
			int index = (int)(Math.random() * nodes.size());
			ClientNode temp = nodes.get(index);
			if(temp.getID() != 0){
				if(temp.marked() && Math.random() < fullRemoveRatio || Math.random() < semiRemoveRatio){
					//remove nodes
					edges.remove(index);
					for(List<Edge> tempList : edges){
						tempList.remove(index);
					}
					System.out.println("Removing Node: " + nodes.get(index).getID());
					nodes.remove(index);
				}
			}
		}
	}
	//Print everyone's current data amount
	private static void infoDump(int time){
		System.out.println("Time: " + time);
		for(ClientNode temp : nodes){
			if(temp.amountLeft() != 0){
				System.out.print("Node: " + temp.getID() + ", Amount Left " + temp.amountLeft()); 
				if(temp.freeLoader()){
					System.out.println("(FreeLoader)");
				}else{
					System.out.println();
				}
			}
		}
	}
	//Check if a node is done, if it is print it and mark it
	private static void printDone(int time){
		for(ClientNode temp : nodes){
			if(temp.amountLeft() == 0 && !temp.marked()){
				System.out.print("Node: " + temp.getID() + " Done, Start: " + temp.timeStarted() + " End: " + time); 
				if(temp.freeLoader()){
					System.out.println(" (FreeLoader)");
				}else{
					System.out.println();
				}
				temp.mark();
			}
		}
	}
	//Set the weights for the edges
	private static void updateWeights(){
		for(int i = 0; i < nodes.size(); i++){
			if(nodes.get(i).freeLoader()){
				for(int j = 0; j < edges.size(); j++){
					assert(edges.get(j).get(i).getNode().equals(nodes.get(i)));
					edges.get(j).get(i).setUpload(0);
				}
			}else{
				for(int j = 0; j < edges.size(); j++){
					assert(edges.get(j).get(i).getNode().equals(nodes.get(i)));
					edges.get(j).get(i).setUpload((int)(Math.random() * exchangeRandom) + exchangeBase);
				}
			}
		}
		for(int i = 0; i < nodes.size(); i++){
			nodes.get(i).clearTop();
			nodes.get(i).setEdgeValue(edges.get(i));
		}
		for(int i = 0; i < nodes.size(); i++){
			nodes.get(i).swapData();
		}
	}

}
