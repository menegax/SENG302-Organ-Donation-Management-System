package controller;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import model.Administrator;
import model.Medication;
import model.Patient;
import org.apache.commons.lang3.StringUtils;
import service.AdministratorDataService;
import service.PatientDataService;
import service.interfaces.IPatientDataService;
import utility.GlobalEnums;
import utility.undoRedo.IAction;
import utility.undoRedo.SingleAction;

import java.io.InvalidObjectException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utility.UserActionHistory.userActions;

/**
 * Patient profile page where in patient view, they may view their attributes,
 * donating organs and required organs. In clinician view, they can see the
 * highlighted cell if the donating organ is also required by the patient. This
 * class loads and controls this view.
 */
public class GUIPatientProfile extends TargetedController {

	@FXML
	private GridPane patientProfilePane;

	@FXML
	private Button deleteButton;

	@FXML
	private Label nhiLbl;

	@FXML
	public Label vitalLbl1;

	@FXML
	private Label dobLbl;

	@FXML
	private Label dateOfDeathLabel;

	@FXML
	private Label deathLocation;

	@FXML
	private Label deathCity;

	@FXML
	private Label deathRegion;

	@FXML
	private Label age;

	@FXML
	private Label heightLbl;

	@FXML
	private Label weightLbl;

	@FXML
	private Label bmi;

	@FXML
	private Label bloodGroupLbl;

	@FXML
	private Label streetLbl;

	@FXML
	private Label cityLbl;

	@FXML
	private Label suburbLbl;

	@FXML
	private Label regionLbl;

	@FXML
	private Label zipLbl;

	@FXML
	private Label genderDeclaration;

	@FXML
	private Label genderStatus;

	@FXML
	private ListView<String> receivingList;

	@FXML
	private Label receivingTitle;

	@FXML
	private Label prefGenderLbl;

	@FXML
	private Label firstNameLbl;

	@FXML
	private Label firstNameValue;

	@FXML
	private RowConstraints genderRow;

	@FXML
	private RowConstraints firstNameRow;

	@FXML
	private ListView<String> donationList;
	@FXML
	private ListView<String> medList;

	private UserControl userControl = UserControl.getUserControl();

	private ListProperty<String> donatingListProperty = new SimpleListProperty<>();

	private ListProperty<String> receivingListProperty = new SimpleListProperty<>();

	private ScreenControl screenControl = ScreenControl.getScreenControl();

	private ListProperty<String> medListProperty = new SimpleListProperty<>();

	private UndoRedoControl undoRedoControl = UndoRedoControl.getUndoRedoControl();

	private IPatientDataService patientDataService = new PatientDataService();

	/**
	 * Initialize the controller depending on whether it is a clinician viewing the
	 * patient or a patient viewing itself
	 */
	public void loadController() {
		IPatientDataService patientDataService = new PatientDataService();
		if (userControl.getLoggedInUser() instanceof Patient) {
			if (patientDataService.getPatientByNhi(((Patient) userControl.getLoggedInUser()).getNhiNumber())
					.getRequiredOrgans().size() == 0) {
				receivingList.setDisable(true);
				receivingList.setVisible(false);
				receivingTitle.setDisable(true);
				receivingTitle.setVisible(false);
				/*
				 * Hide the columns that would hold the receiving listview - this results in the
				 * visible nodes filling up the whole width of the scene
				 */
				for (int i = 9; i <= 11; i++) {
					patientProfilePane.getColumnConstraints().get(i).setMaxWidth(0);
				}
			}

			genderDeclaration.setVisible(false);
			genderStatus.setVisible(false);
			genderRow.setMaxHeight(0);
			firstNameLbl.setVisible(false);
			firstNameValue.setVisible(false);
			firstNameRow.setMaxHeight(0);

			deleteButton.setVisible(false);
			deleteButton.setDisable(true);
		} else {
			deleteButton.setVisible(true);
			deleteButton.setDisable(false);
		}
		assert target != null;
		Patient patientToLoad = patientDataService.getPatientByNhi(((Patient) target).getNhiNumber());
		patientDataService.save(patientToLoad);
		loadProfile(patientToLoad);
	}

	/**
	 * Sets the patient's attributes for the scene's labels
	 *
	 * @exception InvalidObjectException
	 *                if the nhi of the patient does not exist in the database
	 */
	private void loadProfile(Patient patient) {
		loadBasicDetails(patient);
		loadDeathDetails(patient);
		loadBodyDetails(patient);
		loadAddressDetails(patient);
		loadOrgans(patient);
		loadMedications(patient);

		// list view styling/highlighting
		highlightListCell(donationList, true);
		highlightListCell(receivingList, false);
	}

