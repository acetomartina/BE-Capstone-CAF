package com.martina.caf_fapi.utenti.service;

import com.martina.caf_fapi.exception.InvalidDataException;
import com.martina.caf_fapi.exception.ResourceAlreadyExistsException;
import com.martina.caf_fapi.exception.ResourceNotFoundException;
import com.martina.caf_fapi.utenti.dto.CreaUtenteRequest;
import com.martina.caf_fapi.utenti.dto.UtenteResponse;
import com.martina.caf_fapi.utenti.dto.UtenteUpdateRequest;
import com.martina.caf_fapi.utenti.entity.Ruolo;
import com.martina.caf_fapi.utenti.entity.Utente;
import com.martina.caf_fapi.utenti.mapper.UtenteMapper;
import com.martina.caf_fapi.utenti.repository.UtenteRepository;
import com.martina.caf_fapi.util.FormattazioneUtils;
import com.martina.caf_fapi.validation.CodiceFiscaleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final UtenteMapper utenteMapper;
    private final PasswordEncoder passwordEncoder;
    private final CodiceFiscaleValidator codiceFiscaleValidator;

    @Override
    @Transactional
    public UtenteResponse creaUtente(
            CreaUtenteRequest request
    ) {
        CreaUtenteRequest requestNormalizzata =
                normalizzaCreaUtenteRequest(request);

        if (!codiceFiscaleValidator.isValido(
                requestNormalizzata.codiceFiscale()
        )) {
            throw new InvalidDataException(
                    "Il codice fiscale non è valido."
            );
        }


        verificaDuplicati(requestNormalizzata);

        verificaPermessoCreazioneRuolo(
                requestNormalizzata.ruolo()
        );

        Utente utente =
                utenteMapper.toEntity(requestNormalizzata);

        utente.setPassword(
                passwordEncoder.encode(
                        requestNormalizzata.password()
                )
        );

        utente.setAttivo(true);
        utente.setEmailVerificata(false);
        utente.setAccountBloccato(false);
        utente.setTentativiAccessoFalliti(0);
        utente.setPasswordModificataIl(LocalDateTime.now());

        Utente utenteSalvato =
                utenteRepository.save(utente);

        return utenteMapper.toResponse(utenteSalvato);
    }

    @Override
    @Transactional(readOnly = true)
    public UtenteResponse trovaPerId(Long id) {
        Utente utente = trovaEntitaPerId(id);

        return utenteMapper.toResponse(utente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtenteResponse> trovaTutti(
            Pageable pageable
    ) {
        return utenteRepository.findAll(pageable)
                .map(utenteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UtenteResponse> trovaPerRuolo(
            Ruolo ruolo,
            Pageable pageable
    ) {
        return utenteRepository
                .findByRuolo(ruolo, pageable)
                .map(utenteMapper::toResponse);
    }

    @Override
    @Transactional
    public UtenteResponse aggiornaUtente(
            Long id,
            UtenteUpdateRequest request
    ) {
        Utente utente = trovaEntitaPerId(id);

        normalizzaUpdateRequest(request);

        verificaEmailDuplicata(
                utente,
                request
        );

        utenteMapper.updateEntity(
                request,
                utente
        );

        Utente utenteAggiornato =
                utenteRepository.save(utente);

        return utenteMapper.toResponse(
                utenteAggiornato
        );
    }

    @Override
    @Transactional
    public UtenteResponse cambiaRuolo(
            Long id,
            Ruolo nuovoRuolo
    ) {
        Utente utente = trovaEntitaPerId(id);

        if (nuovoRuolo == null) {
            throw new InvalidDataException(
                    "Il nuovo ruolo è obbligatorio."
            );
        }

        if (nuovoRuolo == Ruolo.SUPER_ADMIN) {
            throw new InvalidDataException(
                    "Non è possibile assegnare il ruolo SUPER_ADMIN tramite API."
            );
        }

        verificaPermessoCreazioneRuolo(
                nuovoRuolo
        );

        if (utente.getRuolo() == nuovoRuolo) {
            throw new InvalidDataException(
                    "L'utente possiede già questo ruolo."
            );
        }

        utente.setRuolo(nuovoRuolo);

        Utente utenteAggiornato =
                utenteRepository.save(utente);

        return utenteMapper.toResponse(
                utenteAggiornato
        );
    }

    @Override
    @Transactional
    public UtenteResponse attivaUtente(Long id) {
        Utente utente = trovaEntitaPerId(id);

        utente.setAttivo(true);

        Utente utenteAggiornato =
                utenteRepository.save(utente);

        return utenteMapper.toResponse(
                utenteAggiornato
        );
    }

    @Override
    @Transactional
    public UtenteResponse disattivaUtente(Long id) {
        Utente utente = trovaEntitaPerId(id);

        utente.setAttivo(false);

        Utente utenteAggiornato =
                utenteRepository.save(utente);

        return utenteMapper.toResponse(
                utenteAggiornato
        );
    }

    private CreaUtenteRequest normalizzaCreaUtenteRequest(
            CreaUtenteRequest request
    ) {
        return new CreaUtenteRequest(
                FormattazioneUtils.normalizzaTitleCase(
                        request.nome()
                ),
                FormattazioneUtils.normalizzaTitleCase(
                        request.cognome()
                ),
                FormattazioneUtils.normalizzaCodiceFiscale(
                        request.codiceFiscale()
                ),
                request.dataNascita(),
                FormattazioneUtils.normalizzaTitleCase(
                        request.luogoNascita()
                ),
                FormattazioneUtils.normalizzaEmail(
                        request.email()
                ),
                FormattazioneUtils.normalizzaTelefono(
                        request.telefono()
                ),
                FormattazioneUtils.normalizzaTesto(
                        request.indirizzo()
                ),
                FormattazioneUtils.normalizzaTitleCase(
                        request.comune()
                ),
                FormattazioneUtils.normalizzaProvincia(
                        request.provincia()
                ),
                FormattazioneUtils.normalizzaCap(
                        request.cap()
                ),
                request.password(),
                request.ruolo(),
                FormattazioneUtils.normalizzaTitleCase(
                        request.mansione()
                ),
                FormattazioneUtils.normalizzaTesto(
                        request.numeroMatricola()
                )
        );
    }

    private void normalizzaUpdateRequest(
            UtenteUpdateRequest request
    ) {
        request.setNome(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getNome()
                )
        );

        request.setCognome(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getCognome()
                )
        );

        request.setEmail(
                FormattazioneUtils.normalizzaEmail(
                        request.getEmail()
                )
        );

        request.setTelefono(
                FormattazioneUtils.normalizzaTelefono(
                        request.getTelefono()
                )
        );

        request.setIndirizzo(
                FormattazioneUtils.normalizzaTesto(
                        request.getIndirizzo()
                )
        );

        request.setComune(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getComune()
                )
        );

        request.setProvincia(
                FormattazioneUtils.normalizzaProvincia(
                        request.getProvincia()
                )
        );

        request.setCap(
                FormattazioneUtils.normalizzaCap(
                        request.getCap()
                )
        );

        request.setMansione(
                FormattazioneUtils.normalizzaTitleCase(
                        request.getMansione()
                )
        );

        request.setUrlImmagineProfilo(
                FormattazioneUtils.normalizzaUrl(
                        request.getUrlImmagineProfilo()
                )
        );
    }

    private void verificaDuplicati(
            CreaUtenteRequest request
    ) {
        if (utenteRepository.existsByEmailIgnoreCase(
                request.email()
        )) {
            throw new ResourceAlreadyExistsException(
                    "Esiste già un utente con questa email"
            );
        }

        if (utenteRepository.existsByCodiceFiscale(
                request.codiceFiscale()
        )) {
            throw new ResourceAlreadyExistsException(
                    "Esiste già un utente con questo codice fiscale"
            );
        }

        if (
                request.numeroMatricola() != null
                        && !request.numeroMatricola().isBlank()
                        && utenteRepository.existsByNumeroMatricola(
                        request.numeroMatricola()
                )
        ) {
            throw new ResourceAlreadyExistsException(
                    "Esiste già un utente con questo numero di matricola"
            );
        }
    }

    private void verificaEmailDuplicata(
            Utente utente,
            UtenteUpdateRequest request
    ) {
        boolean emailModificata =
                !utente.getEmail().equalsIgnoreCase(
                        request.getEmail()
                );

        if (
                emailModificata
                        && utenteRepository.existsByEmailIgnoreCase(
                        request.getEmail()
                )
        ) {
            throw new ResourceAlreadyExistsException(
                    "Esiste già un utente con questa email"
            );
        }
    }

    private void verificaPermessoCreazioneRuolo(
            Ruolo ruoloRichiesto
    ) {
        if (ruoloRichiesto == null) {
            throw new InvalidDataException(
                    "Il ruolo è obbligatorio."
            );
        }

        if (ruoloRichiesto == Ruolo.SUPER_ADMIN) {
            throw new AccessDeniedException(
                    "Non è possibile creare o assegnare il ruolo SUPER_ADMIN tramite API."
            );
        }

        Ruolo ruoloAutenticato =
                recuperaRuoloAutenticato();

        boolean consentito =
                switch (ruoloAutenticato) {
                    case SUPER_ADMIN ->
                            ruoloRichiesto == Ruolo.ADMIN
                                    || ruoloRichiesto == Ruolo.USER
                                    || ruoloRichiesto == Ruolo.CLIENTE;

                    case ADMIN ->
                            ruoloRichiesto == Ruolo.USER
                                    || ruoloRichiesto == Ruolo.CLIENTE;

                    case USER ->
                            ruoloRichiesto == Ruolo.CLIENTE;

                    case CLIENTE -> false;
                };

        if (!consentito) {
            throw new AccessDeniedException(
                    "Non possiedi i permessi per creare o assegnare il ruolo "
                            + ruoloRichiesto
                            + "."
            );
        }
    }

    private Ruolo recuperaRuoloAutenticato() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                authentication == null
                        || !authentication.isAuthenticated()
        ) {
            throw new AccessDeniedException(
                    "Utente non autenticato."
            );
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority ->
                        authority.startsWith("ROLE_")
                                ? authority.substring(5)
                                : authority
                )
                .map(authority -> {
                    try {
                        return Ruolo.valueOf(authority);
                    } catch (IllegalArgumentException exception) {
                        return null;
                    }
                })
                .filter(ruolo -> ruolo != null)
                .findFirst()
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Ruolo dell'utente autenticato non valido."
                        )
                );
    }

    private Utente trovaEntitaPerId(Long id) {
        return utenteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Utente non trovato con id: " + id
                        )
                );
    }
}