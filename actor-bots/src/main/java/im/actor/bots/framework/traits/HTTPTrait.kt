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
    fun urlPostJson(url: String, content: JSONObject, vararg headers: String): JSONObject?
    fun urlGetJson(url: String, vararg headers: String): JSONObject?
    fun urlGetJsonArray(url: String, vararg headers: String): JSONArray?
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
            if (res.statusLine.statusCode != 200) {
                res.close()
                return null
            }
            res.close()
            return text
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun urlPostJson(url: String, content: JSONObject, vararg headers: String): JSONObject? {
        assumeHttp()
        try {
            val post = HttpPost(url)
            for (i in 0..headers.size() / 2 - 1) {
                post.addHeader(headers[i * 2], headers[i * 2 + 1])
            }
            post.entity = StringEntity(content.toString(), ContentType.APPLICATION_JSON)
            val res = client!!.execute(post)
            if (res.getStatusLine().getStatusCode() != 200) {
                res.close()
                return null
            }
            val text = IOUtils.toString(res.getEntity().getContent())
            res.close()
            return JSONObject(text)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun urlGetJson(url: String, vararg headers: String): JSONObject? {
        val res = urlGetText(url, *headers) ?: return null
        try {
            return JSONObject(res)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun urlGetJsonArray(url: String, vararg headers: String): JSONArray? {
        val res = urlGetText(url, *headers) ?: return null
        try {
            return JSONArray(res)
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