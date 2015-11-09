package im.actor.bots.framework.stateful

import im.actor.bots.framework.MagicBotMessage
import im.actor.bots.framework.MagicForkScope
import im.actor.bots.framework.i18n.Strings
import im.actor.bots.framework.persistence.MagicPersistentBot
import org.json.JSONObject

abstract class MagicStatefulBot(scope: MagicForkScope) : MagicPersistentBot(scope), ExpectContext, ExpectCommandContainer {

    var enablePersistent = false
    var currentBody: MagicBotMessage? = null
    var root: Expect? = null
    var currentState: Expect? = null

    override fun preStart() {
        configure()
        if (root == null) {
            throw RuntimeException("Root state not installed")
        }
        // Initial state will set in onRestoreState
        super.preStart()
        if (currentState == null) {
            currentState = root
        }
    }

    override fun onSaveState(state: JSONObject) {
        if (enablePersistent) {
            state.put("#state_id", currentState!!.fullName())
        }
    }

    override fun onRestoreState(state: JSONObject) {
        if (enablePersistent) {
            val id = state.optString("#state_id", root!!.fullName())
            log("Loaded name $id")
            val dest = findExpect(id, root!!)
            if (dest != null) {
                log("Found dest ${dest.fullName()}")
                currentState = dest
            } else {
                log("Unable to found")
                currentState = root
            }
        } else {
            currentState = root
        }

    }

    abstract fun configure()

    /**
     * Enabling Simple Mode. Useful for simple geeky bots that works only in private chats
     * and consist of just list of commands.
     */
    fun enableSimpleMode(hint: String, unknown: String? = null) {

        enableInGroups = false
        enablePersistent = true

        //
        // Enable error on unknown command
        //

        oneShot("default") {
            if (unknown != null) {
                sendText(localized(unknown))
            } else {
                sendText(Strings.unknown())
            }
        }

        //
        // Hint
        //

        oneShot("/start") {
            sendText(hint)
        }
    }

    override fun onMessage(message: MagicBotMessage) {
        currentBody = message
        currentState!!.onReceived(this)
    }

    // Context

    override var body: MagicBotMessage?
        get() {
            return currentBody
        }
        set(value) {
        }

    override fun goto(stateId: String) {
        val n = findExpect(stateId, currentState!!) ?: throw RuntimeException("Unable to find $stateId")
        currentState = n
        log("goto: ${currentState!!.fullName()}")
        n.onBefore(this)
    }

    override fun tryGoto(stateId: String): Boolean {
        val n = findExpect(stateId, currentState!!)
        if (n != null) {
            currentState = n
            log("tryGoto: ${currentState!!.fullName()}")
            n.onBefore(this)
            return true
        } else {
            return false
        }
    }

    private fun findExpect(stateId: String, start: Expect): Expect? {
        var parts = stateId.split(".")
        // Finding with starting included
        return findExpectUp(parts, start)
        //        if (res != null) {
        //            return res
        //        }
        ////        // Finding in all children
        ////        for (c in start.child.values) {
        ////            val res = findExpectFrom(parts, c)
        ////            if (res != null) {
        ////                return res
        ////            }
        ////        }
        ////
        ////        // Finding in root
        ////        val resRoot = findExpectFrom(parts, root!!)
        ////        if (resRoot != null) {
        ////            return resRoot
        ////        }
        ////
        ////        // Finding in root children
        ////        for (c in root!!.child.values) {
        ////            val res = findExpectFrom(parts, c)
        ////            if (res != null) {
        ////                return res
        ////            }
        ////        }
        //
        //        // Nothing found
        //        return null
    }

    private fun findExpectUp(stateIds: List<String>, start: Expect): Expect? {
        val res = findExpectFrom(stateIds, start)
        if (res != null) {
            return res
        }
        if (start.parent != null) {
            return findExpectUp(stateIds, start.parent)
        } else {
            return null
        }
    }

    private fun findExpectFrom(stateIds: List<String>, start: Expect): Expect? {
        if (stateIds.count() == 0) {
            return start
        }
        val id = stateIds[0]
        if (start.stateName == id) {
            if (stateIds.count() == 1) {
                return start
            }
            for (i in start.child.values) {
                val res = findExpectFrom(stateIds.drop(1).toList(), i)
                if (res != null) {
                    return res
                }
            }
        }
        for (i in start.child.values) {
            if (i.stateName == id) {
                return findExpectFrom(stateIds.drop(1).toList(), i)
            }
        }
        //        if (start.stateName == id) {
        //            if (stateIds.count() == 1) {
        //                return start
        //            }
        //            for (i in start.child.values) {
        //                val res = findExpectFrom(stateIds.drop(1).toList(), i)
        //                if (res != null) {
        //                    return res
        //                }
        //            }
        //        }

        return null
    }

    override fun gotoParent() {
        if (currentState != null && currentState!!.parent != null) {
            currentState = currentState!!.parent!!
            log("GotoParent: ${currentState!!.fullName()}")
            currentState!!.onBefore(this)
        } else {
            throw RuntimeException("${currentState!!.fullName()} doesn't have parent")
        }
    }

    override fun gotoParent(level: Int) {

    }

    override fun log(text: String) {
        v(text)
    }

    // Container

    override fun addChild(expect: Expect) {
        getContainer().addChild(expect)
    }

    override fun getContainer(): ExpectCommands {
        if (root != null) {
            if (root !is ExpectCommands) {
                throw RuntimeException("Root is not commands container")
            }
        } else {
            root = ExpectCommands("main", null)
        }
        return root as ExpectCommands
    }
}