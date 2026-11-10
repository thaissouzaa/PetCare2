package model.dao;

import java.util.ArrayList;

public interface IAgendaDAO {
    public ArrayList<String> SelecionarAgendamentos(String login);
    public ArrayList<String> SelecionarTodosAgendamentos();
    public boolean InserirAgendamento(String login, String inicio, int codigoServico);
    public boolean AtualizarStatusAgenda(String login, int codigo, boolean confirmar);
    public boolean RemoverAgendamento(String login, int codigo);
}
