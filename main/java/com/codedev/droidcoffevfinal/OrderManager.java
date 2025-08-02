package com.codedev.droidcoffevfinal;

import java.util.HashMap;
import java.util.Map;

public class OrderManager {
    private static OrderManager instance;
    private Map<String, Integer> orderItems;
    private OrderUpdateListener orderUpdateListener;

    // Item constants
    public static final String DONUT = "donut";
    public static final String ICE_CREAM = "ice_cream";
    public static final String FROYO = "froyo";

    // Price constants
    public static final double DONUT_PRICE = 25.00;
    public static final double ICE_CREAM_PRICE = 35.00;
    public static final double FROYO_PRICE = 30.00;

    private OrderManager() {
        orderItems = new HashMap<>();
        orderItems.put(DONUT, 0);
        orderItems.put(ICE_CREAM, 0);
        orderItems.put(FROYO, 0);
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void addItem(String itemType) {
        int currentCount = orderItems.get(itemType);
        orderItems.put(itemType, currentCount + 1);
        notifyOrderUpdate();
    }

    public void removeItem(String itemType) {
        int currentCount = orderItems.get(itemType);
        if (currentCount > 0) {
            orderItems.put(itemType, currentCount - 1);
            notifyOrderUpdate();
        }
    }

    public int getItemCount(String itemType) {
        return orderItems.get(itemType);
    }

    public int getTotalItems() {
        int total = 0;
        for (int count : orderItems.values()) {
            total += count;
        }
        return total;
    }

    public double getSubtotal() {
        double subtotal = 0;
        subtotal += orderItems.get(DONUT) * DONUT_PRICE;
        subtotal += orderItems.get(ICE_CREAM) * ICE_CREAM_PRICE;
        subtotal += orderItems.get(FROYO) * FROYO_PRICE;
        return subtotal;
    }

    public void clearOrder() {
        orderItems.put(DONUT, 0);
        orderItems.put(ICE_CREAM, 0);
        orderItems.put(FROYO, 0);
        notifyOrderUpdate();
    }

    public void setOrderUpdateListener(OrderUpdateListener listener) {
        this.orderUpdateListener = listener;
    }

    private void notifyOrderUpdate() {
        if (orderUpdateListener != null) {
            orderUpdateListener.onOrderUpdated();
        }
    }

    public interface OrderUpdateListener {
        void onOrderUpdated();
    }
}
