package spesa;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ziz
 */
public class Prodotto {
    
    private String costo, marca, nome;

    public Prodotto(String costo, String marca, String nome) {
        this.costo = costo;
        this.marca = marca;
        this.nome = nome;
    }

    public String getCosto() {
        return costo;
    }

    public String getMarca() {
        return marca;
    }

    public String getNome() {
        return nome;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    
    
}