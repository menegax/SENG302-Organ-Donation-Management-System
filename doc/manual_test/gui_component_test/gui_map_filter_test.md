# GUI Map Test

### Preconditions 1
1. The application is running in desktop mode
2. The user logs in as a clinician
3. The user selects the Search Patients tab
4. The user selects the View on Map button with the default first thirty patients
5. The map is populated with the default first thirty users

### Test 1 - Add filter on desktop
1. Preconditions 1 are met
2. The Filter Map button is selected
3. The user clicks on one point on the screen
4. The user clicks on another point on the screen
5. A rectangle appears on screen with opposite corners on the clicked points, and only markers within the rectangle are shown.

### Test 2 - Remove filter on desktop
1. Preconditions 1 are met
2. A filter has been applied to the map
3. The user selects the Clear Filters button
4. The filter rectangle is removed along with the filter
5. All thirty patients are shown without filters applied

### Test 3 - Change filter on map on desktop
1. Preconditions 1 are met
2. A filter has been applied to the map
3. The Filter Map button is selected
4. The filter rectangle is removed along with the filter
5. All thirty patients are shown without filters applied
6. The user clicks on one point on the screen
7. The user clicks on another point on the screen
8. A rectangle appears on screen with opposite corners on the clicked points, and only markers within the rectangle are shown.

### Preconditions 2
1. The application is running in touch mode
2. The user logs in as a clinician
3. The user selects the Search Patients tab
4. The user selects the View on Map button with the default first thirty patients
5. The map is populated with the default first thirty users

### Test 4 - Add filter on touch
1. Preconditions 2 are met
2. The Filter Map button is selected
3. The user touches one point on the screen
4. The user touches another point on the screen
5. A rectangle appears on screen with opposite corners on the touched points, and only markers within the rectangle are shown.

### Test 5 - Remove filter on touch
1. Preconditions 2 are met
2. A filter has been applied to the map
3. The user selects the Clear Filters button
4. The filter rectangle is removed along with the filter
5. All thirty patients are shown without filters applied

### Test 6 - Change filter on map on touch
1. Preconditions 2 are met
2. A filter has been applied to the map
3. The Filter Map button is selected
4. The filter rectangle is removed along with the filter
5. All thirty patients are shown without filters applied
6. The user touches one point on the screen
7. The user touches another point on the screen
8. A rectangle appears on screen with opposite corners on the touched points, and only markers within the rectangle are shown.

## Test History

### 26/09/18 - Maree

Preconditions 1 : Pass 

Test 1 : Pass

Test 2 : Pass

Test 3 : Pass

Preconditions 2 : Pass

Test 4 : Pass

Test 5 : Pass

Test 6 : Pass