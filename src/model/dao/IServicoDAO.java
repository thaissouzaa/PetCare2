package model.dao;

import java.util.ArrayList;

public interface IServicoDAO {
    public String BuscarServico(String busca);
    public ArrayList<Servico> SelecionarTodosServicos();
    public String InserirServico(Servico servico);
    public boolean AtualizarServico(Servico servico);
    public boolean RemoverServico(Servico servico);
}
