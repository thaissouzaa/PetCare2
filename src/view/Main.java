package view;

import controller.UsuarioController;
import controller.ServicoController;
import controller.AgendaController;
import controller.ClienteController;
import model.dao.Animal;
import model.dao.Servico;
import model.dao.Usuario;
import model.dao.Pessoa;
import model.dao.Raca;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //Cria um objeto responsável por chamar as operações relacionadas aos usuários e serviços

        UsuarioController usuarioController = new UsuarioController();
        ServicoController servicoController = new ServicoController();
        AgendaController agendaController = new AgendaController();
        ClienteController clienteController = new ClienteController();

        Scanner sc = new Scanner(System.in);

        //MENU INICIAL

        System.out.println("1 - Fazer Login");
        System.out.println("2 - Cadastrar Novo Usuário");
        System.out.println("3 - Sair");
        int opcao = sc.nextInt();
        sc.nextLine();


        switch (opcao) {

            case 1:
                if (usuarioController.conectaBD("petcare")) {

                    do {
                        System.out.println("LOGIN\n");

                        System.out.println("Login (CPF ou E-mail)");
                        String login = sc.next();

                        System.out.println("Senha ");
                        String senha = sc.next();

                        Usuario usuarioLogin = new Usuario(login, senha);

                        if (usuarioController.VerificarLogin("usuario", usuarioLogin)) {

                            System.out.println("Login efetuado com sucesso\n");

                            boolean Master = usuarioController.UsuarioMaster("usuario", usuarioLogin);

                            do {
                                System.out.println("\n===== MENU PETCARE =====");

                                if (Master) {
                                    System.out.println("1 - Gerenciar serviços");
                                }

                                System.out.println("2 - Serviços");

                                if (Master) {
                                    System.out.println("3 - Listar agendamentos de todos os clientes");
                                    System.out.println("4 - Gerenciar clientes e pets");
                                } else {
                                    System.out.println("3 - Adicionar agendamento");
                                    System.out.println("4 - Retirar agendamento");
                                    System.out.println("5 - Confirmar agendamento");
                                }

                                System.out.println("0 - Sair");
                                System.out.print("Escolha uma opção: ");

                                opcao = sc.nextInt();
                                sc.nextLine();

                                switch (opcao) {

                                    case 1:
                                        if (Master) {
                                            gerenciarServicos(sc, servicoController);
                                        } else {
                                            System.out.println("Usuário sem permissão de acesso!");
                                        }
                                        break;

                                    case 2:
                                        System.out.println("Buscar serviço");
                                        if (servicoController.conectaBD("petcare")) {
                                            ArrayList<Servico> servicos = servicoController.SelecionarTodosServicos();
                                            if (servicos == null || servicos.isEmpty()) {
                                                System.out.println("Nenhum serviço ativo encontrado.");
                                            } else {
                                                System.out.println("\n===== SERVIÇOS DISPONÍVEIS =====");
                                                for (Servico servico : servicos) {
                                                    System.out.println("\nCódigo: " + servico.getCodigo() +
                                                            "\nNome: " + servico.getNome() +
                                                            "\nDescrição: " + servico.getDescricao() +
                                                            "\nDuração: " + servico.getDuracao() + " minutos" +
                                                            "\nValor: " + formatarValor(servico.getValor()));
                                                }
                                            }
                                        } else {
                                            System.out.println("Erro ao conectar ao banco de dados.");
                                        }
                                        break;

                                    case 3:
                                        if (Master) {
                                            System.out.println("\n===== AGENDAMENTOS DE TODOS OS CLIENTES =====");
                                            if (agendaController.conectaBD("petcare")) {
                                                ArrayList<String> todosAgendamentos = agendaController.SelecionarTodosAgendamentos();
                                                if (todosAgendamentos.isEmpty()) System.out.println("Nenhum agendamento encontrado.");
                                                else for (String agendamento : todosAgendamentos) System.out.println(agendamento);
                                            } else System.out.println("Erro ao conectar ao banco de dados.");
                                        } else {
                                            System.out.println("Adicionar agendamento");
                                            if (servicoController.conectaBD("petcare")) {
                                                ArrayList<Servico> servicos = servicoController.SelecionarTodosServicos();
                                                if (servicos == null || servicos.isEmpty()) System.out.println("Nenhum serviço ativo disponível.");
                                                else {
                                                    for (Servico servico : servicos) System.out.println("Código: " + servico.getCodigo() + " | " + servico.getNome() + " | Duração: " + servico.getDuracao() + " min | " + formatarValor(servico.getValor()));
                                                    System.out.print("Código do serviço: ");
                                                    int codigoServico = Integer.parseInt(sc.nextLine());
                                                    System.out.print("Data e hora inicial (DD/MM/AAAA HH:MM): ");
                                                    String inicio = sc.nextLine();
                                                    if (agendaController.conectaBD("petcare")) System.out.println(agendaController.InserirAgendamento(usuarioLogin.getLogin(), inicio, codigoServico) ? "Agendamento solicitado! Aguarde a confirmação." : "Não foi possível adicionar o agendamento. Confira a data/hora e o serviço escolhido.");
                                                    else System.out.println("Erro ao conectar ao banco de dados.");
                                                }
                                            } else System.out.println("Erro ao conectar ao banco de dados.");
                                        }
                                        break;

                                    case 4:
                                        if (Master) {
                                            gerenciarClientesEPets(sc, clienteController);
                                        } else {
                                            System.out.println("Retirar agendamento");
                                            if (agendaController.conectaBD("petcare")) {
                                                ArrayList<String> agendamentos = agendaController.SelecionarAgendamentos(usuarioLogin.getLogin());
                                                if (agendamentos.isEmpty()) System.out.println("Nenhum agendamento encontrado.");
                                                else {
                                                    for (String agendamento : agendamentos) System.out.println(agendamento);
                                                    System.out.print("Código do agendamento a retirar: ");
                                                    int codigo = Integer.parseInt(sc.nextLine());
                                                    System.out.println(agendaController.RemoverAgendamento(usuarioLogin.getLogin(), codigo) ? "Agendamento retirado com sucesso!" : "Não foi possível retirar o agendamento.");
                                                }
                                            } else System.out.println("Erro ao conectar ao banco de dados.");
                                        }
                                        break;

                                    case 5:
                                        if (!Master) {
                                            System.out.println("Confirmar agendamento");
                                            if (agendaController.conectaBD("petcare")) {
                                                ArrayList<String> agendamentos = agendaController.SelecionarAgendamentos(usuarioLogin.getLogin());
                                                if (agendamentos.isEmpty()) System.out.println("Nenhum agendamento encontrado.");
                                                else {
                                                    for (String agendamento : agendamentos) System.out.println(agendamento);
                                                    System.out.print("Código do agendamento a confirmar: ");
                                                    int codigo = Integer.parseInt(sc.nextLine());
                                                    System.out.println(agendaController.AtualizarStatusAgenda(usuarioLogin.getLogin(), codigo, true) ? "Agendamento confirmado!" : "Não foi possível confirmar o agendamento.");
                                                }
                                            } else System.out.println("Erro ao conectar ao banco de dados.");
                                        } else System.out.println("Opção inválida.");
                                        break;

                                    case 6:
                                        System.out.println("Opção inválida.");
                                        break;

                                    case 0:
                                        System.out.println("Saindo...");
                                        break;

                                    default:
                                        System.out.println("Opção inválida.");
                                }

                            } while (opcao != 0);

                            break;

                        } else {
                            System.out.println("Login e/ou senha inválido.");
                        }

                    } while (true);

                    break;

                } else {
                    System.out.println("Erro ao conectar ao banco de dados.");
                }

                break;

                //CADASTRO USUARIO

            case 2:

                if (usuarioController.conectaBD("petcare")) {

                    do {

                        System.out.println("Nome Completo:");
                        String nome = sc.nextLine();

                        System.out.println("CPF: ");
                        String CPF = sc.nextLine();
                        CPF = CPF.replace(".", "").replace("-", "");

                        System.out.println("Telefone: ");
                        String telefone = sc.nextLine();
                        telefone = telefone.replace("(", "")
                                .replace(")", "")
                                .replace("-", "")
                                .replace(" ", "");

                        System.out.println("Email: ");
                        String email = sc.nextLine();

                        System.out.println("CEP: ");
                        String cep = sc.nextLine();
                        cep = cep.replace("-", "");

                        System.out.println("Cidade: ");
                        String cidade = sc.nextLine();

                        System.out.println("Endereco: ");
                        String endereco = sc.nextLine();

                        System.out.println("Senha: ");
                        String senha = sc.nextLine();

                        System.out.println("Confirme sua senha: ");
                        String confirmacaoSenha = sc.nextLine();

                        if (!senha.equals(confirmacaoSenha)) {
                            System.out.println("As senhas não coincidem.");
                        } else {


                            //pega os dados e cria um obj pessoa

                            Pessoa pessoa = new Pessoa(nome, CPF, email, telefone, endereco, cidade, cep);


                            Usuario usuario = new Usuario(CPF, senha);

                            if (usuarioController.VerificarUsuarioExiste("usuario", usuario)) {

                                System.out.println("Usuário já cadastrado! Digite novamente.");

                            } else {

                                String NovoUsuario = usuarioController.InserirUsuario("usuario", usuario, pessoa);
                                System.out.println(NovoUsuario);

                                break;

                            }

                        }

                    }
                    while (true) ;

                } else{
                    System.out.println("Erro ao conectar ao banco de dados.");
                }

                break;

            case 3:
                System.out.println("Programa encerrado.");
                break;

        }
    }

    private static String formatarValor(String valor) {
        String valorLimpo = valor.replace("R$", "").replace("\u00a0", "").replace(" ", "").trim();
        if (valorLimpo.contains(",")) valorLimpo = valorLimpo.replace(".", "").replace(',', '.');
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                .format(new BigDecimal(valorLimpo));
    }

    private static void gerenciarServicos(Scanner sc, ServicoController controller) {
        while (true) {
            System.out.println("\n===== GERENCIAR SERVIÇOS =====");
            System.out.println("1 - Listar serviços");
            System.out.println("2 - Consultar detalhes de um serviço");
            System.out.println("3 - Cadastrar serviço");
            System.out.println("4 - Editar serviço");
            System.out.println("5 - Excluir serviço");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine();
            if (opcao.equals("0")) return;

            switch (opcao) {
                case "1":
                    if (controller.conectaBD("petcare")) listarServicos(controller);
                    else System.out.println("Erro ao conectar ao banco de dados.");
                    break;
                case "2":
                    if (!controller.conectaBD("petcare")) { System.out.println("Erro ao conectar ao banco de dados."); break; }
                    if (listarServicos(controller).isEmpty()) break;
                    System.out.print("Código do serviço: ");
                    System.out.println(controller.BuscarServico(sc.nextLine()));
                    break;
                case "3": {
                    System.out.print("Nome: "); String nome = sc.nextLine();
                    System.out.print("Descrição: "); String descricao = sc.nextLine();
                    System.out.print("Duração em minutos: "); String duracao = sc.nextLine();
                    System.out.print("Valor em reais (ex.: 25,50): "); String valor = sc.nextLine();
                    if (controller.conectaBD("petcare")) System.out.println(controller.InserirServico(new Servico(nome, descricao, duracao, valor)));
                    else System.out.println("Erro ao conectar ao banco de dados.");
                    break;
                }
                case "4": {
                    if (!controller.conectaBD("petcare")) { System.out.println("Erro ao conectar ao banco de dados."); break; }
                    if (listarServicos(controller).isEmpty()) break;
                    System.out.print("Código do serviço que deseja editar: ");
                    int codigo = lerInteiro(sc.nextLine());
                    System.out.print("Novo nome: "); String nome = sc.nextLine();
                    System.out.print("Nova descrição: "); String descricao = sc.nextLine();
                    System.out.print("Nova duração em minutos: "); String duracao = sc.nextLine();
                    System.out.print("Novo valor em reais (ex.: 25,50): "); String valor = sc.nextLine();
                    System.out.println(controller.AtualizarServico(new Servico(codigo, nome, descricao, duracao, valor)) ? "Serviço atualizado com sucesso!" : "Não foi possível atualizar o serviço.");
                    break;
                }
                case "5": {
                    if (!controller.conectaBD("petcare")) { System.out.println("Erro ao conectar ao banco de dados."); break; }
                    if (listarServicos(controller).isEmpty()) break;
                    System.out.print("Código do serviço que deseja excluir: ");
                    int codigo = lerInteiro(sc.nextLine());
                    System.out.print("Confirma a exclusão deste serviço? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        System.out.println(controller.removerServico(new Servico(codigo, "", "", "", "")) ? "Serviço excluído com sucesso!" : "Não foi possível excluir o serviço.");
                    }
                    break;
                }
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static ArrayList<Servico> listarServicos(ServicoController controller) {
        ArrayList<Servico> servicos = controller.SelecionarTodosServicos();
        if (servicos == null) servicos = new ArrayList<>();
        if (servicos.isEmpty()) System.out.println("Nenhum serviço ativo encontrado.");
        else {
            System.out.println("\n===== SERVIÇOS ATIVOS =====");
            for (Servico servico : servicos) {
                System.out.println("Código: " + servico.getCodigo() + " | " + servico.getNome() +
                        " | Duração: " + servico.getDuracao() + " min | " + formatarValor(servico.getValor()));
            }
        }
        return servicos;
    }

    private static void gerenciarClientesEPets(Scanner sc, ClienteController controller) {
        while (true) {
            System.out.println("\n===== GERENCIAR CLIENTES E PETS =====");
            System.out.println("1 - Listar clientes");
            System.out.println("2 - Ver cliente e seus pets");
            System.out.println("3 - Editar cliente");
            System.out.println("4 - Excluir cliente e desativar seus pets");
            System.out.println("5 - Cadastrar pet para um cliente");
            System.out.println("6 - Editar pet");
            System.out.println("7 - Excluir pet");
            System.out.println("0 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine();
            if (opcao.equals("0")) return;

            if (!controller.conectaBD("petcare")) {
                System.out.println("Erro ao conectar ao banco de dados.");
                continue;
            }

            switch (opcao) {
                case "1":
                    listarClientes(controller);
                    break;
                case "2": {
                    Pessoa cliente = selecionarCliente(sc, controller);
                    if (cliente != null) {
                        exibirCliente(cliente);
                        exibirPets(controller.SelecionarPetsCliente(cliente.getCodigo()));
                    }
                    break;
                }
                case "3": {
                    Pessoa cliente = selecionarCliente(sc, controller);
                    if (cliente == null) break;
                    System.out.println("Editando: " + cliente.getNome() + " (CPF " + cliente.getCpf() + ")");
                    System.out.print("Novo nome: "); cliente.setNome(sc.nextLine());
                    System.out.print("Novo e-mail: "); cliente.setEmail(sc.nextLine());
                    System.out.print("Novo telefone: "); cliente.setTelefone(sc.nextLine());
                    System.out.print("Novo endereço: "); cliente.setEndereco(sc.nextLine());
                    System.out.print("Nova cidade: "); cliente.setCidade(sc.nextLine());
                    System.out.print("Novo CEP: "); cliente.setCep(sc.nextLine());
                    System.out.println(controller.AtualizarCliente(cliente) ? "Cliente atualizado com sucesso!" : "Não foi possível atualizar o cliente.");
                    break;
                }
                case "4": {
                    Pessoa cliente = selecionarCliente(sc, controller);
                    if (cliente == null) break;
                    System.out.print("Confirma desativar o cliente e todos os pets dele? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        System.out.println(controller.ExcluirCliente(cliente.getCodigo()) ? "Cliente e pets desativados com sucesso." : "Não foi possível excluir o cliente.");
                    }
                    break;
                }
                case "5": {
                    Pessoa cliente = selecionarCliente(sc, controller);
                    if (cliente == null) break;
                    ArrayList<Raca> racas = controller.SelecionarRacas();
                    if (racas.isEmpty()) { System.out.println("Nenhuma raça cadastrada no banco."); break; }
                    exibirRacas(racas);
                    System.out.print("Código da raça: "); int codigoRaca = lerInteiro(sc.nextLine());
                    System.out.print("Nome do pet: "); String nome = sc.nextLine();
                    System.out.print("Nascimento (DD/MM/AAAA): "); String nascimento = sc.nextLine();
                    System.out.print("Peso em kg (ex.: 5,5): "); String peso = sc.nextLine();
                    Animal pet = new Animal(cliente.getCodigo(), codigoRaca, nome, nascimento, peso);
                    System.out.println(controller.InserirPet(pet) ? "Pet cadastrado com sucesso!" : "Não foi possível cadastrar o pet. Confira raça, data e peso.");
                    break;
                }
                case "6": {
                    Pessoa cliente = selecionarCliente(sc, controller);
                    if (cliente == null) break;
                    ArrayList<Animal> pets = controller.SelecionarPetsCliente(cliente.getCodigo());
                    exibirPets(pets);
                    if (pets.isEmpty()) break;
                    System.out.print("Código do pet que deseja editar: "); int codigoPet = lerInteiro(sc.nextLine());
                    Animal pet = buscarPet(pets, codigoPet);
                    if (pet == null) { System.out.println("Pet não encontrado para este cliente."); break; }
                    ArrayList<Raca> racas = controller.SelecionarRacas();
                    exibirRacas(racas);
                    System.out.print("Novo código da raça: "); pet.setCodigoRaca(lerInteiro(sc.nextLine()));
                    System.out.print("Novo nome: "); pet.setNome(sc.nextLine());
                    System.out.print("Nascimento (DD/MM/AAAA): "); pet.setNascimento(sc.nextLine());
                    System.out.print("Peso em kg (ex.: 5,5): "); pet.setPeso(sc.nextLine());
                    System.out.println(controller.AtualizarPet(pet) ? "Pet atualizado com sucesso!" : "Não foi possível atualizar o pet. Confira raça, data e peso.");
                    break;
                }
                case "7": {
                    Pessoa cliente = selecionarCliente(sc, controller);
                    if (cliente == null) break;
                    ArrayList<Animal> pets = controller.SelecionarPetsCliente(cliente.getCodigo());
                    exibirPets(pets);
                    if (pets.isEmpty()) break;
                    System.out.print("Código do pet que deseja excluir: "); int codigoPet = lerInteiro(sc.nextLine());
                    System.out.println(controller.ExcluirPet(cliente.getCodigo(), codigoPet) ? "Pet desativado com sucesso." : "Não foi possível excluir o pet.");
                    break;
                }
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static ArrayList<Pessoa> listarClientes(ClienteController controller) {
        ArrayList<Pessoa> clientes = controller.SelecionarClientes();
        if (clientes.isEmpty()) System.out.println("Nenhum cliente ativo encontrado.");
        else for (Pessoa cliente : clientes) System.out.println("Código: " + cliente.getCodigo() + " | " + cliente.getNome() + " | CPF: " + cliente.getCpf());
        return clientes;
    }

    private static Pessoa selecionarCliente(Scanner sc, ClienteController controller) {
        if (listarClientes(controller).isEmpty()) return null;
        System.out.print("Código do cliente: ");
        int codigo = lerInteiro(sc.nextLine());
        Pessoa cliente = controller.SelecionarCliente(codigo);
        if (cliente == null) System.out.println("Cliente não encontrado.");
        return cliente;
    }

    private static void exibirCliente(Pessoa cliente) {
        System.out.println("\n===== CLIENTE =====");
        System.out.println("Código: " + cliente.getCodigo() + " | Nome: " + cliente.getNome() + " | CPF: " + cliente.getCpf());
        System.out.println("E-mail: " + cliente.getEmail() + " | Telefone: " + cliente.getTelefone());
        System.out.println("Endereço: " + cliente.getEndereco() + " | Cidade: " + cliente.getCidade() + " | CEP: " + cliente.getCep());
    }

    private static void exibirPets(ArrayList<Animal> pets) {
        if (pets.isEmpty()) System.out.println("Este cliente não possui pets ativos cadastrados.");
        else {
            System.out.println("\n===== PETS =====");
            for (Animal pet : pets) System.out.println("Código: " + pet.getCodigo() + " | " + pet.getNome() + " | Raça: " + pet.getRaca() + " | Nascimento: " + pet.getNascimento() + " | Peso: " + pet.getPeso() + " kg");
        }
    }

    private static void exibirRacas(ArrayList<Raca> racas) {
        System.out.println("\n===== RAÇAS CADASTRADAS =====");
        for (Raca raca : racas) System.out.println("Código: " + raca.getCodigo() + " | " + raca.getNome());
    }

    private static Animal buscarPet(ArrayList<Animal> pets, int codigo) {
        for (Animal pet : pets) if (pet.getCodigo() == codigo) return pet;
        return null;
    }

    private static int lerInteiro(String valor) {
        try { return Integer.parseInt(valor); }
        catch (NumberFormatException e) { return -1; }
    }

}
