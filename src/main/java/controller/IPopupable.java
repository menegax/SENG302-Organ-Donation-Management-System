package controller;

import model.Patient;

/**
 * For any pages that can be loaded as part of a popup.
 * Normally sets the patient that is being viewed so it can be passed between pages
 */
public interface IPopupable {
    void setViewedPatient(Patient patient);
}
