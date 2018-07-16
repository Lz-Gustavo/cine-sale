/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SaleServer;

import java.text.SimpleDateFormat;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.*;
import java.rmi.*;

/**
 * @author GSanM
 * @author lzgustavo
 */
@WebService(serviceName = "SaleServer")
public class SaleServer {

	@WebMethod(operationName = "hello")
	public String hello(@WebParam(name = "name") String txt) {
		return "Hello " + txt + " !";
	}

	@WebMethod(operationName = "getTime")
	public String getTime() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(cal.getTime());
	}

	@WebMethod(operationName = "testRMI")
	public String testRMI() {

		HelloInterface hello;
		String name = "rmi://localhost/HelloServer";
		String text;

		try {
			hello = (HelloInterface) Naming.lookup(name);
			text = hello.sayHello();
			return text;
		} catch (Exception e) {
			return ("Hello Client Exception: " + e);
		}
	}
	
	@WebMethod(operationName = "validate")
	public String validate(String card_number) {
		
		BankInterface bank;
		String name = "rmi://localhost/BankServer";
		String text;
		
		try {
			bank = (BankInterface) Naming.lookup(name);
			text = bank.validate("00001");
			return text;
		} catch (Exception e) {
			return ("Bank error: " + e);
		}
	}
}
