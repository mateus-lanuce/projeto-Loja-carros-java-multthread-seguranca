package apps.Records;

import apps.Categoria;

import java.io.Serializable;

public record Carro(String renavam, String nome, Categoria categoria, int anoFabricacao, double preco) implements Serializable {

    public Carro {
        if (renavam == null || renavam.isBlank()) {
            throw new IllegalArgumentException("Renavam não pode ser nulo ou vazio");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        if (anoFabricacao < 1900) {
            throw new IllegalArgumentException("Ano de fabricação não pode ser menor que 1900");
        }
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço não pode ser menor ou igual a zero");
        }
    }

    //formato de impressão
    @Override
    public String toString() {
        return "\nRenavam: " + renavam + "\n"
                + "Nome: " + nome + "\n"
                + "Categoria: " + categoria + "\n"
                + "Ano de fabricação: " + anoFabricacao + "\n"
                + "Preço: " + String.format("R$ %.2f", preco);
    }
}
