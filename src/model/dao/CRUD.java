package model.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CRUD implements IUsuarioDAO{

    private Statement s;

    public CRUD(Statement s){
        this.s=s;
    }

//Cadastrar usuário

    public String InserirUsuario(String tabela, Usuario usuario, Pessoa pessoa){
        String SQLPessoa = "INSERT INTO pessoa (NOME, EMAIL, CPF, ENDERECO, CEP, TELEFONE, CIDADE, INDICADORATIVO) "+
                "VALUES ('"+pessoa.getNome()+"', '"+pessoa.getEmail() +"', '"+ pessoa.getCpf()+"','"+pessoa.getEndereco()+"','"+pessoa.getCep()+"','"+pessoa.getTelefone()+"','"+pessoa.getCidade()+"', 1)" +
                "RETURNING Codigo";

        try {
            ResultSet linhasafetadas = s.executeQuery(SQLPessoa);

            if(linhasafetadas.next()){
                int codigoPessoa = linhasafetadas.getInt("Codigo");

                String SQLUsuario =
                        "INSERT INTO Usuario " +
                                "(CodigoPessoa, Login, Senha, IndicadorMaster, IndicadorAtivo) " +
                                "VALUES (" + codigoPessoa + ", '" + usuario.getLogin() + "', '" + usuario.getSenha() + "', 0, 1)";

                s.executeUpdate(SQLUsuario);

                return "Usuário cadastrado com sucesso!";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Não foi possível cadastrar o usuário.";
    }

    //Verificar se usuário já não existe
    public Boolean VerificarUsuarioExistente(String tabela, Usuario usuario){
        String SQL = "SELECT 1 FROM USUARIO WHERE LOGIN = '" + usuario.getLogin() + "'";

        try {
            ResultSet linhasafetadas = s.executeQuery(SQL);

            return (linhasafetadas.next());

        } catch (SQLException e) {
            e.printStackTrace();
        } return  false;
    }

    //Login

    public Boolean VerificarLogin(String tabela, Usuario usuario) {

        String SQL = "SELECT USUARIO.* " +
                "FROM USUARIO " +
                "JOIN PESSOA ON PESSOA.CODIGO = USUARIO.CODIGOPESSOA " +
                "WHERE (PESSOA.CPF = '" + usuario.getLogin() + "' " +
                "OR PESSOA.EMAIL = '" + usuario.getLogin() + "') " +
                "AND USUARIO.SENHA = '" + usuario.getSenha() + "' " +
                "AND USUARIO.INDICADORATIVO = 1";

        try {
            ResultSet linhasafetadasLogin = s.executeQuery(SQL);

            return linhasafetadasLogin.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //Usuario Master

    public Boolean Usuariomaster(Usuario usuario) {

        String SQL = "SELECT USUARIO.INDICADORMASTER " +
                "FROM USUARIO " +
                "JOIN PESSOA ON PESSOA.CODIGO = USUARIO.CODIGOPESSOA " +
                "WHERE (PESSOA.CPF = '" + usuario.getLogin() + "' " +
                "OR PESSOA.EMAIL = '" + usuario.getLogin() + "') " +
                "AND USUARIO.SENHA = '" + usuario.getSenha() + "'";

        try {
            ResultSet linhasafetadas = s.executeQuery(SQL);

            if (linhasafetadas.next()) {

                int indicadorMaster = linhasafetadas.getInt("IndicadorMaster");

                return indicadorMaster == 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    //SERVIÇOS

    public String BuscarServico(String busca) {

        String SQL;

        try {

            int codigo = Integer.parseInt(busca);

            SQL = "SELECT * FROM SERVICO " +
                    "WHERE CODIGO = " + codigo +
                    "AND INDICADORATIVO = 1";

        } catch (NumberFormatException e) {

            SQL = "SELECT * FROM SERVICO " +
                    "WHERE LOWER(NOME) LIKE LOWER('%" + busca + "%') " +
                    "AND INDICADORATIVO = 1";
        }

        try {

            ResultSet resultado = s.executeQuery(SQL);

            if (resultado.next()) {

                String nome = resultado.getString("NOME");
                String descricao = resultado.getString("DESCRICAO");
                int duracao = resultado.getInt("DURACAO");
                double valor = resultado.getDouble("VALOR");

                return "\n===== SERVIÇO ENCONTRADO =====" +
                        "\nCódigo: " + resultado.getInt("CODIGO") +
                        "\nNome: " + nome +
                        "\nDescrição: " + descricao +
                        "\nDuração: " + duracao + " minutos" +
                        "\nValor: R$ " + String.format("%.2f", valor);

            } else {

                return "Serviço não encontrado";
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return "Erro ao buscar serviço.";
        }
    }


    public boolean RemoverServico(Servico servico) {
        String SQL = "UPDATE SERVICO SET INDICADORATIVO = 0 WHERE CODIGO = " + servico.getCodigo();

        try {
            ResultSet linhasafetadasLogin = s.executeQuery(SQL);

            return linhasafetadasLogin.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Servico> SelecionarTodosServicos(){
            String SQL = "SELECT * FROM SERVICO WHERE INDICADORATIVO = 1;";
        try {
            ResultSet rset = s.executeQuery(SQL); //cria ponteiro para a tabela
            ArrayList<Servico> lista = new ArrayList<>();
            while(rset.next()){
                Servico a = new Servico();
                a.setNome(rset.getString("nome"));
                a.setDescricao(rset.getString("descricao"));
                a.setDuracao(rset.getString("duracao"));
                a.setValor(rset.getString("valor"));
                lista.add(a);
            }
            return lista; //Deu certo!
        }catch (Exception e){
            e.printStackTrace();
        }
        return null; //deu errado!
    }

}
