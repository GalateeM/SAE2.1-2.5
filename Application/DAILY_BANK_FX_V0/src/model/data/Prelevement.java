package model.data;

public class Prelevement {
	
	public int idPrelev; //id du prélèvement
	public double montant; //montant à prélever
	public int jour; // n° du jour de prélèvement (compris entre 1 et 28)
	public String beneficiaire; //nom du destinataire (ex : EDF)
	public int idNumCompte; //num du compte à prélever
	
	public Prelevement(int pfIdPrelev, double pfMontant, int pfJour, String pfBeneficiaire, int pfIdNumCompte) {
		this.idPrelev=pfIdPrelev;
		this.montant=pfMontant;
		this.jour=pfJour;
		this.beneficiaire=pfBeneficiaire;
		this.idNumCompte=pfIdNumCompte;
	}
	
	public Prelevement(Prelevement p) {
		this(p.idPrelev,p.montant,p.jour,p.beneficiaire,p.idNumCompte);
	}
	
	@Override
	public String toString() {
		return "" + String.format("%05d", this.idPrelev) + " : Montant=" + String.format("%12.02f", this.montant)
		+ "  ,  Bénéficiaire=" + this.beneficiaire + " , Jour=" + this.jour;
	}
}
