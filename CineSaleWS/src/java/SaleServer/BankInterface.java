/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SaleServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * @author GSanM
 * @author lzgustavo
 */

public interface BankInterface extends Remote {

	public String validate(String card_number) throws RemoteException; 
}