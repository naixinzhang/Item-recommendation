package db;
import db.mongodb.MongoDBConnection;
import db.mysql.MySQLConnection;
//为了后面switch的时候比较方便
public class DBConnectionFactory {
		// This should change based on the pipeline.
		//private static final String DEFAULT_DB = "mysql";
	    private static final String DEFAULT_DB = "mongodb";
		private static DBConnection getConnection(String db) {
			switch (db) {
			//case "mysql":
			//	 return new MySQLConnection();
			case "mongodb":
				 return new MongoDBConnection();
			// 
			default:
				throw new IllegalArgumentException("Invalid db:" + db);
			}

		}
		// This is overloading not overriding
		public static DBConnection getConnection() {
			return getConnection(DEFAULT_DB);
		}
	}
	
