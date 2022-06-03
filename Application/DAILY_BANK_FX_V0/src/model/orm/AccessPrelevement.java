package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
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
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
		}
	}
	
	/**
	 * Méthode pour insérer un prélèvement dans la base de données
	 * 
	 * @param prelevement : le prélèvement à insérer
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void insertPrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (" + "seq_id_prelevAuto.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?" + ")";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelevement.montant);
			pst.setInt(2, prelevement.jour);
			pst.setString(3, prelevement.beneficiaire);
			pst.setInt(4, prelevement.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_prelevAuto.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numPrelBase = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();
			
			prelevement.idPrelev = numPrelBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur accès", e);
		}
	}
	
	/**
	 * Méthode pour modifier un prélèvement dans la base de données
	 * 
	 * @param prelevement : le prélèvement à modifier
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void updatePrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET " + "montant = " + "? , " + "daterecurrente = " + "? " + "WHERE idPrelev = ? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelevement.montant);
			pst.setInt(2, prelevement.jour);
			pst.setInt(3, prelevement.idPrelev);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accès", e);
		}
	}
	
	/**
	 * Méthode pour supprimer un prélèvement dans la base de données
	 * 
	 * @param prelevement : le prélèvement à supprimer
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void deletePrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {

			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM PRELEVEMENTAUTOMATIQUE WHERE idPrelev=?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, prelevement.idPrelev);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.DELETE,
						"Delete anormal (delete de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_prelevAuto.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			con.commit();
			
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur accès", e);
		}
	}
}
