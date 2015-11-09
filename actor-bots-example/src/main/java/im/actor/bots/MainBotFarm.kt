package im.actor.bots

import im.actor.bots.framework.farm

fun main(args: Array<String>) {
    farm("BotFarm") {

        //
        // Example of running Bot Father. You need to create bot from actor-cli and grant admin
        // permissions
        //
        // bot(BotFatherBot::class) {
        //     name = "botfather"
        //     token = "<YOUR_TOKEN_HERE>"
        //     traceHook = "<OPTIONAL_TRACE_WEBHOOK>"
        // }
    }
}