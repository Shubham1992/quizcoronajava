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


    @Path("/notifyWhatsappBot")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public void notifyWhatsappBot(Form formParam) {
        System.out.println(formParam);
        // create request to send message
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        if (formParam.get("Body").get(0).equalsIgnoreCase("hi")) {
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                    new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                    "Welcome to the Corona quiz. \n Here is your first question:\n What is the apt distance that should be maintained between individuals?\n " +
                            "a1. 3 feet\n" +
                            "a2. 1 feet\n" +
                            "a3. 10 feet\n \n\n Reply with the correct option(for ex: 'a1' for 3 feet)")
                    .create();
            System.out.println(message.getSid());

        }
        // First question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("a1")
                || formParam.get("Body").get(0).equalsIgnoreCase("a2")
                || formParam.get("Body").get(0).equalsIgnoreCase("a3")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("a1")) {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Right Answer")
                        .create();
                System.out.println(message.getSid());
            } else {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Wrong Answer. \nCorrect answer is 3 Feet\nExplanation- When someone coughs or sneezes they spray small liquid droplets from their nose or mouth which may contain virus. These droplets are too heavy to hang in the air. They quickly fall on floors or surfaces. The projectile of those droplets are 3 feet at the max.\nhttps://www.youtube.com/watch?time_continue=89&v=1APwq1df6Mw&feature=emb_logo")
                        .create();
                System.out.println(message.getSid());
            }

            sendNextQuestion(2, formParam.get("From").get(0));

        }
        // Third question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("b1")
                || formParam.get("Body").get(0).equalsIgnoreCase("b2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("b2")) {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Right Answer")
                        .create();
                System.out.println(message.getSid());
            } else {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Wrong Answer. \nCorrect answer is 'No'\nExplanation-Only 1 out of 6 people who gets Covid will develop respiratory distress.\n")
                        .create();
                System.out.println(message.getSid());
            }
            sendNextQuestion(3, formParam.get("From").get(0));

        }
        // Third question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("c1")
                || formParam.get("Body").get(0).equalsIgnoreCase("c2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("c2")) {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Right Answer")
                        .create();
                System.out.println(message.getSid());
            } else {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Wrong Answer. \nCorrect answer is 'No'\nExplanation-Till date, there is no evidence that a dog, cat or any pet can transmit COVID-19. COVID-19 is mainly spread through droplets produced when an infected person coughs, sneezes, or speaks.")
                        .create();
                System.out.println(message.getSid());
            }
            sendNextQuestion(4, formParam.get("From").get(0));
        }
        // Fourth question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("d1")
                || formParam.get("Body").get(0).equalsIgnoreCase("d2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("d2")) {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Right Answer")
                        .create();
                System.out.println(message.getSid());
            } else {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Wrong Answer. \nCorrect answer is 'No'\nExplanation-Coronaviruses are a group of related viruses that cause diseases in mammals and birds. In humans, coronaviruses cause respiratory tract infections that can be mild, such as some cases of the common cold (among other possible causes, predominantly rhinoviruses), and others that can be lethal, such as SARS, MERS, and COVID-19.\n" +
                                "It’s called novel because Covid-19 is a new kind of coronavirus that has been found to infect humans.")
                        .create();
                System.out.println(message.getSid());
            }
            sendNextQuestion(5, formParam.get("From").get(0));
        }
        // Fifth question response
        else if (formParam.get("Body").get(0).equalsIgnoreCase("e1")
                || formParam.get("Body").get(0).equalsIgnoreCase("e2")) {
            if (formParam.get("Body").get(0).equalsIgnoreCase("e2")) {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Right Answer")
                        .create();
                System.out.println(message.getSid());
            } else {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(formParam.get("From").get(0)),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        "Wrong Answer. \nCorrect answer is 'No'\nExplanation- People can have Covid-19 infection and may not show any symptoms. They’re called healthy carriers. While they may not show Covid related symptoms at all, but they can transmit the virus to others.")
                        .create();
                System.out.println(message.getSid());
            }
            //TODO: save response in db
            sendQuizComplete(formParam.get("From").get(0));
        }

        return;
    }

    void sendNextQuestion(int nextQuestion, String toPhone) {
        switch (nextQuestion) {
            case 2: {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Message messageNextQuestion2 = Message.creator(
                                        new com.twilio.type.PhoneNumber(toPhone),
                                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                                        "Covid will always present itself as respiratory distress/difficulty in breathing?\n" +
                                                "b1. Yes\nb2. No")
                                        .create();
                            }
                        },
                        1000
                );
            }
            break;
            case 3: {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Message messageNextQuestion2 = Message.creator(
                                        new com.twilio.type.PhoneNumber(toPhone),
                                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                                        "3)   Covid can spread through pets like cats and dogs?    \n" +
                                                "c1. Yes\nc2. No")
                                        .create();
                            }
                        },
                        1000
                );
            }
            break;
            case 4: {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Message messageNextQuestion2 = Message.creator(
                                        new com.twilio.type.PhoneNumber(toPhone),
                                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                                        "Coronavirus is new and has spread to humans for the first time?\n" +
                                                "d1. Yes\nd2. No")
                                        .create();
                            }
                        },
                        1000
                );
            }
            break;
            case 5: {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Message messageNextQuestion2 = Message.creator(
                                        new com.twilio.type.PhoneNumber(toPhone),
                                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                                        "If a person has no flu like symptoms, she/he cannot transmit covid-19 to others?\n" +
                                                "e1. Yes\ne2. No")
                                        .create();
                            }
                        },
                        1000
                );
            }
        }
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

}
