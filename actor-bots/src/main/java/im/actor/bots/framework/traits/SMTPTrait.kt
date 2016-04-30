package im.actor.bots.framework.traits

import com.google.protobuf.StructOrBuilder
import org.codemonkey.simplejavamail.Email
import org.codemonkey.simplejavamail.Mailer
import org.codemonkey.simplejavamail.TransportStrategy
import java.util.*
import javax.mail.*

interface SMTPTrait {
    fun sendEmail(subject: String, text: String, html: String, from: String, fromEmail: String,
                  to: List<String>, cc: List<String> = ArrayList<String>(),
                  bcc: List<String> = ArrayList<String>())

    fun sendEmail(subject: String, text: String, html: String, from: String, fromEmail: String,
                  to: String)

    fun sendEmail(subject: String, text: String, from: String, fromEmail: String, to: String)
}

class SMTPTraitIml(val login: String, val password: String,
                   val smtpHost: String, val smtpPort: Int) : SMTPTrait {

    private var mailer: Mailer? = null

    override fun sendEmail(subject: String, text: String, from: String, fromEmail: String, to: String) {
        sendEmail(subject, text, text, from, fromEmail, to)
    }

    override fun sendEmail(subject: String, text: String, html: String, from: String, fromEmail: String, to: String) {
        var to2 = ArrayList<String>()
        to2.add(to)
        sendEmail(subject, text, html, from, fromEmail, to2)
    }

    override fun sendEmail(subject: String,
                           text: String, html: String,
                           from: String, fromEmail: String,
                           to: List<String>,
                           cc: List<String>,
                           bcc: List<String>) {

        val email = Email()
        email.setFromAddress(from, fromEmail)
        email.subject = subject
        email.text = text
        email.textHTML = html
        for (i in to) {
            email.addRecipient(i, i, Message.RecipientType.TO)
        }
        for (i in cc) {
            email.addRecipient(i, i, Message.RecipientType.CC)
        }
        for (i in bcc) {
            email.addRecipient(i, i, Message.RecipientType.BCC)
        }

        if (mailer == null) {
            mailer = Mailer(smtpHost, smtpPort, login, password, TransportStrategy.SMTP_TLS)
        }
        mailer!!.sendMail(email)
    }
}