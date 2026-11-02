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
import org.springframework.web.bind.annotation.RestController;

import br.com.soulmove.bo.MeioTransporteBO;
import br.com.soulmove.dto.TransporteDTO;
import br.com.soulmove.model.MeioTransporte;

@RestController
@RequestMapping("/transportes")
public class MeioTransporteResource {

    private final MeioTransporteBO transporteBO;

    public MeioTransporteResource(MeioTransporteBO transporteBO) {
        this.transporteBO = transporteBO;
    }

    @GetMapping
    public ResponseEntity<List<MeioTransporte>> listar() {
        return ResponseEntity.ok(transporteBO.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeioTransporte> buscar(@PathVariable int id) {
        return ResponseEntity.ok(transporteBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<MeioTransporte> cadastrar(@RequestBody TransporteDTO dto) {
        MeioTransporte criado = transporteBO.cadastrar(dto);
        URI uri = URI.create("/transportes/" + criado.getIdTransporte());
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeioTransporte> atualizar(@PathVariable int id, @RequestBody TransporteDTO dto) {
        return ResponseEntity.ok(transporteBO.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        transporteBO.remover(id);
        return ResponseEntity.noContent().build();
    }
}
