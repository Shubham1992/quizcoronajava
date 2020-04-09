package com.quizcorona.ws.restapis.notification;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.twilio.type.PhoneNumber;

import java.io.IOException;

public class SendTwilioMessage {
    static void sendMessage() {


        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post("https://api.twilio.com/2010-04-01/Accounts/AC180e1c8f1ba9de802d04178f5712ea3a/Messages.json")
                    .header("Authorization", "Basic QUMxODBlMWM4ZjFiYTlkZTgwMmQwNDE3OGY1NzEyZWEzYTo3NWMwMjU5OWRmYzhlMTZmMDUxMjA2ZTNlNGI4NGY4OQ==")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("To", "whatsapp:+918899888995")
                    .field("From", "whatsapp:+14155238886")
                    .field("Body", "Your appointment 😐 is coming up on July 21 at 3PM")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }
}
