import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

/**
 * 
 * @author Gabriella Hafdell, Hanna Eriksson, Philip Larsson
 *
 */

/*
 * Vi behöver kunna:
 * Generera en graf - check
 * Se till att allt är anslutet/Kolla hur allt är anslutet - check
 * Få en adjacency matrix i någon form - check
 * Få en adjacency list i någon form - check
 */
public class findMinimumSpanningTree {
	private Node[] nodesGenerated;
	public Random r;
	
	//analysvariabler; ignore
	int timesRun;
	int graphTraversed;
	
	/*
	 * Tanken är att denna klass agerar som det kanter går mellan; som jag tolkat specen har noder i sig ingen vikt.
	 * En array innehåller alla kanter som ansluter en nod till en annan, antalet är begränsat.
	 */
	private class Node{
		String identifier;
		Vector<Edge> edges;
		
		Node(String identifier, int maxEdges){
			this.identifier = identifier;
			edges = new Vector<Edge>(maxEdges-1);
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
		
		/*
		 * Konstruktor
		 */
		Edge(){
			this.weight = Integer.MAX_VALUE;
		}
		
		Edge(Node node1, Node node2){
			if(node1.edges.capacity() > node1.edges.size() && node2.edges.capacity() > node2.edges.size()) {
				this.Node1 = node1;
				this.Node2 = node2;
				node1.edges.add(this);
				node2.edges.add(this);
				this.weight = 1 + r.nextInt(10);
			}
		}
		
		
		
		Node getOtherNode(Node origin) {
			return origin.toString() == Node1.toString() ? Node2 : Node1;
		}
		
		/*
		 * Metod för att ta bort en kant.
		 */
		void deleteEdge() {
			Node1.edges.remove(this);
			Node2.edges.remove(this);
		}
		
		
	}
	
	
	/*
	 * Skapar en identifierare för en nod.
	 */
	private class assignIdentifier {
		private int n;
		private String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		
		assignIdentifier(){
			n = -1;
		}
		
		String getIdentifier() {
			n++;
			//System.out.println("Generated node "+letters[n]);
			return letters[n];
		}
	}
	
	/*
	 * Genererar en graf.
	 */
	public void generateGraph() {
		//Skapar en intern klass som tilldelar identifierare
		assignIdentifier assigner = new assignIdentifier();
		
		//Antalet noder som ska genereras
		int totalNodesToMake = 23;// + r.nextInt(23);
		
		//Tilldelar nodesGenerated en array med storlek motsvarande antalet noder som ska skapas
		nodesGenerated = new Node[totalNodesToMake];
		
		//Fyller arrayen med noder
		for(int i = 0; i < totalNodesToMake; i++) {
			nodesGenerated[i] = new Node(assigner.getIdentifier(), totalNodesToMake);
		}
		
		//Tilldelar kanter till noderna tills dess att alla noder kan nås från en nod
		while(!isConnected()){
			Node node1;
			Node node2;
			while(true) {
				node1 = nodesGenerated[r.nextInt(nodesGenerated.length)];
				node2 = nodesGenerated[r.nextInt(nodesGenerated.length)];
				if(node1.toString() != node2.toString() && edgeNotAlreadyPresent(node1, node2)) {
					break;
				}
			}
			Edge e = new Edge(node1, node2);
			
		}
	}
	
	/*
	 * Hjälpmetod för generateGraph().
	 */
	private boolean edgeNotAlreadyPresent(Node n1, Node n2) {
		for(int i = 0; i < n1.edges.size(); i++) {
			if(n1.edges.get(i).getOtherNode(n1).toString() == n2.toString()){
				return false;
			}
		}
		return true;
	}
	
	public findMinimumSpanningTree() {
		this.r = new Random(2);
	}
	
	
	/*
	 * Hannas isConnected.
	 * Avgör om grafen är sammanhängande.
	 */
	boolean isConnected() {
        LinkedList<Node> markedNodes = new LinkedList<Node>();
        LinkedList<Node> Q = new LinkedList<Node>();
        Q.add(nodesGenerated[0]);
        while (!Q.isEmpty()) {
            Node u = Q.pop();
            if(!isInLst(u, markedNodes)) {
                markedNodes.add(u);
                for (int i = 0; i < u.edges.size(); i++) {
                    Node nodeToAdd = u.edges.get(i).Node1 == u ? u.edges.get(i).Node2 : u.edges.get(i).Node1;
                    if(!isInLst(nodeToAdd, markedNodes)) {
                        Q.add(nodeToAdd);
                    }
                }
            }



        }
        //System.out.println("isConnected took "+graphTraversed+" loops to traverse the graph."); //debug/analys
        if (markedNodes.size() == nodesGenerated.length) {
            return true;
        }
        return false;
    }

