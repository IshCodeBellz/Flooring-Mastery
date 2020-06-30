/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmasteryapplication.service;


import bello.ishcodebellz.flooringmasteryapplication.dto.FMAOrder;
import bello.ishcodebellz.flooringmasteryapplication.dto.FMAProduct;
import bello.ishcodebellz.flooringmasteryapplication.dto.FMAState;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAAuditDao;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAOrdersDao;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAStatesDao;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAProductsDao;
import bello.ishcodebellz.flooringmasteryapplication.service.FMAFloorServiceLayer;

/**
 *
 * @author AHMED ADEYEMI BELLO
 */
public class FMAFloorServiceLayerImpl implements FMAFloorServiceLayer {

    private FMAOrdersDao ordersDao;
    private FMAProductsDao productsDao;
    private FMAStatesDao statesDao;
    private FMAAuditDao auditDao;

    public FMAFloorServiceLayerImpl(FMAOrdersDao ordersDao, FMAProductsDao productsDao,
            FMAStatesDao statesDao, FMAAuditDao auditDao) {
        this.ordersDao = ordersDao;
        this.productsDao = productsDao;
        this.statesDao = statesDao;
        this.auditDao = auditDao;
    }

    // searches for orders by the date, if none exist then and error is thrown
    @Override
    public List<FMAOrder> getOrders(LocalDate chosenDate) throws InvalidOrderNumberException,
            DataPersistenceException {
        List<FMAOrder> ordersByDate = ordersDao.getOrders(chosenDate);
        if (!ordersByDate.isEmpty()) {
            // unsure if it works if not delete
            auditDao.writeAuditEntry("Loading all orders ");
            return ordersByDate;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders "
                    + "exist on that date.");
        }
    }
// gets an order by searching for a date via lambda stream
    // once the date is found then a search for the order number is checked and returned
    // if the order number isnt located an error is thrown letting the user know its none existent 
    @Override
    public FMAOrder getOrder(LocalDate chosenDate, int orderNumber) throws
            DataPersistenceException, InvalidOrderNumberException {
        List<FMAOrder> orders = getOrders(chosenDate);
        FMAOrder chosenOrder = orders.stream()
                .filter(o -> o.getOrderNumber() == orderNumber)
                .findFirst().orElse(null);
        if (chosenOrder != null) {
            return chosenOrder;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders with that number "
                    + "exist on that date.");
        }
    }
// 
    @Override
    public FMAOrder calculateOrder(FMAOrder o) throws DataPersistenceException,
            OrderValidationException, StateValidationException, ProductValidationException {

        validateOrder(o);
        calculateTax(o);
        calculateMaterial(o);
        calculateTotal(o);

        return o;

    }
// checks to see if the state is correct, if not an exception is thrown 
    // else the state and tax rate is taken
    private void calculateTax(FMAOrder o) throws DataPersistenceException,
            StateValidationException {
        //Set state information in order.
        FMAState chosenState = statesDao.getState(o.getStateAbbr());
        if (chosenState == null) {
            throw new StateValidationException("ERROR: SWG Corp does not "
                    + "serve that state.");
        }
        o.setStateAbbr(chosenState.getStateAbbr());
        o.setTaxRate(chosenState.getTaxRate());
    }
//checks for the material if it exists the items are taken, labor cost material per square cost
    // if it doesnt exist an error is thrown 
    private void calculateMaterial(FMAOrder o) throws DataPersistenceException,
            ProductValidationException {
        //Set product information in order.
        FMAProduct chosenProduct = productsDao.getProduct(o.getProductType());
        if (chosenProduct == null) {
            throw new ProductValidationException("ERROR: We do not sell that "
                    + "product.");
        }
        o.setProductType(chosenProduct.getProductType());
        o.setMaterialCostPerSquareFoot(chosenProduct.getMaterialCostPerSquareFoot());
        o.setLaborCostPerSquareFoot(chosenProduct.getLaborCostPerSquareFoot());
    }
// calculations are performed to get the total price of the whole operation
    private void calculateTotal(FMAOrder o) {
        //Calculate other order fields.
        o.setMaterialCost(o.getMaterialCostPerSquareFoot().multiply(o.getArea())
                .setScale(2, RoundingMode.HALF_UP));
        o.setLaborCost(o.getLaborCostPerSquareFoot().multiply(o.getArea())
                .setScale(2, RoundingMode.HALF_UP));
        o.setTax(o.getTaxRate().divide(new BigDecimal("100.00"))
                .multiply((o.getMaterialCost().add(o.getLaborCost())))
                .setScale(2, RoundingMode.HALF_UP));
        o.setTotal(o.getMaterialCost().add(o.getLaborCost()).add(o.getTax()));
    }
