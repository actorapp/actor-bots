package im.actor.bots.framework.persistence

import im.actor.bots.framework.MagicBotFork
import im.actor.bots.framework.MagicForkScope
import org.json.JSONObject
import shardakka.ShardakkaExtension
import shardakka.keyvalue.SimpleKeyValueJava

abstract class MagicPersistentBot(scope: MagicForkScope) : MagicBotFork(scope) {

    private val stateKeyValue: SimpleKeyValueJava<String> =
            ShardakkaExtension.get(context().system()).simpleKeyValue("\$${scope.name}_state_" + scope.peer.toUniqueId()).asJava()

    override fun preStart() {
        super.preStart()

        val res = stateKeyValue.get("actor_state")
        if (res != null) {
            val state = JSONObject(res)
            val internalState = state.getJSONObject("state")
            onRestoreState(internalState)
        }
    }

    open fun onRestoreState(state: JSONObject) {

    }

    open fun onSaveState(state: JSONObject) {

    }

    override fun afterMessage() {
        saveState()
    }

    fun saveState() {
        val res = JSONObject()
        val internalState = JSONObject()
        onSaveState(internalState)
        res.put("state", internalState)
        stateKeyValue.syncUpsert("actor_state", res.toString())
    }
}