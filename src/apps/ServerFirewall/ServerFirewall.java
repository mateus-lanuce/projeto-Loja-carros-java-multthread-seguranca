package apps.ServerFirewall;

import apps.Records.IpPort;
import apps.ServerFirewall.Controller.ControllerFirewall;
import apps.ServerLoja.Controller.ControlLoja;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ServerFirewall {

    public ServerFirewall(int port, int idPreferencia, String ipGetaway) {
        try {
            IpPort port1 = new IpPort("", 1111);
//            IpPort port2 = new IpPort("", 1112);
//            IpPort port3 = new IpPort("", 1113);
            ArrayList<IpPort> ports = new ArrayList<>();
            ports.add(port1);
//            ports.add(port2);
//            ports.add(port3);

            IpPort authServer = new IpPort("", 1141);

            ArrayList<String> ipsPermitidos = new ArrayList<>();
            ipsPermitidos.add("localhost");
            ipsPermitidos.add("127.0.0.0.1");
            ipsPermitidos.add("26.228.249.147");
            ipsPermitidos.add("26.42.93.107");

            //cria o objeto remoto
            ControllerFirewall firewall = new ControllerFirewall(ports, idPreferencia, authServer, ipsPermitidos);

            Registry registry = LocateRegistry.createRegistry(port);

            //registra o objeto remoto
            registry.rebind("Firewall", firewall);
            System.out.println("Servidor de firewall pronto na porta: " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new ServerFirewall(1121, 1, "26.228.249.147");
    }
}

class ServerFirewall2 {
    public static void main(String[] args) {
        new ServerFirewall(1122, 1, "26.228.249.147");
    }
}

class ServerFirewall3 {
    public static void main(String[] args) {
        new ServerFirewall(1123, 1, "26.228.249.147");
    }
}
