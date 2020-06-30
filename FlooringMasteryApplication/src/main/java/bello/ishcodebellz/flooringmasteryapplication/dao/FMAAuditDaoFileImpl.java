/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.dao;

import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public class FMAAuditDaoFileImpl implements FMAAuditDao {

    public static final String AUDIT_FILE = "FMAAudit.txt";
// a check is done to see if the audit file exists
    // if not it will create a file 
    // if an entry is made a timestamp is given first then the entry follows
    @Override
    public void writeAuditEntry(String entry) throws DataPersistenceException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(AUDIT_FILE, true));
        } catch (IOException e) {
            throw new DataPersistenceException("Could not persist audit information.", e);
        }

        LocalDateTime timestamp = LocalDateTime.now();
        out.println(timestamp.toString() + " : " + entry);
        out.flush();

    }

}
