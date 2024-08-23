package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import manager.CustomerManager;
import manager.ItemManager;
import manager.OrderManager;
import model.Customer;
import model.Item;
import model.Order;
import model.OrderDetail;
import model.tableModel.CartTM;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceOrderFormController {
    public AnchorPane pageContext;

    public Label lblDate;
    public Label lblTime;

    public JFXTextField txtCustId;
    public JFXTextField txtCustName;
    public JFXTextField txtCustAddress;

    public JFXTextField txtItemCode;
    public JFXTextField txtItemDesc;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtQty;
    public JFXButton btnClear;
    public JFXButton btnAddToCart;
    public Label lblTotal;
    public Label lblOrderId;
    public TableView<CartTM> tblCart;
    public TableColumn colItemCode;
    public TableColumn colDesc;
    public TableColumn colQty;
    public TableColumn colUnitPrice;
    public TableColumn colDiscount;
    public TableColumn colPrice;
    public JFXComboBox cmbCustomer;
    public JFXComboBox cmbItem;
    public JFXButton btnPlaceOrder;
    public Label lblItemCount;

    private List<String> customerList;
    private List<String> itemList;
    private double lastDiscount;
    static ObservableList<CartTM> obList = FXCollections.observableArrayList();
    int selectedRow = -1;

    public void initialize() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tblCart.setItems(obList);

        try {
            customerList = new CustomerManager().getAllCustomerIds();
            cmbCustomer.getItems().addAll(customerList);
            itemList = new ItemManager().getAllItemCodes();
            cmbItem.getItems().addAll(itemList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        cmbCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setCustomerData((String) newValue);
        });
        cmbItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setItemData((String) newValue);
        });
        if (obList.isEmpty()) {
            lblTotal.setText("Rs. 0.0");
            lblItemCount.setText("Item Count : 0");
        }
        loadDateAndTime();
        setOrderId();

        tblCart.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            selectedRow = (int) newValue;
        });
    }

    private void setOrderId() {
        try {
            lblOrderId.setText(new OrderManager().getOrderId());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setItemData(String itemCode){
        try {
            Item item=new ItemManager().getItem(itemCode);
            if (item != null) {
                txtItemCode.setText(item.getCode());
                txtItemDesc.setText(item.getDescription());
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
                lastDiscount = roundDouble(item.getDiscountPercent());
            } else {
                new Alert(Alert.AlertType.WARNING, "Empty Result Set");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setCustomerData(String customerId){
        try {
            Customer customer=new CustomerManager().getCustomer(customerId);
            if (customer != null) {
                txtCustId.setText(customer.getId());
                txtCustName.setText(customer.getName());
                txtCustAddress.setText(customer.getAddress());
            } else {
                new Alert(Alert.AlertType.WARNING, "Empty Result Set");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDateAndTime() {
        lblDate.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));

        Timeline time = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            lblTime.setText(currentTime.format(DateTimeFormatter.ofPattern("HH : mm : ss a")));//ISO_LOCAL_TIME.substring(0,8)
        }),
                new KeyFrame(Duration.seconds(1))
        );
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    public void addToCartOnAction(ActionEvent actionEvent) {
        if (isNotFilled()) {
            new Alert(Alert.AlertType.WARNING, "Fields not filled...").show();
        } else if (Integer.parseInt(txtQtyOnHand.getText()) < Integer.parseInt(txtQty.getText())) {
            new Alert(Alert.AlertType.WARNING, "Quantity not sufficient...").show();
        } else {
            CartTM cartTM = new CartTM(
                    txtItemCode.getText(),
                    txtItemDesc.getText(),
                    Integer.parseInt(txtQty.getText()),
                    roundDouble(Double.parseDouble(txtUnitPrice.getText())),
                    roundDouble(lastDiscount * Double.parseDouble(txtQty.getText()) * Double.parseDouble(txtUnitPrice.getText())),
                    roundDouble((1 - lastDiscount) * Double.parseDouble(txtQty.getText()) * Double.parseDouble(txtUnitPrice.getText()))
            );
            lastDiscount = 0;
            if (isExists(cartTM)) {
                int index = 0;
                for (int i = 0; i<obList.size(); i++) {
                    if (cartTM.getCode().equals(obList.get(i).getCode())) {
                        index=i;
                    }
                }
                CartTM temp = obList.get(index);
                CartTM newCartTM = new CartTM(
                        temp.getCode(),
                        temp.getDescription(),
                        temp.getQuantity() + cartTM.getQuantity(),
                        roundDouble(temp.getUnitPrice()),
                        roundDouble(temp.getDiscount() + cartTM.getDiscount()),
                        roundDouble(temp.getPrice() + cartTM.getPrice())
                );
                obList.remove(index);
                obList.add(newCartTM);
            } else {
                obList.add(cartTM);
            }
            deductItemQuantity(cartTM.getCode(),cartTM.getQuantity());
            tblCart.setItems(obList);
            calculateTotal();
            setItemCount();
        }
    }

    private void setItemCount() {
        lblItemCount.setText("Item Count : "+obList.size());
    }

    private void deductItemQuantity(String code, int quantity){
        try {
            txtQtyOnHand.setText(String.valueOf(Integer.parseInt(txtQtyOnHand.getText())-quantity));
            Item item=new ItemManager().getItem(code);
            item.setQtyOnHand(Integer.parseInt(txtQtyOnHand.getText()));
            if(!new ItemManager().updateItem(item)){
                new Alert(Alert.AlertType.WARNING, "Quantity not updated").show();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (int i = 0; i < obList.size(); i++) {
            total += obList.get(i).getPrice();
        }
        lblTotal.setText("Rs. " + roundDouble(total));
    }

    private boolean isExists(CartTM cartTM) {
        for (int i = 0; i < obList.size(); i++) {
            if (cartTM.getCode().equals(obList.get(i).getCode())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotFilled() {
        if (
                txtCustId.getText().equals("") ||
                txtCustName.getText().equals("") ||
                txtCustAddress.getText().equals("") ||
                txtItemCode.getText().equals("") ||
                txtItemDesc.getText().equals("") ||
                txtUnitPrice.getText().equals("") ||
                txtQtyOnHand.getText().equals("") ||
                txtQty.getText().equals("")
        ) {
            return true;
        }
        return false;
    }

    public void clearItemOnAction(ActionEvent actionEvent) {
        //System.out.println(selectedRow);
        if (selectedRow >= 0) {
            replaceItemQuantity(obList.get(selectedRow));
            obList.remove(selectedRow);
            calculateTotal();
            setItemCount();
            tblCart.refresh();
            //selectedRow = -1;
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a row").show();
        }
    }

    private void replaceItemQuantity(CartTM cartTM) {
        try {
            Item item=new ItemManager().getItem(cartTM.getCode());
            item.setQtyOnHand(item.getQtyOnHand()+cartTM.getQuantity());
            if(new ItemManager().updateItem(item)){
                if(txtItemCode.getText().equals(cartTM.getCode())){
                    item=new ItemManager().getItem(cartTM.getCode());
                    txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
                }
            }else{
                new Alert(Alert.AlertType.WARNING, "Quantity not updated").show();
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private static double roundDouble(double d) {
        BigDecimal bigDecimal=new BigDecimal(Double.toString(d));
        bigDecimal=bigDecimal.setScale(2,RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public void placeOrderOnAction(ActionEvent actionEvent) {
        ArrayList<OrderDetail> detailList=new ArrayList<>();
        //System.out.println(lblTotal.getText().split(" ")[1]);
        for(CartTM temp:obList){
            detailList.add(new OrderDetail(
                    temp.getCode(),
                    lblOrderId.getText(),
                    temp.getQuantity(),
                    roundDouble(temp.getPrice()),
                    roundDouble(temp.getDiscount())
                )
            );
        }
        Order order=new Order(
                        lblOrderId.getText(),
                        txtCustId.getText(),
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                        new SimpleDateFormat("hh:mm:ss a").format(new Date()),
                        Double.parseDouble(lblTotal.getText().split(" ")[1]),
                        detailList
                    );
        if (new OrderManager().placeOrder(order)){
            new Alert(Alert.AlertType.CONFIRMATION, "Order placed...").show();
            setOrderId();
            obList.clear();
            refreshPage();
        }else{
            new Alert(Alert.AlertType.WARNING, "Try Again...").show();
        }
    }

    private void refreshPage(){
        try {
            pageContext.getChildren().add(FXMLLoader.load(getClass().getResource("../view/PlaceOrderForm.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}