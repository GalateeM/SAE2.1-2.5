package model.data;

public class Emprunt {

	public double capital, taux, tauxAssurance, tauxApplicable;
	public int duree, nombrePeriode;
	public String periodicite;


	public Emprunt(double capital, int duree, double taux, String periodicite, double tauxAssurance) {
		super();
		this.capital = capital;
		this.duree = duree;
		this.taux = taux;
		this.periodicite = periodicite;
		this.tauxAssurance = tauxAssurance;
		this.nombrePeriode = 0;
		this.tauxApplicable = 0;
	}

}
