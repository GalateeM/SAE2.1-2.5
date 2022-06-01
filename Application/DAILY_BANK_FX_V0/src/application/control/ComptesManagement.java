package application.control;

/**
 * Classe qui gère le controleur de la fenetre de gestion des comptes (premiere page, liste de comptes) et la lance
 */

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
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
import model.orm.exception.Order;
import model.orm.exception.Table;

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmc;
	private DailyBankState dbs;
	private Client clientDesComptes;

	/**
	 * Constructeur de la classe (permet de paramétrer la fenetre)
	 * @param _parentStage : la scene qui appelle cette scene
	 * @param _dbstate : la session de l'utilisateur connecté
	 * @param client : le client dont on gère les comptes
	 */
	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmc = loader.getController();
			this.cmc.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lance la fonction du controleur de la page de gestion des comptes pour afficher la scene
	 */
	public void doComptesManagementDialog() {
		this.cmc.displayDialog();
	}

	/**
	 * Permet de gérer les opérations d'un compte (affiche la scene correspondante)
	 * @param cpt : le compte dont on souhaite gérer les opérations
	 */
	public void gererOperations(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dbs, this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	/**
	 * Permet de créer un compte (ouvre la scene d'ajout d'un compte)
	 * @return : le compte courant créé
	 */
	public CompteCourant creerCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);		

		if(compte == null)
			return compte;
		
		if(compte.solde < 50)
			return null;
		
		if(compte.debitAutorise < 0)
			return null;
		
		try {
			AccessCompteCourant acc = new AccessCompteCourant();				
			acc.insertCompte(compte);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			return null;
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			return null;
		}
		
		return compte;
	}

	/**
	 * Permet de créer un compte (ouvre la scene d'ajout d'un compte)
	 * @return : le compte courant clôturé
	 */
	public CompteCourant cloturerCompte(CompteCourant compte) {
		if(compte == null)
			return compte;
		
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);
		CompteCourant nvCompte = cep.doCompteEditorDialog(this.clientDesComptes, compte, EditionMode.SUPPRESSION);
		
		if(nvCompte == null)
			return null;
		
		try {
			AccessCompteCourant acc = new AccessCompteCourant();				
			acc.cloturerCompte(compte);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
		}
		
		return compte;
	}

	/**
	 * Permet de générer le relevé d'un compte (ouvre la scene de choix du mois et de l'année)
	 * @return : le compte courant clôturé
	 */
	public void genererReleve(CompteCourant compte) {
		if(compte == null)
			return;
		
		GenererRelevePane cep = new GenererRelevePane(this.primaryStage, this.dbs);
		int[] date = cep.doGenererDialog();
		
		if(date[0] == -1 || date[1] == -1)
			return;
		
		try {
			AccessOperation acc = new AccessOperation();				
			ArrayList<Operation> operations = acc.getOperations(compte.idNumCompte, date[0], date[1]);
			
			System.out.println("opérations à utiliser : " + operations.size());
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
		}
		
		return;
	}

	/**
	 * Permet d'obtenir la liste des comptes courants d'un client
	 * @return : la liste des comptes courants
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			AccessCompteCourant acc = new AccessCompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}
}
