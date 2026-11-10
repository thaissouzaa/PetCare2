package view.fx;

import controller.AgendaController;
import controller.ClienteController;
import controller.PlanoController;
import controller.ServicoController;
import controller.UsuarioController;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.dao.Animal;
import model.dao.Pessoa;
import model.dao.Plano;
import model.dao.Raca;
import model.dao.Servico;
import model.dao.Usuario;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

/** JavaFX desktop interface for the existing PetCare controllers. */
public class PetCareApp extends Application {
    private static final String DATABASE = "petcare";
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final UsuarioController usuarios = new UsuarioController();
    private final ServicoController servicos = new ServicoController();
    private final ClienteController clientes = new ClienteController();
    private final PlanoController planos = new PlanoController();
    private final AgendaController agenda = new AgendaController();
    private Stage stage;
    private String loginAtual;
    private String nomeAtual = "";
    private boolean administrador;
    private BorderPane shell;
    private VBox sidebar;
    private Label pageTitle;
    private String paginaAtual = "Início";

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("PetCare | Gestão de banho e tosa");
        stage.setMinWidth(1040);
        stage.setMinHeight(700);
        mostrarLogin();
        stage.show();
    }

    private Scene scene(Node root) {
        Scene scene = new Scene((javafx.scene.Parent) root, 1440, 900);
        var css = getClass().getResource("/view/fx/petcare.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        return scene;
    }

    private void mostrarLogin() {
        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(440);
        Label brand = new Label("🐾  PetCare");
        brand.getStyleClass().add("brand");
        Label intro = new Label("Entre para gerenciar seus atendimentos");
        intro.getStyleClass().add("muted");
        TextField login = new TextField();
        login.setPromptText("CPF ou e-mail");
        PasswordField senha = new PasswordField();
        senha.setPromptText("Senha");
        Label mensagem = new Label();
        mensagem.setWrapText(true);
        Button entrar = primary("Entrar");
        entrar.setMaxWidth(Double.MAX_VALUE);
        entrar.setOnAction(e -> {
            if (login.getText().isBlank() || senha.getText().isBlank()) {
                mensagem.setText("Informe seu CPF ou e-mail e sua senha.");
                return;
            }
            if (!usuarios.conectaBD(DATABASE)) {
                mensagem.setText("Não foi possível conectar ao banco de dados PetCare.");
                return;
            }
            Usuario credenciais = new Usuario(login.getText().trim(), senha.getText());
            if (!usuarios.VerificarLogin("usuario", credenciais)) {
                mensagem.setText("CPF, e-mail ou senha inválidos.");
                return;
            }
            loginAtual = credenciais.getLogin();
            administrador = usuarios.UsuarioMaster("usuario", credenciais);
            nomeAtual = usuarios.BuscarNomeUsuario(credenciais);
            mostrarAplicativo();
        });
        Button cadastrar = new Button("Criar uma conta");
        cadastrar.getStyleClass().add("soft-button");
        cadastrar.setMaxWidth(Double.MAX_VALUE);
        cadastrar.setOnAction(e -> dialogCadastro());
        card.getChildren().addAll(brand, intro, label("CPF ou e-mail"), login,
                label("Senha"), senha, mensagem, entrar, cadastrar);
        StackPane fundo = new StackPane(card);
        fundo.getStyleClass().add("login-background");
        fundo.setPadding(new Insets(32));
        stage.setScene(scene(fundo));
    }

    private void dialogCadastro() {
        Dialog<Pessoa> dialog = new Dialog<>();
        dialog.setTitle("Cadastro de usuário");
        dialog.initOwner(stage);
        dialog.initModality(Modality.WINDOW_MODAL);
        ButtonType salvar = new ButtonType("Criar conta", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvar, ButtonType.CANCEL);
        GridPane form = formGrid();
        TextField nome = new TextField();
        TextField cpf = new TextField();
        TextField email = new TextField();
        TextField telefone = new TextField();
        TextField endereco = new TextField();
        TextField cidade = new TextField();
        TextField cep = new TextField();
        PasswordField senha = new PasswordField();
        addField(form, 0, "Nome completo", nome);
        addField(form, 1, "CPF", cpf);
        addField(form, 2, "E-mail", email);
        addField(form, 3, "Telefone", telefone);
        addField(form, 4, "Endereço", endereco);
        addField(form, 5, "Cidade", cidade);
        addField(form, 6, "CEP", cep);
        addField(form, 7, "Senha", senha);
        dialog.getDialogPane().setContent(form);
        Node botaoSalvar = dialog.getDialogPane().lookupButton(salvar);
        botaoSalvar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (nome.getText().isBlank() || cpf.getText().isBlank() || email.getText().isBlank() || senha.getText().isBlank()) {
                alerta("Preencha nome, CPF, e-mail e senha.");
                event.consume();
            } else if (!usuarios.conectaBD(DATABASE)) {
                alerta("Não foi possível conectar ao banco de dados PetCare.");
                event.consume();
            } else {
                Usuario novo = new Usuario(email.getText().trim(), senha.getText());
                String retorno = usuarios.InserirUsuario("usuario", novo, new Pessoa(nome.getText().trim(),
                        cpf.getText().replaceAll("\\D", ""), email.getText().trim(), telefone.getText().trim(),
                        endereco.getText().trim(), cidade.getText().trim(), cep.getText().trim()));
                if (!retorno.toLowerCase(PT_BR).contains("sucesso")) {
                    alerta(retorno);
                    event.consume();
                } else {
                    mensagemInfo = retorno;
                }
            }
        });
        dialog.showAndWait();
        if (!mensagemInfo.isBlank()) {
            Alert sucesso = new Alert(Alert.AlertType.INFORMATION, mensagemInfo, ButtonType.OK);
            sucesso.initOwner(stage);
            sucesso.showAndWait();
            mensagemInfo = "";
        }
    }

    private String mensagemInfo = "";

    private void mostrarAplicativo() {
        shell = new BorderPane();
        sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        Label marca = new Label("🐾  PetCare");
        marca.getStyleClass().add("brand");
        sidebar.getChildren().addAll(marca, spacer(22));
        pageTitle = new Label();
        pageTitle.getStyleClass().add("page-title");
        addNav("Início");
        addNav("Banho & Tosa");
        if (administrador) addNav("Clientes e pets");
        addNav("Agenda");
        addNav("Planos");
        if (administrador) addNav("Relatórios");
        Region growing = new Region();
        VBox.setVgrow(growing, Priority.ALWAYS);
        Label user = new Label((administrador ? "ADMINISTRADOR" : "CLIENTE") + "\n" + nomeAtual);
        user.getStyleClass().add("status");
        Button sair = new Button("Sair da conta");
        sair.getStyleClass().add("side-link");
        sair.setMaxWidth(Double.MAX_VALUE);
        sair.setOnAction(e -> { loginAtual = null; mostrarLogin(); });
        sidebar.getChildren().addAll(growing, user, sair);
        shell.setLeft(sidebar);
        BorderPane.setMargin(sidebar, Insets.EMPTY);
        stage.setScene(scene(shell));
        abrirPagina("Início");
    }

    private void addNav(String titulo) {
        javafx.scene.shape.SVGPath icone = new javafx.scene.shape.SVGPath();
        icone.setContent(iconeMenu(titulo));
        icone.getStyleClass().add("nav-icon");
        icone.setScaleX(0.82);
        icone.setScaleY(0.82);
        Label texto = new Label(titulo);
        texto.getStyleClass().add("side-label");
        HBox conteudo = new HBox(14, icone, texto);
        conteudo.setAlignment(Pos.CENTER_LEFT);
        Button item = new Button();
        item.setGraphic(conteudo);
        item.setUserData(titulo);
        item.setAccessibleText(titulo);
        item.getStyleClass().add("side-link");
        item.setMaxWidth(Double.MAX_VALUE);
        item.setOnAction(e -> abrirPagina(titulo));
        sidebar.getChildren().add(item);
    }

    private String iconeMenu(String titulo) {
        return switch (titulo) {
            case "Início" -> "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z";
            case "Banho & Tosa" -> "M8 3a2 2 0 1 0 0 4 2 2 0 0 0 0-4zm8 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4zM5 9h14a2 2 0 0 1 2 2v2H3v-2a2 2 0 0 1 2-2zm-1 6h16v2a4 4 0 0 1-4 4H8a4 4 0 0 1-4-4v-2zm5 2a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm6 0a1 1 0 1 0 0 2 1 1 0 0 0 0-2z";
            case "Clientes e pets" -> "M16 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6zM8 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm0 2c-2.7 0-6 1.35-6 4v2h12v-2c0-2.65-3.3-4-6-4zm8 0c-.45 0-.94.03-1.44.1 1.47.93 2.44 2.25 2.44 3.9v2h5v-2c0-2.65-3.3-4-6-4z";
            case "Agenda" -> "M19 4h-1V2h-2v2H8V2H6v2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zm0 16H5V9h14v11zM7 11h4v4H7zm6 0h4v4h-4z";
            case "Planos" -> "M20 4H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zm0 14H4v-7h16v7zM4 8V6h16v2H4zm3 7h5v2H7z";
            case "Relatórios" -> "M4 19h16v2H2V3h2v16zm3-2H5v-5h2v5zm5 0h-2V8h2v9zm5 0h-2V4h2v13zm5 0h-2v-7h2v7z";
            default -> "M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm1 15h-2v-6h2zm0-8h-2V7h2z";
        };
    }

    private void abrirPagina(String titulo) {
        paginaAtual = titulo;
        pageTitle.setText(titulo);
        VBox conteudo = new VBox(22);
        conteudo.getStyleClass().add("page");
        HBox topo = new HBox(12, pageTitle, spacer(0));
        topo.setAlignment(Pos.CENTER_LEFT);
        Label breadcrumb = new Label("Início  /  " + titulo);
        breadcrumb.getStyleClass().add("muted");
        conteudo.getChildren().addAll(topo, breadcrumb);
        switch (titulo) {
            case "Início" -> conteudo.getChildren().add(painelInicio());
            case "Banho & Tosa" -> conteudo.getChildren().add(painelServicos());
            case "Clientes e pets" -> conteudo.getChildren().add(painelClientes());
            case "Agenda" -> conteudo.getChildren().add(painelAgenda());
            case "Planos" -> conteudo.getChildren().add(painelPlanos());
            case "Relatórios" -> conteudo.getChildren().add(painelRelatorios());
            default -> conteudo.getChildren().add(painelIndisponivel(titulo, "Esta seção ainda não foi configurada."));
        }
        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        shell.setCenter(scroll);
        atualizarSelecaoMenu(titulo);
    }

    private void atualizarSelecaoMenu(String titulo) {
        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button button && titulo.equals(button.getUserData())) {
                if (!button.getStyleClass().contains("selected")) button.getStyleClass().add("selected");
            } else if (node instanceof Button button) button.getStyleClass().remove("selected");
        }
    }

    private Node painelInicio() {
        int quantidadeClientes = 0;
        int quantidadeServicos = 0;
        int quantidadeAgenda = 0;
        if (administrador && clientes.conectaBD(DATABASE)) quantidadeClientes = clientes.SelecionarClientes().size();
        if (servicos.conectaBD(DATABASE)) quantidadeServicos = servicos.SelecionarTodosServicos().size();
        if (agenda.conectaBD(DATABASE)) quantidadeAgenda = administrador
                ? agenda.SelecionarTodosAgendamentos().size() : agenda.SelecionarAgendamentos(loginAtual).size();
        HBox metricas = new HBox(16,
                metrica("Serviços ativos", String.valueOf(quantidadeServicos), "✿"),
                metrica(administrador ? "Clientes ativos" : "Meus agendamentos", String.valueOf(administrador ? quantidadeClientes : quantidadeAgenda), "♧"),
                metrica(administrador ? "Agendamentos" : "Atendimentos", String.valueOf(quantidadeAgenda), "▦"));
        for (Node n : metricas.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        VBox boasVindas = new VBox(10);
        boasVindas.getStyleClass().add("card");
        Label titulo = new Label("Olá, " + nomeAtual + "!");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #263a34;");
        Label texto = new Label(administrador
                ? "Acompanhe os serviços, clientes e atendimentos do PetCare em um só lugar."
                : "Consulte os serviços disponíveis e acompanhe seus próximos atendimentos.");
        texto.getStyleClass().add("muted");
        Button abrirAgenda = primary("Ver agenda");
        abrirAgenda.setOnAction(e -> abrirPagina("Agenda"));
        boasVindas.getChildren().addAll(titulo, texto, abrirAgenda);
        VBox box = new VBox(20, metricas, boasVindas);
        return box;
    }

    private Node metrica(String titulo, String valor, String icone) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        Label mark = new Label(icone + "  " + titulo);
        mark.getStyleClass().add("metric-label");
        Label value = new Label(valor);
        value.getStyleClass().add("metric-value");
        card.getChildren().addAll(mark, value);
        return card;
    }

    private Node painelServicos() {
        VBox box = new VBox(16);
        if (!servicos.conectaBD(DATABASE)) return erroBanco();
        ArrayList<Servico> lista = servicos.SelecionarTodosServicos();
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        TextField busca = new TextField();
        busca.setPromptText("Buscar serviço pelo nome");
        busca.setPrefWidth(300);
        Button novo = primary("+  Novo serviço");
        novo.setVisible(administrador);
        novo.setManaged(administrador);
        novo.setOnAction(e -> dialogServico(null));
        acoes.getChildren().addAll(busca, novo);
        TableView<Servico> tabela = new TableView<>();
        tabela.getColumns().add(coluna("Serviço", s -> s.getNome(), 210));
        tabela.getColumns().add(coluna("Descrição", s -> s.getDescricao(), 360));
        tabela.getColumns().add(coluna("Duração", s -> s.getDuracao() + " min", 110));
        tabela.getColumns().add(coluna("Valor", s -> formatarValor(s.getValor()), 130));
        ObservableListBuilder.set(tabela, lista);
        busca.textProperty().addListener((obs, anterior, texto) -> {
            ArrayList<Servico> filtrada = new ArrayList<>();
            for (Servico s : lista) if (s.getNome().toLowerCase(PT_BR).contains(texto.toLowerCase(PT_BR))) filtrada.add(s);
            ObservableListBuilder.set(tabela, filtrada);
        });
        if (administrador) {
            tabela.setRowFactory(tv -> {
                TableRow<Servico> row = new TableRow<>();
                ContextMenu menu = new ContextMenu();
                MenuItem editar = new MenuItem("Editar");
                editar.setOnAction(e -> dialogServico(row.getItem()));
                MenuItem excluir = new MenuItem("Desativar");
                excluir.setOnAction(e -> excluirServico(row.getItem()));
                menu.getItems().addAll(editar, excluir);
                row.setContextMenu(menu);
                row.emptyProperty().addListener((obs, vazioAntes, vazio) -> row.setContextMenu(vazio ? null : menu));
                return row;
            });
        }
        tabela.setPlaceholder(new Label("Nenhum serviço ativo encontrado."));
        VBox cartao = new VBox(tabela);
        cartao.getStyleClass().add("table-card");
        VBox.setVgrow(tabela, Priority.ALWAYS);
        box.getChildren().addAll(acoes, cartao);
        VBox.setVgrow(cartao, Priority.ALWAYS);
        return box;
    }

    private void dialogServico(Servico existente) {
        Dialog<ButtonType> dialog = dialog("Serviço", "Salvar", "Cancelar");
        GridPane form = formGrid();
        TextField nome = new TextField(existente == null ? "" : existente.getNome());
        TextField descricao = new TextField(existente == null ? "" : existente.getDescricao());
        TextField duracao = new TextField(existente == null ? "" : existente.getDuracao());
        TextField valor = new TextField(existente == null ? "" : existente.getValor());
        addField(form, 0, "Nome", nome);
        addField(form, 1, "Descrição", descricao);
        addField(form, 2, "Duração (minutos)", duracao);
        addField(form, 3, "Valor (R$)", valor);
        dialog.getDialogPane().setContent(form);
        Optional<ButtonType> resposta = dialog.showAndWait();
        if (resposta.isEmpty() || resposta.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) return;
        try {
            if (nome.getText().isBlank() || descricao.getText().isBlank()) throw new IllegalArgumentException("Informe o nome e a descrição do serviço.");
            int minutos = Integer.parseInt(duracao.getText().trim());
            BigDecimal preco = new BigDecimal(normalizarValor(valor.getText()));
            if (minutos <= 0 || preco.signum() < 0) throw new IllegalArgumentException("Duração e valor precisam ser válidos.");
            Servico servico = existente == null
                    ? new Servico(nome.getText().trim(), descricao.getText().trim(), String.valueOf(minutos), preco.toPlainString())
                    : new Servico(existente.getCodigo(), nome.getText().trim(), descricao.getText().trim(), String.valueOf(minutos), preco.toPlainString());
            if (!servicos.conectaBD(DATABASE)) throw new IllegalStateException("Não foi possível conectar ao banco de dados.");
            boolean ok = existente == null
                    ? servicos.InserirServico(servico).toLowerCase(PT_BR).contains("sucesso")
                    : servicos.AtualizarServico(servico);
            if (!ok) throw new IllegalStateException("Não foi possível salvar o serviço.");
            abrirPagina("Banho & Tosa");
        } catch (RuntimeException ex) { alerta(ex.getMessage() == null ? "Confira os dados informados." : ex.getMessage()); }
    }

    private void excluirServico(Servico servico) {
        if (servico == null || !confirmar("Desativar o serviço “" + servico.getNome() + "”?")) return;
        if (servicos.conectaBD(DATABASE) && servicos.removerServico(servico)) abrirPagina("Banho & Tosa");
        else alerta("Não foi possível desativar o serviço.");
    }

    private Node painelClientes() {
        VBox box = new VBox(16);
        if (!clientes.conectaBD(DATABASE)) return erroBanco();
        ArrayList<Pessoa> lista = clientes.SelecionarClientes();
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        TextField busca = new TextField();
        busca.setPromptText("Buscar cliente por nome ou CPF");
        busca.setPrefWidth(340);
        Button recarregar = new Button("Atualizar lista");
        recarregar.getStyleClass().add("soft-button");
        recarregar.setOnAction(e -> abrirPagina("Clientes e pets"));
        Button novoCliente = primary("+  Novo cliente");
        novoCliente.setOnAction(e -> dialogNovoCliente());
        acoes.getChildren().addAll(busca, recarregar, novoCliente);
        TableView<Pessoa> tabela = new TableView<>();
        tabela.getColumns().add(coluna("Cliente", Pessoa::getNome, 240));
        tabela.getColumns().add(coluna("CPF", Pessoa::getCpf, 150));
        tabela.getColumns().add(coluna("E-mail", Pessoa::getEmail, 250));
        tabela.getColumns().add(coluna("Telefone", Pessoa::getTelefone, 150));
        ObservableListBuilder.set(tabela, lista);
        busca.textProperty().addListener((obs, antes, texto) -> {
            ArrayList<Pessoa> filtrada = new ArrayList<>();
            String termo = texto.toLowerCase(PT_BR);
            for (Pessoa p : lista) if (p.getNome().toLowerCase(PT_BR).contains(termo) || p.getCpf().contains(termo)) filtrada.add(p);
            ObservableListBuilder.set(tabela, filtrada);
        });
        Button editar = new Button("Editar dados");
        editar.getStyleClass().add("soft-button");
        editar.setOnAction(e -> { if (tabela.getSelectionModel().getSelectedItem() != null) dialogCliente(tabela.getSelectionModel().getSelectedItem()); });
        Button pets = new Button("Ver pets");
        pets.getStyleClass().add("soft-button");
        pets.setOnAction(e -> { if (tabela.getSelectionModel().getSelectedItem() != null) dialogPets(tabela.getSelectionModel().getSelectedItem()); });
        Button excluir = new Button("Desativar cliente");
        excluir.getStyleClass().add("danger-button");
        excluir.setOnAction(e -> {
            Pessoa escolhido = tabela.getSelectionModel().getSelectedItem();
            if (escolhido == null || !confirmar("Desativar " + escolhido.getNome() + " e os pets associados?")) return;
            if (clientes.conectaBD(DATABASE) && clientes.ExcluirCliente(escolhido.getCodigo())) abrirPagina("Clientes e pets");
            else alerta("Não foi possível desativar o cliente.");
        });
        HBox comandos = new HBox(10, editar, pets, excluir);
        VBox cartao = new VBox(12, tabela, comandos);
        cartao.getStyleClass().add("table-card");
        VBox.setVgrow(tabela, Priority.ALWAYS);
        box.getChildren().addAll(acoes, cartao);
        VBox.setVgrow(cartao, Priority.ALWAYS);
        return box;
    }

    private Node painelPlanos() {
        VBox box = new VBox(16);
        if (!planos.conectaBD(DATABASE)) return erroBanco();
        ArrayList<Plano> lista = planos.selecionarPlanos();
        HBox acoes = new HBox(10);
        acoes.setAlignment(Pos.CENTER_RIGHT);
        TextField busca = new TextField();
        busca.setPromptText("Buscar plano pelo nome ou recorrência");
        busca.setPrefWidth(340);
        Button recarregar = new Button("Atualizar lista");
        recarregar.getStyleClass().add("soft-button");
        recarregar.setOnAction(e -> abrirPagina("Planos"));
        Button novo = primary("+  Novo plano");
        novo.setVisible(administrador);
        novo.setManaged(administrador);
        novo.setOnAction(e -> dialogPlano(null));
        acoes.getChildren().addAll(busca, recarregar, novo);
        if (administrador) {
            Button associar = new Button("Associar a pessoa");
            associar.getStyleClass().add("soft-button");
            associar.setOnAction(e -> dialogAssociarPlano());
            acoes.getChildren().add(associar);
        }

        TableView<Plano> tabela = new TableView<>();
        tabela.getColumns().add(coluna("Plano", Plano::getNome, 280));
        tabela.getColumns().add(coluna("Recorrência", Plano::getRecorrencia, 220));
        ObservableListBuilder.set(tabela, lista);
        busca.textProperty().addListener((obs, anterior, texto) -> {
            ArrayList<Plano> filtrada = new ArrayList<>();
            String termo = texto.toLowerCase(PT_BR);
            for (Plano plano : lista) if ((plano.getNome() + " " + plano.getRecorrencia()).toLowerCase(PT_BR).contains(termo)) filtrada.add(plano);
            ObservableListBuilder.set(tabela, filtrada);
        });
        if (administrador) {
            tabela.setRowFactory(tv -> {
                TableRow<Plano> row = new TableRow<>();
                MenuItem editar = new MenuItem("Editar");
                editar.setOnAction(e -> dialogPlano(row.getItem()));
                MenuItem desativar = new MenuItem("Desativar");
                desativar.setOnAction(e -> desativarPlano(row.getItem()));
                ContextMenu menu = new ContextMenu(editar, desativar);
                row.setContextMenu(menu);
                row.emptyProperty().addListener((obs, vazioAntes, vazio) -> row.setContextMenu(vazio ? null : menu));
                return row;
            });
        }
        tabela.setPlaceholder(new Label("Nenhum plano ativo encontrado."));
        VBox cartao = new VBox(tabela);
        cartao.getStyleClass().add("table-card");
        VBox.setVgrow(tabela, Priority.ALWAYS);
        VBox.setVgrow(cartao, Priority.ALWAYS);
        if (!administrador) {
            Plano planoAtual = planos.selecionarPlanoPessoa(loginAtual);
            VBox meuPlano = new VBox(8);
            meuPlano.getStyleClass().add("card");
            Label titulo = new Label("Meu plano");
            titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #263a34;");
            Label descricao = new Label(planoAtual == null ? "Você ainda não possui um plano associado."
                    : planoAtual.getNome() + " · " + planoAtual.getRecorrencia());
            descricao.getStyleClass().add("muted");
            meuPlano.getChildren().addAll(titulo, descricao);
            box.getChildren().add(meuPlano);
        }
        box.getChildren().addAll(acoes, cartao);
        return box;
    }

    private void dialogAssociarPlano() {
        if (!clientes.conectaBD(DATABASE)) { alerta("Não foi possível carregar as pessoas cadastradas."); return; }
        ArrayList<Pessoa> pessoas = clientes.SelecionarClientes();
        ArrayList<Plano> listaPlanos = planos.selecionarPlanos();
        if (pessoas.isEmpty()) { alerta("Não há pessoas cadastradas para associar a um plano."); return; }
        if (listaPlanos.isEmpty()) { alerta("Cadastre um plano ativo antes de fazer a associação."); return; }

        Dialog<ButtonType> dialog = dialog("Associar plano a uma pessoa", "Salvar associação", "Cancelar");
        GridPane form = formGrid();
        ComboBox<Pessoa> pessoa = new ComboBox<>(FXCollections.observableArrayList(pessoas));
        pessoa.setMaxWidth(Double.MAX_VALUE);
        pessoa.setCellFactory(c -> new ListCell<>() {
            @Override protected void updateItem(Pessoa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome() + " · " + item.getCpf());
            }
        });
        pessoa.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Pessoa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Selecione uma pessoa" : item.getNome() + " · " + item.getCpf());
            }
        });
        ComboBox<Plano> plano = new ComboBox<>(FXCollections.observableArrayList(listaPlanos));
        plano.setMaxWidth(Double.MAX_VALUE);
        plano.setCellFactory(c -> new ListCell<>() {
            @Override protected void updateItem(Plano item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome() + " · " + item.getRecorrencia());
            }
        });
        plano.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Plano item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Selecione um plano" : item.getNome() + " · " + item.getRecorrencia());
            }
        });
        pessoa.valueProperty().addListener((obs, anterior, selecionada) -> {
            if (selecionada != null) plano.setValue(planos.selecionarPlanoPessoa(selecionada.getCodigo()));
        });
        addField(form, 0, "Pessoa", pessoa);
        addField(form, 1, "Plano", plano);
        dialog.getDialogPane().setContent(form);
        Optional<ButtonType> resposta = dialog.showAndWait();
        if (resposta.isEmpty() || resposta.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) return;
        if (pessoa.getValue() == null || plano.getValue() == null) { alerta("Selecione uma pessoa e um plano."); return; }
        if (!planos.associarPlanoPessoa(pessoa.getValue().getCodigo(), plano.getValue().getCodigo())) {
            alerta("Não foi possível associar o plano à pessoa.");
            return;
        }
        alertaInfo("Plano associado a " + pessoa.getValue().getNome() + ".");
        abrirPagina("Planos");
    }

    private void dialogPlano(Plano existente) {
        Dialog<ButtonType> dialog = dialog(existente == null ? "Cadastrar plano" : "Editar plano", "Salvar", "Cancelar");
        GridPane form = formGrid();
        TextField nome = new TextField(existente == null ? "" : existente.getNome());
        TextField recorrencia = new TextField(existente == null ? "" : existente.getRecorrencia());
        recorrencia.setPromptText("Ex.: mensal, semanal");
        addField(form, 0, "Nome", nome);
        addField(form, 1, "Recorrência", recorrencia);
        dialog.getDialogPane().setContent(form);
        Optional<ButtonType> resposta = dialog.showAndWait();
        if (resposta.isEmpty() || resposta.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) return;
        String nomeValor = nome.getText().trim();
        String recorrenciaValor = recorrencia.getText().trim();
        if (nomeValor.isBlank() || recorrenciaValor.isBlank()) { alerta("Informe o nome e a recorrência do plano."); return; }
        if (nomeValor.length() > 50 || recorrenciaValor.length() > 50) { alerta("Nome e recorrência devem ter até 50 caracteres."); return; }
        Plano plano = existente == null ? new Plano(nomeValor, recorrenciaValor)
                : new Plano(existente.getCodigo(), nomeValor, recorrenciaValor);
        boolean salvo = existente == null ? planos.inserirPlano(plano) : planos.atualizarPlano(plano);
        if (!salvo) { alerta("Não foi possível salvar o plano."); return; }
        abrirPagina("Planos");
    }

    private void desativarPlano(Plano plano) {
        if (plano == null || !confirmar("Desativar o plano “" + plano.getNome() + "”?")) return;
        if (planos.desativarPlano(plano.getCodigo())) abrirPagina("Planos");
        else alerta("Não foi possível desativar o plano.");
    }

    private Node painelRelatorios() {
        if (!agenda.conectaBD(DATABASE)) return erroBanco();
        ArrayList<String> compromissos = agenda.SelecionarTodosAgendamentos();
        ArrayList<String> resumoServicos = agenda.SelecionarResumoServicosAgendados();
        long confirmados = compromissos.stream().filter(item -> item.endsWith("Confirmado")).count();
        long pendentes = compromissos.size() - confirmados;
        HBox metricas = new HBox(16,
                metrica("Agendamentos", String.valueOf(compromissos.size()), "▦"),
                metrica("Confirmados", String.valueOf(confirmados), "✓"),
                metrica("Pendentes", String.valueOf(pendentes), "◷"));
        for (Node node : metricas.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        TableView<String> porServico = new TableView<>();
        porServico.getColumns().add(coluna("Serviço", item -> item.split("\\t", -1)[0], 240));
        porServico.getColumns().add(coluna("Agendamentos", item -> item.split("\\t", -1)[1], 130));
        porServico.getColumns().add(coluna("Confirmados", item -> item.split("\\t", -1)[2], 120));
        porServico.getColumns().add(coluna("Pendentes", item -> item.split("\\t", -1)[3], 110));
        ObservableListBuilder.set(porServico, resumoServicos);
        porServico.setPlaceholder(new Label("Nenhum serviço com agendamentos no período."));
        VBox servicosCard = new VBox(12, new Label("Agendamentos por serviço"), porServico);
        servicosCard.getStyleClass().add("table-card");
        porServico.setPrefHeight(260);

        ListView<String> lista = new ListView<>(FXCollections.observableArrayList(compromissos));
        lista.setPlaceholder(new Label("Nenhum agendamento encontrado para o relatório."));
        VBox agendamentosCard = new VBox(12, new Label("Detalhamento dos agendamentos"), lista);
        agendamentosCard.getStyleClass().add("table-card");
        lista.setPrefHeight(300);
        Button atualizar = new Button("Atualizar relatórios");
        atualizar.getStyleClass().add("soft-button");
        atualizar.setOnAction(e -> abrirPagina("Relatórios"));
        VBox box = new VBox(18, metricas, atualizar, servicosCard, agendamentosCard);
        return box;
    }

    private void dialogCliente(Pessoa p) {
        Dialog<ButtonType> dialog = dialog("Editar cliente", "Salvar alterações", "Cancelar");
        GridPane form = formGrid();
        TextField nome = new TextField(p.getNome());
        TextField email = new TextField(p.getEmail());
        TextField telefone = new TextField(p.getTelefone());
        TextField endereco = new TextField(p.getEndereco());
        TextField cidade = new TextField(p.getCidade());
        TextField cep = new TextField(p.getCep());
        addField(form, 0, "Nome", nome); addField(form, 1, "E-mail", email);
        addField(form, 2, "Telefone", telefone); addField(form, 3, "Endereço", endereco);
        addField(form, 4, "Cidade", cidade); addField(form, 5, "CEP", cep);
        dialog.getDialogPane().setContent(form);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) return;
        if (nome.getText().isBlank() || email.getText().isBlank()) { alerta("Nome e e-mail são obrigatórios."); return; }
        p.setNome(nome.getText().trim()); p.setEmail(email.getText().trim()); p.setTelefone(telefone.getText().trim());
        p.setEndereco(endereco.getText().trim()); p.setCidade(cidade.getText().trim()); p.setCep(cep.getText().trim());
        if (clientes.conectaBD(DATABASE) && clientes.AtualizarCliente(p)) abrirPagina("Clientes e pets");
        else alerta("Não foi possível atualizar o cliente.");
    }

    private void dialogNovoCliente() {
        Dialog<ButtonType> dialog = dialog("Cadastrar cliente", "Cadastrar", "Cancelar");
        GridPane form = formGrid();
        TextField nome = new TextField();
        TextField cpf = new TextField();
        TextField email = new TextField();
        TextField telefone = new TextField();
        TextField endereco = new TextField();
        TextField cidade = new TextField();
        TextField cep = new TextField();
        PasswordField senha = new PasswordField();
        addField(form, 0, "Nome completo", nome); addField(form, 1, "CPF", cpf);
        addField(form, 2, "E-mail", email); addField(form, 3, "Telefone", telefone);
        addField(form, 4, "Endereço", endereco); addField(form, 5, "Cidade", cidade);
        addField(form, 6, "CEP", cep); addField(form, 7, "Senha inicial", senha);
        dialog.getDialogPane().setContent(form);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) return;
        if (nome.getText().isBlank() || cpf.getText().isBlank() || email.getText().isBlank() || senha.getText().isBlank()) {
            alerta("Nome, CPF, e-mail e senha inicial são obrigatórios."); return;
        }
        if (!usuarios.conectaBD(DATABASE)) { alerta("Não foi possível conectar ao banco de dados."); return; }
        String retorno = usuarios.InserirUsuario("usuario", new Usuario(email.getText().trim(), senha.getText()),
                new Pessoa(nome.getText().trim(), cpf.getText().replaceAll("\\D", ""), email.getText().trim(),
                        telefone.getText().trim(), endereco.getText().trim(), cidade.getText().trim(), cep.getText().trim()));
        if (retorno.toLowerCase(PT_BR).contains("sucesso")) { alertaInfo(retorno); abrirPagina("Clientes e pets"); }
        else alerta(retorno);
    }

    private void dialogPets(Pessoa cliente) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Pets de " + cliente.getNome());
        dialog.initOwner(stage);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        VBox conteudo = new VBox(12);
        TableView<Animal> tabela = new TableView<>();
        tabela.getColumns().add(coluna("Pet", Animal::getNome, 150));
        tabela.getColumns().add(coluna("Raça", Animal::getRaca, 170));
        tabela.getColumns().add(coluna("Nascimento", Animal::getNascimento, 130));
        tabela.getColumns().add(coluna("Peso", a -> a.getPeso() + " kg", 90));
        Runnable carregar = () -> {
            if (clientes.conectaBD(DATABASE)) ObservableListBuilder.set(tabela, clientes.SelecionarPetsCliente(cliente.getCodigo()));
        };
        carregar.run();
        Button novo = primary("+  Cadastrar pet");
        Button editar = new Button("Editar pet"); editar.getStyleClass().add("soft-button");
        Button remover = new Button("Desativar pet"); remover.getStyleClass().add("danger-button");
        novo.setOnAction(e -> { if (dialogPet(cliente, null)) carregar.run(); });
        editar.setOnAction(e -> { Animal pet = tabela.getSelectionModel().getSelectedItem(); if (pet != null && dialogPet(cliente, pet)) carregar.run(); });
        remover.setOnAction(e -> {
            Animal pet = tabela.getSelectionModel().getSelectedItem();
            if (pet == null || !confirmar("Desativar o pet “" + pet.getNome() + "”?")) return;
            if (clientes.conectaBD(DATABASE) && clientes.ExcluirPet(cliente.getCodigo(), pet.getCodigo())) carregar.run();
            else alerta("Não foi possível desativar o pet.");
        });
        conteudo.getChildren().addAll(new Label("Tutor: " + cliente.getNome()), tabela, new HBox(8, novo, editar, remover));
        dialog.getDialogPane().setContent(conteudo);
        dialog.getDialogPane().setPrefSize(720, 480);
        dialog.showAndWait();
    }

    private boolean dialogPet(Pessoa cliente, Animal existente) {
        if (!clientes.conectaBD(DATABASE)) { alerta("Não foi possível conectar ao banco de dados."); return false; }
        ArrayList<Raca> racas = clientes.SelecionarRacas();
        if (racas.isEmpty()) { alerta("Cadastre raças no banco antes de adicionar um pet."); return false; }
        Dialog<ButtonType> dialog = dialog(existente == null ? "Cadastrar pet" : "Editar pet", "Salvar", "Cancelar");
        GridPane form = formGrid();
        TextField nome = new TextField(existente == null ? "" : existente.getNome());
        TextField nascimento = new TextField(existente == null ? "" : existente.getNascimento());
        nascimento.setPromptText("DD/MM/AAAA");
        TextField peso = new TextField(existente == null ? "" : existente.getPeso()); peso.setPromptText("Ex.: 5,5");
        ComboBox<Raca> raca = new ComboBox<>(FXCollections.observableArrayList(racas));
        raca.setMaxWidth(Double.MAX_VALUE);
        raca.setCellFactory(v -> new ListCell<>() { @Override protected void updateItem(Raca item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : item.getNome()); } });
        raca.setButtonCell(new ListCell<>() { @Override protected void updateItem(Raca item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? "Selecione a raça" : item.getNome()); } });
        if (existente != null) racas.stream().filter(r -> r.getCodigo() == existente.getCodigoRaca()).findFirst().ifPresent(raca::setValue);
        addField(form, 0, "Nome do pet", nome); addField(form, 1, "Raça", raca);
        addField(form, 2, "Nascimento", nascimento); addField(form, 3, "Peso (kg)", peso);
        dialog.getDialogPane().setContent(form);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) return false;
        if (nome.getText().isBlank() || nascimento.getText().isBlank() || peso.getText().isBlank() || raca.getValue() == null) {
            alerta("Preencha todos os dados do pet."); return false;
        }
        Animal pet = existente == null ? new Animal(cliente.getCodigo(), raca.getValue().getCodigo(), nome.getText().trim(), nascimento.getText().trim(), peso.getText().trim()) : existente;
        pet.setCodigoPessoa(cliente.getCodigo()); pet.setCodigoRaca(raca.getValue().getCodigo());
        pet.setNome(nome.getText().trim()); pet.setNascimento(nascimento.getText().trim()); pet.setPeso(peso.getText().trim());
        boolean ok = existente == null ? clientes.InserirPet(pet) : clientes.AtualizarPet(pet);
        if (!ok) alerta("Não foi possível salvar o pet. Confira a data (DD/MM/AAAA) e o peso.");
        return ok;
    }

    private Node painelAgenda() {
        VBox box = new VBox(16);
        if (!agenda.conectaBD(DATABASE)) return erroBanco();
        if (administrador) {
            ArrayList<String> compromissos = agenda.SelecionarTodosAgendamentos();
            ListView<String> lista = new ListView<>(FXCollections.observableArrayList(compromissos));
            lista.setPlaceholder(new Label("Nenhum agendamento encontrado."));
            VBox cartao = new VBox(14, new Label("Agendamentos dos clientes"), lista);
            cartao.getStyleClass().add("table-card");
            VBox.setVgrow(lista, Priority.ALWAYS);
            VBox.setVgrow(cartao, Priority.ALWAYS);
            Button novo = primary("+  Cadastrar agendamento para cliente");
            novo.setOnAction(e -> dialogAgendamento());
            box.getChildren().addAll(novo, cartao);
            return box;
        }
        ArrayList<String> compromissos = agenda.SelecionarAgendamentos(loginAtual);
        ListView<String> lista = new ListView<>(FXCollections.observableArrayList(compromissos));
        lista.setPlaceholder(new Label("Você ainda não possui agendamentos."));
        VBox cartao = new VBox(14, new Label("Meus atendimentos"), lista);
        cartao.getStyleClass().add("table-card");
        VBox.setVgrow(lista, Priority.ALWAYS);
        Button novo = primary("+  Solicitar agendamento");
        novo.setOnAction(e -> dialogAgendamento());
        Button confirmar = new Button("Confirmar atendimento"); confirmar.getStyleClass().add("soft-button");
        confirmar.setOnAction(e -> atualizarAgendamento(lista.getSelectionModel().getSelectedItem(), true));
        Button cancelar = new Button("Cancelar agendamento"); cancelar.getStyleClass().add("danger-button");
        cancelar.setOnAction(e -> atualizarAgendamento(lista.getSelectionModel().getSelectedItem(), false));
        HBox comandos = new HBox(10, confirmar, cancelar);
        cartao.getChildren().add(comandos);
        VBox.setVgrow(cartao, Priority.ALWAYS);
        box.getChildren().addAll(novo, cartao);
        return box;
    }

    private void atualizarAgendamento(String selecionado, boolean confirmar) {
        if (selecionado == null) { alerta("Selecione um agendamento na lista."); return; }
        try {
            int codigo = Integer.parseInt(selecionado.substring("Código: ".length()).split(" \\|")[0]);
            if (confirmar) {
                if (!agenda.conectaBD(DATABASE) || !agenda.AtualizarStatusAgenda(loginAtual, codigo, true)) {
                    alerta("Não foi possível confirmar o agendamento."); return;
                }
                alertaInfo("Agendamento confirmado.");
            } else {
                if (!confirmar("Cancelar o agendamento selecionado?")) return;
                if (!agenda.conectaBD(DATABASE) || !agenda.RemoverAgendamento(loginAtual, codigo)) {
                    alerta("Não foi possível cancelar o agendamento."); return;
                }
            }
            abrirPagina("Agenda");
        } catch (RuntimeException ex) { alerta("Não foi possível identificar o agendamento selecionado."); }
    }

    private void dialogAgendamento() {
        if (!servicos.conectaBD(DATABASE)) { alerta("Não foi possível conectar ao banco de dados."); return; }
        ArrayList<Servico> itens = servicos.SelecionarTodosServicos();
        if (itens.isEmpty()) { alerta("Não há serviços ativos para agendar."); return; }
        ArrayList<Pessoa> pessoas = new ArrayList<>();
        if (administrador) {
            if (!clientes.conectaBD(DATABASE)) { alerta("Não foi possível carregar a lista de clientes."); return; }
            pessoas = clientes.SelecionarClientes();
            if (pessoas.isEmpty()) { alerta("Não há clientes ativos para agendar."); return; }
        }
        Dialog<ButtonType> dialog = dialog(administrador ? "Cadastrar agendamento para cliente" : "Solicitar agendamento",
                administrador ? "Cadastrar" : "Solicitar", "Cancelar");
        GridPane form = formGrid();
        ComboBox<Pessoa> pessoa = null;
        if (administrador) {
            pessoa = new ComboBox<>(FXCollections.observableArrayList(pessoas));
            pessoa.setMaxWidth(Double.MAX_VALUE);
            pessoa.setCellFactory(v -> new ListCell<>() {
                @Override protected void updateItem(Pessoa p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? null : p.getNome() + " · " + p.getCpf());
                }
            });
            pessoa.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(Pessoa p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? "Selecione um cliente" : p.getNome() + " · " + p.getCpf());
                }
            });
        }
        ComboBox<Servico> servico = new ComboBox<>(FXCollections.observableArrayList(itens));
        servico.setMaxWidth(Double.MAX_VALUE);
        servico.setCellFactory(v -> new ListCell<>() { @Override protected void updateItem(Servico s, boolean empty) { super.updateItem(s, empty); setText(empty || s == null ? null : s.getNome() + " · " + formatarValor(s.getValor())); } });
        servico.setButtonCell(new ListCell<>() { @Override protected void updateItem(Servico s, boolean empty) { super.updateItem(s, empty); setText(empty || s == null ? "Selecione um serviço" : s.getNome() + " · " + formatarValor(s.getValor())); } });
        TextField inicio = new TextField(); inicio.setPromptText("DD/MM/AAAA HH:MM");
        if (administrador) addField(form, 0, "Cliente", pessoa);
        addField(form, administrador ? 1 : 0, "Serviço", servico);
        addField(form, administrador ? 2 : 1, "Data e hora", inicio);
        dialog.getDialogPane().setContent(form);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) return;
        if (administrador && pessoa.getValue() == null) { alerta("Selecione o cliente."); return; }
        if (servico.getValue() == null || inicio.getText().isBlank()) { alerta("Selecione o serviço e informe a data e hora."); return; }
        boolean salvo = administrador
                ? agenda.InserirAgendamentoPessoa(pessoa.getValue().getCodigo(), inicio.getText().trim(), servico.getValue().getCodigo(), true)
                : agenda.InserirAgendamento(loginAtual, inicio.getText().trim(), servico.getValue().getCodigo());
        if (!salvo) {
            alerta("Não foi possível salvar o agendamento. Confira o cliente, o horário (DD/MM/AAAA HH:MM) e o serviço.");
            return;
        }
        alertaInfo(administrador ? "Agendamento cadastrado e confirmado para " + pessoa.getValue().getNome() + "."
                : "Solicitação enviada. Seu atendimento ficará pendente de confirmação.");
        abrirPagina("Agenda");
    }

    private Node painelIndisponivel(String titulo, String descricao) {
        VBox aviso = new VBox(12);
        aviso.getStyleClass().add("card");
        Label cabecalho = new Label(titulo);
        cabecalho.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label texto = new Label(descricao);
        texto.setWrapText(true);
        texto.getStyleClass().add("muted");
        aviso.getChildren().addAll(cabecalho, texto);
        aviso.setMaxWidth(700);
        return aviso;
    }

    private Node erroBanco() {
        VBox aviso = new VBox(10);
        aviso.getStyleClass().add("notice");
        aviso.getChildren().addAll(new Label("Não foi possível conectar ao banco de dados PetCare."),
                new Label("Confira se o PostgreSQL está em execução e tente novamente."));
        Button tentar = new Button("Tentar novamente");
        tentar.getStyleClass().add("soft-button");
        tentar.setOnAction(e -> abrirPagina(paginaAtual));
        aviso.getChildren().add(tentar);
        return aviso;
    }

    private <T> TableColumn<T, String> coluna(String titulo, java.util.function.Function<T, String> valor, double largura) {
        TableColumn<T, String> coluna = new TableColumn<>(titulo);
        coluna.setCellValueFactory(c -> new SimpleStringProperty(valor.apply(c.getValue()) == null ? "" : valor.apply(c.getValue())));
        coluna.setPrefWidth(largura);
        return coluna;
    }

    private Dialog<ButtonType> dialog(String titulo, String confirmar, String cancelar) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.initOwner(stage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType(confirmar, ButtonBar.ButtonData.OK_DONE), new ButtonType(cancelar, ButtonBar.ButtonData.CANCEL_CLOSE));
        return dialog;
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(12); grid.setPadding(new Insets(12));
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(30);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(70);
        grid.getColumnConstraints().addAll(c1, c2);
        return grid;
    }

    private void addField(GridPane grid, int row, String titulo, Node campo) {
        Label label = label(titulo);
        grid.add(label, 0, row); grid.add(campo, 1, row);
        if (campo instanceof Region region) region.setMaxWidth(Double.MAX_VALUE);
    }

    private Label label(String text) { Label label = new Label(text); label.getStyleClass().add("form-label"); return label; }
    private Button primary(String text) { Button button = new Button(text); button.getStyleClass().add("primary-button"); return button; }
    private Region spacer(double width) { Region spacer = new Region(); if (width > 0) spacer.setMinHeight(width); HBox.setHgrow(spacer, Priority.ALWAYS); return spacer; }

    private boolean confirmar(String texto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.CANCEL, ButtonType.OK);
        alert.setTitle("Confirmar ação"); alert.setHeaderText(null); alert.initOwner(stage);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    private void alerta(String texto) {
        Alert alert = new Alert(Alert.AlertType.WARNING, texto, ButtonType.OK);
        alert.setTitle("PetCare"); alert.setHeaderText(null); alert.initOwner(stage); alert.showAndWait();
    }

    private void alertaInfo(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, texto, ButtonType.OK);
        alert.setTitle("PetCare"); alert.setHeaderText(null); alert.initOwner(stage); alert.showAndWait();
    }

    private String formatarValor(String valor) {
        try { return NumberFormat.getCurrencyInstance(PT_BR).format(new BigDecimal(valor.replace(',', '.'))); }
        catch (RuntimeException ex) { return valor == null ? "" : valor; }
    }

    private String normalizarValor(String valor) {
        String limpo = valor.trim().replace("R$", "").replace(" ", "");
        return limpo.contains(",") ? limpo.replace(".", "").replace(',', '.') : limpo;
    }

    private static final class ObservableListBuilder {
        private ObservableListBuilder() { }
        static <T> void set(TableView<T> tabela, java.util.Collection<T> dados) {
            tabela.setItems(FXCollections.observableArrayList(dados));
        }
    }

    public static void main(String[] args) { launch(args); }
}
