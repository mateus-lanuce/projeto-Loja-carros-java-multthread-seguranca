package apps.ServerDB.Controller;

import apps.Categoria;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Records.Carro;
import apps.ServerDB.Entity.CarrosDataBase;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class ControlDB extends UnicastRemoteObject implements ServerDBInterface {
    @Serial
    private static final long serialVersionUID = 1L;

    LinkedList<ServerDBInterface> replicDBsTotal;
    LinkedList<ServerDBInterface> replicDBsConnected;

    final CarrosDataBase carrosDataBase;

    public ControlDB(LinkedList<ServerDBInterface> replicas) throws RemoteException {
        super();
        this.replicDBsTotal = replicas;
        this.replicDBsConnected = new LinkedList<>();
        validateReplicas();
        carrosDataBase = new CarrosDataBase();
    }

    public ControlDB() throws RemoteException {
        super();
        this.replicDBsTotal = new LinkedList<>();
        this.replicDBsConnected = new LinkedList<>();
        validateReplicas();
        carrosDataBase = new CarrosDataBase();
    }

    /**
     * funcao de validação da conexão das replicas
     */
    private void validateReplicas() {
        replicDBsConnected.clear();
        replicDBsTotal.forEach(replica -> {
            try {
                if (replica.isAlive()) {
                    replicDBsConnected.add(replica);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * sincroniza a adição de um carro nas replicas.
     * o retorno deve ser feito quando pelo menos uma replica tiver salvo o dado.
     * mas deve ser feito de forma assíncrona nas demais replicas.
     * @param carro O carro a ser adicionado.
     */
    private synchronized Carro sync_adicionar(Carro carro) throws IllegalArgumentException {
        //adiciona o carro na replica local
        Carro carroLocal = carrosDataBase.adicionar(carro);

        // valida as replicas conectadas
        validateReplicas();

        // adicionar na primeira replica
        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).adicionar(carro);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        t.start();

        //aguarda a primeira replica salvar o dado
        try {
            t.join();

            //como a primeira replica salvou, podemos enviar para as demais assincronamente enquanto retornamos o dado
            replicDBsConnected.stream().skip(1).forEach(replica -> {
                Thread t2 = new Thread(() -> {
                    try {
                        replica.adicionar(carro);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
                t2.start();
            });

            return carroLocal;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return carroLocal;
    }

    private synchronized LinkedList<Carro> sync_removerPorNome(String nome) throws RemoteException {
        //remove o carro na replica local
        LinkedList<Carro> carros = carrosDataBase.removerPorNome(nome);

        validateReplicas();

        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).removerPorNome(nome);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        t.start();

        //aguarda a primeira replica salvar o dado
        try {
            t.join();

            //como a primeira replica salvou, podemos enviar para as demais assincronamente enquanto retornamos o dado
            replicDBsConnected.stream().skip(1).forEach(replica -> {
                Thread t2 = new Thread(() -> {
                    try {
                        replica.removerPorNome(nome);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
                t2.start();
            });

            return carros;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return carros;
    }
    private synchronized Carro sync_remover(String renavam) throws IllegalArgumentException, RemoteException {
        //remove o carro na replica local
        Carro carroLocal = carrosDataBase.remover(renavam);

        validateReplicas();

        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).remover(renavam);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        t.start();

        //aguarda a primeira replica salvar o dado
        try {
            t.join();

            //como a primeira replica salvou, podemos enviar para as demais assincronamente enquanto retornamos o dado
            replicDBsConnected.stream().skip(1).forEach(replica -> {
                Thread t2 = new Thread(() -> {
                    try {
                        replica.remover(renavam);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
                t2.start();
            });

            return carroLocal;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return carroLocal;
    }

    private synchronized Carro sync_alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        //adiciona o carro na replica local
        Carro carroLocal = carrosDataBase.alterar(renavam, carro);

        // valida as replicas conectadas
        validateReplicas();

        // adicionar na primeira replica
        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).alterar(renavam, carro);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        t.start();

        //aguarda a primeira replica salvar o dado
        try {
            t.join();

            //como a primeira replica salvou, podemos enviar para as demais assincronamente enquanto retornamos o dado
            replicDBsConnected.stream().skip(1).forEach(replica -> {
                Thread t2 = new Thread(() -> {
                    try {
                        replica.alterar(renavam, carro);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
                t2.start();
            });

            return carroLocal;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return carroLocal;
    }

    @Override
    public Carro adicionar(Carro carro) throws IllegalArgumentException, RemoteException {
        //a sincronizacao deve aguardar que o dado tenha sido salvo em uma replica antes de retornar
        return sync_adicionar(carro);
    }
    @Override
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        return sync_remover(renavam);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        return sync_removerPorNome(nome);
    }

    @Override
    public LinkedList<Carro> getCarros(Categoria categoria) throws RemoteException {
        return carrosDataBase.getCarros(categoria);
    }

    @Override
    public LinkedList<Carro> getCarrosByNome(String nome) throws RemoteException {
        return carrosDataBase.getCarrosByNome(nome);
    }

    @Override
    public Carro getCarroByRenavam(String renavam) throws RemoteException {
        return carrosDataBase.getCarroByRenavam(renavam);
    }

    @Override
    public Carro alterar(String renavam, Carro carro) throws IllegalArgumentException, RemoteException {
        return sync_alterar(renavam, carro);
    }

    @Override
    public int getQuantidade() throws RemoteException {
        return carrosDataBase.getQuantidade();
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }
}
