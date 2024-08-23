package manager;

import db.DatabaseConnection;
import model.Order;
import model.OrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderManager {
    public String getOrderId() throws SQLException, ClassNotFoundException {
        PreparedStatement statement= DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT orderId FROM `Order` ORDER BY orderId DESC LIMIT 1");
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            int index=Integer.parseInt(resultSet.getString(1).split("-")[1]);
            if(index<9){
                return "O-000"+ ++index;
            }else if(index<99){
                return "O-00"+ ++index;
            }else if(index<999){
                return "O-0"+ ++index;
            }else{
                return "O-"+ ++index;
            }
        }else{
            return "O-0001";
        }
    }

    public boolean placeOrder(Order order) {
        Connection connection=null;
        try{
            connection=DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if(isOrderSaved(order,connection) && isOrderDetailSaved(order,connection)){
                connection.commit();
                return true;
            }else{
                connection.rollback();
                return false;
            }
        }catch(SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        } finally{
            try {
                assert connection != null;
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private boolean isOrderDetailSaved(Order order, Connection connection) {
        ArrayList<OrderDetail> detailList=order.getDetailList();
        try {
            int affectedRows=0;
            for(OrderDetail temp : detailList) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `Order Detail` VALUES(?,?,?,?,?)");
                statement.setObject(1, temp.getItemCode());
                statement.setObject(2, order.getOrderId());
                statement.setObject(3, temp.getOrderQty());
                statement.setObject(4, temp.getPrice());
                statement.setObject(5, temp.getDiscount());
                if(statement.executeUpdate()>0){
                    affectedRows++;
                }else{
                    return false;
                }
            }
            System.out.println(detailList.size()+"-->"+affectedRows);
            return detailList.size()==affectedRows;
        }catch(SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    private boolean isOrderSaved(Order order, Connection connection) {
        try {
            PreparedStatement statement=connection.prepareStatement("INSERT INTO `Order` VALUES(?,?,?,?,?)");
            statement.setObject(1, order.getOrderId());
            statement.setObject(2, order.getCustId());
            statement.setObject(3, order.getOrderDate());
            statement.setObject(4, order.getOrderTime());
            statement.setObject(5, order.getCost());
            return statement.executeUpdate()>0;
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return false;
    }

    public ArrayList<Order> getAllOrders() throws SQLException, ClassNotFoundException {
        Connection connection=DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement=connection.prepareStatement("SELECT * FROM `Order`");
        ResultSet resultSet=statement.executeQuery();
        ArrayList<Order> orders=new ArrayList<>();
        while(resultSet.next()){
            orders.add(new Order(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getDouble(5),
                            new OrderManager().getAllOrderDetails(resultSet.getString(1))
                    )
            );
        }
        return orders;
    }
    public Order getOrder(String oId) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order` WHERE orderId=?");
        statement.setObject(1,oId);
        ResultSet resultSet=statement.executeQuery();
        Order order=null;
        if(resultSet.next()){
            order=new Order(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getDouble(5),
                            new OrderManager().getAllOrderDetails(resultSet.getString(1))
                    );
        }
        return order;
    }

    private ArrayList<OrderDetail> getAllOrderDetails(String oId) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order Detail` WHERE orderId=?");
        statement.setObject(1,oId);
        ResultSet resultSet=statement.executeQuery();
        ArrayList<OrderDetail> orderDetails=new ArrayList<>();
        while(resultSet.next()){
            orderDetails.add(new OrderDetail(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getInt(3),
                            resultSet.getDouble(4),
                            resultSet.getDouble(5)
                    )
            );
        }
        return orderDetails;
    }

    public boolean updateOrder(Order o) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("UPDATE `Order` SET custId=?, orderDate=?, time=?, cost=? WHERE orderId=?");
        statement.setObject(5,o.getOrderId());
        statement.setObject(1,o.getCustId());
        statement.setObject(2,o.getOrderDate());
        statement.setObject(3,o.getOrderTime());
        statement.setObject(4,o.getCost());
        return statement.executeUpdate()>0;
    }

    public boolean deleteOrder(String orderId) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("DELETE FROM `Order` WHERE orderId='"+orderId+"'");
        return statement.executeUpdate()>0;
    }

    public double getCustomerIncome(String cId) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order` WHERE custId=?");
        statement.setObject(1,cId);
        ResultSet resultSet=statement.executeQuery();
        double income=0;
        while(resultSet.next()){
            income+=resultSet.getDouble(5);
        }
        return income;
    }

    public int getItemSoldAmount(String code) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order Detail` WHERE itemCode=?");
        statement.setObject(1,code);
        ResultSet resultSet=statement.executeQuery();
        int amount=0;
        while(resultSet.next()){
            amount+=resultSet.getInt(3);
        }
        return amount;
    }

    public double getDailyIncome(int year, int month, int date) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order`");
        ResultSet resultSet=statement.executeQuery();
        double income=0;
        while(resultSet.next()){
            String[] day=resultSet.getString(3).split("-");
            //System.out.println(Arrays.toString(day));
            if(Integer.parseInt(day[0])==year && Integer.parseInt(day[1])==month && Integer.parseInt(day[2])==date){
                income+=resultSet.getDouble(5);
            }
        }
        return income;
    }
    public double getMonthlyIncome(int year, int month) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order`");
        ResultSet resultSet=statement.executeQuery();
        double income=0;
        while(resultSet.next()){
            String[] day=resultSet.getString(3).split("-");
            //System.out.println(Arrays.toString(day));
            if(Integer.parseInt(day[0])==year && Integer.parseInt(day[1])==month){
                income+=resultSet.getDouble(5);
            }
        }
        return income;
    }
    public double getAnnualIncome(int year) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM `Order`");
        ResultSet resultSet=statement.executeQuery();
        double income=0;
        while(resultSet.next()){
            String[] day=resultSet.getString(3).split("-");
            //System.out.println(Arrays.toString(day));
            if(Integer.parseInt(day[0])==year){
                income+=resultSet.getDouble(5);
            }
        }
        return income;
    }
}
