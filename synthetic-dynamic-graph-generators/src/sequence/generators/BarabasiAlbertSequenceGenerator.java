/**
 * In an initial implementation, whenever the initialization of the generator is needed, we have two options. One, when initializing the generator
 * and we want to add vertices in sequential manner we use the constructor with three parameters (initialNodes, edgesPerNode, finalNodes).
 * On the other side, when we want to initialize a sequence generator with the growth of the number of edges we use the constructor with four parameters
 * ( initalNodes, edgesPerNode, finalNodeNumber, edgeCount).
 * Keep in mind that you can work only with one kind of generator.
 * */

package sequence.generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;
import org.jgrapht.io.GraphMLExporter;
import org.jgrapht.util.SupplierUtil;

import generators.jgrapht.BarabasiAlbertGraphGeneratorMe;

public class BarabasiAlbertSequenceGenerator<V, E> implements PeriodicallyPublishableDynamicGraph<V, E> {

public static int NUMBER_OF_GENERATIONS = 1;
	
	protected boolean takeSnapshotsByEdgeAdditions;
	protected int edgesPerSnapshot;
	
	private int initalNodes;
	private int edgesPerNode;
	private int finalNodeNumber;
	
	public static SimpleGraph<String, DefaultEdge> graph;
	private ArrayList<String> vertexList;
	
	Controller controller = new Controller();
	ArrayList<Graph<String, DefaultEdge>> snapshotList = controller.getSnapshotList();
	ArrayList<Integer> graphSizes = new ArrayList<Integer>();
	
	List<String> newEndpoints_edgeCreation = new ArrayList<String>();
	
	private int EDGE_COUNT = 0;
	private final Random random;
	
	Supplier<String> vSupplier = new Supplier<String>() {
        private int id = 0;

        @Override
        public String get()
        {
            return "" + id++;
        }
    };
	
	public BarabasiAlbertSequenceGenerator(int initalNodes, int edgesPerNode, int finalNodeNumber) {
		this.takeSnapshotsByEdgeAdditions = false;
		this.initalNodes = initalNodes;
		this.edgesPerNode = edgesPerNode;
		this.finalNodeNumber = finalNodeNumber;
		this.graph = new SimpleGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
		random = new Random();
	}
	
	public BarabasiAlbertSequenceGenerator(int initalNodes, int edgesPerNode, int finalNodeNumber, int edgeCount) {
		this.takeSnapshotsByEdgeAdditions = true;
		this.edgesPerSnapshot = edgeCount;
		this.initalNodes = initalNodes;
		this.edgesPerNode = edgesPerNode;
		this.finalNodeNumber = finalNodeNumber;
		this.graph = new SimpleGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
		random = new Random();
	}
/** In this method whenever we need to initialize the graph we just pass any integer in the method. Since we are implementing methods from the interface
 * we need to override this as well. **/
	
	@Override
	public void initialize(int initialNodes) {	
		BarabasiAlbertGraphGeneratorMe<String, DefaultEdge> bAFGraphGenerator = 
				new BarabasiAlbertGraphGeneratorMe<String, DefaultEdge>(initalNodes, edgesPerNode, finalNodeNumber);		
		bAFGraphGenerator.generateGraph(graph);
		Set<String> vertexSet = graph.vertexSet();
		this.vertexList = new ArrayList<String>();
		vertexList.addAll(vertexSet);
		EDGE_COUNT = this.graph.edgeSet().size();
	}

	@Override
	public int getNumberOfModifications(Graph<V, E> graph) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/** Whenever we call make snapshot we have two cases :
	 * 1- when we grow the graph with vertices we just write the number of additions when calling makeSnapshot(int numberOfModification),
	 * 2- when we grow the graph with edges we have to make sure that we write the same number that we declared in the constructor(edgeCount=numberOfModification). 
	 * **/
	
	@Override
	public void makeSnapshot(int numberOfModification) {
		if(takeSnapshotsByEdgeAdditions) {			
			if(!snapshotList.isEmpty()) {
				printGraphForRelease(snapshotList.get(0));
				snapshotList.remove(0);
			}else {
				growGraphWithEdges();
				printGraphForRelease(snapshotList.get(0));
				snapshotList.remove(0);
			}
		}else {
			growGraphWithVertex(numberOfModification);
			printGraphForRelease(graph);
		}		
	}
	
