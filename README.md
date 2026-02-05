# 🧠 Scheduling API
### *Sistema de Agendamento Multi-tenant com Arquitetura de Domínio Isolada*


Este projeto é uma implementação de referência para sistemas de agendamento de alta complexidade (Barbearias, Clínicas, Centros Estéticos). Ele demonstra como lidar com **regras de negócio dinâmicas**, **segurança defensiva** e **infraestrutura modular**, fugindo do padrão "CRUD simples" para focar em escalabilidade e manutenibilidade.

---

Cada módulo isola responsabilidade de negócio.

### Módulos principais

- `auth` → autenticação e registro
- `business` → gestão da empresa
- `user` → usuários e papéis
- `servicecatalog` → serviços oferecidos
- `staff` → profissionais
- `appointment` → núcleo do sistema
- `common` → segurança, configs e infraestrutura

---

## 🧱 Modelagem de Domínio

Entidades desenhadas para refletir operação real:

- **Business** → empresa e configurações
- **User** → clientes, staff e proprietários
- **ServiceCatalogItem** → serviços e duração
- **Staff** → profissionais e especialidades
- **Appointment** → agendamentos
- **WorkingHours** → horários de funcionamento

Relacionamentos incluem:

- Staff ↔ Services (Many-to-Many)
- Business ↔ Users
- Appointment ↔ Staff / Client / Service

---

## 💎 Diferenciais Técnicos

### 1. Arquitetura de Políticas (Strategy Pattern)
O sistema utiliza o **Strategy Pattern** para isolar as validações de agendamento em `SchedulingPolicy`. Isso permite que o comportamento da aplicação mude (ex: regras de cancelamento diferentes para uma barbearia vs uma escola) apenas injetando a política correta, mantendo o código aberto para extensão e fechado para modificação (**OCP - SOLID**).

### 2. Segurança de Contexto (Zero Trust em Parâmetros)
A API implementa segurança a nível de thread. Informações sensíveis, como o ID do usuário solicitante, são extraídas diretamente do **Security Context (JWT)**. Isso impede ataques de *Insecure Direct Object Reference* (IDOR), onde um usuário poderia tentar manipular dados de terceiros alterando IDs em requisições.

### 3. Confiabilidade com JUnit 5 & Mockito
A suíte de testes foca na **Pirâmide de Testes**, garantindo que as regras de negócio complexas e as integrações entre serviços sejam validadas em isolamento através de mocks, garantindo um código resiliente a refatorações.

### 4. Infraestrutura como Código (Docker & Compose)
O projeto é 100% "ready to run". Através do **Docker Compose**, o ambiente completo (Aplicação + Banco de Dados) é orquestrado de forma idêntica ao ambiente produtivo, eliminando o clássico problema do "na minha máquina funciona".

---

## ⚠️ Notas de Implementação e Segurança
Para fins de **demonstração técnica e facilidade de avaliação (POC)**, as credenciais de banco de dados e configurações de infraestrutura estão presentes no `application.properties`. 

> **Atenção:** Em um cenário de produção real, o projeto está preparado para consumir esses segredos via **Variáveis de Ambiente** ou **Secret Managers** (como AWS Secrets Manager ou HashiCorp Vault), seguindo as diretrizes do *12-Factor App* para garantir que dados sensíveis nunca sejam versionados.

---

## 🛠️ Stack Tecnológica
* **Linguagem:** Java 17+
* **Framework:** Spring Boot 3 (Data JPA, Security, Validation)
* **Segurança:** Stateless JWT Authentication
* **Testes:** JUnit 5, Mockito
* **Containerização:** Docker & Docker Compose
* **Banco de Dados:** PostgreSQL / H2 (Dev)

---

