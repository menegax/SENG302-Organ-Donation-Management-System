var map, mapBridge, successCount;
var patients = [];
var circles = [];
var markers = [];
var infoWindows = [];
var failedPatientArray = [];
var markerSetId = 0;
var filterByAreaListener, filterStart, filterEnd;
var filterAreaSet = false;
var potentialMatches = [];
var donations = [];
var currentMarker;
var currentOrgan;
var donorPatientNhi;
var receiverPatientNhi;
var NORTHBOUND = -33;
var SOUTHBOUND = -48;
var EASTBOUND = 180;
var WESTBOUND = 165;
var rectangle = [];
var donorPatientNhi;
var receiverPatientNhi;
var dropDownDonations = [];
var isViewingPotentialMatches = false;
var originalZoom;
var defaultZoom = 6;
var defaultCenterPos = {lat: -40.59225, lng: 173.51012};
var iconBase = '../image/markers/';
var icons = {
    deadDonor: {
        name: 'Dead Donor', icon: iconBase + 'orange.png'
    },
    deceased: {
        name: 'Deceased', icon: iconBase + 'blue.png'
    }, alive: {
        name: 'Alive', icon: iconBase + 'green.png'
    }
};

var isViewingPotentialMatches = false;

/**
 * Initialize method
 */
function init() {
    setUpMap();
    setUpLegend(icons);
    setUpViewAvailableOrgansButton();
    setUpFilterAreaButton();
    setUpFilterClearAreaButton();
    setUpResetMapButton();
}

/**
 * Resets the map
 */
function resetMap() {
    centerAndZoomMap();
    clearFilterArea();
    clearMarkers();
    clearCircles();
    clearRectangle();
    hideGenericNotification();
    hideNotification()
}

/**
 * Sets the map to default center position and zoom
 */
function centerAndZoomMap() {
    map.setCenter(defaultCenterPos); //defs good
    map.setZoom(defaultZoom);
}

/**
 * Sets up the map with styling and etc.
 */
function setUpMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: defaultCenterPos,
        zoom: defaultZoom,
        minZoom: 6, //zooming out, lower, the further you can zoom out
        maxZoom: 12,
        disableDefaultUI: true,
        scaleControl: true,
        zoomControl: true,
        heading: 90,
        tilt: 45,
        clickableIcons: false,
        mapTypeId: 'roadmap',
        gestureHandling: 'cooperative',
        styles: getMapCustomStyle()
    });
}

/**
 * Sets up the reset map button with a listener
 */
function setUpResetMapButton() {
    google.maps.event.addListenerOnce(map, 'idle', function () {
        document.getElementById('resetMapBtn').addEventListener('click', function () {
            resetMap();
            showGenericNotification('Map reset');
        });
    });
}

/**
 * Sets up the filter and clear area button with a listener
 */
function setUpFilterClearAreaButton() {
    // clear filter area button
    google.maps.event.addListenerOnce(map, 'idle', function () {
        document.getElementById('clearFilterAreaBtn').addEventListener('click', function () {
            clearFilterArea();
            clearRectangle();
            showGenericNotification('Area filters have been cleared');
        });
    });
}

/**
 * Sets up the filter  area button with a listener
 */
function setUpFilterAreaButton() {
    // filter area button
    google.maps.event.addListenerOnce(map, 'idle', function () {
        document.getElementById('filterAreaBtn').addEventListener('click', function () {
            clearRectangle();
            clearFilterArea();
            if (filterStart === undefined) {
                showGenericNotification('Press two points on the map to filter');
            }
            filterByAreaListener = google.maps.event.addListener(map, 'click', function (e) {
                if (filterStart === undefined) {
                    filterStart = e.latLng;
                    showGenericNotification('Press a second point on the map');
                }
                else {
                    filterEnd = e.latLng;
                    filterArea({start: filterStart, end: filterEnd});
                    google.maps.event.removeListener(filterByAreaListener);
                    filterByAreaListener = undefined;
                    makeAndAttachFilterRectangle();
                    $('#filterAreaBtn').hide();
                    $('#clearFilterAreaBtn').show();
                }
            });
        });
    });
}

/**
 * Sets up the view available organs button with a listener
 */
