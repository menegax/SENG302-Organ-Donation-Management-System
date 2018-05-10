package service;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import static utility.UserActionHistory.userActions;


/**
 * Class to watch the text fields, the usage for this class is to know when a method can be called,
 * once a user has finished typing. Rather than making an API call for every letter entered, we would rather make one call
 * everytime the user has finished typing. Look to GUIPatientMedications for an example.
 */
public class TextWatcher {

    private Timer timer = new Timer();

    /**
     *  Starts timer with a delay of 300ms, after delay has been exceeded, given method is called
     * @param method - method to be called after successful timer delay
     * @param classInstance - the class instance from where the timer is called
     */
    public void afterTextChange(java.lang.reflect.Method method, Object classInstance){

        timer.schedule(new TimerTask() { //create new timer task
            @Override
            public void run() {
                try {
                    method.invoke(classInstance,null); //invoke the method with the given class instance
                    timer.cancel(); //CANCEL TIMER THREAD!!!
                } catch (IllegalAccessException | InvocationTargetException e) {
                    userActions.log(Level.SEVERE, "Could not invoke method from timer");
                }
            }
        }, 500); // delay 500ms before executing timer task
    }

    /**
     * Resets the timer. Called when user is still typing, that way method is not invoked
     */
    public void onTextChange() {
        timer.cancel(); //cancel timer
        timer = new Timer(); //create new instance of a timer
    }
}
