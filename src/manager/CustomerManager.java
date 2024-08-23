package manager;

import db.DatabaseConnection;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerManager {

    public boolean addCustomer(Customer c) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("INSERT INTO Customer VALUES (?,?,?,?,?,?,?)");
        statement.setObject(1,c.getId());
        statement.setObject(2,c.getTitle());
        statement.setObject(3,c.getName());
        statement.setObject(4,c.getAddress());
        statement.setObject(5,c.getCity());
        statement.setObject(6,c.getProvince());
        statement.setObject(7,c.getPostalCode());
        return statement.executeUpdate()>0;
    }
    public boolean updateCustomer(Customer c) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("UPDATE Customer SET custTitle=?, custName=?, custAddress=?, city=?, province=?, postalCode=? WHERE custId=?");
        statement.setObject(7,c.getId());
        statement.setObject(1,c.getTitle());
        statement.setObject(2,c.getName());
        statement.setObject(3,c.getAddress());
        statement.setObject(4,c.getCity());
        statement.setObject(5,c.getProvince());
        statement.setObject(6,c.getPostalCode());
        return statement.executeUpdate()>0;
    }
    public boolean deleteCustomer(String customerId) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("DELETE FROM Customer WHERE custId='"+customerId+"'");
        return statement.executeUpdate()>0;
    }

    public ArrayList<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Customer");
        ResultSet resultSet=statement.executeQuery();
        ArrayList<Customer> customers=new ArrayList<>();
        while(resultSet.next()){
            customers.add(new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7)
                )
            );
        }
        return customers;
    }

    public String getCustomerId() throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT custId FROM Customer ORDER BY custId DESC LIMIT 1");
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            int index=Integer.parseInt(resultSet.getString(1).split("-")[1]);
            if(index<10){
                return "C-000"+ ++index;
            }else if(index<100){
                return "C-00"+ ++index;
            }else if(index<1000){
                return "C-0"+ ++index;
            }else{
                return "C-"+ ++index;
            }
        }else{
            return "C-0001";
        }
    }

    public Customer getCustomer(String customerId) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Customer WHERE custId=?");
        statement.setObject(1,customerId);
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            return new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7)
            );
        }else{
            return null;
        }
    }

    public ArrayList<String> getAllCustomerIds() throws SQLException, ClassNotFoundException {
        /*PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Customer");
        ResultSet resultSet=statement.executeQuery();*/
        ArrayList<String> customerList=new ArrayList<>();
        ArrayList<Customer> customers=new CustomerManager().getAllCustomers();
        for(Customer c: customers){
           customerList.add(c.getId());
        }
        /*while(resultSet.next()){
            resultSet.getString(1);
        }*/
        return customerList;
    }
}
