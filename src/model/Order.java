package model;

import java.util.ArrayList;

public class Order{
	private String orderId;
    private String custId;
    private String orderDate;
    private String orderTime;
    private double cost;
    private ArrayList<OrderDetail> detailList;

    public Order() {   }

    public Order(String orderId, String custId, String orderDate, String orderTime, double cost, ArrayList<OrderDetail> detailList) {
        this.setOrderId(orderId);
        this.setCustId(custId);
        this.setOrderDate(orderDate);
        this.setOrderTime(orderTime);
        this.setCost(cost);
        this.setDetailList(detailList);
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustId() {
        return custId;
    }
    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    public ArrayList<OrderDetail> getDetailList() {
        return detailList;
    }
    public void setDetailList(ArrayList<OrderDetail> detailList) {
        this.detailList = detailList;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + getOrderId() + '\'' +
                ", custId='" + getCustId() + '\'' +
                ", orderDate='" + getOrderDate() + '\'' +
                ", orderTime='" + getOrderTime() + '\'' +
                ", cost=" + getCost() +
                ", detailList=" + getDetailList() +
            '}';
    }
}
