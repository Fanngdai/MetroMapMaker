package findRoute;

public class Station {
    private final int fromNodeIndex;
    private final int toNodeIndex;

    public Station (int fromNodeIndex, int toNodeIndex) {
        this.fromNodeIndex = fromNodeIndex;
        this.toNodeIndex = toNodeIndex;
    }
    public int getFromNodeIndex() {
        return fromNodeIndex;
    }
    public int getToNodeIndex() {
        return toNodeIndex;
    }

    // determines the neighbouring node of a supplied node, based on the two nodes connected by this edge
    public int getNeighbourIndex(int nodeIndex) {
        if (this.fromNodeIndex == nodeIndex) {
            return this.toNodeIndex;
        } else {
            return this.fromNodeIndex;
        }
    }
}