# PetCare

Aplicação desktop de gerenciamento de banho e tosa, construída em Java e JavaFX.

## Requisitos

- JDK 17 ou superior
- Maven 3.8 ou superior
- PostgreSQL com o banco `petcare` configurado

## Executar

Na pasta do projeto, execute:

```powershell
mvn javafx:run
```

O Maven baixa as bibliotecas do JavaFX e do PostgreSQL declaradas em `pom.xml`. O ponto de entrada gráfico é `view.Main`; a antiga interface de terminal foi mantida no método `consoleMain` da mesma classe.

## Áreas disponíveis

- Login e cadastro de usuário
- Visão geral
- Consulta e manutenção de serviços
- Clientes e pets (perfil administrador)
- Solicitação e consulta de agendamentos
- Navegação preparada para Planos e Relatórios

Planos e Relatórios aparecem na navegação para acompanhar o protótipo, mas permanecem informativos até existirem operações e dados correspondentes no banco.

## Banco de dados

A aplicação espera o PostgreSQL local na porta `5432`, banco `petcare`. A configuração atual de usuário e senha permanece na classe `model.dao.Conexao`.
