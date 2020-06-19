package com.daniel.lojamobile.modelo.beans;

public class Caixa {
    private int codigo;
    private String data;
    private String in;
    private String out;
    private float trocoIn;
    private float trocoFin;
    private String status;
    private int funcionario;
    private int empresa;

    public int getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(int funcionario) {
        this.funcionario = funcionario;
    }

    public int getEmpresa() {
        return empresa;
    }

    public void setEmpresa(int empresa) {
        this.empresa = empresa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public float getTrocoIn() {
        return trocoIn;
    }

    public void setTrocoIn(float trocoIn) {
        this.trocoIn = trocoIn;
    }

    public float getTrocoFin() {
        return trocoFin;
    }

    public void setTrocoFin(float trocoFin) {
        this.trocoFin = trocoFin;
    }
}
