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
import com.example.Pharmacy.service.RedisService;
import com.example.Pharmacy.service.UserService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.Pharmacy.messages.Orders.OrderExceptionMessages.*;
import static com.example.Pharmacy.messages.Orders.OrderLogMessages.*;
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
    private final RedisService redisService;

    private static final String START = "start";
    private static final String END = "end";
    private static final String ORDER_PLACED = "Order has been placed - %s";
    private static final String ORDER_CANCELLED = "Order has been cancelled - %s";

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
            // Save the order details in the database
            orderRepository.save(order);
            // Save the medication quantity details in the database
            medicationQuantityRepository.saveAll(medicationQuantityList);
            log.info(String.format(LOG_ORDER_PLACED, order.getOrderID()));
            // Update the medication stock
            orderServiceHelper.updateMedicationStock(medicationQuantityList, true);
            log.info(LOG_MEDICATION_STOCK_ORDER_CONFIRMED);
        } catch (Exception exception) {
            log.error(String.format(LOG_UNABLE_TO_PLACE_ORDER, order.getOrderID(), exception.getMessage()));
            throw new OrderException(String.format(UNABLE_TO_PLACE_ORDER, exception.getMessage()));
        }
        // Send the order details to the user via email
        sendOrderDetails(order, true);
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setMedications(orderServiceHelper.getMedicationOrderResponse(medicationQuantityList));
        // Save the order details in Redis Cache
        redisService.setData(order.getOrderID(), orderResponse, 400L);
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
        OrderResponse orderResponse = redisService.getData(orderID, OrderResponse.class);
        if (orderResponse != null) {
            return orderResponse;
        }
        Orders orders = orderRepository.findById(orderID).orElseThrow(() -> {
            log.error(String.format(LOG_ORDER_NOT_FOUND, orderID));
            return new OrderException(String.format(ORDER_NOT_FOUND, orderID));
        });
        orderResponse = orderMapper.toOrderResponse(orders);
        orderResponse.setMedications(orderServiceHelper.getMedicationOrderResponse(orders.getMedicationQuantityList()));
        redisService.setData(orderID, orderResponse, 400L);
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
            log.error(String.format(LOG_ORDER_NOT_FOUND, orderID));
            return new OrderException(String.format(ORDER_NOT_FOUND, orderID));
        });
        if (orders.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new OrderException(ORDER_ALREADY_CANCELLED);
        }
        try {
            orderRepository.cancelOrder(OrderStatus.CANCELLED, orders.getOrderID());
            log.info(String.format(LOG_ORDER_CANCELLED, orderID));
            orderServiceHelper.updateMedicationStock(orders.getMedicationQuantityList(), false);
            log.info(LOG_MEDICATION_STOCK_ORDER_CONFIRMED);
            redisService.deleteData(orderID);
        } catch (Exception exception) {
            throw new OrderException(String.format(UNABLE_TO_CANCEL_ORDER, exception.getMessage()));
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
            throw new OrderException(INVALID_VALUE_ORDER_STATUS);
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
        LocalDate start = localDateMap.get(START);
        LocalDate end = localDateMap.get(END);

        List<Orders> orders = redisService.getData("All-Orders", List.class);
        if (orders == null) {
            orders = orderRepository.findAll();
            redisService.setData("All-Orders", orders, 400L);
        }
        if (orders.isEmpty()) {
            throw new OrderException(NO_ORDERS_FOUND);
        }

        List<Orders> filteredOrders = getFilteredOrders(start, end, orders);
        if (filteredOrders.isEmpty()) {
            throw new OrderException(NO_ORDERS_FOUND);
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
        LocalDate start = localDateMap.get(START);
        LocalDate end = localDateMap.get(END);

        List<Orders> orders = redisService.getData("All-Orders-" + username, List.class);
        if (orders == null) {
            orders = orderRepository.findByUsername(username);
            redisService.setData("All-Orders-" + username, orders, 400L);
        }
        if (orders.isEmpty()) {
            throw new OrderException(NO_ORDERS_FOUND);
        }

        List<Orders> filteredOrders = getFilteredOrders(start, end, orders);
        if (filteredOrders.isEmpty()) {
            throw new OrderException(NO_ORDERS_FOUND);
        }

        return getOrderResponse(new PageImpl<>(filteredOrders, pageable, 0));
    }

    /**
     * This method is used to generate the order data in PDF format
     *
     * @param username
     * @param startDate
     * @param lastDate
     * @return
     */
    @Override
    public ByteArrayOutputStream generateOrderDataInPDF(String username, String startDate, String lastDate) {
        Map<String, LocalDate> localDateMap = validateDates(startDate, lastDate);
        LocalDate start = localDateMap.get(START);
        LocalDate end = localDateMap.get(END);

        List<Orders> orders = redisService.getData("All-Orders-" + username, List.class);
        if (orders == null) {
            orders = orderRepository.findByUsername(username);
            redisService.setData("All-Orders-" + username, orders, 400L);
        }
        if (orders.isEmpty()) {
            throw new OrderException(NO_ORDERS_FOUND);
        }

        List<Orders> filteredOrders = getFilteredOrders(start, end, orders);
        if (filteredOrders.isEmpty()) {
            throw new OrderException(NO_ORDERS_FOUND);
        }

        return addDataToPDF(filteredOrders);
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
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(lastDate);
        } catch (DateTimeParseException exception) {
            throw new OrderException(exception.getMessage());
        }

        if (start.isAfter(end) || end.isBefore(start)) {
            throw new OrderException("Invalid dates");
        }

        localDateMap.put(START, start);
        localDateMap.put(END, end);
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

    /**
     * This method is used to send the order details to the user
     *
     * @param orders
     * @param isConfirmed
     */
    private void sendOrderDetails(Orders orders, boolean isConfirmed) {
        String subject = null;
        if (isConfirmed) {
            subject = String.format(ORDER_PLACED, orders.getOrderID());
        } else {
            subject = String.format(ORDER_CANCELLED, orders.getOrderID());
        }
        String body = String.format("Hi %s, %n%nPlease find the order details below:%nOrder ID - %s%nTotal Amount - %f%n%nThanks,%nTeam Pharmacy", orders.getUsername(), orders.getOrderID(), orders.getTotalAmount());
        String userEmail = userService.getUserDetails(orders.getUsername()).getEmail();
        emailService.sendEmail(userEmail, subject, body);
    }

    /**
     * This method is used to add the data to PDF
     *
     * @param ordersList
     * @return
     */
    private ByteArrayOutputStream addDataToPDF(List<Orders> ordersList) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(outputStream);

        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.add(new Paragraph("Order Details"));

        float[] columnsWidth = {1, 3, 3, 3};

        Table table = new Table(columnsWidth);
        table.addCell(new Cell().add(new Paragraph("SI.NO")));
        table.addCell(new Cell().add(new Paragraph("Order ID")));
        table.addCell(new Cell().add(new Paragraph("Amount")));
        table.addCell(new Cell().add(new Paragraph("Ordered Date")));

        int counter = 1;
        for (Orders order : ordersList) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(counter))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(order.getOrderID()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(order.getTotalAmount()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(order.getOrderedDate()))));
            counter += 1;
        }

        document.add(table);
        document.close();

        return outputStream;
    }
}
