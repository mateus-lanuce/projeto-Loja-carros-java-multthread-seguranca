package apps.Client.View;

import apps.Categoria;
import apps.Client.Client;
import apps.Records.Carro;
import apps.Records.User;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * The ClientView class represents the view layer for the client functionality of the application.
 * It provides methods for user input and displaying information to the user.
 */
public class ClientView {

    Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user to enter their email and password for login.
     * @return The User object representing the logged-in user.
     */
    public User login() {
        System.out.println("Digite seu email: ");
        String email = scanner.nextLine();
        System.out.println("Digite sua senha: ");
        String password = scanner.nextLine();
        return new User(email, password, true);
    }

    /**
     * Displays the menu options for the client.
     * 1. Listar carros
     * 2. Pesquisar carro
     * 3. Exibir quantidade de carros
     * 4. Comprar carro
     * 0. Sair
     */
    public void showMenuCliente() {
        System.out.println("----- Menu do Cliente -----");
        System.out.println("1. Listar carros");
        System.out.println("2. Pesquisar carro");
        System.out.println("3. Exibir quantidade de carros");
        System.out.println("4. Comprar carro");
        System.out.println("0. Sair");
    }

    /**
     * Displays the menu options for the employee.
     * 1. Listar carros
     * 2. Pesquisar carro
     * 3. Exibir quantidade de carros
     * 4. Comprar carro
     * 5. Adicionar carro
     * 6. Apagar carro
     * 7. Alterar atributos de carro
     */
    public void showMenuFuncionario() {
        System.out.println("----- Menu do Funcionário -----");
        System.out.println("1. Listar carros");
        System.out.println("2. Pesquisar carro");
        System.out.println("3. Exibir quantidade de carros");
        System.out.println("4. Comprar carro");
        System.out.println("5. Adicionar carro");
        System.out.println("6. Apagar carro");
        System.out.println("7. Alterar atributos de carro");
        System.out.println("0. Sair");
    }

    /**
     * Displays the menu options for listing cars.
     * 1. Listar todos os carros
     * 2. Listar carros por categoria
     */
    public void showMenuListarCarros() {
        System.out.println("----- Listar Carros -----");
        System.out.println("1. Listar todos os carros");
        System.out.println("2. Listar carros por categoria");
        System.out.println("0. Voltar");
    }

    /**
     * Displays the menu options for searching cars.
     * 1. Pesquisar carro por nome
     * 2. Pesquisar carro por renavam
     */
    public void showMenuPesquisarCarro() {
        System.out.println("----- Pesquisar Carro -----");
        System.out.println("1. Pesquisar carro por nome");
        System.out.println("2. Pesquisar carro por renavam");
        System.out.println("0. Voltar");
    }

    /**
     * Displays the menu options for buying cars.
     * 1. Comprar carro por nome
     * 2. Comprar carro por renavam
     */
    public void showMenuComprarCarro() {
        System.out.println("----- Comprar Carro -----");
        System.out.println("1. Comprar carro por nome");
        System.out.println("2. Comprar carro por renavam");
        System.out.println("0. Voltar");
    }

    /**
     * Displays the menu options for deleting cars.
     * 1. Apagar carro por nome
     * 2. Apagar carro por renavam
     * 0. Voltar
     */
    public void showMenuApagarCarro() {
        System.out.println("----- Apagar Carro -----");
        System.out.println("1. Apagar carro por nome");
        System.out.println("2. Apagar carro por renavam");
        System.out.println("0. Voltar");
    }

    /**
     * Prompts the user to enter details of a new car to be added.
     * @return The Carro object representing the new car.
     */
    public Carro adicionarCarro() {
        //limpar buffer
        scanner.nextLine();
        System.out.println("Digite o renavam do carro: ");
        String renavam = scanner.nextLine();
        System.out.println("Digite o nome do carro: ");
        String nome = scanner.nextLine();
        System.out.println("Digite a categoria do carro: ");
        System.out.println("opções: " + Categoria.ECONOMICO + ", " + Categoria.INTERMEDIARIO + ", " + Categoria.EXECUTIVO);
        Categoria categoria = Categoria.valueOf(scanner.nextLine());
        System.out.println("Digite o ano do carro: ");
        int ano = scanner.nextInt();
        System.out.println("Digite o preço do carro: ");
        double preco = scanner.nextDouble();
        return new Carro(renavam, nome, categoria, ano, preco);
    }

    /**
     * Displays a list of cars.
     * @param carros The list of cars to be displayed.
     */
    public void listarCarros(LinkedList<Carro> carros) {
        System.out.println("Carros: ");
        carros.forEach(System.out::println);
    }

    /**
     * Prompts the user to enter the name of a car.
     * @return The name of the car entered by the user.
     */
    public String getNomeCarro() {
        System.out.println("Digite o nome do carro: ");
        //limpar buffer
        scanner.nextLine();
        String nome = scanner.nextLine();
        return nome;
    }