    boolean isInLst(Node node,LinkedList<Node> nodeLst) {
        for(int i = 0; i < nodeLst.size(); i++) {
            if (node == nodeLst.get(i)) {
                return true;
            }
        }
        return false;
    }
	
	/*
	 * Skapar en jämn matris för alla noder i grafen
	 */
    int[][] generateAdjacencyMatrix() {
        int[][] matrix = new int[nodesGenerated.length][nodesGenerated.length];
        String letterTest = "abcdefghijklmnopqrstuvwxyz";
        int x = 0;

        // Loop through all rows 
        for (int i = 0; i < nodesGenerated.length; i++) {
            // Loop through all elements of current row 
            for (int j = 0; j < nodesGenerated[i].edges.size(); j++) {
                x = letterTest.indexOf( nodesGenerated[i].edges.get(j).getOtherNode(nodesGenerated[i]).toString());
                matrix[i][x] = 1;
                    }
            }
        return matrix;
    }
	
	
	/*
	 * Skapar en "ojämn" lista för alla noder i grafen
	 */
	String generateAdjacencyList(){
		String Lst = "\nGraph adjacency list:\n";
		for(int i = 0; i < nodesGenerated.length; i++) {
			Lst += nodesGenerated[i].toString()+":";
			for(int x = 0; x < nodesGenerated[i].edges.size(); x++) {
				Lst = Lst + nodesGenerated[i].edges.get(x).getOtherNode(nodesGenerated[i]).toString();
			}
			Lst += "\n";
		}
		return Lst;
	}
	
    
    //testfunktioner, ignore =====================================================================
    private void jumpingThroughHoops() {
    	Node a = new Node("a", 3);
    	Node b = new Node("b", 3);
    	Edge e = new Edge(a, b);
    	
    	System.out.println(e.getOtherNode(b).toString());
    }
    
	/*
	 * Testgraf med känt utseende.
	 * Modellerad efter https://gyazo.com/67ff3e7cf619dc77a287dff14d473672
	 * En viktig ändring: Dubbla kanter tillåts inte, grafen ska tolkas som oriktad
	 */
	private Node[] knownGraph() {
		Node v1 = new Node("a", 4); //v1
		Node v2 = new Node("b", 4); //v2
		Node v3 = new Node("c", 4); //v3
		Node v4 = new Node("d", 4); //v4
		Node v5 = new Node("e", 4); //v5
		
		//Kanter för nod v1
		Edge v1_v2 = new Edge(v1, v2);
		Edge v1_v5 = new Edge(v1, v5);
		
		//Kanter för nod v2
		Edge v2_v1 = new Edge(v2, v1);
		Edge v2_v5 = new Edge(v2, v5);
		Edge v2_v3 = new Edge(v2, v3);
		Edge v2_v4 = new Edge(v2, v4);
		
		//Kanter för nod v3
		Edge v3_v2 = new Edge(v3, v2);
		Edge v3_v4 = new Edge(v3, v4);
		Edge v3_v5 = new Edge(v3, v5);
		
		//Kanter för nod v4
		Edge v4_v2 = new Edge(v4, v2);
		Edge v4_v3 = new Edge(v4, v3);
		Edge v4_v5 = new Edge(v4, v5);
		
		//Kanter för nod v5
		Edge v5_v1 = new Edge(v5, v2);
		Edge v5_v2 = new Edge(v5, v5);
		Edge v5_v3 = new Edge(v5, v2);
		Edge v5_v4 = new Edge(v5, v5);
		
		Node [] graph = {v1, v2, v3, v4, v5};
		return graph;
	}
	
	private void printAdjacencyMatrix() {

		int[][] theMatrix = generateAdjacencyMatrix();
		String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		String alphaMatrix = "|   |";
		
		for(int i = 0; i < nodesGenerated.length; i++) {
			alphaMatrix += " "+letters[i].toString()+" |";
		}
		alphaMatrix += "\n";
		
		for(int i = 0; i < theMatrix.length; i++) {
			alphaMatrix += "| "+letters[i]+" |";
			
			for(int x = 0; x < theMatrix[i].length; x++) {
				alphaMatrix += " "+theMatrix[i][x]+" |";
			}
			
			alphaMatrix += "\n";
		}
		System.out.println(alphaMatrix);
	}
	
