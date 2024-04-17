package apps.Interfaces.ServerFirewall;

import apps.Categoria;
import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerGetawayInterface;
import apps.Interfaces.ServerSecurityInterface;
import apps.Records.Carro;
import apps.Records.Message;
import apps.Records.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

public interface ServerFirewallInterface extends Remote, ServerSecurityInterface {
    /**
     * Adiciona um carro à coleção.
     * @param carro O carro a ser adicionado.
     */
    Message adicionar(Message carro) throws IllegalArgumentException, RemoteException;

    /**
     * Remove um carro da coleção.
     * @param renavam O renavam do carro a ser removido.
     */
    Message remover(Message renavam) throws IllegalArgumentException, RemoteException;

    /**
     * Remove todos os carros com um determinado nome da coleção.
     * @param nome O nome dos carros a serem removidos.
     * @return A coleção de carros com os carros removidos.
     */
    LinkedList<Message> removerPorNome(Message nome) throws RemoteException;

    /**
     * @param categoria A categoria dos carros a serem retornados, ou null para todos os carros.
     * @return coleção de carros.
     */
    LinkedList<Message> getCarros(Message categoria) throws RemoteException;

    /**
     * @param nome O nome dos carros a serem retornados.
     * @return coleção de carros.
     */
    LinkedList<Message> getCarrosByNome(Message nome) throws RemoteException;

    /**
     * @param renavam O renavam do carro a ser retornado.
     * @return O carro com o renavam especificado, ou null se não existir.
     */
    Message getCarroByRenavam(Message renavam) throws RemoteException;

    /**
     * altera os dados de um carro
     * @param renavam O renavam do carro a ser alterado.
     * @param carro O carro com os novos dados.
     * @return O carro com os novos dados.
     */
    Message alterar(Message renavam, Message carro) throws IllegalArgumentException, RemoteException;

    /**
     * Retorna a quantidade de carros na coleção.
     * @return A quantidade de carros na coleção.
     */
    int getQuantidade() throws RemoteException;

    Message login(Message email, Message password) throws RemoteException;

    void addUser(User user) throws RemoteException;

    boolean isAlive() throws RemoteException;
}
