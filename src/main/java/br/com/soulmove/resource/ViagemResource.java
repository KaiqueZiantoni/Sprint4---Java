package br.com.soulmove.resource;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.soulmove.bo.ViagemBO;
import br.com.soulmove.dto.ViagemDTO;
import br.com.soulmove.model.Viagem;

@RestController
@RequestMapping("/viagens")
public class ViagemResource {

    private final ViagemBO viagemBO;

    public ViagemResource(ViagemBO viagemBO) {
        this.viagemBO = viagemBO;
    }

    /** GET /viagens ou GET /viagens?idUsuario=1 */
    @GetMapping
    public ResponseEntity<List<Viagem>> listar(@RequestParam(required = false) Integer idUsuario) {
        if (idUsuario != null) {
            return ResponseEntity.ok(viagemBO.listarPorUsuario(idUsuario));
        }
        return ResponseEntity.ok(viagemBO.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Viagem> buscar(@PathVariable int id) {
        return ResponseEntity.ok(viagemBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Viagem> registrar(@RequestBody ViagemDTO dto) {
        Viagem criada = viagemBO.registrar(dto);
        URI uri = URI.create("/viagens/" + criada.getIdViagem());
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Viagem> atualizar(@PathVariable int id, @RequestBody ViagemDTO dto) {
        return ResponseEntity.ok(viagemBO.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        viagemBO.remover(id);
        return ResponseEntity.noContent().build();
    }
}
