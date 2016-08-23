package im.actor.bots

import im.actor.botkit.RemoteBot
import im.actor.bots.framework.farm

fun main(args: Array<String>) {

    // val endpoint = ""wss://api.your-actor.im""
    val endpoint = RemoteBot.DefaultEndpoint()

    farm("NewFarm", endpoint) {

        bot(EchoStatefulBot::class) {
            name = "BOT_NAME_HERE"
            token = "YOUR_TOKEN_HERE"
        }

    }
}