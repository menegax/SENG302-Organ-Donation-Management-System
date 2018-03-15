# CLI command line action history

##1
1. On initial application start up -> check that no commands display when pressing UP/DOWN
2. Enter "-h" and press enter. Check that only "-h" displays by pressing UP. Pressing down should result in a blank line.
3. Enter "-h", "donor view -a" (any seq of commands). Pressing UP will cycle through previous commands. 
Pressing DOWN will cycle through to the most recent command, then result in blank line

##2
1. Enter the “donor add” command into the terminal window
2. Press up to select any of this command from history
3. Edit the command to “donor add -h” using the left/right arrows and/or backspace
4. Press enter to submit the command
5. The program should react accordingly by showing the help response for the donor add command rather than trying to execute the “donor add” command itself