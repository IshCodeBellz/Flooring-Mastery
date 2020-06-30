/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication;

import bello.ishcodebellz.flooringmasteryapplication.controller.FMAController;
import bello.ishcodebellz.flooringmasteryapplication.service.DataExportException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public class FMAapp {
       public static void main(String[] args) throws DataExportException {
        // original main method has been setup using Spring, for dependency injection
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        FMAController controller = ctx.getBean("controller", FMAController.class);
        // this function allow the main method to run the whole application 
        controller.runFlooringMasteryApplication();

    }     
        
}