	private void loadMedications(Patient patient) {
		// Populate current medication listview
		Collection<Medication> meds = patient.getCurrentMedications();
		List<String> medsMapped = meds.stream().map(Medication::getMedicationName).collect(Collectors.toList());
		medListProperty.setValue(FXCollections.observableArrayList(medsMapped));
		if (medListProperty.getValue().size() == 0) {
		    medListProperty.setValue(FXCollections.observableArrayList(""));
        }
		medList.itemsProperty().bind(medListProperty);
	}

	private void loadOrgans(Patient patient) {
		if (patient.getRequiredOrgans() == null) {
			patient.setRequiredOrgans(new HashMap<>());
		}
		if (patient.getDonations() == null) {
			patient.setDonations(new HashMap<>());
		}
		List<String> organsMappedD = new ArrayList<>();
		List<String> organsMappedR = new ArrayList<>();
		for (GlobalEnums.Organ organ : patient.getRequiredOrgans().keySet()) {
			String donor = patient.getRequiredOrgans().get(organ).getDonorNhi() == null ? "--" : patient.getRequiredOrgans().get(organ).getDonorNhi();
			organsMappedR.add(StringUtils.capitalize(organ.getValue()) + " | "
					+ patient.getRequiredOrgans().get(organ).getRegisteredOn().toString() + " | "
					+ donor);
		}
		for (GlobalEnums.Organ organ : patient.getDonations().keySet()) {
			if (patient.getDonations().get(organ) == null) {
				organsMappedD.add(StringUtils.capitalize(organ.getValue()) + " | --");
			} else {
				organsMappedD.add(StringUtils.capitalize(organ.getValue()) + " | " +
						patient.getDonations().get(organ));
			}
		}
		donatingListProperty.setValue(FXCollections.observableArrayList(organsMappedD));
		receivingListProperty.setValue(FXCollections.observableArrayList(organsMappedR));
        if (donatingListProperty.getValue().size() == 0) {
            donatingListProperty.setValue(FXCollections.observableArrayList(""));
        }
        if (receivingListProperty.getValue().size() == 0) {
            receivingListProperty.setValue(FXCollections.observableArrayList(""));
        }
		donationList.itemsProperty().bind(donatingListProperty);
		receivingList.itemsProperty().bind(receivingListProperty);
		if (!(userControl.getLoggedInUser() instanceof Patient)) {
            setupListviewDoubleClick();
        }
	}

