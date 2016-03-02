//package im.actor.bots.framework.traits
//
//import org.json.JSONObject
//import java.net.URLEncoder
//
///**
// * Parse.com trait. Useful for storing bots's data in casual way. But, before use, try our built-in
// * storage
// */
//interface ParseTrait {
//    fun parseAddObject(className: String, obj: JSONObject): String?
//    fun parseUpdateObject(className: String, id: String, obj: JSONObject): Boolean
//    fun parseGetObject(className: String, id: String): JSONObject?
//    fun parseFindObject(className: String, query: String): JSONObject?
//}
//
//class ParseTraitImpl(val appId: String, val restApiKey: String) : ParseTrait,
//        HTTPTrait by HTTPTraitImpl() {
//
//    private val ENDPOINT = "https://api.parse.com/1"
//
//    override fun parseAddObject(className: String, obj: JSONObject): String? {
//        val res = urlPostJson("$ENDPOINT/classes/$className", obj,
//                "X-Parse-Application-Id", appId,
//                "X-Parse-REST-API-Key", restApiKey)
//        if (res != null) {
//            return res.getString("objectId")
//        }
//        return null
//    }
//
//    override fun parseUpdateObject(className: String, id: String, obj: JSONObject): Boolean {
//        val res = urlPostJson("$ENDPOINT/classes/$className/$id", obj,
//                "X-Parse-Application-Id", appId,
//                "X-Parse-REST-API-Key", restApiKey)
//        return res != null
//    }
//
//    override fun parseGetObject(className: String, id: String): JSONObject? {
//        throw NotImplementedError()
//    }
//
//    override fun parseFindObject(className: String, query: String): JSONObject? {
//        val res = urlGetJson("$ENDPOINT/classes/$className?where=${URLEncoder.encode(query)}",
//                "X-Parse-Application-Id", appId,
//                "X-Parse-REST-API-Key", restApiKey)
//        if (res != null) {
//            val res = res.optJSONArray("results")
//            if (res != null) {
//                if (res.length() != 1) {
//                    return null
//                } else {
//                    return res.getJSONObject(0)
//                }
//            }
//        }
//        return null
//    }
//}