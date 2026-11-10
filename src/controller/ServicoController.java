package controller;

import model.dao.Conexao;
import model.dao.CRUD;
import model.dao.Servico;
import java.util.ArrayList;

import java.sql.Statement;

public class ServicoController {

    private CRUD CRUDServico;
    public ServicoController() {}

    public boolean conectaBD(String db) {
        if (CRUDServico != null) return true;
        try {
            Conexao conn = new Conexao();
            conn.conectaBD(db);

            Statement s = conn.getS();
            if (s == null) return false;

            CRUDServico = new CRUD(s);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //Buscar Serviço

    public String BuscarServico(String busca) {

        try {

            return this.CRUDServico.BuscarServico(busca);

        } catch (Exception e) {

            e.printStackTrace();
            return "Erro ao buscar serviço.";
        }
    }

    public boolean removerServico(Servico servico) {
        try {

            return this.CRUDServico.RemoverServico(servico);

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    public String InserirServico(Servico servico) {
        try { return CRUDServico.InserirServico(servico); }
        catch (Exception e) { e.printStackTrace(); return "Não foi possível cadastrar o serviço."; }
    }

    public ArrayList<Servico> SelecionarTodosServicos() {
        try { return CRUDServico.SelecionarTodosServicos(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean AtualizarServico(Servico servico) {
        try { return CRUDServico.AtualizarServico(servico); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }
}
