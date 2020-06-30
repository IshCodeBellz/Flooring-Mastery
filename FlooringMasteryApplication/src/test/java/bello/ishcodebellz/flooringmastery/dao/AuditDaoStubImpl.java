/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmastery.dao;

import bello.ishcodebellz.flooringmasteryapplication.dao.FMAAuditDao;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;

/**
 *
 * @author amanda
 */
public class AuditDaoStubImpl implements FMAAuditDao {

    @Override
    public void writeAuditEntry(String entry) throws DataPersistenceException {
        //Does nothing.
    }

}
