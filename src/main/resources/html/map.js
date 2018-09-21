var map, geocoder, patients, mapBridge;
var markers = [];
var infoWindows = [];
var validCount = 0;

function init() {
    geocoder = new google.maps.Geocoder();
        map = new google.maps.Map(document.getElementById('map'), {
            center: {lat: -40.59225, lng: 173.51012},
            zoom: 6,
            disableDefaultUI: true,
            scaleControl: true,
            gestureHandling: 'cooperative'
        });

    google.maps.event.addListenerOnce(map, 'idle', function(){
        setMapDragEnd();
        markerLoop(patients.size());

        document.getElementById('availableOrgansView').addEventListener('click', function() {
            validCount = 0;

            markers.forEach(function(marker) {
                marker.setMap(null);
            });
            markers = [];

            patients = mapBridge.getAvailableOrgans();

            markerLoop(patients.size());
        });
    });
}

/**
 * Sets the viewable area of the map
 */
function setMapDragEnd() {
    // Bounds for the World
    var allowedBounds = new google.maps.LatLngBounds(
        new google.maps.LatLng(-84.220892, -177.871399),
        new google.maps.LatLng(84.889374, 179.872535));

    // Listen for the dragend event
    google.maps.event.addListener(map, 'dragend', function() {
        if (allowedBounds.contains(map.getCenter())) return;

        // Out of bounds - Move the map back within the bounds

        var c = map.getCenter(),
            x = c.lng(),
            y = c.lat(),
            maxX = allowedBounds.getNorthEast().lng(),
            maxY = allowedBounds.getNorthEast().lat(),
            minX = allowedBounds.getSouthWest().lng(),
            minY = allowedBounds.getSouthWest().lat();

        if (x < minX) x = minX;
        if (x > maxX) x = maxX;
        if (y < minY) y = minY;
        if (y > maxY) y = maxY;

        map.setCenter(new google.maps.LatLng(y, x));
    });
}

function markerLoop(i) {
    if (i < 1) return;
    if (validCount >= 30) return;
    addMarker(patients.get(i-1));
    setTimeout(function() {
        markerLoop(--i);
    }, 700);
}

/**
 * Adds a marker to the map
 * @param patient
 */
function addMarker(patient) {
    console.log('Geocoding patient ' + patient.getNhiNumber() + ' with address ' + patient.getFormattedAddress());
    var latLong = patient.getCurrentLocation();
    if (latLong !== null) {
        validCount++;
        var marker = makeMarker(patient, latLong); //set up markers
        attachInfoWindow(patient, marker);
        markers.push(marker);
    } else {
        console.log('Geocoding failed because: ' + status);
    }
}

function makeMarker(patient, results) {
    var name = patient.getNameConcatenated();

    var randx = Math.random() * 0.02 - 0.01;
    var randy = Math.random() * 0.02 - 0.01;
    var finalLoc = new google.maps.LatLng(results.lat + randx, results.lng + randy); //todo replace with patient.getCurrentLocation

    if (patient.isDead()){
        return new google.maps.Marker({
            map: map, position: finalLoc, title: name, animation: google.maps.Animation.DROP, label: 'D'
        });
    } else if (!patient.isDead()) {
        return new google.maps.Marker({
            map: map, position: finalLoc, title: name, animation: google.maps.Animation.DROP, label: 'A'
        });
    }


}

function attachInfoWindow(patient, marker) {
    var infoWindow;
    if (patient.isDead()) {
        infoWindow = attachDonationInfoWindow(patient, marker);
    } else {
        var organOptions = getOrganOptions(patient);
        infoWindow = new google.maps.InfoWindow({
            content: '<h5>' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() +
            '</h5><span style="font-size: 14px">' +
            patient.getAddressString() + '<br><br>' +
            organOptions.donating + '<br><br>' +
            organOptions.receiving +
            '</span><br><input type="button" onclick="openPatientProfile(\''+patient.getNhiNumber()+'\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/>'
        });
    }

    infoWindows.push(infoWindow);

    // when clicking on the marker, all other markers' info windows close
    marker.addListener('click', function() {
        //infoWindow.open(map, marker);
        infoWindows.forEach(function(iw) {
            if (iw !== infoWindow) {
                iw.close();
            } else {
                iw.open(map, marker);
            }
        })
    });

    marker.addListener('click', function toggleBounce() {
        if (marker.getAnimation() !== null) {
            marker.setAnimation(null);
        } else {
            marker.setAnimation(google.maps.Animation.BOUNCE);
        }
    });
}


function attachDonationInfoWindow(patient, marker) {
    var content = '<div id="iw-container">' +
        '<h5>' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() +
        '</h5><span style="font-size: 14px">' +
        '<br><br><br><br>'+
        '<div class="dropdown show">' +
        '<a class="btn btn-secondary dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Organs</a>' +
            '<div class="dropdown-menu" aria-labelledby="dropdownMenuLink">' +
                '<a class="dropdown-item" href="#">Action</a>' +
                '<a class="dropdown-item" href="#">Another action</a>' +
            '<a class="dropdown-item" href="#">Something else here</a>' +
            '</div>' +
        '</div>' +
        '<br><br><br><br>'+
        '</span><br><input type="button" onclick="openPatientProfile(\''+patient.getNhiNumber()+'\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/>'+
        '</div>';
    return new google.maps.InfoWindow({
        content: content,
        maxWidth: 500,
        maxHeight: 600
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
    } else {
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


function setPatients() {
    validCount = 0;

    markers.forEach(function(marker) {
        marker.setMap(null);
    });
    markers = [];

    markerLoop(patients.size());
}
