package im.actor.bots.framework.i18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

public class I18NEngine {

    private final Random random = new Random();
    private HashMap<String, ArrayList<String>> strings = new HashMap<>();

    public I18NEngine(String fileName) throws IOException {

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(fileName));

        for (String key : properties.stringPropertyNames()) {
            String value = new String(properties.getProperty(key).getBytes("ISO-8859-1"), "UTF-8");

            String[] keyParts = key.split("\\.");
            try {
                Integer.parseInt(keyParts[keyParts.length - 1]);
                key = "";
                for (int i = 0; i < keyParts.length - 1; i++) {
                    if (key.length() > 0) {
                        key += ".";
                    }
                    key += keyParts[i];
                }
            } catch (Exception e) {
                // Expected
            }

            if (strings.containsKey(key)) {
                strings.get(key).add(value);
            } else {
                ArrayList<String> s = new ArrayList<>();
                s.add(value);
                strings.put(key, s);
            }
        }
    }

    public String pick(String key) {
        ArrayList<String> s = strings.get(key);
        int index;
        synchronized (random) {
            index = random.nextInt(s.size());
        }
        return s.get(index);
    }
}