package com.quizcorona.ws.restapis.notification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.quizcorona.ws.persister.cassandra.DbService;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.representation.Form;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Path("/v1")

public class V1NotificationWs {

    // deployment
    //public static final String ACCOUNT_SID = "";
    //public static final String AUTH_TOKEN = "";

    //navals auth token
    //public static final String ACCOUNT_SID = "";
    //public static final String AUTH_TOKEN = "";

    //local account 2
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";


    private static String whatsappBotNumber = "whatsapp:+14155238886";

    private static String introduction1 = "Hello, ✅  ✨ *QuizCorona* is a fun new way to learn about the *novel COVID-19 virus*. \n" +
            "\n" +
            "A team of doctors collects the information from the *most verified sources* such as WHO, ICMR and CDC and turns them into quizzes. Learn as you play!";

    private static String introduction2 = "Rules:\n" +
            "1. *+4* for the right answer\n" +
            "2. *-1* for the wrong answer\n" +
            "3. *10 seconds* per question\n" +
            "\n" +
            "I will also explain why each answer is right or wrong.\n\n" +
            "Sounds simple? Type *Start* to Play!";


    private static String scoreMessage = "Your total score is *";

    private static String scoreEnd = " points*.";
    private static String scoreEnd1Point = " point*.";

    public static String KEYSPACENAME = "sb91_kysp";
    private String tableName = "quizcorona";
    private int plusPoints = 4;
    private int minusPoints = -1;
    private int maxRetries = 3;

    static ArrayList<String> questionsArrNew = new ArrayList<>();
    static ArrayList<String> rightanswerResponseArrNew = new ArrayList<>();
    static ArrayList<String> wronganswerResponseArrNew = new ArrayList<>();
    static ArrayList<String> answerArray = new ArrayList<>();
    static long answerReplyTime = 15l;

    static {
        Client client = Client.create();
        client.addFilter(new LoggingFilter(System.out));
        WebResource webResource = client.resource("https://api.airtable.com/v0/appFGXkAs5hui2ZEc/questionsdb?maxRecords=300&view=Grid%20view");

        ClientResponse response = webResource.type("application/json").header("Authorization", "Bearer key77eAlTPdgU3cjF")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        } else {
            String output = response.getEntity(String.class);
            try {
                JSONObject jsonObject = new JSONObject(output);
                JSONArray jsonArray = jsonObject.getJSONArray("records");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObjQuestion = jsonArray.getJSONObject(i).getJSONObject("fields");
                    System.out.println(jObjQuestion);
                    String dataUnavailable = "Data not available";
                    questionsArrNew.add(jObjQuestion.optString("question", dataUnavailable));
                    rightanswerResponseArrNew.add(jObjQuestion.optString("rightanswerresponse", dataUnavailable));
                    wronganswerResponseArrNew.add(jObjQuestion.optString("wronganswerresponse", dataUnavailable));
                    answerArray.add(jObjQuestion.optString("rightanswer", dataUnavailable));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @Path("/notifyWhatsappBot")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public void notifyWhatsappBot(Form formParam) {
        System.out.println(formParam);
        // create request to send message
        final String phone = formParam.get("From").get(0);

        if (formParam.get("Body").get(0).equalsIgnoreCase("reset")) {
            resetSession(phone);
            return;
        }
        if (isUserQuizCompleted(phone)) {
            sendMessage(phone, whatsappBotNumber, "You have *completed the quiz*. Your final score is *" + getUserScore(phone) + " Points*", maxRetries);
            return;
        }

        if (formParam.get("Body").get(0).equalsIgnoreCase("hi")) {
            sendMessage(phone, whatsappBotNumber, "\uD83D\uDC96Because I knew you, I have been **changed** _for good_.\uD83D\uDC9A", maxRetries);
            sendMessageDelay(phone, whatsappBotNumber, introduction2, 5000);
        } else if (formParam.get("Body").get(0).equalsIgnoreCase("start")) {
            if (!checkIfQuizAlreadyStarted(phone)) {
                // send first question
                sendNextQuestion(0, phone, maxRetries);
                System.out.print("create db entry for question 1 to a person");
                createUserQuizStartEntry(phone);
            } else {
                sendMessage(phone, whatsappBotNumber, "You have already started the quiz. Your score is " + getUserScore(formParam.get("From").get(0)), maxRetries);
            }
        } else {
            String msg = formParam.get("Body").get(0);
            int lastQ = getLastQuestionSent(phone);
            String correctAnswerForLastQuestion = answerArray.get(lastQ - 1);
            System.out.println("correct answer " + correctAnswerForLastQuestion);

            long qstnSentTime = getUserLastQuestionSentTime(phone);
            // check if user answered after the set time for answering the questions
            if ((new Date().getTime() - qstnSentTime) > (answerReplyTime * 1000)) {
                updateUserScore(phone, 0, new Date().getTime());
                sendMessage(phone, whatsappBotNumber, "*Time exceeded*\n\n" + rightanswerResponseArrNew.get(lastQ - 1), maxRetries);
            } else if (correctAnswerForLastQuestion.equalsIgnoreCase(msg)) {
                updateUserScore(phone, plusPoints, new Date().getTime());
                sendMessage(phone, whatsappBotNumber, "*Good Job, thats's right*\n\n" + rightanswerResponseArrNew.get(lastQ - 1), maxRetries);
            } else {
                updateUserScore(phone, minusPoints, new Date().getTime());
                sendMessage(phone, whatsappBotNumber, "*Oh no, thats's wrong*\n\n" + wronganswerResponseArrNew.get(lastQ - 1), maxRetries);
            }
            // send total score to user
            sendMessage(phone, whatsappBotNumber, scoreMessage + getUserScore(phone) + getScoreEndText(getUserScore(phone)), maxRetries);
            sendNextQuestion(lastQ, phone, maxRetries);
        }
        return;
    }

    private String getScoreEndText(int score) {
        if (score <= 1)
            return scoreEnd1Point;
        else return scoreEnd;
    }

    private boolean checkIfQuizAlreadyStarted(String phone) {

        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, "quizcorona")
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            String lastQ = row.getString("last_question");
            if (lastQ == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    int getUserScore(String phone) {

        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, tableName)
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            return row.getInt("total_points");
        }

        return 0;
    }

    int getLastQuestionSent(String phone) {

        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, tableName)
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            return Integer.parseInt(row.getString("last_question"));
        }