function setUpViewAvailableOrgansButton() {
    // view available organs button
    google.maps.event.addListenerOnce(map, 'idle', function () {
        setMapDragEnd();
        document.getElementById('availableOrgansView').addEventListener('click', function () {
            resetMap();
            failedPatientArray = [];
            markers.forEach(function (marker) {
                marker.setMap(null);
            });
            markers = [];
            patients = mapBridge.getAvailableOrgans();
            successCount = 0;
            markerSetId++;
            addMarkers(patients.size(), markerSetId);
            showGenericNotification('Populating map...');
        });
        document.getElementById('cancelAssignmentBtn').addEventListener('click', function () {
            isViewingPotentialMatches = false;
            mapBridge.populateLastSetOfPatients();
            $('#cancelAssignmentBtn').hide();
            $('#dropdown').prop('disabled', false);
        });
    });
}

/**
 * Gets patients who are within the area and resets the markers on the map to be them
 */
function filterArea(area) {
    filterAreaSet = true;
    markers.forEach(function (marker) {
        if (!isPatientInArea(marker, area)) {
            marker.setMap(null);
        }
        else {
            marker.setMap(map);
        }
    });
}

/**
 * Sets up the map legend
 */
function setUpLegend() {

    var legend = document.getElementById('legend');
    for (var key in icons) {
        var type = icons[key];
        var name = type.name;
        var icon = type.icon;
        var div = document.createElement('div');
        div.innerHTML = name + '<img src="' + icon + '" style="float: right; margin-top: 3px">';
        legend.appendChild(div);
    }
}

/**
 * Reconnects every marker back to the map
 */
function clearFilterArea() {
    if (filterByAreaListener !== undefined) {
        google.maps.event.removeListener(filterByAreaListener);
        filterByAreaListener = undefined;
    }

    filterAreaSet = false;

    markers.forEach(function (marker) {
        marker.setMap(map);
    });

    $('#clearFilterAreaBtn').hide();
    $('#filterAreaBtn').show();
}

/**
 * Finds out if a patient is within a given area
 * @param marker the marker to test
 * @param area the area bounds
 * @returns {boolean} if the patient is inside or outside the area
 */
function isPatientInArea(marker, area) {

    var minLng, minLat, maxLng, maxLat;
    var current = marker.position;

    if (area.start.lng() > area.end.lng()) {
        maxLng = area.start.lng();
        minLng = area.end.lng();
    }
    else {
        maxLng = area.end.lng();
        minLng = area.start.lng();
    }

    if (area.start.lat() > area.end.lat()) {
        maxLat = area.start.lat();
        minLat = area.end.lat();
    }
    else {
        maxLat = area.end.lat();
        minLat = area.start.lat();
    }

    if ((minLng < 0 && maxLng < 0) || (minLng > 0 && maxLng > 0)) { // not crossing lng border from -179 to 179
        if (current.lng() < minLng) {
            return false;
        }
        else if (current.lat() < minLat) {
            return false;
        }
        else if (current.lng() > maxLng) {
            return false;
        }
        else if (current.lat() > maxLat) {
            return false;
        }
    }
    else {
        if (current.lng() > minLng && current.lng() < maxLng) {
            return false;
        }
        else if (current.lat() < minLat) {
            return false;
        }
        else if (current.lat() > maxLat) {
            return false;
        }
    }

    return true;
}

/**
 * validates that the filter option touches have taken geolocations within the max bounds of nz, otherwise sets the
 * out of bounds lat or lng to the max
 */
function validateFilterBounds() {
    if (filterStart.lng() < WESTBOUND) {
        if (filterStart.lng() < 0) {
            filterStart = new google.maps.LatLng({lat: filterStart.lat(), lng: EASTBOUND})
        }
        else {
            filterStart = new google.maps.LatLng({lat: filterStart.lat(), lng: WESTBOUND})
        }
    }
    if (filterEnd.lng() < WESTBOUND) {
        if (filterEnd.lng() < 0) {
            filterEnd = new google.maps.LatLng({lat: filterEnd.lat(), lng: EASTBOUND});
        }
        else {
            filterEnd = new google.maps.LatLng({lat: filterEnd.lat(), lng: WESTBOUND});
        }
    }
    if (filterStart.lat() > NORTHBOUND) {
        filterStart = new google.maps.LatLng({lat: NORTHBOUND, lng: filterStart.lng()});
    }
    else if (filterStart.lat() < SOUTHBOUND) {
        filterStart = new google.maps.LatLng({lat: SOUTHBOUND, lng: filterStart.lng()});
    }
    if (filterEnd.lat() > NORTHBOUND) {
        filterEnd = new google.maps.LatLng({lat: NORTHBOUND, lng: filterEnd.lng()});
    }
    else if (filterEnd.lat() < SOUTHBOUND) {
        filterEnd = new google.maps.LatLng({lat: SOUTHBOUND, lng: filterEnd.lng()});
    }
}

