package apps.Interfaces.ServerDB;

import apps.Categoria;
import apps.Records.Carro;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 * Interface para representar um conjunto de carros e suas operações.
 */
public interface DBCarrosInterface extends Remote {
    /**
     * Adiciona um carro à coleção.
     * @param carro O carro a ser adicionado.
     */
    Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException;

    /**
     * Remove um carro da coleção.
     * @param renavam O renavam do carro a ser removido.
     */
    Carro remover(String renavam) throws IllegalArgumentException, RemoteException;

    /**
     * Remove todos os carros com um determinado nome da coleção.
     * @param nome O nome dos carros a serem removidos.
     * @return A coleção de carros com os carros removidos.
     */
    LinkedList<Carro> removerPorNome(String nome) throws RemoteException;

    /**
     * @param categoria A categoria dos carros a serem retornados, ou null para todos os carros.
     * @return coleção de carros.
     */
    LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException;

    /**
     * @param nome O nome dos carros a serem retornados.
     * @return coleção de carros.
     */
    LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException;

    /**
     * @param renavam O renavam do carro a ser retornado.
     * @return O carro com o renavam especificado, ou null se não existir.
     */
    Carro getCarroByRenavam(String renavam) throws RemoteException;

    /**
     * altera os dados de um carro
     * @param renavam O renavam do carro a ser alterado.
     * @param carro O carro com os novos dados.
     * @return O carro com os novos dados.
     */
    Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException;

    /**
     * Retorna a quantidade de carros na coleção.
     * @return A quantidade de carros na coleção.
     */
    int getQuantidade() throws RemoteException;
}
