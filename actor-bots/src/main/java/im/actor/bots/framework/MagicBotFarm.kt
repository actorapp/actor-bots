package im.actor.bots.framework

import akka.actor.ActorSystem
import akka.actor.Props
import im.actor.botkit.RemoteBot
import java.util.*
import kotlin.reflect.KClass

class BotFarm(val name: String) {

    var endpoint = RemoteBot.DefaultEndpoint()
    val system = ActorSystem.create(name)
    val bots = ArrayList<BotDescription>()

    init {

    }

    fun <T : MagicBotFork> bot(clazz: KClass<T>, init: BotDescription.() -> Unit) {
        val b = BotDescription(clazz as KClass<MagicBotFork>)
        b.init()
        bots.add(b)
    }

    fun startFarm() {

        for (b in bots) {
            var config = MagicBotConfig(b.name!!, b.clazz.java, b.overlordClazz, b.token!!,
                    endpoint, b.traceHook)
            system.actorOf(Props.create(MagicalRemoteBot::class.java, config), b.name)
        }

        system.awaitTermination()
    }
}

class BotDescription(val clazz: KClass<MagicBotFork>) {
    var name: String? = null
    var token: String? = null
    var traceHook: String? = null
    var overlordClazz: Class<*>? = null
}

public fun farm(name: String, init: BotFarm.() -> Unit) {
    val res = BotFarm(name)
    res.init()
    res.startFarm()
}