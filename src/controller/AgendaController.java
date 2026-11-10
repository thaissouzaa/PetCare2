package controller;

import model.dao.CRUD;
import model.dao.Conexao;

import java.sql.Statement;
import java.util.ArrayList;

public class AgendaController {
    private CRUD CRUDAgenda;

    public boolean conectaBD(String db) {
        if (CRUDAgenda != null) return true;
        try {
            Conexao conn = new Conexao();
            conn.conectaBD(db);
            Statement s = conn.getS();
            if (s == null) return false;
            CRUDAgenda = new CRUD(s);
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public ArrayList<String> SelecionarAgendamentos(String login) {
        try { return CRUDAgenda.SelecionarAgendamentos(login); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public ArrayList<String> SelecionarTodosAgendamentos() {
        try { return CRUDAgenda.SelecionarTodosAgendamentos(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public ArrayList<String> SelecionarResumoServicosAgendados() {
        try { return CRUDAgenda.SelecionarResumoServicosAgendados(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean InserirAgendamento(String login, String inicio, int codigoServico) {
        try { return CRUDAgenda.InserirAgendamento(login, inicio, codigoServico); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean InserirAgendamentoPessoa(int codigoPessoa, String inicio, int codigoServico, boolean confirmar) {
        try { return CRUDAgenda.InserirAgendamentoPessoa(codigoPessoa, inicio, codigoServico, confirmar); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean AtualizarStatusAgenda(String login, int codigo, boolean confirmar) {
        try { return CRUDAgenda.AtualizarStatusAgenda(login, codigo, confirmar); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean RemoverAgendamento(String login, int codigo) {
        try { return CRUDAgenda.RemoverAgendamento(login, codigo); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }
}