/**
 * Calculates the north, south, east, west bounds for the google maps rectangle with the start and end filter touch
 * geolocations
 * @returns - Bounds in the format: {{north: *, south: *, east: *, west: *}}
 */
function getFilterRectangleBounds() {
    var north, south, east, west;
    if (filterStart.lng() > filterEnd.lng()) {
        if (filterStart.lat() > filterEnd.lat()) {
            // filterStart: top right & filterEnd: bottom left
            north = parseFloat(filterStart.lat());
            south = parseFloat(filterEnd.lat());
            east = parseFloat(filterStart.lng());
            west = parseFloat(filterEnd.lng());
        }
        else {
            // filterStart: bottom right & filterEnd: top left
            north = parseFloat(filterEnd.lat());
            south = parseFloat(filterStart.lat());
            east = parseFloat(filterStart.lng());
            west = parseFloat(filterEnd.lng());
        }
    }
    else {
        if (filterStart.lat() > filterEnd.lat()) {
            // filterStart: top left & filterEnd: bottom right
            north = parseFloat(filterStart.lat());
            south = parseFloat(filterEnd.lat());
            east = parseFloat(filterEnd.lng());
            west = parseFloat(filterStart.lng());
        }
        else {
            // filterStart: bottom left & filterEnd: top right
            north = parseFloat(filterEnd.lat());
            south = parseFloat(filterStart.lat());
            east = parseFloat(filterEnd.lng());
            west = parseFloat(filterStart.lng());
        }
    }
    return {north: north, south: south, east: east, west: west};
}

/**
 * Create the filtered area of the search
 */
function makeAndAttachFilterRectangle() {

    validateFilterBounds();

    // Define a rectangle and set its editable property to true.
    var filterBound = new google.maps.Rectangle({
        map: map, bounds: getFilterRectangleBounds(), strokeColor: '#c4c6c9', //from bootstrap alert-secondary
        strokeOpacity: 0.8, strokeWeight: 2, fillColor: '#e2e3e5', //from bootstrap alert-secondary
        fillOpacity: 0.35
    });
    rectangle.push(filterBound);
    google.maps.event.removeListener(filterByAreaListener);
}

/**
 * Sets the viewable area of the map
 */
function setMapDragEnd() {
    // Bounds for the World
    var allowedBounds = new google.maps.LatLngBounds(new google.maps.LatLng(-56.831005, 140.304953), new google.maps.LatLng(-22.977599, -165.689951));

    // Listen for the drag end event
    google.maps.event.addListener(map, 'dragend', function () {

        if (!allowedBounds.contains(map.getCenter())) {
            // Out of bounds - Move the map back to center
            centerAndZoomMap();
        }
    });
}

/**
 * Adds a marker to the map
 * @param patient
 */
function addMarker(patient) {
    var latLong = patient.getCurrentLocation();
    if (latLong !== null) {
        successCount++;
        var marker = makeMarker(patient, latLong); //set up markers
        if (filterAreaSet && !isPatientInArea(marker, {start: filterStart, end: filterEnd})) {
            marker.setMap(null);
        }
        makeAndAttachInfoWindow(patient, marker);
        markers.push(marker);
    }
    else {
        var index = failedPatientArray.indexOf(patient);
        if (index !== -1) {
            failedPatientArray[index] = patient;
        }
        else {
            failedPatientArray.push(patient);
        }
        console.log('Geocoding failed because: ' + status);
    }
}

/**
 * Pseudo factory which retrieves the correctly formatted marker depending on patient attributes
 * @param patient the patient to base the marker styling from
 * @param location the location of
 * @returns {google.maps.Marker} the created marker
 */
