package apps.ServerDB.Controller;

import apps.Categoria;
import apps.Interfaces.ServerDB.ServerDBInterface;
import apps.Records.Carro;
import apps.Records.IpPort;
import apps.ServerDB.Entity.CarrosDataBase;

import java.io.Serial;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

public class ControlDB extends UnicastRemoteObject implements ServerDBInterface {
    @Serial
    private static final long serialVersionUID = 1L;

    private LinkedList<ServerDBInterface> replicDBsTotal;
    private LinkedList<ServerDBInterface> replicDBsConnected;
    private ArrayList<IpPort> ports;


    final CarrosDataBase carrosDataBase;

    public ControlDB(ArrayList<IpPort> ports) throws RemoteException {
        super();
        this.replicDBsTotal = new LinkedList<>();
        this.replicDBsConnected = new LinkedList<>();
        this.ports = ports;
        this.connectDB(ports);
        this.validateReplicas();
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

        if (replicDBsConnected.isEmpty()) {
            System.out.println("Nenhuma replica disponivel tentando reconectar");
            this.connectDB(ports);
        }
    }

    /**
     * função de conexão com as replicas
     * @param ports lista de portas para conexão
     */
    private void connectDB(ArrayList<IpPort> ports) {
        for (IpPort port : ports){
            try {
                Registry registryDB = LocateRegistry.getRegistry(port.ip(), port.port());
                ServerDBInterface serverDB = (ServerDBInterface) registryDB.lookup("Carros");
                replicDBsTotal.add(serverDB);
                System.out.println("Conexão com o servidor de bancos de dados: "+ port.ip() + " feita na porta " + port.port());
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao conectar com o servidor "+ port.ip() + " na porta " + port.port());
                e.printStackTrace();
            }
        }

    }

    /**
     * sincroniza a adição de um carro nas replicas.
     * o retorno deve ser feito quando pelo menos uma replica tiver salvo o dado.
     * mas deve ser feito de forma assíncrona nas demais replicas.
     * @param carro O carro a ser adicionado.
     * @param sync se a operação deve ser sincronizada.
     */
    private Carro sync_adicionar(Carro carro, boolean sync) throws IllegalArgumentException {
        //adiciona o carro na replica local
        Carro carroLocal = carrosDataBase.adicionar(carro);

        // se a operação está vindo de uma replica, não é necessário sincronizar com as demais
        if (!sync) {
            return carroLocal;
        }

        // valida as replicas conectadas
        validateReplicas();

        // adicionar na primeira replica
        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).adicionar(carro, false);
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
                        replica.adicionar(carro, false);
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

    private LinkedList<Carro> sync_removerPorNome(String nome, boolean sync) throws RemoteException {
        //remove o carro na replica local
        LinkedList<Carro> carros = carrosDataBase.removerPorNome(nome);

        // se a operação está vindo de uma replica, não é necessário sincronizar com as demais
        if (!sync) {
            return carros;
        }

        validateReplicas();

        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).removerPorNome(nome, false);
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
                        replica.removerPorNome(nome, false);
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

    private Carro sync_remover(String renavam, boolean sync) throws IllegalArgumentException, RemoteException {
        //remove o carro na replica local
        Carro carroLocal = carrosDataBase.remover(renavam);

        // se a operação está vindo de uma replica, não é necessário sincronizar com as demais
        if (!sync) {
            return carroLocal;
        }

        validateReplicas();

        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).remover(renavam, false);
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
                        replica.remover(renavam, false);
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

    private Carro sync_alterar(String renavam, Carro carro, boolean sync) throws IllegalArgumentException, RemoteException {
        //adiciona o carro na replica local
        Carro carroLocal = carrosDataBase.alterar(renavam, carro);

        // se a operação está vindo de uma replica, não é necessário sincronizar com as demais
        if (!sync) {
            return carroLocal;
        }

        // valida as replicas conectadas
        validateReplicas();

        // adicionar na primeira replica
        Thread t = new Thread(() -> {
            try {
                //TODO: rever a logica para o caso de falha na primeira replica
                replicDBsConnected.get(0).alterar(renavam, carro, false);
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
                        replica.alterar(renavam, carro, false);
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
        return sync_adicionar(carro, true);
    }

    @Override
    public Carro adicionar(Carro carro, boolean sync) throws IllegalArgumentException, RemoteException {
        return sync_adicionar(carro, sync);
    }

    @Override
    public Carro remover(String renavam) throws IllegalArgumentException, RemoteException {
        return sync_remover(renavam, true);
    }

    @Override
    public Carro remover(String renavam, boolean sync) throws IllegalArgumentException, RemoteException {
        return sync_remover(renavam, sync);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome) throws RemoteException {
        return sync_removerPorNome(nome, true);
    }

    @Override
    public LinkedList<Carro> removerPorNome(String nome, boolean sync) throws RemoteException {
        return sync_removerPorNome(nome, sync);
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
        return sync_alterar(renavam, carro, true);
    }

    @Override
    public Carro alterar(String renavam, Carro carro, boolean sync) throws IllegalArgumentException, RemoteException {
        return sync_alterar(renavam, carro, sync);
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