	private void printAdjacencyList(){
		System.out.println(generateAdjacencyList());
	}
	//end testfunktioner ========================================================================
	
	public static void main(String[] args) {
		findMinimumSpanningTree asdf = new findMinimumSpanningTree();
		
		asdf.generateGraph();
		
		asdf.printAdjacencyMatrix();
		
		asdf.printAdjacencyList();
		
		
		System.out.println("Running prim on it!");
		
		//do not remove commented lines below
//		asdf.nodesGenerated = asdf.createNewTree(asdf.unsortedPrimAlg());
//		
//		asdf.printAdjacencyMatrix();
//		
//		asdf.printAdjacencyList();
//		
//		System.out.println(asdf.cyclePresent(asdf.nodesGenerated[0]));
		
		
		asdf.heapedPrimAlg(asdf.unsortedPrimAlg());
		
		
		
	}
	
	
	
//=================================|Del 2|=========================================================
	/*
	 * (*inte* Relevant för del 2)
	 * Hitta om en cykel finns.
	 */
	boolean cyclePresent(Node start) {
		LinkedList<Node> foundNodes = new LinkedList<Node>();
		LinkedList<Edge> edgesMarked = new LinkedList<Edge>();
		LinkedList<Node> Queue = new LinkedList<Node>();
		
		Queue.add(start);
		while(!Queue.isEmpty()) {
			Node n = Queue.pop();
			if(foundNodes.contains(n)) {
				return true;
			} else {
				foundNodes.add(n);
			}
			for(int i = 0; i < n.edges.size(); i++) {
				Edge e = n.edges.get(i);
				if(edgesMarked.contains(e)) {
					continue;
				}
				edgesMarked.add(e);
				Node next = n.edges.get(i).getOtherNode(n);
				Queue.add(next);
			}
			
		}
		return false;
	}
	
	
	/*
	 * 
	 */
	LinkedList<Edge> unsortedPrimAlg() {
		//data
		LinkedList<Node> chosenNodes = new LinkedList<Node>();
		LinkedList<Edge> chosenEdges = new LinkedList<Edge>();
		
		//vars
		Node cur = nodesGenerated[r.nextInt(nodesGenerated.length)]; //startnod
		Edge minEdge = new Edge();
		
		//init
		chosenNodes.add(cur);
		
		while(chosenNodes.size() < nodesGenerated.length) {
			for(int i = 0; i < chosenNodes.size(); i++) {
				cur = chosenNodes.get(i);
				minEdge = new Edge();
				
				for(int x = 0; x < cur.edges.size(); x++) {
					if(cur.edges.get(x).weight < minEdge.weight && !chosenNodes.contains(cur.edges.get(x).getOtherNode(cur))) {
						minEdge = cur.edges.get(x);
					}//if
				}//for
				
				if(minEdge.Node1 != null) {
					chosenEdges.add(minEdge);
					chosenNodes.add(minEdge.getOtherNode(cur));
				}
			
			}
		}//while
		return chosenEdges;
	}
	
	/*
	 * This is broke
	 */
	Node[] heapedPrimAlg(LinkedList<Edge> eList) {
		Node[] test = {};
		PriorityQueue<Edge> pList = new PriorityQueue<Edge>();
		
		for(int i = 0; i < eList.size(); i++) {
			pList.add(eList.get(i));
		}
		
		System.out.println("First element connects node "+pList.peek().Node1.toString()+" and "+pList.peek().Node2.toString()+".");
		
		
		return test;
	}
	
	
	/*
	 * Skapar ett träd genom att ta bort alla kanter som Prims algoritm inte valde.
	 */
	Node[] createNewTree(LinkedList<Edge> eList) {
		Node[] copyOfOriginal = nodesGenerated.clone();
		Edge[] killList = new Edge[50];
		
		int asdf = -1;
		for(Node n : copyOfOriginal) {
			for(Edge e : n.edges) {
				if(!eList.contains(e)) {
					asdf++;
					killList[asdf] = e;
				}
			}
		}
		
		for(Edge target : killList) {
			if(target == null) {
				break;
			}
			target.deleteEdge();
		}
		
		return copyOfOriginal;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}