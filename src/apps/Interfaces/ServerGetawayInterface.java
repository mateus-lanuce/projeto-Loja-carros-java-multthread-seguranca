package apps.Interfaces;

import apps.Interfaces.ServerDB.DBCarrosInterface;

import java.rmi.Remote;

public interface ServerGetawayInterface extends Remote, UsersInterface, DBCarrosInterface {}
