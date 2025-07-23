package com.leandrosnazareth.nfce_java.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nfce")
@Data
public class NfceConfiguration {
    
    private String ambiente;
    private Integer uf;
    private Integer serie;
    private Integer numeroInicial;
    
    private Certificado certificado = new Certificado();
    private Webservice webservice = new Webservice();
    private Qrcode qrcode = new Qrcode();
    
    @Data
    public static class Certificado {
        private String path;
        private String senha;
    }
    
    @Data
    public static class Webservice {
        private Url url = new Url();
        
        @Data
        public static class Url {
            private String autorizacao;
            private String retornoAutorizacao;
            private String consultaProtocolo;
            private String statusServico;
            private String consultaCadastro;
        }
    }
    
    @Data
    public static class Qrcode {
        private Url url = new Url();
        
        @Data
        public static class Url {
            private String consulta;
        }
    }
}
