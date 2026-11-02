package br.com.soulmove.resource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.soulmove.exception.ValidacaoException;

/**
 * Calculadora de impacto usada pela pagina inicial do site (front-end).
 * Reproduz a formula da calculadora do front, agora servida pela API.
 */
@RestController
@RequestMapping("/impacto")
public class ImpactoResource {

    private static final double KG_CO2_POR_KM = 0.09;
    private static final double ABSORCAO_ARVORE_ANO_KG = 22;
    private static final int PONTOS_POR_KG = 10;
    private static final int SEMANAS_POR_ANO = 48;

    @GetMapping
    public ResponseEntity<Map<String, Object>> calcular(@RequestParam double kmSemanais) {
        if (kmSemanais < 0) {
            throw new ValidacaoException("A quilometragem semanal nao pode ser negativa.");
        }
        if (kmSemanais > 5000) {
            throw new ValidacaoException("Quilometragem semanal acima do limite de 5000 km.");
        }

        double co2AnualKg = kmSemanais * SEMANAS_POR_ANO * KG_CO2_POR_KM;

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("kmSemanais", kmSemanais);
        resultado.put("co2AnualKg", co2AnualKg);
        resultado.put("arvoresEquivalentes", co2AnualKg / ABSORCAO_ARVORE_ANO_KG);
        resultado.put("pontosSoulUp", Math.round(co2AnualKg * PONTOS_POR_KG));
        return ResponseEntity.ok(resultado);
    }
}
