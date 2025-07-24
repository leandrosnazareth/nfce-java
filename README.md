# NFC-e Java - Sistema Integrador para Nota Fiscal de Consumidor Eletrônica

## Sobre o Projeto

Integração com NFC-e (Nota Fiscal de Consumidor Eletrônica) desenvolvido em Java com Spring Boot.

## Funcionalidades

### Funcionalidades Implementadas

- **Geração de NFC-e**: Criação completa de notas fiscais eletrônicas
- **Chave de Acesso**: Geração e validação automática da chave de acesso
- **QR Code**: Geração de QR Code para consulta da NFC-e
- **XML**: Geração de XML conforme layout oficial da SEFAZ
- **Validações**: Validação completa dos dados de entrada
- **API REST**: Endpoints completos para todas as operações
- **Documentação**: Swagger/OpenAPI integrado
- **Banco de Dados**: Persistência com JPA/Hibernate
- **Numeração**: Controle automático de numeração sequencial
- **Status**: Controle de status da NFC-e (Rascunho → Assinada → Enviada → Autorizada)

### Fluxo de Processamento

1. **Criação**: Criação da NFC-e com validação dos dados
2. **Assinatura**: Assinatura digital do XML (simulada)
3. **Envio**: Transmissão para a SEFAZ (simulada)
4. **Autorização**: Recebimento da autorização (simulada)
5. **Cancelamento**: Cancelamento de notas autorizadas (simulado)

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **Spring Web**
- **Lombok**
- **H2 Database** (desenvolvimento)
- **Jackson XML**
- **Apache HttpClient**
- **BouncyCastle** (criptografia)
- **ZXing** (QR Code)
- **SpringDoc OpenAPI** (Swagger)
- **JUnit 5** (testes)

## Estrutura do Projeto

```
src/main/java/com/leandrosnazareth/nfce_java/
├── config/           # Configurações
│   ├── NfceConfiguration.java
│   └── SwaggerConfig.java
├── controller/       # Controllers REST
│   ├── NfceController.java
│   └── QrCodeController.java
├── exception/        # Tratamento de exceções
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   └── ValidationErrorResponse.java
├── model/           # Modelos de dados
│   ├── dto/         # DTOs
│   │   ├── NfceRequestDto.java
│   │   └── NfceResponseDto.java
│   └── entity/      # Entidades JPA
│       ├── Nfce.java
│       ├── ItemNfce.java
│       └── PagamentoNfce.java
├── repository/      # Repositórios
│   └── NfceRepository.java
└── service/         # Serviços
    ├── NfceService.java
    ├── ChaveAcessoService.java
    ├── QrCodeService.java
    ├── XmlService.java
    └── NumeracaoService.java
```

## Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior

### Executando a aplicação

1. **Clone o repositório:**
```bash
git clone <url-do-repositorio>
cd nfce-java
```

2. **Execute a aplicação:**
```bash
mvn spring-boot:run
```

3. **Acesse a aplicação:**
- API: http://localhost:8080/nfce-api
- Swagger UI: http://localhost:8080/nfce-api/swagger-ui/index.html
- H2 Console: http://localhost:8080/nfce-api/h2-console

## Documentação da API

### Endpoints Principais

#### NFC-e

- `POST /api/v1/nfce` - Criar nova NFC-e
- `GET /api/v1/nfce/{id}` - Buscar NFC-e por ID
- `GET /api/v1/nfce/chave/{chaveAcesso}` - Buscar por chave de acesso
- `GET /api/v1/nfce/status/{status}` - Buscar por status
- `GET /api/v1/nfce/emitente/{cnpj}` - Buscar por emitente
- `POST /api/v1/nfce/{id}/assinar` - Assinar NFC-e
- `POST /api/v1/nfce/{id}/enviar` - Enviar NFC-e
- `POST /api/v1/nfce/{id}/autorizar` - Autorizar NFC-e
- `POST /api/v1/nfce/{id}/cancelar` - Cancelar NFC-e
- `GET /api/v1/nfce/numeracao/proxima/{serie}` - Próximo número

#### QR Code

- `GET /api/v1/qrcode/gerar` - Gerar URL do QR Code
- `GET /api/v1/qrcode/imagem` - Gerar imagem PNG
- `GET /api/v1/qrcode/imagem-base64` - Gerar imagem Base64
- `POST /api/v1/qrcode/validar` - Validar QR Code

## Exemplo de Uso

### Criando uma NFC-e

```bash
curl -X POST "http://localhost:8080/nfce-api/api/v1/nfce" \
  -H "Content-Type: application/json" \
  -d @src/main/resources/exemplos/nfce-request-exemplo.json
```

### Resposta de Sucesso

```json
{
  "id": 1,
  "numero": 123,
  "serie": 1,
  "chave_acesso": "35240112345678000195650010000001231000000010",
  "data_emissao": "2024-01-15T10:30:00",
  "natureza_operacao": "Venda de mercadoria",
  "status": "RASCUNHO",
  "qr_code": "https://www.fazenda.sp.gov.br/nfce/qrcode?p=...",
  "url_consulta": "https://www.fazenda.sp.gov.br/nfce/qrcode?p=...",
  "emitente": {
    "cnpj": "12345678000195",
    "razao_social": "EMPRESA EXEMPLO LTDA"
  },
  "totais": {
    "valor_total_produtos": 66.75,
    "valor_total_nota": 65.25,
    "valor_desconto": 1.00
  }
}
```

## Configuração

### application.properties

As principais configurações podem ser ajustadas no arquivo `application.properties`:

```properties
# Ambiente da NFC-e
nfce.ambiente=homologacao
nfce.uf=35
nfce.serie=1
nfce.numero-inicial=1

# URLs dos webservices (podem ser alteradas conforme UF)
nfce.webservice.url.autorizacao=https://homologacao.nfce.fazenda.sp.gov.br/ws/nfceautorizacao.asmx
```

## Testes

Execute os testes com:

```bash
mvn test
```

Os testes incluem:
- Validação da geração de chave de acesso
- Validação de QR Code
- Testes de integração da API

## Status da NFC-e

A NFC-e passa pelos seguintes status:

1. **RASCUNHO** - NFC-e criada, mas não assinada
2. **ASSINADA** - NFC-e assinada digitalmente
3. **ENVIADA** - NFC-e enviada para a SEFAZ
4. **AUTORIZADA** - NFC-e autorizada pela SEFAZ
5. **REJEITADA** - NFC-e rejeitada pela SEFAZ
6. **CANCELADA** - NFC-e cancelada
7. **DENEGADA** - NFC-e denegada pela SEFAZ

## Segurança

- Validação completa de dados de entrada
- Tratamento de exceções personalizado
- Logs estruturados para auditoria
- Validação de chave de acesso com dígito verificador

## Roadmap

### Futuras Implementações

- [ ] Assinatura digital real com certificado A1/A3
- [ ] Integração real com webservices da SEFAZ
- [ ] Geração de DANFE (PDF)
- [ ] Consulta de cadastro de contribuinte
- [ ] Manifestação do destinatário
- [ ] Carta de correção eletrônica
- [ ] Eventos de NFC-e
- [ ] Backup automático de XMLs
- [ ] Dashboard de monitoramento
- [ ] Múltiplas empresas (multi-tenant)

---
 **Se este projeto foi útil para você, considere dar uma estrela no GitHub!**
