const API_URL = `http://${window.location.hostname || "localhost"}:8080`;
let cacheEquipamentos = [];

async function requisicao(rota, opcoes = {}) {
    const resposta = await fetch(`${API_URL}${rota}`, {
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        ...opcoes
    });

    if (resposta.status === 401) {
        window.location.href = "login.html";
        throw new Error("Sessão expirada.");
    }

    if (!resposta.ok) {
        const erroJson = await resposta.json().catch(() => ({}));
        throw new Error(erroJson.erro || erroJson.message || `Erro HTTP ${resposta.status}`);
    }

    if (resposta.status === 204) return null;
    return await resposta.json();
}

function mostrarFeedback(mensagem, tipo = "ok") {
    const box = document.getElementById("feedback");
    box.textContent = mensagem;
    box.className = `feedback ${tipo}`;
    setTimeout(() => box.classList.add("hidden"), 6000);
}

document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));
        document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));
        btn.classList.add("active");
        document.getElementById(`tab-${btn.dataset.tab}`).classList.add("active");
    });
});

async function carregarUsuarioLogado() {
    const usuario = await requisicao("/api/auth/me");
    document.getElementById("usuario-logado").textContent =
        `Operador: ${usuario.nome || usuario.email}`;
}

async function fazerLogout() {
    await requisicao("/api/auth/logout", { method: "POST" }).catch(() => {});
    window.location.href = "login.html";
}

async function carregarResumo() {
    const resumo = await requisicao("/api/equipamentos/resumo");
    document.getElementById("stat-total-equipamentos").textContent = resumo.totalEquipamentos ?? 0;
    document.getElementById("stat-total-unidades").textContent = resumo.totalUnidades ?? 0;
    document.getElementById("stat-itens-criticos").textContent = resumo.itensCriticos ?? 0;

    const banner = document.getElementById("alerta-critico-banner");
    if (resumo.itensCriticos > 0 && Array.isArray(resumo.listaCriticos)) {
        const nomes = resumo.listaCriticos
            .map(item => `${item.nome} (${item.quantidadeAtual}/${item.estoqueMinimo})`)
            .join(", ");
        banner.textContent = `Atenção — Estoque crítico ou abaixo do mínimo: ${nomes}`;
        banner.classList.remove("hidden");
    } else {
        banner.classList.add("hidden");
    }
}

async function carregarEquipamentos() {
    const termo = document.getElementById("filtro-termo").value.trim();
    const categoria = document.getElementById("filtro-categoria").value.trim();

    const params = new URLSearchParams();
    if (termo) params.append("termo", termo);
    if (categoria) params.append("categoria", categoria);

    const query = params.toString() ? `?${params.toString()}` : "";
    const lista = await requisicao(`/api/equipamentos${query}`);
    cacheEquipamentos = lista || [];

    const tbody = document.getElementById("lista-equipamentos");
    const select = document.getElementById("mov-equipamento");
    tbody.innerHTML = "";
    select.innerHTML = '<option value="">Selecione um equipamento...</option>';

    cacheEquipamentos.forEach(eq => {
        const critico = eq.estoqueCritico || (eq.quantidadeAtual <= eq.estoqueMinimo);
        const badgeStatus = critico
            ? '<span class="badge critico">CRÍTICO</span>'
            : '<span class="badge ok">NORMAL</span>';

        tbody.innerHTML += `
            <tr>
                <td><strong>${eq.codigoPatrimonio}</strong></td>
                <td>${eq.nome}</td>
                <td>${eq.categoria}</td>
                <td>${eq.especificacaoTecnica}</td>
                <td>${eq.quantidadeAtual}</td>
                <td>${eq.estoqueMinimo}</td>
                <td>${badgeStatus}</td>
                <td>
                    <button class="btn-edit" onclick="prepararEdicao(${eq.id})">Editar</button>
                    <button class="btn-danger" onclick="excluirEquipamento(${eq.id})">Excluir</button>
                </td>
            </tr>
        `;

        select.innerHTML += `<option value="${eq.id}">[${eq.codigoPatrimonio}] ${eq.nome} — Saldo: ${eq.quantidadeAtual}</option>`;
    });
}

function limparFiltros() {
    document.getElementById("filtro-termo").value = "";
    document.getElementById("filtro-categoria").value = "";
    carregarEquipamentos();
}