function makeMarker(patient, location) {
    var name = patient.getNameConcatenated();

    var finalLoc = new google.maps.LatLng(location.lat, location.lng);

    if (patient.isDead() && !patient.getDonations().isEmpty()) {
        return new google.maps.Marker({
            map: map, position: finalLoc, title: name, animation: google.maps.Animation.DROP, nhi: patient.getNhiNumber(), icon: icons.deadDonor.icon
        });
    }
    else if (patient.isDead()) {
        return new google.maps.Marker({
            map: map, position: finalLoc, title: name, animation: google.maps.Animation.DROP, nhi: patient.getNhiNumber(), icon: icons.deceased.icon
        });
    }
    else if (!patient.isDead()) {
        return new google.maps.Marker({
            map: map, position: finalLoc, title: name, animation: google.maps.Animation.DROP, nhi: patient.getNhiNumber(), icon: icons.alive.icon
        });
    }

}

/**
 * Creates and attaches the appropriate info window to the marker based off the patient attributes
 * @param patient the patient to base the info window off
 * @param marker the marker to attach the info window to
 */
function makeAndAttachInfoWindow(patient, marker) {
    var infoWindow;
    if (patient.isDead()) {
        infoWindow = new google.maps.InfoWindow({
            content: getDeadPatientInfoContent(patient), maxWidth: 550
        });
        buildOrganDropdown(infoWindow);
    } else if (potentialMatches !== [] && isViewingPotentialMatches) {
        infoWindow = new google.maps.InfoWindow({
            content: getPotentialMatchesContent(patient),
            maxWidth:350
        });
    } else {
        infoWindow = new google.maps.InfoWindow({
            content: getAlivePatientInfoContent(patient), maxWidth: 350
        });
    }
    mapInfoWindowToPatient(infoWindow, patient);
    marker.addListener('click', function () { // when clicking on the marker, all other markers' info windows close
        currentMarker = marker;
        if (!isViewingPotentialMatches) {
            currentOrgan = undefined;
        }
        infoWindows.forEach(function (iw) {
            if (iw["iwindow"] !== infoWindow) {
                iw["iwindow"].close();
            }
            else {
                iw["iwindow"].open(map, marker);
            }
        });
    });
}

/**
 * Gets the dead patients html content for the info window
 * @param patient - patient to attach to info window
 * @returns {string}
 */
function getDeadPatientInfoContent(patient) {
    var addressString = patient.getDeathLocationConcat();
    var nhi = patient.getNhiNumber();
    return '<button onclick="openPatientProfile(\'' + nhi + '\')" type="button" class="btn btn-link" style="font-size: 24px; margin-left: -10px">'
            + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</button>' + '<br>' + '<span class="info-window-address">'
            + addressString + '</span><br>' + '<label>Blood Group: ' + patient.getBloodGroup() + '</label><br>' + '<label>Age: ' + patient.getAge()
            + '</label><br>' + '<label>Birth Gender: ' + patient.getBirthGender() + '</label><br>'
            + '<label style="padding-top: 5px;">Organ to Assign:</label>'
            + '<select id="dropdown" style="margin-left: 5%; float: right; height: 25px"></select>'
            + '<input type="button" onclick="viewPotentialMatches(\'' + nhi + '\')" class="btn btn-sm btn-block btn-primary mt-3 float-left" value="View Potential Matches" style="margin-top: 20px"/>';
}

/**
 * Triggers Java method to find potential matches
 */
function viewPotentialMatches(patientNhi) {
    if (currentOrgan !== undefined) {
        mapBridge.getPotentialMatches(patientNhi, currentOrgan);
    } else {
        showGenericNotification("Please select an organ to view potential matches for.")
    }
}

/**
 * Populates map with potential matches
 */
function populatePotentialMatches(patientNhi, donor) {
    $('#cancelAssignmentBtn').show();
    isViewingPotentialMatches = true;
    var donorMarker;
    markers.forEach(function (marker) {
        if (marker.nhi === patientNhi) {
           donorMarker = marker;
        } else {
            marker.setMap(null);
        }
    });
    infoWindows.forEach(function (infoWindow) {
        if (infoWindow["nhi"] === donor.getNhiNumber()) {
            infoWindow["iwindow"].close();
        }
    });
    if (donorMarker !== undefined) {
        successCount = 0;
        infoWindows = [];
        markerSetId++;
        addMarkers(patients.size(), markerSetId);
        potentialMatches = [];
        donorMarker.setMap(map);
        markers.push(donorMarker);
        patients.add(donor);
        makeAndAttachInfoWindow(donor, donorMarker);
    }

    showGenericNotification(patients.size() - 1 + " potential match(es) found. Click on a match to assign the organ.");
}

