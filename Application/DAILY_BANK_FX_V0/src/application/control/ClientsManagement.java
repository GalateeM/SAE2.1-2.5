package application.control;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 * Classe qui gère le controleur de la fenetre de gestion des clients (premiere page, liste de clients) et la lance
 */

import java.util.ArrayList;
import java.util.HashMap;

import com.itextpdf.text.DocumentException;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.PdfUtilities;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Operation;
import model.orm.AccessClient;
import model.orm.AccessOperation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class ClientsManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private ClientsManagementController cmc;

	/**
	 * Constructeur de la classe (permet de paramétrer la fenetre)
	 * @param _parentStage : la scene qui appelle cette scene
	 * @param _dbstate : la session de l'utilisateur connecté
	 */
	public ClientsManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clientsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des clients");
			this.primaryStage.setResizable(false);

			this.cmc = loader.getController();
			this.cmc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lance la fonction du controleur de la page de gestion des clients pour afficher la scene
	 */
	public void doClientManagementDialog() {
		this.cmc.displayDialog();
	}

	/**
	 * Permet de modifier un client en lançant une autre fenêtre de gestion des clients (ClientEditor)
	 * @param c : le client sélectionné à modifier
	 * @return : le client avec les informations modifiées
	 */
	public Client modifierClient(Client c) {
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dbs);
		Client result = cep.doClientEditorDialog(c, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessClient ac = new AccessClient();
				ac.updateClient(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}

	/**
	 * Permet de modifier un client en lançant une autre fenêtre de gestion des clients (ClientEditor)
	 * @param c : le client sélectionné à modifier
	 * @return : le client avec les informations modifiées
	 */
	public Client desactiverClient(Client c) {
		if(c == null)
			return null;
		
		try {
			c.estInactif = "O";
			
			AccessClient ac = new AccessClient();
			ac.updateClient(c);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			return null;
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			return null;
		}
		
		return c;
	}

	/**
	 * Permet de créer un client en lançant une autre fenêtre de gestion des clients (ClientEditor)
	 * @return : le client créé
	 */
	public Client nouveauClient() {
		Client client;
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dbs);
		client = cep.doClientEditorDialog(null, EditionMode.CREATION);
		if (client != null) {
			try {
				AccessClient ac = new AccessClient();

				ac.insertClient(client);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				client = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				client = null;
			}
		}
		return client;
	}

	/** 
	 * Permet de gérer les comptes d'un client, lance la fenetre gestion des comptes
	 * @param c : le client sélectionné dont on souhaite gérer les comptes
	 */
	public void gererComptesClient(Client c) {
		ComptesManagement cm = new ComptesManagement(this.primaryStage, this.dbs, c);
		cm.doComptesManagementDialog();
	}
	
	public void genererReleves() {		
		GenererRelevePane cep = new GenererRelevePane(this.primaryStage, this.dbs);
		String[] data = cep.doGenererDialog();
		
		if(data[0] == null || data[1] == null || data[2] == null)
			return;
		
		try {
			AccessOperation acc = new AccessOperation();				
			
			String mois = String.format("%02d", Integer.valueOf(data[0]));
			String annee = data[1];
			String dest = data[2];
			
			ArrayList<Operation> operations = acc.getOperations(mois, annee);
		
			HashMap<Integer, ArrayList<Operation>> liste = new HashMap<Integer, ArrayList<Operation>>();
			
			for(Operation o : operations) {
				if(liste.get(o.idNumCompte) == null)
					liste.put(o.idNumCompte, new ArrayList<Operation>());

				liste.get(o.idNumCompte).add(o);
			}

			for(int id : liste.keySet()) {							
				String chemin = Paths.get(dest, "releve_" + id + "_" + mois + "_" + annee + ".pdf").toString();
			
				try {
					PdfUtilities.genererReleve(chemin, id, liste.get(id));
				} catch (FileNotFoundException | DocumentException e) {
					AlertUtilities.showAlert(primaryStage, "Erreur", "Impossible de sauvegarder", "Une erreur est survenue lors de la sauvegarde du relevé mensuel", AlertType.ERROR);
				}
			}
			
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
	 * Permet de rechercher la liste des comptes en fonction du paramètre de recherché utilisé
	 * @param _numCompte : numéro du compte recherché
	 * @param _debutNom : debut (ou totalité) du nom du client auquel appartient le compte
	 * @param _debutPrenom : debut (ou totalité) du prénom du client auquel appartient le compte
	 * @return : une liste de compte correspondants à la recherche
	 */
	public ArrayList<Client> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Client> listeCli = new ArrayList<>();
		try {
			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte != -1 et debutNom non vide => recherche nom/prenom
			// numCompte != -1 et debutNom vide => recherche tous les clients

			AccessClient ac = new AccessClient();
			listeCli = ac.getClients(this.dbs.getEmpAct().idAg, _numCompte, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCli = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeCli = new ArrayList<>();
		}
		return listeCli;
	}
}
