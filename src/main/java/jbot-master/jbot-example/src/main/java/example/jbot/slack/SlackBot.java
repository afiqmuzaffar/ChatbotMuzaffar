package example.jbot.slack;


import example.jbot.neuralNetWok.QA;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import example.jbot.neuralNetWok.CampusQA;


import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.EventType;
import me.ramswaroop.jbot.core.common.JBot;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.WebSocketSession;

import java.util.regex.Matcher;

/**
 * A simple Slack Bot. You can create multiple bots by just
 * extending {@link Bot} class like this one. Though it is
 * recommended to create only bot per jbot instance.
 *
 * @author ramswaroop
 * @version 1.0.0, 05/06/2016
 */
@JBot
@Profile("slack")
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    static MultiLayerNetwork model;
    private QA campusQA;

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }



    /**
     * Invoked when an item is pinned in the channel.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.PIN_ADDED)
    public void onPinAdded(WebSocketSession session, Event event) {
        reply(session, event, "Thanks for the pin! You can find all pinned items under channel details.");
    }



    @Controller(pattern="(What is the definition of goods and services?)")
    public void onFAQ1(WebSocketSession session, Event event) throws Exception {
        campusQA = new CampusQA();
        reply(session, event, new Message(campusQA.getAnswer("What is the definition of goods and services?")));
        nextConversation(event);
    }

    @Controller(pattern="(Is PIP relevant to all parties in the organization?)")
    public void onFAQ2(WebSocketSession session, Event event) throws Exception {
        campusQA = new CampusQA();
        reply(session, event, new Message(campusQA.getAnswer("Is PIP relevant to all parties in the organization?")));
        nextConversation(event);
    }


    @Controller(events = EventType.MESSAGE, pattern = "Hi", next = "nextChat")
    public void onFAQ(WebSocketSession session, Event event) {
        startConversation(event, "nextChat");   // start conversation
        reply(session, event, "Hi There!");
    }

    @Controller(next = "conversationEnd")
    public void nextChat(WebSocketSession session, Event event){
//        campusQA = new CampusQA();
        reply(session, event, "Hi Nexent the chatbot,\nwe would like to know list people who is attending work/OOO/MC/AL/late right now?");
        /*reply(session, event, new Message(campusQA.getAnswer("What is the definition of goods and services?")));*/
        nextConversation(event);
    }


    @Controller
    public void conversationEnd(WebSocketSession session, Event event) {
        if (event.getText().contains("Thanks")) {
            reply(session, event, "Its my honour :)");
            nextConversation(event);    // jump to next question in conversation
        } else {
            reply(session, event, "Unfortunately,Nexent the chatbot and I couldn't chat together due to unique API ID needed");
            stopConversation(event);    // stop conversation only if user says no
        }
    }



}