package com.martina.caf_fapi.allegati.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService
        implements FileStorageService {

    private static final Set<String> MIME_TYPES_CONSENTITI =
            Set.of(
                    "application/pdf",
                    "image/jpeg",
                    "image/png"
            );

    private static final long DIMENSIONE_MASSIMA =
            20L * 1024 * 1024;

    private final Path directoryStorage;

    public LocalFileStorageService(
            @Value(
                    "${app.storage.documenti-path:uploads/documenti}"
            )
            String directory
    ) {
        try {
            this.directoryStorage =
                    Paths.get(directory)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(
                    this.directoryStorage
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Impossibile inizializzare lo storage dei documenti.",
                    e
            );
        }
    }

    @Override
    public FileSalvato salva(
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Il file è obbligatorio."
            );
        }

        if (file.getSize() > DIMENSIONE_MASSIMA) {
            throw new IllegalArgumentException(
                    "Il file non può superare 20 MB."
            );
        }

        String mimeType =
                file.getContentType();

        if (
                mimeType == null ||
                        !MIME_TYPES_CONSENTITI.contains(
                                mimeType
                        )
        ) {
            throw new IllegalArgumentException(
                    "Formato non supportato. Sono consentiti PDF, JPG e PNG."
            );
        }

        String nomeOriginale =
                StringUtils.cleanPath(
                        file.getOriginalFilename() != null
                                ? file.getOriginalFilename()
                                : "documento"
                );

        if (nomeOriginale.contains("..")) {
            throw new IllegalArgumentException(
                    "Nome file non valido."
            );
        }

        String estensione =
                ottieniEstensione(
                        nomeOriginale
                );

        String nomeStorage =
                UUID.randomUUID() +
                        estensione;

        Path destinazione =
                directoryStorage.resolve(
                        nomeStorage
                ).normalize();

        if (
                !destinazione.startsWith(
                        directoryStorage
                )
        ) {
            throw new IllegalArgumentException(
                    "Percorso file non valido."
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    destinazione,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Errore durante il salvataggio del file.",
                    e
            );
        }

        return new FileSalvato(
                nomeOriginale,
                nomeStorage,
                mimeType,
                file.getSize()
        );
    }

    @Override
    public Resource carica(
            String nomeStorage
    ) {
        try {
            Path file =
                    directoryStorage
                            .resolve(nomeStorage)
                            .normalize();

            if (
                    !file.startsWith(
                            directoryStorage
                    )
            ) {
                throw new IllegalArgumentException(
                        "Percorso file non valido."
                );
            }

            Resource resource =
                    new UrlResource(
                            file.toUri()
                    );

            if (
                    !resource.exists() ||
                            !resource.isReadable()
            ) {
                throw new IllegalArgumentException(
                        "File non trovato."
                );
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                    "Errore durante il caricamento del file.",
                    e
            );
        }
    }

    @Override
    public void elimina(
            String nomeStorage
    ) {
        try {
            Path file =
                    directoryStorage
                            .resolve(nomeStorage)
                            .normalize();

            if (
                    !file.startsWith(
                            directoryStorage
                    )
            ) {
                throw new IllegalArgumentException(
                        "Percorso file non valido."
                );
            }

            Files.deleteIfExists(file);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Errore durante l'eliminazione del file.",
                    e
            );
        }
    }

    private String ottieniEstensione(
            String nomeFile
    ) {
        int indice =
                nomeFile.lastIndexOf('.');

        if (
                indice < 0 ||
                        indice == nomeFile.length() - 1
        ) {
            return "";
        }

        return nomeFile.substring(
                indice
        );
    }
}