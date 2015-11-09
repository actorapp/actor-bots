package im.actor.bots.framework.parser;

import java.util.List;

public class MessageCommand extends ParsedMessage {

    private String command;
    private List<String> args;
    private String data;

    public MessageCommand(String command, List<String> args, String data) {
        this.command = command;
        this.data = data;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }

    public List<String> getArgs() {
        return args;
    }
}
