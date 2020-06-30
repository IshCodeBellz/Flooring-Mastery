/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.controller;

import bello.ishcodebellz.flooringmasteryapplication.dto.FMAOrder;
import bello.ishcodebellz.flooringmasteryapplication.service.DataExportException;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;
import bello.ishcodebellz.flooringmasteryapplication.service.InvalidOrderNumberException;
import bello.ishcodebellz.flooringmasteryapplication.service.OrderValidationException;
import bello.ishcodebellz.flooringmasteryapplication.service.ProductValidationException;
import bello.ishcodebellz.flooringmasteryapplication.service.StateValidationException;
import java.time.LocalDate;
import javax.swing.text.View;
import bello.ishcodebellz.flooringmasteryapplication.service.FMAFloorServiceLayer;
import bello.ishcodebellz.flooringmasteryapplication.ui.FMAConsoleView;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public class FMAController {

    FMAFloorServiceLayer service;
    FMAConsoleView view;

    public FMAController(FMAFloorServiceLayer service, FMAConsoleView view) {
        this.service = service;
        this.view = view;
    }
// the application menu with the 5 options
//    cases represent the option for the user to choose and then the method is called to be performed
    public void runFlooringMasteryApplication() throws DataExportException {

        boolean keepGoing = true;
        int menuSelection = 0;
        try {
            while (keepGoing) {

                menuSelection = getMenuSelection();

                switch (menuSelection) {
                    case 1:
                        getOrdersByDate();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        cancelOrder();
                        break;
                    case 5: 
                        saveData();
                        break;
                    case 6:
                        keepGoing = false;
                        break;       
                    default:
                        unknownCommand();
                }

            }
            exitMessage();
        } catch (DataPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }
// menu selction options
    private int getMenuSelection() {
        return view.printMenuAndGetSelection();
    }
// it will select the options to get and display all the orders made by date
    // the date is not valid the error is thrown else it will display all orders from the date 
    private void getOrdersByDate() throws DataPersistenceException {
        LocalDate dateChoice = view.inputDate();
        view.displayDateBanner(dateChoice);
        try {
            view.displayDateOrders(service.getOrders(dateChoice));
            view.displayContinue();
        } catch (InvalidOrderNumberException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void addOrder() throws DataPersistenceException {
        try {
            FMAOrder o = service.calculateOrder(view.getOrder());
            view.displayOrder(o);
            String response = view.askSave();
            if (response.equalsIgnoreCase("Y")) {
                service.addOrder(o);
                view.displayAddOrderSuccess(true, o);
            } else if (response.equalsIgnoreCase("N")) {
                view.displayAddOrderSuccess(false, o);
            } else {
                unknownCommand();
            }
        } catch (OrderValidationException
                | StateValidationException | ProductValidationException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void editOrder() throws DataPersistenceException {
        view.displayEditOrderBanner();
        try {
            LocalDate dateChoice = view.inputDate();
            int orderNumber = view.inputOrderNumber();
            FMAOrder savedOrder = service.getOrder(dateChoice, orderNumber);
            FMAOrder editedOrder = view.editOrderInfo(savedOrder);
            FMAOrder updatedOrder = service.compareOrders(savedOrder, editedOrder);
            view.displayEditOrderBanner();
            view.displayOrder(updatedOrder);
            String response = view.askSave();
            if (response.equalsIgnoreCase("Y")) {
                service.editOrder(updatedOrder);
                view.displayEditOrderSuccess(true, updatedOrder);
            } else if (response.equalsIgnoreCase("N")) {
                view.displayEditOrderSuccess(false, updatedOrder);
            } else {
                unknownCommand();
            }
        } catch (InvalidOrderNumberException
                | ProductValidationException | StateValidationException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void cancelOrder() throws DataPersistenceException {
        view.displayRemoveOrderBanner();
        LocalDate dateChoice = view.inputDate();
        view.displayDateBanner(dateChoice);
        try {
            view.displayDateOrders(service.getOrders(dateChoice));
            int orderNumber = view.inputOrderNumber();
            FMAOrder o = service.getOrder(dateChoice, orderNumber);
            view.displayRemoveOrderBanner();
            view.displayOrder(o);
            String response = view.askRemove();
            if (response.equalsIgnoreCase("Y")) {
                service.cancelOrder(o);
                view.displayRemoveOrderSuccess(true, o);
            } else if (response.equalsIgnoreCase("N")) {
                view.displayRemoveOrderSuccess(false, o);
            } else {
                unknownCommand();
            }
        } catch (InvalidOrderNumberException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }
 
    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }

    private void exitMessage() {
        view.displayExitBanner();
    }
    
    private void saveData() throws DataPersistenceException, DataExportException{
        service.backup();
        view.displaySavedExportMessage();
    }
}
