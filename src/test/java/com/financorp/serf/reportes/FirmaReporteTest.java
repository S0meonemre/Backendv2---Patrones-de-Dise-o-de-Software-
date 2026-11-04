package com.financorp.serf.reportes;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FirmaReporteTest {
    @Test void firmaVerificableYDetectaAlteraciones() throws Exception {
        var firma = new FirmaReporteService(new ObjectMapper()).firmar(Map.of("ingresos", 100, "periodo", "2026-11"));
        var decoder = Base64.getDecoder();
        var publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoder.decode(firma.get("clavePublica"))));
        Signature verificador = Signature.getInstance(firma.get("algoritmo"));
        verificador.initVerify(publicKey);
        verificador.update(decoder.decode(firma.get("contenidoBase64")));
        assertTrue(verificador.verify(decoder.decode(firma.get("valor"))));
        verificador.initVerify(publicKey);
        verificador.update("alterado".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertFalse(verificador.verify(decoder.decode(firma.get("valor"))));
    }
}
