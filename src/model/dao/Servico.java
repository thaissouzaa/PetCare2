package model.dao;

public class Servico {

    private int codigo;
    private String nome;
    private String Descricao;
    private String duracao;
    private String valor;

    //cadastrar um serviço novo
    public Servico(String nome, String descricao, String duracao, String valor) {
        this.nome = nome;
        Descricao = descricao;
        this.duracao = duracao;
        this.valor = valor;
    }

    //servico que já existe
    public Servico(Integer codigo, String nome, String descricao, String duracao, String valor) {
        this.codigo = codigo;
        this.nome = nome;
        Descricao = descricao;
        this.duracao = duracao;
        this.valor = valor;
    }

    public Servico() {

    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao(String descricao) {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
