var map, geocoder, globalPatients, mapBridge, successCount;
var markers = [];
var infoWindows = [];
var failedPatientArray = [];
var markerSetId = 0;
var filterByAreaListener, filterStart, filterEnd;
var patientsFilteredByArea;
var interruptMarkers = false;

var iconBase = '../image/markers/';
var originalZoom;

var icons = {
    deceased: {
        name: 'Deceased',
        icon: iconBase + 'blue.png'
    },
    alive: {
        name: 'Alive',
        icon: iconBase + 'green.png'
    }
};

function init() {
    geocoder = new google.maps.Geocoder();

    var styleHidePoi =[
        {
            featureType: "poi",
            elementType: "labels",
            stylers: [
                { visibility: "off" }
            ]
        }
    ];

    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -40.59225, lng: 173.51012},
        zoom: 6,
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
        styles: styleHidePoi
    });

    setUpLegend(icons);

    // view available organs button
    google.maps.event.addListenerOnce(map, 'idle', function () {
        setMapDragEnd();
        document.getElementById('availableOrgansView').addEventListener('click', function () {
            map.setCenter({lat: -40.59225, lng: 173.51012});
            validCount = 0;
            failedPatientArray = [];
            markers.forEach(function (marker) {
                marker.setMap(null);
            });
            markers = [];
            globalPatients = mapBridge.getAvailableOrgans();
            successCount = 0;
            markerSetId++;
            addMarkers(globalPatients.size(), markerSetId);
        });
    });

    // filter area button
    google.maps.event.addListenerOnce(map, 'idle', function () {
        document.getElementById('filterAreaBtn').addEventListener('click', function () {
            console.log("Filter area button clicked!");
            filterByAreaListener = google.maps.event.addListener(map, 'click', function(e) {
                if (filterStart === undefined) {
                    filterStart = e.latLng;
                } else {
                    filterEnd = e.latLng;
                    filterArea({start: filterStart, end: filterEnd});
                    google.maps.event.removeListener(filterByAreaListener);
                }
            });
        });
    });

    // clear filter area button
    google.maps.event.addListenerOnce(map, 'idle', function () {
        document.getElementById('clearFilterAreaBtn').addEventListener('click', function () {
            console.log("Clear filter area button clicked!");
            clearFilterArea();
        });
    });
}

/**
 * Gets globalPatients who are within the area and resets the markers on the map to be them
 */
function filterArea(area) {
    interruptMarkers = true
    markers.forEach(function (marker) {
        if (!isPatientInArea(marker, area)) {
            marker.setMap(null);
        }
    });
}

function setUpLegend(icons) {
    var legend = document.getElementById('legend');
    for (var key in icons) {
        var type = icons[key];
        var name = type.name;
        var icon = type.icon;
        var div = document.createElement('div');
        div.innerHTML = '<img src="' + icon + '"> ' + name;
        legend.appendChild(div);
    }

    map.controls[google.maps.ControlPosition.RIGHT_TOP].push(legend);
}

/**
 * Reconnects every marker back to the map
 */
function clearFilterArea() {
    markers.forEach(function (marker) {
            marker.setMap(map);
    });
}

/**
 * Finds out if a patient is within a given area
 * @param patient the patient to test
 * @param area the area bounds
 * @returns {boolean} if the patient is inside or outside the area
 */
function isPatientInArea(marker, area) {

    var minLng, minLat, maxLng, maxLat;
    var current = marker.position;

    if (area.start.lng() > area.end.lng()) {
        maxLng = area.start.lng();
        minLng = area.end.lng();
    } else {
        maxLng = area.end.lng();
        minLng = area.start.lng();
    }

    if (area.start.lat() > area.end.lat()) {
        maxLat = area.start.lat();
        minLat = area.end.lat();
    } else {
        maxLat = area.end.lat();
        minLat = area.start.lat();
    }

    console.log("Finding if patient is in area: " + area.start.lng() + " " + area.start.lat() + " " + area.end.lng() + " " + area.end.lat());

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
    } else {
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
 * Sets the viewable area of the map
 */
function setMapDragEnd() {
    // Bounds for the World
    var allowedBounds = new google.maps.LatLngBounds(new google.maps.LatLng(-56.831005, 140.304953), new google.maps.LatLng(-22.977599, -165.689951));

    // Listen for the dragend event
    google.maps.event.addListener(map, 'dragend', function () {
        if (allowedBounds.contains(map.getCenter())) {
            return;
        }

        // Out of bounds - Move the map back within the bounds

        var c = map.getCenter(), x = c.lng(), y = c.lat(), maxX = allowedBounds.getNorthEast().lng(), maxY = allowedBounds.getNorthEast().lat(),
                minX = allowedBounds.getSouthWest().lng(), minY = allowedBounds.getSouthWest().lat();

        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }

        map.setCenter(new google.maps.LatLng(y, x));
    });
}

