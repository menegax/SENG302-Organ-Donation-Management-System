#GUI Base Touch Controls Test

##Preconditions

1. The device used to launch the application has or is connected to a touch interface.

###Zoom in on a scene

1. A stage is open in the application
2. The user performs a two-finger pinch gesture moving outwards
3. The scene and the stage are enlarged as they are zoomed into

###Zoom out on a scene

1. A stage is open in the application
2. The user performs a two-finger pinch gesture moving inwards
3. The scene and the stage's sizes are decreased as they are zoomed out of

###Rotate a scene

1. A stage is open in the application
2. The user performs a two-finger gesture of the fingers moving in a circular direction
3. The scene is rotated in the direction of the gesture (clockwise or anti-clockwise)

###Scroll across a scene

1. A stage is open in the application
2. The user performs a one-finger scrolling gesture in an upwards direction
3. The scene is moved upwards
4. The user performs a one-finger scrolling gesture in a downwards direction
5. The scene is moved downwards
6. The user performs a one-finger scrolling gesture moving to the left
7. The scene is moved to the left
8. The user performs a one-finger scrolling gesture moving to the right
9. The scene is moved to the right


###Close a touch pane that is not the root pane
1. At least two panes are visible in the application pane (i.e. a clinician search pane and a searched patient pane)
2. The "File" menu is selected in the opened patient profile pane
3. The "Close window" option is selected
4. The patient profile is closed, and the search pane is left open

###Close a touch pane that is the root pane
1. The main home pane for the logged in user is visible.
2. The "File" menu is selected in the opened patient profile pane
3. The "Close window" option is selected
4. An error message is shown in the status bar explaining the root window can not be closed

###Open the system keyboard
1. A pane is visible, and the system keyboard is not showing
2. The "Window" menu is selected in the opened patient profile pane
3. The "Open keyboard" option is selected
4. The system keyboard is opened and visible on screen

###Open the built-in keyboard
1. A pane that contains an editable text field is shown
2. The user taps on the editable text field
3. A built-in keyboard appears for the text field
4. Input in the built-in keyboard is entered into the text field

###Open multiple built-in keyboards in multiple focus areas
1. Two separate panes that contain an editable text field are shown
2. The user taps on the editable text field in one pane
3. A built-in keyboard appears for the text field
4. The user taps on the editable text field in the second pane
5. The built in keyboard appears for the second pane, and the first keyboard is still visible
6. Input for each keyboard only appears in the linked text field


##Test History

###14/8/2018 - Maree Palmer

Zoom in on a scene - pass

Zoom out on a scene - pass

Rotate a scene - pass

Scroll across a scene - pass

Close a touch pane that is not the root pane - pass

Close a touch pane that is the root pane - pass

Open the system keyboard - pass

Open the built-in keyboard - pass

Open multiple built-in keyboards in multiple focus areas - pass

###14/8/2018 - Kyle Lamb

Zoom in on a scene - pass

Zoom out on a scene - pass

Rotate a scene - pass

Scroll across a scene - pass

Close a touch pane that is not the root pane - pass

Close a touch pane that is the root pane - pass

Open the system keyboard - pass

Open the built-in keyboard - pass

Open multiple built-in keyboards in multiple focus areas - pass