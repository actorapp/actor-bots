package im.actor.bots.framework.stateful

import im.actor.bots.BotMessages
import im.actor.bots.framework.MagicBotDocMessage
import im.actor.bots.framework.MagicBotJsonMessage
import im.actor.bots.framework.MagicBotMessage
import im.actor.bots.framework.MagicBotTextMessage
import org.json.JSONObject
import java.util.*

abstract class Expect(val stateName: String, val parent: Expect?) : ExpectContainer {

    var defaultState: String? = null
    var child = HashMap<String, Expect>()

    private var beforeClosure: (ExpectContext.() -> Unit)? = null

    fun before(before: (ExpectContext.() -> Unit)?) {
        beforeClosure = before
    }

    open fun onReceived(context: ExpectContext) {

    }

    open fun onBefore(context: ExpectContext) {
        if (beforeClosure != null) {
            applyContext(context, beforeClosure!!)
        }
    }

    override fun addChild(expect: Expect) {
        if (defaultState == null) {
            defaultState = expect.stateName
        }
        child.put(expect.stateName, expect)
    }

    protected fun applyContext(context: ExpectContext, closure: ExpectContext.() -> Unit) {
        context.closure()
    }

    protected fun applyContext(context: ExpectContext, closure: ExpectContext.() -> Boolean): Boolean {
        return context.closure()
    }

    fun fullName(): String {
        var res = stateName
        if (parent != null) {
            res = parent.fullName() + "." + res
        }
        return res
    }

    override fun getContainer(): Expect? {
        return this
    }
}

interface ExpectContext {
    var body: MagicBotMessage?
        get

    fun goto(stateId: String)
    fun tryGoto(stateId: String): Boolean
    fun gotoParent(level: Int)
    fun gotoParent()
    fun log(text: String)
    fun sendText(text: String)
}

interface ExpectContainer {
    fun addChild(expect: Expect)
    fun getContainer(): Expect?
}

interface ExpectCommandContainer : ExpectContainer {

}

var ExpectContext.text: String
    get() {
        if (body is MagicBotTextMessage) {
            return (body as MagicBotTextMessage).text!!
        }
        throw RuntimeException()
    }
    private set(v) {

    }

var ExpectContext.isText: Boolean
    get() {
        if (body is MagicBotTextMessage) {
            return (body as MagicBotTextMessage).command == null
        }
        return false
    }
    private set(v) {

    }

var ExpectContext.isCommand: Boolean
    get() {
        if (body is MagicBotTextMessage) {
            return (body as MagicBotTextMessage).command != null
        }
        return false
    }
    private set(v) {

    }

var ExpectContext.command: String?
    get() {
        if (body is MagicBotTextMessage) {
            return (body as MagicBotTextMessage).command
        }
        return null
    }
    private set(v) {

    }

var ExpectContext.isCancel: Boolean
    get() {
        return isCommand && command == "cancel"
    }
    private set(v) {

    }

var ExpectContext.isDoc: Boolean
    get() {
        return body is MagicBotDocMessage
    }
    private set(v) {

    }

var ExpectContext.doc: BotMessages.DocumentMessage
    get() {
        if (body is MagicBotDocMessage) {
            return (body as MagicBotDocMessage).doc
        }
        throw RuntimeException()
    }
    private set(v) {

    }

var ExpectContext.isPhoto: Boolean
    get() {
        if (body is MagicBotDocMessage) {
            val doc = body as MagicBotDocMessage
            if (doc.doc.ext.isPresent && doc.doc.ext.get() is BotMessages.DocumentExPhoto) {
                return true
            }
        }
        return false
    }
    private set(v) {

    }

var ExpectContext.responseJson: JSONObject
    get() {
        if (body is MagicBotJsonMessage) {
            return (body as MagicBotJsonMessage).json
        }
        throw RuntimeException()
    }
    private set(v) {

    }

var ExpectContext.isJson: Boolean
    get() {
        return body is MagicBotJsonMessage
    }
    private set(v) {

    }