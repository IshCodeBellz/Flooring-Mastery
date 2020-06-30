/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.dao;

import bello.ishcodebellz.flooringmasteryapplication.dto.FMAProduct;
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
public class FMAProductsDaoFileImpl implements FMAProductsDao {

    private static final String PRODUCTS_FILE = "FloorProducts.txt";
    private static final String DELIMITER = ",";
    //when getting the product
// if no product has been selected nothing will return
    // else the wether they input caps or no caps is ignored and the product will be selected
    @Override
    public FMAProduct getProduct(String productType) throws DataPersistenceException {
        List<FMAProduct> products = loadProducts();
        if (products == null) {
            return null;
        } else {
            FMAProduct chosenProduct = products.stream()
                    .filter(p -> p.getProductType().equalsIgnoreCase(productType))
                    .findFirst().orElse(null);
            return chosenProduct;
        }
    }
    //theres a check for the product file if not an error is thrown
// while loading the products from a file it is sccanned by the scanner 
    // each line is taken in and items are seperated by the delimiter
    // items are placed and set
    private List<FMAProduct> loadProducts() throws DataPersistenceException {
        Scanner scanner;
        List<FMAProduct> products = new ArrayList<>();

        try {
            scanner = new Scanner(
                    new BufferedReader(
                            new FileReader(PRODUCTS_FILE)));
        } catch (FileNotFoundException e) {
            throw new DataPersistenceException(
                    "-_- Could not load products data into memory.", e);
        }

        String currentLine;
        String[] currentItems;
        scanner.nextLine();//Skips scanning document headers       
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentItems = currentLine.split(DELIMITER);
            if (currentItems.length == 3) {
                FMAProduct currentProduct = new FMAProduct();
                currentProduct.setProductType(currentItems[0]);
                currentProduct.setMaterialCostPerSquareFoot(new BigDecimal(currentItems[1]));
                currentProduct.setLaborCostPerSquareFoot(new BigDecimal(currentItems[2]));
                // Put currentProduct into the map using productType as the key
                products.add(currentProduct);
            } else {
                //Ignores line if delimited wrong or empty.
            }
        }
        scanner.close();

        if (!products.isEmpty()) {
            return products;
        } else {
            return null;
        }
    }

}
