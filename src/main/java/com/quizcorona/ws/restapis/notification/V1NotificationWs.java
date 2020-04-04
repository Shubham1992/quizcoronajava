package com.quizcorona.ws.restapis.notification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.quizcorona.ws.persister.cassandra.DbService;
import com.sun.jersey.api.representation.Form;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.util.Date;

@Path("/v1")

public class V1NotificationWs {

    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    public static String[] questionsArr = {"Here is your first question:\n What is the apt distance that should be maintained between individuals?\n " +
            "a1. 3 feet\n" +
            "a2. 1 feet\n" +
            "a3. 10 feet\n \n\n Reply with the correct option(for ex: 'a1' for 3 feet)"

            , "Covid will always present itself as respiratory distress/difficulty in breathing?\n" +
            "b1. Yes\nb2. No",

            "Covid can spread through pets like cats and dogs? \n" +
                    "c1. Yes\nc2. No",

            "Coronavirus is new and has spread to humans for the first time? \n" +
                    "d1. Yes\nd2. No",

            " If a person has no flu like symptoms, she/he cannot transmit covid-19 to others?\n" +
                    "e1. Yes\ne2. No"
    };
    private static String whatsappBotNumber = "whatsapp:+14155238886";

    private static String introduction1 = "Hello, QuizCorona is a fun new way to learn about the novel Covid-19 virus. \n" +
            "\n" +
            "I will ask you questions that test your knowledge on Corona Virus and as you play, I will reward points that are converted into real money. \n" +
            "\n" +
            "The money you earn is then donated directly to the PMCares fund used to help those affected by the virus.\n" +
            "So you get to help the nation while you learn something new. Win Win! \n";

    private static String introduction2 = "For every right answer, you get 1 point and for every wrong, 1/4 points are deducted. If you don’t answer a question in 10 seconds, I move to the next question! To help you understand, I will also explain why each answer was right or wrong.\n" +
            "\n" +
            "Let’s play? Type Start to Start Quiz.\n";

    public static String[] wrongAnswerArray = {"",

            "Oh oh, that doesn’t seem to be correct, the apt distance that should be maintained " +
                    "between individuals is 3 feet." +
                    "\nhttps://www.youtube.com/watch?time_continue=89&v=1APwq1df6Mw&feature=emb_logo",

            "No, Covid will not always present itself as respiratory distress/difficulty in breathing\n",

            "No, Covid cannot spread through pets like cats and dogs",

            "No, Coronavirus is a known group of viruses that has been infecting humans for ages.",

            "No, a person with no flu like symptoms can also transmit covid-19 to others",

            "Wrong Answer"};

    public static String[] rightAnswerArray = {"",

            "Good job, that’s the right answer, the apt distance that should be maintained between individuals is 3 feet.\n"
                    + "\nhttps://www.youtube.com/watch?time_continue=89&v=1APwq1df6Mw&feature=emb_logo",

            "Good Job, Covid will not always present itself as respiratory distress/difficulty in breathing\n"
            ,

            "Good Job, Covid cannot spread through pets like cats and dogs",

            "Good Job, Coronavirus is a known group of viruses that has been infecting humans for ages.",

            "Good Job, a person with no flu like symptoms can also transmit covid-19 to others",

            "Right Answer"};

    private static String scoreMessage = "Your total score is *";

    private static String scoreEnd = " point(s)*.";

    public static String KEYSPACENAME = "sb91_kysp";
    private String tableName = "quizcorona";
    private int plusPoints = 4;
    private int minusPoints = -1;



