package im.actor.bots.blocks

import im.actor.bots.framework.*
import im.actor.bots.framework.persistence.ServerKeyValue
import im.actor.bots.framework.stateful.MagicStatefulBot
import im.actor.bots.framework.stateful.oneShot
import im.actor.bots.framework.traits.HTTPTrait
import im.actor.bots.framework.traits.HTTPTraitImpl
import org.json.JSONObject
import java.util.*

private val OAuth2WebHookName = "oauth_callback_url"
private var apiCache = HashMap<String, HashMap<Int, OAuth2Api>>()

abstract class OAuth2Bot<T : OAuth2Api>(scope: MagicForkScope) : MagicStatefulBot(scope) {

    // Used for generating random state string
    private val random = Random()

    override fun configure() {

        // Configuring built-in admin method
        if (isAdminScope()) {
            oneShot("/token") {
                sendText("OAuth2 Callback url is: " + getOAuthCallback())
            }
        }

        if (scope.peer.isPrivate) {
            var api = apiForUid(scope.peer.id)
            oneShot("/login") {
                if (api.isAuthenticated()) {
                    sendText("You is already authenticated. Send [/logout](send:/logout) to logout.")
                } else {
                    sendText("Please, open url: ${api.authenticateUrl(registerOAuth2Request())}")
                }
            }
            oneShot("/logout") {
                if (!api.isAuthenticated()) {
                    sendText("You is not authenticated. Please, send [/login](send:/login) to log in.")
                } else {
                    api.revokeAuthentication()
                    api.saveAuthState()
                    sendText("You successfully logged out.")
                }
            }
        } else {
            oneShot("/login") {
                sendText("Please, do it in private")
            }
            oneShot("/logout") {
                sendText("Please, do it in private")
            }
        }
    }

    /**
     * Getting API for UID with built-in cache
     */
    fun apiForUid(uid: Int): T {
        synchronized(apiCache) {
            var res = apiCache[scope.name]
            if (res != null) {
                val cached = res[uid]
                if (cached != null) {
                    return cached as T
                }
                val resApi = createApi(uid)
                res.put(uid, resApi)
                return resApi
            } else {
                res = HashMap<Int, OAuth2Api>()
                val resApi = createApi(uid)
                res.put(uid, resApi)
                apiCache.put(scope.name, res)
                return resApi
            }
        }
    }

    /**
     * Create API for user with UID
     */
    abstract fun createApi(uid: Int): T


    /**
     * Called when authentication successful
     */
    open fun onAuthSuccess() {

    }

    /**
     * Called when authentication failed
     */
    open fun onAuthFailure() {

    }

    //
    // Implementation
    //

    /**
     * Generating random state string
     */
    fun randomState(): String {
        return Math.abs(random.nextLong()).toString()
    }

    /**
     * Implement this method for handling OAuth2 Response
     */
    fun onOAuthResponse(code: String) {
        val api = apiForUid(scope.peer.id)
        if (api.authenticate(code)) {
            api.saveAuthState()
            onAuthSuccess()
        } else {
            onAuthFailure()
        }
    }

    /**
     * Registering OAuth2 request with internally generated state
     */
    fun registerOAuth2Request(): String {
        val state = randomState()
        registerOAuth2Request(state)
        return state
    }

    /**
     * Registering OAuth2 request
     */
    fun registerOAuth2Request(state: String) {
        if (!scope.peer.isPrivate) {
            throw RuntimeException("Can't register state not from private chat")
        }
        sendToOverlord(RegisterOAuth2State(state, scope.peer))
    }

    /**
     * Get This OAuth2 Callback to entering to OAuth2 provider
     */
    fun getOAuthCallback(): String {
        val cached = scope.botKeyValue.getStringValue("oauth_callback_url")
        if (cached != null) {
            return cached
        }
        val res = createHook(OAuth2WebHookName) ?: throw RuntimeException("Unable to register hook")
        scope.botKeyValue.setStringValue("oauth_callback_url", res)
        return res
    }

    override fun onOverlordMessage(message: Any) {
        when (message) {
            is OAuth2Result -> {
                onOAuthResponse(message.code)
            }
        }
    }
}

/**
 * Subclass from this overlord for providing OAuth2 support
 */
open class OAuth2Overlord(scope: MagicOverlordScope) : MagicOverlord(scope) {

    private val ids = HashMap<String, OutPeer>()

    override fun onWebHookReceived(hook: HookData) {
        when (hook.name) {
            OAuth2WebHookName -> {
                var args = hook.query.split("&")
                var code: String? = null
                var state: String? = null
                for (a in args) {
                    val parts = a.split("=")
                    if (parts.size != 2) {
                        continue
                    }
                    when (parts[0]) {
                        "code" -> {
                            code = parts[1]
                        }
                        "state" -> {
                            state = parts[1]
                        }
                    }
                }

                if (code != null && state != null) {
                    val peer = ids[state]
                    if (peer != null) {
                        sendToForks(peer, OAuth2Result(code))
                    }
                }
            }
        }
    }

    override fun onReceive(update: Any?) {
        when (update) {
            is RegisterOAuth2State -> {
                ids.put(update.state, update.peer)
            }
            else -> {
                super.onReceive(update)
            }
        }
    }
}

data class OAuth2Result(val code: String)
data class RegisterOAuth2State(val state: String, val peer: OutPeer)

abstract class OAuth2Api(val clientId: String, val clientSecret: String, val storage: ServerKeyValue, val uid: Int) :
        HTTPTrait by HTTPTraitImpl() {

    init {
        val auth = storage.getJSONValue("auth_$uid")
        if (auth != null) {
            loadAuthState(auth)
        }
    }

    abstract fun isAuthenticated(): Boolean

    abstract fun authenticate(authCode: String): Boolean

    abstract fun authenticateUrl(state: String): String

    abstract fun revokeAuthentication()

    fun saveAuthState() {
        var obj = JSONObject()
        saveAuthState(obj)
        storage.setJSONValue("auth_$uid", obj)
    }

    protected abstract fun saveAuthState(obj: JSONObject)

    protected abstract fun loadAuthState(obj: JSONObject)
}