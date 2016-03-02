package im.actor.bots.framework.traits

import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * HTTP Requesting Trait
 */
interface HTTPTrait {
    fun urlGetText(url: String, vararg headers: String): String?
    fun urlPostUrlEncodedText(url: String, content: String, vararg headers: String): String?
    fun urlPostJson(url: String, content: Json, vararg headers: String): Json?
    fun urlGetJson(url: String, vararg headers: String): Json?
}

class HTTPTraitImpl : HTTPTrait {

    private var client: CloseableHttpClient? = null

    override fun urlGetText(url: String, vararg headers: String): String? {
        assumeHttp()
        try {
            val get = HttpGet(url)
            for (i in 0..headers.size / 2 - 1) {
                get.addHeader(headers[i * 2], headers[i * 2 + 1])
            }
            val res = client!!.execute(get)
            val text = IOUtils.toString(res.entity.content)
            res.close()
            if (res.statusLine.statusCode >= 200 && res.statusLine.statusCode < 300) {
                return text
            }
            return null
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun urlPostUrlEncodedText(url: String, content: String, vararg headers: String): String? {
        assumeHttp()
        try {
            val post = HttpPost(url)
            for (i in 0..headers.size / 2 - 1) {
                post.addHeader(headers[i * 2], headers[i * 2 + 1])
            }
            post.entity = StringEntity(content, ContentType.APPLICATION_FORM_URLENCODED)
            val res = client!!.execute(post)
            val text = IOUtils.toString(res.entity.content)
            res.close()
            if (res.statusLine.statusCode >= 200 && res.statusLine.statusCode < 300) {
                return text
            }
            return null
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun urlPostJson(url: String, content: Json, vararg headers: String): Json? {
        assumeHttp()
        try {
            val post = HttpPost(url)
            for (i in 0..headers.size / 2 - 1) {
                post.addHeader(headers[i * 2], headers[i * 2 + 1])
            }
            post.entity = StringEntity(content.toString(), ContentType.APPLICATION_JSON)
            val res = client!!.execute(post)
            val text = IOUtils.toString(res.entity.content)
            res.close()
            if (res.statusLine.statusCode >= 200 && res.statusLine.statusCode < 300) {
                return parseJson(text)
            }
            return null
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun urlGetJson(url: String, vararg headers: String): Json? {
        val res = urlGetText(url, *headers) ?: return null
        try {
            return parseJson(res)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun assumeHttp() {
        if (client == null) {
            client = HttpClients.createDefault()
        }
    }
}

fun parseJson(text: String): Json? {
    try {
        return Json.JsonObject(JSONObject(text))
    } catch(e: Exception) {

    }
    try {
        return Json.JsonArray(JSONArray(text))
    } catch(e: Exception) {

    }

    return null
}

sealed class Json {
    class JsonObject(val json: JSONObject) : Json() {
        override fun toString(): String {
            return json.toString()
        }
    }

    class JsonArray(val json: JSONArray) : Json() {
        override fun toString(): String {
            return json.toString()
        }
    }
}