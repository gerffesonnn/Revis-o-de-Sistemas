package br.com.arenaxp.config;

import br.com.arenaxp.model.Equipamento;
import br.com.arenaxp.model.Usuario;
import br.com.arenaxp.repository.EquipamentoRepository;
import br.com.arenaxp.repository.MovimentacaoRepository;
import br.com.arenaxp.repository.UsuarioRepository;
import br.com.arenaxp.service.AuthService;
import br.com.arenaxp.service.MovimentacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${arenaxp.admin.password:}")
    private String senhaAdmin;

    @Bean
    public CommandLineRunner inicializarDadosSaep(
            UsuarioRepository usuarioRepository,
            EquipamentoRepository equipamentoRepository,
            MovimentacaoRepository movimentacaoRepository,
            AuthService authService,
            MovimentacaoService movimentacaoService) {

        return args -> {
            Usuario admin = null;
            Usuario op1 = null;
            Usuario op2 = null;

            // 1. Criar 3 usuários somente se a tabela estiver vazia
            if (usuarioRepository.count() == 0) {
                if (senhaAdmin == null || senhaAdmin.length() < 12) {
                    logger.warn("Usuário administrador não criado. Configure ARENAXP_ADMIN_PASSWORD com pelo menos 12 caracteres.");
                    return;
                }

                Usuario u1 = new Usuario();
                u1.setNome("Administrador ArenaXP");
                u1.setEmail("admin@arenaxp.com");
                u1.setSenha(senhaAdmin);
                admin = authService.registrar(u1);
                admin.setPerfil("ADMIN");
                admin = usuarioRepository.save(admin);

                Usuario u2 = new Usuario();
                u2.setNome("Lucas Almeida (Operador)");
                u2.setEmail("operador1@arenaxp.com");
                u2.setSenha("Operador@2026");
                op1 = authService.registrar(u2);

                Usuario u3 = new Usuario();
                u3.setNome("Mariana Costa (Operador)");
                u3.setEmail("operador2@arenaxp.com");
                u3.setSenha("Operador@2026");
                op2 = authService.registrar(u3);

                logger.info("3 usuários iniciais criados com sucesso.");
            } else {
                admin = usuarioRepository.findAll().get(0);
                op1 = admin;
                op2 = admin;
            }

            // 2. Criar 5 equipamentos somente se a tabela estiver vazia
            Equipamento headset = null;
            Equipamento monitor = null;
            Equipamento mouse = null;
            Equipamento notebook = null;
            Equipamento controle = null;

            if (equipamentoRepository.count() == 0) {
                headset = equipamentoRepository.save(criarEquipamento(
                        "PAT-001", "Headset Gamer", "Periféricos",
                        "Surround 7.1 USB, Microfone com cancelamento de ruído", 12, 3));

                monitor = equipamentoRepository.save(criarEquipamento(
                        "PAT-002", "Monitor 24\"", "Monitores",
                        "Painel IPS Full HD 165Hz, Tempo de resposta 1ms", 6, 2));

                mouse = equipamentoRepository.save(criarEquipamento(
                        "PAT-003", "Mouse Gamer", "Periféricos",
                        "Sensor óptico 16000 DPI, 6 botões programáveis", 2, 3));

                notebook = equipamentoRepository.save(criarEquipamento(
                        "PAT-004", "Notebook Gamer", "Computadores",
                        "Intel Core i7, 16GB RAM, SSD 512GB, RTX 4060", 4, 1));

                controle = equipamentoRepository.save(criarEquipamento(
                        "PAT-005", "Controle Xbox", "Controles",
                        "Wireless Series X/S Preto com cabo USB-C", 8, 2));

                logger.info("5 equipamentos iniciais criados com sucesso.");
            }

            // 3. Criar 4 movimentações somente se a tabela estiver vazia e os equipamentos acabaram de ser criados
            if (movimentacaoRepository.count() == 0 && mouse != null && monitor != null && headset != null && controle != null) {
                movimentacaoService.registrar(
                        mouse.getId(), "ENTRADA", 5,
                        "2026-11-20T09:30:00",
                        "Reposição de estoque de periféricos para torneio",
                        admin.getId()
                );

                movimentacaoService.registrar(
                        monitor.getId(), "SAIDA", 1,
                        "2026-11-20T14:15:00",
                        "Substituição na bancada de transmissão Arena 01",
                        op1.getId()
                );

                movimentacaoService.registrar(
                        headset.getId(), "ENTRADA", 3,
                        "2026-11-21T10:00:00",
                        "Compra lote adicional de headsets",
                        admin.getId()
                );

                movimentacaoService.registrar(
                        controle.getId(), "SAIDA", 2,
                        "2026-11-21T16:45:00",
                        "Empréstimo para etapa classificatória console",
                        op2.getId()
                );

                logger.info("4 movimentações iniciais registradas com sucesso.");
            }
        };
    }

    private Equipamento criarEquipamento(String codigo, String nome, String categoria,
                                         String especificacao, int qtdInicial, int minimo) {
        Equipamento eq = new Equipamento();
        eq.setCodigoPatrimonio(codigo);
        eq.setNome(nome);
        eq.setCategoria(categoria);
        eq.setEspecificacaoTecnica(especificacao);
        eq.setQuantidadeAtual(qtdInicial);
        eq.setEstoqueMinimo(minimo);
        return eq;
    }
}
