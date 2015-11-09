package im.actor.bots

import im.actor.bots.framework.farm

fun main(args: Array<String>) {
    farm("bots") {
        //
        // Use separate farms for production and development for easier deployment
        //
        // bot(BotFatherBot::class) {
        //     name = "botfather"
        //     token = "<YOUR_TOKEN_HERE>"
        //     traceHook = "<OPTIONAL_TRACE_WEBHOOK>"
        // }
    }
}