package findRoute;

import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) {
        
        Station[] edges = {            
            new Station(0, 1), new Station(0, 2), new Station(0, 4),
            new Station(1, 0), new Station(1, 3), new Station(1, 5),
            new Station(2, 0), new Station(2, 3),
            new Station(3, 1), new Station(3, 2),
            new Station(4, 0), new Station(4, 5),
            new Station(5, 1), new Station(5, 4), new Station(5, 6),
            new Station(6, 5)
        };
        
        Graph g = new Graph(edges);
        g.calculateShortestDistances();
        ArrayList result = g.getResult(6);
        for(int i = 0; i < result.size(); i++)
            System.out.println((result.get(i)));
    }
}