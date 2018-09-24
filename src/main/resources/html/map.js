var map, geocoder, patients, mapBridge, successCount;
var markers = [];
var infoWindows = [];
var failedPatientArray = [];

function init() {
    geocoder = new google.maps.Geocoder();
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: -40.59225, lng: 173.51012}, zoom: 6, disableDefaultUI: true, scaleControl: true, gestureHandling: 'cooperative'
    });

    google.maps.event.addListenerOnce(map, 'idle', function () {
        setMapDragEnd();
        document.getElementById('availableOrgansView').addEventListener('click', function () {
            validCount = 0;
            failedPatientArray = [];
            markers.forEach(function (marker) {
                marker.setMap(null);
            });
            markers = [];
            patients = mapBridge.getAvailableOrgans();
            successCount = 0;
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
            label: 'D',
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
            label: 'A',
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
        })
    });

    // marker.addListener('click', function toggleBounce() {
    //     if (marker.getAnimation() !== null) {
    //         marker.setAnimation(null); //todo make closing the info window stop the animation
    //     }
    //     else {
    //         marker.setAnimation(google.maps.Animation.BOUNCE);
    //     }
    // });
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
        + '<label>Organ to Assign</label>'
        + '<select id="dropdown" style="margin-left: 5%"></select>' + '<br><br>'
        + '<input type="button" onclick="assignOrgan()" class="btn btn-sm btn-primary mt-3 float-left" value="Assign Organ" style="margin-top: 20px"/>';
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
    if (donations !== '{}') {
        var reg = /(\w+)=\w+,?/g;
        donationStr = '<b>Donations:</b><br>';
        var donationsArray = [];
        var result;
        var string = donations.substring(1, donations.length - 1);
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
 * Shows number of successfully loaded patients
 * @param numSuccess successfully loaded patients
 * @param numTotal total patients to load
 */
function showNotification(numSuccess, numTotal) {
    var modalContent = "";
    var modalMessage = 'Successfully loaded ' + numSuccess + ' out of ' + numTotal + ' patient locations';
    $('#marker-notification-msg').html();
    $('#marker-notification').show();
    failedPatientArray.forEach(function(patient) {
        var nhi = patient.getNhiNumber();
        var address;
        console.log(patient);
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
        $('#marker-notification').html('<span>' + modalMessage + '</span>');
    } else {
        $('#marker-notification').html('<span>' + modalMessage + '</span>' +
            '    <a href="#" data-toggle="modal" data-target="#failedPatients">View failed patients</a>\n' +
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
                var patient2 = iw["patient"];
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
 * Intended to be able to reload patients info window,
 * to be called from JAVA
 * @param patient - patient whos info window is to be updated
 */
function reloadInfoWindow(patient) {
    if (patient.isDead()) {
        markers.filter(function(marker) {
            return marker.nhi === patient.getNhiNumber();
        })[0].setOptions({
            icon: '../image/markers/blue.png'
        });
    }
    for (var i =0; i<infoWindows.length; i++) {
        if (infoWindows[i]["nhi"] === patient.getNhiNumber()) {
            infoWindows[i]["patient"] = patient;
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
        if (infoWindows[i]["patient"].getNhiNumber() === patient.getNhiNumber()) {
            hasExistingInfoWindow = true;
            break;
        }
    };
    if (hasExistingInfoWindow) {
        infoWindows.splice(i, 1, { "iwindow" : infoWindow, "patient" : patient, "nhi" : patient.getNhiNumber()}); //hacks -> cannot use patient obj so need nhi
                                                                                                                  //java -> js references out of whack when updating
    } else {
        infoWindows.push({ "iwindow" : infoWindow, "patient" : patient, "nhi" : patient.getNhiNumber()}); //
    }
}
