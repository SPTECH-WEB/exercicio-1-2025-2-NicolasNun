package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping()
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.status(200).body(usuarios);
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        Usuario usuarioComEmail = usuarioRepository.findByEmail(usuario.getEmail());
        Usuario usuarioComCpf = usuarioRepository.findByCpf(usuario.getCpf());

        if (usuarioComEmail != null || usuarioComCpf != null) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.status(201).body(usuarioRepository.save(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.status(200).body(usuario))
                .orElse(ResponseEntity.status(404).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(404).build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(
            @RequestParam LocalDate nascimento) {
        List<Usuario> usuarios = usuarioRepository.findByDataNascimentoGreaterThan(nascimento);

        return usuarios.isEmpty()
                ? ResponseEntity.status(204).build()
                : ResponseEntity.status(200).body(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario
    ) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.status(404).build();
        }

        Usuario usuarioComEmail = usuarioRepository.findByEmail(usuario.getEmail());
        Usuario usuarioComCpf = usuarioRepository.findByCpf(usuario.getCpf());

        if ((usuarioComEmail != null && !usuarioComEmail.getId().equals(id))
            || (usuarioComCpf != null && !usuarioComCpf.getId().equals(id))) {
            return ResponseEntity.status(409).build();
        }

        usuario.setId(id);
        Usuario atualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(atualizado);
    }
}
