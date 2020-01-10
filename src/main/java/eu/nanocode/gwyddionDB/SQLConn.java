package eu.nanocode.gwyddionDB;

import java.io.Serializable;

class SQLConn implements Serializable {
	
    public enum SQLConfig {
    	MySQL("com.mysql.cj.jdbc.Driver",				"jdbc:mysql://",	 		"org.hibernate.dialect.MySQL5InnoDBDialect"),
    	ORACLE("oracle.jdbc.pool.OracleDataSource", 	"jdbc:oracle:", 			"com.cri.poller.hibernate.Oracle10gDialect"),
    	PostgreSQL("org.postgresql.Driver", 			"jdbc:postgresql://", 		"org.hibernate.dialect.PostgreSQLDialect"),
    	SQLServer("net.sourceforge.jtds.jdbc.Driver", 	"jdbc:jtds:sqlserver://", 	"org.hibernate.dialect.SQLServerDialect"),
    	Other("","","");
    	
    	String driver_class, urlPrefix, dialect;
  	
		private SQLConfig(String driver_class, String urlPrefix,  String dialect) {
			this.driver_class = driver_class;
			this.urlPrefix = urlPrefix;
			this.dialect = dialect;
		}    	
    }
	
	private static final long serialVersionUID = 42L;
	SQLConfig conifg;
	String user, password, url, other_driver_class, other_dialect ;
	boolean useSSL;
	
	public SQLConn() {
		super();
	}
	
	public SQLConn(SQLConfig conifg, String user, String password, String url, boolean useSSL) {
		this.conifg = conifg;
		this.user = user;
		this.password = password;
		this.url = url;
		this.useSSL =  useSSL;
	}
	public SQLConn(SQLConfig conifg, String user, String password, String url, boolean useSSL, String other_driver_class,
			String other_dialect) {
		this(conifg, user, password, url, useSSL);
		this.other_driver_class = other_driver_class;
		this.other_dialect = other_dialect;
	}	
	
	
}