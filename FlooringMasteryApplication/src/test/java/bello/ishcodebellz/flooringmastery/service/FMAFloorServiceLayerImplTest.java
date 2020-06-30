/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bello.ishcodebellz.flooringmastery.service;

import bello.ishcodebellz.flooringmastery.dao.AuditDaoStubImpl;
import bello.ishcodebellz.flooringmastery.dao.OrdersDaoStubImpl;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAAuditDao;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAOrdersDao;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAProductsDao;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAProductsDaoFileImpl;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAStatesDao;
import bello.ishcodebellz.flooringmasteryapplication.dao.FMAStatesDaoFileImpl;
import bello.ishcodebellz.flooringmasteryapplication.dto.FMAOrder;
import bello.ishcodebellz.flooringmasteryapplication.service.FMAFloorServiceLayer;
import bello.ishcodebellz.flooringmasteryapplication.service.FMAFloorServiceLayerImpl;
import bello.ishcodebellz.flooringmasteryapplication.service.InvalidOrderNumberException;
import bello.ishcodebellz.flooringmasteryapplication.service.OrderValidationException;
import bello.ishcodebellz.flooringmasteryapplication.service.ProductValidationException;
import bello.ishcodebellz.flooringmasteryapplication.service.StateValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author amanda
 */
public class FMAFloorServiceLayerImplTest {

    private FMAFloorServiceLayer service;

    public FMAFloorServiceLayerImplTest() {
        FMAOrdersDao ordersDao = new OrdersDaoStubImpl();
        FMAProductsDao productsDao = new FMAProductsDaoFileImpl();
        FMAStatesDao statesDao = new FMAStatesDaoFileImpl();
        FMAAuditDao auditDao = new AuditDaoStubImpl();

        service = new FMAFloorServiceLayerImpl(ordersDao, productsDao, statesDao, auditDao);
    }

    /**
     * Test of getOrders method, of class FloorServiceImpl.
     */
    @Test
    public void testGetOrders() throws Exception {

        assertEquals(1, service.getOrders(LocalDate.of(2001, 04, 30)).size());

        try {
            List<FMAOrder> orders = service.getOrders(LocalDate.of(1990, 01, 01));
            fail("Expected InvalidOrderNumberException was not thrown.");
        } catch (InvalidOrderNumberException e) {
        }
    }

    /**
     * Test of getOrder method, of class FloorServiceImpl.
     */
    @Test
    public void testGetOrder() throws Exception {
        FMAOrder order = service.getOrder(LocalDate.of(2001, 04, 30), 1);
        assertNotNull(order);

        try {
            order = service.getOrder(LocalDate.of(2001, 04, 30), 0);
            fail("Expected InvalidOrderNumberException was not thrown.");
        } catch (InvalidOrderNumberException e) {
        }

        try {
            service.getOrder(LocalDate.of(1990, 01, 01), 1);
            fail("Expected InvalidOrderNumberException was not thrown.");
        } catch (InvalidOrderNumberException e) {
        }

    }

    /**
     * Test of calculateOrder method, of class FloorServiceImpl.
     */
//    @Test
//    public void testCalculateOrder() throws Exception {
//
//        FMAOrder order1 = new FMAOrder();
//        order1.setCustomerName("Place LLC");
//        order1.setStateAbbr("MI");
//        order1.setProductType("Wood");
//        order1.setArea(new BigDecimal("100"));
//
//        FMAOrder order2 = new FMAOrder();
//        order2.setCustomerName("Place LLC");
//        order2.setStateAbbr("MI");
//        order2.setProductType("Wood");
//        order2.setArea(new BigDecimal("100"));
//
//        assertEquals(service.calculateOrder(order1), service.calculateOrder(order2));
//
//        order1.setCustomerName("");
//
//        try {
//            service.calculateOrder(order1);
//            fail("Expected OrderValidationException was not thrown.");
//        } catch (OrderValidationException e) {
//        }
//
//        order1.setCustomerName("Place LLC");
//        order1.setStateAbbr("");
//
//        try {
//            service.calculateOrder(order1);
//            fail("Expected OrderValidationException was not thrown.");
//        } catch (OrderValidationException e) {
//        }
//
//        order1.setStateAbbr("MI");
//        order1.setProductType("");
//
//        try {
//            service.calculateOrder(order1);
//            fail("Expected OrderValidationException was not thrown.");
//        } catch (OrderValidationException e) {
//        }
//
//        order1.setProductType("Wood");
//        order1.setArea(new BigDecimal("0"));
//
//        try {
//            service.calculateOrder(order1);
//            fail("Expected OrderValidationException was not thrown.");
//        } catch (OrderValidationException e) {
//        }
//
//        order1.setArea(new BigDecimal("100"));
//        order1.setStateAbbr("MN");
//
//        try {
//            service.calculateOrder(order1);
//            fail("Expected StateValidationException was not thrown.");
//        } catch (StateValidationException e) {
//        }
//
//        order1.setStateAbbr("MI");
//        order1.setProductType("Glass");
//
//        try {
//            service.calculateOrder(order1);
//            fail("Expected ProductValidationException was not thrown.");
//        } catch (ProductValidationException e) {
//        }
//
//    }

    /**
     * Test of addOrder method, of class FloorServiceImpl.
     */
    @Test
    public void testAddOrder() throws Exception {

        FMAOrder order = new FMAOrder();
        order.setCustomerName("Place LLC");
        order.setStateAbbr("MI");
        order.setProductType("Wood");
        order.setArea(new BigDecimal("100"));
        service.addOrder(order);

        assertEquals(order, service.addOrder(order));

    }

    /**
     * Test of compareOrders method, of class FloorServiceImpl.
     */
    @Test
    public void testCompareOrders() throws Exception {

        FMAOrder savedOrder = service.getOrder(LocalDate.of(2001, 04, 30), 1);

        FMAOrder editedOrder = new FMAOrder();
        editedOrder.setCustomerName("Peanut Butter LLC");

        FMAOrder updatedOrder = service.compareOrders(savedOrder, editedOrder);

        assertEquals(updatedOrder, savedOrder);

    }

    /**
     * Test of editOrder method, of class FloorServiceImpl.
     */
    @Test
    public void testEditOrder() throws Exception {

        FMAOrder savedOrder = service.getOrder(LocalDate.of(2001, 04, 30), 1);
        assertNotNull(savedOrder);

        try {
            savedOrder = service.getOrder(LocalDate.of(2001, 04, 30), 0);
            fail("Expected InvalidOrderNumberException was not thrown.");
        } catch (InvalidOrderNumberException e) {
        }

    }

    /**
     * Test of removeOrder method, of class FloorServiceImpl.
     */
    @Test
    public void testRemoveOrder() throws Exception {

        FMAOrder removedOrder = service.getOrder(LocalDate.of(2001, 04, 30), 1);
        assertNotNull(removedOrder);

        try {
            removedOrder = service.getOrder(LocalDate.of(2001, 04, 30), 0);
            fail("Expected InvalidOrderNumberException was not thrown.");
        } catch (InvalidOrderNumberException e) {
        }

    }

}
