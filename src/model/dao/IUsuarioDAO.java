package model.dao;

public interface IUsuarioDAO {

    public String InserirUsuario(String tabela, Usuario usuario, Pessoa pessoa);
    public Boolean VerificarUsuarioExistente(String tabela, Usuario usuario);
    public Boolean VerificarLogin(String tabela, Usuario usuario);
    public Boolean Usuariomaster(Usuario usuario);


}
