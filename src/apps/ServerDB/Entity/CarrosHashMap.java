package apps.ServerDB.Entity;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;

import apps.Interfaces.CarrosInterface;
import apps.Records.Carro;
import apps.Categoria;

/**
 * Classe que representa um conjunto de carros usando uma hashTable.
 * implementa o RMI para ser acessível remotamente.
 */
public class CarrosHashMap extends UnicastRemoteObject implements CarrosInterface {

    private static final long serialVersionUID = 1L;
    private final Hashtable<String, Carro> carros;

    public CarrosHashMap() throws RemoteException {
        super();
        carros = new Hashtable<>();
    }

    @Override
    public Carro adicionar(Carro carro) throws IllegalArgumentException {

        //verifica se o carro já existe
        if (carros.containsKey(carro.renavam())) {
            throw new IllegalArgumentException("já existe um carro com o renavam " + carro.renavam());
        }

        System.out.println("\nAdicionando carro: " + carro);

        return carros.put(carro.renavam(), carro);
    }

    @Override
    public Carro remover(String renavam) throws IllegalArgumentException {

        //verifica se o carro existe
        if (!carros.containsKey(renavam)) {
            throw new IllegalArgumentException("não existe um carro com o renavam " + renavam);
        }

        System.out.println("\nRemovendo carro com renavam: " + renavam);

        return carros.remove(renavam);
    }

    @Override
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

    @Override
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

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) {
        LinkedList<Carro> carrosNome = new LinkedList<>();
        carros.values().stream().filter(carro -> carro.nome().equals(nome)).forEach(carrosNome::add);

        //ordenar por nome
        carrosNome.sort(Comparator.comparing(Carro::nome));
        return carrosNome;
    }

    @Override
    public Carro getCarroByRenavam(String renavam) {
        return carros.get(renavam);
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException {

        //verifica se o carro existe
        if (!carros.containsKey(renavam)) {
            throw new IllegalArgumentException("não existe um carro com o renavam " + renavam);
        }

        return carros.put(renavam, carro);
    }

    @Override
    public int getQuantidade() {
        return carros.size();
    }

}
