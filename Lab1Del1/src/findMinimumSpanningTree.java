
public class findMinimumSpanningTree {
	private Node root;
	
	/*
	 * Tanken är att denna klass agerar som det kanter går mellan; som jag tolkat specen har noder i sig ingen vikt.
	 * En array innehåller alla kanter som ansluter en nod till en annan, antalet är obegränsat.
	 */
	private class Node{
		String identifier;
		Edge[] edges;
		
		Node(String identifier){
			this.identifier = identifier;
			this.edges = null;
		}
	}
	
	/*
	 * Kanten mellan noder: Har en vikt och måste vara ansluten till två olika noder.
	 */
	private class Edge{
		Node Node1, Node2;
		int weight;
		
		Edge(){
			this.Node1 = null;
			this.Node2 = null;
			this.weight = -1;
		}
	}
	
	
	public findMinimumSpanningTree() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