/**
 * no potential matches found
 */
function noPotentialMatchesFound(){
    mapBridge.populateLastSetOfPatients();
    isViewingPotentialMatches = false;
    $('#cancelAssignmentBtn').hide();
    showGenericNotification(0 + " potential matches found.");
}

/**
 * Gets the alive patients html content for the info window
 * @param patient - patient to attach to info window
 * @returns {string}
 */
function getAlivePatientInfoContent(patient) {
    var organOptions = getOrganOptions(patient);
    var nhi = patient.getNhiNumber()
    return '<button onclick="openPatientProfile(\'' + nhi + '\')" type="button" class="btn btn-link" style="font-size: 24px; margin-left: -10px">'
                        + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</button>' + '<br>' + '<span class="info-window-address">'
            + patient.getAddressString() + '</span><br><br>' + organOptions.donating + '<br><br>' + organOptions.receiving
            + '</span>';
}

/**
 * Create the information window content for a patient that is a potential match for the organ searched for
 * @param patient
 * @returns {string}
 */
function getPotentialMatchesContent(patient) {
    var organOptions = getOrganOptions(patient);
    var modalContent = '';
    receiverPatientNhi = patient.getNhiNumber();
    modalContent += '<tr>\n' +
        '<td style=\"font-size: 15px; padding-top: 18px\">' + donorPatientNhi + '</td>\n' +
        '<td style=\"font-size: 15px; padding-top: 18px\">' + receiverPatientNhi + '</td>\n' +
        '<td style=\"font-size: 15px; padding-top: 18px\">' + currentOrgan + '</td>\n' +
        '</tr>';
    $('#assignOrganTableBody').html(modalContent);
    return '<h5>' + receiverPatientNhi + ' - ' + patient.getNameConcatenated() + '</h5><span style="font-size: 14px">'
        + patient.getAddressString() + '<br><br>' + organOptions.donating + '<br><br>' + organOptions.receiving
        + '</span><br><input type="button" onclick="openPatientProfile(\'' + receiverPatientNhi
        + '\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/> '
        + '<input type="button" class="btn btn-sm btn-success mt-3" '
        + 'style="margin: auto" value="Assign ' + currentOrgan + '" data-toggle="modal" data-target="#assignOrganModal">';
}

/**
 * Triggers Java method to assign the organ to a recipient and match the two donor, receivers on their profiles
 */
function assignOrgan() {
    mapBridge.assignOrgan(donorPatientNhi, receiverPatientNhi, currentOrgan);
    mapBridge.populateLastSetOfPatients();
    receiverPatientNhi = undefined;
}

/**
 * Creates a circle radii for current organ marker selected
 */
function createMarkerRadii(radius, color, organ) {
    var markerCircle;

    // Add the circle for this organ to the map.
    markerCircle = new google.maps.Circle({
        map: null,
        strokeColor: "#484848",
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: color,
        fillOpacity: 0.6,
        center: currentMarker.position,
        radius: radius,
        organ: organ
    });
    circles.push(markerCircle);
}

/**
 * Sets the current organ to the organ selected to view or the first one in a patient profile if no organ is selected
 * @param organ - String format of organ to set currentOrgan to
 */
function setCurrentOrgan(organ) {
    clearCircles();
    if (organ === undefined) {
        currentOrgan = undefined;
        return;
    }
    currentOrgan = organ.trim();
    mapBridge.loadCircle(currentMarker.nhi, currentOrgan);
}

/**
 * Updates the circle radii and colour for current marker selected
 */
function updateMarkerRadii(radius, color, organ) {
    circles.forEach(function (circle) {
        if (circle.organ === currentOrgan) {
            if (circle.organ === organ) {
                circle.setOptions({radius: radius, fillColor: color, map: map});
            }
        }
        else {
            circle.setOptions({map: null});
        }
    });
}

/**
 * Opens patient profile when the button from the infoWindow is clicked on
 */
function openPatientProfile(patientNhi) {
    mapBridge.openPatientProfile(patientNhi);
}

/**
 * Returns the donations and requirements from the patient object
 * @param patient
 * @returns {{donating: string, receiving: string}}
 */
