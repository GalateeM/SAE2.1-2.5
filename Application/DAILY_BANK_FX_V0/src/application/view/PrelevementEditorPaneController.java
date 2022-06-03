package application.view;

/**
 * Fenetre d'édition des prélèvements (création ou modification)
 */

import java.net.URL;
import java.util.ResourceBundle;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Prelevement;

public class PrelevementEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;
	
	private EditionMode em;
	private Prelevement prelevementEdite;
	private Prelevement prelevementResult;

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
	 * Affiche les informations du prélèvement dans la nouvelle fenetre d'édition
	 * 
	 * @param prelevement : le prélèvement concerné
	 * @param mode : mode d'édition (création ou modification)
	 * @return le prélèvement 
	 */
	public Prelevement displayDialog(Prelevement prelevement, EditionMode mode) {
		this.em = mode;
		if (prelevement == null) {
			this.prelevementEdite = new Prelevement(0,0,0,"",0);
			
		} else {
			this.prelevementEdite = new Prelevement(prelevement);
		}
		this.prelevementResult = null;
		switch (mode) {
		case CREATION:
			this.txtBeneficiaire.setEditable(true);
			this.txtBeneficiaire.setDisable(false);
			this.lblMessage.setText("Création d'un nouveau prélèvement");
			this.btnOk.setText("Créer");
			this.btnCancel.setText("Annuler");
			break;
			
		case MODIFICATION:
			this.txtBeneficiaire.setEditable(false);
			this.txtBeneficiaire.setDisable(true);
			this.lblMessage.setText("Modification d'un prélèvement");
			this.btnOk.setText("Modifier");
			this.btnCancel.setText("Annuler");
			break;
		}
		// initialisation du contenu des champs
		this.txtBeneficiaire.setText(this.prelevementEdite.beneficiaire);
		this.txtJour.setText(""+this.prelevementEdite.jour);
		this.txtMontant.setText(""+this.prelevementEdite.montant);

		this.prelevementResult = null;
		this.primaryStage.showAndWait();
		return this.prelevementResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	/**
	 * Redéfinition de la fonction initialize
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	@FXML
	private TextField txtBeneficiaire;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtJour;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;
	@FXML
	private Label lblBeneficiaire;
	@FXML
	private Label lblJour;
	@FXML
	private Label lblMontant;
	@FXML
	private Label lblMessage;
	
	@FXML
	private void doCancel() {
		this.prelevementResult = null;
		this.primaryStage.close();
	}
	
	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.prelevementResult = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.prelevementResult = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.prelevementResult = this.prelevementEdite;
			this.primaryStage.close();
			break;
		}
	}
	
	private boolean isSaisieValide() {
		this.prelevementEdite.beneficiaire = this.txtBeneficiaire.getText().trim();
		this.prelevementEdite.jour = Integer.parseInt(this.txtJour.getText().trim());
		this.prelevementEdite.montant = Double.parseDouble(this.txtMontant.getText().trim());
		
		if (this.prelevementEdite.beneficiaire.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le beneficiaire ne doit pas être vide",
					AlertType.WARNING);
			this.txtBeneficiaire.requestFocus();
			return false;
		}
		if (this.prelevementEdite.jour<1 || this.prelevementEdite.jour>28) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le jour doit être compris entre 1 et 28",
					AlertType.WARNING);
			this.txtJour.requestFocus();
			return false;
		}
		if (this.prelevementEdite.montant<0) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le montant doit être positif",
					AlertType.WARNING);
			this.txtMontant.requestFocus();
			return false;
		}
		return true;
	}
}