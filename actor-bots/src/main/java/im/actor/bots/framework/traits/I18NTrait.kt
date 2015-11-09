package im.actor.bots.framework.traits

import im.actor.bots.framework.i18n.I18NEngine

interface I18NTrait {
    var language: String
    fun pickLocale(supported: Array<String>, usersLocales: Array<String>): String
    fun initLocalize(fileName: String)
    fun localized(key: String): String
}

fun I18NTrait.initLocalize(name: String, supported: Array<String>, usersLocales: Array<String>) {
    val locale = pickLocale(supported, usersLocales)
    if (locale == "en") {
        language = "en"
        initLocalize("$name.properties")
    } else {
        language = locale
        initLocalize("${name}_${locale.capitalize()}.properties")
    }
}

class I18NTraitImpl : I18NTrait {

    override var language: String = "en"

    private var i18n: I18NEngine? = null

    override fun pickLocale(supported: Array<String>, usersLocales: Array<String>): String {
        for (ul in usersLocales) {

            // TODO: Implement country checking
            val uLang = ul.substring(0, 2)
            for (s in supported) {
                if (s.substring(0, 2).toLowerCase() == uLang) {
                    return s.toLowerCase()
                }
            }
        }
        return supported[0]
    }

    override fun initLocalize(fileName: String) {
        i18n = I18NEngine(fileName)
    }

    override fun localized(key: String): String {
        return i18n!!.pick(key)
    }
}