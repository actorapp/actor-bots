package im.actor.bots.framework.stateful

import im.actor.bots.framework.MagicBotTextMessage

class ExpectCommands(name: String, parent: Expect?) : Expect(name, parent), ExpectCommandContainer {

    private var unknownCommand: (ExpectContext.() -> Unit)? = null
    private var notACommand: (ExpectContext.() -> Unit)? = null

    override fun onReceived(context: ExpectContext) {

        if (context.body is MagicBotTextMessage) {
            val textMessage = context.body as MagicBotTextMessage
            if (textMessage.command != null) {
                if (child.containsKey("/" + textMessage.command!!)) {

                    context.goto("/" + textMessage.command!!)
                    return
                } else {

                    // Unknown command
                    if (unknownCommand != null) {
                        applyContext(context, unknownCommand!!)
                    } else {
                        context.tryGoto("default")
                    }

                    return
                }
            }
        }

        if (notACommand != null) {
            applyContext(context, notACommand!!)
        } else {
            context.tryGoto("default")
        }
    }

    override fun getContainer(): ExpectCommands {
        return this
    }
}

class ExpectCommand(name: String, parent: Expect?) : Expect(name, parent) {


    override fun onReceived(context: ExpectContext) {

    }
}

public fun ExpectCommandContainer.command(name: String, init: (ExpectCommand.() -> Unit)): ExpectCommand {
    val res = ExpectCommand(name, getContainer())
    addChild(res)
    res.init()
    return res
}

public fun ExpectContainer.oneShot(name: String, init: (ExpectContext.() -> Unit)): ExpectCommand {
    val res = ExpectCommand(name, getContainer())
    res.before {
        init()
        gotoParent()
    }
    addChild(res)
    return res
}

public fun ExpectCommandContainer.expectCommands(init: (ExpectCommands.() -> Unit)): ExpectCommands {
    return expectCommands("main", init)
}

public fun ExpectCommandContainer.expectCommands(name: String, init: (ExpectCommands.() -> Unit)): ExpectCommands {
    val res = ExpectCommands(name, getContainer())
    addChild(res)
    res.init()
    return res
}