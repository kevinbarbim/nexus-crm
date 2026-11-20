// As contagens já são calculadas pelo CRUD do funil; não há uma API nova.
const graficoEtapas = document.querySelector('[data-resumo-funil]');
async function carregarEtapas() {
    const mensagem = graficoEtapas.querySelector('[role="status"]');
    try {
        const resposta = await fetch('/leads');
        if (!resposta.ok || resposta.redirected) {
            throw new Error('Consulta indisponível');
        }
        const pagina = new DOMParser().parseFromString(await resposta.text(), 'text/html');
        const colunas = pagina.querySelectorAll('.column[data-stage][data-count]');
        if (colunas.length !== 7) {
            throw new Error('Contagens indisponíveis');
        }
        let total = 0;
        let maior = 0;
        for (const coluna of colunas) {
            const quantidade = Number(coluna.dataset.count);
            if (!Number.isInteger(quantidade) || quantidade < 0) {
                throw new Error('Contagem inválida');
            }
            total += quantidade;
            if (quantidade > maior) {
                maior = quantidade;
            }
        }
        for (const coluna of colunas) {
            const quantidade = Number(coluna.dataset.count);
            const linha = graficoEtapas.querySelector('[data-stage="' + coluna.dataset.stage + '"]');
            if (!linha) {
                throw new Error('Etapa indisponível');
            }
            linha.querySelector('.stage-value').textContent = quantidade;
            linha.querySelector('.stage-fill').style.width = (maior > 0 ? 100 * quantidade / maior : 0) + '%';
        }
        mensagem.textContent = total > 0 ? total + ' clientes distribuídos entre as etapas.' : 'Nenhum cliente cadastrado.';
        graficoEtapas.dataset.state = total > 0 ? 'ready' : 'empty';
    } catch (erro) {
        graficoEtapas.dataset.state = 'error';
        mensagem.setAttribute('role', 'alert');
        mensagem.textContent = 'Não foi possível carregar a distribuição dos clientes.';
        graficoEtapas.querySelector('.chart-retry').hidden = false;
    } finally {
        graficoEtapas.setAttribute('aria-busy', 'false');
    }
}
if (graficoEtapas) {
    carregarEtapas();
}
