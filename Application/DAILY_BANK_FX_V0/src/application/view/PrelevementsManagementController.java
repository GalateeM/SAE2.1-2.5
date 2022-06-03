package application.view;

/**
 * Fenetre de gestion des prélèvements (première page, liste des prélèvements)
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.PrelevementsManagement;
import application.tools.AlertUtilities;
import application.tools.PairsOfValue;
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
import model.data.Prelevement;

public class PrelevementsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private PrelevementsManagement pm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Prelevement> olPrelevement;

	// Manipulation de la fenêtre
	/**
	 * Définit les variables de la fenetre
	 * 
	 * @param _primaryStage : scene
	 * @param _om : fenetre
	 * @param _dbstate : données de la session de l'utilisateur
	 * @param client : client du compte
	 * @param compte : compte courant du client 
	 */
	public void initContext(Stage _primaryStage, PrelevementsManagement _pm, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.pm = _pm;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olPrelevement = FXCollections.observableArrayList();
		this.lvPrelevements.setItems(this.olPrelevement);
		this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevements.getFocusModel().focus(-1);
		this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
		this.updateInfoPrelevementsCompte();
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
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Prelevement> lvPrelevements;
	@FXML
	private Button btnNouveauPrel;
	@FXML
	private Button btnModifierPrel;
	@FXML
	private Button btnSupprimerPrel;

	
	/**
	 * Redéfinition de la fonction initialize
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	/** Fonction appelée lors du clic sur "annuler"
	 *  Elle ferme la fenetre
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	@FXML
	private void doNouveauPrel() {
		Prelevement prelevement;
		prelevement = this.pm.nouveauPrelevement(this.compteConcerne.idNumCompte);
		if (prelevement != null) {
			this.olPrelevement.add(prelevement);
		}
		this.updateInfoPrelevementsCompte();
	}
	
	@FXML
	private void doModifierPrel() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelEdite = this.olPrelevement.get(selectedIndice);
			Prelevement prelResult = this.pm.modifierPrelevement(prelEdite);
			if (prelResult != null) {
				this.olPrelevement.set(selectedIndice, prelResult);
			}
		}
		this.updateInfoPrelevementsCompte();
	}
	
	@FXML
	private void doSupprimerPrel() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelEdite = this.olPrelevement.get(selectedIndice);
			boolean continuer = AlertUtilities.confirmYesCancel(primaryStage, "Supprimer le prélèvement", "Supprimer le prélèvement numéro " + prelEdite.idPrelev, "êtes-vous sûr de vouloir supprimer ce prélèvement ?", AlertType.CONFIRMATION);
			if(continuer) {
				this.pm.supprimerPrelevement(prelEdite);
				this.olPrelevement.remove(selectedIndice);
			}
		}
		this.updateInfoPrelevementsCompte();
	}
	
	/** Mets à jour les informations affichées (infos du clients, des opérations, du solde, débit autorisé, etc...)
	 * 
	 */
	private void updateInfoPrelevementsCompte() {

		PairsOfValue<CompteCourant, ArrayList<Prelevement>> opesEtCompte;

		opesEtCompte = this.pm.prelevementsDunCompte();

		ArrayList<Prelevement> listeP;
		this.compteConcerne = opesEtCompte.getLeft();
		listeP = opesEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.olPrelevement.clear();
		for (Prelevement p : listeP) {
			this.olPrelevement.add(p);
		}

	}
	
	private void validateComponentState() {
		this.btnModifierPrel.setDisable(true);
		this.btnSupprimerPrel.setDisable(true);
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifierPrel.setDisable(false);
			this.btnSupprimerPrel.setDisable(false);
		}
	}
}