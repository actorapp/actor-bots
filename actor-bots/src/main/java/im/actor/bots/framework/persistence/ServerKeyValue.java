package im.actor.bots.framework.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import im.actor.botkit.RemoteBot;
import im.actor.bots.BotMessages;
import scala.Option;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import static scala.compat.java8.JFunction.proc;

public class ServerKeyValue {

    private static final String KEY_SPACE = "default";

    private String keySpace;
    private RemoteBot remoteBot;
    private HashMap<String, String> cachedValues = new HashMap<String, String>();

    public ServerKeyValue(@NotNull RemoteBot remoteBot) {
        this(remoteBot, KEY_SPACE);
    }

    public ServerKeyValue(@NotNull RemoteBot remoteBot, @NotNull String keySpace) {
        this.keySpace = keySpace;
        this.remoteBot = remoteBot;
    }

    public void setStringValue(@NotNull String key, @Nullable String value) {
        cachedValues.put(key, value);
        remoteBot.requestSetValue(keySpace, key, value).foreach(proc(s -> {

        }), remoteBot.context().dispatcher());
    }

    @Nullable
    public String getStringValue(@NotNull String key) throws Exception {
        if (cachedValues.containsKey(key)) {
            return cachedValues.get(key);
        }
        BotMessages.Container<Option<String>> res = Await.result(remoteBot.requestGetValue(keySpace, key), Duration.create(60, TimeUnit.SECONDS));
        if (res.value().nonEmpty()) {
            String val = res.value().get();
            cachedValues.put(key, val);
            return val;
        }
        return null;
    }

    public void setDoubleValue(@NotNull String key, @Nullable Double value) {
        if (value != null) {
            setStringValue(key, Double.toString(value));
        } else {
            setStringValue(key, null);
        }
    }

    @Nullable
    public Double getDoubleValue(@NotNull String key) throws Exception {
        String res = getStringValue(key);
        if (res != null) {
            return Double.parseDouble(res);
        } else {
            return null;
        }
    }

    public void setIntValue(@NotNull String key, @Nullable Integer value) {
        if (value != null) {
            setStringValue(key, Integer.toString(value));
        } else {
            setStringValue(key, null);
        }
    }

    @Nullable
    public Integer getIntValue(@NotNull String key) throws Exception {
        String res = getStringValue(key);
        if (res != null) {
            return Integer.parseInt(res);
        } else {
            return null;
        }
    }

    public void setBoolValue(@NotNull String key, @Nullable Boolean value) {
        if (value != null) {
            setStringValue(key, Boolean.toString(value));
        } else {
            setStringValue(key, null);
        }
    }

    @Nullable
    public Boolean getBoolValue(@NotNull String key) throws Exception {
        String res = getStringValue(key);
        if (res != null) {
            return Boolean.parseBoolean(res);
        } else {
            return null;
        }
    }

    public boolean getBoolValue(@NotNull String key, boolean value) throws Exception {
        String res = getStringValue(key);
        if (res != null) {
            return Boolean.parseBoolean(res);
        } else {
            return value;
        }
    }

    public void setLongValue(@NotNull String key, @Nullable Long value) {
        if (value != null) {
            setStringValue(key, Long.toString(value));
        } else {
            setStringValue(key, null);
        }
    }

    @Nullable
    public Long getLongValue(@NotNull String key) throws Exception {
        String res = getStringValue(key);
        if (res != null) {
            return Long.parseLong(res);
        } else {
            return null;
        }
    }

    public void setJSONValue(@NotNull String key, @Nullable JSONObject value) {
        if (value != null) {
            setStringValue(key, value.toString());
        } else {
            setStringValue(key, null);
        }
    }

    @Nullable
    public JSONObject getJSONValue(@NotNull String key) throws Exception {
        String res = getStringValue(key);
        if (res != null) {
            return new JSONObject(res);
        } else {
            return null;
        }
    }
}