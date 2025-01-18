package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.OrderResponse;
import com.example.Pharmacy.exception.OrderException;
import com.example.Pharmacy.helper.OrderServiceHelper;
import com.example.Pharmacy.mapper.OrderMapper;
import com.example.Pharmacy.model.MedicationQuantity;
import com.example.Pharmacy.model.OrderStatus;
import com.example.Pharmacy.model.Orders;
import com.example.Pharmacy.repo.MedicationQuantityRepository;
import com.example.Pharmacy.repo.OrderRepository;
import com.example.Pharmacy.service.EmailService;
import com.example.Pharmacy.service.OrderService;
import com.example.Pharmacy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.Pharmacy.validators.OrderValidator.validateOrderRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MedicationQuantityRepository medicationQuantityRepository;
    private final OrderMapper orderMapper;
    private final OrderServiceHelper orderServiceHelper;
    private final EmailService emailService;
    private final UserService userService;

    /**
     * This method is used to place the order
     *
     * @param request
     * @return
     */
    @Transactional
    @Override
    public OrderResponse placeOrder(OrderRequest request) {
        validateOrderRequest(request);
        Orders order = orderServiceHelper.generateOrder(request);
        List<MedicationQuantity> medicationQuantityList = orderServiceHelper.generateMedicationQuantity(request.getMedicationOrderRequestList(), order);
        try {
            orderRepository.save(order);
            medicationQuantityRepository.saveAll(medicationQuantityList);
            orderServiceHelper.updateMedicationStock(medicationQuantityList, true);
        } catch (Exception exception) {
            throw new OrderException("Unable to place the order");
        }
        sendOrderDetails(order, true);
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setMedications(orderServiceHelper.getMedicationOrderResponse(medicationQuantityList));
        return orderResponse;
    }

    /**
     * This method is used to get the order details by using orderID
     *
     * @param orderID
     * @return
     */
    @Override
    public OrderResponse getOrderDetails(String orderID) {
        Orders orders = orderRepository.findById(orderID).orElseThrow(() -> new OrderException("Order not found"));
        OrderResponse orderResponse = orderMapper.toOrderResponse(orders);
        orderResponse.setMedications(orderServiceHelper.getMedicationOrderResponse(orders.getMedicationQuantityList()));
        return orderResponse;
    }

    /**
     * This method is used to get all the orders by using username
     *
     * @param username
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderResponse> getAllOrdersByUsername(String username, Pageable pageable) {
        Page<Orders> orders = orderRepository.findByUsername(username, pageable);
        return getOrderResponse(orders);
    }

    /**
     * This method is used to get all the orders
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Orders> orders = orderRepository.findAll(pageable);
        return getOrderResponse(orders);
    }

    /**
     * This method is used to cancel the order by using orderID
     *
     * @param orderID
     * @return
     */
    @Transactional
    @Override
    public OrderResponse cancelOrder(String orderID) {
        Orders orders = orderRepository.findById(orderID).orElseThrow(() -> {
            return new OrderException("Order not found");
        });
        if (orders.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new OrderException("This order has been already cancelled");
        }
        try {
            orderRepository.cancelOrder(OrderStatus.CANCELLED, orders.getOrderID());
            orderServiceHelper.updateMedicationStock(orders.getMedicationQuantityList(), false);
        } catch (Exception exception) {
            throw new OrderException("Unable to cancel the order");
        }
        sendOrderDetails(orders, false);
        return getOrderDetails(orderID);
    }

    /**
     * This method is used to get all the cancelled orders
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderResponse> getAllCancelledOrder(Pageable pageable) {
        Page<Orders> orders = orderRepository.getOrdersByStatus(OrderStatus.CANCELLED, pageable);
        return getOrderResponse(orders);
    }

    /**
     * This method is used to get all the cancelled orders by using username
     *
     * @param username
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderResponse> getAllCancelledOrdersByUsername(String username, Pageable pageable) {
        Page<Orders> orders = orderRepository.getAllOrdersByStatusAndUsername(OrderStatus.CANCELLED, username, pageable);
        return getOrderResponse(orders);
    }

    /**
     * This method is used to get all the orders by using order status
     *
     * @param status
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderResponse> getAllOrdersByStatus(String status, Pageable pageable) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status);
        } catch (Exception exception) {
            throw new OrderException("Un processable value for order status");
        }
        Page<Orders> orders = orderRepository.getOrdersByStatus(orderStatus, pageable);
        return getOrderResponse(orders);
    }

    /**
     * This method is used to get all the orders based on the given dates range
     *
     * @param startDate
     * @param lastDate
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderResponse> getAllOrdersByDate(String startDate, String lastDate, Pageable pageable) {
        Map<String, LocalDate> localDateMap = validateDates(startDate, lastDate);
        LocalDate start = localDateMap.get("start");
        LocalDate end = localDateMap.get("end");

        List<Orders> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new OrderException("Order count = 0");
        }

        List<Orders> filteredOrders = getFilteredOrders(start, end, orders);
        if (filteredOrders.isEmpty()) {
            throw new OrderException("Order count = 0");
        }

        return getOrderResponse(new PageImpl<>(filteredOrders, pageable, 0));
    }

    /**
     * `This method is used to get all the orders based on the given dates range and username
     *
     * @param username
     * @param startDate
     * @param lastDate
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderResponse> getAllOrdersByDateAndUser(String username, String startDate, String lastDate, Pageable pageable) {
        Map<String, LocalDate> localDateMap = validateDates(startDate, lastDate);
        LocalDate start = localDateMap.get("start");
        LocalDate end = localDateMap.get("end");

        List<Orders> orders = orderRepository.findByUsername(username);
        if (orders.isEmpty()) {
            throw new OrderException("Order count = 0");
        }

        List<Orders> filteredOrders = getFilteredOrders(start, end, orders);
        if (filteredOrders.isEmpty()) {
            throw new OrderException("Order count = 0");
        }

        return getOrderResponse(new PageImpl<>(filteredOrders, pageable, 0));
    }

    /**
     * This method is used to get the order response
     *
     * @param orders
     * @return
     */
    private Page<OrderResponse> getOrderResponse(Page<Orders> orders) {
        return orders.map(order -> {
            OrderResponse orderResponse = orderMapper.toOrderResponse(order);
            orderResponse.setMedications(orderServiceHelper.getMedicationOrderResponse(order.getMedicationQuantityList()));
            return orderResponse;
        });
    }

    /**
     * This method is used to validate the given dates
     *
     * @param startDate
     * @param lastDate
     * @return
     */
    private Map<String, LocalDate> validateDates(String startDate, String lastDate) {
        Map<String, LocalDate> localDateMap = new HashMap<>();
        LocalDate start, end;
        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(lastDate);
        } catch (DateTimeParseException exception) {
            throw new OrderException(exception.getMessage());
        }

        if (start.isAfter(end) || end.isBefore(start)) {
            throw new OrderException("Invalid dates");
        }

        localDateMap.put("start", start);
        localDateMap.put("end", end);
        return localDateMap;
    }

    /**
     * This method is used to get the filtered orders based on the given dates
     *
     * @param start
     * @param end
     * @param orders
     * @return
     */
    private List<Orders> getFilteredOrders(LocalDate start, LocalDate end, List<Orders> orders) {
        List<Orders> filteredOrders = new ArrayList<>();
        for (Orders order : orders) {
            LocalDate orderedDate = order.getOrderedDate();
            if (orderedDate.isEqual(start) || orderedDate.equals(end) || (orderedDate.isAfter(start) && orderedDate.isBefore(end))) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }

    private void sendOrderDetails(Orders orders, boolean isConfirmed) {
        String subject = null;
        if (isConfirmed) {
            subject = "Order has been placed - " + orders.getOrderID();
        } else {
            subject = "Order has been cancelled - " + orders.getOrderID();
        }
        String body = String.format("Hi %s, %n%nPlease find the order details below:%nOrder ID - %s%nTotal Amount - %f%n%nThanks,%nTeam Pharmacy", orders.getUsername(), orders.getOrderID(), orders.getTotalAmount());
        String userEmail = userService.getUserDetails(orders.getUsername()).getEmail();
        emailService.sendEmail(userEmail, subject, body);
    }

}
