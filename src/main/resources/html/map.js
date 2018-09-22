var map, geocoder, patients, mapBridge, successCount;
var markers = [];
var infoWindows = [];
var matchedOrgans = [];

function init() {
    geocoder = new google.maps.Geocoder();
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -40.59225, lng: 173.51012}, zoom: 6, disableDefaultUI: true, scaleControl: true, gestureHandling: 'cooperative'
    });
    setMapDragEnd();
}

/**
 * Sets the viewable area of the map
 */
function setMapDragEnd() {
    // Bounds for the World
    var allowedBounds = new google.maps.LatLngBounds(new google.maps.LatLng(-84.220892, -177.871399), new google.maps.LatLng(84.889374, 179.872535));

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
    var address = patient.getFormattedAddress();
    var name = patient.getNameConcatenated();
    console.log("Adding marker to map for patient " + patient.getNhiNumber());
    geocoder.geocode({'address': address}, function (results, status) {
        if (status === 'OK') {
            successCount++;
            var organOptions = getOrganOptions(patient);
            var finalLoc = new google.maps.LatLng(results[0].geometry.location.lat(), results[0].geometry.location.lng());
            var marker = new google.maps.Marker({
                map: map, position: finalLoc, title: name
            });

            // create info window
            var infoWindow = new google.maps.InfoWindow({
                content: '<h5>' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</h5><span style="font-size: 14px">'
                + patient.getAddressString() + '<br><br>' + organOptions.donating + '<br><br>' + organOptions.receiving
                + '</span><br><input type="button" onclick="openPatientProfile(\'' + patient.getNhiNumber()
                + '\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/>'
            });
            infoWindows.push(infoWindow);

            // add listener to open infoWindow when marker clicked
            marker.addListener('click', function () {
                //infoWindow.open(map, marker);
                infoWindows.forEach(function (iw) {
                    if (iw !== infoWindow) {
                        iw.close();
                    }
                    else {
                        iw.open(map, marker);
                    }
                })
            });
            markers.push(marker);
            mapBridge.checkOrganMatch(marker.position, patient.getNhiNumber());
        }
        else {
            console.log('Geocode failed for patient ' + patient.getNhiNumber() + ' because: ' + status);
        }
    });
}

/**
 * passes marker and patient through to java to check for a match
 */
function matchedOrgan(geolocation, geolocation1) {
    //var matchedOrganPath = [{geolocation}, {geolocation1.lat, ge}];
    var matchedOrganPath = [{
        lat: geolocation.lat, lng: geolocation.lng
    },{
        lat: geolocation1.lat, lng: geolocation1.lng
    }];
    var matchedOrgan = new google.maps.Polyline({
        map: map,
        path: matchedOrganPath,
        geodesic: true,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 2
    });
    matchedOrgans.push(matchedOrgan);
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
    var donationStr;
    if (donations !== '[]') {
        donationStr = '<b>Donations:</b><br>' + donations.substring(1, donations.length - 1);
    }
    else {
        donationStr = 'No Donations';
    }

    var required = patient.getRequiredOrgans().toString();
    var matching = required.substring(1, required.length - 1).match(/[a-z]+/g);
    var requiredStr;
    if (matching) {
        requiredStr = '<b>Required:</b><br>' + matching.join(', ');
    }
    else {
        requiredStr = 'No Requirements';
    }
    return {donating: donationStr, receiving: requiredStr};
}

/**
 * Sets the patients for the map and adds the markers to the map
 * @param _patients
 */
function setPatients(_patients) {
    patients = _patients;
    clearMarkers();
    successCount = 0;
    addMarkers(patients.size());
}

/**
 * Add markers to the map
 * @param i
 */
function addMarkers(i) {
    if (i < 1) {
        showNotification(successCount, patients.size());
        return;
    }
    addMarker(patients.get(i-1));
    setTimeout(function() {
        addMarkers(--i);
    }, 700);
}

/**
 * Clear the markers from the map
 */
function clearMarkers() {
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
 * Shows number of successfully loaded patients
 * @param numSuccess successfully loaded patients
 * @param numTotal total patients to load
 */
function showNotification(numSuccess, numTotal) {
    $('#marker-notification-msg').html('Successfully loaded ' + numSuccess + ' out of ' + numTotal + ' patient locations');
    $('#marker-notification').show();
}
