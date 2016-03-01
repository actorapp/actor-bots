package im.actor.bots

import im.actor.bots.framework.farm
import im.actor.bots.framework.traits.sharedBugSnagClient

fun main(args: Array<String>) {


    farm("NewFarm") {


        bot(EchoStatefulBot::class) {
            name = "BOT_NAME_HERE"
            token = "YOUR_TOKEN_HERE"
        }

    }
}