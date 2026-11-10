package controller;

import model.dao.Conexao;
import model.dao.CRUD;
import model.dao.Plano;

import java.sql.Statement;
import java.util.ArrayList;

public class PlanoController {
    private CRUD crud;

    public boolean conectaBD(String db) {
        if (crud != null) return true;
        try {
            Conexao conexao = new Conexao();
            conexao.conectaBD(db);
            Statement statement = conexao.getS();
            if (statement == null) return false;
            crud = new CRUD(statement);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Plano> selecionarPlanos() {
        try { return crud.SelecionarPlanos(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean inserirPlano(Plano plano) {
        try { return crud.InserirPlano(plano); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean atualizarPlano(Plano plano) {
        try { return crud.AtualizarPlano(plano); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean desativarPlano(int codigo) {
        try { return crud.DesativarPlano(codigo); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean associarPlanoPessoa(int codigoPessoa, Integer codigoPlano) {
        try { return crud.AssociarPlanoPessoa(codigoPessoa, codigoPlano); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public Plano selecionarPlanoPessoa(String login) {
        try { return crud.SelecionarPlanoPessoa(login); }
        catch (Exception e) { e.printStackTrace(); return null; }
    }

    public Plano selecionarPlanoPessoa(int codigoPessoa) {
        try { return crud.SelecionarPlanoPessoa(codigoPessoa); }
        catch (Exception e) { e.printStackTrace(); return null; }
    }

}
