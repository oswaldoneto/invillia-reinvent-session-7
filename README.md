# invillia-reinvent-session-7



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

### Importanto o projeto no Intellij

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

Abra a classe clicando duas vezes sobre o arquivo e note que o Intellij já reconhece como 
uma classe de execução (runtime). 

Clique no ícone verde na linha de instrução do nome da classe e depois em `Run ProductCatalog...main()`

Após executar o Spring deverá soltar algumas informações no console. Se tudo ocorreu bem até este ponto você 
deverá ver as duas últimas linhas indicando que a aplicação esta ouvindo a porta 8080 e o tempo que levou para
a aplicação subir:

```
Tomcat started on port(s): 8080 (http) with context path ''
Started ProductCatalogApplication in 3.163 seconds (JVM running for 3.754)
```






















 






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









