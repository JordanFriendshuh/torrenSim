


public class Edge implements Comparable <Edge>{
	private int upload;
	private ClientNode node;
	public Edge(ClientNode node){
		upload = 0;
		this.node = node;
	}
	public void setUpload(int in){upload = in;}
	public int getUpload(){return upload;}
	public ClientNode getNode(){return node;}
	@Override
	public int compareTo(Edge arg0) {
		return arg0.getUpload() - getUpload();
	}
}
