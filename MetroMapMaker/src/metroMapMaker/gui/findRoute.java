package metroMapMaker.gui;

import djf.AppTemplate;
import djf.ui.AppAlertDialogSingleton;
import findRoute.Graph;
import findRoute.Station;
import java.util.ArrayList;
import metroMapMaker.data.DraggableStation;
import metroMapMaker.data.MapData;
import metroMapMaker.data.MetroLine;

/**
 *
 * @author fannydai
 */
public class findRoute {
    private final MapData dataManager;
    
    public findRoute(AppTemplate initApp) {
        dataManager = (MapData)initApp.getDataComponent();
    }
    
    /**
     * Finds the shortest path and opens singleton for it.
     * 
     * @param from
     *  Where you from?
     * @param to 
     *  must be at position 0
     */
    public void showDialog(String from, String to){
        AppAlertDialogSingleton dialog = AppAlertDialogSingleton.getSingleton();
        
        ArrayList<MetroLine> metroLines = dataManager.getLines();
        ArrayList<DraggableStation> dragStation = dataManager.getStations();
        ArrayList<Station> edges = new ArrayList<>();
        
        DraggableStation toStation = dataManager.findMetroStation(to);
        DraggableStation fromStation = dataManager.findMetroStation(from);
        if(toStation == null || fromStation == null)
            return;
        
        dragStation.remove(toStation);
        // Add station to first station
        dragStation.add(0, toStation);
        int indexOfFromStation = dragStation.indexOf(fromStation);
        
        boolean seenTo = false;
        boolean seenFrom = false;
        
        // For circular stations
        for(MetroLine line: metroLines){
            if(line.getCircular() && line.getStations().size() > 1){
               DraggableStation station1 = line.getFirstDraggableStation();
               DraggableStation station2 = line.getLastDraggableStation();
               
               int s1 = dragStation.indexOf(station1);
               int s2 = dragStation.indexOf(station2);
               
               // For circular stations.
               edges.add(new Station(s1, s2));
               edges.add(new Station(s2, s1));
            }
            
            for(int i=0; i<line.size(); i++){
                breakFromAdding:
                if(line.get(i) instanceof DraggableStation){
                    for(int j=i+1; j<line.size(); j++){
                        if(line.get(j) instanceof DraggableStation){
                            
                            int indexOfFirst = dragStation.indexOf(line.get(i));
                            int indexOfLast = dragStation.indexOf(line.get(j));
                            
                            if(indexOfFirst == 0 || indexOfLast == 0){
                                seenTo = true;
                            }
                            if(indexOfFromStation == indexOfFirst || indexOfFromStation == indexOfLast){
                                seenFrom = true;
                            }
                            
                            if(!edges.contains(new Station(indexOfFirst, indexOfLast))){
                                edges.add(new Station(indexOfFirst, indexOfLast));
                                edges.add(new Station(indexOfLast, indexOfFirst));
                            }
                            break breakFromAdding;
                        }
                    }
                }
            }
        }

        // If the station is not on a line.
        if(!seenTo || !seenFrom){
            dialog.show("Route" , "Route from " + from + " to " + to, "No possible result!");
            return;
        }
        
        Station[] edge = new Station[edges.size()];
        edge = (Station[]) edges.toArray(edge);
        
        Graph g = new Graph(edge);
        g.calculateShortestDistances();
        // Where we want to go from
        ArrayList<Integer> numResult = g.getResult(indexOfFromStation);
        String result = "";
        
        for(int i = 0; i < numResult.size(); i++){
            result += (dragStation.get(numResult.get(i)).getName()) + "\n\n";
        }
        
        dialog.show("Route" , "Route from " + from + " to " + to, result.trim());
    }
}
