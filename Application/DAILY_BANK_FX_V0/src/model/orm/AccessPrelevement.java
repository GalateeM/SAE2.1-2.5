package model.orm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Operation;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class AccessPrelevement {
	
	public AccessPrelevement() {
	}
	
	/**
	 * Recherche de tous les prélèvements d'un compte.
	 *
	 * @param idNumCompte id du compte dont on cherche toutes les prélèvements
	 * @return Tous les prélèvements du compte, liste vide si pas de prélèvement
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<Prelevement> getPrelevements(int idNumCompte) throws DataAccessException, DatabaseConnexionException {
		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Prelevementautomatique where idNumCompte = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelevement = rs.getInt("idPrelev");
				double montant = rs.getDouble("montant");
				int dateRecurrente = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompteConcerne= rs.getInt("idNumCompte");

				alResult.add(new Prelevement(idPrelevement, montant, dateRecurrente, beneficiaire, idNumCompteConcerne));
			}
			rs.close();
			pst.close();
			return alResult;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
		}
	}
}
