package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

public class ComptesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ComptesManagement cm;

	// Fenêtre physique
	private Stage primaryStage;

	// Donnéees de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> olCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.olCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

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
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private Button btnVoirPrelevements;
	@FXML
	private Button btnGenererReleve;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			this.cm.gererOperations(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	@FXML
	private void doModifierCompte() {
	}
	
	@FXML
	private void doVoirPrelevements() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			this.cm.gererPrelevements(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}
	
	@FXML
	private void doGenererReleve() {		
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			this.cm.genererReleve(cpt);
		}
	}

	@FXML
	private void doSupprimerCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			
			if(cpt.solde != 0) {
				AlertUtilities.showAlert(primaryStage, "Impossible", "Suppression du compte impossible", "Le solde du compte doit être à 0€", AlertType.ERROR);
				return;
			}
			
			CompteCourant nvCompte = this.cm.cloturerCompte(cpt);
			
			if(nvCompte != null)
				cpt.estCloture = "O";

			this.olCompteCourant.set(selectedIndice, cpt);
		}
		
		this.validateComponentState();
	}

	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cm.creerCompte();
		if (compte != null) {
			this.olCompteCourant.add(compte);
		}
	}

	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cm.getComptesDunClient();
		this.olCompteCourant.clear();
		for (CompteCourant co : listeCpt) {
			this.olCompteCourant.add(co);
		}
	}

	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnModifierCompte.setDisable(true);
		this.btnSupprCompte.setDisable(true);

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			boolean estCloture = cpt.estCloture.equals("O");

			this.btnGenererReleve.setDisable(false);
			this.btnVoirOpes.setDisable(estCloture);
			this.btnSupprCompte.setDisable(estCloture);
		} else {
			this.btnGenererReleve.setDisable(true);
			this.btnVoirOpes.setDisable(true);
			this.btnSupprCompte.setDisable(true);
		}
	}
}
