
package es.jonatantierno.scrumdailytimer;

/**
 * This interface represents the Chrono User Interface, and allows to manage it.
 * 
 * @author root
 */
public interface ChronoInterface {

    /**
     * Sets the time in the chrono View
     * 
     * @param i time to show, in seconds.
     */
    void setTime(int i);

    /**
     * Sets the timer
     * 
     * @param prettyTime
     */
    void setDailyTimer(String prettyTime);

    /**
     * Call when countdown expires
     */
    void timeOut();

    /**
     * Sets time for the countdown of the current participant.
     * 
     * @param string time to show.
     */
    void setCountDown(String prettyTime);

}