	protected void printGraphForRelease(Graph<String, DefaultEdge> graph) {
		//Graph<V, E> graph = snapshotList.get(0);
		GraphExporter<String, DefaultEdge> exporter = new DOTExporter<>();
		
		//GraphExporter<V, E> exporterGraphML=new GraphMLExporter<>();
		Writer writer = new StringWriter();
	    try {
			exporter.exportGraph(graph, writer);
			//exporterGraphML.exportGraph(graph, writer);
		} catch (ExportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //System.out.println(writer.toString());
	    PrintStream o;
		try {
			if(takeSnapshotsByEdgeAdditions) {
				o = new PrintStream(new File("Generations/BarabasiAlbert(Edges)-Snapshot_" + NUMBER_OF_GENERATIONS +".txt"));
			}else {
				o = new PrintStream(new File("Generations/BarabasiAlbert(Vertices)-Snapshot_" + NUMBER_OF_GENERATIONS +".txt"));
			}
			
			System.setOut(o);
			System.out.println(writer.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/////////////////////////////////////////////////////////////////////////////////////////////
		GraphExporter<String, DefaultEdge> exporterGraphML=new GraphMLExporter<>();

		Writer writergml = new StringWriter();
	    try {
			
			exporterGraphML.exportGraph(graph, writergml);
		} catch (ExportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //System.out.println(writer.toString());
	    PrintStream oGML;
		try {
			if(takeSnapshotsByEdgeAdditions) {
				oGML = new PrintStream(new File("GenerationsGML/BarabasiAlbert(Edges)-Snapshot_" + NUMBER_OF_GENERATIONS +".txt"));
			}else {
				oGML = new PrintStream(new File("GenerationsGML/BarabasiAlbert(Vertices)-Snapshot_" + NUMBER_OF_GENERATIONS +".txt"));
			}
			
			System.setOut(oGML);
			System.out.println(writergml.toString());
			NUMBER_OF_GENERATIONS++;
			
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
	}
	
	private void growGraphWithVertex(int numberOfModification) {		
		Set<String> nodeSet = graph.vertexSet();
        List<String> nodes = new ArrayList<String>();
        nodes.addAll(nodeSet);
		
		for (int i = 0; i < numberOfModification; i++) {
            String v = graph.addVertex();

            List<String> newEndpoints = new ArrayList<>();
            int added = 0;
            while (added < edgesPerNode) {
                String u = nodes.get(random.nextInt(nodes.size()));
                if (!graph.containsEdge(v, u)) {
                	graph.addEdge(v, u);
                    added++;
                    newEndpoints.add(v);
                    if (i > 1) {
                        newEndpoints.add(u);
                    }
                }
            }
            nodes.addAll(newEndpoints);
        }
    }
	
	private void growGraphWithEdges() {
		Set<String> nodeSet = graph.vertexSet();
        List<String> nodes = new ArrayList<String>();
        nodes.addAll(nodeSet);
        
		boolean condition = true;
		while(condition) {			
	        String v = graph.addVertex();
	
	        int added = 0;
	        while (added < edgesPerNode) {
	            String u = nodes.get(random.nextInt(nodes.size()));
	            if (!graph.containsEdge(v, u)) {
	            	graph.addEdge(v, u);
	                added++;
	                newEndpoints_edgeCreation.add(v);
	                if (graph.edgeSet().size() == EDGE_COUNT + this.edgesPerSnapshot) {
	            		controller.add(Utils.cloneGraph(graph));
	            		graphSizes.add((Integer)graph.edgeSet().size());
	            		condition = false;
	            	}else if(graph.edgeSet().size() > EDGE_COUNT+this.edgesPerSnapshot) {
						int difference = graph.edgeSet().size() - (EDGE_COUNT + this.edgesPerSnapshot);
						if(difference == this.edgesPerSnapshot) {
							  controller.add(Utils.cloneGraph(graph));
							  graphSizes.add((Integer)graph.edgeSet().size());
						}else if(difference > this.edgesPerSnapshot){
							  if(difference % this.edgesPerSnapshot == 0) {
								  controller.add(Utils.cloneGraph(graph));
								  graphSizes.add((Integer)graph.edgeSet().size());
							  }						 
						}else if(difference < this.edgesPerSnapshot) {
							  int additions = this.edgesPerSnapshot - difference; 
							  addEdges(additions); 
							  controller.add(Utils.cloneGraph(graph));
							  graphSizes.add((Integer)graph.edgeSet().size());
						 }
	            	}
	            }
	        }
	        nodes.addAll(newEndpoints_edgeCreation);	        			
		}		
		int a=0;
		a=+1;
    }
	
	private void addEdges(int additions) {
		Set<String> nodeSet = graph.vertexSet();
		List<String> nodes = new ArrayList<String>();
		nodes.addAll(nodeSet);
		for (int i = 0; i < additions; i++) {
			String v = graph.addVertex();
			String u = nodes.get(random.nextInt(nodes.size()));

			if (!graph.containsEdge(v, u)) {
				graph.addEdge(v, u);
				newEndpoints_edgeCreation.add(v);
			}
		}
	}
}
