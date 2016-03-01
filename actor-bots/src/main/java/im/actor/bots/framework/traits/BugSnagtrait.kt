package im.actor.bots.framework.traits

var sharedBugSnagClient: com.bugsnag.Client? = null

interface BugSnag {
    fun logException(e: Throwable?)
}

class BugSnagImpl() : BugSnag {

    override fun logException(e: Throwable?) {
        if (e != null) {
            sharedBugSnagClient?.notify(e)
        }
    }
}