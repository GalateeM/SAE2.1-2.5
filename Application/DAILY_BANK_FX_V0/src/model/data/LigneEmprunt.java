package model.data;

public class LigneEmprunt {

	private double capDebut, interet, principal, mensualite, capFin;
	private int periode;


	public LigneEmprunt(int periode, double capDebut, double interet, double principal, double mensualite, double capFin) {
		super();
		this.periode = periode;
		this.capDebut = capDebut;
		this.interet = interet;
		this.principal = principal;
		this.mensualite = mensualite;
		this.capFin = capFin;
	}


	public void setCapDebut(double capDebut) {
		this.capDebut = capDebut;
	}


	public void setInteret(double interet) {
		this.interet = interet;
	}


	public void setPrincipal(double principal) {
		this.principal = principal;
	}


	public void setMensualite(double mensualite) {
		this.mensualite = mensualite;
	}


	public void setCapFin(double capFin) {
		this.capFin = capFin;
	}


	public void setPeriode(int periode) {
		this.periode = periode;
	}


	public double getCapDebut() {
		return capDebut;
	}


	public double getInteret() {
		return interet;
	}


	public double getPrincipal() {
		return principal;
	}


	public double getMensualite() {
		return mensualite;
	}


	public double getCapFin() {
		return capFin;
	}


	public int getPeriode() {
		return periode;
	}
	


}
