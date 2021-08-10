# Invillia Reinvent - Sessao 7

Este tutorial apresenta o passo-a-passo para criação uma aplicação web 
usando Java e Spring Boot.

O objetivo é implementar uma API Rest da forma mais simples possível, fazendo o uso 
de uma dependência chamada `spring-boot-starter-web`. Esta dependência inclui o `spring-web`
e `spring-webmvc`, além de um servidor Tomcat que permite que a aplicação seja executada 
sem complicações.

## API 

- GET /product - Retorna todos os produtos.
- GET /product/{id} - Retorna um único produto.
- POST /product - Cria ou atualiza um novo produto.
- Payload de exemplo `
{
"id": "123",
"description": "teste 1",
"price": 99.80
}`

## Instalação do Ambiente de Desenvolvimento

Para executar esse projeto você precisa ter os seguintes softwares instalados:

- JDK 11+
- Gradle 7+
- Jetbrains IntelliJ 
- Docker 

**Observação**: Este tutorial não cobre o procedimento de instalação dos softwares que é uma premissa
para realização deste tutorial. Por ser softwares de uso comum dos desenvolvedores(as) Java, os procedimentos
de instalação são comuns e muito bem documentados nos sites dos fornecedores e também em outros sites especializados 
da internet.

### Testando a Instalação

Uma vez instalado os softwares podem ser testados executando os seguintes comandos no terminal:

`java --version`

`gradle -version`

`docker --version`

Todos ao serem executados deverão apresentar informações da versão instaladas no ambiente.


## Iniciando o Projeto 

### Gerando a estrutura inicial do projeto com o Spring Initializr 

O Spring Initializr é uma aplicação web que permite gerar a estrutura de um projeto base usando Spring Boot
com Maven ou Gradle.

Vamos utilizar o https://start.spring.io para gerar a estrutura inicial do projeto.

- Project: Gradle Project
- Language: Java
- Spring Boot: 2.5.3
- Project Metadata
  - Group: com.invillia.reinvent
  - Artifact: product-catalog
  - Name: product-catalog
  - Description: Product Catalog example for Invillia Reinvent
  - Package name: com.invillia.reinvent.productcatalog
- Packing: Jar
- Java: 11
- Dependencies:
  - Spring Web
  - Spring Data Elastic Search

Clique em `Generate` faça o download do arquivo .zip e descompacte o arquivo.

### Importanto o projeto no IntelliJ

1. Inicie um novo projeto vazio "Empty Project". Neste momento você precisa definir um diretório para armazenar 
todos os os arquivos do projeto;
2. Copie o diretório descompactado gerado pelo Spring Initializr no diretório do projeto; 
3. Clique em File > Project Structure;
4. No lista de ítem na parte esquerda da tela selecione `Modules`;
5. Clique no ícone `+` e em seguida em `Import Module`;
6. Selecione o diretório que fora descompactado e copiado para o diretório do projeto; e
7. Será apresentado um popup com a opção `Gradle` selecionado, confirme a importação.

O gradle levará alguns minutos para resolver as dependências do projeto e pronto.

### Executando o projeto

Procure na aplicação a classe com um método main e a anotação `@SpringBootApplication`. A classe
pode ser encontrada em `src\main\java\com\invillia\reinvent\productcatalog\ProductCatalogApplication`.

Abra a classe clicando duas vezes sobre o arquivo e note que o IntelliJ já reconhece como 
uma classe executável. 

Clique no ícone verde na linha de instrução do nome da classe e depois em `Run ProductCatalog...main()`

Após executar o Spring deverá soltar algumas informações no console. Se tudo ocorreu bem até este ponto você 
deverá ver as duas últimas linhas indicando que a aplicação esta ouvindo a porta 8080 e o tempo que levou para
a aplicação inicializar:

```
...
Tomcat started on port(s): 8080 (http) with context path ''
Started ProductCatalogApplication in 3.163 seconds (JVM running for 3.754)
```


## Iniciando a Implementação 

### Criando a Entidade `Produto`

Vamos começar criando a classe `Produto`. Essa classe representa um produto
no catálogo de produtos.

Vamos manter a simplicidade e criar uma classe com apenas três atributos: `id`, `description` e `price`. 
Os dois primeiros atributos são alfanuméricos então usaremos o tipo String, o último é um decimal portanto 
usaremos o tipo BigDecimal mais recomendado para valores monetários.

