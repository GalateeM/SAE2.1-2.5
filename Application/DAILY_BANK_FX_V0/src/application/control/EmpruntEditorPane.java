package application.control;

/**
 * Classe qui gère le controleur de la fenetre de simulation d'un emprunt
 */

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmpruntEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Emprunt;

public class EmpruntEditorPane {

	private Stage primaryStage;
	private DailyBankState dbs;
	private EmpruntEditorPaneController eepc;

	/**
	 * Constructeur de la classe (permet de paramétrer la fenetre)
	 * @param _parentStage : la scene qui appelle cette scene
	 * @param _dbstate : la session de l'utilisateur connecté
	 */
	public EmpruntEditorPane(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmpruntEditorPaneController.class.getResource("emprunteditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Simulation d'un emprunt");
			this.primaryStage.setResizable(false);

			this.eepc = loader.getController();
			this.eepc.initContext(this.primaryStage, _dbstate, this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lance la fonction du controleur de la page de simulation pour afficher la scene
	 *
	 */
	public void doEmpruntEditorDialog() {
		this.eepc.displayDialog();
	}
	
	/**
	 * Lance la fonction du controleur de la page de résultat de la simulation
	 */
	public void doResultat(Emprunt emp) {
		this.primaryStage.close();
		EmpruntResultat er = new EmpruntResultat(this.primaryStage, this.dbs);
		er.doEmpruntResultatDialog(emp);
	}
}