function prepararEdicao(id) {
    const eq = cacheEquipamentos.find(item => item.id === id);
    if (!eq) return;

    document.getElementById("eq-id").value = eq.id;
    document.getElementById("eq-codigo").value = eq.codigoPatrimonio;
    document.getElementById("eq-nome").value = eq.nome;
    document.getElementById("eq-categoria").value = eq.categoria;
    document.getElementById("eq-especificacao").value = eq.especificacaoTecnica;
    document.getElementById("eq-qtd").value = eq.quantidadeAtual;
    document.getElementById("eq-minimo").value = eq.estoqueMinimo;

    document.getElementById("titulo-form-equipamento").textContent = `Editando: ${eq.nome}`;
    document.getElementById("btn-salvar-equipamento").textContent = "Atualizar Equipamento";
    document.getElementById("btn-cancelar-edicao").classList.remove("hidden");
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function cancelarEdicao() {
    document.getElementById("form-equipamento").reset();
    document.getElementById("eq-id").value = "";
    document.getElementById("titulo-form-equipamento").textContent = "Cadastrar Novo Equipamento";
    document.getElementById("btn-salvar-equipamento").textContent = "Salvar Equipamento";
    document.getElementById("btn-cancelar-edicao").classList.add("hidden");
}

document.getElementById("form-equipamento").addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
        const idEdicao = document.getElementById("eq-id").value;
        const payload = {
            codigoPatrimonio: document.getElementById("eq-codigo").value.trim(),
            nome: document.getElementById("eq-nome").value.trim(),
            categoria: document.getElementById("eq-categoria").value.trim(),
            especificacaoTecnica: document.getElementById("eq-especificacao").value.trim(),
            quantidadeAtual: Number(document.getElementById("eq-qtd").value),
            estoqueMinimo: Number(document.getElementById("eq-minimo").value)
        };

        if (idEdicao) {
            await requisicao(`/api/equipamentos/${idEdicao}`, {
                method: "PUT",
                body: JSON.stringify(payload)
            });
            mostrarFeedback("Equipamento atualizado com sucesso!");
        } else {
            await requisicao("/api/equipamentos", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            mostrarFeedback("Equipamento cadastrado com sucesso!");
        }

        cancelarEdicao();
        await Promise.all([carregarEquipamentos(), carregarResumo()]);
    } catch (erro) {
        mostrarFeedback(erro.message, "erro");
    }
});

async function excluirEquipamento(id) {
    if (!confirm("Deseja realmente excluir este equipamento?")) return;
    try {
        await requisicao(`/api/equipamentos/${id}`, { method: "DELETE" });
        mostrarFeedback("Equipamento excluído.");
        await Promise.all([carregarEquipamentos(), carregarResumo()]);
    } catch (erro) {
        mostrarFeedback(erro.message, "erro");
    }
}

function preencherDataHoraAtual() {
    const agora = new Date();
    agora.setMinutes(agora.getMinutes() - agora.getTimezoneOffset());
    document.getElementById("mov-datahora").value = agora.toISOString().slice(0, 16);
}

async function carregarMovimentacoes() {
    const lista = await requisicao("/api/movimentacoes");
    const tbody = document.getElementById("lista-movimentacoes");
    tbody.innerHTML = "";

    (lista || []).forEach(mov => {
        const isEntrada = mov.tipoMovimentacao === "ENTRADA";
        const badgeTipo = isEntrada
            ? '<span class="badge entrada">ENTRADA</span>'
            : '<span class="badge saida">SAÍDA</span>';
        const dataFormatada = mov.dataHora
            ? new Date(mov.dataHora).toLocaleString("pt-BR")
            : "-";

        tbody.innerHTML += `
            <tr>
                <td>#${mov.id}</td>
                <td>${dataFormatada}</td>
                <td>${badgeTipo}</td>
                <td>${mov.equipamento?.nome ?? "-"} (${mov.equipamento?.codigoPatrimonio ?? ""})</td>
                <td><strong>${mov.quantidade}</strong></td>
                <td>${mov.usuario?.nome ?? mov.usuario?.email ?? "-"}</td>
                <td>${mov.observacaoEvento || "-"}</td>
            </tr>
        `;
    });
}

document.getElementById("form-movimentacao").addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
        const rawDataHora = document.getElementById("mov-datahora").value;
        const dataHoraFormatada = rawDataHora.length === 16 ? `${rawDataHora}:00` : rawDataHora;

        const payload = {
            equipamentoId: Number(document.getElementById("mov-equipamento").value),
            tipoMovimentacao: document.getElementById("mov-tipo").value,
            quantidade: Number(document.getElementById("mov-quantidade").value),
            dataHora: dataHoraFormatada,
            observacaoEvento: document.getElementById("mov-obs").value.trim()
        };

        const resposta = await requisicao("/api/movimentacoes", {
            method: "POST",
            body: JSON.stringify(payload)
        });

        e.target.reset();
        preencherDataHoraAtual();
        mostrarFeedback(resposta.mensagem, resposta.alertaCritico ? "erro" : "ok");

        await Promise.all([
            carregarEquipamentos(),
            carregarMovimentacoes(),
            carregarResumo()
        ]);
    } catch (erro) {
        mostrarFeedback(erro.message, "erro");
    }
});

window.addEventListener("DOMContentLoaded", async () => {
    preencherDataHoraAtual();
    try {
        await carregarUsuarioLogado();
        await Promise.all([
            carregarResumo(),
            carregarEquipamentos(),
            carregarMovimentacoes()
        ]);
    } catch (erro) {
        console.error(erro);
    }
});
