import br.com.nexoimob.entities.*;
import br.com.nexoimob.repositories.*;
import br.com.nexoimob.services.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.bind.*;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerificarRevisao {
  static int passed=0;
  static void ok(String name, boolean value) {
    if (!value) throw new AssertionError(name);
    System.out.println("PASS " + (++passed) + " " + name);
  }
  static void rejects(String name, Runnable call) {
    try {call.run();} catch(IllegalArgumentException e) {ok(name,true); return;}
    throw new AssertionError(name);
  }
  @SuppressWarnings("unchecked")
  static <T> T proxy(Class<T> type, java.util.function.BiFunction<String,Object[],Object> impl) {
    return (T) Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},(p,m,a)->{
      if(m.getName().equals("toString")) return type.getSimpleName()+"AuditDouble";
      return impl.apply(m.getName(),a);
    });
  }
  static class Sender extends JavaMailSenderImpl {
    SimpleMailMessage last;
    @Override public void send(SimpleMailMessage message) {
      last=new SimpleMailMessage(message);
      super.send(message);
    }
  }
  public static void main(String[] args) throws Exception {
    BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    SenhaUtil.validar("MinhaSenhaSegura2026!"); ok("senha valida aceita",true);
    rejects("senha curta recusada",()->SenhaUtil.validar("123456789"));
    rejects("senha acima de 72 bytes recusada",()->SenhaUtil.validar("é".repeat(37)));
    ok("SHA256 conhecido",PasswordService.hash("abc").equals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"));
    Map<Long,Usuario> users=new HashMap<>();
    Usuario u=new Usuario(); u.setId(1L);u.setEmail("pessoa@example.test");u.setNome("Teste");
    u.setAtivo(true);u.setPerfil(Perfil.CORRETOR);users.put(1L,u);
    UsuarioRepository ur=proxy(UsuarioRepository.class,(m,a)->switch(m){
      case "findByEmailIgnoreCase"->users.values().stream().filter(x->x.getEmail().equalsIgnoreCase((String)a[0])).findFirst();
      case "findById"->Optional.ofNullable(users.get((Long)a[0]));
      case "save","saveAndFlush"->{Usuario x=(Usuario)a[0];if(x.getId()==null)x.setId((long)users.size()+1);users.put(x.getId(),x);yield x;}
      default->throw new UnsupportedOperationException(m);
    });
    Map<String,TokenRecuperacao> ts=new LinkedHashMap<>();
    TokenRecuperacaoRepository tr=proxy(TokenRecuperacaoRepository.class,(m,a)->switch(m){
      case "save"->{TokenRecuperacao t=(TokenRecuperacao)a[0];ts.put(t.getTokenHash(),t);yield t;}
      case "findByTokenHash"->Optional.ofNullable(ts.get((String)a[0]));
      case "findByUsuarioIdAndUsadoFalse"->ts.values().stream().filter(t->!t.isUsado() && t.getUsuario().getId().equals(a[0])).toList();
      default->throw new UnsupportedOperationException(m);
    });
    DefaultListableBeanFactory beans=new DefaultListableBeanFactory();
    PasswordService ps=new PasswordService(ur,tr,encoder,beans.getBeanProvider(JavaMailSender.class),"http://localhost:8080","nexus@example.test");
    rejects("SMTP ausente acusa configuracao",()->ps.solicitar(u.getEmail()));
    ps.solicitar("ausente@example.test");ok("email inexistente termina sem envio",true);
    u.setAtivo(false);ps.solicitar(u.getEmail());ok("conta inativa termina sem envio",true);u.setAtivo(true);
    ts.clear();
    Sender sender=new Sender();sender.setHost("127.0.0.1");sender.setPort(Integer.parseInt(args[0]));
    sender.setDefaultEncoding("UTF-8");
    sender.getJavaMailProperties().setProperty("mail.smtp.connectiontimeout","3000");
    sender.getJavaMailProperties().setProperty("mail.smtp.timeout","3000");
    beans.registerSingleton("mailSender",sender);
    ps.solicitar(" PESSOA@example.test ");
    ok("solicitacao envia por SMTP local",sender.last!=null && ts.size()==1);
    ok("remetente e destinatario corretos",sender.last.getFrom().equals("nexus@example.test") && sender.last.getTo()[0].equals(u.getEmail()));
    String raw=sender.last.getText().split("token=",2)[1];
    ok("hash guardado em vez do token",ts.containsKey(PasswordService.hash(raw)) && !ts.containsKey(raw));
    TokenRecuperacao token=ts.get(PasswordService.hash(raw));
    ok("expiracao de 15 minutos",Duration.between(LocalDateTime.now(),token.getExpiraEm()).toSeconds()>890);
    ps.redefinir(raw,"OutraSenhaSegura2026!");
    ok("nova senha usa BCrypt",encoder.matches("OutraSenhaSegura2026!",u.getSenhaHash()));
    ok("token marcado usado",token.isUsado());
    rejects("reutilizacao de link recusada",()->ps.redefinir(raw,"UmaTerceiraSenha2026!"));
    rejects("link inexistente recusado",()->ps.redefinir("nao-existe","UmaTerceiraSenha2026!"));
    token.setUsado(false);token.setExpiraEm(LocalDateTime.now().minusSeconds(1));
    rejects("link expirado recusado",()->ps.redefinir(raw,"UmaTerceiraSenha2026!"));
    token.setExpiraEm(LocalDateTime.now().plusMinutes(15));u.setAtivo(false);
    rejects("link de conta desativada recusado",()->ps.redefinir(raw,"UmaTerceiraSenha2026!"));u.setAtivo(true);
    ts.clear();ps.solicitar(u.getEmail());String link1=sender.last.getText().split("token=",2)[1];
    ps.solicitar(u.getEmail());String link2=sender.last.getText().split("token=",2)[1];
    Usuario outro=new Usuario();outro.setId(99L);
    TokenRecuperacao outroToken=new TokenRecuperacao();outroToken.setUsuario(outro);outroToken.setTokenHash("outro-usuario");
    ts.put(outroToken.getTokenHash(),outroToken);
    ps.redefinir(link2,"SenhaMaisRecente2026!");
    rejects("link anterior invalidado apos outra redefinicao",()->ps.redefinir(link1,"SenhaLinkAnterior2026!"));
    ok("tentativa antiga preserva senha mais recente",encoder.matches("SenhaMaisRecente2026!",u.getSenhaHash()));
    ok("tokens de outros usuarios preservados",!outroToken.isUsado());
    UsuarioService us=new UsuarioService(ur,encoder,null);
    us.cadastrar(" Novo Corretor ","NOVO@example.test","SenhaCadastro2026!");
    Usuario novo=users.get(2L);
    ok("cadastro publico cria corretor inativo",!novo.isAtivo() && novo.getPerfil()==Perfil.CORRETOR);
    ok("cadastro normaliza email e nome",novo.getEmail().equals("novo@example.test") && novo.getNome().equals("Novo Corretor"));
    ok("cadastro guarda BCrypt",encoder.matches("SenhaCadastro2026!",novo.getSenhaHash()));
    rejects("cadastro duplicado recusado",()->us.cadastrar("Outro","novo@example.test","SenhaCadastro2026!"));
    var login=new UsuarioLoginService(ur).loadUserByUsername(u.getEmail());
    ok("sessao identificada pelo ID",login.getUsername().equals("1"));
    u.setAtivo(false);ok("login marca conta inativa",!new UsuarioLoginService(ur).loadUserByUsername(u.getEmail()).isEnabled());
    StandardEnvironment env=new StandardEnvironment();
    env.getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,Map.of(
      "SPRING_MAIL_HOST","smtp.gmail.com","SPRING_MAIL_PORT","587",
      "SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH","true",
      "SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE","true")));
    ConfigurationPropertySources.attach(env);
    MailProperties bound=Binder.get(env).bind("spring.mail",Bindable.of(MailProperties.class)).get();
    ok("variaveis SMTP do README vinculam host e porta",bound.getHost().equals("smtp.gmail.com") && bound.getPort()==587);
    System.out.println("BOUND_MAIL_PROPERTIES="+bound.getProperties());
    ok("variaveis SMTP do README vinculam AUTH","true".equals(bound.getProperties().get("mail.smtp.auth")));
    ok("variaveis SMTP do README vinculam STARTTLS","true".equals(bound.getProperties().get("mail.smtp.starttls.enable")));
    var handler=new br.com.nexoimob.web.CommonModel(null);
    java.lang.reflect.Method method=handler.getClass().getDeclaredMethod("falhaBanco",org.springframework.dao.DataAccessException.class,org.springframework.ui.Model.class,jakarta.servlet.http.HttpServletResponse.class);
    method.setAccessible(true);
    int[] status={0};
    var response=proxy(jakarta.servlet.http.HttpServletResponse.class,(name,a)->{if(name.equals("setStatus"))status[0]=(int)a[0];return null;});
    var model=new org.springframework.ui.ExtendedModelMap();
    Object view=method.invoke(handler,new org.springframework.jdbc.UncategorizedSQLException("aceitar","EXEC",new java.sql.SQLException("Imóvel indisponível.","S0001",51033)),model,response);
    ok("erro SQL de regra usa pagina controlada",view.equals("error") && status[0]==409);
    ok("mensagem de regra SQL preservada",model.get("message").equals("Imóvel indisponível."));
    method.invoke(handler,new org.springframework.dao.DataAccessResourceFailureException("detalhe tecnico interno"),model,response);
    ok("falha de banco usa 503 sem expor detalhe",status[0]==503 && !model.get("message").toString().contains("interno"));
    var tree=new org.springframework.data.repository.query.parser.PartTree("findByUsuarioIdAndUsadoFalse",TokenRecuperacao.class);
    ok("consulta derivada de tokens reconhecida pelo Spring",tree.getParts().stream().count()==2);
    String seed=java.nio.file.Files.readString(java.nio.file.Path.of(args[1]));
    var hash=java.util.regex.Pattern.compile("\\$2b\\$10\\$[A-Za-z0-9./]{53}").matcher(seed);
    ok("senha demonstracao confere com hash SQL",hash.find() && encoder.matches("NexoDemo2026!",hash.group()));
    System.out.println("TOTAL_PASS="+passed);
  }
}
