// Executado antes do CSS para abrir a página já no tema escolhido.
let temaInicial = 'dark';
try {
    if (localStorage.getItem('nexo-tema') === 'light') {
        temaInicial = 'light';
    }
} catch (erro) {
    // O tema escuro continua funcionando se o navegador bloquear o armazenamento.
}
document.documentElement.setAttribute('data-theme', temaInicial);
