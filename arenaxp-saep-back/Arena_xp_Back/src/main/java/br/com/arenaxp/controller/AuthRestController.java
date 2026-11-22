package br.com.arenaxp.controller;

import br.com.arenaxp.config.AuthInterceptor;
import br.com.arenaxp.model.Usuario;
import br.com.arenaxp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> credenciais, HttpSession session) {
        Usuario usuario = authService.autenticar(credenciais.get("email"), credenciais.get("senha"));
        session.setAttribute(AuthInterceptor.SESSAO_USUARIO_ID, usuario.getId());
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@RequestBody Map<String, String> dados, HttpSession session) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dados.get("nome"));
        novoUsuario.setEmail(dados.get("email"));
        novoUsuario.setSenha(dados.get("senha"));

        Usuario salvo = authService.registrar(novoUsuario);
        session.setAttribute(AuthInterceptor.SESSAO_USUARIO_ID, salvo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> usuarioLogado(HttpSession session) {
        Long id = (Long) session.getAttribute(AuthInterceptor.SESSAO_USUARIO_ID);
        return ResponseEntity.ok(authService.buscarPorId(id));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

}
