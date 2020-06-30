/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.dao;

import bello.ishcodebellz.flooringmasteryapplication.dto.FMAProduct;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;

/**
 *
 * @author amanda
 */
public interface FMAProductsDao {

    FMAProduct getProduct(String productType) throws DataPersistenceException;

}
