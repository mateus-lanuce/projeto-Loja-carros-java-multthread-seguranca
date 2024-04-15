package apps.Interfaces;

import apps.Interfaces.ServerDB.DBCarrosInterface;
import apps.Interfaces.ServerDB.ServerDBInterface;

import java.rmi.Remote;

public interface ServerGetawayInterface extends Remote, UsersInterface, DBCarrosInterface {}