Antes de criar a classe você precisa criar o pacote onde a classe irá ser criado a seguir. 

Crie o pacote `entity` e o caminho completo do pacote ficará assim 
`src\main\java\com\invillia\reinvent\productcatalog\entity`:

A seguir crie a classe, inclua os atributos da classe com o modificador privado e gere os métodos 
`get` e `set` para a classe. Lembre-se que o IntelliJ pode te ajudar com a construção de parte do código,
mas se você é novo(a) em Java eu sugiro que você crie manualmente para ir se acostumando com a sintaxe 
da linguagem.

Para simplificar a construção de uma instância da classe Produto, vamos criar um método 
contrutor padrão para a classe com todos os atributos da classe como parâmetro.

Por fim, vamos também criar um método `toString()`. Este método será chamado toda vez 
que precisarmos imprimir o estado do objeto no console por exemplo.

Segue o código completo da classe produto:

```
package com.invillia.reinvent.productcatalog.entity;

import java.math.BigDecimal;

public class Product {

    private String id;
    private String description;
    private BigDecimal price;
    
    public Product(String id, String description, BigDecimal price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
```

### Criando o Controller

O nome controller tem origem na arquitetura MVC (Model View Controller). Controller são 
classes de objetos que possuem as seguintes responsabilidades:

1. Interceptar HTTP Requests enviados para a aplicação;
2. Converter o payload da requisição em uma estrutura interna de objetos;
3. Chamar a camada Model;
4. Receber o retorno do Model;
5. Enviar o resultado para a aplicação que fez a chamada HTTP Response.

Vamos iniciar com a implementação do método que deve retornar todos os produtos
catálogo.

Nesta fase do tutorial usaremos um `objeto mock` com o objetivo de simular um dado
real sendo obtido da camada de persistência da aplicação. Mais a frente substituiremos
esse código por uma integração com o repositório de dados.

Inicialmente a classe `ProductCatalogController` ficará da seguinte forma:

```
package com.invillia.reinvent.productcatalog.controller;

import com.invillia.reinvent.productcatalog.entity.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/product")
public class ProductCatalogController {

    @GetMapping
    public List<Product> listProducts() {

        List<Product> productList = new ArrayList<>() {{
           add(new Product("123", "Produto X", new BigDecimal(120.5)));
           add(new Product("125", "Produto Y", new BigDecimal(76.00)));
        }};

        return productList;
    }
}
```

Agora vamos implementar um segundo método na classe para tratar as requisições para 
um único produto. Note que diferente do método que implementamos anteriormente, este novo
método recebe como parâmetro identificador do produto.

```
    @GetMapping(value = "/{id}")
    public Product getProductById(@PathVariable String id) {
        return new Product("123", "Produto X", new BigDecimal(120.5));
    }
```

O último método implementado tratará a criação de um novo produto. Os dados do produto
serão fornecidos no corpo da requisição e obtidos através de um parâmetro do método.

```
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return product;
    }
```

## Persistência de Dados com Spring

### Elastic Search

Este tutorial utiliza o Elastic Search como repositório de dados, o mesmo que banco de dados. O Elastic Search
possui operações para indexar (adicionar ou atualizar) e consultar dados.

No Elastic Search os dados são organizados em classes de documentos (Document) e mantidos em índices (index). Fazendo
uma analogia com banco de dados relacional, o índice é equivalente a uma tabela, enquanto que o documento equivale a
um registro na tabela.

Ambos documento no caso do Elastic Search ou registro em um banco de dados relacional, possuem
campos (fields) que armazenam valores como o `código do produto` ou `nome do cliente`. Os valores possuem tipos diferentes,
`numérico` no caso do `código do produto` ou `alphanumérico`no caso do `nome do cliente`.

O Elastic Search possui um poderoso motor de busca, permitindo criar consultas complexas e personalizadas, sendo utilizado
por e-commerces que usam o Elastic Search para entregar produtos mais relevantes para o usuário.

Para simplificar, usaremos o Docker para instânciar do Elastic Search e mante-lo ouvindo na porta 9200.

Excute o seguinte comando no terminal do seu sistema operacional:

```
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.6.2
```

### Spring Data

