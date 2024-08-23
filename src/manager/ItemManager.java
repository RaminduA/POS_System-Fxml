package manager;

import db.DatabaseConnection;
import model.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemManager {
    public boolean addItem(Item i) throws SQLException, ClassNotFoundException {
        PreparedStatement statement= DatabaseConnection.getInstance().getConnection().prepareStatement("INSERT INTO Item VALUES (?,?,?,?,?,?)");
        statement.setObject(1,i.getCode());
        statement.setObject(2,i.getDescription());
        statement.setObject(3,i.getPackSize());
        statement.setObject(4,i.getQtyOnHand());
        statement.setObject(5,i.getUnitPrice());
        statement.setObject(6,i.getDiscountPercent());

        return statement.executeUpdate()>0;
    }
    public boolean updateItem(Item i) throws SQLException, ClassNotFoundException {
        PreparedStatement statement= DatabaseConnection.getInstance().getConnection().prepareStatement("UPDATE Item SET description=?, packSize=?, qtyOnHand=?, unitPrice=?, discountPercent=? WHERE itemCode=?");
        statement.setObject(6,i.getCode());
        statement.setObject(1,i.getDescription());
        statement.setObject(2,i.getPackSize());
        statement.setObject(3,i.getQtyOnHand());
        statement.setObject(4,i.getUnitPrice());
        statement.setObject(5,i.getDiscountPercent());
        return statement.executeUpdate()>0;
    }
    public boolean deleteItem(String itemCode) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("DELETE FROM Item WHERE itemCode='"+itemCode+"'");
        return statement.executeUpdate()>0;
    }

    public String getItemCode() throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT itemCode FROM Item ORDER BY itemCode DESC LIMIT 1");
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            int index=Integer.parseInt(resultSet.getString(1).split("-")[1]);
            if(index<10){
                return "I-000"+ ++index;
            }else if(index<100){
                return "I-00"+ ++index;
            }else if(index<1000){
                return "I-0"+ ++index;
            }else{
                return "I-"+ ++index;
            }
        }else{
            return "I-0001";
        }
    }

    public ArrayList<Item> getAllItems() throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Item");
        ResultSet resultSet=statement.executeQuery();
        ArrayList<Item> items=new ArrayList<>();
        while(resultSet.next()){
            items.add(new Item(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getInt(4),
                    resultSet.getDouble(5),
                    resultSet.getDouble(6)
                )
            );
        }
        return items;
    }

    public Item getItem(String itemCode) throws SQLException, ClassNotFoundException {
        PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Item WHERE itemCode=?");
        statement.setObject(1,itemCode);
        ResultSet resultSet=statement.executeQuery();
        if(resultSet.next()){
            return new Item(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getInt(4),
                    resultSet.getDouble(5),
                    resultSet.getDouble(6)
            );
        }else{
            return null;
        }
    }

    public ArrayList<String> getAllItemCodes() throws SQLException, ClassNotFoundException {
        /*PreparedStatement statement=DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Item");
        ResultSet resultSet=statement.executeQuery();*/
        ArrayList<String> itemList=new ArrayList<>();
        ArrayList<Item> items=new ItemManager().getAllItems();
        for(Item i: items){
            itemList.add(i.getCode());
        }
        /*while(resultSet.next()){
            resultSet.getString(1);
        }*/
        return itemList;
    }
}
