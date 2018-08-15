// Note: This example requires that you consent to location sharing when
// prompted by your browser. If you see the error "The Geolocation service
// failed.", it means you probably did not give permission for the browser to
// locate you.
var map, geocoder, patients, jsInjector;
var markers = [];
var infoWindows = [];

function init() {
    geocoder = new google.maps.Geocoder();
    map = new google.maps.Map(document.getElementById('map'), {
        //center: {lat: -43.31225, lng: 172.35012},
        center: {lat: 0, lng: 0},
        zoom: 2,
        disableDefaultUI: true,
        zoomControl: true,
        scaleControl: true,
        gestureHandling: 'cooperative'
    });
    markerLoop(patients.size());
}

function markerLoop(i) {
    if (i < 1) return;
    addMarker(patients.get(i-1));
    setTimeout(function() {
        markerLoop(--i);
    }, 700);
}

function addMarker(patient) {
    var address = patient.getFormattedAddress();
    var name = patient.getNameConcatenated();
    geocoder.geocode({'address': address}, function (results, status) {
        if (status === 'OK') {
            var organOptions = getOrganOptions(patient);
            console.log('Placing marker on map');
            var marker = new google.maps.Marker({
                map: map,
                position: results[0].geometry.location,
                title: name
            });
            var infoWindow = new google.maps.InfoWindow({
                content: '<h5>' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() +
                '</h5><span style="font-size: 14px">' +
                patient.getAddressString() + '<br><br>' +
                organOptions.donating + '<br><br>' +
                organOptions.receiving +
                '</span><br><input type="button" onclick="openPatientProfile(\''+patient.getNhiNumber()+'\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/>'
            });
            infoWindows.push(infoWindow);
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
            markers.push(marker);
        } else {
            console.log('Geocode failed because: ' + status);
        }
    });
}

//Opens patient profile when the button from the infoWindow is clicked on
function openPatientProfile(patientNhi) {
    console.log("Patient: " + patientNhi);
    jsInjector.openPatientProfile(patientNhi);
}

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

/*infoWindow = new google.maps.InfoWindow;

// Try HTML5 geolocation.
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
        var pos = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
        };

        infoWindow.setPosition(pos);
        infoWindow.setContent('Location found.');
        infoWindow.open(map);
        map.setCenter(pos);
    }, function() {
        handleLocationError(true, infoWindow, map.getCenter());
    });
} else {
    // Browser doesn't support Geolocation
    handleLocationError(false, infoWindow, map.getCenter());
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
    infoWindow.setPosition(pos);
    infoWindow.setContent(browserHasGeolocation ?
        'Error: The Geolocation service failed.' :
        'Error: Your browser doesn\'t support geolocation.');
    infoWindow.open(map);
}*/