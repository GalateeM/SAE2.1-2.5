package application.view;

import java.lang.reflect.Array;

/**
 * Fenetre de génération d'un relevé
 */

import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GenererRelevePaneController implements Initializable {

	private String[] listeMois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

	
	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private int mois;
	private int annee;

	// Manipulation de la fenêtre
	/**
	 * Définit les variables de la fenetre
	 * 
	 * @param _primaryStage : scene
	 * @param _dbstate : données de la session de l'utilisateur
	 */
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Affiche la fenetre et attend une action
	 * @return : le mois et l'année
	 */
	public int[] displayDialog() {	
		int anneeActuelle = Calendar.getInstance().get(Calendar.YEAR);
		this.mois = -1;
		this.annee = -1;
		
		this.selectMois.getItems().addAll("Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre");
		
		for(int i = 2022; i <= anneeActuelle; i++)
			this.selectAnnee.getItems().add(i);
		
		this.primaryStage.showAndWait();
		
		int[] resultat = new int[2];
		resultat[0] = this.mois;
		resultat[1] = this.annee;
		
		return resultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Button btnGenerer;
	@FXML
	private Button btnAnnuler;
	@FXML
	private ComboBox<String> selectMois;
	@FXML
	private ComboBox<Integer> selectAnnee;
	
	/**
	 * Redéfinition de la fonction initialize
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.mois = -1;
		this.annee = -1;
		this.primaryStage.close();
	}

	@FXML
	private void doGenerer() {
		if(this.mois == -1 || this.annee == -1) {
			AlertUtilities.showAlert(primaryStage, "Impossible", "Saisie invalide", "Vous devez sélectionner un mois et une année", AlertType.ERROR);
			return;
		}
		
		this.primaryStage.close();

	}
	
	@FXML
	private void doSelectMois() {
		this.mois = Arrays.asList(listeMois).indexOf(selectMois.getValue()) + 1;
	}
	
	@FXML
	private void doSelectAnnee() {
		this.annee = selectAnnee.getValue();
	}
}
