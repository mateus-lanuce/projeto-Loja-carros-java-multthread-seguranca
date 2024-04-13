package apps.ServerDB.Entity;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Records.Carro;
import apps.Categoria;

/**
 * Classe que representa um conjunto de carros usando uma hashTable.
 * implementa o RMI para ser acessível remotamente.
 */
public class CarrosDataBase implements DBCarrosInterface {
    private final ConcurrentHashMap<String, Carro> carros;

    public CarrosDataBase() throws RemoteException {
        carros = new ConcurrentHashMap<>();
    }

    
    public Carro adicionar(Carro carro) throws IllegalArgumentException {

        //verifica se o carro já existe
        if (carros.containsKey(carro.renavam())) {
            throw new IllegalArgumentException("já existe um carro com o renavam " + carro.renavam());
        }

        System.out.println("\nAdicionando carro: " + carro);

        return carros.put(carro.renavam(), carro);
    }

    
    public Carro remover(String renavam) throws IllegalArgumentException {

        //verifica se o carro existe
        if (!carros.containsKey(renavam)) {
            throw new IllegalArgumentException("não existe um carro com o renavam " + renavam);
        }

        System.out.println("\nRemovendo carro com renavam: " + renavam);

        return carros.remove(renavam);
    }

    
    public LinkedList<Carro> removerPorNome(String nome) {
        LinkedList<Carro> carrosNome = new LinkedList<>();
        carros.values().stream().filter(carro -> carro.nome().equals(nome)).forEach(carrosNome::add);
        carrosNome.forEach(carro -> carros.remove(carro.renavam()));

        //ordenar por nome
        carrosNome.sort(Comparator.comparing(Carro::nome));

        System.out.println("\nRemovendo carros com nome: " + nome);
        System.out.println("Carros removidos: " + carrosNome);
        return carrosNome;
    }

    
    public LinkedList<Carro> getCarros(Categoria categoria) {
        LinkedList<Carro> carrosCategoria = new LinkedList<>();

        if (categoria == null) {
            carrosCategoria.addAll(carros.values());
            //ordenar por nome
            carrosCategoria.sort(Comparator.comparing(Carro::nome));
            return carrosCategoria;
        }

        carros.values().stream().filter(carro -> carro.categoria().equals(categoria)).forEach(carrosCategoria::add);

        //ordenar por nome
        carrosCategoria.sort(Comparator.comparing(Carro::nome));

        System.out.println("\nListando carros com categoria: " + categoria);

        carrosCategoria.forEach(System.out::println);

        return carrosCategoria;
    }

    
    public LinkedList<Carro> getCarrosByNome(String nome) {
        LinkedList<Carro> carrosNome = new LinkedList<>();
        carros.values().stream().filter(carro -> carro.nome().equals(nome)).forEach(carrosNome::add);

        //ordenar por nome
        carrosNome.sort(Comparator.comparing(Carro::nome));
        return carrosNome;
    }

    
    public Carro getCarroByRenavam(String renavam) {
        return carros.get(renavam);
    }

    
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException {

        //verifica se o carro existe
        if (!carros.containsKey(renavam)) {
            throw new IllegalArgumentException("não existe um carro com o renavam " + renavam);
        }

        return carros.put(renavam, carro);
    }

    
    public int getQuantidade() {
        return carros.size();
    }

}
