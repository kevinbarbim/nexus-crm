import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/** Executa pelo carregador do JAR, sem conexao ao banco. */
public class VerificarEmpacotamento {
    public static void main(String[] args) throws Exception {
        PathMatchingResourcePatternResolver recursos = new PathMatchingResourcePatternResolver();
        verificar(recursos.getResource("classpath:templates/").exists(), "diretorio de templates");
        verificar(recursos.getResource("classpath:templates/login.html").exists(), "template de login");

        Resource[] classes = recursos.getResources("classpath*:br/com/nexoimob/**/*.class");
        verificar(classes.length == 68, "descoberta das 68 classes do Nexus (encontradas: " + classes.length + ")");

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(true);
        Set<String> componentes = new HashSet<>();
        for (BeanDefinition bean : scanner.findCandidateComponents("br.com.nexoimob")) {
            componentes.add(bean.getBeanClassName());
        }
        verificar(componentes.contains("br.com.nexoimob.config.SecurityConfig")
                && componentes.contains("br.com.nexoimob.web.AuthController")
                && componentes.contains("br.com.nexoimob.services.UsuarioService")
                && componentes.contains("br.com.nexoimob.config.BootstrapAdmin"),
                "descoberta da seguranca, login, usuarios e administrador");

        Resource[] repositorios = recursos.getResources("classpath*:br/com/nexoimob/repositories/*.class");
        verificar(repositorios.length == 10, "descoberta dos 10 repositorios");
        System.out.println("TOTAL_EMPACOTAMENTO=5");
    }

    private static void verificar(boolean condicao, String nome) {
        if (!condicao) throw new IllegalStateException("Empacotamento invalido: " + nome);
        System.out.println("PASS empacotamento: " + nome);
    }
}
