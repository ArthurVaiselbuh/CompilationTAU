package RegisterAllocation;

import Globals.Globals;

import java.util.*;


class InterferenceGraph
{
	private int numOfNodes;//number of nodes in the graph = number of temps we generated
	private LinkedList<Integer> adjacencyMatrix[]; // infinite_temp_num -> list of neighbors
    public HashMap<Integer,Integer> colorAssignemnts =null;// infinite_temp_num-> real target register (indices)

	InterferenceGraph(int numOfNodes){
		this.numOfNodes = numOfNodes;
		colorAssignemnts = new HashMap<>();
		adjacencyMatrix = new LinkedList[numOfNodes];
		for (int i = 0; i< numOfNodes; i++)
			adjacencyMatrix[i] = new LinkedList();
	}
	void addInterference(int v, int w)
	{
		if (!adjacencyMatrix[v].contains(w))
			adjacencyMatrix[v].add(w);
		if (!adjacencyMatrix[w].contains(v))
			adjacencyMatrix[w].add(v);
	}

	// k-color all vertices, preventing neighboring nodes having the same color.
	// Greedy coloring.
	//This method fills the field colorAssignemnts with the assigned colors
	public void colorGraph()
	{
		//color per vertex
		int coloring[] = new int[numOfNodes];


		//initially all nodes are uncolored
		for (int i = 0; i < coloring.length; i++) {
			//-1 : unassigned.
			coloring[i] = -1;
		}
		// Color first vertex:
		coloring[0] = 0;
		for (int v = 1; v < numOfNodes; v++)
		{
			coloring[v] = getAvailableColor(v, coloring);
		}
		//add the coloring
		int maxIdx = 0;
		for (int u = 0; u < numOfNodes; u++){
			if(maxIdx < coloring[u]){
				maxIdx = coloring[u];
			}
			colorAssignemnts.put(u,coloring[u]);
		}
		if (maxIdx > 8){
			//TODO: remove this error
			Globals.debugError("Failed to allocate registers");
		}
	}

	private int getAvailableColor(int v, int[] curColoring){
		//return available color for vertex #v
		// Used during coloring
		// currently available colors:
		boolean availableColors[] = new boolean[numOfNodes];
		// initially, all colors are available
		Arrays.fill(availableColors, true);
		//mark all unavailable colors
		for (int i:adjacencyMatrix[v]){
			if (curColoring[i] != -1){
				availableColors[curColoring[i]] = false;
			}
		}
		// Find lowest available color
		int chosenColor=-1;
		for (int i = 0; i < numOfNodes; i++){
			if (availableColors[i]) {
				chosenColor = i;
				break;
			}
		}
		if (chosenColor == -1){
			//No color found! this is a problem...
			Globals.debugError(String.format("Could not find color for tmp #%d", v));
		}
		return chosenColor;
	}
}

