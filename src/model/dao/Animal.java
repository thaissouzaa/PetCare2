package model.dao;

public class Animal {
    private int codigo;
    private int codigoPessoa;
    private int codigoRaca;
    private String nome;
    private String nascimento;
    private String peso;
    private String raca;

    public Animal() { }

    public Animal(int codigoPessoa, int codigoRaca, String nome, String nascimento, String peso) {
        this.codigoPessoa = codigoPessoa;
        this.codigoRaca = codigoRaca;
        this.nome = nome;
        this.nascimento = nascimento;
        this.peso = peso;
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }
    public int getCodigoPessoa() { return codigoPessoa; }
    public void setCodigoPessoa(int codigoPessoa) { this.codigoPessoa = codigoPessoa; }
    public int getCodigoRaca() { return codigoRaca; }
    public void setCodigoRaca(int codigoRaca) { this.codigoRaca = codigoRaca; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getNascimento() { return nascimento; }
    public void setNascimento(String nascimento) { this.nascimento = nascimento; }
    public String getPeso() { return peso; }
    public void setPeso(String peso) { this.peso = peso; }
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
}
