package im.actor.bots.framework.i18n;

import java.util.Random;

public class Strings {
    public static final String[] UNKNOWN_MESSAGES = {
            "Command is invalid. Say what?",
            "Command is invalid. I really didn't get it...",
            "Command is invalid. What do you mean?",
            "Command is invalid. Please, say it again in a good way.",
    };
    private static Random random = new Random();

    public static String unknown() {
        return UNKNOWN_MESSAGES[random.nextInt(UNKNOWN_MESSAGES.length)];
    }

}
