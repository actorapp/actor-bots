package im.actor.bots.framework.traits

import akka.actor.Actor
import akka.event.DiagnosticLoggingAdapter
import akka.event.Logging

/**
 * Logging Trait
 */
interface LogTrait {
    fun initLog(root: Actor)
    fun d(msg: String)
    fun v(msg: String)
}

class LogTraitImpl() : LogTrait {

    private var LOG: DiagnosticLoggingAdapter? = null

    override fun initLog(root: Actor) {
        LOG = Logging.apply(root)
    }


    override fun v(msg: String) {
        LOG!!.info(msg)
    }

    override fun d(msg: String) {
        LOG!!.debug(msg)
    }

}