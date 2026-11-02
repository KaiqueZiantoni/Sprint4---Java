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

import br.com.soulmove.bo.UsuarioBO;
import br.com.soulmove.dto.ResgateDTO;
import br.com.soulmove.model.Usuario;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    private final UsuarioBO usuarioBO;

    public UsuarioResource(UsuarioBO usuarioBO) {
        this.usuarioBO = usuarioBO;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioBO.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable int id) {
        return ResponseEntity.ok(usuarioBO.buscar(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        Usuario criado = usuarioBO.cadastrar(usuario);
        URI uri = URI.create("/usuarios/" + criado.getIdUsuario());
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable int id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioBO.atualizar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        usuarioBO.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resgates")
    public ResponseEntity<Usuario> resgatarPontos(@PathVariable int id, @RequestBody ResgateDTO resgate) {
        return ResponseEntity.ok(usuarioBO.resgatarPontos(id, resgate.getPontos()));
    }
}
