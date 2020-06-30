/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.dao;

import bello.ishcodebellz.flooringmasteryapplication.dto.FMAState;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public class FMAStatesDaoFileImpl implements FMAStatesDao {

    private static final String STATES_FILE = "AbbrevTaxes.txt";
    private static final String DELIMITER = ",";
// checks to see is the state exists ignoring the users case inputs
    // if it does the state is returned 
    @Override
    public FMAState getState(String stateAbbr) throws DataPersistenceException {
        List<FMAState> states = loadStates();
        if (states == null) {
            return null;
        } else {
            FMAState chosenState = states.stream()
                    .filter(s -> s.getStateAbbr().equalsIgnoreCase(stateAbbr))
                    .findFirst().orElse(null);
            return chosenState;
        }
    }
//    scanner checks to see if theres a file named AbbrevTaxes.txt if it doesnt exist an error is thrown
//    if it does the scanner starts on the second line ignoring the header
//    items are seperated by delimiters and are placed into list for further use  
    // if a line is empty it is ignored, states are then returned
    private List<FMAState> loadStates() throws DataPersistenceException {
        Scanner scanner;
        List<FMAState> states = new ArrayList<>();

        try {
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(STATES_FILE)));
        } catch (FileNotFoundException e) {
            throw new DataPersistenceException(
                    "-_- Could not load states data into memory.", e);
        }

        String currentLine;
        String[] currentItems;
        scanner.nextLine();//Skips scanning document headers
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentItems = currentLine.split(DELIMITER);
            if (currentItems.length == 2) {
                FMAState currentState = new FMAState();
                currentState.setStateAbbr(currentItems[0]);
                currentState.setTaxRate(new BigDecimal(currentItems[1]));
                // Put currentState into the map using stateAbbr as the key
                states.add(currentState);
            } else {
                //Ignores line if delimited wrong or empty.
            }
        }
        scanner.close();

        if (!states.isEmpty()) {
            return states;
        } else {
            return null;
        }
    }
}
