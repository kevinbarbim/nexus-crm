// Troca apenas as cores. A preferência vale para todas as telas deste navegador.
const botoesTema = document.querySelectorAll('[data-theme-toggle]');
function atualizarBotoesTema() {
    const escuro = document.documentElement.getAttribute('data-theme') !== 'light';
    for (const botao of botoesTema) {
        botao.querySelector('.theme-label').textContent = escuro ? 'Tema claro' : 'Tema escuro';
        botao.setAttribute('aria-label', escuro ? 'Ativar tema claro' : 'Ativar tema escuro');
        botao.hidden = false;
    }
    document.querySelector('meta[name="theme-color"]').setAttribute('content', escuro ? '#08090b' : '#f6f7f8');
}
atualizarBotoesTema();
for (const botao of botoesTema) {
    botao.addEventListener('click', function () {
        const escuro = document.documentElement.getAttribute('data-theme') !== 'light';
        const tema = escuro ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', tema);
        try {
            localStorage.setItem('nexo-tema', tema);
        } catch (erro) {
            // A troca funciona mesmo quando a preferência não pode ser salva.
        }
        atualizarBotoesTema();
    });
}

// Destaca a seção atual, incluindo telas de cadastro e edição.
const caminho = window.location.pathname;
const linksMenu = document.querySelectorAll('.sidebar nav a, .profile-link');
for (const link of linksMenu) {
    const destino = link.getAttribute('href');
    if (caminho === destino || (destino !== '/' && caminho.startsWith(destino + '/'))) {
        link.setAttribute('aria-current', 'page');
    }
}

// No celular, o botão abre o menu completo sem cobrir os formulários.
const botaoMenu = document.querySelector('.menu-toggle');
if (botaoMenu) {
    botaoMenu.addEventListener('click', function () {
        const aberto = botaoMenu.getAttribute('aria-expanded') === 'true';
        botaoMenu.setAttribute('aria-expanded', String(!aberto));
        botaoMenu.closest('.sidebar').classList.toggle('is-open', !aberto);
    });
}
const secaoAtual = document.querySelector('.sidebar nav a[aria-current="page"], .profile-link[aria-current="page"]');
const caminhoSecao = document.querySelector('.workspace-section');
if (secaoAtual && caminhoSecao) {
    caminhoSecao.textContent = secaoAtual.textContent.trim();
}

// Mostrar ou ocultar a senha é uma opção local, sem enviar nenhum dado.
for (const botao of document.querySelectorAll('[data-password]')) {
    botao.addEventListener('click', function () {
        const campo = document.getElementById(botao.dataset.password);
        const mostrar = campo.type === 'password';
        campo.type = mostrar ? 'text' : 'password';
        botao.setAttribute('aria-pressed', String(mostrar));
        botao.setAttribute('aria-label', mostrar ? 'Ocultar senha' : 'Mostrar senha');
    });
}

// Confirma ações usando o diálogo nativo, com suporte a teclado e Escape.
const dialogo = document.getElementById('confirmacao');
let formularioPendente = null;
let botaoPendente = null;
let formularioConfirmado = null;
if (dialogo) {
    dialogo.addEventListener('close', function () {
        if (dialogo.returnValue === 'confirmar' && formularioPendente) {
            formularioConfirmado = formularioPendente;
            if (botaoPendente) {
                formularioPendente.requestSubmit(botaoPendente);
            } else {
                formularioPendente.requestSubmit();
            }
        }
        formularioPendente = null;
        botaoPendente = null;
    });
}

document.addEventListener('submit', function (event) {
    if (event.defaultPrevented) {
        return;
    }
    const formulario = event.target;
    if (formulario.matches('[data-confirm]') && formularioConfirmado !== formulario) {
        event.preventDefault();
        if (dialogo && typeof dialogo.showModal === 'function') {
            formularioPendente = formulario;
            botaoPendente = event.submitter;
            dialogo.querySelector('#confirmacao-texto').textContent = formulario.dataset.confirm;
            const exclusao = formulario.dataset.confirm.startsWith('Excluir');
            const confirmar = dialogo.querySelector('button[value="confirmar"]');
            confirmar.textContent = exclusao ? 'Excluir' : 'Confirmar';
            confirmar.classList.toggle('destructive', exclusao);
            dialogo.returnValue = '';
            dialogo.showModal();
        } else if (window.confirm(formulario.dataset.confirm)) {
            formularioConfirmado = formulario;
            formulario.requestSubmit();
        }
        return;
    }
    formularioConfirmado = null;
    if (formulario.method === 'post') {
        // Aguarda as opções do imóvel antes de enviar o cadastro.
        if (formulario.querySelector('select[aria-busy="true"]')) {
            event.preventDefault();
            return;
        }
        if (formulario.dataset.enviando === 'true') {
            event.preventDefault();
            return;
        }
        formulario.dataset.enviando = 'true';
        const botao = event.submitter;
        if (botao && botao.classList.contains('btn')) {
            botao.setAttribute('aria-busy', 'true');
        }
    }
});

// Ao voltar pelo navegador, a página pode ser restaurada do cache.
window.addEventListener('pageshow', function () {
    for (const formulario of document.querySelectorAll('form[data-enviando]')) {
        delete formulario.dataset.enviando;
    }
    for (const botao of document.querySelectorAll('button[aria-busy]')) {
        botao.removeAttribute('aria-busy');
    }
});

// Visitas e propostas só oferecem imóveis de interesse do cliente escolhido.
const cadastroComImovel = document.querySelector('[data-imoveis-por-lead]');
if (cadastroComImovel) {
    const cliente = cadastroComImovel.querySelector('#leadId');
    const imovel = cadastroComImovel.querySelector('#imovelId');
    const ajuda = imovel.parentElement.querySelector('.help');
    let consultaAtual = 0;
    ajuda.setAttribute('role', 'status');
    cliente.addEventListener('change', async function () {
        const numeroConsulta = ++consultaAtual;
        imovel.replaceChildren(new Option('Selecione', ''));
        imovel.disabled = true;
        ajuda.classList.remove('error-text');
        if (!cliente.value) {
            ajuda.textContent = 'Selecione o cliente para consultar seus imóveis de interesse.';
            imovel.disabled = false;
            imovel.removeAttribute('aria-busy');
            return;
        }
        imovel.setAttribute('aria-busy', 'true');
        ajuda.textContent = 'Carregando imóveis de interesse…';
        try {
            const resposta = await fetch('/imoveis/opcoes?leadId=' + cliente.value);
            if (!resposta.ok || resposta.redirected) {
                throw new Error('Não foi possível consultar os imóveis. Recarregue a página.');
            }
            const opcoes = await resposta.json();
            // Ignora respostas antigas se o cliente mudou durante a consulta.
            if (numeroConsulta !== consultaAtual) {
                return;
            }
            for (const opcao of opcoes) {
                imovel.add(new Option(opcao.titulo, opcao.id));
            }
            ajuda.textContent = opcoes.length ? 'Escolha um imóvel de interesse.'
                : 'Adicione um imóvel de interesse na ficha do cliente.';
        } catch (erro) {
            if (numeroConsulta === consultaAtual) {
                ajuda.classList.add('error-text');
                ajuda.textContent = erro.message;
            }
        } finally {
            if (numeroConsulta === consultaAtual) {
                imovel.disabled = false;
                imovel.removeAttribute('aria-busy');
            }
        }
    });
}
