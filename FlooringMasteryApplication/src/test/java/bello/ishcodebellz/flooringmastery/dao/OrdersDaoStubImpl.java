/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmastery.dao;

import bello.ishcodebellz.flooringmasteryapplication.dao.FMAOrdersDao;
import bello.ishcodebellz.flooringmasteryapplication.dto.FMAOrder;
import bello.ishcodebellz.flooringmasteryapplication.service.DataPersistenceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.Order;

/**
 *
 * @author amanda
 */
public class OrdersDaoStubImpl implements FMAOrdersDao {

    private FMAOrder onlyOrder;
    private List<FMAOrder> ordersList = new ArrayList<>();

    public OrdersDaoStubImpl() {

        onlyOrder = new FMAOrder();
        onlyOrder.setDate(LocalDate.parse("04302001",
                DateTimeFormatter.ofPattern("MMddyyyy")));
        onlyOrder.setOrderNumber(1);
        onlyOrder.setCustomerName("Coolest Company");
        onlyOrder.setStateAbbr("IN");
        onlyOrder.setTaxRate(new BigDecimal("6.00"));
        onlyOrder.setProductType("Laminate");
        onlyOrder.setArea(new BigDecimal("100"));
        onlyOrder.setMaterialCostPerSquareFoot(new BigDecimal("1.75"));
        onlyOrder.setLaborCostPerSquareFoot(new BigDecimal("2.10"));
        onlyOrder.setMaterialCost(onlyOrder.getMaterialCostPerSquareFoot()
                .multiply(onlyOrder.getArea()).setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setLaborCost(onlyOrder.getLaborCostPerSquareFoot().multiply(onlyOrder.getArea())
                .setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setTax(onlyOrder.getTaxRate().divide(new BigDecimal("100.00"))
                .multiply((onlyOrder.getMaterialCost().add(onlyOrder.getLaborCost())))
                .setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setTotal(onlyOrder.getMaterialCost().add(onlyOrder.getLaborCost())
                .add(onlyOrder.getTax()));
        onlyOrder.setStatus("ACTIVE");
        ordersList.add(onlyOrder);

    }

    @Override
    public List<FMAOrder> getOrders(LocalDate dateChoice) throws DataPersistenceException {
        if (dateChoice.equals(onlyOrder.getDate())) {
            return ordersList;
        } else {
            //Should return an empty list like the dao does.
            return new ArrayList<>();
        }
    }

    @Override
    public FMAOrder addOrder(FMAOrder o) throws DataPersistenceException {
        ordersList.add(o);
        return o;
    }

    @Override
    public FMAOrder editOrder(FMAOrder editedOrder) throws DataPersistenceException {
        if (editedOrder.getOrderNumber() == onlyOrder.getOrderNumber()) {
            return onlyOrder;
        } else {
            return null;
        }
    }

    @Override
    public FMAOrder cancelOrder(FMAOrder o) throws DataPersistenceException {
        if (o.equals(onlyOrder)) {
            return onlyOrder;
        } else {
            return null;
        }
    }

    @Override
    public void backUpAllOrders() throws DataPersistenceException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
