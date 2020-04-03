package com.quizcorona.ws.persister.cassandra.ddl;

import java.text.MessageFormat;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.quizcorona.ws.persister.CStopwatch;
import com.quizcorona.ws.persister.cassandra.DbService;

public class TableManager {

	private static final TableManager Instance = new TableManager();

	private final String CREATETABLEQUERY = "CREATE TABLE {0} ({1}) with caching = '{' '''keys''': '''NONE''','''rows_per_partition''':'''120''' '}';";

	private final String CREATEINDEXQUERY = "CREATE INDEX ON {0} ( {1} );";

	private final String DROPTABLEQUERY = "DROP TABLE IF EXISTS {0};";

	public static TableManager getInstance() {
		return Instance;
	}

	private TableManager() {
	}

	public void createTable(String keySpaceName, Cluster cluster,
			String tableName, String column_properties) {
		Session session = cluster.connect(DbService.CLUSTER_NAME);
		CStopwatch cStopwatch = new CStopwatch();
		Object[] tableArgs = { keySpaceName + "." + tableName,
				column_properties };

		MessageFormat form = new MessageFormat(CREATETABLEQUERY);
		DbService.getLogService().info(form.format(tableArgs));
		// session.execute(CREATEUSERTABLEQUERY);
		session.execute(form.format(tableArgs));
		DbService.getLogService().info(
				"Time to create the Table : " + tableName + "is"
						+ cStopwatch.elapsedTime());
	}

	public void deleteTable(String keySpaceName, Cluster cluster,
			String tableName) {
		Session session = cluster.connect();
		CStopwatch cStopwatch = new CStopwatch();
		Object[] tableArgs = { keySpaceName + "." + tableName };

		MessageFormat form = new MessageFormat(DROPTABLEQUERY);
		DbService.getLogService().info(form.format(tableArgs));
		session.execute(form.format(tableArgs));

		DbService.getLogService().info(
				"Time to delete the Table : " + tableName + "is"
						+ cStopwatch.elapsedTime());
	}

	public void createIndexs(String keySpaceName, Cluster cluster,
			String tableName, String colName) {
		Session session = cluster.connect();
		CStopwatch cStopwatch = new CStopwatch();
		Object[] tableArgs = { keySpaceName + "." + tableName, colName };

		MessageFormat form = new MessageFormat(CREATEINDEXQUERY);
		DbService.getLogService().info(form.format(tableArgs));
		session.execute(form.format(tableArgs));

		DbService.getLogService().info(
				"Time to Create the index : " + colName + " in table : "
						+ tableName + "is" + cStopwatch.elapsedTime());
	}

}
