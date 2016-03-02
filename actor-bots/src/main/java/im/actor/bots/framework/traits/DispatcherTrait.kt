package im.actor.bots.framework.traits

import akka.actor.Actor
import akka.actor.Cancellable
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit


interface DispatchTrait {
    fun initDispatch(actor: Actor)
    fun schedule(message: Any, delay: Long): Cancellable
}

class DispatchTraitImpl : DispatchTrait {

    private var actor: Actor? = null

    override fun schedule(message: Any, delay: Long): Cancellable {
        return schedullerSchedule(message, delay)
    }

    override fun initDispatch(actor: Actor) {
        this.actor = actor
    }

    private fun schedullerSchedule(message: Any, delay: Long): Cancellable {
        return actor!!.context().system().scheduler().scheduleOnce(Duration.create(delay, TimeUnit.MILLISECONDS), {
            actor!!.self().tell(message, actor!!.self())
        }, actor!!.context().dispatcher())
    }

}