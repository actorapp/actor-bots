package im.actor.bots.framework.parser;

public class ParsingUtils {

    public static String[] splitFirstWord(String text) {
        return text.trim().split(" ", 2);
    }
}
