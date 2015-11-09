package im.actor.bots.framework.parser;

import java.util.ArrayList;

public abstract class ParsedMessage {
    public static ParsedMessage matchType(String message) {
        message = message.trim();
        if (message.startsWith("/")) {
            String[] data = ParsingUtils.splitFirstWord(message);
            String command = data[0].substring(1);
            ArrayList<String> args = new ArrayList<String>();
            String text = "";
            if (data.length == 2) {
                text = data[1];
            }

            try {
                if (command.contains("(") || command.endsWith(")")) {
                    String container = command.substring(command.indexOf('(') + 1, command.length() - 1);
                    if (container.contains("(") || container.contains(")")) {
                        throw new RuntimeException();
                    }
                    for (String s : container.split(",")) {
                        args.add(s.trim());
                    }
                    command = command.substring(0, command.indexOf('('));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return new MessageCommand(command, args, text);
        } else {
            return new MessageText(message);
        }
    }
}
