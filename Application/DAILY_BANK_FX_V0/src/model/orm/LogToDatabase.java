package model.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import model.orm.exception.DatabaseConnexionException;

/*
 * Classe qui gÃ¨re la connexion Ã  la BD
 *
 * Code source Ã  modifier pour user/mdp/hote/port/SID (attributs privÃ©s)
 *
 */
public class LogToDatabase {

	// Gloabl : accÃ¨s Ã  la base de donnÃ©e : Ã  ajuster selon le compte
	private static final String user = "G2B1";
	private static final String passwd = "Iut2022S2";

	private static final String hoteOracle = "oracle.iut-blagnac.fr";
	private static final String portOracle = "1521";
	private static final String SIDOracle = "db11g";

	// Local
	private static Connection currentConnexion = null;

	/*
	 * Load du driver Code dit "static" (hors d'une mÃ©thode) => exÃ©cutÃ© au
	 * chargement de la classe Permet de retrouver le diver => arrÃªt si driver non
	 * trouvÃ©
	 */
	{
		// driver oracle
		String nomDriver = "oracle.jdbc.driver.OracleDriver";

		try {
			Class.forName(nomDriver);
		} catch (ClassNotFoundException cnfe) {
			System.err.println("La classe " + nomDriver + " n'a pas Ã©tÃ© trouvÃ©e");
			System.exit(0);
		}
	}

	/**
	 * MÃ©thode statique pour demander une connexion Ã  la BD.
	 *
	 * Retourne la derniÃ¨re connexion active ou une nouvelle connexion si besoin.
	 * Connexion paramÃ©trÃ©e SANS COMMIT AUTOMATIQUE.
	 *
	 * Appel : Connection c = LogToDatabase.getConnexion();
	 *
	 * @return Une connexion Ã  la base de donnÃ©e SANS commit automatique
	 * @throws DatabaseConnexionException
	 */
	public static Connection getConnexion() throws DatabaseConnexionException {

		if (LogToDatabase.currentConnexion != null) {
			try {
				if (LogToDatabase.currentConnexion.isValid(0)) {
					return LogToDatabase.currentConnexion;
				}
			} catch (SQLException e) {
				// Let's continue
			}
			try {
				LogToDatabase.currentConnexion.close();
			} catch (SQLException e) {
				// Let's continue
			}
			LogToDatabase.currentConnexion = null;
		}

		String url = "jdbc:oracle:thin:" + "@" + LogToDatabase.hoteOracle + ":" + LogToDatabase.portOracle + ":"
				+ LogToDatabase.SIDOracle;

		try {
			LogToDatabase.currentConnexion = DriverManager.getConnection(url, LogToDatabase.user, LogToDatabase.passwd);

			LogToDatabase.currentConnexion.setAutoCommit(false);

		} catch (SQLTimeoutException e) {
			LogToDatabase.currentConnexion = null;
			throw new DatabaseConnexionException("Timeout sur connexion", e);
		} catch (SQLException e) {
			LogToDatabase.currentConnexion = null;
			throw new DatabaseConnexionException("Connexion Impossible", e);
		}

		return LogToDatabase.currentConnexion;
	}

	public static void closeConnexion() throws DatabaseConnexionException {
		if (LogToDatabase.currentConnexion != null) {
			try {
				LogToDatabase.currentConnexion.close();
				LogToDatabase.currentConnexion = null;
			} catch (SQLException e) {
				throw new DatabaseConnexionException("Fermeture Connexion Impossible", e);
			}
		}
	}
}