/**
 * Adds a marker to the map
 * @param patient
 */
function addMarker(patient) {
    if (!interruptMarkers) {
        console.log("Adding marker to map for patient " + patient.getNhiNumber());
        var latLong = patient.getCurrentLocation();
        if (latLong !== null) {
            successCount++;
            var marker = makeMarker(patient, latLong); //set up markers
            attachInfoWindow(patient, marker);
            markers.push(marker);
        }
        else {
            var index = failedPatientArray.indexOf(patient);
            if (index !== -1) {
                failedPatientArray[index] = patient;
            }else {
                failedPatientArray.push(patient);
            }
            console.log('Geocoding failed because: ' + status);
        }
    }
}

function makeMarker(patient, results) {
    var name = patient.getNameConcatenated();

    var randx = Math.random() * 0.02 - 0.01;
    var randy = Math.random() * 0.02 - 0.01;
    var finalLoc = new google.maps.LatLng(results.lat + randx, results.lng + randy);

    if (patient.isDead()) {
        return new google.maps.Marker({
            map: map,
            position: finalLoc,
            title: name,
            animation: google.maps.Animation.DROP,
            nhi: patient.getNhiNumber(),
            icon: icons.deceased
        });
    }
    else if (!patient.isDead()) {
        return new google.maps.Marker({
            map: map,
            position: finalLoc,
            title: name,
            animation: google.maps.Animation.DROP,
            nhi: patient.getNhiNumber(),
            icon: icons.alive
        });
    }

}

