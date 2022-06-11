package application.view;

/**
 * Fenetre de simulation d'un emprunt
 */

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.EmpruntEditorPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Emprunt;

public class EmpruntEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private EmpruntEditorPane eep;

	// Fenêtre physique
	private Stage primaryStage;


	// Manipulation de la fenêtre
	/**
	 * Définit les variables de la fenetre
	 * 
	 * @param _primaryStage : scene
	 * @param _dbstate : données de la session de l'utilisateur
	 */
	public void initContext(Stage _primaryStage, DailyBankState _dbstate, EmpruntEditorPane _eep) {
		this.eep = _eep;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Affiche la fenetre et attend une action
	 */
	public void displayDialog() {

		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblCapital;
	@FXML
	private Label lblDuree;
	@FXML
	private Label lblTaux;
	@FXML
	private Label lblAssurance;
	@FXML
	private TextField txtCapital;
	@FXML
	private TextField txtDuree;
	@FXML
	private TextField txtTaux;
	@FXML
	private ChoiceBox<String> periodicite;
	@FXML
	private TextField txtAssurance;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	/**
	 * Redéfinition de la fonction initialize, ajoute les options "mensuelle" et "annuelle" à la choicebox
	 * Sélectionne mensuelle par défaut
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		periodicite.getItems().add("mensuelle");
		periodicite.getItems().add("annuelle");
		periodicite.getSelectionModel().select(0);
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doSimuler() {
		Emprunt emp = isSaisieValide();
		if(emp!=null) {
			this.eep.doResultat(emp);
		}
	}

	//vérifie la saisie
	private Emprunt isSaisieValide() {
		Emprunt emp = null;
		boolean estValide = true;
		double capital = 0;
		double taux =0;
		double tauxAssurance = 0;
		String periodicite="";
		int duree=0;
		//Verif Capital
		this.txtCapital.getStyleClass().remove("borderred");
		this.lblCapital.getStyleClass().remove("borderred");
		this.txtCapital.getStyleClass().remove("borderred");
		try {
			capital = Double.parseDouble(this.txtCapital.getText().trim());
			if (capital <= 0)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			this.txtCapital.getStyleClass().add("borderred");
			this.lblCapital.getStyleClass().add("borderred");
			this.txtCapital.requestFocus();
			estValide = false;
		}

		//Verif Durée
		this.txtDuree.getStyleClass().remove("borderred");
		this.lblDuree.getStyleClass().remove("borderred");
		this.txtDuree.getStyleClass().remove("borderred");
		try {
			duree = Integer.parseInt(this.txtDuree.getText().trim());
			if (duree <= 0)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			this.txtDuree.getStyleClass().add("borderred");
			this.lblDuree.getStyleClass().add("borderred");
			this.txtDuree.requestFocus();
			estValide = false;
		}

		//Verif Taux
		this.txtTaux.getStyleClass().remove("borderred");
		this.lblTaux.getStyleClass().remove("borderred");
		this.txtTaux.getStyleClass().remove("borderred");
		try {
			taux = Double.parseDouble(this.txtTaux.getText().trim());
			if (taux <= 0 || taux >=100)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			this.txtTaux.getStyleClass().add("borderred");
			this.lblTaux.getStyleClass().add("borderred");
			this.txtTaux.requestFocus();
			estValide = false;
		}

		//Verif Assurance
		this.txtAssurance.getStyleClass().remove("borderred");
		this.lblAssurance.getStyleClass().remove("borderred");
		this.txtAssurance.getStyleClass().remove("borderred");
		try {
			tauxAssurance = Double.parseDouble(this.txtAssurance.getText().trim());
			if (tauxAssurance < 0 || tauxAssurance >=100)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			this.txtAssurance.getStyleClass().add("borderred");
			this.lblAssurance.getStyleClass().add("borderred");
			this.txtAssurance.requestFocus();
			estValide = false;
		}
		periodicite = this.periodicite.getValue();
		if(estValide==true) {
			emp = new Emprunt(capital, duree, taux, periodicite, tauxAssurance);
		}
		return emp;
	}
}
