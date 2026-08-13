package com.martina.caf_fapi.auth.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

        SimpleMailMessage messaggio =
                new SimpleMailMessage();

        messaggio.setFrom(mittente);
        messaggio.setTo(destinatario);
        messaggio.setSubject(
                OGGETTO_RESET_PASSWORD
        );

        messaggio.setText(
                corpoResetPassword(
                        costruisciLinkResetPassword(token)
                )
        );

        mailSender.send(messaggio);

        /*
         * Nessun indirizzo e nessun token nei log:
         * il primo è un dato personale,
         * il secondo è una credenziale.
         */
        LOGGER.info(
                "Inviata una mail di recupero password."
        );
    }


    @Override
    public void inviaInvitoAttivazioneAccount(
            String destinatario,
            String nome,
            String token
    ) {

        SimpleMailMessage messaggio =
                new SimpleMailMessage();

        messaggio.setFrom(mittente);
        messaggio.setTo(destinatario);
        messaggio.setSubject(
                OGGETTO_ATTIVAZIONE_ACCOUNT
        );

        messaggio.setText(
                corpoAttivazioneAccount(
                        nome,
                        costruisciLinkAttivazioneAccount(token)
                )
        );

        mailSender.send(messaggio);

        /*
         * Anche qui non registriamo né email né token.
         */
        LOGGER.info(
                "Inviata una mail di attivazione account."
        );
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

                abbiamo ricevuto una richiesta di reimpostazione della password \
                per la tua area personale del CAF FAPI Pianopoli.

                Apri questo link per scegliere una nuova password:
                %s

                Il link è valido per %d minuti e può essere usato una sola volta.

                Se non hai richiesto tu il cambio password puoi ignorare questo \
                messaggio: la tua password attuale resta valida.

                CAF FAPI Pianopoli — 377 960 9155
                """.formatted(
                link,
                durataResetPasswordMinuti
        );
    }


    private String corpoAttivazioneAccount(
            String nome,
            String link
    ) {

        String nomeCliente =
                nome == null || nome.isBlank()
                        ? ""
                        : " " + nome.strip();

        return """
                Ciao%s,

                il CAF FAPI Pianopoli ha creato per te un'Area Cliente personale.

                Per completare l'attivazione del tuo account e scegliere \
                la tua password, apri il seguente link:

                %s

                Il link è valido per %d minuti e può essere utilizzato \
                una sola volta.

                Se non riconosci questa richiesta, non utilizzare il link \
                e contatta il CAF FAPI Pianopoli.

                CAF FAPI Pianopoli — 377 960 9155
                """.formatted(
                nomeCliente,
                link,
                durataAttivazioneMinuti
        );
    }
}