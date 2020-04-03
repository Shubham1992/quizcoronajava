package com.quizcorona.ws.persister.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.quizcorona.ws.persister.cassandra.ddl.KeySpaceManager;
import com.quizcorona.ws.persister.cassandra.ddl.TableManager;

import java.util.logging.Logger;

public class DbService {

    public static Cluster cluster = null;

    public Session session = null;

    public static Logger getLogService() {
        return null;
    }

    public Session getSession() {
        if (session == null) {
            initSession();
        }
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public static final String CLUSTER_NAME = "sb91_kysp";
    private static final DbService INSTANCE = new DbService();


    public static DbService getInstance() {
        return INSTANCE;
    }

    private static final boolean dev = true;

    public static boolean isDev() {
        return dev;
    }

    private DbService() {
        if (dev) {
            cluster = Cluster.builder().addContactPoint("13.233.111.123").build();
        } else {
            cluster = Cluster.builder().addContactPoint("mob-cass-prod-1507668984.ap-south-1.elb.amazonaws.com").
                    // .addContactPoint("13.233.172.35")
                    // .addContactPoint("13.233.82.189")
                    // .addContactPoint("13.232.222.125").
                            build();// 13.233.172.35
        }
    }

    public void initSession() {
        session = cluster.connect(DbService.CLUSTER_NAME);
    }

    public void createKeySpace() {
        KeySpaceManager.getInstance().createKeySpace(CLUSTER_NAME, cluster);
        useKeySpace();

    }
    /*
     * cqlsh> SELECT * FROM system_schema.keyspaces;
     *
     * keyspace_name | durable_writes | replication
     * --------------------+----------------+---------------------------------------
     * ---------------------------------------------- system_auth | True | {'class':
     * 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1'}
     * sb91_kysp | True | {'class': 'org.apache.cassandra.locator.SimpleStrategy',
     * 'replication_factor': '3'} system_schema | True | {'class':
     * 'org.apache.cassandra.locator.LocalStrategy'} system_distributed | True |
     * {'class': 'org.apache.cassandra.locator.SimpleStrategy',
     * 'replication_factor': '3'} system | True | {'class':
     * 'org.apache.cassandra.locator.LocalStrategy'} system_traces | True |
     * {'class': 'org.apache.cassandra.locator.SimpleStrategy',
     * 'replication_factor': '2'}
     */

    public void updateKeySpace() {
        KeySpaceManager.getInstance().updateKeySpace(CLUSTER_NAME, cluster);
        useKeySpace();

    }

    public void useKeySpace() {
        KeySpaceManager.getInstance().useKeySpace(CLUSTER_NAME, cluster);

    }

    public void dropKeySpace() {
        KeySpaceManager.getInstance().dropKeySpace(CLUSTER_NAME, cluster);
    }

    public void createTables() {

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "sessionfb",
                // "id uuid PRIMARY KEY, " + "useruuid text,"+"role text,userid text,name
                // text,email text,mobile text,fmcId text,teamid text");
                "id uuid PRIMARY KEY, " + "fmcId text");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "sessions",
                // "id uuid PRIMARY KEY, " + "useruuid text,"+"role text,userid text,name
                // text,email text,mobile text,fmcId text,teamid text");
                "id uuid PRIMARY KEY, " + "useruuid uuid," + "profile text,"
                        + "role text,email text,access_token text,expires_in bigint,id_token text,fmcId text,"
                        + "refresh_token text");

