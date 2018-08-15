package controller;

interface IWindowObserver {

    /**
     * Called when a window shown by this controller through screen control is closed
     */
    void windowClosed();
}
