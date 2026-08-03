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

    private static final String OGGETTO =
            "Reimposta la password della tua area personale";

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String urlFrontend;

    @Value("${app.reset-password.mittente}")
    private String mittente;

    @Value("${app.reset-password.durata-minuti}")
    private int durataMinuti;

    @Override
    public void inviaLinkRecuperoPassword(
            String destinatario,
            String token
    ) {
        SimpleMailMessage messaggio = new SimpleMailMessage();

        messaggio.setFrom(mittente);
        messaggio.setTo(destinatario);
        messaggio.setSubject(OGGETTO);
        messaggio.setText(corpo(costruisciLink(token)));

        mailSender.send(messaggio);

        /* Nessun indirizzo e nessun token nei log: il primo è un dato
           personale, il secondo è una credenziale a tutti gli effetti. */
        LOGGER.info("Inviata una mail di recupero password.");
    }

    private String costruisciLink(String token) {
        String base = urlFrontend.endsWith("/")
                ? urlFrontend.substring(0, urlFrontend.length() - 1)
                : urlFrontend;

        return base + "/reset-password/" + token;
    }

    private String corpo(String link) {
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
                """.formatted(link, durataMinuti);
    }
}