function attachInfoWindow(patient, marker) {
    var infoWindow;
    if (patient.isDead()) {
        infoWindow = new google.maps.InfoWindow({
            content: getDeadPatientInfoContent(patient),
            maxWidth:550
        });
        buildOrganDropdown(infoWindow);
    } else {
        infoWindow = new google.maps.InfoWindow({
            content: getAlivePatientInfoContent(patient),
            maxWidth:350
        });
    }
    mapInfoWindowToPatient(infoWindow, patient);
    marker.addListener('click', function () { // when clicking on the marker, all other markers' info windows close
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
 * Gets the dead globalPatients html content for the info window
 * @param patient - patient to attach to info window
 * @returns {string}
 */
function getDeadPatientInfoContent(patient) {
    var addressString = patient.getDeathLocationConcat();
    var nhi = patient.getNhiNumber();
    return '<button onclick="openPatientProfile(\'' + nhi + '\')" type="button" class="btn btn-link" style="font-size: 24px; margin-left: -10px">' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</button>' + '<br>'
        + '<span class="info-window-address">' + addressString + '</span><br>'
        + '<label>Blood Group: ' + patient.getBloodGroup() + '</label><br>'
        + '<label>Age: ' + patient.getAge() + '</label><br>'
        + '<label>Birth Gender: ' + patient.getBirthGender() + '</label><br>'
        + '<label style="padding-top: 5px;">Organ to Assign:</label>'
        + '<select id="dropdown" style="margin-left: 5%; float: right; height: 25px"></select>'
        + '<input type="button" onclick="assignOrgan()" class="btn btn-sm btn-block btn-primary mt-3 float-left" value="Assign Organ" style="margin-top: 20px"/>';
}

/**
 * Gets the alive globalPatients html content for the info window
 * @param patient - patient to attach to info window
 * @returns {string}
 */
function getAlivePatientInfoContent(patient) {
    var organOptions = getOrganOptions(patient);
    return '<h5>' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</h5><span style="font-size: 14px">'
        + patient.getAddressString() + '<br><br>' + organOptions.donating + '<br><br>' + organOptions.receiving
        + '</span><br><input type="button" onclick="openPatientProfile(\'' + patient.getNhiNumber()
        + '\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/>';
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
    var donationStr, reg, string, result;
    if (donations !== '{}') {
        reg = /(\w+)=\w+,?/g;
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
        reg = /(\w+)=\w+,?/g;
        reqsArray = [];
        requiredStr = '<b>Required:</b><br>';
        string = required.substring(1, required.length - 1);
        while (result = reg.exec(string)) {
            reqsArray.push(result[1]);
        }
        requiredStr += reqsArray.join(", ");
    } else {
        requiredStr = 'No Requirements';
    }

    return {donating: donationStr, receiving: requiredStr};
}

/**
 * Sets the globalPatients for the map and adds the markers to the map
 * @param _patients
 */
function setPatients(_patients) {
    globalPatients = _patients;
    hideNotification();
    clearMarkers();
    successCount = 0;
    infoWindows = [];
    markerSetId++;
    interruptMarkers = false;
    addMarkers(globalPatients.size(), markerSetId);
}

/**
 * Add markers to the map
 * @param i
 * @param id
 */
function addMarkers(i, id) {
    if (i < 1) {
        showNotification(successCount, globalPatients.size());
        return;
    }
    if (id !== markerSetId) {
        return; //break task
    }
    addMarker(globalPatients.get(i-1));
    setTimeout(function() {
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
 * Hides the notification
 */
function hideNotification() {
    $('#marker-notification').hide();
}

/**
 * Shows number of successfully loaded globalPatients
 * @param numSuccess successfully loaded globalPatients
 * @param numTotal total globalPatients to load
 */
function showNotification(numSuccess, numTotal) {
    var modalContent = "";
    var modalMessage = 'Successfully loaded ' + numSuccess + ' out of ' + numTotal + ' patient locations.';
    $('#marker-notification-msg').html();
    $('#marker-notification').show();
    setTimeout(function() {
        hideNotification();
    }, 10000);
    failedPatientArray.forEach(function(patient) {
        var nhi = patient.getNhiNumber();
        var address;
        if (patient.isDead()) {
            address = patient.getDeathLocationConcat();
        } else {
            address = patient.getFormattedAddress();
        }
        modalContent += '<tr>\n' +
           '<th scope=\"row\"><button  onclick="openPatientProfile(\'' + nhi + '\')" type=\"button\" class=\"btn btn-link\" style=\"font-size: 15px; margin-left: -20px\">'+  patient.getNhiNumber() + '</button></th>\n' +
           '<td style=\"font-size: 15px; padding-top: 18px\">' + patient.getNameConcatenated() + '</td>\n' +
           '<td style=\"font-size: 15px; padding-top: 18px\">' + address + '</td>\n' +
           '</tr>';
    });
    if (failedPatientArray.length ===  0){
        $('#marker-notification').html('<span>' + modalMessage + '</span><span class="marker-notification-close" onclick="hideNotification()"> &times;</span>');
    } else {
        $('#marker-notification').html('<span>' + modalMessage + '</span>' +
            '    <a href="#" data-toggle="modal" data-target="#failedPatients">View failed globalPatients</a>\n' +
            '    <span class="marker-notification-close" onclick="hideNotification()"> &times;</span>');
        $('#failed-patient-table').html(modalContent);
    }
}

/**
 * Populates organ dropdown with patient dontations
 * @param patient - patient whos info window is being looked at
 * @param infowindow - info window being displayed
 */
function buildOrganDropdown(infowindow) {
    google.maps.event.addListener(infowindow, "domready", function() {
        infoWindows.forEach(function(iw) {
            if (iw["iwindow"] === infowindow) {
                var patient2 = mapBridge.getPatientByNhi(iw["nhi"]);
                $('#dropdown').html('');
                $('#dropdown').html('<option value="organs">None</option>');
                var reg = /(\w+)=\w+,?/g;
                var donationsArray = [];
                var result;
                while (result = reg.exec(patient2.getDonations().toString().slice(1, -1))) {
                    donationsArray.push(result[1]);
                }
                for (var i = 0; i< donationsArray.length; i++) {
                    $('#dropdown').append($('<option>', {
                        value: i + 1,
                        text: donationsArray[i]
                    }));
                }
            }
        });
    }, false);
}

/**
 * Intended to be able to reload globalPatients info window,
 * to be called from JAVA
 * @param patient - patient whose info window is to be updated
 */
function reloadInfoWindow(patient) {
    if (patient.isDead()) {
        var matchedMarkers = markers.filter(function(marker) {
            return marker.nhi === patient.getNhiNumber();
        });
        if (matchedMarkers.length > 0) {
            matchedMarkers[0].setOptions({
                icon: '../image/markers/blue.png'
            });
        }
    }
    for (var i =0; i<infoWindows.length; i++) {
        if (infoWindows[i]["nhi"] === patient.getNhiNumber()) {
            if (patient.isDead()) {
                infoWindows[i]["iwindow"].setContent(getDeadPatientInfoContent(patient));
                buildOrganDropdown(infoWindows[i]["iwindow"]);
            } else {
                infoWindows[i]["iwindow"].setContent(getAlivePatientInfoContent(patient));
            }
        }
    }
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
        infoWindows.splice(i, 1, { "iwindow" : infoWindow, "nhi" : patient.getNhiNumber()}); //hacks -> cannot use patient obj so need nhi
                                                                                                                  //java -> js references out of whack when updating
    } else {
        infoWindows.push({ "iwindow" : infoWindow, "nhi" : patient.getNhiNumber()}); //
    }
}

function setJankaZoom(newZoom) {
    map.setZoom(newZoom * originalZoom);
}

function setJankaOriginal() {
    originalZoom = map.getZoom();
}