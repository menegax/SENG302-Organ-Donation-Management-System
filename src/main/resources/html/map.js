var map, geocoder, patients, mapBridge, successCount;
var markers = [];
var infoWindows = [];

function init() {
    geocoder = new google.maps.Geocoder();
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -40.59225, lng: 173.51012}, zoom: 6, disableDefaultUI: true, scaleControl: true, gestureHandling: 'cooperative'
    });

    google.maps.event.addListenerOnce(map, 'idle', function () {
        setMapDragEnd();
        addMarkers(patients.size());

        document.getElementById('availableOrgansView').addEventListener('click', function () {
            validCount = 0;

            markers.forEach(function (marker) {
                marker.setMap(null);
            });
            markers = [];

            patients = mapBridge.getAvailableOrgans();

            addMarkers(patients.size());
        });
    });
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
    console.log("Adding marker to map for patient " + patient.getNhiNumber());
    var latLong = patient.getCurrentLocation();
    if (latLong !== null) {
        successCount++;
        var marker = makeMarker(patient, latLong); //set up markers
        attachInfoWindow(patient, marker);
        markers.push(marker);
    }
    else {
        console.log('Geocoding failed because: ' + status);
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
            label: 'D',
            icon: 'https://maps.google.com/mapfiles/kml/shapes/info-i_maps.png'
        });
    }
    else if (!patient.isDead()) {
        return new google.maps.Marker({
            map: map,
            position: finalLoc,
            title: name,
            animation: google.maps.Animation.DROP,
            label: 'A',
            icon: 'https://maps.google.com/mapfiles/kml/shapes/parking_lot_maps.png'
        });
    }

}

function attachInfoWindow(patient, marker) {

    var organOptions = getOrganOptions(patient);

    var infoWindow = new google.maps.InfoWindow({
        content: '<h5>' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</h5><span style="font-size: 14px">'
        + patient.getAddressString() + '<br><br>' + organOptions.donating + '<br><br>' + organOptions.receiving
        + '</span><br><input type="button" onclick="openPatientProfile(\'' + patient.getNhiNumber()
        + '\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/>'
    });
    infoWindows.push(infoWindow);

    // when clicking on the marker, all other markers' info windows close
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

    marker.addListener('click', function toggleBounce() {
        if (marker.getAnimation() !== null) {
            marker.setAnimation(null); //todo make closing the info window stop the animation
        }
        else {
            marker.setAnimation(google.maps.Animation.BOUNCE);
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
    } else {
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
