import apps.Records.User;

public class Main {
    public static void main(String[] args) {

        User user = new User("email", "password", true);
        System.out.println(user);
    }

}