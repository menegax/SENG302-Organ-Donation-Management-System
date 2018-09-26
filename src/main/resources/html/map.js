var map, geocoder, mapBridge, successCount;
var patients = [];
var circles = [];
var markers = [];
var infoWindows = [];
var failedPatientArray = [];
var markerSetId = 0;
var potentialMatches = [];
var donations = [];
var currentMarker;
var currentOrgan = undefined;
var donorPatient;
var dropDownDonations = [];

var originalZoom;

var isViewingPotentialMatches = false;

function init() {
    geocoder = new google.maps.Geocoder();
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -40.59225, lng: 173.51012},
        zoom: 6,
        disableDefaultUI: true,
        scaleControl: true,
        zoomControl: false,
        heading:90,
        tilt:45,
        gestureHandling: 'cooperative'
    });

    google.maps.event.addListenerOnce(map, 'idle', function () {
        setMapDragEnd();
        document.getElementById('availableOrgansView').addEventListener('click', function () {
            map.setCenter({lat: -40.59225, lng: 173.51012});
            mapBridge.getAvailableOrgans();
        });
    });
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
            map.setCenter({lat: -40.59225, lng: 173.51012}); //todo replace with recenter map method in BP6
        }
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
        var index = failedPatientArray.indexOf(patient);
        if (index !== -1) {
            failedPatientArray[index] = patient;
        }else {
            failedPatientArray.push(patient);
        }
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
            // label: 'D',
            nhi: patient.getNhiNumber(),
            icon: '../image/markers/blue.png'
        });
    }
    else if (!patient.isDead()) {
        return new google.maps.Marker({
            map: map,
            position: finalLoc,
            title: name,
            animation: google.maps.Animation.DROP,
            // label: 'A',
            nhi: patient.getNhiNumber(),
            icon: '../image/markers/green.png'
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
    } else if (potentialMatches !== []) {
        infoWindow = new google.maps.InfoWindow({
            content: getPotentialMatchesContent(patient),
            maxWidth:350
        });
    } else {
        infoWindow = new google.maps.InfoWindow({
            content: getAlivePatientInfoContent(patient),
            maxWidth:350
        });
    }
    mapInfoWindowToPatient(infoWindow, patient);
    marker.addListener('click', function () { // when clicking on the marker, all other markers' info windows close
        currentMarker = marker;
        currentOrgan = undefined;
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
    return '<button onclick="openPatientProfile(\'' + nhi + '\')" type="button" class="btn btn-link" style="font-size: 24px; margin-left: -10px">' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</button>' + '<br>'
        + '<span class="info-window-address">' + addressString + '</span><br>'
        + '<label>Blood Group: ' + patient.getBloodGroup() + '</label><br>'
        + '<label>Age: ' + patient.getAge() + '</label><br>'
        + '<label>Birth Gender: ' + patient.getBirthGender() + '</label><br>'
        + '<label style="padding-top: 5px;">Organ to Assign</label>'
        + '<select id="dropdown" style="margin-left: 5%; float: right; height: 25px"></select>'
        + '<input type="button" onclick="viewPotentialMatches(\'' + nhi + '\')" class="btn btn-sm btn-block btn-primary mt-3 float-left" value="View Potential Matches" style="margin-top: 20px"/>';
}

/**
 * Triggers Java method to find potential matches
 */
function viewPotentialMatches(patientNhi) {
    isViewingPotentialMatches = false;
    mapBridge.getPotentialMatches(patientNhi, currentOrgan);
}

/**
 * Populates map with potential matches
 */
function populatePotentialMatches(patientNhi, donor) {
    isViewingPotentialMatches = true;
    var donorMarker;
    markers.forEach(function (marker) {
       if (marker.nhi === patientNhi) {
           donorMarker = marker;
       }
    });
    infoWindows.forEach(function (infoWindow) {
        if (infoWindow["nhi"] === donor.getNhiNumber()) {
            infoWindow["iwindow"].close();
        }
    });
    setPatients(potentialMatches);
    if (donorMarker !== undefined) {
        donorMarker.setMap(map);
        markers.push(donorMarker);
        patients.add(donor);
        attachInfoWindow(donor, donorMarker);
    }
    // showGenericNotification(potentialMatches.size() + " potential matches found.");
}

/**
 * no potential matches found
 */
function noPotentialMatchesFound(numberOfPotentialMatches){
    isViewingPotentialMatches = false;
    // showGenericNotification(numberOfPotentialMatches + " potential matches found.");
}

/**
 * Gets the alive patients html content for the info window
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
 * Create the information window content for a patient that is a potential match for the organ searched for
 * @param patient
 * @returns {string}
 */
function getPotentialMatchesContent(patient) {
    var organOptions = getOrganOptions(patient);
    var modalContent = '';
    modalContent += '<tr>\n' +
        '<td style=\"font-size: 15px; padding-top: 18px\">' + donorPatient + '</td>\n' +
        '<td style=\"font-size: 15px; padding-top: 18px\">' + patient.getNhiNumber() + '</td>\n' +
        '<td style=\"font-size: 15px; padding-top: 18px\">' + currentOrgan + '</td>\n' +
        '</tr>';
    $('#assignOrganTable').html(modalContent);
    return '<h5>' + patient.getNhiNumber() + ' - ' + patient.getNameConcatenated() + '</h5><span style="font-size: 14px">'
        + patient.getAddressString() + '<br><br>' + organOptions.donating + '<br><br>' + organOptions.receiving
        + '</span><br><input type="button" onclick="openPatientProfile(\'' + patient.getNhiNumber()
        + '\')" class="btn btn-sm btn-primary mt-3" style="margin: auto" value="Open Profile"/> '
        + '<input type="button" class="btn btn-sm btn-success mt-3" '
        + 'style="margin: auto" value="Assign \'' + currentOrgan +'\'" onClick="modalContent(\''+ patient.getNhiNumber() +'\')" data-toggle="modal" data-target="#assignOrganModal">';
}

function modalContent(receiverNhi) {

}

function assignOrgan() {

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
    hideNotification();
    clearMarkers();
    clearCircles();
    successCount = 0;
    infoWindows = [];
    markerSetId++;
    addMarkers(patients.size(), markerSetId);
    potentialMatches = [];
    isViewingPotentialMatches = false;
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
    addMarker(patients.get(i-1));
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
 * Clear circles from the map
 */
function clearCircles() {
    if (circles.length > 0) {
        circles.forEach(function (circle){
            circle.setMap(null);
        });
    }
    circles = [];
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
    if (failedPatientArray.length ===  0){ //no failed patients -> success
        $('#marker-notification').html('<span>' + modalMessage + '</span><span class="marker-notification-close" onclick="hideNotification()"> &times;</span>');
    } else {
        $('#marker-notification').html('<span>' + modalMessage + '</span>' +
            '    <a href="#" data-toggle="modal" data-target="#failedPatients" style="color: yellow">View failed patients</a>\n' +
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
        infoWindows.splice(i, 1, { "iwindow" : infoWindow, "nhi" : patient.getNhiNumber()}); //hacks -> cannot use patient obj so need nhi
                                                                                                                  //java -> js references out of whack when updating
    } else {
        infoWindows.push({ "iwindow" : infoWindow, "nhi" : patient.getNhiNumber()}); //
    }
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
    for (var i = 0; i<patientOrgans.size(); i++) {
        donations.push(patientOrgans.get(i).getOrgan());
    }
    for (var i = 0; i< donations.length; i++) {
        $('#dropdown').append($('<option>', {
            value: donations[i],
            text: donations[i]
        }));
    }
    $('#dropdown').change(function() {
        var selected = $('#dropdown :selected').text();
        if (selected.toLowerCase() !== 'none') {
            setCurrentOrgan(selected);
        } else {
            setCurrentOrgan(undefined);
        }
    });
    if (currentOrgan !== undefined) {
        $('#dropdown').val(currentOrgan);
    }
}
