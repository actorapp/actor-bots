package im.actor.bots.framework.traits

import im.actor.bots.BotMessages
import im.actor.bots.framework.MagicForkScope
import java.util.*

/**
 * Trait for enabling administration control of Bot. Enables admins nicknames and checking if user is
 * bots's admin
 */
interface AdminTrait {
    var admins: MutableList<String>
    fun isAdmin(user: BotMessages.User?): Boolean
}

interface AdminTraitScoped : AdminTrait {
    fun isAdminScope(): Boolean
}

open class AdminTraitImpl : AdminTrait {

    override var admins: MutableList<String> = ArrayList<String>()

    override fun isAdmin(user: BotMessages.User?): Boolean {
        if (user == null) {
            return false
        }
        if (user.username.isPresent) {
            return admins.contains(user.username.get())
        } else {
            return false
        }
    }
}

class AdminTraitScopedImpl(val scope: MagicForkScope) : AdminTraitImpl(), AdminTraitScoped {

    override fun isAdminScope(): Boolean {

        if (scope.peer.isPrivate) {
            try {
                val usr = scope.bot.getUser(scope.peer.id) ?: return false
                return isAdmin(usr)
            } catch(e: Exception) {

            }
        }

        return false
    }

}