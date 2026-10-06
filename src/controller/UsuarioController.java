package controller;

import model.dao.CRUD;
import model.dao.Conexao;
import model.dao.Pessoa;
import model.dao.Usuario;

import java.sql.Statement;

public class UsuarioController {

    private CRUD CRUDUsuario;
    private String tabela = "usuario";

    public UsuarioController() {}

    public boolean conectaBD(String db) {
        try {
            Conexao conn = new Conexao();
            conn.conectaBD(db);
            Statement s = conn.getS();
            CRUDUsuario = new CRUD(s);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Inserir Usuario

    public String InserirUsuario(String tabela, Usuario usuario, Pessoa pessoa){
        try {
            return this.CRUDUsuario.InserirUsuario(tabela, usuario, pessoa);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return "Não foi possível adicionar usuário.";
    }

    //Verificar se usuário já não existe

    public Boolean VerificarUsuarioExiste(String tabela, Usuario usuario){
        try {
            return this.CRUDUsuario.VerificarUsuarioExistente(tabela, usuario);
        } catch (Exception e) {
            e.printStackTrace();
        } return false;
    }

    //Verificar Login

    public Boolean VerificarLogin(String tabela, Usuario usuario){
        try {
            return this.CRUDUsuario.VerificarLogin(tabela, usuario);
        } catch (Exception e) {
            e.printStackTrace();
        } return false;
    }

    //Verificar Usuário Master

    public Boolean UsuarioMaster(String tabela,Usuario usuario){
        try{
            return this.CRUDUsuario.Usuariomaster(usuario);
        } catch (Exception e) {
            e.printStackTrace();
        } return false;
    }

}