function getOrganOptions(patient) {
    var donations = patient.getDonations().toString();
    var donationStr, string, result;
    var reg = /([\w\s]+)=\w+,?/g;
    if (donations !== '{}') {
        donationStr = '<b>Donations:</b><br>';
        var donationsArray = [];
        string = donations.substring(1, donations.length - 1);
        while (result = reg.exec(string)) {
            donationsArray.push(result[1]);
        }
        donationStr += donationsArray.join(", ");
    }
    else {
        donationStr = 'No Donations';
    }
    var required = patient.getRequiredOrgans().toString();
    if (required !== '{}') {
        reqsArray = [];
        requiredStr = '<b>Required:</b><br>';
        string = required.substring(1, required.length - 1);
        while (result = reg.exec(string)) {
            reqsArray.push(result[1]);
        }
        requiredStr += reqsArray.join(", ");
    }
    else {
        requiredStr = 'No Requirements';
    }

    return {donating: donationStr, receiving: requiredStr};
}

/**
 * Sets the patients for the map and adds the markers to the map
 * @param newPatients
 */
function setPatients(newPatients) {
    $('#dropdown').prop('disabled', true);
    patients = newPatients;
    resetMap();
    successCount = 0;
    infoWindows = [];
    markerSetId++;
    filterAreaSet = false;
    potentialMatches = [];
    addMarkers(patients.size(), markerSetId);
    $('#cancelAssignmentBtn').hide();
}

/**
 * Add markers to the map
 * @param i
 * @param id
 */
function addMarkers(i, id) {
    if (i < 1) {
        showNotification(successCount, patients.size());
        return;
    }
    if (id !== markerSetId) {
        return; //break task
    }
    addMarker(patients.get(i - 1));
    setTimeout(function () {
        addMarkers(--i, id);
    }, 700);
}

/**
 * Clear the markers from the map
 */
function clearMarkers() {
    failedPatientArray = [];
    markers.forEach(function (marker) {
        marker.setMap(null);
    });
    markers = [];
}

/**
 * Clear circles from the map
 */
function clearCircles() {
    if (circles.length > 0) {
        circles.forEach(function (circle) {
            circle.setMap(null);
        });
    }
    circles = [];
}

/**
 * clear the rectangle filter area on map
 */
function clearRectangle() {
    if (rectangle.length > 0) {
        rectangle[0].setMap(null);
    }
    rectangle = [];
    filterStart = undefined;
    filterEnd = undefined;
}

/**
 * Hides the marker notification
 */
function hideNotification() {
    $('#marker-notification').hide();
}

/**
 * Shows number of successfully loaded patients
 * @param numSuccess successfully loaded patients
 * @param numTotal total patients to load
 */
function showNotification(numSuccess, numTotal) {
    var modalContent = "";
    failedPatientArray.forEach(function (patient) {

        var nhi = patient.getNhiNumber();
        var address;
        if (patient.isDead()) {
            address = patient.getDeathLocationConcat();
        }
        else {
            address = patient.getFormattedAddress();
        }
        modalContent += '<tr>\n' + '<th scope=\"row\"><button  onclick="openPatientProfile(\'' + nhi
                + '\')" type=\"button\" class=\"btn btn-link\" style=\"font-size: 15px; margin-left: -20px\">' + patient.getNhiNumber()
                + '</button></th>\n' + '<td style=\"font-size: 15px; padding-top: 18px\">' + patient.getNameConcatenated() + '</td>\n'
                + '<td style=\"font-size: 15px; padding-top: 18px\">' + address + '</td>\n' + '</tr>';
    });
    if (failedPatientArray.length === 0) { //no failed patients -> success

        $('#marker-notification').html('<span>' + 'Successfully loaded all ' + numTotal + ' patients');
        setTimeout(function () {
            hideNotification();
        }, 3000);

    }
    else {
        var modalMessage = 'Successfully loaded ' + numSuccess + ' out of ' + numTotal + ' patient locations.';
        $('#marker-notification').html('<span>' + modalMessage + '</span>'
                + '    <a href="#" data-toggle="modal" data-target="#failedPatients">View failed patients</a>\n'
                + '    <span class="marker-notification-close" onclick="hideNotification()"> &times;</span>');
        $('#failed-patient-table').html(modalContent);
    }

    if (numTotal > 0) {
        $('#marker-notification').show();
    }

}

/**
 * Shows a generic notification using a given message
 * @param message the message to display in the notification
 */
