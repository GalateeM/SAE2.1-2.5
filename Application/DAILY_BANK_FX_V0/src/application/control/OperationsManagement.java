package application.control;

/**
 * Classe qui gère le controleur de la fenetre de gestion des opérations (premiere page, liste d'opérations) et la lance
 */

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class OperationsManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private OperationsManagementController omc;
	private Client clientDuCompte;
	private CompteCourant compteConcerne;

	/**
	 * Constructeur de la classe (permet de paramétrer la fenetre)
	 * @param _parentStage : la scene qui appelle cette scene
	 * @param _dbstate : la session de l'utilisateur connecté
	 * @param client : le client auquel appartient le compte dont on gère les opérations
	 * @param compte : le compte dont on gère les modifications
	 */
	public OperationsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("operationsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des opérations");
			this.primaryStage.setResizable(false);

			this.omc = loader.getController();
			this.omc.initContext(this.primaryStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lance la fonction du controleur de la page de gestion des opérations pour afficher la scene
	 */
	public void doOperationsManagementDialog() {
		this.omc.displayDialog();
	}

	/**
	 * Permet d'enregistrer un nouveau débit (ou débit exceptionnel) sur le compte d'un client
	 * @param isDebitExceptionnel : true s'il s'agit d'un débit exceptionnel, false sinon
	 * @return : l'opération créé (le nouveau débit)
	 */
	public Operation enregistrerDebit(boolean isDebitExceptionnel) {
		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dbs);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT, isDebitExceptionnel)[0];
		if (op != null) {
			if (isDebitExceptionnel==false) {
				try {
					AccessOperation ao = new AccessOperation();
					ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

				} catch (DatabaseConnexionException e) {
					ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
					ed.doExceptionDialog();
					this.primaryStage.close();
					op = null;
				} catch (ApplicationException ae) {
					ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
					ed.doExceptionDialog();
					op = null;
				}
			} else {
				try {
					AccessOperation ao = new AccessOperation();
					ao.insertDebitExceptionnel(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

				} catch (DatabaseConnexionException e) {
					ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
					ed.doExceptionDialog();
					this.primaryStage.close();
					op = null;
				} catch (ApplicationException ae) {
					ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
					ed.doExceptionDialog();
					op = null;
				}
			}
		}
		return op;
	}
	
	
	/**
	 * Permet d'enregistrer un nouveau crédit sur le compte d'un client
	 * @return : l'opération créé (le nouveau crédit)
	 */
	public Operation enregistrerCredit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dbs);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT, false)[0];
		if (op != null) {
			try {
				AccessOperation ao = new AccessOperation();
				ao.insertCredit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	
	/**
	 * Permet d'enregistrer un nouveau virement sur le compte d'un client
	 * @return : tableau d'opérations : [0] correspond au compte qui fait le virement, et [1] correspond au compte destinataire
	 */
	public Operation[] enregistrerVirement() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dbs);
		Operation[] op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.VIREMENT, false);
		if (op != null) {
			try {
				AccessOperation ao = new AccessOperation();
				ao.insertDebit(this.compteConcerne.idNumCompte, op[0].montant, op[0].idTypeOp);
				ao.insertCredit(op[1].idNumCompte, op[1].montant, op[1].idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}

	/**
	 * Permet d'obtenir la liste des opérations en fonction d'un compte courant
	 * @return : une paire de valeur comprenant le compte et sa liste d'opérations
	 */
	public PairsOfValue<CompteCourant, ArrayList<Operation>>  operationsEtSoldeDunCompte() {
		ArrayList<Operation> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			AccessCompteCourant acc = new AccessCompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			AccessOperation ao = new AccessOperation();
			listeOP = ao.getOperations(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeOP = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeOP = new ArrayList<>();
		}
		System.out.println(this.compteConcerne.solde);
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
}