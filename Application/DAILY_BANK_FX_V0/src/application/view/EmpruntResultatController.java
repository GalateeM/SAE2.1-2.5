package application.view;

/**
 * Fenetre de résultat de simulation d'un emprunt
 */

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Emprunt;
import model.data.LigneEmprunt;

public class EmpruntResultatController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;
	private Emprunt emprunt;


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
	 * Affiche la fenetre et attend une action
	 */
	public void displayDialog(Emprunt emp) {
		this.emprunt=emp;
		this.lblTitre.setText("Capital emprunté : "+this.emprunt.capital+"\tDurée : "+this.emprunt.duree+"\tTaux annuel : "+this.emprunt.taux+"\tPeriodicité : "+this.emprunt.periodicite);
		if (this.emprunt.periodicite.compareTo("mensuelle")==0) {
			this.emprunt.tauxApplicable = this.emprunt.taux/12/100;
			this.emprunt.tauxApplicable = Math.round(this.emprunt.tauxApplicable*1000000.0)/1000000.0;
			this.emprunt.nombrePeriode = this.emprunt.duree*12;
		} else {
			this.emprunt.tauxApplicable = this.emprunt.taux/100;
			this.emprunt.tauxApplicable = Math.round(this.emprunt.tauxApplicable*100.0)/100.0;
			this.emprunt.nombrePeriode = this.emprunt.duree;
		}
		this.lblTitre2.setText("Taux applicable : "+this.emprunt.tauxApplicable+"\t\tNombre de période : "+this.emprunt.nombrePeriode);

		TableView<LigneEmprunt> table = this.tvLE;

		// Creation des colonnes
		TableColumn<LigneEmprunt, String> periode = new TableColumn<LigneEmprunt, String>("Numéro période");
		TableColumn<LigneEmprunt, String> capDebut = new TableColumn<LigneEmprunt, String>("Capital Restant du en \ndébut de période");
		TableColumn<LigneEmprunt, String> interet = new TableColumn<LigneEmprunt, String>("Montant des intérêts");
		TableColumn<LigneEmprunt, String> principal = new TableColumn<LigneEmprunt, String>("Montant du principal");
		TableColumn<LigneEmprunt, String> mensualite= new TableColumn<LigneEmprunt, String>("Montant à rembourser\n(Mensualité)");
		TableColumn<LigneEmprunt, String> capFin = new TableColumn<LigneEmprunt, String>("Capital Restant du en \nfin de période");


		// Definission des colonnes (comment les remplir)
		periode.setCellValueFactory(new PropertyValueFactory<>("periode"));
		capDebut.setCellValueFactory(new PropertyValueFactory<>("capDebut"));
		interet.setCellValueFactory(new PropertyValueFactory<>("interet"));
		principal.setCellValueFactory(new PropertyValueFactory<>("principal"));
		mensualite.setCellValueFactory(new PropertyValueFactory<>("mensualite"));
		capFin.setCellValueFactory(new PropertyValueFactory<>("capFin"));

		// Liste observable des lignes d'emprunt
		ObservableList<LigneEmprunt> list = getLignes();
		table.setItems(list);

		table.getColumns().addAll(periode, capDebut, interet, principal, mensualite, capFin);
		
		
		//partie mensualite
		double tauxAssurance = this.emprunt.tauxAssurance;
		if(tauxAssurance==0) {
			this.valeurTauxAssurance.setText("0");
			this.valeurTotAssurance.setText("0");
		} else {
			this.valeurTauxAssurance.setText(""+tauxAssurance);
			this.titreMensualite.setVisible(true);
			this.valeurMensualite.setVisible(true);
			double mensualiteAssurance = tauxAssurance/100*this.emprunt.capital/12;
			this.valeurMensualite.setText(""+mensualiteAssurance);
			this.titreDuree.setVisible(true);
			this.valeurDuree.setVisible(true);
			this.valeurDuree.setText(""+this.emprunt.nombrePeriode);
			this.valeurTotAssurance.setText(""+this.emprunt.nombrePeriode*mensualiteAssurance);
		}


		this.primaryStage.showAndWait();
	}

	/** Fonction permettant de récupérer les lignes de la simulation
	 * 
	 * @return observableList des lignes
	 */
	private ObservableList<LigneEmprunt> getLignes() {
		ObservableList<LigneEmprunt> list = FXCollections.observableArrayList();
		
		double tauxApplicable = this.emprunt.tauxApplicable;
		double capDebut = this.emprunt.capital;
		int nbPeriode = this.emprunt.nombrePeriode;
		LigneEmprunt [] tabLignes = new LigneEmprunt [nbPeriode];
		for (int i=0; i<nbPeriode; i++) {
			tabLignes[i] = new LigneEmprunt(0, 0, 0, 0, 0, 0);
			if (i==0) {
				tabLignes[i].setCapDebut(capDebut);
			} else {
				
				tabLignes[i].setCapDebut((double)Math.round((tabLignes[i-1].getCapFin())*100)/100);
			}
			tabLignes[i].setPeriode(i+1);
			tabLignes[i].setInteret((double)Math.round((tauxApplicable*tabLignes[i].getCapDebut())*100)/100);
			tabLignes[i].setMensualite ((double)Math.round((capDebut*(tauxApplicable/(1-Math.pow((1+tauxApplicable),-nbPeriode))))*100)/100);
			tabLignes[i].setPrincipal((double)Math.round((tabLignes[i].getMensualite()-tabLignes[i].getInteret())*100)/100);
			tabLignes[i].setCapFin((double)Math.round((tabLignes[i].getCapDebut()-tabLignes[i].getPrincipal())*100)/100);
			list.add(tabLignes[i]);
		}
		
		

		return list;

	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblTitre ;
	@FXML
	private Label lblTitre2;
	@FXML
	private TableView<LigneEmprunt> tvLE;
	@FXML
	private Label valeurTauxAssurance;
	@FXML
	private Label titreMensualite;
	@FXML
	private Label valeurMensualite;
	@FXML
	private Label titreDuree;
	@FXML
	private Label valeurDuree;
	@FXML
	private Label valeurTotAssurance;


	/**
	 * Redéfinition de la fonction initialize
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}




}
