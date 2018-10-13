package findRoute;

import java.util.ArrayList;

// now we must create graph object and implement dijkstra algorithm
public class Graph {
    private final StationNode[] nodes;
    private final int noOfNodes;
    private final Station[] edges;
    private final int noOfEdges;

    public Graph(Station[] edges) {
        this.edges = edges;
        // create all nodes ready to be updated with the edges
        this.noOfNodes = calculateNoOfNodes(edges);
        this.nodes = new StationNode[this.noOfNodes];
        for (int n = 0; n < this.noOfNodes; n++) {
            this.nodes[n] = new StationNode();
        }
        // add all the edges to the nodes, each edge added to two nodes (to and from)
        this.noOfEdges = edges.length;
        for (int edgeToAdd = 0; edgeToAdd < this.noOfEdges; edgeToAdd++) {
            this.nodes[edges[edgeToAdd].getFromNodeIndex()].getEdges().add(edges[edgeToAdd]);
            this.nodes[edges[edgeToAdd].getToNodeIndex()].getEdges().add(edges[edgeToAdd]);
        }
    }

    private int calculateNoOfNodes(Station[] edges) {
        int noOfNode = 0;
        for (Station e : edges) {
            if (e.getToNodeIndex() > noOfNode)
                noOfNode = e.getToNodeIndex();
            if (e.getFromNodeIndex() > noOfNode)
            noOfNode = e.getFromNodeIndex();
        }
        noOfNode++;
        return noOfNode;
    }

    public void calculateShortestDistances() {
        // node 0 as source
        this.nodes[0].setDistanceFromSource(0);
        int nextNode = 0;
        // visit every node
        for (StationNode node : this.nodes) {
            // loop around the edges of current node
            ArrayList<Station> currentNodeEdges = this.nodes[nextNode].getEdges();
            for (int joinedEdge = 0; joinedEdge < currentNodeEdges.size(); joinedEdge++) {
                int neighbourIndex = currentNodeEdges.get(joinedEdge).getNeighbourIndex(nextNode);
                // only if not visited
                if (!this.nodes[neighbourIndex].isVisited()) {
                    // 1 is the length of each stop
                    int tentative = this.nodes[nextNode].getDistanceFromSource() + 1;
                    if (tentative < nodes[neighbourIndex].getDistanceFromSource()) {
                        nodes[neighbourIndex].setDistanceFromSource(tentative);
                    }
                }
            }
            // all neighbours checked so node visited
            nodes[nextNode].setVisited(true);
            // next node must be with shortest distance
            nextNode = getNodeShortestDistanced();
        }
  }

    // now we're going to implement this method in next part !
    private int getNodeShortestDistanced() {
        int storedNodeIndex = 0;
        int storedDist = Integer.MAX_VALUE;
        for (int i = 0; i < this.nodes.length; i++) {
        int currentDist = this.nodes[i].getDistanceFromSource();
            if (!this.nodes[i].isVisited() && currentDist < storedDist) {
                storedDist = currentDist;
                storedNodeIndex = i;
            }
        }
        return storedNodeIndex;
    }

    // display result
    public ArrayList<Integer> getResult(int i) {
        ArrayList<Integer> result = new ArrayList<>();

        int temp = nodes[i].getDistanceFromSource();
        StationNode selectedNode = nodes[i];
        
        for(int j = 0; j <= nodes[i].getDistanceFromSource(); j++){
            valueBreak:
            for(int k = 0; k < selectedNode.getEdges().size(); k++){
                StationNode tempNode = nodes[selectedNode.getEdges().get(k).getFromNodeIndex()];
                if(tempNode.getDistanceFromSource() == temp){
                    result.add(selectedNode.getEdges().get(k).getFromNodeIndex());
                    selectedNode = nodes[selectedNode.getEdges().get(k).getFromNodeIndex()];
                    temp--;
                    break valueBreak;
                }
            }
        }
        
        return result;
    }

    public StationNode[] getNodes() {
        return nodes;
    }
    public int getNoOfNodes() {
        return noOfNodes;
    }
    public Station[] getEdges() {
        return edges;
    }
    public int getNoOfEdges() {
        return noOfEdges;
    }
}