package metroMapMaker.data;

/**
 * This enum has the various possible states of the logo maker app
 * during the editing process which helps us determine which controls
 * are usable or not and what specific user actions should affect.
 * 
 * @author Richard McKenna
 * @author Fanng Dai
 * @version 1.0
 */
public enum MapState {
    ADD_STATION,    // To line
    REMOVE_STATION, // From line
    
    ADD_LINE,
    
    ADD_IMAGE,
    
    ADD_TEXT,
    
    SELECTING_SHAPE,
    DRAGGING_SHAPE,
    NOTHING,
    
    ADD_SHAPE,
    FUCKING_SHIT
}
