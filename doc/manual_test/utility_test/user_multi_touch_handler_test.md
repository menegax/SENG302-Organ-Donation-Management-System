# User Multi Touch Handler Test

### Before every test:
1. Run application with the argument 'touch'.

### Test 1. Can move window pane
1. Touch and move with single finger inside of the log in pane.
2. EXPECTED: Pane moves along with your finger.

### Test 2. Can rotate pane
1. Touch log in pane with 2 fingers.
2. Keep one finger stationary while moving the other around the stationary finger.
3. EXPECTED: The pane will rotate in the direction of the moving finger.

### Test 3. Can rotate pane 2
1. Touch log in pane with 2 fingers.
2. Rotate both fingers around clockwise as if spinng around a centre point between your two fingers.
3. EXPECTED: The pane will rotate in the direction of the moving finger.

### Test 4. Can zoom pane
1. Touch log in pane with 2 fingers.
2. Move a single finger toward your other finger.
3. EXPECTED: The pane will grow larger as to zoom in.
5. Move a single finger away from the other finger.
6. EXPECTED: The pane will shrink as to zoom out.

### Test 5. Can zoom pane2
1. Touch log in pane with 2 fingers.
2. Move both fingers toward each other.
3. EXPECTED: The pane will grow larger as to zoom in.
5. Move both fingers away from each other.
6. EXPECTED: The pane will shrink as to zoom out.

### Test 6. Can move multiple pane's simultaneously
1. Log in as an administrator and this is pane 1.
2. Select the `Search Users` tab.
3. Double tap on one patient profile or more up to a maximum of four.
4. For each profile, touch with a single finger.
5. Move every finger in any direction.
6. EXPECTED: All panes move simultaneously and independently.

### Test 7. Can rotate multiple pane's simultaneously
1. Log in as an administrator and this is pane 1.
2. Select the `Search Users` tab.
3. Double tap on one patient profile or more up to a maximum of four.
4. For each profile, touch with two fingers.
5. Do the rotation gesture for each pane touched.
6. EXPECTED: All panes touched will rotate simultaneously and independently.

### Test 8. Can Zoom on multiple pane's simultaneously
1. Log in as an administrator and this is pane 1.
2. Select the `Search Users` tab.
3. Double tap on one patient profile or more up to a maximum of four.
4. For each profile, touch with 2 fingers.
5. Do the zoom gesture for each pane touched.
6. EXPECTED: All panes touched will zoom simultaneously and independently.

### Test 9. Pane's are screen wrapped in all directions
1. Touch and swipe with single finger inside of the log in pane from one edge of the screen to the other, releasing before you stop.
2. EXPECTED: Pane moves along in the direction of the swipe and when it reaches the edge of the screen, it appears on the opposite edge.

### Test 10. Pane's have minimal zoom
1. Touch log in pane with 2 fingers.
2. Do the zoom out gesture.
3. Repeat until pane does no longer zoom out.
5. EXPECTED: Pane is zoomed out to the maximum and zoom out gesture no longer has any effect.

### Test 11. Pane's have momentum and slow down over time
1. Touch and swipe with single finger inside of the log in pane from one edge of the screen to the other, releasing before you stop.
2. EXPECTED: Pane moves along in the direction of the swipe, over time loses it's momentum and comes to a stop.

### Test 12. Can scroll when touch inside the list views
1. Log in as an administrator and this is pane 1.
2. Select the `Search Users` tab.
3. Ensure there are enough users to scroll the table.
4. Place one finger within the table bounds and move it upwards (to scroll down).
5. EXPECTED: Table view scrolls down.

### Test 13. Pane's will not move when list view is touched
1. Log in as an administrator and this is pane 1.
2. Select the `Search Users` tab.
3. Ensure there are enough users to scroll the table.
4. Place one finger within the table bounds and move it upwards (to scroll down).
5. EXPECTED: The pane remains unmoved.

### Test 14. TUIOFX keyboard's fully function
1. Tap on the patient NHI entry texy field.
2. EXPECTED: TUIOFX keyboard will appear.
3. Type ABC1234 using the TUIOFX keyboard.
4. EXPECTED: ABC1234 will appear in the entry text field.

### Test 15. Can open multiple TUIOFX keyboards at once
1. Log in as an administrator and this is pane 1.
2. Select the `Search Users` tab.
3. Double tap on one patient profile or more up to a maximum of four.
4. For each profile, select the `Update` tab.
5. For each profile, tap onto any text entry field.
6. EXPECTED: Multiple TUIOFX keyboards will appear.

##Test History

###20/09/2018 - Joshua and Patrick

Test 1: Pass

Test 2: Pass

Test 3: Pass

Test 4: Pass

Test 5: Pass

Test 6: Pass

Test 7: Pass

Test 8: Pass

Test 9: Pass

Test 10: Pass

Test 11: Pass

Test 12: Pass

Test 13: Pass

Test 14: Pass

Test 15: Pass - Note lags between multiple users typing at the same time.
