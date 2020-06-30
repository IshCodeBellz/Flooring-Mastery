/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.service;


import bello.ishcodebellz.flooringmasteryapplication.dto.FMAOrder;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public interface FMAFloorServiceLayer {

    List<FMAOrder> getOrders(LocalDate dateChoice) throws InvalidOrderNumberException,
            DataPersistenceException;

    FMAOrder calculateOrder(FMAOrder o) throws DataPersistenceException,
            OrderValidationException, StateValidationException, ProductValidationException;

    FMAOrder getOrder(LocalDate dateChoice, int orderNumber) throws
            DataPersistenceException, InvalidOrderNumberException;

    FMAOrder addOrder(FMAOrder o) throws DataPersistenceException;

    FMAOrder compareOrders(FMAOrder savedOrder, FMAOrder editedOrder)
            throws DataPersistenceException, StateValidationException,
            ProductValidationException;

    FMAOrder editOrder(FMAOrder updatedOrder) throws DataPersistenceException,
            InvalidOrderNumberException;

    FMAOrder cancelOrder(FMAOrder cancelledOrder) throws DataPersistenceException,
            InvalidOrderNumberException;

 ///////////////////////////////////////////////////////////////////////////////////////////////////   
    public void backup()throws DataPersistenceException,
            DataExportException;
}
