package apps.Client.Controller;

import apps.Categoria;
import apps.Client.Model.ClientModel;
import apps.Client.View.ClientView;
import apps.Records.Carro;
import apps.Records.IpPort;
import apps.Records.User;

import java.util.ArrayList;
import java.util.LinkedList;

public class ClientController {

    private final ClientModel model;
    private final ClientView view;

    private User Client;
    private boolean isLogged = false;
    private boolean stop = false;

    public ClientController(ArrayList<IpPort> ports, int idPreferencia) {
        this.model = new ClientModel(ports, idPreferencia);
        this.view = new ClientView();
        this.start();
    }

    public void start() {

        while (!isLogged) {
            User user = view.login();
//            this.Client = model.autenticar(user.email(), user.password());
            if (this.Client != null) {
                isLogged = true;
                view.showMensagem("Usuário logado com sucesso!");
            } else {
                view.showMensagem("Usuário ou senha inválidos, tentar novamente...");
                //parar o programa por 2 segundos
                delay(2000);
                view.limparTela();
            }
        }

        while (!stop) {

            view.halfLine();
            view.showCabecalho();
            if (Client.client()) {
                view.showMenuCliente();
                int opcao = view.getOption();

                switch (opcao) {
                    case 1:
                        listarCarros();
                        break;
                    case 2:
                        buscarCarro();
                        break;
                    case 3:
                        quantidadeCarros();
                        break;
                    case 4:
                        comprarCarro();
                        break;
                    case 0:
                        stop();
                        break;
                    default:
                        view.showErro("Opção inválida, tente novamente...");
                        delay(2000);
                }
            } else {
                view.showMenuFuncionario();
                int opcao = view.getOption();

                switch (opcao) {
                    case 1:
                        listarCarros();
                        break;
                    case 2:
                        buscarCarro();
                        break;
                    case 3:
                        quantidadeCarros();
                        break;
                    case 4:
                        comprarCarro();
                        break;
                    case 5:
                        adicionarCarro();
                        break;
                    case 6:
                        apagarCarro();
                        break;
                    case 7:
                        alterarCarro();
                        break;
                    case 0:
                        stop();
                        break;
                    default:
                        view.showErro("Opção inválida, tente novamente...");
                        delay(2000);
                }
            }

        }
    }

    public void stop() {
        this.stop = true;
    }

    public void listarCarros() {
        view.limparTela();
        view.showMenuListarCarros();
        int opcao = view.getOption();

        switch (opcao) {
            case 1:
                LinkedList<Carro> carros = model.getCarros(null);
                view.listarCarros(carros);
                break;
            case 2:
                Categoria categoria = view.getCategoria();
                view.listarCarros(model.getCarros(categoria));
                break;
            case 0:
                break;
            default:
                view.showErro("Opção inválida, tente novamente...");
                delay(2000);
        }

    }

    public void adicionarCarro() {
        view.limparTela();
        view.showMensagem("Adicionar carro");

        Carro novoCarro = view.adicionarCarro();

        model.adicionar(novoCarro);
    }

    public void apagarCarro() {
        view.limparTela();
        view.showMenuApagarCarro();
        int opcao = view.getOption();

        switch (opcao) {
            case 1:
                String nome = view.getNomeCarro();
                view.showMensagem("Carros removidos: ");
                view.listarCarros(model.apagarPorNome(nome));
                break;
            case 2:
                String renavam = view.getRenavamCarro();
                view.showMensagem("Carro removido: ");
                view.showCarro(model.apagarPorRenavam(renavam));
                break;
            case 0:
                break;
            default:
                view.showErro("Opção inválida, tente novamente...");
                delay(2000);
        }
    }

    public void buscarCarro() {
        view.limparTela();
        view.showMenuPesquisarCarro();
        int opcao = view.getOption();

        switch (opcao) {
            case 1:
                String nome = view.getNomeCarro();
                view.listarCarros(model.getCarrosPorNome(nome));
                break;
            case 2:
                String renavam = view.getRenavamCarro();
                view.showCarro(model.getCarroPorRenavam(renavam));
                break;
            case 0:
                break;
            default:
                view.showErro("Opção inválida, tente novamente...");
                delay(2000);
        }
    }

    public void alterarCarro() {
        view.limparTela();
        view.showMensagem("Alterar carro");

        String renavam = view.getRenavamCarro();

        //verificar se o carro existe
        Carro carro = model.getCarroPorRenavam(renavam);

        if (carro == null) {
            view.showErro("Carro não encontrado");
            delay(2000);
        } else {
            view.showMensagem("Carro encontrado: ");
            view.showCarro(carro);
            view.halfLine();
            Carro carroAlterado = view.alterarCarro(renavam);
            model.alterar(renavam, carroAlterado);

            view.limparTela();
            view.showMensagem("Carro alterado com sucesso!");
            view.showCarro(model.getCarroPorRenavam(renavam));
            delay(2000);
        }
    }

    public void quantidadeCarros() {
        view.limparTela();
        view.showMensagem("Quantidade de carros: " + model.getQuantidade());
        delay(1000);
    }

    public void comprarCarro() {
        view.limparTela();
        view.showMenuComprarCarro();

        int opcao = view.getOption();

        switch (opcao) {
            //comprar por nome
            case 1:
                String nome = view.getNomeCarro();

                //exibir os carros com o nome especificado
                view.listarCarros(model.getCarrosPorNome(nome));

                //pegar o renavam do carro
                view.showMensagem("Para comprar um carro digite o renavam dele");
                String renavam = view.getRenavamCarro();

                //comprar o carro é remover ele da lista
                Carro carro = model.apagarPorRenavam(renavam);

                view.showMensagem("Carro comprado com sucesso!");
                view.showCarro(carro);
                delay(2000);
                break;
            //comprar por renavam
            case 2:
                String renavam2 = view.getRenavamCarro();
                Carro carro2 = model.apagarPorRenavam(renavam2);
                view.showMensagem("Carro comprado com sucesso!");
                view.showCarro(carro2);
                delay(2000);
                break;
            case 0:
                break;
            default:
                view.showErro("Opção inválida, tente novamente...");
                delay(2000);
        }

    }


    public void delay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
