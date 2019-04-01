package sequence.generators;

import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class Controller {

	protected ArrayList<Graph<String, DefaultEdge>> snapshotList = new ArrayList<Graph<String, DefaultEdge>>();
	protected static int edgesCounter = 0;
	
	public Controller(){
	}
	
	public void add(Graph<String, DefaultEdge> graph) {
		snapshotList.add(graph);
	}
	
	public ArrayList<Graph<String, DefaultEdge>> getSnapshotList(){
		return snapshotList;
	}
}
