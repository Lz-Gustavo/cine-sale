/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SaleServer;
import java.rmi.*;

/**
 *
 * @author lzgustavo
 */
public interface RMInterface extends Remote {
    
    public String sayHello() throws RemoteException;
}
