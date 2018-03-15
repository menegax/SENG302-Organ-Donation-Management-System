# CLI command line action history
1. On initial application start up -> check that no commands display when pressing UP/DOWN
2. Enter "-h" and press enter. Check that only "-h" displays by pressing UP. Pressing down should result in a blank line.
3. Enter "-h", "donor view -a" (any seq of commands). Pressing UP will cycle through previous commands. 
Pressing DOWN will cycle through to the most recent command, then result in blank line