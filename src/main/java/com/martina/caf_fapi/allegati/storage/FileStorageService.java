package com.martina.caf_fapi.allegati.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    FileSalvato salva(
            MultipartFile file
    );

    Resource carica(
            String nomeStorage
    );

    void elimina(
            String nomeStorage
    );
}