Em uma aplicação Spring, o responsável por simplificar o acesso ao repositório de dados é o Spring Data. Sem o Spring
Data o desenvolvedor(a) teria que criar uma camada de acesso a dados também conhecido como DAO Layer. Normalmente,
a implementação de DAO requer gerar vários artefatos de configuração e controle de acesso tornando o código mais
verboso.

https://spring.io/projects/spring-data

O Spring Data visa eliminar a complexidade evitando o chamado boilerplate code. Por exemplo, se você definir uma
interface que extende ElasticsearchRepository, esta interface irá prover as operações CRUD, permitindo indexar e
consultar documentos no Elastic Search com chamadas simples para os métodos da interface. 


### Configurando o Elastic Search no Java com Spring

Para configurar o Elastic Search na nossa aplicação Java, precisamos definir como a aplicacão
vai se conectar com a instância do Elastic Search. 

Neste tutorial usaremos o `RestHighLevelClient`
que é uma das formas de conexão disponíveis quando adicionamos a dependência 
`org.springframework.boot:spring-boot-starter-data-elasticsearch` no projeto.

Crie o pacote `config` e o caminho completo do pacote ficará assim
`src\main\java\com\invillia\reinvent\productcatalog\config`.

No novo pacote cria uma classe chamada `ElasticSearchConfig` conforme exemplo abaixo:

```
package com.invillia.reinvent.productcatalog.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.invillia.reinvent.productcatalog.repository")

public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient elasticSearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticSearchClient());
    }
}
```

A anotação `@Configuration` informa ao Spring que a classe possui um ou mais métodos anotados
com `@Bean`. Essa é uma instrução que permite ao Spring injetar a instância dos objetos 
(retornados pelo método anotado) no contexto da aplicação. 

A anotação `@EnableElasticsearchRepositories` permite que o Spring escaneie o pacote 
fornecido e identifique as classes `Repositories`.

Finalmente através da configuração injetamos as instâncias de `RestHighLevelClient` e 
`ElasticsearchOperations`no contexto da aplicação. Isso é o suficiente para o Spring Data
conectar e executar operações no Elastic Search.


### Definindo Interfaces Repository

Para definir um novo repositório de dados, podemos extender uma das interfaces fornecidas pelo 
Spring Data Elastic Search. Neste tutorial vamos extender a interface `ElasticsearchRepository`
que fornece operações basicas de indexação e consulta aos dodos do ElasticSearch.

```
package com.invillia.reinvent.productcatalog.repository;

import com.invillia.reinvent.productcatalog.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}
```

### Mapeando Entidade como Documento

O mapeamento das entidade em documento permite definir a estrutura de dados que será persistida
no ElasticSearch. Mapeando a estrutura evitamos comportamentos indesejados na nossa aplicação. 

Abra a classe `Produto` e anote a classe com `@Document` fornecendo como parâmetro o nome do 
índice que será criado no ElasticSearch.

O atributo `id` deve ganhar a notação `@Id` assim o ElasticSearch saberá qual atributo deve
ser usado como identificador único do documento.

```
package com.invillia.reinvent.productcatalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

@Document(indexName = "product")
public class Product {

    @Id
    private String id;
    private String description;
    private BigDecimal price;
    ...
```

Agora precisamos mudar o código da nossa classe Controller, vamos substituir os `objetos mock`
por chamadas para a camada de persistência de dados.

Primeiramente é neessário injetar a instância do nosso repositório na classe. Para isso crie 
um atributo privado e anote-o com `@Autowired`. 

Por fim modifique o código dos três métodos conforme abaixo:

```
@RestController
@RequestMapping(path = "/product")
public class ProductCatalogController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> listProducts() {
        Iterable<Product> productIterable = productRepository.findAll();
        return StreamSupport.stream(productIterable.spliterator(),false).collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
         Optional<Product> productOptional = productRepository.findById(id);
         if (productOptional.isPresent()) {
             return ResponseEntity.ok().body(productOptional.get());
         }
         return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
}
```

## Conclusão

Neste tutorial utilizamos o Spring Boot para gerar uma aplicação back-end que expoe uma
API Rest para um catalogo de produtos. Exploramos ainda como conectar uma aplicação 
a um repositório de dados usando o Spring Data e Elastic Search.

O código utilizado neste tutorial esta disponível em https://github.com/oswaldoneto/invillia-reinvent-session-7
