    @Path("/notifyWhatsappBot")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public void notifyWhatsappBot(Form formParam) {
        System.out.println(formParam);
        // create request to send message

        if (formParam.get("Body").get(0).equalsIgnoreCase("hi")) {
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, introduction1);
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, introduction2);
        } else if (formParam.get("Body").get(0).equalsIgnoreCase("start")) {
            if (!checkIfQuizAlreadyStarted(formParam.get("From").get(0))) {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, questionsArr[0]);
                System.out.print("create db entry for question 1 to a person");
                createUserQuizStartEntry(formParam.get("From").get(0));
            } else {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, "You have already started the quiz. Your score is " + getUserScore(formParam.get("From").get(0)));
            }

        }
        // First question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("a1")
                || formParam.get("Body").get(0).equalsIgnoreCase("a2")
                || formParam.get("Body").get(0).equalsIgnoreCase("a3")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("a1")) {
                updateUserScore(formParam.get("From").get(0), plusPoints);
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[1]);
            } else {
                updateUserScore(formParam.get("From").get(0), minusPoints);
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[1]);
            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage + getUserScore(formParam.get("From").get(0)) + scoreEnd);

            sendNextQuestion(2, formParam.get("From").get(0));

        }
        // Third question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("b1")
                || formParam.get("Body").get(0).equalsIgnoreCase("b2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("b2")) {
                updateUserScore(formParam.get("From").get(0), plusPoints);

                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[2]);
            } else {
                updateUserScore(formParam.get("From").get(0), minusPoints);

                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[2]);

            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage + getUserScore(formParam.get("From").get(0)) + scoreEnd);

            sendNextQuestion(3, formParam.get("From").get(0));

        }
        // Third question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("c1")
                || formParam.get("Body").get(0).equalsIgnoreCase("c2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("c2")) {
                updateUserScore(formParam.get("From").get(0), plusPoints);

                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[3]);

            } else {
                updateUserScore(formParam.get("From").get(0), minusPoints);

                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[3]);
            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage + getUserScore(formParam.get("From").get(0)) + scoreEnd);

            sendNextQuestion(4, formParam.get("From").get(0));
        }
        // Fourth question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("d1")
                || formParam.get("Body").get(0).equalsIgnoreCase("d2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("d2")) {
                updateUserScore(formParam.get("From").get(0), plusPoints);

                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[4]);

            } else {
                updateUserScore(formParam.get("From").get(0), minusPoints);

                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[4]);

            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage + getUserScore(formParam.get("From").get(0)) + scoreEnd);

            sendNextQuestion(5, formParam.get("From").get(0));
        }
        // Fifth question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("e1")
                || formParam.get("Body").get(0).equalsIgnoreCase("e2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("e2")) {
                updateUserScore(formParam.get("From").get(0), plusPoints);

                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[5]);
            } else {
                updateUserScore(formParam.get("From").get(0), minusPoints);
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[5]);
            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage + getUserScore(formParam.get("From").get(0)) + scoreEnd);

            //TODO: save response in db
            sendQuizComplete(formParam.get("From").get(0));
        }

        return;
    }

    private boolean checkIfQuizAlreadyStarted(String phone) {

        Statement statement = QueryBuilder.select().all().from(KEYSPACENAME, "quizcorona")
                .where(QueryBuilder.eq("id", phone));

        ResultSet results = DbService.getInstance().getSession().execute(statement);
        for (Row row : results) {
            System.out.print("row-> " + row);
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

    int updateUserScore(String phone, int pointsToAdd) {
        Statement stmt = QueryBuilder.update(DbService.CLUSTER_NAME, tableName)
                .with(QueryBuilder.set("total_points", getUserScore(phone) + pointsToAdd)).where(QueryBuilder.eq("id", phone));
        DbService.getInstance().getSession().execute(stmt);
        System.out.println(getUserScore(phone));
        return getUserScore(phone);
    }

    void sendNextQuestion(int nextQuestion, String toPhone) {

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Message messageNextQuestion2 = Message.creator(
                                new com.twilio.type.PhoneNumber(toPhone),
                                new com.twilio.type.PhoneNumber(whatsappBotNumber),
                                questionsArr[nextQuestion - 1])
                                .create();
                    }
                },
                1000
        );

    }

    void sendQuizComplete(String toPhone) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Message messageNextQuestion2 = Message.creator(
                                new com.twilio.type.PhoneNumber(toPhone),
                                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                                "Congratulations. You have completes the quiz. Your contribution to the fund is 50 Rupees")
                                .create();
                    }
                },
                1000
        );
    }

    void sendMessage(String to, String from, String message) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            Message messageCreator = Message.creator(
                    new com.twilio.type.PhoneNumber(to),
                    new com.twilio.type.PhoneNumber(from),
                    message).create();
            System.out.println(messageCreator.getSid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createUserQuizStartEntry(String phone) {
        PreparedStatement insertStatement = DbService.getInstance().getSession()
                .prepare("INSERT INTO sb91_kysp.quizcorona"
                        + "(id ,phone, last_question, is_quiz_completed, replyTime, total_points, questionsentTime)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?);");
        BoundStatement boundStatement = new BoundStatement(insertStatement);
        DbService.getInstance().getSession()
                .execute(boundStatement.bind(phone, phone, "1", false, new Date().getTime(), 0, new Date().getTime()));


    }

    void deleteAllSessions(){

    }


}
