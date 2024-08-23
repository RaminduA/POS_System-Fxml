package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import manager.CustomerManager;
import manager.ItemManager;
import model.Customer;
import model.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageItemFormController {
    public AnchorPane pageContext;

    public TableView<Item> tblItemList;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colQtyOnHand;
    public TableColumn colUnitPrice;
    public TableColumn colDiscount;

    public JFXTextField itemSearchBar;
    public JFXTextField txtItemCode1;
    public JFXTextField txtDescription1;
    public JFXTextField txtPackSize1;
    public JFXTextField txtQtyOnHand1;
    public JFXTextField txtUnitPrice1;
    public JFXTextField txtDiscount1;
    public JFXTextField txtItemCode2;
    public JFXTextField txtDescription2;
    public JFXTextField txtPackSize2;
    public JFXTextField txtQtyOnHand2;
    public JFXTextField txtUnitPrice2;
    public JFXTextField txtDiscount2;

    public JFXButton btnAdd;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;


    ObservableList<Item> obList= FXCollections.observableArrayList();

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discountPercent"));

        try {
            setItemCode();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        tblItemList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
                setItemData(newValue);
            }
        );

        try {
            viewAllOnAction();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        FilteredList<Item> filteredList=new FilteredList<>(obList, b->true);
        itemSearchBar.textProperty().addListener((observable,oldValue,newValue)->{
            filteredList.setPredicate(item->{
                return filterSearchItems(newValue,item);
            });
        });
        SortedList<Item> sortedList=new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tblItemList.comparatorProperty());
        tblItemList.setItems(sortedList);
    }

    private boolean filterSearchItems(String newValue, Item item) {
        if(newValue==null || newValue.isEmpty()) {
            return true;
        }
        String search=newValue.toLowerCase();
        if(item.getCode().toLowerCase().contains(search)){
            return true;
        }else if(item.getDescription().toLowerCase().contains(search)){
            return true;
        }else if(item.getPackSize().toLowerCase().contains(search)){
            return true;
        }else if(String.valueOf(item.getQtyOnHand()).toLowerCase().contains(search)){
            return true;
        }else if(String.valueOf(item.getUnitPrice()).toLowerCase().contains(search)) {
            return true;
        }else if(String.valueOf(item.getDiscountPercent()).toLowerCase().contains(search)) {
            return true;
        }else{
            return false;
        }
    }


    public void addItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        if(isFilled(1)){
            Item item=new Item(
                    txtItemCode1.getText(),
                    txtDescription1.getText(),
                    txtPackSize1.getText(),
                    Integer.parseInt(txtQtyOnHand1.getText()),
                    roundDouble(Double.parseDouble(txtUnitPrice1.getText())),
                    roundDouble(Double.parseDouble(txtDiscount1.getText()))
            );
            if(new ItemManager().addItem(item)){
                new Alert(Alert.AlertType.CONFIRMATION,"Saved...").show();
                clearAllFields();
                setItemCode();
                refreshPage();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Fields not filled...").show();
        }

    }
    public void updateItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        if(isFilled(2)){
            Item item=new Item(
                txtItemCode2.getText(),
                txtDescription2.getText(),
                txtPackSize2.getText(),
                Integer.parseInt(txtQtyOnHand2.getText()),
                roundDouble(Double.parseDouble(txtUnitPrice2.getText())),
                roundDouble(Double.parseDouble(txtDiscount2.getText()))
            );
            if(new ItemManager().updateItem(item)){
                new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                clearAllFields();
                setItemCode();
                refreshPage();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Fields not filled...").show();
        }

    }
    public void deleteItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        if(isFilled(2)){
            String itemCode=txtItemCode2.getText();
            if(new ItemManager().deleteItem(itemCode)){
                new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                clearAllFields();
                setItemCode();
                refreshPage();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Fields not filled...").show();
        }

    }

    private void refreshPage() throws IOException {
        pageContext.getChildren().add(FXMLLoader.load(getClass().getResource("../view/ManageItemForm.fxml")));
    }

    private boolean isFilled(int i) {
        if(i==1 && !txtItemCode1.getText().equals("") && !txtDescription1.getText().equals("") && !txtPackSize1.getText().equals("") && !txtQtyOnHand1.getText().equals("") && !txtUnitPrice1.getText().equals("") && !txtDiscount1.getText().equals("")){
            return true;
        }else if(i==2 && !txtItemCode2.getText().equals("") && !txtDescription2.getText().equals("") && !txtPackSize2.getText().equals("") && !txtQtyOnHand2.getText().equals("") && !txtUnitPrice2.getText().equals("") && !txtDiscount2.getText().equals("")){
            return true;
        }
        return false;
    }

    private void clearAllFields() {
        txtItemCode1.clear();
        txtDescription1.clear();
        txtPackSize1.clear();
        txtQtyOnHand1.clear();
        txtUnitPrice1.clear();
        txtDiscount1.clear();
        txtItemCode2.clear();
        txtDescription2.clear();
        txtPackSize2.clear();
        txtQtyOnHand2.clear();
        txtUnitPrice2.clear();
        txtDiscount2.clear();
    }

    private void setItemCode() throws SQLException, ClassNotFoundException {
        txtItemCode1.setText(new ItemManager().getItemCode());
    }

    private void viewAllOnAction() throws SQLException, ClassNotFoundException {
        obList.clear();
        ArrayList<Item> items=new ItemManager().getAllItems();
        obList.addAll(items);
        tblItemList.setItems(obList);
    }

    public void selectItemOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String itemCode=txtItemCode2.getText();
        Item item=new ItemManager().getItem(itemCode);
        if(item!=null){
            setItemData(item);
        }else{
            new Alert(Alert.AlertType.WARNING, "Empty Result Set").show();
        }
    }

    private void setItemData(Item i) {
        txtItemCode2.setText(i.getCode());
        txtDescription2.setText(i.getDescription());
        txtPackSize2.setText(i.getPackSize());
        txtQtyOnHand2.setText(String.valueOf(i.getQtyOnHand()));
        txtUnitPrice2.setText(String.valueOf(i.getUnitPrice()));
        txtDiscount2.setText(String.valueOf(i.getDiscountPercent()));
    }

    private static double roundDouble(double d) {
        BigDecimal bigDecimal=new BigDecimal(Double.toString(d));
        bigDecimal=bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
