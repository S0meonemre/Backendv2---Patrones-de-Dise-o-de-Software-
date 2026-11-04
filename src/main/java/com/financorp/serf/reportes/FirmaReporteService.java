package com.financorp.serf.reportes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.security.*;
import java.util.Base64;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Firma RSA real para el prototipo. La clave efímera cambia en cada arranque. */
@Service
public class FirmaReporteService {
    private final KeyPair claves;
    private final ObjectMapper mapper;
    public FirmaReporteService(ObjectMapper mapper) {
        this.mapper = mapper.copy().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        try {
            KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
            generador.initialize(2048);
            claves = generador.generateKeyPair();
        } catch (GeneralSecurityException ex) { throw new IllegalStateException("No se pudo inicializar la firma.", ex); }
    }
    public Map<String, String> firmar(Map<String, Object> contenido) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(contenido);
            Signature firma = Signature.getInstance("SHA256withRSA");
            firma.initSign(claves.getPrivate());
            firma.update(bytes);
            Base64.Encoder encoder = Base64.getEncoder();
            return Map.of("algoritmo", "SHA256withRSA", "valor", encoder.encodeToString(firma.sign()),
                    "clavePublica", encoder.encodeToString(claves.getPublic().getEncoded()),
                    "contenidoBase64", encoder.encodeToString(bytes),
                    "alcance", "Firma académica con clave efímera, sin certificado de identidad.");
        } catch (Exception ex) { throw new IllegalStateException("No se pudo firmar el reporte.", ex); }
    }
}
