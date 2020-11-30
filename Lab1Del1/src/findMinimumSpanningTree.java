import java.util.Collections;
import java.util.Comparator;
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
 * Vi behöver kunna: Generera en graf - check Se till att allt är anslutet/Kolla
 * hur allt är anslutet - check Få en adjacency matrix i någon form - check Få
 * en adjacency list i någon form - check
 */
public class findMinimumSpanningTree {
	private Node[] nodesGenerated;
	public Random r;

	// easy to reach testing levers
	private static int mode = 0;
	/*
	 * Mode 0 = invalid
	 * Mode 1 = unsortedListPrimAlg
	 * Mode 2 = unsortedListPrimAlgMatrix
	 * Mode 3 = heapedPrimAlgAdjecencyLst
	 * Mode 4 = heapedPrimAlgAdjecencyMatrix
	 */
	private boolean useRandomSeed = false;
	private int seedToUse = 984;
	private int nodesToMake = 10000;

	// analysvariabler; ignore
	int timesRun;
	int graphAttempts;
	int graphTraversed;
	int maximumRestart = 0;

	// analysis: different prims
	int heapedPrimAlgActions;
	int unsortedPrimAlgActions;

	// analysis: adjacency matrix and list
	int matrixActions;
	int listActions;

	/*
	 * Tanken är att denna klass agerar som det kanter går mellan; som jag tolkat
	 * specen har noder i sig ingen vikt. En array innehåller alla kanter som
	 * ansluter en nod till en annan, antalet är begränsat.
	 */
	private class Node {
		String identifier;
		Vector<Edge> edges;

		Node(String identifier, int maxEdges) {
			this.identifier = identifier;
			edges = new Vector<Edge>(maxEdges - 1);
		}

		public String toString() {
			return identifier;
		}

