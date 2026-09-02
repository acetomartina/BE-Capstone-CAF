package com.martina.caf_fapi.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    private static final String OGGETTO_RESET_PASSWORD =
            "Reimposta la password della tua area personale";

    private static final String OGGETTO_ATTIVAZIONE_ACCOUNT =
            "Attiva la tua Area Cliente CAF FAPI";

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String urlFrontend;

    @Value("${app.reset-password.mittente}")
    private String mittente;

    @Value("${app.reset-password.durata-minuti}")
    private int durataResetPasswordMinuti;

    @Value("${app.account-activation.durata-minuti}")
    private int durataAttivazioneMinuti;

    @Override
    public void inviaLinkRecuperoPassword(
            String destinatario,
            String token
    ) {
        String link =
                costruisciLinkResetPassword(
                        token
                );

        MimeMessage messaggio =
                mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            messaggio,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(mittente);
            helper.setTo(destinatario);
            helper.setSubject(
                    OGGETTO_RESET_PASSWORD
            );

            helper.setText(
                    corpoResetPassword(link),
                    corpoResetPasswordHtml(link)
            );

            mailSender.send(messaggio);

            LOGGER.info(
                    "Inviata una mail HTML di recupero password."
            );
        } catch (MessagingException exception) {
            LOGGER.error(
                    "Impossibile preparare la mail di recupero password."
            );

            throw new MailSendException(
                    "Impossibile preparare la mail di recupero password.",
                    exception
            );
        }
    }

    private String corpoResetPasswordHtml(
            String link
    ) {
        String linkSicuro =
                HtmlUtils.htmlEscape(link);

        String durataSicura =
                HtmlUtils.htmlEscape(
                        descriviDurata(
                                durataResetPasswordMinuti
                        )
                );

        return """
            <!doctype html>
            <html lang="it">
            <head>
                <meta charset="UTF-8">
                <meta
                    name="viewport"
                    content="width=device-width, initial-scale=1.0"
                >
                <title>Reimposta la password</title>
            </head>

            <body style="
                margin: 0;
                padding: 0;
                background-color: #f3f6fa;
                color: #344054;
                font-family: Arial, Helvetica, sans-serif;
            ">
                <div style="
                    display: none;
                    max-height: 0;
                    overflow: hidden;
                    opacity: 0;
                    color: transparent;
                ">
                    Usa il link sicuro per scegliere una nuova password.
                </div>

                <table
                    role="presentation"
                    width="100%%"
                    cellspacing="0"
                    cellpadding="0"
                    border="0"
                    style="background-color: #f3f6fa;"
                >
                    <tr>
                        <td
                            align="center"
                            style="padding: 36px 16px;"
                        >
                            <table
                                role="presentation"
                                width="100%%"
                                cellspacing="0"
                                cellpadding="0"
                                border="0"
                                style="
                                    width: 100%%;
                                    max-width: 620px;
                                    overflow: hidden;
                                    border: 1px solid #dfe6ef;
                                    border-radius: 20px;
                                    background-color: #ffffff;
                                    box-shadow:
                                        0 18px 45px
                                        rgba(16, 44, 101, 0.10);
                                "
                            >
                                <tr>
                                    <td style="
                                        height: 6px;
                                        background-color: #c71468;
                                        font-size: 0;
                                        line-height: 0;
                                    ">
                                        &nbsp;
                                    </td>
                                </tr>

                                <tr>
                                    <td style="
                                        padding: 26px 38px;
                                        background-color: #102c65;
                                    ">
                                        <table
                                            role="presentation"
                                            width="100%%"
                                            cellspacing="0"
                                            cellpadding="0"
                                            border="0"
                                        >
                                            <tr>
                                                <td
                                                    valign="middle"
                                                    style="width: 54px;"
                                                >
                                                    <div style="
                                                        width: 46px;
                                                        height: 46px;
                                                        border-radius: 13px;
                                                        background-color: #ffffff;
                                                        color: #247a3a;
                                                        font-size: 17px;
                                                        font-weight: 800;
                                                        line-height: 46px;
                                                        text-align: center;
                                                    ">
                                                        CF
                                                    </div>
                                                </td>

                                                <td
                                                    valign="middle"
                                                    style="padding-left: 13px;"
                                                >
                                                    <div style="
                                                        color: #ffffff;
                                                        font-size: 19px;
                                                        font-weight: 800;
                                                        line-height: 1.2;
                                                    ">
                                                        CAF FAPI Pianopoli
                                                    </div>

                                                    <div style="
                                                        margin-top: 4px;
                                                        color: #cbd8ee;
                                                        font-size: 12px;
                                                        line-height: 1.4;
                                                    ">
                                                        La tua area personale
                                                        protetta
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="
                                        padding: 38px 38px 18px;
                                    ">
                                        <div style="
                                            margin-bottom: 12px;
                                            color: #c71468;
                                            font-size: 12px;
                                            font-weight: 800;
                                            letter-spacing: 1.1px;
                                            text-transform: uppercase;
                                        ">
                                            Sicurezza account
                                        </div>

                                        <h1 style="
                                            margin: 0;
                                            color: #102c65;
                                            font-size: 29px;
                                            font-weight: 800;
                                            line-height: 1.15;
                                            letter-spacing: -0.6px;
                                        ">
                                            Scegli una nuova password
                                        </h1>

                                        <p style="
                                            margin: 22px 0 0;
                                            color: #344054;
                                            font-size: 16px;
                                            line-height: 1.65;
                                        ">
                                            Ciao,
                                        </p>

                                        <p style="
                                            margin: 10px 0 0;
                                            color: #667085;
                                            font-size: 15px;
                                            line-height: 1.65;
                                        ">
                                            Abbiamo ricevuto una richiesta
                                            per reimpostare la password della
                                            tua Area Cliente CAF FAPI.
                                        </p>
                                    </td>
                                </tr>

                                <tr>
                                    <td
                                        align="center"
                                        style="padding: 16px 38px 22px;"
                                    >
                                        <a
                                            href="%s"
                                            target="_blank"
                                            style="
                                                display: inline-block;
                                                padding: 15px 28px;
                                                border-radius: 10px;
                                                background-color: #3f62a5;
                                                color: #ffffff;
                                                font-size: 15px;
                                                font-weight: 800;
                                                line-height: 1;
                                                text-decoration: none;
                                                box-shadow:
                                                    0 9px 20px
                                                    rgba(63, 98, 165, 0.22);
                                            "
                                        >
                                            Reimposta la password
                                        </a>

                                        <p style="
                                            margin: 16px 0 0;
                                            color: #667085;
                                            font-size: 12px;
                                            line-height: 1.5;
                                        ">
                                            Il link è valido per
                                            <strong style="color: #344054;">
                                                %s
                                            </strong>
                                            e può essere utilizzato
                                            una sola volta.
                                        </p>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 0 38px 16px;">
                                        <table
                                            role="presentation"
                                            width="100%%"
                                            cellspacing="0"
                                            cellpadding="0"
                                            border="0"
                                            style="
                                                border-radius: 12px;
                                                background-color: #fff0d8;
                                            "
                                        >
                                            <tr>
                                                <td style="
                                                    padding: 16px 17px;
                                                ">
                                                    <div style="
                                                        color: #9a5708;
                                                        font-size: 12px;
                                                        font-weight: 800;
                                                    ">
                                                        Non sei stata tu?
                                                    </div>

                                                    <div style="
                                                        margin-top: 6px;
                                                        color: #6f5a3f;
                                                        font-size: 12px;
                                                        line-height: 1.55;
                                                    ">
                                                        Puoi ignorare questa
                                                        email. La password
                                                        attuale resterà valida
                                                        e non verrà modificata.
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 0 38px 30px;">
                                        <table
                                            role="presentation"
                                            width="100%%"
                                            cellspacing="0"
                                            cellpadding="0"
                                            border="0"
                                            style="
                                                border: 1px solid #dfe6ef;
                                                border-radius: 12px;
                                                background-color: #f7f9fc;
                                            "
                                        >
                                            <tr>
                                                <td style="
                                                    padding: 15px 17px;
                                                ">
                                                    <div style="
                                                        color: #102c65;
                                                        font-size: 12px;
                                                        font-weight: 800;
                                                    ">
                                                        Il pulsante non funziona?
                                                    </div>

                                                    <div style="
                                                        margin-top: 6px;
                                                        color: #667085;
                                                        font-size: 11px;
                                                        line-height: 1.5;
                                                    ">
                                                        Copia e incolla questo
                                                        indirizzo nel browser:
                                                    </div>

                                                    <div style="
                                                        margin-top: 7px;
                                                        overflow-wrap: anywhere;
                                                        word-break: break-all;
                                                        color: #3f62a5;
                                                        font-size: 11px;
                                                        line-height: 1.55;
                                                    ">
                                                        %s
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="
                                        padding: 22px 38px;
                                        border-top: 1px solid #e6ebf2;
                                        background-color: #f9fbfd;
                                    ">
                                        <p style="
                                            margin: 0;
                                            color: #667085;
                                            font-size: 11px;
                                            line-height: 1.6;
                                        ">
                                            Per sicurezza, non condividere
                                            questa email o il link con altre
                                            persone.
                                        </p>

                                        <p style="
                                            margin: 10px 0 0;
                                            color: #102c65;
                                            font-size: 12px;
                                            font-weight: 800;
                                            line-height: 1.6;
                                        ">
                                            CAF FAPI Pianopoli
                                            &nbsp;·&nbsp;
                                            377 960 9155
                                        </p>
                                    </td>
                                </tr>
                            </table>

                            <p style="
                                max-width: 620px;
                                margin: 16px auto 0;
                                color: #98a2b3;
                                font-size: 10px;
                                line-height: 1.5;
                                text-align: center;
                            ">
                                Messaggio automatico:
                                non rispondere direttamente a questa email.
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                linkSicuro,
                durataSicura,
                linkSicuro
        );
    }

    @Override
    public void inviaInvitoAttivazioneAccount(
            String destinatario,
            String nome,
            String token
    ) {
        String link =
                costruisciLinkAttivazioneAccount(
                        token
                );

        MimeMessage messaggio =
                mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            messaggio,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(mittente);
            helper.setTo(destinatario);
            helper.setSubject(
                    OGGETTO_ATTIVAZIONE_ACCOUNT
            );

            /*
             * Inviamo entrambe le versioni:
             * - testo semplice per i client meno moderni;
             * - HTML per la versione grafica.
             */
            helper.setText(
                    corpoAttivazioneAccountTestuale(
                            nome,
                            link
                    ),
                    corpoAttivazioneAccountHtml(
                            nome,
                            link
                    )
            );

            mailSender.send(messaggio);

            LOGGER.info(
                    "Inviata una mail HTML di attivazione account."
            );
        } catch (MessagingException exception) {
            LOGGER.error(
                    "Impossibile preparare la mail di attivazione account."
            );

            throw new MailSendException(
                    "Impossibile preparare la mail di attivazione account.",
                    exception
            );
        }
    }

    private String costruisciLinkResetPassword(
            String token
    ) {
        return urlFrontendNormalizzato()
                + "/reset-password/"
                + token;
    }

    private String costruisciLinkAttivazioneAccount(
            String token
    ) {
        return urlFrontendNormalizzato()
                + "/attiva-account/"
                + token;
    }

    private String urlFrontendNormalizzato() {
        return urlFrontend.endsWith("/")
                ? urlFrontend.substring(
                0,
                urlFrontend.length() - 1
        )
                : urlFrontend;
    }

    private String corpoResetPassword(
            String link
    ) {
        return """
                Ciao,

                abbiamo ricevuto una richiesta di reimpostazione della password
                per la tua area personale del CAF FAPI Pianopoli.

                Apri questo link per scegliere una nuova password:

                %s

                Il link è valido per %s e può essere usato una sola volta.

                Se non hai richiesto tu il cambio password puoi ignorare questo
                messaggio: la tua password attuale resta valida.

                CAF FAPI Pianopoli
                377 960 9155
                """.formatted(
                link,
                descriviDurata(
                        durataResetPasswordMinuti
                )
        );
    }

    private String corpoAttivazioneAccountTestuale(
            String nome,
            String link
    ) {
        return """
                %s

                Benvenuta nella tua Area Cliente CAF FAPI Pianopoli.

                Il tuo spazio personale è pronto. Potrai seguire le tue
                pratiche, controllare le scadenze e verificare i documenti
                richiesti dal CAF.

                Attiva il tuo account e scegli la password aprendo questo link:

                %s

                Il link è valido per %s e può essere utilizzato una sola volta.

                Se non riconosci questa richiesta, non utilizzare il link
                e contatta il CAF FAPI Pianopoli.

                CAF FAPI Pianopoli
                377 960 9155
                """.formatted(
                costruisciSaluto(nome),
                link,
                descriviDurata(
                        durataAttivazioneMinuti
                )
        );
    }

    private String corpoAttivazioneAccountHtml(
            String nome,
            String link
    ) {
        String salutoSicuro =
                HtmlUtils.htmlEscape(
                        costruisciSaluto(nome)
                );

        String linkSicuro =
                HtmlUtils.htmlEscape(link);

        String durataSicura =
                HtmlUtils.htmlEscape(
                        descriviDurata(
                                durataAttivazioneMinuti
                        )
                );

        return """
                <!doctype html>
                <html lang="it">
                <head>
                    <meta charset="UTF-8">
                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >
                    <title>Attiva la tua Area Cliente CAF FAPI</title>
                </head>

                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #f3f6fa;
                    color: #344054;
                    font-family: Arial, Helvetica, sans-serif;
                ">
                    <div style="
                        display: none;
                        max-height: 0;
                        overflow: hidden;
                        opacity: 0;
                        color: transparent;
                    ">
                        La tua Area Cliente CAF FAPI Pianopoli è pronta.
                    </div>

                    <table
                        role="presentation"
                        width="100%%"
                        cellspacing="0"
                        cellpadding="0"
                        border="0"
                        style="background-color: #f3f6fa;"
                    >
                        <tr>
                            <td
                                align="center"
                                style="padding: 36px 16px;"
                            >
                                <table
                                    role="presentation"
                                    width="100%%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                    style="
                                        width: 100%%;
                                        max-width: 620px;
                                        overflow: hidden;
                                        border: 1px solid #dfe6ef;
                                        border-radius: 20px;
                                        background-color: #ffffff;
                                        box-shadow:
                                            0 18px 45px
                                            rgba(16, 44, 101, 0.10);
                                    "
                                >
                                    <tr>
                                        <td style="
                                            height: 6px;
                                            background: linear-gradient(
                                                90deg,
                                                #247a3a 0%%,
                                                #247a3a 68%%,
                                                #c71468 68%%,
                                                #c71468 100%%
                                            );
                                            font-size: 0;
                                            line-height: 0;
                                        ">
                                            &nbsp;
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="
                                            padding: 26px 38px;
                                            background-color: #102c65;
                                        ">
                                            <table
                                                role="presentation"
                                                width="100%%"
                                                cellspacing="0"
                                                cellpadding="0"
                                                border="0"
                                            >
                                                <tr>
                                                    <td
                                                        valign="middle"
                                                        style="width: 54px;"
                                                    >
                                                        <div style="
                                                            width: 46px;
                                                            height: 46px;
                                                            border-radius: 13px;
                                                            background-color: #ffffff;
                                                            color: #247a3a;
                                                            font-size: 17px;
                                                            font-weight: 800;
                                                            line-height: 46px;
                                                            text-align: center;
                                                        ">
                                                            CF
                                                        </div>
                                                    </td>

                                                    <td
                                                        valign="middle"
                                                        style="padding-left: 13px;"
                                                    >
                                                        <div style="
                                                            color: #ffffff;
                                                            font-size: 19px;
                                                            font-weight: 800;
                                                            line-height: 1.2;
                                                        ">
                                                            CAF FAPI Pianopoli
                                                        </div>

                                                        <div style="
                                                            margin-top: 4px;
                                                            color: #cbd8ee;
                                                            font-size: 12px;
                                                            line-height: 1.4;
                                                        ">
                                                            La tua assistenza,
                                                            anche online
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding: 38px 38px 16px;">
                                            <div style="
                                                margin-bottom: 12px;
                                                color: #247a3a;
                                                font-size: 12px;
                                                font-weight: 800;
                                                letter-spacing: 1.1px;
                                                text-transform: uppercase;
                                            ">
                                                Il tuo spazio personale
                                            </div>

                                            <h1 style="
                                                margin: 0;
                                                color: #102c65;
                                                font-size: 29px;
                                                font-weight: 800;
                                                line-height: 1.15;
                                                letter-spacing: -0.6px;
                                            ">
                                                La tua Area Cliente è pronta
                                            </h1>

                                            <p style="
                                                margin: 22px 0 0;
                                                color: #344054;
                                                font-size: 16px;
                                                line-height: 1.65;
                                            ">
                                                %s
                                            </p>

                                            <p style="
                                                margin: 12px 0 0;
                                                color: #667085;
                                                font-size: 15px;
                                                line-height: 1.65;
                                            ">
                                                Abbiamo creato per te uno spazio
                                                riservato, semplice e sicuro,
                                                dal quale potrai seguire le tue
                                                pratiche e restare aggiornata.
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding: 10px 38px 8px;">
                                            <table
                                                role="presentation"
                                                width="100%%"
                                                cellspacing="0"
                                                cellpadding="0"
                                                border="0"
                                                style="
                                                    border: 1px solid #dfe6ef;
                                                    border-radius: 14px;
                                                    background-color: #f7f9fc;
                                                "
                                            >
                                                <tr>
                                                    <td style="padding: 20px;">
                                                        <div style="
                                                            color: #102c65;
                                                            font-size: 14px;
                                                            font-weight: 800;
                                                        ">
                                                            Nella tua area potrai:
                                                        </div>

                                                        <div style="
                                                            margin-top: 13px;
                                                            color: #667085;
                                                            font-size: 14px;
                                                            line-height: 1.9;
                                                        ">
                                                            <span style="color: #247a3a;">●</span>
                                                            &nbsp;Seguire lo stato delle pratiche<br>

                                                            <span style="color: #3f62a5;">●</span>
                                                            &nbsp;Controllare documenti e richieste<br>

                                                            <span style="color: #c71468;">●</span>
                                                            &nbsp;Tenere d’occhio le scadenze
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td
                                            align="center"
                                            style="padding: 26px 38px 20px;"
                                        >
                                            <a
                                                href="%s"
                                                target="_blank"
                                                style="
                                                    display: inline-block;
                                                    padding: 15px 28px;
                                                    border-radius: 10px;
                                                    background-color: #247a3a;
                                                    color: #ffffff;
                                                    font-size: 15px;
                                                    font-weight: 800;
                                                    line-height: 1;
                                                    text-decoration: none;
                                                    box-shadow:
                                                        0 9px 20px
                                                        rgba(36, 122, 58, 0.22);
                                                "
                                            >
                                                Attiva la mia Area Cliente
                                            </a>

                                            <p style="
                                                margin: 16px 0 0;
                                                color: #667085;
                                                font-size: 12px;
                                                line-height: 1.5;
                                            ">
                                                Il link è valido per
                                                <strong style="color: #344054;">
                                                    %s
                                                </strong>
                                                e può essere usato una sola volta.
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding: 0 38px 30px;">
                                            <table
                                                role="presentation"
                                                width="100%%"
                                                cellspacing="0"
                                                cellpadding="0"
                                                border="0"
                                                style="
                                                    border-radius: 12px;
                                                    background-color: #e6f4e9;
                                                "
                                            >
                                                <tr>
                                                    <td style="padding: 15px 17px;">
                                                        <div style="
                                                            color: #247a3a;
                                                            font-size: 12px;
                                                            font-weight: 800;
                                                        ">
                                                            Il pulsante non funziona?
                                                        </div>

                                                        <div style="
                                                            margin-top: 6px;
                                                            color: #4f5f57;
                                                            font-size: 11px;
                                                            line-height: 1.5;
                                                        ">
                                                            Copia e incolla questo
                                                            indirizzo nel browser:
                                                        </div>

                                                        <div style="
                                                            margin-top: 7px;
                                                            overflow-wrap: anywhere;
                                                            word-break: break-all;
                                                            color: #3f62a5;
                                                            font-size: 11px;
                                                            line-height: 1.55;
                                                        ">
                                                            %s
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="
                                            padding: 22px 38px;
                                            border-top: 1px solid #e6ebf2;
                                            background-color: #f9fbfd;
                                        ">
                                            <p style="
                                                margin: 0;
                                                color: #667085;
                                                font-size: 11px;
                                                line-height: 1.6;
                                            ">
                                                Se non riconosci questa richiesta,
                                                non utilizzare il link e contatta
                                                il CAF FAPI Pianopoli.
                                            </p>

                                            <p style="
                                                margin: 10px 0 0;
                                                color: #102c65;
                                                font-size: 12px;
                                                font-weight: 800;
                                                line-height: 1.6;
                                            ">
                                                CAF FAPI Pianopoli
                                                &nbsp;·&nbsp;
                                                377 960 9155
                                            </p>
                                        </td>
                                    </tr>
                                </table>

                                <p style="
                                    max-width: 620px;
                                    margin: 16px auto 0;
                                    color: #98a2b3;
                                    font-size: 10px;
                                    line-height: 1.5;
                                    text-align: center;
                                ">
                                    Messaggio automatico:
                                    non rispondere direttamente a questa email.
                                </p>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                salutoSicuro,
                linkSicuro,
                durataSicura,
                linkSicuro
        );
    }

    private String costruisciSaluto(
            String nome
    ) {
        if (nome == null || nome.isBlank()) {
            return "Ciao,";
        }

        return "Ciao " + nome.strip() + ",";
    }

    private String descriviDurata(
            int durataMinuti
    ) {
        if (durataMinuti <= 0) {
            return "un periodo limitato";
        }

        if (durataMinuti % 60 == 0) {
            int ore = durataMinuti / 60;

            return ore == 1
                    ? "1 ora"
                    : ore + " ore";
        }

        return durataMinuti == 1
                ? "1 minuto"
                : durataMinuti + " minuti";
    }
}