        // createIndexs("sessions", "sessionid");
        createIndexs("sessions", "useruuid");
        createIndexs("sessions", "email");
        createIndexs("sessions", "role");
        createIndexs("sessions", "fmcId");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "users",
                "id uuid PRIMARY KEY," + "lastupdate_on text, " + "created_on text, " + "login_email text, "
                        + "contact_email text," + "designation text," + "mobile text, " + "name text, "
                        + "avatar_url text, " + "blood_group text," + "bio text," + "groups list<uuid>,"
                        + "is_verified boolean," + "ctoken text," + "is_alumnai boolean," + "is_loggedin boolean,"
                        + "loggedinat bigint," + "modifiedat bigint," + "address_id text, " + "organization_id text, "
                        + "emergency_contact_id text, " + "dob text," + "skills list<text>," + "interests list<text>,"
                        + "password text," + "mobilappstatus text," + "otp text");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "organizations",
                "id uuid PRIMARY KEY," + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, "
                        + "name text, " + "description text, " + "website text, " + "owned_by int, " + "pan text, "
                        + "organization_type_id text, " + "address_id text, " + "billing_emails text, "
                        + "is_gst_applicable boolean, " + "brand_name text, " + "logo text," + "questionaire text, "
                        + "sections list<text>, " + "onboarded boolean, " + "onboardeding_step boolean");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "groups", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, " + "name text	");

        // createIndexs("groups", "cid");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "organization_types", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, " + "name text	");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "tags", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, " + "name text	");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "user_kyc_documents",
                "id uuid PRIMARY KEY," + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, "
                        + "user_id text," + "kyc_documents text," + "document_url text");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "emergency_contacts", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, " + "name text," + "mobile text");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "states",
                "id uuid PRIMARY KEY," + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, "
                        + "name text," + "gstin_code text");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "kyc_documents",
                "id uuid PRIMARY KEY," + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, "
                        + "kyc_type_id text," + "document_type text");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "kyc_types", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, " + "name text	");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "organization_tags", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "organization_id text, " + "tag_id text	");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "conversion_channels", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, " + "name text	");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "conversion_sources", "id uuid PRIMARY KEY,"
                + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, " + "name text	");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "gstins",
                "id uuid PRIMARY KEY," + "gstin text, " + "gst_lastupdated text, " + "lastupdate_on text, "
                        + "created_on text, " + "state_id text, " + "organization_id text, " + "is_deleted boolean");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "addresses",
                "id uuid PRIMARY KEY, " + "lastupdate_on text, " + "created_on text, " + "city text, "
                        + "country text, " + "address_id text, " + "latitude text, " + "line1 text," + "line2 text,"
                        + "state text," + "zipcode text," + "is_deleted boolean, " + "longitude text");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "hubs",
                "id uuid PRIMARY KEY, " + "lastupdate_on text, " + "created_on text, " + "is_deleted boolean, "
                        + "address_id text, " + "name text, " + "hubcity text, " + "public_name text, "
                        + "hub_email text, " + "qb_company_id text, " + "capacity bigint, "
                        + "floating_desk_cnt bigint," + "reserved_desks_for_91s_cnt bigint,"
                        + "instamojo_qb_taxcode_id int," + "defaultgrp uuid");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "user_preference", "id uuid PRIMARY KEY,"
                + "userid uuid, " + "preference_name text, " + "user_email text," + "is_enabled boolean ");

        createIndexs("hubs", "defaultgrp");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "user_hubs",
                "id uuid PRIMARY KEY," + "lastupdate_on text, " + "created_on text, " + "hub_id text, "
                        + "user_id text, " + "group_id text,	" + "is_active boolean");

        createIndexs("user_hubs", "hub_id");
        createIndexs("user_hubs", "group_id");
        createIndexs("user_hubs", "user_id");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "skills",
                "id text PRIMARY KEY, " + "lastupdate_on text, " + "created_on text, " + "userids list<text> ");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "interests",
                "id text PRIMARY KEY, " + "lastupdate_on text, " + "created_on text, " + "userids list<text> ");

        //
        // createIndexs("user", "name");
        // createIndexs("user", "email");
        // createIndexs("user", "mobile");
        // createIndexs("user", "password");
        // createIndexs("user", "userid");
        // createIndexs("user", "otp");
        // createIndexs("user", "role");
        // createIndexs("user", "teamuuid");
        //
        //
        // TableManager.getInstance().createTable(
        // CLUSTER_NAME,
        // cluster,
        // "team",
        // "id uuid PRIMARY KEY, " + "name text, "+ "email text, "+ "password text," +
        // "otp text"
        // );
        //
        // createIndexs("team", "name");
        // createIndexs("team", "otp");
        //
        // createIndexs("team", "email");
        //
        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "chatgroups",
                "id uuid PRIMARY KEY, " + "lastupdate_on bigint, " + "created_on bigint, " + "is_deleted boolean, "
                        + "name text, " + "description text, " + "members list<uuid>," + "admins list<uuid>,"
                        + "topics list<text>," + "pendingrequests list<uuid>," + "hubs list<uuid>," + "imageurl text, "
                        + "is_active boolean," + "is_private boolean," + "is_approved boolean," + "modifiedat bigint,"
                        + "cities text," + "is_pan91 boolean," + "is_open boolean," + "lastmessagetext text,"
                        + "created_by uuid," + "is_onetoone boolean," + "is_community boolean," + "is_default boolean,"
                        + "blocked boolean," + "blockedby uuid," + "fromuuid uuid," + "touuid uuid," + "fromname text,"
                        + "toname text," + "firstmessagenoack boolean," + "muted_users list<uuid>");

        createIndexs("chatgroups", "name");
        createIndexs("chatgroups", "cities");
        createIndexs("chatgroups", "is_pan91");
        createIndexs("chatgroups", "fromuuid");
        createIndexs("chatgroups", "touuid");
        createIndexs("chatgroups", "is_onetoone");

        TableManager.getInstance().createTable(CLUSTER_NAME, cluster, "topics",
                "id text PRIMARY KEY, " + "lastupdate_on text, " + "created_on text, " + "chatgroupids list<text> ");

        TableManager.getInstance().createTable(DbService.CLUSTER_NAME, DbService.cluster, "messages",
                "id uuid PRIMARY KEY, " + "toentity uuid, " + "isdeleted  boolean," + "isGroupMessage  boolean,"
                        + "content  text," + "fromUserName  text," + "acknowledged  boolean," + "firstMessage  boolean,"
                        + "mediaUploaded boolean," + "subcontent  text," + "fromentity uuid, " + "filename text," + "reply_count int,"
                        + "filetype text," + "fileurl text," + "type text," + "sentTime bigint"// ," +"PRIMARY KEY ((id,
                // toentity),sentTime)"
        );




        //
        createIndexs("messages", "toentity");
        createIndexs("messages", "fromentity");
        createIndexs("messages", "filename");
        createIndexs("messages", "type");
        createIndexs("messages", "isGroupMessage");
        // createIndexs("messages", "fromentity");
        createIndexs("messages", "sentTime");
        createIndexs("messages", "content");



        TableManager.getInstance().createTable(DbService.CLUSTER_NAME, DbService.cluster, "onlinecms",
                "id uuid PRIMARY KEY, " + "emailid text");

        //
        createIndexs("onlinecms", "emailid");

        TableManager.getInstance().createTable(DbService.CLUSTER_NAME, DbService.cluster, "abuses",
                "id uuid PRIMARY KEY, " + "fromentity uuid, " + "content  text," + "messageid uuid, "
                        + "chatGroup uuid, " + "sentTime bigint");

        //
        createIndexs("abuses", "fromentity");
        createIndexs("abuses", "messageid");
        createIndexs("abuses", "chatGroup");
        createIndexs("abuses", "sentTime");

        TableManager.getInstance().createTable(DbService.CLUSTER_NAME, DbService.cluster, "chatgroupjoinreq",
                "id uuid PRIMARY KEY, " + "lastupdate_on bigint, " + "created_on bigint, " + "is_deleted boolean, "
                        + "groupid uuid, " + "userid uuid, " + " message text");

        DbService.getInstance().createIndexs("chatgroupjoinreq", "groupid");
        DbService.getInstance().createIndexs("chatgroupjoinreq", "userid");

        // createIndexs("groups", "teamid");
        //
        // TableManager.getInstance().createTable(
        // CLUSTER_NAME,
        // cluster,
        // "messages",
        // "id uuid PRIMARY KEY, " + "toentity uuid, "+"isGroupMessage boolean,"+
        // "fromentity uuid, "+ "filename text,"+ "filetype text,"+ "fileurl text," +
        // "type text," + "sentTime bigint"
        // );
        ////
        // createIndexs("messages", "toentity");
        // createIndexs("messages", "isGroupMessage");
        // createIndexs("messages", "fromentity");
        // createIndexs("messages", "sentTime");

    }

    public void createnewTable() {
        useKeySpace();
    }

    public void createIndexes() {


    }

    public void createIndexs(String tableName, String columnName) {
        TableManager.getInstance().createIndexs(CLUSTER_NAME, cluster, tableName, columnName);

    }

    public void droptables() {

    }

}
