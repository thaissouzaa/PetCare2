package controller;

import model.dao.CRUD;
import model.dao.Conexao;
import model.dao.Servico;

import java.sql.Statement;

public class ServicoController {

    private CRUD CRUDServico;
    public ServicoController() {}

    public boolean conectaBD(String db) {
        try {
            Conexao conn = new Conexao();
            conn.conectaBD(db);

            Statement s = conn.getS();

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
}