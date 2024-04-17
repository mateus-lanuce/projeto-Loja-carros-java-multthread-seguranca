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

    // return a Carro object from a string in the format Carro[renavam=renavam, nome=nome, categoria=categoria, anoFabricacao=anoFabricacao, preco=preco]
    public static Carro fromString(String carroString) {
        String[] parts = carroString.split(",");
        String renavam = parts[0].split("=")[1];
        String nome = parts[1].split("=")[1];
        Categoria categoria = Categoria.valueOf(parts[2].split("=")[1]);
        int anoFabricacao = Integer.parseInt(parts[3].split("=")[1]);
        double preco = Double.parseDouble(parts[4].split("=")[1].replace("]", ""));
        return new Carro(renavam, nome, categoria, anoFabricacao, preco);
    }
}
