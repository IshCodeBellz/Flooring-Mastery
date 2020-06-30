/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.dao;

import org.apache.commons.io.FileUtils;

import bello.ishcodebellz.flooringmasteryapplication.dto.FMAOrder;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.springframework.core.annotation.Order;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public class FMAOrdersProdDaoFileImpl implements FMAOrdersDao {

    private int lastOrderNumber;
    private static final String STATUSact ="ACTIVE";
    private static final String STATUScan ="CANCELLED";
    private static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,"
            + "ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,"
            + "MaterialCost,LaborCost,Tax,Total,STATUS";
    private static final String DELIMITER = ",";
    private String dataFolder = "orders/";
    private String exportFolder ="EXPORTS/";
    
    
    
    public FMAOrdersProdDaoFileImpl() {
    }

    public FMAOrdersProdDaoFileImpl(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    private void readLastOrderNumber() throws DataPersistenceException {
        Scanner scanner;

        try {
            //Create Scanner to read file
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(dataFolder + "LastOrderNumber.txt")));
        } catch (FileNotFoundException e) {
            //Throwing a general exception to the calling code
            throw new DataPersistenceException(
                    "-_- Could not load order number into memory.", e);
        }

        int savedOrderNumber = Integer.parseInt(scanner.nextLine());

        this.lastOrderNumber = savedOrderNumber;

        scanner.close();

    }

    private void writeLastOrderNumber(int lastOrderNumber) throws DataPersistenceException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(dataFolder + "LastOrderNumber.txt"));
        } catch (IOException e) {
            throw new DataPersistenceException(
                    "Could not save order number.", e);
        }

        out.println(lastOrderNumber);

        out.flush();

        out.close();
    }

    @Override
    public List<FMAOrder> getOrders(LocalDate chosenDate) throws DataPersistenceException {
        return loadOrders(chosenDate);
    }

    @Override
    public FMAOrder addOrder(FMAOrder o) throws DataPersistenceException {
        //Checks input for commas
        o = clearFields(o);
        //Getting the last used number, adding one, and saving it
        readLastOrderNumber();
        lastOrderNumber++;
        o.setOrderNumber(lastOrderNumber);
        writeLastOrderNumber(lastOrderNumber);

        List<FMAOrder> orders = loadOrders(o.getDate());
        orders.add(o);
        writeOrders(orders, o.getDate());

        return o;
    }

    @Override
    public FMAOrder editOrder(FMAOrder editedOrder)
            throws DataPersistenceException {
        //Checks input for commas
        editedOrder = clearFields(editedOrder);
        int orderNumber = editedOrder.getOrderNumber();

        List<FMAOrder> orders = loadOrders(editedOrder.getDate());
        FMAOrder chosenOrder = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst().orElse(null);

        if (chosenOrder != null) {
            int index = orders.indexOf(chosenOrder);
            orders.set(index, editedOrder);
            writeOrders(orders, editedOrder.getDate());
            return editedOrder;
        } else {
            return null;
        }
    }

    @Override
    public FMAOrder cancelOrder(FMAOrder chosenOrder)
            throws DataPersistenceException {

        int orderNumber = chosenOrder.getOrderNumber();

        List<FMAOrder> orders = loadOrders(chosenOrder.getDate());
        FMAOrder cancelledOrder = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst().orElse(null);

        if (cancelledOrder != null) {
            
            cancelledOrder.setStatus(STATUScan);
            //orders.remove(cancelledOrder);
            writeOrders(orders, chosenOrder.getDate());
            return cancelledOrder;
        } else {
            return null;
        }

    }

    private FMAOrder clearFields(FMAOrder order) {
        //Dao does not know what the other layers are doing so its clearing delimiters
        String clearCustomerName = order.getCustomerName().replace(DELIMITER, "");
        String clearStateAbbr = order.getStateAbbr().replace(DELIMITER, "");
        String clearProductType = order.getProductType().replace(DELIMITER, "");

        order.setCustomerName(clearCustomerName);
        order.setStateAbbr(clearStateAbbr);
        order.setProductType(clearProductType);

        return order;
    }

    private List<FMAOrder> loadOrders(LocalDate chosenDate) throws DataPersistenceException {
        //Loads one file for a specific date
        Scanner scanner;
        String fileDate = chosenDate.format(DateTimeFormatter.ofPattern("MMddyyyy"));

        File f = new File(String.format(dataFolder + "Orders_%s.txt", fileDate));

        List<FMAOrder> orders = new ArrayList<>();

        if (f.isFile()) {
            try {
                scanner = new Scanner(
                        new BufferedReader(
                                new FileReader(f)));
            } catch (FileNotFoundException e) {
                throw new DataPersistenceException(
                        "-_- Could not load order data into memory.", e);
            }
            String currentLine;
            String[] currentItems;
            scanner.nextLine();//Skips scanning document headers
            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine();
                currentItems = currentLine.split(DELIMITER);
                if (currentItems.length == 13) {
                    FMAOrder currentOrder = new FMAOrder();
                    currentOrder.setDate(LocalDate.parse(fileDate,
                            DateTimeFormatter.ofPattern("MMddyyyy")));
                    currentOrder.setOrderNumber(Integer.parseInt(currentItems[0]));
                    currentOrder.setCustomerName(currentItems[1]);
                    currentOrder.setStateAbbr(currentItems[2]);
                    currentOrder.setTaxRate(new BigDecimal(currentItems[3]));
                    currentOrder.setProductType(currentItems[4]);
                    currentOrder.setArea(new BigDecimal(currentItems[5]));
                    currentOrder.setMaterialCostPerSquareFoot(new BigDecimal(currentItems[6]));
                    currentOrder.setLaborCostPerSquareFoot(new BigDecimal(currentItems[7]));
                    currentOrder.setMaterialCost(new BigDecimal(currentItems[8]));
                    currentOrder.setLaborCost(new BigDecimal(currentItems[9]));
                    currentOrder.setTax(new BigDecimal(currentItems[10]));
                    currentOrder.setTotal(new BigDecimal(currentItems[11]));
                    currentOrder.setStatus(currentItems[12]);
                    orders.add(currentOrder);
                } else {
                    //Ignore line.
                }
            }
            scanner.close();
            return orders;
        } else {
            return orders;
        }
    }

    private void writeOrders(List<FMAOrder> orders, LocalDate orderDate)
            throws DataPersistenceException {

        PrintWriter out;

        String fileDate = orderDate.format(DateTimeFormatter
                .ofPattern("MMddyyyy"));

        File f = new File(String.format(dataFolder + "Orders_%s.txt", fileDate));

        try {
            out = new PrintWriter(new FileWriter(f));
        } catch (IOException e) {
            throw new DataPersistenceException(
                    "Could not save order data.", e);
        }

        // Write out the FMAOrder objects to the file.
        out.println(HEADER);
        for (FMAOrder currentOrder : orders) {
            // Write the FMAOrder objects to the file
            out.println(currentOrder.getOrderNumber() + DELIMITER
                    + currentOrder.getCustomerName() + DELIMITER
                    + currentOrder.getStateAbbr() + DELIMITER
                    + currentOrder.getTaxRate() + DELIMITER
                    + currentOrder.getProductType() + DELIMITER
                    + currentOrder.getArea() + DELIMITER
                    + currentOrder.getMaterialCostPerSquareFoot() + DELIMITER
                    + currentOrder.getLaborCostPerSquareFoot() + DELIMITER
                    + currentOrder.getMaterialCost() + DELIMITER
                    + currentOrder.getLaborCost() + DELIMITER
                    + currentOrder.getTax() + DELIMITER
                    + currentOrder.getTotal() + DELIMITER
                    + currentOrder.getStatus());

            //Force PrintWriter to write line to the file
            out.flush();
        }
        //clear up
        out.close();
    }

/////////////////////////////////////////////////////////////////////////////////
    @Override
    public void backUpAllOrders()  throws DataPersistenceException {
		File from = new File("orders/");
		File to = new File("BACKUP");

		try {
			FileUtils.copyDirectory(from, to);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
    }
}
