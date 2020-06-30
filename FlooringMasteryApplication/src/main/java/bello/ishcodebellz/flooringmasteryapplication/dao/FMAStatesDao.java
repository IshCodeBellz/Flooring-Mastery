/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.dao;

import bello.ishcodebellz.flooringmasteryapplication.dto.FMAState;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;

/**
 *
 * @author amanda
 */
public interface FMAStatesDao {

    FMAState getState(String stateAbbr) throws DataPersistenceException;

}