		/*
		 * Dies horribly if invalid node is provided
		 */
		public Edge getEdge(Node a) throws NumberFormatException {
			Edge theEdge = null;
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get(i).getOtherNode(this) == a) {
					theEdge = edges.get(i);
					break;
				}
			}
			return theEdge;
		}// method
	}

	/*
	 * Kanten mellan noder: Har en vikt och måste vara ansluten till två olika
	 * noder.
	 */
	private class Edge {
		Node Node1, Node2;
		int weight;

		/*
		 * Konstruktor
		 */
		Edge() {
			this.weight = Integer.MAX_VALUE;
		}

		Edge(Node node1, Node node2) {
			if (node1.edges.capacity() > node1.edges.size() && node2.edges.capacity() > node2.edges.size()) {
//				edgesCreated++; //debug
				this.Node1 = node1;
				this.Node2 = node2;
				node1.edges.add(this);
				node2.edges.add(this);
				this.weight = 1 + r.nextInt(20); // should be 20
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
	 * Old compare
	 */
	public class Compare implements Comparator<Edge> {

		@Override
		public int compare(Edge o1, Edge o2) {
//			if(o1.weight < o2.weight) {
//				return -1;
//			}else if(o1.weight == o2.weight) {
//				return 0;
//			}else {
//				return 1;
//			}
			return (o1.weight - o2.weight);
		}

	}

	/*
	 * Skapar en identifierare för en nod.
	 */
	private class assignIdentifier {
		private int n;
		private int x;
		private String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
				"q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

		assignIdentifier() {
			n = -1;
		}

		String getIdentifier() {
			String identity = "";
			if(n > 24) {
				n = 0;
				x++;
			}else {
				n++;
			}
			identity += letters[n]+x;
			
			return identity;
		}
	}

	/*
	 * Genererar en graf.
	 */
	public void generateGraph(int nodesToMake) {
//		System.out.println("Generating graph...");
		// Skapar en intern klass som tilldelar identifierare
		assignIdentifier assigner = new assignIdentifier();

		// Antalet noder som ska genereras
//		int totalNodesToMake = this.nodesToMake;

		// Tilldelar nodesGenerated en array med storlek motsvarande antalet noder som
		// ska skapas
		nodesGenerated = new Node[nodesToMake];

		// Fyller arrayen med noder
		for (int i = 0; i < nodesToMake; i++) {
			nodesGenerated[i] = new Node(assigner.getIdentifier(), nodesToMake);
		}
		
		LinkedList<Node> selection = new LinkedList<Node>();
		
		Collections.addAll(selection, nodesGenerated);

		// Tilldelar kanter till noderna tills dess att alla noder kan nås från en nod
		Node node1;
		Node node2;
		int count = 0;
		while (true) {
			count++;
			while (true) {
				graphAttempts++;
				node1 = selection.get(r.nextInt(selection.size()));
				node2 = selection.get(r.nextInt(selection.size()));
				if (node1.toString() != node2.toString() && edgeNotAlreadyPresent(node1, node2)) {
					break;
				}
			}
			Edge e = new Edge(node1, node2);
			if(node1.edges.size() == nodesToMake) {
				selection.remove(node1);
			}else if(node2.edges.size() == nodesToMake) {
				selection.remove(node2);
			}
			
			if(count > nodesGenerated.length / 10) {
				if(isConnected()) {
					break;
				}else {
					count = 0;
				}
			}

		}
//		System.out.println("isConnected spammed: "+timesRun);
//		System.out.println("Node connection attempts spammed: "+graphAttempts);
//		System.out.println("Done!");
	}

	/*
	 * Hjälpmetod för generateGraph().
	 */
	private boolean edgeNotAlreadyPresent(Node n1, Node n2) {
		for (int i = 0; i < n1.edges.size(); i++) {
			if (n1.edges.get(i).getOtherNode(n1).toString() == n2.toString()) {
				return false;
			}
		}
		return true;
	}

	public findMinimumSpanningTree() {
		if (this.useRandomSeed) {
			this.r = new Random(System.currentTimeMillis());
		} else {
			this.r = new Random(this.seedToUse);
		}
	}

	/*
	 * Hannas isConnected. Avgör om grafen är sammanhängande.
	 */
	boolean isConnected() {
		timesRun++;
		LinkedList<Node> markedNodes = new LinkedList<Node>();
		LinkedList<Node> Q = new LinkedList<Node>();
		Q.add(nodesGenerated[0]);
		while (!Q.isEmpty()) {
			Node u = Q.pop();
			if (!markedNodes.contains(u)) {
				markedNodes.add(u);
				for (int i = 0; i < u.edges.size(); i++) {
					Node nodeToAdd = u.edges.get(i).Node1 == u ? u.edges.get(i).Node2 : u.edges.get(i).Node1;
					if (!markedNodes.contains(nodeToAdd)) {
						Q.add(nodeToAdd);
					}
				}
			}

		}
		// System.out.println("isConnected took "+graphTraversed+" loops to traverse the
		// graph."); //debug/analys
		if (markedNodes.size() == nodesGenerated.length) {
			return true;
		}
		return false;
	}

//	boolean isInLst(Node node, LinkedList<Node> nodeLst) {
//		for (int i = 0; i < nodeLst.size(); i++) {
//			if (node == nodeLst.get(i)) {
//				return true;
//			}
//		}
//		return false;
//	}

	/*
	 * Philips bastardiserade version av Bellas AdjacencyMatrix
	 */
	Node[][] generateWorseAdjacencyMatrix() {
		Node[][] matrix = new Node[nodesGenerated.length][nodesGenerated.length + 1];
		int pos;

		for (int i = 0; i < nodesGenerated.length; i++) {
			matrix[i][0] = nodesGenerated[i];
			matrixActions++;
			for (int x = 0; x < nodesGenerated[i].edges.size(); x++) {
				matrixActions++;
				pos = getPos(nodesGenerated[i].edges.get(x).getOtherNode(nodesGenerated[i]).toString());
				matrixActions++;
				matrix[i][pos + 1] = nodesGenerated[i].edges.get(x).getOtherNode(nodesGenerated[i]);
				matrixActions++;
			}
		}
		matrixActions++;
		return matrix;
	}
	
	int getPos(String ident) {
		String letterTest = "abcdefghijklmnopqrstuvwxyz";
		int p1 = letterTest.indexOf(ident.substring(0, 0));
		int p2 = Integer.parseInt(ident.substring(1));
		return p1 + 26 * p2;
		
	}

	/*
	 * Skapar en "ojämn" lista för alla noder i grafen
	 */
	Node[][] generateAdjacencyList() {
		Node[][] adjList = new Node[nodesGenerated.length][];

		for (int i = 0; i < nodesGenerated.length; i++) { // browse nodes
			adjList[i] = new Node[nodesGenerated[i].edges.size() + 1];
			adjList[i][0] = nodesGenerated[i];
			listActions++;
			for (int x = 0; x < nodesGenerated[i].edges.size(); x++) {
				adjList[i][x + 1] = nodesGenerated[i].edges.get(x).getOtherNode(nodesGenerated[i]);
				listActions++;
			}
		}
		listActions++;
		return adjList;
	}

	// testfunktioner, ignore
	// =====================================================================
	private void jumpingThroughHoops() {
		Node a = new Node("a", 3);
		Node b = new Node("b", 3);
		Edge e = new Edge(a, b);

		System.out.println(e.getOtherNode(b).toString());
	}

	/*
	 * Testgraf med känt utseende. Modellerad efter
	 * https://gyazo.com/67ff3e7cf619dc77a287dff14d473672 En viktig ändring: Dubbla
	 * kanter tillåts inte, grafen ska tolkas som oriktad
	 */
	private Node[] knownGraph() {
		Node v1 = new Node("a", 4); // v1
		Node v2 = new Node("b", 4); // v2
		Node v3 = new Node("c", 4); // v3
		Node v4 = new Node("d", 4); // v4
		Node v5 = new Node("e", 4); // v5

		// Kanter för nod v1
		Edge v1_v2 = new Edge(v1, v2);
		Edge v1_v5 = new Edge(v1, v5);

		// Kanter för nod v2
		Edge v2_v1 = new Edge(v2, v1);
		Edge v2_v5 = new Edge(v2, v5);
		Edge v2_v3 = new Edge(v2, v3);
		Edge v2_v4 = new Edge(v2, v4);

		// Kanter för nod v3
		Edge v3_v2 = new Edge(v3, v2);
		Edge v3_v4 = new Edge(v3, v4);
		Edge v3_v5 = new Edge(v3, v5);

		// Kanter för nod v4
		Edge v4_v2 = new Edge(v4, v2);
		Edge v4_v3 = new Edge(v4, v3);
		Edge v4_v5 = new Edge(v4, v5);

		// Kanter för nod v5
		Edge v5_v1 = new Edge(v5, v2);
		Edge v5_v2 = new Edge(v5, v5);
		Edge v5_v3 = new Edge(v5, v2);
		Edge v5_v4 = new Edge(v5, v5);

		Node[] graph = { v1, v2, v3, v4, v5 };
		return graph;
	}

	private void printWorseAdjacencyMatrix() {
		Node[][] matrix = generateWorseAdjacencyMatrix();
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		String stringmatrix = "|   |";
		
		if(nodesGenerated.length > 50) {
			System.out.println("The generated matrix was too large to display.");
			return;
		}

		// begin
		System.out.println("Adjacency Matrix: ");

		for (int a = 0; a < alphabet.length(); a++) {
			stringmatrix += " " + alphabet.charAt(a) + " |";
		}
		stringmatrix += "\n";

		for (int i = 0; i < matrix.length; i++) {
			stringmatrix += "| " + alphabet.charAt(i) + " |";
			for (int x = 1; x < matrix[i].length; x++) {
				stringmatrix += matrix[i][x] == null ? " 0 |" : " 1 |";
			}
			stringmatrix += "\n";
		}

		System.out.println(stringmatrix);
	}

	private void printAdjacencyList() {
		if(nodesGenerated.length > 50) {
			System.out.println("The generated adjacency list was too large to display.");
			return;
		}
		Node[][] aList = generateAdjacencyList();
		System.out.println("Graph adjacency list: ");
		for (int i = 0; i < aList.length; i++) {
			System.out.print(aList[i][0].toString() + ":");
			for (int x = 1; x < aList[i].length; x++) {
				System.out.print(aList[i][x].toString());
			}
			System.out.print("\n");
		}
	}
	// end testfunktioner
	// ========================================================================

	public static void main(String[] args) {

//		findMinimumSpanningTree asdf = new findMinimumSpanningTree();
//
//		asdf.generateGraph();
//
//		======= ORIGINAL GRAPH =======
//		asdf.printWorseAdjacencyMatrix();
//		asdf.printAdjacencyList();
//		System.out.println("Has cycle?: " + asdf.cyclePresent(asdf.nodesGenerated[0]));
//		System.out.println("Is connected?: " + asdf.isConnected());
//		System.out.println("The matrix took " + asdf.matrixActions + " actions to generate.");
//		System.out.println("The list took " + asdf.listActions + " actions to generate.");
//
//		asdf.matrixActions = 0;
//		asdf.listActions = 0;

		
		switch(mode) {
		
		case 1:
//			======= UNSORTED VERSION w/ LIST =======
			System.out.println("Running Unsorted list.\n Input: Adjacency List. \n Nodes: 2000");
			findMinimumSpanningTree listlist2k = new findMinimumSpanningTree();
			listlist2k.generateGraph(2000);
			listlist2k.printUnsortedList();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency List. \n Nodes: 4000");
			findMinimumSpanningTree listlist4k = new findMinimumSpanningTree();
			listlist4k.generateGraph(4000);
			listlist4k.printUnsortedList();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency List. \n Nodes: 6000");
			findMinimumSpanningTree listlist6k = new findMinimumSpanningTree();
			listlist6k.generateGraph(6000);
			listlist6k.printUnsortedList();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency List. \n Nodes: 8000");
			findMinimumSpanningTree listlist8k = new findMinimumSpanningTree();
			listlist8k.generateGraph(8000);
			listlist8k.printUnsortedList();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency List. \n Nodes: 10000");
			findMinimumSpanningTree listlist10k = new findMinimumSpanningTree();
			listlist10k.generateGraph(10000);
			listlist10k.printUnsortedList();
			System.out.print("\n");
		
			break;
			
		case 2:
//			======= UNSORTED VERSION w/ MATRIX =======
			System.out.println("Running Unsorted list.\n Input: Adjacency Matrix.\n Nodes: 2000");
			findMinimumSpanningTree listMatrix2k = new findMinimumSpanningTree();
			listMatrix2k.generateGraph(2000);
			listMatrix2k.printUnsortedMatrix();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency Matrix.\n Nodes: 4000");
			findMinimumSpanningTree listMatrix4k = new findMinimumSpanningTree();
			listMatrix4k.generateGraph(4000);
			listMatrix4k.printUnsortedMatrix();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency Matrix.\n Nodes: 6000");
			findMinimumSpanningTree listMatrix6k = new findMinimumSpanningTree();
			listMatrix6k.generateGraph(6000);
			listMatrix6k.printUnsortedMatrix();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency Matrix.\n Nodes: 8000");
			findMinimumSpanningTree listMatrix8k = new findMinimumSpanningTree();
			listMatrix8k.generateGraph(8000);
			listMatrix8k.printUnsortedMatrix();
			System.out.print("\n");
			
			System.out.println("Running Unsorted list.\n Input: Adjacency Matrix.\n Nodes: 10000");
			findMinimumSpanningTree listMatrix10k = new findMinimumSpanningTree();
			listMatrix10k.generateGraph(10000);
			listMatrix10k.printUnsortedMatrix();
			System.out.print("\n");
			break;
			
		case 3:
//			====== HEAPED VERSION w/ LIST ========== 
			System.out.println("Running min-heap Prim.\n Input: Adjacency List.\n Nodes: 2000");
			findMinimumSpanningTree heapList2k = new findMinimumSpanningTree();
			heapList2k.generateGraph(2000);
			heapList2k.printHeapedList();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency List.\n Nodes: 4000");
			findMinimumSpanningTree heapList4k = new findMinimumSpanningTree();
			heapList4k.generateGraph(4000);
			heapList4k.printHeapedList();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency List.\n Nodes: 6000");
			findMinimumSpanningTree heapList6k = new findMinimumSpanningTree();
			heapList6k.generateGraph(6000);
			heapList6k.printHeapedList();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency List.\n Nodes: 8000");
			findMinimumSpanningTree heapList8k = new findMinimumSpanningTree();
			heapList8k.generateGraph(8000);
			heapList8k.printHeapedList();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency List.\n Nodes: 10000");
			findMinimumSpanningTree heapList10k = new findMinimumSpanningTree();
			heapList10k.generateGraph(10000);
			heapList10k.printHeapedList();
			System.out.print("\n");
			break;
			
		case 4:
//		====== HEAPED VERSION w/ MATRIX ========== 
			System.out.println("Running min-heap Prim.\n Input: Adjacency Matrix.\n Nodes: 2000");
			findMinimumSpanningTree heapMatrix2k = new findMinimumSpanningTree();
			heapMatrix2k.generateGraph(2000);
			heapMatrix2k.printHeapedMatrix();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency Matrix.\n Nodes: 4000");
			findMinimumSpanningTree heapMatrix4k = new findMinimumSpanningTree();
			heapMatrix4k.generateGraph(4000);
			heapMatrix4k.printHeapedMatrix();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency Matrix.\n Nodes: 6000");
			findMinimumSpanningTree heapMatrix6k = new findMinimumSpanningTree();
			heapMatrix6k.generateGraph(6000);
			heapMatrix6k.printHeapedMatrix();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency Matrix.\n Nodes: 8000");
			findMinimumSpanningTree heapMatrix8k = new findMinimumSpanningTree();
			heapMatrix8k.generateGraph(8000);
			heapMatrix8k.printHeapedMatrix();
			System.out.print("\n");
			
			System.out.println("Running min-heap Prim.\n Input: Adjacency Matrix.\n Nodes: 10000");
			findMinimumSpanningTree heapMatrix10k = new findMinimumSpanningTree();
			heapMatrix10k.generateGraph(10000);
			heapMatrix10k.printHeapedMatrix();
			System.out.print("\n");
			
			break;
			
		default:
			System.out.println("Invalid mode selected");
			break;
		}
	}

	void printUnsortedList() {
		nodesGenerated = createNewTree(unsortedListPrimAlg(generateAdjacencyList()));
		int ListActions = this.listActions;
		// printWorseAdjacencyMatrix();

		printAdjacencyList();

		System.out.println("Has cycle?: " + cyclePresent(nodesGenerated[0]));
		System.out.println("Is connected?: " + isConnected());
		System.out.println("Unsorted version used " + unsortedPrimAlgActions + " actions.");
		System.out.println("The input list took " + ListActions + " actions to generate.");
	}

	void printUnsortedMatrix() {
		nodesGenerated = createNewTree(unsortedListPrimAlgMatrix(generateWorseAdjacencyMatrix()));
		int MatrixActions = this.matrixActions;
		// printWorseAdjacencyMatrix();

		printAdjacencyList();

		System.out.println("Has cycle?: " + cyclePresent(nodesGenerated[0]));
		System.out.println("Is connected?: " + isConnected());
		System.out.println("Unsorted version used " + unsortedPrimAlgActions + " actions.");
		System.out.println("The input matrix took " + MatrixActions + " actions to generate.");
//		System.out.println("The list took " + listActions + " actions to generate.");
	}

	void printHeapedList() {
		nodesGenerated = createNewTree(heapedPrimAlgAdjecencyLst());
		int ListActions = this.listActions;
//		printWorseAdjacencyMatrix();

		printAdjacencyList();

		System.out.println("Has cycle?: " + cyclePresent(nodesGenerated[0]));
		System.out.println("Is connected?: " + isConnected());
		System.out.println("Heaped version used " + heapedPrimAlgActions + " actions.");
//		System.out.println("The matrix took "+matrixActions+" actions to generate.");
		System.out.println("The input list took " + ListActions + " actions to generate.");
	}

	void printHeapedMatrix() {
		nodesGenerated = createNewTree(heapedPrimAlgAdjecencyMatrix());
		int MatrixActions = this.matrixActions;
//		printWorseAdjacencyMatrix();

		printAdjacencyList();

		System.out.println("Has cycle?: " + cyclePresent(nodesGenerated[0]));
		System.out.println("Is connected?: " + isConnected());
		System.out.println("Heaped version used " + heapedPrimAlgActions + " actions.");
		System.out.println("The input matrix took " + MatrixActions + " actions to generate.");
//		System.out.println("The list took "+listActions+" actions to generate.");
	}

//=================================|Del 2|=========================================================
	/*
	 * (typ relevant för del 2) Hitta om en cykel finns.
	 */
	boolean cyclePresent(Node start) {
		LinkedList<Node> foundNodes = new LinkedList<Node>();
		LinkedList<Edge> edgesMarked = new LinkedList<Edge>();
		LinkedList<Node> Queue = new LinkedList<Node>();

		Queue.add(start);
		while (!Queue.isEmpty()) {
			Node n = Queue.pop();
			if (foundNodes.contains(n)) {
				return true;
			} else {
				foundNodes.add(n);
			}
			for (int i = 0; i < n.edges.size(); i++) {
				Edge e = n.edges.get(i);
				if (edgesMarked.contains(e)) {
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
	 * Skapar ett träd genom att ta bort alla kanter som Prims algoritm inte valde.
	 * 
	 * Note: Denna implementation är något klumpig för att undvika ett
	 * concurrentModificationException.
	 */
	Node[] createNewTree(LinkedList<Edge> eList) {
		Node[] copyOfOriginal = nodesGenerated.clone();
		Edge[] killList = new Edge[nodesGenerated.length*nodesGenerated.length];

		int asdf = -1;
		for (Node n : copyOfOriginal) {
			for (Edge e : n.edges) {
				if (!eList.contains(e)) {
					asdf++;
					killList[asdf] = e;
				}
			}
		}

		for (Edge target : killList) {
			if (target == null) {
				break;
			}
			target.deleteEdge();
		}

		return copyOfOriginal;
	}

	/*
	 * Unsorted prim algorithm w/ list
	 */
	LinkedList<Edge> unsortedListPrimAlg(Node[][] adjList) {
		// data
		LinkedList<Node> chosenNodes = new LinkedList<Node>();
		LinkedList<Edge> chosenEdges = new LinkedList<Edge>();

		// vars
		Node cur = adjList[r.nextInt(adjList.length)][0]; // choose starting node randomly
		// 0th element in each array is the node; the rest are connected nodes
		Edge minEdge;

		// init
		chosenNodes.add(cur);
		unsortedPrimAlgActions++;
		// find the lowest weight edge from this node to an unselected node

		while (chosenNodes.size() < adjList.length) {
			for (int i = 0; i < chosenNodes.size(); i++) {
				cur = chosenNodes.get(i);
				unsortedPrimAlgActions++;
				minEdge = new Edge(); // reset min value found

				for (int x = 0; x < adjList.length; x++) {
					unsortedPrimAlgActions++;
					if (cur == adjList[x][0]) {
						for (int y = 1; y < adjList[x].length; y++) {
							unsortedPrimAlgActions++;
							if (adjList[x][y].getEdge(cur).weight < minEdge.weight
									&& !chosenNodes.contains(adjList[x][y].getEdge(cur).getOtherNode(cur))) {
								minEdge = adjList[x][y].getEdge(cur);
								

							}
						}
					}
				}

				if (minEdge.Node1 != null) {
					chosenEdges.add(minEdge);
					unsortedPrimAlgActions++;
					chosenNodes.add(minEdge.getOtherNode(cur));
					unsortedPrimAlgActions++;
				}

			}
		}

		return chosenEdges;
	}

	/*
	 * Unsorted prim algorithm w/ matrix
	 */
	LinkedList<Edge> unsortedListPrimAlgMatrix(Node[][] adjMatrix) {
		// data
		LinkedList<Node> chosenNodes = new LinkedList<Node>();
		LinkedList<Edge> chosenEdges = new LinkedList<Edge>();

		// vars
		Node cur = adjMatrix[r.nextInt(adjMatrix.length)][0]; // choose starting node randomly
		// 0th element in each array is the node; the rest are connected nodes
		Edge minEdge;

		// init
		chosenNodes.add(cur);
		unsortedPrimAlgActions++;
		// find the lowest weight edge from this node to an unselected node

		while (chosenNodes.size() < adjMatrix.length) {
			for (int i = 0; i < chosenNodes.size(); i++) {
				cur = chosenNodes.get(i);
				unsortedPrimAlgActions++;
				minEdge = new Edge(); // reset min value found

				for (int x = 0; x < adjMatrix.length; x++) {
					unsortedPrimAlgActions++;
					if (cur == adjMatrix[x][0]) {
						for (int y = 1; y < adjMatrix[x].length; y++) {
							if (adjMatrix[x][y] == null) {
								unsortedPrimAlgActions++;
								continue;
							}
							unsortedPrimAlgActions++;
							if (adjMatrix[x][y].getEdge(cur).weight < minEdge.weight
									&& !chosenNodes.contains(adjMatrix[x][y].getEdge(cur).getOtherNode(cur))) {
								minEdge = adjMatrix[x][y].getEdge(cur);
								

							}
						}
					}
				}

				if (minEdge.Node1 != null) {
					chosenEdges.add(minEdge);
					unsortedPrimAlgActions++;
					chosenNodes.add(minEdge.getOtherNode(cur));
					unsortedPrimAlgActions++;
				}

			}
		} // while

		return chosenEdges;
	}

	/*
	 * heaped prim algorithm w/ list
	 */
	LinkedList<Edge> heapedPrimAlgAdjecencyLst() {
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(new Compare());
		LinkedList<Node> markedNodes = new LinkedList<Node>();
		LinkedList<Edge> markedEdges = new LinkedList<Edge>();
		Node[][] adjecencyLst = generateAdjacencyList();
		Node curNode = nodesGenerated[r.nextInt(nodesGenerated.length)];
		int restart = 0;
		markedNodes.add(curNode);
		heapedPrimAlgActions++;
		while (markedNodes.size() < adjecencyLst.length) {
			heapedPrimAlgActions++;
			for (int i = 0; i < adjecencyLst.length; i++) {
				heapedPrimAlgActions++;
				if (curNode == adjecencyLst[i][0]) {
					for (int j = 1; j < adjecencyLst[i].length; j++) {
						Edge e = curNode.getEdge(adjecencyLst[i][j]);
						if (!edges.contains(e)) {
							edges.add(e);
						}
						heapedPrimAlgActions++;
					}
					break;
				}
			}

			while (!edges.isEmpty()) {
				Edge e = edges.poll();
				heapedPrimAlgActions++;
				if (markedNodes.contains(e.Node1) && !markedNodes.contains(e.Node2)) {
					markedEdges.add(e);
					markedNodes.add(e.Node2);
					restart++;
					heapedPrimAlgActions++;
					curNode = e.Node2;
					break;
				} else if (!markedNodes.contains(e.Node1) && markedNodes.contains(e.Node2)) {
					markedEdges.add(e);
					markedNodes.add(e.Node1);
					restart++;
					heapedPrimAlgActions++;
					curNode = e.Node1;
					break;
				}
			}
			if (edges.isEmpty()) {
				this.maximumRestart++; // analysis/debug
				if (restart == 0) {
					restart = markedNodes.size() - 1;
				} else {
					restart--;
				}
				heapedPrimAlgActions++;
				curNode = markedNodes.get(restart);
			}
		}
		heapedPrimAlgActions++;
		return markedEdges;
	}

	/*
	 * heaped prim algorithm w/ matrix
	 */

	LinkedList<Edge> heapedPrimAlgAdjecencyMatrix() {
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(new Compare());
		LinkedList<Node> markedNodes = new LinkedList<Node>();
		LinkedList<Edge> markedEdges = new LinkedList<Edge>();
		Node[][] adjecencyMatrix = generateWorseAdjacencyMatrix();
		Node curNode = nodesGenerated[r.nextInt(nodesGenerated.length)];
		int restart = 0;

		heapedPrimAlgActions++;
		markedNodes.add(curNode);
		heapedPrimAlgActions++;
		while (markedNodes.size() < adjecencyMatrix.length) {
			heapedPrimAlgActions++;
			for (int i = 0; i < adjecencyMatrix.length; i++) {
				heapedPrimAlgActions++;
				if (curNode == adjecencyMatrix[i][0]) {
					for (int j = 1; j < adjecencyMatrix[i].length; j++) {
						if (adjecencyMatrix[i][j] == null) {
							heapedPrimAlgActions++;
							continue;
						}
						Edge e = curNode.getEdge(adjecencyMatrix[i][j]);
						heapedPrimAlgActions++;
						edges.add(e);
					}
					break;
				}
			}
			while (!edges.isEmpty()) {
				Edge e = edges.poll();
				heapedPrimAlgActions++;
				if (markedNodes.contains(e.Node1) && !markedNodes.contains(e.Node2)) {
					markedEdges.add(e);
					markedNodes.add(e.Node2);
					restart++;
					heapedPrimAlgActions++;
					curNode = e.Node2;
					break;
				} else if (!markedNodes.contains(e.Node1) && markedNodes.contains(e.Node2)) {
					markedEdges.add(e);
					markedNodes.add(e.Node1);
					restart++;
					heapedPrimAlgActions++;
					curNode = e.Node1;
					break;
				}
			}
			if (edges.isEmpty()) {
				if (restart == 0) {
					restart = markedNodes.size() - 1;
				} else {
					restart--;
				}
				heapedPrimAlgActions++;
				curNode = markedNodes.get(restart);
			}
		}
		heapedPrimAlgActions++;
		return markedEdges;
	}
	/*
	 * =============================================================================
	 * Fond memories below
	 * =============================================================================
	 */

	/*
	 * Hannas originallösning för heapedPrimAlg
	 *
	 * Ändra så att den börjar från en given nod använd adjacency list eller matrix
	 * för nästa steg
	 */
	/*
	 * LinkedList<Edge> heapedPrimAlg(Node[] graph) { PriorityQueue<Edge> allEdges =
	 * new PriorityQueue<Edge>(new Compare()); LinkedList<Node> markedNodes = new
	 * LinkedList<Node>(); LinkedList<Edge> markedEdges = new LinkedList<Edge>();
	 * 
	 * //Hitta alla kanter i grafen for(int i = 0; i < graph.length; i++) { for(int
	 * j = 0; j < graph[i].edges.size(); j++) {
	 * if(!allEdges.contains(graph[i].edges.get(j))) {
	 * allEdges.add(graph[i].edges.get(j)); } } }
	 * 
	 * //Run Prim's algorithm LinkedList<Edge> allEdgesLst = new LinkedList<Edge>();
	 * while(!allEdges.isEmpty()) { allEdgesLst.add(allEdges.poll()); } Edge e =
	 * allEdgesLst.remove(); heapedPrimAlgActions++; markedNodes.add(e.Node1);
	 * markedNodes.add(e.Node2); markedEdges.add(e); heapedPrimAlgActions++;
	 * while(markedNodes.size() < nodesGenerated.length) { for(int i = 0; i <
	 * allEdgesLst.size(); i++) { heapedPrimAlgActions++; if
	 * (markedNodes.contains(allEdgesLst.get(i).Node1) &&
	 * !markedNodes.contains(allEdgesLst.get(i).Node2)){
	 * markedNodes.add(allEdgesLst.get(i).Node2);
	 * markedEdges.add(allEdgesLst.get(i)); allEdgesLst.remove(i);
	 * heapedPrimAlgActions++; break; } else if
	 * (!markedNodes.contains(allEdgesLst.get(i).Node1) &&
	 * markedNodes.contains(allEdgesLst.get(i).Node2)){
	 * markedNodes.add(allEdgesLst.get(i).Node1);
	 * markedEdges.add(allEdgesLst.get(i)); allEdgesLst.remove(i);
	 * heapedPrimAlgActions++; break; }
	 * 
	 * } }
	 * 
	 * return markedEdges; }
	 */
	/*
	 * Originallösningen för unsorted
	 */
	/*
	 * LinkedList<Edge> unsortedPrimAlg(Node[] graph) { //data LinkedList<Node>
	 * chosenNodes = new LinkedList<Node>(); LinkedList<Edge> chosenEdges = new
	 * LinkedList<Edge>();
	 * 
	 * //vars Node cur = graph[r.nextInt(graph.length)]; //startnod Edge minEdge =
	 * new Edge();
	 * 
	 * //init chosenNodes.add(cur); unsortedPrimAlgActions++;
	 * 
	 * while(chosenNodes.size() < graph.length) { for(int i = 0; i <
	 * chosenNodes.size(); i++) { cur = chosenNodes.get(i);
	 * unsortedPrimAlgActions++; minEdge = new Edge(); //reset min value found
	 * 
	 * for(int x = 0; x < cur.edges.size(); x++) { unsortedPrimAlgActions++;
	 * if(cur.edges.get(x).weight < minEdge.weight &&
	 * !chosenNodes.contains(cur.edges.get(x).getOtherNode(cur))) { minEdge =
	 * cur.edges.get(x); unsortedPrimAlgActions++; }//if }//for
	 * 
	 * if(minEdge.Node1 != null) { chosenEdges.add(minEdge);
	 * unsortedPrimAlgActions++; chosenNodes.add(minEdge.getOtherNode(cur));
	 * unsortedPrimAlgActions++; }
	 * 
	 * } }//while return chosenEdges; }
	 */

//	=============================
// 		Pensionerade funktioner
//	=============================

	/*
	 * Bellas AdjacencyMatrix Skapar en jämn matris för alla noder i grafen
	 */
	/*
	 * int[][] generateAdjacencyMatrix() { String letterTest =
	 * "abcdefghijklmnopqrstuvwxyz"; int x = 0;
	 * 
	 * int[][] matrix = new int[nodesGenerated.length][nodesGenerated.length];
	 * 
	 * 
	 * // Loop through all rows for (int i = 0; i < nodesGenerated.length; i++) { //
	 * Loop through all elements of current row matrixActions++; for (int j = 0; j <
	 * nodesGenerated[i].edges.size(); j++) { matrixActions++; x =
	 * letterTest.indexOf(
	 * nodesGenerated[i].edges.get(j).getOtherNode(nodesGenerated[i]).toString());
	 * matrixActions++; matrix[i][x] = 1; matrixActions++; } } matrixActions++;
	 * return matrix; }
	 */

}