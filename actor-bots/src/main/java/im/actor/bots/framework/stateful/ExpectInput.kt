package im.actor.bots.framework.stateful

open class ExpectInput(stateName: String, parent: Expect?) : Expect(stateName, parent) {

    protected var receivedClosure: (ExpectContext.() -> Unit)? = null

    fun received(receive: (ExpectContext.() -> Unit)?) {
        receivedClosure = receive
    }

    override fun onReceived(context: ExpectContext) {
        if (receivedClosure != null) {
            applyContext(context, receivedClosure!!)
        }
    }
}

open class ExpectValidatedInput(stateName: String, parent: Expect?) : ExpectInput(stateName, parent) {

    private var validateClosure: (ExpectContext.() -> Boolean)? = null

    fun validate(validate: (ExpectContext.() -> Boolean)?) {
        validateClosure = validate
    }

    override fun onReceived(context: ExpectContext) {
        if (applyContext(context, validateClosure!!)) {
            if (receivedClosure != null) {
                applyContext(context, receivedClosure!!)
            }
        }
    }
}

fun ExpectContainer.expectInput(name: String, init: ((ExpectValidatedInput.() -> Unit))): ExpectValidatedInput {
    val res = ExpectValidatedInput(name, getContainer())
    addChild(res)
    res.init()
    return res
}