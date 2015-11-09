package im.actor.bots.framework.parser;

public class MessageText extends ParsedMessage {

    final private String text;

    public MessageText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
