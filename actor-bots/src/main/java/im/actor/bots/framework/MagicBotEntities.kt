package im.actor.bots.framework

import im.actor.bots.BotMessages
import org.json.JSONObject

//
// Magic Bot Messages
//

public abstract class MagicBotMessage(val peer: OutPeer, val sender: BotMessages.UserOutPeer?,
                                      val rid: Long) {

}

public class MagicBotTextMessage(peer: OutPeer, sender: BotMessages.UserOutPeer?, rid: Long,
                                 val text: String) : MagicBotMessage(peer, sender, rid) {
    var command: String? = null
    var commandArgs: String? = null
}

public class MagicBotJsonMessage(peer: OutPeer, sender: BotMessages.UserOutPeer?, rid: Long,
                                 val json: JSONObject) : MagicBotMessage(peer, sender, rid) {

}

public class MagicBotDocMessage(peer: OutPeer, sender: BotMessages.UserOutPeer?, rid: Long,
                                val doc: BotMessages.DocumentMessage) : MagicBotMessage(peer, sender, rid) {

}

public class MagicBotStickerMessage(peer: OutPeer, sender: BotMessages.UserOutPeer?, rid: Long,
                                    val sticker: BotMessages.StickerMessage) : MagicBotMessage(peer, sender, rid) {

}

//
// User Extensions
//

var BotMessages.User.isEnterprise: Boolean
    get() {
        return this.emailContactRecords.size > 0
    }
    private set(v) {

    }

//
// Peers and OutPeers
//

public fun peerFromJson(json: JSONObject): Peer {
    val type = json.getString("type")
    when (type) {
        "group" -> {
            return Peer(PeerType.GROUP, json.getInt("id"))
        }
        "private" -> {
            return Peer(PeerType.PRIVATE, json.getInt("id"))
        }
        else -> {
            throw RuntimeException("Unknown type $type")
        }
    }
}

public class Peer(val type: PeerType, val id: Int) {

    var isGroup: Boolean
        get() {
            return type == PeerType.GROUP
        }
        private set(v) {
        }

    var isPrivate: Boolean
        get() {
            return type == PeerType.PRIVATE
        }
        private set(v) {
        }

    fun toJson(): JSONObject {
        val res = JSONObject()
        res.put("id", id)
        when (type) {
            PeerType.GROUP -> {
                res.put("type", "group")
            }
            PeerType.PRIVATE -> {
                res.put("type", "private")
            }
        }
        return res
    }

    fun toKit(): BotMessages.Peer {
        when (type) {
            PeerType.PRIVATE -> {
                return BotMessages.UserPeer(id)
            }
            PeerType.GROUP -> {
                return BotMessages.GroupPeer(id)
            }
        }
    }

    fun toUniqueId(): String {
        when (type) {
            PeerType.PRIVATE -> {
                return "PRIVATE_$id"
            }
            PeerType.GROUP -> {
                return "GROUP_$id"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Peer

        if (type != other.type) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result += 31 * result + id
        return result
    }
}

public fun outPeerFromJson(json: JSONObject): OutPeer {
    val type = json.getString("type")
    when (type) {
        "group" -> {
            return OutPeer(PeerType.GROUP, json.getInt("id"), json.getString("accessHash").toLong())
        }
        "private" -> {
            return OutPeer(PeerType.PRIVATE, json.getInt("id"), json.getString("accessHash").toLong())
        }
        else -> {
            throw RuntimeException("Unknown type $type")
        }
    }
}

public fun BotMessages.OutPeer.toUsable(): OutPeer {
    if (this is BotMessages.UserOutPeer) {
        return OutPeer(PeerType.PRIVATE, id(), accessHash())
    } else if (this is BotMessages.GroupOutPeer) {
        return OutPeer(PeerType.GROUP, id(), accessHash())
    } else {
        throw RuntimeException("Unknown type")
    }
}

public class OutPeer(val type: PeerType, val id: Int, val accessHash: Long) {

    var isGroup: Boolean
        get() {
            return type == PeerType.GROUP
        }
        private set(v) {
        }

    var isPrivate: Boolean
        get() {
            return type == PeerType.PRIVATE
        }
        private set(v) {
        }

    fun toJson(): JSONObject {
        val res = JSONObject()
        res.put("id", id)
        res.put("accessHash", "$accessHash")
        when (type) {
            PeerType.GROUP -> {
                res.put("type", "group")
            }
            PeerType.PRIVATE -> {
                res.put("type", "private")
            }
        }
        return res
    }

    fun toPeer(): Peer {
        return Peer(type, id)
    }

    fun toKit(): BotMessages.OutPeer {
        when (type) {
            PeerType.PRIVATE -> {
                return BotMessages.UserOutPeer(id, accessHash)
            }
            PeerType.GROUP -> {
                return BotMessages.GroupOutPeer(id, accessHash)
            }
        }
    }

    fun toUniqueId(): String {
        when (type) {
            PeerType.PRIVATE -> {
                return "PRIVATE_$id"
            }
            PeerType.GROUP -> {
                return "GROUP_$id"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as OutPeer

        if (type != other.type) return false
        if (id != other.id) return false
        if (accessHash != other.accessHash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result += 31 * result + id
        result += 31 * result + accessHash.hashCode()
        return result
    }
}

public enum class PeerType(val id: Int) {
    PRIVATE(0), GROUP(1)
}


public fun BotMessages.Peer.toUsable(): Peer {
    if (this is BotMessages.UserPeer) {
        return Peer(PeerType.PRIVATE, id())
    } else if (this is BotMessages.GroupPeer) {
        return Peer(PeerType.GROUP, id())
    } else {
        throw RuntimeException("Unknown type")
    }
}
