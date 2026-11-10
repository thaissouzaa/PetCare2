package model.dao;

public class Plano {
    private int codigo;
    private String nome;
    private String recorrencia;

    public Plano(int codigo, String nome, String recorrencia) {
        this.codigo = codigo;
        this.nome = nome;
        this.recorrencia = recorrencia;
    }

    public Plano(String nome, String recorrencia) {
        this(0, nome, recorrencia);
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRecorrencia() { return recorrencia; }
    public void setRecorrencia(String recorrencia) { this.recorrencia = recorrencia; }
}
