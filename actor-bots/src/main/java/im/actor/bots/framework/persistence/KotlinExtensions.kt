package im.actor.bots.framework.persistence

import akka.util.Timeout
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import shardakka.keyvalue.SimpleKeyValueJava
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

fun <T> ServerKeyValue.setDataClass(key: String, obj: T?) {
    if (obj == null) {
        setStringValue(key, null)
    } else {
        val output = ByteArrayOutputStream()
        val generator = jacksonObjectMapper().jsonFactory.createGenerator(output)
        generator.writeObject(obj)
        val str = String(output.toByteArray())
        setStringValue(key, str)
    }
}

inline fun <reified T: Any> ServerKeyValue.getDataClass(key: String): T? {
    val str = getStringValue(key)
    if (str == null) {
        return null
    } else {
        return jacksonObjectMapper().readValue<T>(str)
    }
}

fun <T> SimpleKeyValueJava<T>.get(key: String): T? {
    val res = syncGet(key, Timeout.apply(10, TimeUnit.SECONDS))
    if (res.isPresent) {
        return res.get()
    } else {
        return null
    }
}