	/**
	 * Setup mouse listeners for double clicking on either the donating or receiving listview.
	 * Double clicking an assigned organ extracts the NHI with regex and opens up the profile
	 * for the patient with that NHI
	 */
	private void setupListviewDoubleClick() {
		donationList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				String selectedItem = donationList.getSelectionModel().getSelectedItem();
				openMatchedNHI(selectedItem, GlobalEnums.UIRegex.DONATINGLIST.getValue());
			}
		});
		receivingList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				String selectedItem = receivingList.getSelectionModel().getSelectedItem();
				openMatchedNHI(selectedItem, GlobalEnums.UIRegex.RECEIVINGLIST.getValue());
			}
		});
	}

	/**
	 * Matches a string with the given pattern. If the pattern matches, the matcher will contain two groups:
	 * - The whole string that was matched eg. Bone | ABC1238
	 * - The first capture group (i.e. the NHI): eg. ABC1238
	 * Upon matching, this nhi is used to fetch the patient and open their profile
	 * @param listItem The listItem (String) to match
	 * @param patternStr The regex pattern to match against
	 */
	private void openMatchedNHI(String listItem, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(listItem);
		if (matcher.find()) {
			Patient selected = patientDataService.getPatientByNhi(matcher.group(1));
			GUIHome controller = (GUIHome) screenControl.show("/scene/home.fxml", true, null, selected, screenControl.getTouchParent(patientProfilePane));
			controller.setTarget(selected);
		}
	}

	private void loadBodyDetails(Patient patient) {
		age.setText(String.valueOf(patient.getAge()));
		heightLbl.setText(String.valueOf(patient.getHeight() + " m"));
		weightLbl.setText(String.valueOf(patient.getWeight() + " kg"));
		bmi.setText(String.valueOf(patient.getBmi()));
		bloodGroupLbl.setText(patient.getBloodGroup() == null ? "Not set" : patient.getBloodGroup().getValue());
	}

	private void loadAddressDetails(Patient patient) {
		streetLbl.setText((patient.getStreetName() == null || patient.getStreetName().length() == 0) ? "Not set"
				: patient.getStreetNumber() + " " + patient.getStreetName());
		if (patient.getStreetName() == null || patient.getStreetName().length() == 0) {
			streetLbl.getStyleClass().add("notSet");
		}
		else {
			if (screenControl.isTouch()) {
				streetLbl.setStyle("-fx-fill: white; ");
			} else {
				streetLbl.setStyle("-fx-fill: black; ");
			}
		}
		suburbLbl.setText((patient.getSuburb() == null || patient.getSuburb().length() == 0) ? "Not set" : patient.getSuburb());
		if (patient.getSuburb() == null || patient.getSuburb().length() == 0) {
			suburbLbl.getStyleClass().add("notSet");
		}
		else {
			if (screenControl.isTouch()) {
				suburbLbl.setStyle("-fx-fill: white; ");
			} else {
				suburbLbl.setStyle("-fx-fill: black; ");
			}
		}
		cityLbl.setText((patient.getCity() == null || patient.getCity().length() == 0) ? "Not set" : patient.getCity());
		if (patient.getCity() == null || patient.getCity().length() == 0) {
			cityLbl.getStyleClass().add("notSet");
		} else {
			if (screenControl.isTouch()) {
				cityLbl.setStyle("-fx-fill: white; ");
			} else {
				cityLbl.setStyle("-fx-fill: black; ");
			}
		}
		regionLbl.setText(patient.getRegion() == null ? "Not set" : patient.getRegion().getValue());
		if (patient.getRegion() == null) {
			regionLbl.getStyleClass().add("notSet");
		} else {
			if (screenControl.isTouch()) {
				cityLbl.setStyle("-fx-fill: white; ");
			} else {
				cityLbl.setStyle("-fx-fill: black; ");
			}
		}
		if (patient.getZip() != 0) {
			zipLbl.setText(String.valueOf(patient.getZip()));
			while (zipLbl.getText().length() < 4) {
				zipLbl.setText("0" + zipLbl.getText());
			}
		} else {
			if (screenControl.isTouch()) {
				zipLbl.setStyle("-fx-fill: white; ");
			} else {
				zipLbl.setStyle("-fx-fill: black; ");
			}
		}
	}

	private void loadDeathDetails(Patient patient) {
		dateOfDeathLabel.setText(patient.getDeathDate() == null ? "Not set"
				: patient.getDeathDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		deathLocation.setText(patient.getDeathStreet() == null || patient.getDeathStreet().length() == 0 ? "Not set"
				: patient.getDeathStreet());
		deathCity.setText(patient.getDeathCity() == null || patient.getDeathCity().length() == 0 ? "Not set"
				: patient.getDeathCity());
		deathRegion.setText(patient.getDeathRegion() == null ? "Not set" : patient.getDeathRegion().getValue());
	}

	private void loadBasicDetails(Patient patient) {
		nhiLbl.setText(patient.getNhiNumber());
		firstNameValue.setText(patient.getFirstName());
		genderDeclaration.setText("Birth Gender: ");
		genderStatus.setText(patient.getBirthGender() == null ? "Not set" : patient.getBirthGender().getValue());
		prefGenderLbl
				.setText(patient.getPreferredGender() == null ? "Not set" : patient.getPreferredGender().getValue());
		vitalLbl1.setText(patient.getDeathDate() == null ? "Alive" : "Deceased");
		dobLbl.setText(patient.getBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	}

	/**
	 * Highlights the listview cell if the organ donating is also required by the
	 * patient in clinician view. If in patient view, the listview cells are just
	 * styled.
	 *
	 * @param listView
	 *            The listView that the cells being highlighted are in
	 * @param isDonorList
	 *            boolean for if the list is the donating list or not
	 */
	private void highlightListCell(ListView<String> listView, boolean isDonorList) {
		listView.setCellFactory(column -> new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (!(userControl.getLoggedInUser() instanceof Patient)) {
					if (isDonorList) {
						setListInvalidStyle(item, receivingListProperty);
					} else {
						setListInvalidStyle(item, donatingListProperty);
					}
				} else {
					this.setText(item);
				}
			}

			private void setListInvalidStyle(String item, ListProperty<String> listProperty) {
				this.setText(item);
				if (item != null && !item.equals("")) {
					String[] itemArray = item.split(" \\|");
					String organ = itemArray[0];
					for (String listItem : listProperty) {
						if (listItem.contains(organ)) {
							this.getStyleClass().add("invalid");
							this.setStyle("-fx-background-color: #ff0000");
						}
					}
				}
			}
		});
	}

	/**
	 * Deletes the current profile from the HashSet in Database, not from disk, not
	 * until saved
	 */
	public void deleteProfile() {
		IAction action = new SingleAction(target, null);
		new AdministratorDataService().deleteUser(target);
		GlobalEnums.UndoableScreen undoableScreen;
		if (userControl.getLoggedInUser() instanceof Administrator) {
			undoableScreen = GlobalEnums.UndoableScreen.ADMINISTRATORPROFILE;
		} else {
			undoableScreen = GlobalEnums.UndoableScreen.CLINICIANPROFILE;
		}
		undoRedoControl.addAction(action, undoableScreen);
		userActions.log(Level.INFO, "Successfully deleted patient profile",
				new String[] { "Attempted to delete patient profile", ((Patient) target).getNhiNumber() });
		screenControl.closeWindow(patientProfilePane);
	}

}
