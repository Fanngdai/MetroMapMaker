package findRoute;

import java.util.ArrayList;

public class StationNode {
    private int distanceFromSource = Integer.MAX_VALUE;
    private boolean visited;
    // now we must create edges
    private ArrayList<Station> edges = new ArrayList<>();

    public int getDistanceFromSource() {
        return distanceFromSource;
    }
    public void setDistanceFromSource(int distanceFromSource) {
        this.distanceFromSource = distanceFromSource;
    }
    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    public ArrayList<Station> getEdges() {
        return edges;
    }
    public void setEdges(ArrayList<Station> edges) {
        this.edges = edges;
    }
}