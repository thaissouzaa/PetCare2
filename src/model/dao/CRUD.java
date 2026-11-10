package model.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CRUD implements IUsuarioDAO, IServicoDAO, IAgendaDAO, IClienteDAO {
    private Statement s;

    public CRUD(Statement s) { this.s = s; }

    public String InserirUsuario(String tabela, Usuario usuario, Pessoa pessoa) {
        String SQLPessoa = "INSERT INTO pessoa (NOME, EMAIL, CPF, ENDERECO, CEP, TELEFONE, CIDADE, INDICADORATIVO) VALUES ('" +
                textoSql(pessoa.getNome()) + "', '" + textoSql(pessoa.getEmail()) + "', '" + textoSql(pessoa.getCpf()) + "', '" +
                textoSql(pessoa.getEndereco()) + "', '" + textoSql(pessoa.getCep()) + "', '" + textoSql(pessoa.getTelefone()) +
                "', '" + textoSql(pessoa.getCidade()) + "', 1) RETURNING Codigo";
        try {
            ResultSet resultado = s.executeQuery(SQLPessoa);
            if (resultado.next()) {
                int codigoPessoa = resultado.getInt("Codigo");
                String SQLUsuario = "INSERT INTO Usuario (CodigoPessoa, Login, Senha, IndicadorMaster, IndicadorAtivo) VALUES (" +
                        codigoPessoa + ", '" + textoSql(usuario.getLogin()) + "', '" + textoSql(usuario.getSenha()) + "', 0, 1)";
                s.executeUpdate(SQLUsuario);
                return "Usuário cadastrado com sucesso!";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Não foi possível cadastrar o usuário.";
    }

    public Boolean VerificarUsuarioExistente(String tabela, Usuario usuario) {
        try { return s.executeQuery("SELECT 1 FROM USUARIO WHERE LOGIN = '" + textoSql(usuario.getLogin()) + "'").next(); }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Boolean VerificarLogin(String tabela, Usuario usuario) {
        String SQL = "SELECT USUARIO.* FROM USUARIO JOIN PESSOA ON PESSOA.CODIGO = USUARIO.CODIGOPESSOA " +
                "WHERE (PESSOA.CPF = '" + textoSql(usuario.getLogin()) + "' OR PESSOA.EMAIL = '" + textoSql(usuario.getLogin()) + "') " +
                "AND USUARIO.SENHA = '" + textoSql(usuario.getSenha()) + "' AND USUARIO.INDICADORATIVO = 1";
        try { return s.executeQuery(SQL).next(); }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public String BuscarNomeUsuario(Usuario usuario) {
        String SQL = "SELECT P.NOME FROM USUARIO U JOIN PESSOA P ON P.CODIGO = U.CODIGOPESSOA " +
                "WHERE (P.CPF = '" + textoSql(usuario.getLogin()) + "' OR P.EMAIL = '" + textoSql(usuario.getLogin()) + "') " +
                "AND U.SENHA = '" + textoSql(usuario.getSenha()) + "' AND U.INDICADORATIVO = 1";
        try {
            ResultSet resultado = s.executeQuery(SQL);
            return resultado.next() ? resultado.getString("NOME") : "";
        } catch (SQLException e) { e.printStackTrace(); return ""; }
    }

    public Boolean Usuariomaster(Usuario usuario) {
        String SQL = "SELECT USUARIO.INDICADORMASTER FROM USUARIO JOIN PESSOA ON PESSOA.CODIGO = USUARIO.CODIGOPESSOA " +
                "WHERE (PESSOA.CPF = '" + textoSql(usuario.getLogin()) + "' OR PESSOA.EMAIL = '" + textoSql(usuario.getLogin()) + "') " +
                "AND USUARIO.SENHA = '" + textoSql(usuario.getSenha()) + "'";
        try {
            ResultSet resultado = s.executeQuery(SQL);
            return resultado.next() && resultado.getInt("IndicadorMaster") == 1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public String BuscarServico(String busca) {
        String SQL;
        try {
            SQL = "SELECT * FROM SERVICO WHERE CODIGO = " + Integer.parseInt(busca) + " AND INDICADORATIVO = 1";
        } catch (NumberFormatException e) {
            SQL = "SELECT * FROM SERVICO WHERE LOWER(NOME) LIKE LOWER('%" + textoSql(busca) + "%') AND INDICADORATIVO = 1";
        }
        try {
            ResultSet resultado = s.executeQuery(SQL);
            if (!resultado.next()) return "Serviço não encontrado";
            java.math.BigDecimal valor = new java.math.BigDecimal(converterValor(resultado.getString("VALOR")));
            return "\n===== SERVIÇO ENCONTRADO =====" + "\nCódigo: " + resultado.getInt("CODIGO") +
                    "\nNome: " + resultado.getString("NOME") + "\nDescrição: " + resultado.getString("DESCRICAO") +
                    "\nDuração: " + resultado.getInt("DURACAO") + " minutos" +
                    "\nValor: R$ " + String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.2f", valor);
        } catch (SQLException e) { e.printStackTrace(); return "Erro ao buscar serviço."; }
    }

    public ArrayList<Servico> SelecionarTodosServicos() {
        ArrayList<Servico> lista = new ArrayList<>();
        try {
            ResultSet r = s.executeQuery("SELECT * FROM SERVICO WHERE INDICADORATIVO = 1 ORDER BY NOME");
            while (r.next()) {
                Servico servico = new Servico();
                servico.setCodigo(r.getInt("CODIGO"));
                servico.setNome(r.getString("NOME"));
                servico.setDescricao(r.getString("DESCRICAO"));
                servico.setDuracao(r.getString("DURACAO"));
                servico.setValor(r.getString("VALOR"));
                lista.add(servico);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public ArrayList<Plano> SelecionarPlanos() {
        ArrayList<Plano> lista = new ArrayList<>();
        try {
            ResultSet r = s.executeQuery("SELECT CODIGO, NOME, RECORRENCIA FROM PLANO WHERE INDICADORATIVO = 1 ORDER BY NOME");
            while (r.next()) lista.add(new Plano(r.getInt("CODIGO"), r.getString("NOME"), r.getString("RECORRENCIA")));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean InserirPlano(Plano plano) {
        String SQL = "INSERT INTO PLANO (NOME, RECORRENCIA, INDICADORATIVO) VALUES ('" + textoSql(plano.getNome()) + "', '" +
                textoSql(plano.getRecorrencia()) + "', 1)";
        try { return s.executeUpdate(SQL) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean AtualizarPlano(Plano plano) {
        String SQL = "UPDATE PLANO SET NOME = '" + textoSql(plano.getNome()) + "', RECORRENCIA = '" + textoSql(plano.getRecorrencia()) +
                "' WHERE CODIGO = " + plano.getCodigo() + " AND INDICADORATIVO = 1";
        try { return s.executeUpdate(SQL) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean DesativarPlano(int codigo) {
        try { return s.executeUpdate("UPDATE PLANO SET INDICADORATIVO = 0 WHERE CODIGO = " + codigo + " AND INDICADORATIVO = 1") > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean AssociarPlanoPessoa(int codigoPessoa, Integer codigoPlano) {
        String plano = codigoPlano == null ? "NULL" : String.valueOf(codigoPlano);
        try { return s.executeUpdate("UPDATE PESSOA SET CODIGOPLANO = " + plano + " WHERE CODIGO = " + codigoPessoa) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Plano SelecionarPlanoPessoa(String login) {
        String SQL = "SELECT PL.CODIGO, PL.NOME, PL.RECORRENCIA FROM PESSOA P JOIN PLANO PL ON PL.CODIGO = P.CODIGOPLANO " +
                "WHERE (P.CPF = '" + textoSql(login) + "' OR P.EMAIL = '" + textoSql(login) + "') AND PL.INDICADORATIVO = 1";
        try {
            ResultSet r = s.executeQuery(SQL);
            return r.next() ? new Plano(r.getInt("CODIGO"), r.getString("NOME"), r.getString("RECORRENCIA")) : null;
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public Plano SelecionarPlanoPessoa(int codigoPessoa) {
        String SQL = "SELECT PL.CODIGO, PL.NOME, PL.RECORRENCIA FROM PESSOA P JOIN PLANO PL ON PL.CODIGO = P.CODIGOPLANO " +
                "WHERE P.CODIGO = " + codigoPessoa;
        try {
            ResultSet r = s.executeQuery(SQL);
            return r.next() ? new Plano(r.getInt("CODIGO"), r.getString("NOME"), r.getString("RECORRENCIA")) : null;
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ArrayList<String> SelecionarResumoServicosAgendados() {
        ArrayList<String> lista = new ArrayList<>();
        String SQL = "SELECT S.NOME AS SERVICO, COUNT(DISTINCT A.CODIGO) AS TOTAL, " +
                "COUNT(DISTINCT CASE WHEN A.INDICADORATIVO = 1 THEN A.CODIGO END) AS CONFIRMADOS, " +
                "COUNT(DISTINCT CASE WHEN A.INDICADORATIVO = 0 THEN A.CODIGO END) AS PENDENTES " +
                "FROM AGENDA A JOIN SERVICOAGENDA SA ON SA.CODIGOAGENDA = A.CODIGO " +
                "JOIN SERVICO S ON S.CODIGO = SA.CODIGOSERVICO GROUP BY S.CODIGO, S.NOME ORDER BY S.NOME";
        try {
            ResultSet r = s.executeQuery(SQL);
            while (r.next()) lista.add(r.getString("SERVICO") + "\t" + r.getInt("TOTAL") + "\t" +
                    r.getInt("CONFIRMADOS") + "\t" + r.getInt("PENDENTES"));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public String InserirServico(Servico servico) {
        String SQL = "INSERT INTO SERVICO (NOME, DESCRICAO, DURACAO, VALOR, INDICADORATIVO) VALUES ('" +
                textoSql(servico.getNome()) + "', '" + textoSql(servico.getDescricao()) + "', " + servico.getDuracao() +
                ", " + converterValor(servico.getValor()) + ", 1)";
        try { return s.executeUpdate(SQL) > 0 ? "Serviço cadastrado com sucesso!" : "Não foi possível cadastrar o serviço."; }
        catch (SQLException | NumberFormatException e) { e.printStackTrace(); return "Não foi possível cadastrar o serviço."; }
    }

    public boolean AtualizarServico(Servico servico) {
        String SQL = "UPDATE SERVICO SET NOME = '" + textoSql(servico.getNome()) + "', DESCRICAO = '" + textoSql(servico.getDescricao()) +
                "', DURACAO = " + servico.getDuracao() + ", VALOR = " + converterValor(servico.getValor()) +
                " WHERE CODIGO = " + servico.getCodigo() + " AND INDICADORATIVO = 1";
        try { return s.executeUpdate(SQL) > 0; }
        catch (SQLException | NumberFormatException e) { e.printStackTrace(); return false; }
    }

    public boolean RemoverServico(Servico servico) {
        try { return s.executeUpdate("UPDATE SERVICO SET INDICADORATIVO = 0 WHERE CODIGO = " + servico.getCodigo() + " AND INDICADORATIVO = 1") > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<String> SelecionarAgendamentos(String login) {
        ArrayList<String> lista = new ArrayList<>();
        String SQL = "SELECT A.CODIGO, TO_CHAR(A.DATAHORAINICIO, 'DD/MM/YYYY HH24:MI') AS DATAHORAINICIO, " +
                "TO_CHAR(A.DATAHORAFINAL, 'DD/MM/YYYY HH24:MI') AS DATAHORAFINAL, A.INDICADORATIVO " +
                "FROM AGENDA A JOIN PESSOA P ON P.CODIGO = A.CODIGOPESSOA WHERE P.CPF = '" + textoSql(login) +
                "' OR P.EMAIL = '" + textoSql(login) + "' ORDER BY A.DATAHORAINICIO";
        try {
            ResultSet r = s.executeQuery(SQL);
            while (r.next()) lista.add("Código: " + r.getInt("CODIGO") + " | Início: " + r.getString("DATAHORAINICIO") +
                    " | Fim: " + r.getString("DATAHORAFINAL") + " | " + (r.getInt("INDICADORATIVO") == 1 ? "Confirmado" : "Pendente"));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public ArrayList<String> SelecionarTodosAgendamentos() {
        ArrayList<String> lista = new ArrayList<>();
        String SQL = "SELECT A.CODIGO, P.NOME AS CLIENTE, P.CPF, TO_CHAR(A.DATAHORAINICIO, 'DD/MM/YYYY HH24:MI') AS INICIO, " +
                "TO_CHAR(A.DATAHORAFINAL, 'DD/MM/YYYY HH24:MI') AS FIM, COALESCE(STRING_AGG(S.NOME, ', '), 'Serviço não informado') AS SERVICOS, A.INDICADORATIVO " +
                "FROM AGENDA A JOIN PESSOA P ON P.CODIGO = A.CODIGOPESSOA " +
                "LEFT JOIN SERVICOAGENDA SA ON SA.CODIGOAGENDA = A.CODIGO LEFT JOIN SERVICO S ON S.CODIGO = SA.CODIGOSERVICO " +
                "GROUP BY A.CODIGO, P.NOME, P.CPF, A.DATAHORAINICIO, A.DATAHORAFINAL, A.INDICADORATIVO ORDER BY A.DATAHORAINICIO";
        try {
            ResultSet r = s.executeQuery(SQL);
            while (r.next()) lista.add("Agendamento: " + r.getInt("CODIGO") + " | Cliente: " + r.getString("CLIENTE") +
                    " (CPF: " + r.getString("CPF") + ") | Serviço: " + r.getString("SERVICOS") + " | Início: " +
                    r.getString("INICIO") + " | Fim: " + r.getString("FIM") + " | " + (r.getInt("INDICADORATIVO") == 1 ? "Confirmado" : "Pendente"));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean InserirAgendamento(String login, String inicio, int codigoServico) {
        try {
            ResultSet pessoa = s.executeQuery("SELECT CODIGO FROM PESSOA WHERE CPF = '" + textoSql(login) + "' OR EMAIL = '" + textoSql(login) + "'");
            return pessoa.next() && InserirAgendamentoPessoa(pessoa.getInt("CODIGO"), inicio, codigoServico, false);
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean InserirAgendamentoPessoa(int codigoPessoa, String inicio, int codigoServico, boolean confirmar) {
        java.sql.Connection conexao = null;
        boolean autoCommitAnterior = true;
        try {
            conexao = s.getConnection();
            autoCommitAnterior = conexao.getAutoCommit();
            conexao.setAutoCommit(false);
            ResultSet pessoa = s.executeQuery("SELECT CODIGO FROM PESSOA WHERE CODIGO = " + codigoPessoa + " AND INDICADORATIVO = 1");
            if (!pessoa.next()) { conexao.rollback(); return false; }
            ResultSet servico = s.executeQuery("SELECT DURACAO FROM SERVICO WHERE CODIGO = " + codigoServico + " AND INDICADORATIVO = 1");
            if (!servico.next()) { conexao.rollback(); return false; }
            int duracao = servico.getInt("DURACAO");
            java.time.format.DateTimeFormatter formatoBR = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            java.time.LocalDateTime dataHoraInicio = java.time.LocalDateTime.parse(inicio, formatoBR);
            java.time.LocalDateTime dataHoraFinal = dataHoraInicio.plusMinutes(duracao);
            java.time.format.DateTimeFormatter formatoBanco = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String inicioBanco = dataHoraInicio.format(formatoBanco);
            String fimBanco = dataHoraFinal.format(formatoBanco);
            ResultSet agenda = s.executeQuery("INSERT INTO AGENDA (CODIGOPESSOA, DATAHORAINICIO, DATAHORAFINAL, INDICADORATIVO) VALUES (" +
                    codigoPessoa + ", TIMESTAMP '" + inicioBanco + "', TIMESTAMP '" + fimBanco + "', " + (confirmar ? 1 : 0) + ") RETURNING CODIGO");
            if (!agenda.next()) { conexao.rollback(); return false; }
            int codigoAgenda = agenda.getInt("CODIGO");
            if (s.executeUpdate("INSERT INTO SERVICOAGENDA (CODIGOSERVICO, CODIGOAGENDA) VALUES (" + codigoServico + ", " + codigoAgenda + ")") == 0) {
                conexao.rollback(); return false;
            }
            conexao.commit();
            return true;
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            try { if (conexao != null) conexao.rollback(); } catch (SQLException erroRollback) { erroRollback.printStackTrace(); }
            e.printStackTrace(); return false;
        } finally {
            try { if (conexao != null) conexao.setAutoCommit(autoCommitAnterior); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean AtualizarStatusAgenda(String login, int codigo, boolean confirmar) {
        String SQL = "UPDATE AGENDA SET INDICADORATIVO = " + (confirmar ? 1 : 0) + " WHERE CODIGO = " + codigo +
                " AND CODIGOPESSOA = (SELECT CODIGO FROM PESSOA WHERE CPF = '" + textoSql(login) + "' OR EMAIL = '" + textoSql(login) + "')";
        try { return s.executeUpdate(SQL) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean RemoverAgendamento(String login, int codigo) {
        String dono = "SELECT A.CODIGO FROM AGENDA A JOIN PESSOA P ON P.CODIGO = A.CODIGOPESSOA WHERE A.CODIGO = " + codigo +
                " AND (P.CPF = '" + textoSql(login) + "' OR P.EMAIL = '" + textoSql(login) + "')";
        java.sql.Connection conexao = null;
        boolean autoCommitAnterior = true;
        try {
            conexao = s.getConnection(); autoCommitAnterior = conexao.getAutoCommit(); conexao.setAutoCommit(false);
            if (!s.executeQuery(dono).next()) { conexao.rollback(); return false; }
            s.executeUpdate("DELETE FROM SERVICOAGENDA WHERE CODIGOAGENDA = " + codigo);
            boolean removido = s.executeUpdate("DELETE FROM AGENDA WHERE CODIGO = " + codigo) > 0;
            if (removido) conexao.commit(); else conexao.rollback();
            return removido;
        } catch (SQLException e) {
            try { if (conexao != null) conexao.rollback(); } catch (SQLException erroRollback) { erroRollback.printStackTrace(); }
            e.printStackTrace(); return false;
        } finally {
            try { if (conexao != null) conexao.setAutoCommit(autoCommitAnterior); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public ArrayList<Pessoa> SelecionarClientes() {
        ArrayList<Pessoa> lista = new ArrayList<>();
        String SQL = "SELECT P.CODIGO, P.NOME, P.CPF, P.EMAIL, P.TELEFONE, P.ENDERECO, P.CIDADE, P.CEP FROM PESSOA P " +
                "WHERE P.INDICADORATIVO = 1 AND NOT EXISTS (SELECT 1 FROM USUARIO U WHERE U.CODIGOPESSOA = P.CODIGO AND U.INDICADORMASTER = 1) ORDER BY P.NOME";
        try {
            ResultSet r = s.executeQuery(SQL);
            while (r.next()) {
                Pessoa pessoa = pessoaDe(r);
                lista.add(pessoa);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Pessoa SelecionarCliente(int codigo) {
        String SQL = "SELECT P.CODIGO, P.NOME, P.CPF, P.EMAIL, P.TELEFONE, P.ENDERECO, P.CIDADE, P.CEP FROM PESSOA P " +
                "WHERE P.CODIGO = " + codigo + " AND P.INDICADORATIVO = 1 AND NOT EXISTS (SELECT 1 FROM USUARIO U WHERE U.CODIGOPESSOA = P.CODIGO AND U.INDICADORMASTER = 1)";
        try {
            ResultSet r = s.executeQuery(SQL);
            return r.next() ? pessoaDe(r) : null;
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    private Pessoa pessoaDe(ResultSet r) throws SQLException {
        Pessoa pessoa = new Pessoa(r.getString("NOME"), r.getString("CPF"), r.getString("EMAIL"), r.getString("TELEFONE"),
                r.getString("ENDERECO"), r.getString("CIDADE"), r.getString("CEP"));
        pessoa.setCodigo(r.getInt("CODIGO"));
        return pessoa;
    }

    public boolean AtualizarCliente(Pessoa pessoa) {
        String SQL = "UPDATE PESSOA SET NOME = '" + textoSql(pessoa.getNome()) + "', EMAIL = '" + textoSql(pessoa.getEmail()) +
                "', TELEFONE = '" + textoSql(pessoa.getTelefone()) + "', ENDERECO = '" + textoSql(pessoa.getEndereco()) +
                "', CIDADE = '" + textoSql(pessoa.getCidade()) + "', CEP = '" + textoSql(pessoa.getCep()) +
                "' WHERE CODIGO = " + pessoa.getCodigo() + " AND INDICADORATIVO = 1";
        try { return s.executeUpdate(SQL) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean ExcluirCliente(int codigo) {
        String SQLCliente = "UPDATE PESSOA SET INDICADORATIVO = 0 WHERE CODIGO = " + codigo + " AND INDICADORATIVO = 1 " +
                "AND NOT EXISTS (SELECT 1 FROM USUARIO WHERE CODIGOPESSOA = " + codigo + " AND INDICADORMASTER = 1)";
        java.sql.Connection conexao = null;
        boolean autoCommitAnterior = true;
        try {
            conexao = s.getConnection(); autoCommitAnterior = conexao.getAutoCommit(); conexao.setAutoCommit(false);
            if (s.executeUpdate(SQLCliente) == 0) { conexao.rollback(); return false; }
            s.executeUpdate("UPDATE ANIMAL SET INDICADORATIVO = 0 WHERE CODIGOPESSOA = " + codigo);
            s.executeUpdate("UPDATE USUARIO SET INDICADORATIVO = 0 WHERE CODIGOPESSOA = " + codigo);
            conexao.commit(); return true;
        } catch (SQLException e) {
            try { if (conexao != null) conexao.rollback(); } catch (SQLException erroRollback) { erroRollback.printStackTrace(); }
            e.printStackTrace(); return false;
        } finally {
            try { if (conexao != null) conexao.setAutoCommit(autoCommitAnterior); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public ArrayList<Animal> SelecionarPetsCliente(int codigoPessoa) {
        ArrayList<Animal> lista = new ArrayList<>();
        String SQL = "SELECT A.CODIGO, A.CODIGORACA, A.NOME, TO_CHAR(A.NASCIMENTO, 'DD/MM/YYYY') AS NASCIMENTO, A.PESO, R.RACA " +
                "FROM ANIMAL A JOIN RACA R ON R.CODIGO = A.CODIGORACA WHERE A.CODIGOPESSOA = " + codigoPessoa +
                " AND A.INDICADORATIVO = 1 ORDER BY A.NOME";
        try {
            ResultSet r = s.executeQuery(SQL);
            while (r.next()) {
                Animal animal = new Animal(codigoPessoa, r.getInt("CODIGORACA"), r.getString("NOME"), r.getString("NASCIMENTO"), r.getString("PESO"));
                animal.setCodigo(r.getInt("CODIGO")); animal.setRaca(r.getString("RACA")); lista.add(animal);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public ArrayList<Raca> SelecionarRacas() {
        ArrayList<Raca> lista = new ArrayList<>();
        try {
            ResultSet r = s.executeQuery("SELECT CODIGO, RACA FROM RACA ORDER BY RACA");
            while (r.next()) lista.add(new Raca(r.getInt("CODIGO"), r.getString("RACA")));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean InserirPet(Animal animal) {
        try {
            String peso = new java.math.BigDecimal(animal.getPeso().replace(',', '.')).toPlainString();
            String SQL = "INSERT INTO ANIMAL (CODIGORACA, CODIGOPESSOA, NOME, NASCIMENTO, PESO, INDICADORATIVO) VALUES (" +
                    animal.getCodigoRaca() + ", " + animal.getCodigoPessoa() + ", '" + textoSql(animal.getNome()) +
                    "', TO_DATE('" + textoSql(animal.getNascimento()) + "', 'DD/MM/YYYY'), " + peso + ", 1)";
            return s.executeUpdate(SQL) > 0;
        } catch (SQLException | NumberFormatException e) { e.printStackTrace(); return false; }
    }

    public boolean AtualizarPet(Animal animal) {
        try {
            String peso = new java.math.BigDecimal(animal.getPeso().replace(',', '.')).toPlainString();
            String SQL = "UPDATE ANIMAL SET CODIGORACA = " + animal.getCodigoRaca() + ", NOME = '" + textoSql(animal.getNome()) +
                    "', NASCIMENTO = TO_DATE('" + textoSql(animal.getNascimento()) + "', 'DD/MM/YYYY'), PESO = " + peso +
                    " WHERE CODIGO = " + animal.getCodigo() + " AND CODIGOPESSOA = " + animal.getCodigoPessoa() + " AND INDICADORATIVO = 1";
            return s.executeUpdate(SQL) > 0;
        } catch (SQLException | NumberFormatException e) { e.printStackTrace(); return false; }
    }

    public boolean ExcluirPet(int codigoPessoa, int codigoPet) {
        String SQL = "UPDATE ANIMAL SET INDICADORATIVO = 0 WHERE CODIGOPESSOA = " + codigoPessoa + " AND CODIGO = " + codigoPet + " AND INDICADORATIVO = 1";
        try { return s.executeUpdate(SQL) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private String textoSql(String texto) { return texto.replace("'", "''"); }

    private String converterValor(String valor) {
        String limpo = valor.replace("R$", "").replace("\u00a0", "").replace(" ", "").trim();
        if (limpo.contains(",")) limpo = limpo.replace(".", "").replace(',', '.');
        return new java.math.BigDecimal(limpo).stripTrailingZeros().toPlainString();
    }
}
