import java.util.LinkedList;
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
 * Få en adjacency matrix i någon form
 * Få en adjacency list i någon form - check
 */
public class findMinimumSpanningTree {
	private Node[] nodesGenerated;
	public Random r;
	int timesRun;
	int graphTraversed;
//	private Node root;
//	private Random r;
	
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
		int totalNodesToMake = 4 + r.nextInt(4); //ska vara 23
		
		//Tilldelar nodesGenerated en array med storlek motsvarande antalet noder som ska skapas
		nodesGenerated = new Node[totalNodesToMake];
		
		//Fyller arrayen med noder
		for(int i = 0; i < totalNodesToMake; i++) {
			nodesGenerated[i] = new Node(assigner.getIdentifier(), totalNodesToMake);
		}
		
		//Tilldelar kanter till noderna tills dess att alla noder kan nås från en nod
		do{
			Node node1;
			Node node2;
			while(true) {
				node1 = nodesGenerated[r.nextInt(nodesGenerated.length)];
				node2 = nodesGenerated[r.nextInt(nodesGenerated.length)];
				if(node1.toString() != node2.toString() && edgeAlreadyPresent(node1, node2)) {
					break;
				}
			}
			Edge e = new Edge(node1, node2);
//			System.out.println("Generated edge has weight "+e.weight);
			
		}while(!isConnected());
	}
	
	/*
	 * Hjälpmetod för generateGraph().
	 */
	private boolean edgeAlreadyPresent(Node n1, Node n2) {
		for(int i = 0; i < n1.edges.size(); i++) {
			if(n1.edges.get(i).getOtherNode(n1).toString() == n2.toString()){
				return false;
			}
		}
		return true;
	}
	
	public findMinimumSpanningTree() {
		this.r = new Random(58646);
	}
	
	
	/*
	 * Hannas isConnected.
	 */
	boolean isConnected() {
		timesRun++;
        LinkedList<Node> markedNodes = new LinkedList<Node>();
        LinkedList<Node> Q = new LinkedList<Node>();
        Q.add(nodesGenerated[0]);
        graphTraversed = 0;
        while (!Q.isEmpty()) {
        	graphTraversed++;
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
        System.out.println("isConnected took "+graphTraversed+" loops to traverse the graph.");
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
//    int[][] generateAdjacencyMatrix() {
//        int[][] matrix = new int[nodesGenerated.length][nodesGenerated.length];
//        for(int i = 0; i < nodesGenerated.length; i++) {
//            for(int j = 0; j < nodesGenerated.length; j++) {
//                matrix[i][j] = (isEqual(i)) ? 1 : 0;
//            }
//        }
//        return matrix;
//
//    }
//
//    boolean isEqual(int node) {
//            for (int i = 0; i < nodesGenerated[node].edges.size(); i++) {
//                if (nodesGenerated[node] == nodesGenerated[node].edges.get(i).getOtherNode(nodesGenerated[node])){
//                    return true;
//                }
//            }
//            return false;
//    }
    
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

        // test
//        for (int i = 0; i < nodesGenerated.length; i++) {
//            // Loop through all elements of current row 
//            for (int j = 0; j < nodesGenerated.length; j++) {
//                System.out.print(matrix[i][j]+" ");
//                    }System.out.print("\n");
//            }
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
	
	//end testfunktioner ========================================================================
	public static void main(String[] args) {
		findMinimumSpanningTree asdf = new findMinimumSpanningTree();
		
		asdf.generateGraph();
		
//		for(int i = 0; i < asdf.nodesGenerated.length; i++) {
//			System.out.println("Node "+asdf.nodesGenerated[i].toString()+" has "+asdf.nodesGenerated[i].edges.size()+" edges.");
//		}
		
		
		int[][] theMatrix = asdf.generateAdjacencyMatrix();
		String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		String alphaMatrix = "|   |";
		
		for(int i = 0; i < asdf.nodesGenerated.length; i++) {
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
		
		
		//much debug
		System.out.print(asdf.generateAdjacencyList());
		
		System.out.println("isConnected ran "+asdf.timesRun);
		
		
		
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