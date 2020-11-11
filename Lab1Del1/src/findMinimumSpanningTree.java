import java.util.LinkedList;
import java.util.Random;

/**
 * 
 * @author Gabriella Hafdell, Hanna Eriksson, Philip Larsson
 *
 */

/*
 * Vi behöver kunna:
 * Generera en graf
 * Se till att allt är anslutet/Kolla hur allt är anslutet
 * Få en adjacency matrix i någon form
 * Få en adjacency list i någon form
 */
public class findMinimumSpanningTree {
	private Node root;
	private Node[] nodesGenerated;
	private Random r;
	
	/*
	 * Tanken är att denna klass agerar som det kanter går mellan; som jag tolkat specen har noder i sig ingen vikt.
	 * En array innehåller alla kanter som ansluter en nod till en annan, antalet är obegränsat.
	 */
	private class Node{
		String identifier;
		Edge[] edges;
		
		Node(String identifier, int maxEdges){
			this.identifier = identifier;
			edges = new Edge[maxEdges-1];
		}
		
		void addEdge(Edge e) {
			for(int i = 0; i < edges.length; i++) {
				if(edges[i] == null) {
					edges[i] = e;
					break;
				}
			}
		}
		
		public String toString() {
			return identifier;
		}
	}
	
	/*
	 * Kanten mellan noder: Har en vikt och måste vara ansluten till två olika noder.
	 */
	private class Edge{
		Node Node1, Node2;
		int weight;
		
		Edge(Node node1, Node node2){
			this.Node1 = node1;
			this.Node2 = node2;
			this.weight = -1;
		}
	}
	
	
	/*
	 * Skapar en identifierare för en nod.
	 */
	private class assignIdentifier {
		private int n;
		private String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"}; //+resten av alfabetet, senare
		
		assignIdentifier(){
			n = -1;
		}
		
		String getIdentifier() {
			n++;
			return letters[n];
		}
	}
	
	
	public findMinimumSpanningTree() {
		/*
		 * Konstruktorn bör generera en ny graf när den körs?
		 */
		
		//Generar en graf
		assignIdentifier assigner = new assignIdentifier();
		Random r = new Random(System.currentTimeMillis());
		
		int totalNodesToMake = 4 + r.nextInt(4); //ska vara 23
		
		nodesGenerated = new Node[totalNodesToMake];
		
		for(int i = 0; i < totalNodesToMake; i++) {
			nodesGenerated[i] = new Node(assigner.getIdentifier(), totalNodesToMake);
		}
		
		while(!graphIsConnected(nodesGenerated)) {
			Node node1;
			Node node2;
			while(true) {
				node1 = nodesGenerated[r.nextInt(nodesGenerated.length)];
				node2 = nodesGenerated[r.nextInt(nodesGenerated.length)];
				if(node1 != node2) {
					break;
				}
			}
			Edge e = new Edge(node1, node2);
			node1.addEdge(e);
			node2.addEdge(e);
			
		}
	}


	/*
	 * Ska returnera true när alla noder i grafen kan nås från alla andra noder.
	 */
	boolean graphIsConnected(Node[] nodes) {
		//variables
		Node[] markedNodes = new Node[nodes.length];
		LinkedList<Node> fifo = new LinkedList<Node>();
		
		
		//spaghetti begins here
		fifo.add(nodes[0]);
		
		while (!fifo.isEmpty()) {
			int markedIterator = 0;
			Node s = fifo.pop();
			for(int i = 0; i < markedNodes.length; i++) {
				if(s == markedNodes[i]) {
					break;
				}else {
					//System.out.println("List length: "+markedNodes.length+", markedIterator: "+markedIterator+", current value of i: "+i);
					markedNodes[markedIterator] = s;
					markedIterator++;
					for(int x = 0; x < s.edges.length; x++) {
						if(s.edges[x] == null) {
							continue;
						}
						Node nodeToAdd = s.edges[x].Node1 == s ? s.edges[x].Node2 : s.edges[x].Node1;
						fifo.add(nodeToAdd);
					}//for
				}//if
			}//for
			//System.out.println("amount of whiles");
		}//while
		
		//if an element is null, all nodes were not marked
		for(int i = 0; i < markedNodes.length; i++) {
			System.out.println(markedNodes[i].toString());
			if(markedNodes[i] == null) {
				
				return false;
			}
		}
		return true;
	}
	
//	private int findNextNull(Node[] list) {
//		for(int i = 0; i < list.length; i++) {
//			if(list[i] == null) {
//				return i;
//			}
//		}
//		
//	}
	
	/*
	 * Skapar en jämn matris för alla noder i grafen
	 */
//	int[][] generateAdjacencyMatrix() {
//		
//	}
	
	
	/*
	 * Skapar en ojämn lista för alla noder i grafen
	 */
//	Node[][] generateAdjancencyList(){
//		
//	}
	
	public static void main(String[] args) {
		findMinimumSpanningTree asdf = new findMinimumSpanningTree();
		//asdf.generateAdjacencyMatrix()
		
		//much debug
		for(int i = 0; i < asdf.nodesGenerated.length; i++) {
			//System.out.println("Node "+(i+1)+" is: "+asdf.nodesGenerated[i].toString()+"\n");
		}
	}
//==========================================================================================
	/*
	 * (Relevant för del 2)
	 * Hitta om en cykel finns.
	 */
//	boolean findCycle(){
//		Node[] nodesVisited;
//		
//	}
	
}