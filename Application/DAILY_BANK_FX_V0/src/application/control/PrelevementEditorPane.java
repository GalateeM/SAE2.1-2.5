package application.control;

/**
 * Classe qui gère le controleur de la fenetre de gestion des prélèvements (ajout ou modification d'un prélèvement) et la lance
 */

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Prelevement;

public class PrelevementEditorPane {
	private Stage primaryStage;
	private PrelevementEditorPaneController pepc;

	/**
	 * Constructeur de la classe (permet de paramétrer la fenetre)
	 * @param _parentStage : la scene qui appelle cette scene
	 * @param _dbstate : la session de l'utilisateur connecté
	 */
	public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate) {
		try {
			FXMLLoader loader = new FXMLLoader(PrelevementEditorPaneController.class.getResource("prelevementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un prélèvement");
			this.primaryStage.setResizable(false);

			this.pepc = loader.getController();
			this.pepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lance la fonction du controleur de la page d'ajout ou de modification des prélèvements pour afficher la scene
	 *
	 * @param prélèvement : le prélèvement que l'on modifie
	 * @param em : precise le mode d'edition (ajout, modification, suppression)
	 * @return le prélèvement modifié ou non
	 */
	public Prelevement doPrelevementEditorDialog(Prelevement prelevement, EditionMode em) {
		return this.pepc.displayDialog(prelevement, em);
	}
}
