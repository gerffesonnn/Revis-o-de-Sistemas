package br.com.arenaxp.service;

import br.com.arenaxp.exception.RegraNegocioException;
import br.com.arenaxp.model.Usuario;
import br.com.arenaxp.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new RegraNegocioException("Informe e-mail e senha para entrar.");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha inválidos."));

        String hashInformado = gerarSha256(senha);
        boolean senhaValida = usuario.getSenha().equalsIgnoreCase(hashInformado)
                || usuario.getSenha().equals(senha);

        if (!senhaValida) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }

        return usuario;
    }

    public Usuario registrar(Usuario novoUsuario) {
        if (novoUsuario == null
                || novoUsuario.getNome() == null || novoUsuario.getNome().isBlank()
                || novoUsuario.getEmail() == null || novoUsuario.getEmail().isBlank()
                || novoUsuario.getSenha() == null || novoUsuario.getSenha().isBlank()) {
            throw new RegraNegocioException("Informe nome, e-mail e senha para se registrar.");
        }

        if (novoUsuario.getNome().trim().length() > 100 || novoUsuario.getEmail().trim().length() > 120) {
            throw new RegraNegocioException("Nome ou e-mail excede o limite permitido.");
        }

        String email = novoUsuario.getEmail().trim();
        if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        novoUsuario.setId(null);
        novoUsuario.setNome(novoUsuario.getNome().trim());
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(gerarSha256(novoUsuario.getSenha()));
        novoUsuario.setPerfil("OPERADOR");

        return usuarioRepository.save(novoUsuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Usuário da sessão não encontrado."));
    }

    private String gerarSha256(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível.", e);
        }
    }
}
