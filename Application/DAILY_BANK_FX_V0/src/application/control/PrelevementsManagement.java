package application.control;

/**
 *  Classe qui gère le controleur de la fenetre de gestion des prélèvements (premiere page, liste des prélèvements) et la lance
 */

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.AccessCompteCourant;
import model.orm.AccessPrelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class PrelevementsManagement {
	
	private Stage primaryStage;
	private DailyBankState dbs;
	private PrelevementsManagementController pmc;
	private CompteCourant compteConcerne;
	
	/**
	 * Constructeur de la classe (permet de paramétrer la fenetre)
	 * @param _parentStage : la scene qui appelle cette scene
	 * @param _dbstate : la session de l'utilisateur connecté
	 * @param client : le client auquel appartient le compte dont on gère les prélèvements
	 * @param compte : le compte dont on gère les prélèvements
	 */
	public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.compteConcerne = compte;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					PrelevementsManagementController.class.getResource("prelevementsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements");
			this.primaryStage.setResizable(false);

			this.pmc = loader.getController();
			this.pmc.initContext(this.primaryStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PairsOfValue<CompteCourant, ArrayList<Prelevement>>  prelevementsDunCompte() {
		ArrayList<Prelevement> listeP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			AccessCompteCourant acc = new AccessCompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			AccessPrelevement ap = new AccessPrelevement();
			listeP = ap.getPrelevements(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeP = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeP = new ArrayList<>();
		}
		System.out.println(this.compteConcerne.solde);
		return new PairsOfValue<>(this.compteConcerne, listeP);
	}
	
	/**
	 * Appel la fonction d'affichage de la fenetre
	 */
	public void doPrelevementsManagementDialog() {
		this.pmc.displayDialog();
	}
	
	/** 
	 * Récupère les modifications et se connecte à la base de données pour modifier le prélèvement
	 * 
	 * @param p : le prélèvement à modifier
	 * @return le prélèvement modifié ou non, null si echec
	 */
	public Prelevement modifierPrelevement(Prelevement p) {
		PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		Prelevement prelResult = pep.doPrelevementEditorDialog(p, EditionMode.MODIFICATION);
		if (prelResult != null) {
			try {
				AccessPrelevement ap = new AccessPrelevement();
				ap.updatePrelevement(prelResult);
			} catch (DatabaseConnexionException exc) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, exc);
				ed.doExceptionDialog();
				prelResult = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				prelResult = null;
			}
		}
		return prelResult;
	}
	
	/** 
	 * Se connecte à la base de données pour supprimer un prélèvement
	 * 
	 * @param p : le prélèvement à supprimer
	 */
	public void supprimerPrelevement(Prelevement p) {
		try {
			AccessPrelevement ap = new AccessPrelevement();
			ap.deletePrelevement(p);
		} catch (DatabaseConnexionException exc) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, exc);
			ed.doExceptionDialog();
			this.primaryStage.close();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
		}
	}
	
	/**
	 * Récupère les informations de création d'un prélèvement et se connecte à la base de données
	 * 
	 * @param numCompteConcerne : numéro du compte dont on veut créer un prélèvement
	 * @return : le nouveau prélèvement, null si echec
	 */
	public Prelevement nouveauPrelevement(int numCompteConcerne) {
		Prelevement prelevement;
		PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		prelevement = pep.doPrelevementEditorDialog(null, EditionMode.CREATION);
		if (prelevement != null) {
			try {
				prelevement.setIdNumCompte(numCompteConcerne);
				AccessPrelevement ap = new AccessPrelevement();
				ap.insertPrelevement(prelevement);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelevement = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				prelevement = null;
			}
		}
		return prelevement;
	}

}
