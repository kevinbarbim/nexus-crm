package br.com.nexoimob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class NexoImobApplication {
    public static void main(String[] args) { java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/Sao_Paulo")); SpringApplication.run(NexoImobApplication.class, args); }
}
