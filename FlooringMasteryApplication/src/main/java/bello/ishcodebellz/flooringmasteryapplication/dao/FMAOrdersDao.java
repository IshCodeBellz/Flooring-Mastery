/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.dao;

import bello.ishcodebellz.flooringmasteryapplication.dto.FMAOrder;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public interface FMAOrdersDao {

    List<FMAOrder> getOrders(LocalDate dateChoice) throws DataPersistenceException;

    FMAOrder addOrder(FMAOrder o) throws DataPersistenceException;

    FMAOrder editOrder(FMAOrder editedOrder) throws DataPersistenceException;

    FMAOrder cancelOrder(FMAOrder o) throws DataPersistenceException;
    
    /////////////////////////////////////////////////////////////////////////////////
    
    public void backUpAllOrders() throws DataPersistenceException;
    
}
