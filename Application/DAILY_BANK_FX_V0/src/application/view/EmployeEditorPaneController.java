package application.view;

/**
 * Fenetre de gestion des employés (ajout ou modification)
 */

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class EmployeEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Employe employeEdite;
	private EditionMode em;
	private Employe employeResult;

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
	 * Affiche la fenetre et attend une action, la fenetre change en fonction du mode (ajout, modification)
	 * @param employe : l'employé qui est modifié, ou null s'il s'agit d'un ajout
	 * @param mode : le mode d'edition (ajout, modification)
	 * @return : l'employé modifié ou non
	 */
	public Employe displayDialog(Employe employe, EditionMode mode) {

		this.em = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dbs.getEmpAct().idAg);
			
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeResult = null;
		switch (mode) {
		case CREATION:
			this.txtIdEmp.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.rbGuichetier.setSelected(true);
			this.rbChefAg.setSelected(false);
			this.txtLogin.setDisable(false);
			this.txtPassword1.setDisable(false);
			this.txtPassword2.setDisable(false);
			this.txtIdAg.setDisable(true);
			
			
			this.lblMessage.setText("Informations sur le nouvel employé");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
			
		case MODIFICATION:
			this.txtIdEmp.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.rbGuichetier.setSelected(true);
			this.rbChefAg.setSelected(false);
			this.txtLogin.setDisable(false);
			this.txtPassword1.setDisable(false);
			this.txtPassword2.setDisable(false);
			this.txtIdAg.setDisable(true);
			
			this.lblMessage.setText("Informations employés");
			this.butOk.setText("Modifier");
			this.butCancel.setText("Annuler");
			break;

		}

		// initialisation du contenu des champs
		this.txtIdEmp.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtLogin.setText(this.employeEdite.login);
		this.txtPassword1.setText(this.employeEdite.motPasse);
		this.txtPassword2.setText(this.employeEdite.motPasse);
		this.txtIdAg.setText(this.employeEdite.idAg+"");

		if (ConstantesIHM.isAdmin(this.employeEdite)) {
			this.rbChefAg.setSelected(true);
			this.rbGuichetier.setSelected(false);
		} else {
			this.rbChefAg.setSelected(false);
			this.rbGuichetier.setSelected(true);
		}

		this.employeResult = null;

		this.primaryStage.showAndWait();
		return this.employeResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdEmp;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private RadioButton rbGuichetier;
	@FXML
	private RadioButton rbChefAg;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtIdAg;
	@FXML
	private PasswordField txtPassword1;
	@FXML
	private PasswordField txtPassword2;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	/**
	 * Redéfinition de la fonction initialize
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.employeResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
			break;
		}

	}

	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLogin.getText().trim();
		this.employeEdite.motPasse = this.txtPassword1.getText().trim();
		this.employeEdite.idAg = Integer.parseInt(this.txtIdAg.getText().trim());
		if (this.rbGuichetier.isSelected()) {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_GUICHETIER;
		} else {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_CHEF;
		}

		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}
		
		if (this.employeEdite.login.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide",
					AlertType.WARNING);
			this.txtLogin.requestFocus();
			return false;
		}

		if (this.txtPassword1.getText().trim().isEmpty() || this.txtPassword1.getText().trim().isEmpty() || !this.txtPassword1.getText().equals(txtPassword2.getText())) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Les mots de passe doivent correspondre et ne pas être vides",
					AlertType.WARNING);
			this.txtPassword1.requestFocus();
			return false;
		}
		
		if (this.txtIdAg.getText().trim()=="") {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "L'ID Agence ne doit pas être vide",
					AlertType.WARNING);
			this.txtIdAg.requestFocus();
			return false;
		}

		

		return true;
	}
}
