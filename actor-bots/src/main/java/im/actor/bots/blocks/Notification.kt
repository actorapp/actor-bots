package im.actor.bots.blocks

import im.actor.bots.framework.*
import im.actor.bots.framework.stateful.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

open class NotificationBot(scope: MagicForkScope) : MagicStatefulBot(scope) {

    var welcomeMessage = "Hello! I am notification bots and i can send you various notifications. " +
            "Just send me /subscribe and i will start to broadcast messages to you"

    override fun configure() {

        oneShot("/start") {
            if (isAdmin(scope.sender)) {
                sendText("Hello! I can help you distribute notifications to people. " +
                        "They just need to send me message '/subscribe' to subscribe to notifications " +
                        "and i will broadcast it to them.\n" +
                        "I have following commands:\n" +
                        "- */hook_url* - Get web hook url\n" +
                        "- */send* - Broadcast message\n" +
                        "- */subscribe_admin* - Subscribing to admin events\n" +
                        "- */unsubscribe_admin* - unsubscribing to admin events\n")
            } else {
                sendText(welcomeMessage)
            }
        }

        oneShot("/subscribe") {
            sendText("Congratulations! You have successfully *subscribed* to my notifications! To unsubscribe send /unsubscribe")
            sendToOverlord(NotificationOverlord.Subscribe(scope.peer))
        }

        oneShot("/unsubscribe") {
            sendText("You have *unsubscribed* from my notifications. Feel free to subscribe again with /subscribe command")
            sendToOverlord(NotificationOverlord.Unsubscribe(scope.peer))
        }

        oneShot("/subscribe_admin") {
            sendText("Congratulations! You have successfully *subscribed* to admin notifications! To unsubscribe send /unsubscribe_admin")
            sendToOverlord(NotificationOverlord.SubscribeAdmin(scope.peer))
        }

        oneShot("/unsubscribe_admin") {
            sendText("You have *unsubscribed* from admin notifications. Feel free to subscribe again with /subscribe_admin command")
            sendToOverlord(NotificationOverlord.UnsubscribeAdmin(scope.peer))
        }

        oneShot("/hook_url") {
            if (isSenderAdmin()) {
                var hook = scope.botKeyValue.getStringValue("notification_url")
                if (hook == null) {
                    hook = createHook("notification_hook_url")
                    scope.botKeyValue.setStringValue("notification_url", hook)
                }
                sendText("Notification hook is: *$hook*")
            } else {
                sendText("You is not allowed to do this")
            }
        }

        raw("/send") {

            var broadcastMessage: String? = null

            before {
                sendText("What do you want to broadcast?")
                goto("message")
            }

            expectInput("message") {
                received {
                    broadcastMessage = text
                    sendText("Success. Are you sure want to send message $broadcastMessage? Send yes or no in response.")
                    goto("confirm")
                }
                validate {
                    if (!isText) {
                        sendText("Please, send valid text message")
                        return@validate false
                    }
                    return@validate true
                }
            }

            expectInput("confirm") {
                received {
                    when (text.toLowerCase()) {
                        "yes" -> {
                            sendToOverlord(NotificationOverlord.DoBroadcast(broadcastMessage!!))
                            sendText("Message sent!")
                            goto("main")
                        }
                        "no" -> {
                            sendText("Message send cancelled.")
                            goto("main")
                        }

                    }
                }
                validate {
                    if (isText) {
                        when (text.toLowerCase()) {
                            "yes" -> {
                                return@validate true
                            }
                            "no" -> {
                                return@validate true
                            }
                        }
                    }
                    sendText("Please, send yes or no.")
                    return@validate false
                }
            }
        }
    }

    fun isSenderAdmin(): Boolean {
        if (scope.sender == null) {
            return false
        }
        val sender = getUser(scope.sender!!.id())
        if (sender.username.isPresent && admins.contains(sender.username.get())) {
            return true
        }
        return false
    }
}

class NotificationOverlord(scope: MagicOverlordScope) : MagicOverlord(scope) {

    val keyValue = scope.botKeyValue
    val subscribers = ArrayList<OutPeer>()
    val adminSubscribers = ArrayList<OutPeer>()

    // Events

    fun onText(text: String) {
        for (s in subscribers) {
            sendText(s, text)
        }

        onAdminText("Broadcasted message\n$text")
    }

    fun onAdminText(text: String) {
        for (s in adminSubscribers) {
            sendText(s, text)
        }
    }

    fun onSubscribe(peer: OutPeer) {
        if (subscribers.contains(peer)) {
            return
        }
        subscribers.add(peer)
        saveSubscribers()

        onAdminText("Subscribed $peer")
    }

    fun onUnsubscribe(peer: OutPeer) {
        subscribers.remove(peer)
        saveSubscribers()

        onAdminText("Unsubscribed $peer")
    }

    fun onSubscribeAdmin(peer: OutPeer) {
        if (adminSubscribers.contains(peer)) {
            return
        }
        adminSubscribers.add(peer)
        saveSubscribers()
    }

    fun onUnsubscribeAdmin(peer: OutPeer) {
        adminSubscribers.remove(peer)
        saveSubscribers()
    }

    fun saveSubscribers() {
        val storage = JSONObject()

        val peers = JSONArray()
        for (s in subscribers) {
            peers.put(s.toJson())
        }
        storage.put("peers", peers)

        val adminPeers = JSONArray()
        for (s in adminSubscribers) {
            adminPeers.put(s.toJson())
        }
        storage.put("adminPeers", adminPeers)

        keyValue.setStringValue("storage", storage.toString())
    }

    fun loadSubscribers() {
        subscribers.clear()
        adminSubscribers.clear()
        try {
            val storage = JSONObject(keyValue.getStringValue("storage"))
            val peers = storage.getJSONArray("peers")
            for (i in 0..peers.length()) {
                try {
                    subscribers.add(outPeerFromJson(peers.getJSONObject(i)))
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
            val adminPeers = storage.getJSONArray("adminPeers")
            for (i in 0..adminPeers.length()) {
                try {
                    adminSubscribers.add(outPeerFromJson(peers.getJSONObject(i)))
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    // Processor

    override fun preStart() {
        super.preStart()

        loadSubscribers()
    }

    override fun onReceive(update: Any?) {
        if (update is Subscribe) {
            onSubscribe(update.peer)
        } else if (update is Unsubscribe) {
            onUnsubscribe(update.peer)
        } else if (update is SubscribeAdmin) {
            onSubscribeAdmin(update.peer)
        } else if (update is UnsubscribeAdmin) {
            onUnsubscribeAdmin(update.peer)
        } else if (update is DoBroadcast) {
            onText(update.message)
        } else {
            super.onReceive(update)
        }
    }

    override fun onWebHookReceived(hook: HookData) {
        if (hook.jsonBody != null) {
            val text = hook.jsonBody.optString("text")
            if (text != null) {
                onText(text)
            }
        }
    }

    data class DoBroadcast(val message: String)

    data class Subscribe(val peer: OutPeer)

    data class Unsubscribe(val peer: OutPeer)

    data class SubscribeAdmin(val peer: OutPeer)

    data class UnsubscribeAdmin(val peer: OutPeer)
}