    /**
     * Prompts the user to enter the renavam of a car.
     * @return The renavam of the car entered by the user.
     */
    public String getRenavamCarro() {
        System.out.println("Digite o renavam do carro: ");
        //limpar buffer
        scanner.nextLine();
        return scanner.nextLine();
    }

    /**
     * Prompts the user to enter details of a car to be updated.
     * @return The Carro object representing the updated car.
     */
    public Carro alterarCarro(String renavam) {
        System.out.println("Digite o novo nome do carro: ");
        String nome = scanner.nextLine();
        System.out.println("Digite a nova categoria do carro: ");
        System.out.println("opções: " + Categoria.ECONOMICO + ", " + Categoria.INTERMEDIARIO + ", " + Categoria.EXECUTIVO);
        Categoria categoria = Categoria.valueOf(scanner.nextLine());
        System.out.println("Digite o novo ano do carro: ");
        int ano = scanner.nextInt();
        System.out.println("Digite o novo preço do carro: ");
        double preco = scanner.nextDouble();
        return new Carro(renavam, nome, categoria, ano, preco);
    }

    /**
     * Displays information about a car.
     * @param carro The car to be displayed.
     */
    public void showCarro(Carro carro) {
        System.out.println();
        System.out.println(carro);
        System.out.println();
    }

    /**
     * Displays the quantity of cars.
     * @param quantidade The quantity of cars to be displayed.
     */
    public void showQuantidadeCarros(int quantidade) {
        System.out.println("Quantidade de carros: " + quantidade);
    }

    /**
     * Displays a message.
     * @param mensagem The message to be displayed.
     */
    public void showMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    /**
     * Displays an error message.
     * @param erro The error message to be displayed.
     */
    public void showErro(String erro) {
        System.out.println("Erro: " + erro);
    }

    /**
     * Prompts the user to enter an option.
     * @return The option entered by the user.
     */
    public int getOption() {
        System.out.println();
        System.out.print("Digite a opção desejada: ");
        return scanner.nextInt();
    }

    public Categoria getCategoria() {
        System.out.println("Digite a categoria do carro: ");
        System.out.println("" +
                "opções: " + Categoria.ECONOMICO + " - 1" + "\n"
                + Categoria.INTERMEDIARIO + " - 2" + "\n"
                + Categoria.EXECUTIVO + " - 3");

        int categoria = getOption();

        switch (categoria) {
            case 1:
                return Categoria.ECONOMICO;
            case 2:
                return Categoria.INTERMEDIARIO;
            case 3:
                return Categoria.EXECUTIVO;
            default:
                return null;
        }

    }

    /**
     * Displays a header.
     */
    public void showCabecalho() {
        String cabeçalho = """
    .______    _______ .___  ___.    ____    ____  __  .__   __.  _______   ______           ___                                                 \s
    |   _  \\  |   ____||   \\/   |    \\   \\  /   / |  | |  \\ |  | |       \\ /  __  \\         /   \\                                                \s
    |  |_)  | |  |__   |  \\  /  |     \\   \\/   /  |  | |   \\|  | |  .--.  |  |  |  |       /  ^  \\                                               \s
    |   _  <  |   __|  |  |\\/|  |      \\      /   |  | |  . `  | |  |  |  |  |  |  |      /  /_\\  \\                                              \s
    |  |_)  | |  |____ |  |  |  |       \\    /    |  | |  |\\   | |  '--'  |  `--'  |     /  _____  \\                                             \s
    |______/  |_______||__|  |__|        \\__/     |__| |__| \\__| |_______/ \\______/     /__/     \\__\\                                            \s
                                                                                                                                                 \s
     __        ______          __       ___          _______   _______      ______     ___      .______      .______        ______        _______.
    |  |      /  __  \\        |  |     /   \\        |       \\ |   ____|    /      |   /   \\     |   _  \\     |   _  \\      /  __  \\      /       |
    |  |     |  |  |  |       |  |    /  ^  \\       |  .--.  ||  |__      |  ,----'  /  ^  \\    |  |_)  |    |  |_)  |    |  |  |  |    |   (----`
    |  |     |  |  |  | .--.  |  |   /  /_\\  \\      |  |  |  ||   __|     |  |      /  /_\\  \\   |      /     |      /     |  |  |  |     \\   \\   \s
    |  `----.|  `--'  | |  `--'  |  /  _____  \\     |  '--'  ||  |____    |  `----./  _____  \\  |  |\\  \\----.|  |\\  \\----.|  `--'  | .----)   |  \s
    |_______| \\______/   \\______/  /__/     \\__\\    |_______/ |_______|    \\______/__/     \\__\\ | _| `._____|| _| `._____| \\______/  |_______/   \s
                                                                                                                                                 \s
    """;
    }

    /**
     * Clears the console screen.
     */
    public void limparTela() {
        System.out.println("\n".repeat(50));
    }

    public void halfLine() {
        System.out.println("\n\n");
        System.out.println("--------------------------------------------------");
    }

}
