package model.dao;

import java.util.ArrayList;

public interface IClienteDAO {
    public ArrayList<Pessoa> SelecionarClientes();
    public Pessoa SelecionarCliente(int codigo);
    public boolean AtualizarCliente(Pessoa pessoa);
    public boolean ExcluirCliente(int codigo);
    public ArrayList<Animal> SelecionarPetsCliente(int codigoPessoa);
    public ArrayList<Raca> SelecionarRacas();
    public boolean InserirPet(Animal animal);
    public boolean AtualizarPet(Animal animal);
    public boolean ExcluirPet(int codigoPessoa, int codigoPet);
}
