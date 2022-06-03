package model.data;

public class Prelevement {
	
	public int idPrelev; 
	public double montant; 
	public int jour; 
	public String beneficiaire; 
	public int idNumCompte; 
	
	/**
	 * Constructeur qui permet de créer un prélèvement avec tous les paramètres
	 * 
	 * @param pfIdPrelev : id du prélèvement
	 * @param pfMontant : montant à prélever
	 * @param pfJour : n° du jour de prélèvement (compris entre 1 et 28)
	 * @param pfBeneficiaire : nom du destinataire (ex : EDF)
	 * @param pfIdNumCompte : num du compte à prélever
	 */
	public Prelevement(int pfIdPrelev, double pfMontant, int pfJour, String pfBeneficiaire, int pfIdNumCompte) {
		this.idPrelev=pfIdPrelev;
		this.montant=pfMontant;
		this.jour=pfJour;
		this.beneficiaire=pfBeneficiaire;
		this.idNumCompte=pfIdNumCompte;
	}
	
	/**
	 * Constructeur d'un prélèvement à partir d'un autre
	 * 
	 * @param p : l'autre prélèvement
	 */
	public Prelevement(Prelevement p) {
		this(p.idPrelev,p.montant,p.jour,p.beneficiaire,p.idNumCompte);
	}
	
	@Override
	public String toString() {
		return "" + String.format("%05d", this.idPrelev) + " : Montant=" + String.format("%12.02f", this.montant)
		+ "  ,  Bénéficiaire=" + this.beneficiaire + " , Jour=" + this.jour;
	}
	
	/**
	 * Méthode pour modifier le numéro du compte concerné par le prélèvement
	 * 
	 * @param pfIdNumCompte : numéro du compte à prélever
	 */
	public void setIdNumCompte(int pfIdNumCompte) {
		this.idNumCompte=pfIdNumCompte;
	}
}
