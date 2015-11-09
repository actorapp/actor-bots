package im.actor.bots.framework.stateful

class ExpectRaw(stateName: String, parent: Expect?) : Expect(stateName, parent) {

    private var receiveClosure: (ExpectContext.() -> Unit)? = null

    fun received(receive: (ExpectContext.() -> Unit)?) {
        receiveClosure = receive
    }

    override fun onReceived(context: ExpectContext) {
        if (receiveClosure != null) {
            applyContext(context, receiveClosure!!)
        }
    }
}

fun ExpectContainer.raw(name: String, init: ((ExpectRaw.() -> Unit))): ExpectRaw {
    val res = ExpectRaw(name, getContainer())
    addChild(res)
    res.init()
    return res
}

fun ExpectContainer.static(name: String, init: ((ExpectContext.() -> Unit))): ExpectRaw {
    val res = ExpectRaw(name, getContainer())
    addChild(res)
    res.before {
        init()
    }
    return res
}