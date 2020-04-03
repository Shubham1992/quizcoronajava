package com.quizcorona.ws.persister.cassandra.ddl;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.quizcorona.ws.persister.CStopwatch;
import com.quizcorona.ws.persister.cassandra.DbService;

public class KeySpaceManager {

    private static final KeySpaceManager Instance = new KeySpaceManager();


    private final String CREATEKEYSPACEQUERY = "CREATE KEYSPACE sb91_kysp WITH replication "
            + "= {'class': 'SimpleStrategy', 'replication_factor': '1'};";
    private final String UPDATEKEYSPACEQUERY = "ALTER KEYSPACE sb91_kysp WITH replication "
            + "= {'class': 'SimpleStrategy', 'replication_factor': '3'};";

    private final String DROPKEYSPACEQUERY = "DROP KEYSPACE sb91_kysp";

    ;

    public static KeySpaceManager getInstance() {
        return Instance;
    }

    private KeySpaceManager() {
    }

    public void updateKeySpace(String keySpaceName, Cluster cluster) {
        Session session = cluster.connect();
        CStopwatch cStopwatch = new CStopwatch();
        session.execute(UPDATEKEYSPACEQUERY);
        DbService.getLogService().info(
                "Time to create the keyspace" + cStopwatch.elapsedTime());
    }

    public void createKeySpace(String keySpaceName, Cluster cluster) {
        Session session = cluster.connect();
        CStopwatch cStopwatch = new CStopwatch();
        session.execute(CREATEKEYSPACEQUERY);
        DbService.getLogService().info(
                "Time to create the keyspace" + cStopwatch.elapsedTime());
    }

    public void dropKeySpace(String keySpaceName, Cluster cluster) {

        Session session = cluster.connect();
        session.execute(DROPKEYSPACEQUERY);

    }

    public void useKeySpace(String keySpaceName, Cluster cluster) {
        Session session = cluster.connect();
        try {

            CStopwatch cStopwatch = new CStopwatch();
            session.execute("USE " + keySpaceName);
            DbService.getLogService().info(
                    "Time to switch to keyspace" + cStopwatch.elapsedTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
