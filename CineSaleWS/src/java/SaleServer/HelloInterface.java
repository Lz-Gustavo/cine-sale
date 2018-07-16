/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SaleServer;
import java.rmi.*;

/**
 * @author GSanM
 * @author lzgustavo
 */
public interface HelloInterface extends Remote {
    
    public String sayHello() throws RemoteException;
}
