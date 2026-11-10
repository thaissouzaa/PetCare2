package controller;

import model.dao.Animal;
import model.dao.Conexao;
import model.dao.CRUD;
import model.dao.Pessoa;
import model.dao.Raca;

import java.sql.Statement;
import java.util.ArrayList;

public class ClienteController {
    private CRUD CRUDCliente;

    public boolean conectaBD(String db) {
        if (CRUDCliente != null) return true;
        try {
            Conexao conn = new Conexao();
            conn.conectaBD(db);
            Statement s = conn.getS();
            if (s == null) return false;
            CRUDCliente = new CRUD(s);
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Pessoa> SelecionarClientes() {
        try { return CRUDCliente.SelecionarClientes(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public Pessoa SelecionarCliente(int codigo) {
        try { return CRUDCliente.SelecionarCliente(codigo); }
        catch (Exception e) { e.printStackTrace(); return null; }
    }

    public boolean AtualizarCliente(Pessoa pessoa) {
        try { return CRUDCliente.AtualizarCliente(pessoa); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean ExcluirCliente(int codigo) {
        try { return CRUDCliente.ExcluirCliente(codigo); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public ArrayList<Animal> SelecionarPetsCliente(int codigoPessoa) {
        try { return CRUDCliente.SelecionarPetsCliente(codigoPessoa); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public ArrayList<Raca> SelecionarRacas() {
        try { return CRUDCliente.SelecionarRacas(); }
        catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean InserirPet(Animal animal) {
        try { return CRUDCliente.InserirPet(animal); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean AtualizarPet(Animal animal) {
        try { return CRUDCliente.AtualizarPet(animal); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean ExcluirPet(int codigoPessoa, int codigoPet) {
        try { return CRUDCliente.ExcluirPet(codigoPessoa, codigoPet); }
        catch (Exception e) { e.printStackTrace(); return false; }
    }
}
