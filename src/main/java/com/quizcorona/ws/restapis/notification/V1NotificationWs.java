package com.quizcorona.ws.restapis.notification;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.representation.Form;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Path("/v1")

public class V1NotificationWs {

    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    public static String[] questionsArr = {"Welcome to the Corona quiz. \n Here is your first question:\n What is the apt distance that should be maintained between individuals?\n " +
            "a1. 3 feet\n" +
            "a2. 1 feet\n" +
            "a3. 10 feet\n \n\n Reply with the correct option(for ex: 'a1' for 3 feet)", "Covid will always present itself as respiratory distress/difficulty in breathing?\n" +
            "b1. Yes\nb2. No"};
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

    public static String[] rightAnswerArray = {"",

            "Good job, that’s the right answer, the apt distance that should be maintained between individuals is 3 feet." +
                    " Here’s why : Explanation- When someone coughs or sneezes they spray small liquid droplets from their nose or mouth" +
                    " which may contain virus. These droplets are too heavy to hang in the air. They quickly fall on floors or surfaces. The projectile of those droplets are 3 feet at the max.\nhttps://www.youtube.com/watch?time_continue=89&v=1APwq1df6Mw&feature=emb_logo",

            "Right Answer",

            "Right Answer",

            "Right Answer",

            "Right Answer"};

    public static String[] wrongAnswerArray = {"",

            "Oh oh, that doesn’t seem to be correct, the apt distance that should be maintained " +
                    "between individuals is 3 feet. Here’s why\nExplanation- When someone coughs or sneezes they spray small liquid droplets" +
                    " from their nose or mouth which may contain virus. These droplets are too heavy to hang in the air. " +
                    "They quickly fall on floors or surfaces. The projectile of those droplets are 3 feet at the max." +
                    "\nhttps://www.youtube.com/watch?time_continue=89&v=1APwq1df6Mw&feature=emb_logo",

            "Wrong Answer",

            "Wrong Answer. \nCorrect answer is 'No'\nExplanation-Till date, there is no evidence that a dog, cat or any pet can transmit COVID-19." +
                    " COVID-19 is mainly spread through droplets produced when an infected person coughs, sneezes, or speaks.",

            "Wrong Answer",

            "Wrong Answer",

            "Wrong Answer"};

    private static String scoreMessage = "Feel free to share with your friends..\n" +
            "Your total score is 1 point(s).\n" +
            "Let’s move to the next question… \n";

    @Path("/notifyWhatsappBot")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public void notifyWhatsappBot(Form formParam) {
        System.out.println(formParam);
        // create request to send message
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        if (formParam.get("Body").get(0).equalsIgnoreCase("hi")) {
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, introduction1);
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, introduction2);
        } else if (formParam.get("Body").get(0).equalsIgnoreCase("start")) {
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, questionsArr[0]);
        }
        // First question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("a1")
                || formParam.get("Body").get(0).equalsIgnoreCase("a2")
                || formParam.get("Body").get(0).equalsIgnoreCase("a3")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("a1")) {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[1]);
            } else {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[1]);
            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage);

            sendNextQuestion(2, formParam.get("From").get(0));

        }
        // Third question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("b1")
                || formParam.get("Body").get(0).equalsIgnoreCase("b2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("b2")) {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[2]);
            } else {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[2]);

            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage);

            sendNextQuestion(3, formParam.get("From").get(0));

        }
        // Third question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("c1")
                || formParam.get("Body").get(0).equalsIgnoreCase("c2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("c2")) {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[3]);

            } else {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[3]);
            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage);

            sendNextQuestion(4, formParam.get("From").get(0));
        }
        // Fourth question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("d1")
                || formParam.get("Body").get(0).equalsIgnoreCase("d2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("d2")) {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[4]);

            } else {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[4]);

            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage);

            sendNextQuestion(5, formParam.get("From").get(0));
        }
        // Fifth question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("e1")
                || formParam.get("Body").get(0).equalsIgnoreCase("e2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("e2")) {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, rightAnswerArray[5]);
            } else {
                sendMessage(formParam.get("From").get(0), whatsappBotNumber, wrongAnswerArray[5]);
            }
            sendMessage(formParam.get("From").get(0), whatsappBotNumber, scoreMessage);

            //TODO: save response in db
            sendQuizComplete(formParam.get("From").get(0));
        }

        return;
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
                                "Congratulations. You have completes the quiz. blah blah blah...")
                                .create();
                    }
                },
                1000
        );
    }

    void sendMessage(String to, String from, String message) {
        try {
            Message messageCreator = Message.creator(
                    new com.twilio.type.PhoneNumber(to),
                    new com.twilio.type.PhoneNumber(from),
                    message).create();
            System.out.println(messageCreator.getSid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
