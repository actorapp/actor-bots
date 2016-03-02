package im.actor.bots.framework.traits

import org.json.JSONObject
import java.util.*

/**
 * API.AI Integration trait.
 * Before use set your Subscription Key and register agents
 */
interface AiTrait {
    var aiSubscriptionKey: String?
    fun registerAiAgent(key: String, lang: String, token: String)

    fun aiQuery(query: String): AiResponse?

    fun aiQuery(query: String, closure: AiResponse.() -> Unit)

    fun aiQuery(agent: String, query: String): AiResponse?

    fun aiQuery(agent: String, query: String, closure: AiResponse.() -> Unit)
}

class AiTraitImpl : AiTrait,
        HTTPTrait by HTTPTraitImpl() {

    private val ENDPOINT = "https://api.api.ai/v1/"
    private var defaultAgent: String? = null
    private val agents = HashMap<String, AiAgent>()
    private val session = Random().nextLong()
    override var aiSubscriptionKey: String? = null

    override fun registerAiAgent(key: String, lang: String, token: String) {
        if (aiSubscriptionKey == null) {
            throw RuntimeException("set subscription key first")
        }
        if (agents.containsKey(key)) {
            throw RuntimeException("$key agent is already registered!")
        }
        agents.put(key, AiAgent(key, lang, token))
        if (defaultAgent == null) {
            defaultAgent = key
        }
    }

    override fun aiQuery(query: String): AiResponse? {
        if (defaultAgent == null) {
            throw RuntimeException("No Agents registered!")
        }
        return aiQuery(defaultAgent!!, query)
    }

    override fun aiQuery(query: String, closure: AiResponse.() -> Unit) {
        aiQuery(query)?.closure()
    }

    override fun aiQuery(agent: String, query: String): AiResponse? {
        val agentConfig = agents[agent]!!
        val req = JSONObject()
        req.put("lang", agentConfig.lang)
        req.put("query", query)
        req.put("sessionId", "$session")
        val resp = aiRequest("query", agentConfig, req)
        if (resp != null) {
            return AiResponse(resp)
        } else {
            return null
        }
    }

    override fun aiQuery(agent: String, query: String, closure: AiResponse.() -> Unit) {
        aiQuery(agent, query)?.closure()
    }

    private fun aiRequest(command: String, agent: AiAgent, request: JSONObject): JSONObject? {
        return (urlPostJson(ENDPOINT + command + "?v=20150910",
                Json.JsonObject(request), "Authorization", "Bearer ${agent.token}",
                "ocp-apim-subscription-key", aiSubscriptionKey!!) as? Json.JsonObject)?.json
    }
}

private data class AiAgent(val key: String, val lang: String, val token: String)

class AiResponse(val raw: JSONObject) {

    val action: String
    val speech: String?

    val pQuery: String?
    val pSimplified: String?
    val pRequestType: String?
    val pSummary: String?
    val pTime: Date?

    init {
        val result = raw.getJSONObject("result")
        action = result.optString("action", "input.unknown")

        if (result.has("fulfillment")) {
            val fulfillment = result.getJSONObject("fulfillment")
            val speech2 = fulfillment.optString("speech")
            if (speech2 == "") {
                speech = null
            } else {
                speech = speech2
            }
        } else {
            speech = null
        }
        if (result.has("parameters")) {
            val p = result.getJSONObject("parameters")
            pQuery = p.optString("q")
            pSimplified = p.optString("simplified")
            pRequestType = p.optString("request_type")
            val pSummaryS = p.optString("summary")
            if (pSummaryS == "") {
                pSummary = null
            } else {
                pSummary = pSummaryS;
            }
        } else {
            pQuery = null
            pSimplified = null
            pRequestType = null
            pSummary = null
        }

        pTime = null;
    }
}