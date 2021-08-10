# Invillia Reinvent - Sessao 7

Este tutorial mostra como criar uma aplicação web 
usando Java e Spring Boot.

O objetivo é implementar uma API Rest usando o máximo possível 
de recursos e dependências do Spring, permitindo que o desenvolvedor(a)
escreva menos código.


## Instalação

- JDK 11+
- Gradle 7+
- jetbrains IntelliJ 
- Docker 



## Iniciando o projeto 

### Gerando a estrutura inicial do projeto com o Spring Initializr 

https://start.spring.io/

Configuração da aplicação product-catalog


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

Clique em `Generate` faça o download do .zip.

Descompacte o .zip. 

### Importanto o projeto no IntelliJ

1. Inicie um novo projeto vazio "Empty Project". Neste momento você precisa definir um diretório para armazenar 
todos os os arquivos do projeto. 
2. Copie o diretório descompactado gerado pelo Spring Initializr no diretório do projeto. 
3. Clique em File > Project Structure
4. No lista de ítem na parte esquerda da tela selecione `Modules`;
5. Clique no ícone `+` e em seguida em `Import Module`;
6. Selecione o diretório que fora descompactado e copiado para o diretório do projeto. 
7. Será apresentado um popup com a opção `Gradle` selecionado, confirme a importação.

O gradle levará alguns minutos para resolver as dependências do projeto e pronto o projeto esta importado.

### Executando o projeto

Procure na aplicação a classe com um método main e a anotação @SpringBootApplication. A classe
pode ser encontrada em `src\main\java\com\invillia\reinvent\productcatalog\ProductCatalogApplication`.

Abra a classe clicando duas vezes sobre o arquivo e note que o IntelliJ já reconhece como 
uma classe de execução (runtime). 

Clique no ícone verde na linha de instrução do nome da classe e depois em `Run ProductCatalog...main()`

Após executar o Spring deverá soltar algumas informações no console. Se tudo ocorreu bem até este ponto você 
deverá ver as duas últimas linhas indicando que a aplicação esta ouvindo a porta 8080 e o tempo que levou para
a aplicação subir:

```
Tomcat started on port(s): 8080 (http) with context path ''
Started ProductCatalogApplication in 3.163 seconds (JVM running for 3.754)
```


## Iniciando a implementação 

### Criando a entidade

Vamos começar criando a classe `Produto`. Essa classe representa um produto
no catálogo de produtos.

A classe deve ser criada em `src\main\java\com\invillia\reinvent\productcatalog\entity`:

```
package com.invillia.reinvent.productcatalog.entity;

import java.math.BigDecimal;

public class Product {

    private String id;
    private String description;
    private BigDecimal price;

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
A classe `Produto` deve ter os seguintes atributos:
- id - Identificador único para o produto
- description - Descrição do produto
- price - Preço unitário do produto

### Criando o controller

Criando a classe de Controller

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
    public List<Product> list() {

        List<Product> productList = new ArrayList<>() {{
           add(new Product("123", "Produto X", new BigDecimal(120.5)));
           add(new Product("125", "Produto Y", new BigDecimal(76.00)));
        }};

        return productList;
    }
}
```

Implementar o resource

```
    @GetMapping(value = "/{id}")
    public Product getProductById(@PathVariable String id) {
        return new Product("123", "Produto X", new BigDecimal(120.5));
    }
```

Teste

```
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return product;
    }
```

## Persistência do dado

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


### Configurando o Elastic Search na aplicação Java com Spring

Para fazer a aplicação Java com Spring se conectar ao Elastic Search é necessário
criar uma configuração (Configuration).

Crie um classe chamada `ElasticSearchConfig`:

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

Repository class:

```
package com.invillia.reinvent.productcatalog.repository;

import com.invillia.reinvent.productcatalog.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}
```

Anote a classe `Produto`

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

Conectando com o repositório

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








