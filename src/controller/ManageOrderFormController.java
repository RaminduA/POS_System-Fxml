package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import manager.ItemManager;
import manager.OrderManager;
import model.Order;
import model.OrderDetail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageOrderFormController {
    public AnchorPane pageContext;

    public TableView<Order> tblOrderList;
    public TableColumn colOrderId1;
    public TableColumn colCustId;
    public TableColumn colDate;
    public TableColumn colTime;
    public TableColumn colCost;

    public TableView<OrderDetail> tblOrderDetailList;
    public TableColumn colItemCode;
    public TableColumn colOrderId2;
    public TableColumn colQty;
    public TableColumn colPrice;
    public TableColumn colDiscount;

    public JFXButton btnUpdate;
    public JFXButton btnDelete;
    public JFXTextField orderSearchBar;

    public JFXTextField txtOrderId;
    public JFXTextField txtCustId;
    public JFXTextField txtDate;
    public JFXTextField txtTime;
    public JFXTextField txtCost;

    public JFXTextField txtItemCode;
    public JFXTextField txtQty;
    public JFXTextField txtPrice;
    public JFXTextField txtDiscount;

    ObservableList<Order> obList=FXCollections.observableArrayList();
    ObservableList<OrderDetail> obDetailList=FXCollections.observableArrayList();

    public void initialize(){
        colOrderId1.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colOrderId2.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

        tblOrderList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
                if(isOrderDetailFilled()){
                    obDetailList.clear();
                    //refreshPage();
                }
                //clearAllFields();
                setOrderData(newValue);
                displayOrderDetails(newValue);
            }
        );
        tblOrderDetailList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
                setOrderDetailData(newValue);
            }
        );

        viewAllOnAction();
    }

    private boolean isOrderDetailFilled() {
        if(
            !txtItemCode.getText().equals("") &&
            !txtQty.getText().equals("") &&
            !txtPrice.getText().equals("") &&
            !txtDiscount.getText().equals("")
        ){
            return true;
        }
        return false;
    }
    private boolean isOrderFilled() {
        if(
            !txtOrderId.getText().equals("") &&
            !txtCustId.getText().equals("") &&
            !txtDate.getText().equals("") &&
            !txtTime.getText().equals("") &&
            !txtCost.getText().equals("")
        ){
            return true;
        }
        return false;
    }

    private void clearAllFields() {
        txtOrderId.setText("");
        txtCustId.setText("");
        txtDate.setText("");
        txtTime.setText("");
        txtCost.setText("");
        txtItemCode.setText("");
        txtQty.setText("");
        txtPrice.setText("");
        txtDiscount.setText("");
    }

    private void setOrderDetailData(OrderDetail orderDetail) {
        txtItemCode.setText(orderDetail.getItemCode());
        txtQty.setText(String.valueOf(orderDetail.getOrderQty()));
        txtPrice.setText(String.valueOf(orderDetail.getPrice()));
        txtDiscount.setText(String.valueOf(orderDetail.getDiscount()));
    }

    private void displayOrderDetails(Order order) {
        obDetailList.clear();
        ArrayList<OrderDetail> orderDetails=order.getDetailList();
        obDetailList.addAll(orderDetails);
        tblOrderDetailList.setItems(obDetailList);
    }

    private void setOrderData(Order order) {
        clearAllFields();
        txtOrderId.setText(order.getOrderId());
        txtCustId.setText(order.getCustId());
        txtDate.setText(order.getOrderDate());
        txtTime.setText(order.getOrderTime());
        txtCost.setText(String.valueOf(order.getCost()));
    }

    public void updateOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(isOrderFilled()){
            if(isOrderDetailFilled()){
                new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                clearAllFields();
                refreshPage();
            }else{
                Order order=new OrderManager().getOrder(txtOrderId.getText());
                order.setCustId(txtCustId.getText());
                order.setOrderDate(txtDate.getText());
                order.setOrderTime(txtTime.getText());
                if(new OrderManager().updateOrder(order)){
                    new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                    clearAllFields();
                    refreshPage();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try again...").show();
                }
            }
        }else{
            new Alert(Alert.AlertType.WARNING, "Fields not filled").show();
        }
    }
    public void deleteOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(isOrderFilled()){
            if(isOrderDetailFilled()){
                //String itemCode=txtItemCode.getText();
                new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                clearAllFields();
                refreshPage();
            }else{
                String orderId=txtOrderId.getText();
                if(new OrderManager().deleteOrder(orderId)){
                    new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                    clearAllFields();
                    refreshPage();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try again...").show();
                }
            }
        }else{
            new Alert(Alert.AlertType.WARNING, "Fields not filled").show();
        }
    }

    private void viewAllOnAction(){
        try {
            obList.clear();
            ArrayList<Order> orders=new OrderManager().getAllOrders();
            obList.addAll(orders);
            tblOrderList.setItems(obList);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private void refreshPage(){
        try {
            pageContext.getChildren().add(FXMLLoader.load(getClass().getResource("../view/ManageOrderForm.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
