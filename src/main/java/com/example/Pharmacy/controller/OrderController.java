package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.OrderResponse;
import com.example.Pharmacy.model.OrderStatus;
import com.example.Pharmacy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
}