        return 0;
    }

    int updateUserScore(String phone, int pointsToAdd, long replytime) {

        int total = getUserScore(phone) + pointsToAdd;
        if (total < 0) total = 0;

        Statement stmt = QueryBuilder.update(DbService.CLUSTER_NAME, tableName)
                .with(QueryBuilder.set("total_points", total))
                .and(QueryBuilder.set("is_lastquestion_answered", true))
                .and(QueryBuilder.set("replytime", replytime))
                .where(QueryBuilder.eq("id", phone));
        DbService.getInstance().getSession().execute(stmt);
        System.out.println(getUserScore(phone));
        return getUserScore(phone);
    }

    String updateUserQuizLanguage(String phone, String language) {
        Statement stmt = QueryBuilder.update(DbService.CLUSTER_NAME, tableName)
                .with(QueryBuilder.set("quiz_language", language)).where(QueryBuilder.eq("id", phone));
        DbService.getInstance().getSession().execute(stmt);
        System.out.println("Language updated-> " + language);
        return language;
    }

    String getUserLanguage(String phone) {
        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, tableName)
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            return row.getString("quiz_language");
        }
        return "English";
    }

    int updateUserLastQuestion(String phone, int lastQ) {

        Statement stmt = QueryBuilder.update(DbService.CLUSTER_NAME, tableName)
                .with(QueryBuilder.set("last_question", String.valueOf(lastQ)))
                .and(QueryBuilder.set("is_lastquestion_answered", false))
                .and(QueryBuilder.set("questionsenttime", new Date().getTime()))
                .where(QueryBuilder.eq("id", phone));
        DbService.getInstance().getSession().execute(stmt);
        System.out.println("Last question updated-> " + getLastQuestionSent(phone));
        return getLastQuestionSent(phone);
    }

    void sendNextQuestion(int nextQuestion, String toPhone, int retry) {
        try {
            if (retry == 0) {
                return;
            }

            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            if (nextQuestion == questionsArrNew.size()) {
                sendQuizComplete(toPhone);
                updateQuizCompletedInDB(toPhone);
                return;
            }

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Message.creator(
                                    new com.twilio.type.PhoneNumber(toPhone),
                                    new com.twilio.type.PhoneNumber(whatsappBotNumber),
                                    questionsArrNew.get(nextQuestion))
                                    .create();
                            updateUserLastQuestion(toPhone, nextQuestion + 1);
                            //createTimerOutThread(toPhone);
                        }
                    },
                    3000
            );
        } catch (Exception e) {
            sendNextQuestion(nextQuestion, toPhone, maxRetries);
        }

    }

    private void createTimerOutThread(String toPhone) {
        Runnable worker = new SendTimeExceededMessage(toPhone);
        executorService.schedule(worker, answerReplyTime, TimeUnit.SECONDS);
    }

    private void updateQuizCompletedInDB(String phone) {

        Statement stmt = QueryBuilder.update(DbService.CLUSTER_NAME, tableName)
                .with(QueryBuilder.set("is_quiz_completed", true))
                .where(QueryBuilder.eq("id", phone));
        DbService.getInstance().getSession().execute(stmt);

    }

    void sendQuizComplete(String toPhone) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Message messageNextQuestion2 = Message.creator(
                                new com.twilio.type.PhoneNumber(toPhone),
                                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                                "Congratulations\n" +
                                        "You have *completed the quiz*. Your final score is *" + getUserScore(toPhone) + " Points*").create();
                    }
                },
                1000
        );
    }

    void sendMessage(String to, String from, String message, int retry) {
        try {
            if (retry == 0) {
                return;
            }
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message messageCreator = Message.creator(
                    new com.twilio.type.PhoneNumber(to),
                    new com.twilio.type.PhoneNumber(from),
                    message).create();
            System.out.println(messageCreator.getSid());
        } catch (Exception e) {
            e.printStackTrace();
            //retrying just in case it fails first time
            sendMessage(to, from, message, retry--);
        }
    }

    void sendMessageDelay(String to, String from, String message, long delay) {
        try {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {

                            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

                            Message messageCreator = Message.creator(
                                    new com.twilio.type.PhoneNumber(to),
                                    new com.twilio.type.PhoneNumber(from),
                                    message).create();
                            System.out.println(messageCreator.getSid());
                        }

                    },
                    1000
            );

        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    void createUserQuizStartEntry(String phone) {
        PreparedStatement insertStatement = DbService.getInstance().getSession()
                .prepare("INSERT INTO sb91_kysp.quizcorona"
                        + "(id ,phone, last_question, is_quiz_completed, replyTime, total_points, questionsentTime, is_lastquestion_answered)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
        BoundStatement boundStatement = new BoundStatement(insertStatement);
        DbService.getInstance().getSession()
                .execute(boundStatement.bind(phone, phone.replace("whatsapp:", ""), "1", false, new Date().getTime(), 0, 0l, false));


    }

    ArrayList<String> getQuestionsArrayBasedOnLanguage(String lang) {
        switch (lang) {
            case "English": {
                return questionsArrNew;
            }
            default:
                return questionsArrNew;

        }
    }

    void resetSession(String phone) {
        Statement stmt = QueryBuilder.delete().from(DbService.CLUSTER_NAME, tableName).where(QueryBuilder.eq("id", phone));
        DbService.getInstance().getSession().execute(stmt);
    }

    long getUserLastQuestionSentTime(String phone) {
        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, tableName)
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            return row.getLong("questionsenttime");
        }
        return 0l;
    }


    boolean isUserQuizCompleted(String phone) {
        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, tableName)
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            return row.getBool("is_quiz_completed");
        }
        return false;
    }

    boolean islastQuestionAlreadyAnswered(String phone) {
        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, tableName)
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            return row.getBool("is_quiz_completed");
        }
        return false;
    }


    // send notification after every 15 seconds of sending a question
    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public class SendTimeExceededMessage implements Runnable {

        private String phone;

        public SendTimeExceededMessage(String phone) {
            this.phone = phone;
        }

        @Override
        public void run() {
            sendMessage(phone, whatsappBotNumber, "*Time exceeded*", maxRetries);
        }

        void sendMessage(String to, String from, String message, int retry) {
            try {
                if (retry == 0) {
                    return;
                }

                if (new Date().getTime() - getUserLastQuestionSentTime(phone) < (answerReplyTime * 1000)) {
                    return;
                }

                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                Message messageCreator = Message.creator(
                        new com.twilio.type.PhoneNumber(to),
                        new com.twilio.type.PhoneNumber(from),
                        message).create();
                System.out.println(messageCreator.getSid());
            } catch (Exception e) {
                e.printStackTrace();
                //retrying just in case it fails first time
                sendMessage(to, from, message, retry--);
            }
        }


    }



}
