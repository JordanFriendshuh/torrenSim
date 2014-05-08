import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainSim {

	/**
	 * @param args
	 */
	final static boolean debug = false;
	final static int size = 3000;
	final static int runTime = 2000;
	final static int exchangeRandom = 30;
	final static int exchangeBase = 20;
	final static double addRatio = .2;
	final static double freeLoadRatio = .25;
	final static double straightRemoveRatio = .1;
	final static double fullRemoveRatio = .8;
	final static double semiRemoveRatio = .1;
	final static int startingNodes = 25;
	static int nodeCount = 0;
	static List<List<Edge>> edges;
	static List<ClientNode> nodes;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Add a source node
		nodes = new ArrayList<ClientNode>();
		edges = new ArrayList<List<Edge>>();
		nodes.add(new ClientNode(nodeCount, size, 0, true, false));
		//Add empty nodes
		for(int i = 1; i < startingNodes; i++){
			nodeCount++;
			nodes.add(new ClientNode(nodeCount, size, 0, false, false));
		}
		for(int i = 0; i < nodes.size(); i++){
			edges.add(new ArrayList<Edge>());
			for(int j = 0; j < nodes.size(); j++){
				edges.get(i).add(new Edge(nodes.get(j)));
			}
		}
		int i = 0;
		System.out.println("Starting program");
		while(!done()){
			performSwap();
			/*try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			printDone(i);
			if(i > runTime){
				addNodes(i);
			}
			removeNodes();
			i++;
		}
	}
	private static boolean done(){
		for(ClientNode temp: nodes){
			if(!temp.marked()){
				return false;
			}
		}
		return true;
	}
	private static void performSwap(){
		for(ClientNode temp : nodes){
			temp.clearTop();
		}
		updateWeights();
		for(ClientNode temp : nodes){
			temp.swapData();
		}
		if(debug){
			System.out.print("Done Swapping");
			infoDump();
		}
	}
	private static void addNodes(int time){
		if(Math.random() < addRatio){
			ClientNode temp; 
			nodeCount++;
			System.out.print("Adding Node: " + nodeCount);
			if(Math.random() < freeLoadRatio){
				temp = new ClientNode(nodeCount, size, time, false, true);
				System.out.println("(Freeloader)");
			}else{
				temp = new ClientNode(nodeCount, size, time, false, false);
				System.out.println();
			}
			nodes.add(temp);
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
	private static void infoDump(){
		for(ClientNode temp : nodes){
			System.out.print("Node: " + temp.getID() + ", Amount Left " + temp.amountLeft()); 
			if(temp.freeLoader()){
				System.out.println("(FreeLoader)");
			}else{
				System.out.println();
			}
		}
	}
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
