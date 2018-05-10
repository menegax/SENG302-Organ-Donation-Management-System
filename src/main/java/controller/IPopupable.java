package controller;

import model.Donor;

/**
 * For any pages that can be loaded as part of a popup.
 * Normally sets the donor that is being viewed so it can be passed between pages
 */
public interface IPopupable {
    void setViewedDonor(Donor donor);
}
