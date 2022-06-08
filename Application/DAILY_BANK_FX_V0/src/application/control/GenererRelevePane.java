package application.control;

/**
 * Classe qui gère le controleur de la fenetre de gestion des comptes (ajout, modification d'un compte) et la lance
 */

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.CompteEditorPaneController;
import application.view.GenererRelevePaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

public class GenererRelevePane {

	private Stage primaryStage;
	private GenererRelevePaneController controller;

	/**
	 * Constructeur de la classe (permet de paramétrer la fenetre)
	 * @param _parentStage : la scene qui appelle cette scene
	 * @param _dbstate : la session de l'utilisateur connecté
	 */
	public GenererRelevePane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(CompteEditorPaneController.class.getResource("genererrelevepane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Génération d'un relevé mensuel");
			this.primaryStage.setResizable(false);

			this.controller = loader.getController();
			this.controller.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lance la fonction du controleur de la page d'ajout ou de modification des comptes pour afficher la scene
	 * @param client : le client auquel appartient le compte
	 * @param cpte : le compte à modifier (null si ajout)
	 * @param em : precise le mode d'edition (ajout, modification, suppression)
	 * @return le client modifié ou non
	 */
	public String[] doGenererDialog() {
		return this.controller.displayDialog();
	}
}