function showGenericNotification(message) {

    // set notification message
    $('#generic-notification').html('<span>' + message + '</span>');

    // show notification
    $('#generic-notification-msg').html();
    $('#generic-notification').show();

    setTimeout(function () {
        hideGenericNotification();
    }, 5000);

}

/**
 * Hides the generic  notification
 */
function hideGenericNotification() {
    $('#generic-notification').hide();
}

/**
 * Populates organ dropdown with patient dontations
 * @param patient - patient whos info window is being looked at
 * @param infowindow - info window being displayed
 */
function buildOrganDropdown(infowindow) {
    google.maps.event.addListener(infowindow, "domready", function () {
        infoWindows.forEach(function (iw) {
            if (iw["iwindow"] === infowindow) {
                $('#dropdown').html('<option>None</option>');
                mapBridge.getPatientActiveDonations(iw["nhi"]);
            }
        });
    }, false);
}

/**
 * Intended to be able to reload patients info window,
 * to be called from JAVA
 * @param patient - patient whose info window is to be updated
 */
function reloadInfoWindow(patient) {
    if (patient.isDead()) {
        var matchedMarkers = markers.filter(function (marker) {
            return marker.nhi === patient.getNhiNumber();
        });
        if (matchedMarkers.length > 0) {
            matchedMarkers[0].setOptions({
                icon: icons.deceased
            });
        }
    }
    for (var i = 0; i < infoWindows.length; i++) {
        if (infoWindows[i]["nhi"] === patient.getNhiNumber()) {
            if (patient.isDead()) {
                infoWindows[i]["iwindow"].setContent(getDeadPatientInfoContent(patient));
                buildOrganDropdown(infoWindows[i]["iwindow"]);
            }
            else {
                infoWindows[i]["iwindow"].setContent(getAlivePatientInfoContent(patient));
            }
        }
    }
    clearCircles();
    mapBridge.loadCircle(patient.getNhiNumber(), currentOrgan);
}

/**
 * Maps patient to its information window in global jsonarray
 * @param infoWindow - info window to map patient to
 * @param patient - patient to map
 */
function mapInfoWindowToPatient(infoWindow, patient) {
    var hasExistingInfoWindow = false;
    var i;
    for (i = 0; i < infoWindows.length; i++) {
        if (infoWindows[i]["nhi"] === patient.getNhiNumber()) {
            hasExistingInfoWindow = true;
            break;
        }
    }
    if (hasExistingInfoWindow) {
        infoWindows.splice(i, 1, {"iwindow": infoWindow, "nhi": patient.getNhiNumber()}); //hacks -> cannot use patient obj so need nhi
        //java -> js references out of whack when updating
    }
    else {
        infoWindows.push({"iwindow": infoWindow, "nhi": patient.getNhiNumber()}); //
    }
}

/**
 * Gets the styling to set for the custom Google map style param
 * @returns {*[]} a list of styles
 */
function getMapCustomStyle() {
    var styleHidePoi = [{
        featureType: "poi", elementType: "labels", stylers: [{visibility: "off"}]
    }];
    return styleHidePoi;
}

/**
 * Sets the zoom on the map
 * @param newZoom - new zoom to set
 */
function setJankaZoom(newZoom) {
    map.setZoom(newZoom * originalZoom);
}

/**
 * Sets the original zoom
 */
function setJankaOriginal() {
    originalZoom = map.getZoom();
}

/**
 * Loads active organs and populates the dropdown in the DOM
 * @param patientOrgans - organs that are active
 */
function loadActiveDonations(patientOrgans) {
    var donations = [];
    for (var i = 0; i < patientOrgans.size(); i++) {
        donations.push(patientOrgans.get(i).getOrgan());
    }
    for (var i = 0; i < donations.length; i++) {
        $('#dropdown').append($('<option>', {
            value: donations[i], text: donations[i]
        }));
    }
    $('#dropdown').change(function () {
        var selected = $('#dropdown :selected').text();
        if (selected.toLowerCase() !== 'none') {
            setCurrentOrgan(selected);
        }
        else {
            setCurrentOrgan(undefined);
        }
    });
    if (currentOrgan !== undefined) {
        $('#dropdown').val(currentOrgan);
    }
    if (isViewingPotentialMatches) {
        $('#dropdown').prop('disabled', true);
    }

}
