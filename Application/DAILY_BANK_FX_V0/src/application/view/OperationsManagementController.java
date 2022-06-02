package application.view;

/**
 * Fenetre de gestion des clients (première page, liste des opérations)
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class OperationsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private OperationsManagement om;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Operation> olOperation;
	
	private boolean debitExceptionnel;

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
	public void initContext(Stage _primaryStage, OperationsManagement _om, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.om = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.debitExceptionnel = false;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olOperation = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.olOperation);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
		this.lvOperations.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
		this.updateInfoCompteClient();
	}
	
	

	private void validateComponentState() {
		if(this.dbs.isChefDAgence()) {
			this.btnDebitExceptionnel.setDisable(false);
		} 	
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
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;
	@FXML
	private Button btnDebitExceptionnel;

	
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
	
	/** Fonction appelée lors du clic sur "enregistrer débit"
	 *  Créer une opération de débit sur le compte concerné et actualise les informations
	 */
	@FXML
	private void doDebit() {
		this.debitExceptionnel = false;
		Operation op = this.om.enregistrerDebit(this.debitExceptionnel);
		if (op != null) {
			this.updateInfoCompteClient();
		}
	}
	

	@FXML
	private void doDebitExceptionnel() {
		this.debitExceptionnel = true;
		Operation op = this.om.enregistrerDebit(this.debitExceptionnel);
		if (op != null) {
			this.updateInfoCompteClient();
		}
	}
	
	/** Fonction appelé lors du clic sur "enregistrer crédit"
	 *  Créer une opération de crédit sur le compte concerné et actualise les informations
	 */
	@FXML
	private void doCredit() {
		Operation op = this.om.enregistrerCredit();
		if (op != null) {
			this.updateInfoCompteClient();
		}
	}
	
	/** Fonction appelé lors du clic sur "effectuer un virement"
	 *  Créer une opération de virement sur les comptes concernés et actualise les informations
	 *  Operation [0] correspond à celui qui effectue le virement, Operation [1] correspond au compte destinataire
	 */
	@FXML
	private void doAutre() {
		Operation[] op = this.om.enregistrerVirement();
		if (op[0] != null) {
			this.updateInfoCompteClient();
		}
	}
	
	/** Mets à jour les informations affichées (infos du clients, des opérations, du solde, débit autorisé, etc...)
	 * 
	 */
	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte;

		opesEtCompte = this.om.operationsEtSoldeDunCompte();

		ArrayList<Operation> listeOP;
		this.compteConcerne = opesEtCompte.getLeft();
		listeOP = opesEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.olOperation.clear();
		for (Operation op : listeOP) {
			this.olOperation.add(op);
		}

	}
}