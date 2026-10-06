package model.dao;

public class Usuario {

    private String login;
    private String senha;
    private int IndicadorAtivo;

    public Usuario(String login,String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getIndicadorAtivo() {
        return IndicadorAtivo;
    }

    public void setIndicadorAtivo(int indicadorAtivo) {
        IndicadorAtivo = indicadorAtivo;
    }

}
