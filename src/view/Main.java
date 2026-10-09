package view;

import controller.UsuarioController;
import controller.ServicoController;
import model.dao.Usuario;
import model.dao.Pessoa;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //Cria um objeto responsável por chamar as operações relacionadas aos usuários e serviços

        UsuarioController usuarioController = new UsuarioController();
        ServicoController servicoController = new ServicoController();

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
                                    System.out.println("1 - Cadastrar novo serviço");
                                }

                                System.out.println("2 - Buscar serviço");

                                if (Master) {
                                    System.out.println("3 - Remover serviço");
                                    System.out.println("4 - Atualizar serviço");
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
                                            System.out.println("Cadastrar novo serviço");
                                            // Chamar método de cadastro
                                        } else {
                                            System.out.println("Usuário sem permissão de acesso!");
                                        }
                                        break;

                                    case 2:
                                        System.out.println("Buscar serviço");
                                        System.out.println("Digite o nome ou código do serviço:");

                                        String busca = sc.nextLine();

                                        if (servicoController.conectaBD("petcare")) {
                                            String resultado = servicoController.BuscarServico(busca);
                                            System.out.println(resultado);
                                        } else {
                                            System.out.println("Erro ao conectar ao banco de dados.");
                                        }
                                        break;

                                    case 3:
                                        if (Master) {
                                            System.out.println("Remover serviço");
                                            // Chamar método de remoção
                                        } else {
                                            System.out.println("Adicionar agendamento");
                                            // Chamar método de agendamento
                                        }
                                        break;

                                    case 4:
                                        if (Master) {
                                            System.out.println("Atualizar serviço");
                                            // Chamar método de atualização
                                        } else {
                                            System.out.println("Retirar agendamento");
                                            // Chamar método de retirada
                                        }
                                        break;

                                    case 5:
                                        if (!Master) {
                                            System.out.println("Confirmar agendamento");
                                            // Chamar método de confirmação
                                        } else {
                                            System.out.println("Opção inválida.");
                                        }
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


                            Usuario usuario = new Usuario(email, senha);

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

}