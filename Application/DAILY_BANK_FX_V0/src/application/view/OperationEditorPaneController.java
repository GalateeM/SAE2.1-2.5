package application.view;

/**
 * Fenetre de gestion des opérations (enregistrement débit ou crédit)
 */

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessCompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class OperationEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation[] operationResultat;
	
	//indique si l'operation est un debit exceptionnel (pas de vérification de dépassement de découvert)
	private boolean isDebitExceptionnel;

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
	 * Affiche la fenetre et attend une action, la fenetre change en fonction du type d'opération (débit ou crédit)
	 * @param cpte : le compte sur lequel l'opération sera ajoutée
	 * @param mode : le type d'opération (débit ou crédit)
	 * @return : tableau d'opérations : [0] correspond au compte qui fait le virement, et [1] correspond au compte destinataire
	 */
	public Operation[] displayDialog(CompteCourant cpte, CategorieOperation mode, boolean isDebitExceptionnel) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;
		this.isDebitExceptionnel = isDebitExceptionnel;
		String info;
		ObservableList<String> list;
		
		switch (mode) {
		
		case DEBIT:

			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);
			
			if(this.isDebitExceptionnel== false) {
				this.btnOk.setText("Effectuer Débit");
				this.btnCancel.setText("Annuler débit");
			} else {
				this.btnOk.setText("Effectuer Débit Exceptionnel");
				this.btnCancel.setText("Annuler débit exceptionnel");
			}
			

			list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_DEBIT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
			
		case CREDIT:
			
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer Crédit");
			this.btnCancel.setText("Annuler crédit");

			list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_CREDIT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			
			break;
			
		case VIREMENT :
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer Virement");
			this.btnCancel.setText("Annuler virement");
			setVisibleVirement();

			list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_VIREMENT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}

		this.operationResultat=new Operation[2]; //création du tableau d'opérations
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}
	
	/**
	 * Rend les champs correspondants au virement visible seulement en mode virement
	 */
	private void setVisibleVirement() {
		//affichage champs
		this.lblNumCompte.setVisible(true);
		this.txtNumCompte.setVisible(true);
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
	@FXML
	private Label lblNumCompte;
	@FXML
	private ComboBox<String> cbTypeOpe;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtNumCompte;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	/**
	 * Redéfinition de la fonction initialize
	 * Rend invisible les champs correspondants au virement
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.lblNumCompte.setVisible(false);
		this.txtNumCompte.setVisible(false);
	}
	
	/**
	 * Fonction appelée lors du clic sur "annuler"
	 * Annule l'opération (null) et ferme la fenetre
	 */
	@FXML
	private void doCancel() {
		this.operationResultat[0] = null;
		this.operationResultat[1] = null;
		this.primaryStage.close();
	}
	
	/** Fonction appelée lorsque l'utilisateur valide l'opération 
	 * Vérifie notamment les entrées (non vide, formatage, compte existant, etc..)
	 * Créer les opérations si tout est correct
	 */
	@FXML
	private void doAjouter() {
		double montant;
		String info;
		String typeOp;
		
		switch (this.categorieOperation) {
		case DEBIT :
			

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			
			if(this.isDebitExceptionnel== false) {
				// règles de validation d'un débit "normal" (pas exceptionnel) :
				// - le montant doit être un nombre valide
				// - et si l'utilisateur n'est pas chef d'agence,
				// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
				if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
					info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
							+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
							+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
					this.lblMessage.setText(info);
					this.txtMontant.getStyleClass().add("borderred");
					this.lblMontant.getStyleClass().add("borderred");
					this.lblMessage.getStyleClass().add("borderred");
					this.txtMontant.requestFocus();
					return;
				}

			}
			
			
			typeOp = this.cbTypeOpe.getValue();
			this.operationResultat[0] = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
			
		case CREDIT:
			
			// règle de validation d'un crédit :
			// - le montant doit être un nombre valide

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			typeOp = this.cbTypeOpe.getValue();
			this.operationResultat[0] = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
			
		case VIREMENT :
			// même chose que le case DEBIT + partie numéro compte destinataire

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			this.lblNumCompte.getStyleClass().remove("borderred");
			this.txtNumCompte.getStyleClass().remove("borderred");
			info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
				info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			typeOp = this.cbTypeOpe.getValue();
			this.operationResultat[0] = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			
			//partie numéro compte destinataire 
					
			AccessCompteCourant acc = new AccessCompteCourant();
			if (this.txtNumCompte.getText().equals("")) {
				this.lblNumCompte.getStyleClass().add("borderred");
				this.txtNumCompte.getStyleClass().add("borderred");
				this.txtNumCompte.requestFocus();
				return;
			}
			
			int numCompteDestinataire=Integer.parseInt(this.txtNumCompte.getText());
			CompteCourant destinataire=null;
			
			try {
				destinataire=acc.getCompteCourant(numCompteDestinataire);
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
			}
			
			if (destinataire==null) {
				info = "Le compte entré n'existe pas.";
				this.lblMessage.setText(info);
				this.txtNumCompte.getStyleClass().add("borderred");
				this.lblNumCompte.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtNumCompte.requestFocus();
				return;
			}
			
			if (destinataire.estCloture.equals("O")) {
				info = "Le compte entré est cloturé.";
				this.lblMessage.setText(info);
				this.txtNumCompte.getStyleClass().add("borderred");
				this.lblNumCompte.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtNumCompte.requestFocus();
				return;
			} 
			
			//opération destinataire
			this.operationResultat[1]=new Operation(-1, montant, null, null, numCompteDestinataire,ConstantesIHM.TYPE_OP_7);
			
			this.primaryStage.close();
			break;
		}
	}
}