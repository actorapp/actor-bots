package im.actor.bots.framework.traits

import im.actor.bots.BotMessages
import java.util.*

/**
 * Trait for enabling administration control of Bot. Enables admins nicknames and checking if user is
 * bot's admin
 */
interface AdminTrait {
    var admins: MutableList<String>
    fun isAdmin(user: BotMessages.User): Boolean
}

class AdminTraitImpl : AdminTrait {

    override var admins: MutableList<String> = ArrayList<String>()

    override fun isAdmin(user: BotMessages.User): Boolean {
        if (user.username.isPresent) {
            return admins.contains(user.username.get())
        } else {
            return false
        }
    }
}