// this checks for a valid order, if the customers name is filled, the state exists
    //the product selected exists and the area is within the range then the order can be carried out 
//    and it is valid if not an error message is thrown
    private void validateOrder(FMAOrder o) throws OrderValidationException {
        String message = "";
        if (o.getCustomerName().trim().isEmpty() || o.getCustomerName() == null) {
            message += "Customer name is required.\n";
        }
        if (o.getStateAbbr().trim().isEmpty() || o.getStateAbbr() == null) {
            message += "State is required.\n";
        }
        if (o.getProductType().trim().isEmpty() || o.getProductType() == null) {
            message += "Product type is required.\n";
        }
        if (o.getArea().compareTo(BigDecimal.ZERO) == 0 || o.getArea() == null) {
            message += "Area square footage is required.";
        }
        if (!message.isEmpty()) {
            throw new OrderValidationException(message);
        }
    }
// if a new order is being added successfully then it is recorded by the audit 
    @Override
    public FMAOrder addOrder(FMAOrder o) throws DataPersistenceException {
        ordersDao.addOrder(o);
        auditDao.writeAuditEntry("Order #"
                + o.getOrderNumber() + " for date "
                + o.getDate() + " ADDED.");
        return o;
    }
// this compares the edit order to the original, if theres no changes it will leave it
    // if the changes exist nothing will change 
    @Override
    public FMAOrder compareOrders(FMAOrder savedOrder, FMAOrder editedOrder)
            throws DataPersistenceException, StateValidationException,
            ProductValidationException {

        //This will only update the already saved order's fields
        if (editedOrder.getCustomerName() == null
                || editedOrder.getCustomerName().trim().equals("")) {
            //No change
        } else {
            savedOrder.setCustomerName(editedOrder.getCustomerName());
        }

        if (editedOrder.getStateAbbr() == null
                || editedOrder.getStateAbbr().trim().equals("")) {
        } else {
            savedOrder.setStateAbbr(editedOrder.getStateAbbr());
            calculateTax(savedOrder);
        }

        if (editedOrder.getProductType() == null
                || editedOrder.getProductType().equals("")) {
        } else {
            savedOrder.setProductType(editedOrder.getProductType());
            calculateMaterial(savedOrder);
        }

        if (editedOrder.getArea() == null
                || (editedOrder.getArea().compareTo(BigDecimal.ZERO)) == 0) {
        } else {
            savedOrder.setArea(editedOrder.getArea());
        }

        calculateTotal(savedOrder);

        return savedOrder;
    }
// if the order is has been selected to be editted and the order number exists
    // the audit file will write the order number and mention its been editted 
    //if the order number cannot be located an error is thrown
    @Override
    public FMAOrder editOrder(FMAOrder updatedOrder) throws DataPersistenceException,
            InvalidOrderNumberException {
        updatedOrder = ordersDao.editOrder(updatedOrder);
        if (updatedOrder != null) {
            auditDao.writeAuditEntry("Order #"
                    + updatedOrder.getOrderNumber() + " for date "
                    + updatedOrder.getDate() + " EDITED.");
            return updatedOrder;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders with that number "
                    + "exist on that date.");
        }
    }
// if an order is selected to be cancelled and it follows thorough then 
    // this is written to the audit text file and timestamped 
    // if the order doesnt correlate to the system and error is thrown
    @Override
    public FMAOrder cancelOrder(FMAOrder cancelledOrder) throws DataPersistenceException,
            InvalidOrderNumberException {
        cancelledOrder = ordersDao.cancelOrder(cancelledOrder);
        if (cancelledOrder != null) {
            auditDao.writeAuditEntry("Order #"
                    + cancelledOrder.getOrderNumber() + " for date "
                    + cancelledOrder.getDate() + " CANCELLED.");
            return cancelledOrder;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders with that number "
                    + "exist on that date.");
        }
    }
// exporting all files created in the order folder to a new backup folder 
    
    @Override
    public void backup() throws DataPersistenceException,
            DataExportException {
        ordersDao.backUpAllOrders();
    }

}
