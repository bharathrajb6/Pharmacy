package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.OrderResponse;
import com.example.Pharmacy.model.OrderStatus;
import com.example.Pharmacy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * This method is used to place the order
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public OrderResponse placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    /**
     * This method is used to get the order details by using its ID
     *
     * @param orderID
     * @return
     */
    @RequestMapping(value = "/order/{orderID}", method = RequestMethod.GET)
    public OrderResponse getOrderDetails(@PathVariable String orderID) {
        return orderService.getOrderDetails(orderID);
    }

    /**
     * This method will return all the orders for the user
     *
     * @param username
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public Page<OrderResponse> getOrdersByUsername(@RequestParam("username") String username, Pageable pageable) {
        return orderService.getAllOrdersByUsername(username, pageable);
    }

    /**
     * This method will return all the orders
     *
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/order/all", method = RequestMethod.GET)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    /**
     * This method is used to cancel the order by using its ID.
     *
     * @param orderID
     * @return
     */
    @RequestMapping(value = "/order/{orderID}/cancel", method = RequestMethod.PUT)
    public OrderResponse cancelOrder(@PathVariable String orderID) {
        return orderService.cancelOrder(orderID);
    }

    /**
     * This method is used to track the order by using its ID.
     *
     * @param orderID
     * @return
     */
    @RequestMapping(value = "/order/{orderID}/track", method = RequestMethod.GET)
    public OrderStatus trackOrder(@PathVariable String orderID) {
        return orderService.getOrderDetails(orderID).getOrderStatus();
    }

    /**
     * This method will return all the cancelled orders
     *
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/order/cancel", method = RequestMethod.GET)
    public Page<OrderResponse> getAllCancelledOrders(Pageable pageable) {
        return orderService.getAllCancelledOrder(pageable);
    }

    /**
     * This method will return all the cancelled orders for the user
     *
     * @param username
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/order/{username}/cancel", method = RequestMethod.GET)
    public Page<OrderResponse> getAllCancelledOrdersByUsername(@PathVariable String username, Pageable pageable) {
        return orderService.getAllCancelledOrdersByUsername(username, pageable);
    }

    /**
     * This method will return all the orders between the given dates
     *
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/order/filter", method = RequestMethod.GET)
    public Page<OrderResponse> getOrderByDate(@RequestParam(value = "start", required = true) String startDate,
                                              @RequestParam(value = "end", required = true) String endDate,
                                              Pageable pageable) {
        return orderService.getAllOrdersByDate(startDate, endDate, pageable);
    }

    /**
     * This method will return all the orders between the given dates for the user
     *
     * @param username
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/order/filter/{username}", method = RequestMethod.GET)
    public Page<OrderResponse> getOrderByDateAndUsername(@PathVariable(value = "username", required = true) String username,
                                                         @RequestParam(value = "start", required = true) String startDate,
                                                         @RequestParam(value = "end", required = true) String endDate,
                                                         Pageable pageable) {
        return orderService.getAllOrdersByDateAndUser(username, startDate, endDate, pageable);
    }


    /**
     * This method will return the order data in PDF format
     *
     * @param username
     * @param start
     * @param end
     * @return
     */
    @RequestMapping(value = "/order/filter/pdf/{username}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getOrdersDate(@PathVariable String username,
                                                @RequestParam(value = "start", required = true) String start,
                                                @RequestParam(value = "end", required = true) String end) {

        ByteArrayOutputStream outputStream = orderService.generateOrderDataInPDF(username, start, end);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
    }
}
