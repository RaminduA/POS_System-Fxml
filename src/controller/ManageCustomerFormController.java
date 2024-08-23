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
import model.Customer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageCustomerFormController {
    public AnchorPane pageContext;

    public TableView<Customer> tblCustomerList;
    public TableColumn colCustomerId;
    public TableColumn colTitle;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colCity;
    public TableColumn colProvince;
    public TableColumn colPostalCode;

    public JFXTextField customerSearchBar;
    public JFXTextField txtCustId1;
    public JFXTextField txtTitle1;
    public JFXTextField txtName1;
    public JFXTextField txtAddress1;
    public JFXTextField txtCity1;
    public JFXTextField txtProvince1;
    public JFXTextField txtPostalCode1;
    public JFXTextField txtCustId2;
    public JFXTextField txtTitle2;
    public JFXTextField txtName2;
    public JFXTextField txtAddress2;
    public JFXTextField txtCity2;
    public JFXTextField txtProvince2;
    public JFXTextField txtPostalCode2;

    public JFXButton btnAdd;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;

    ObservableList<Customer> obList= FXCollections.observableArrayList();

    public void initialize(){
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        try {
            setCustomerId();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        tblCustomerList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> {
                setCustomerData(newValue);
            }
        );
        /*tblCustomerList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
                 try {
                     if((int)newValue>=0){
                         setCustomerData(new CustomerManager().getAllCustomers().get((int)newValue));
                     }
                 } catch (SQLException throwables) {
                     throwables.printStackTrace();
                 } catch (ClassNotFoundException e) {
                     e.printStackTrace();
                 }
             }
        );*/

        try {
            viewAllOnAction();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        FilteredList<Customer> filteredList=new FilteredList<>(obList, b->true);
        customerSearchBar.textProperty().addListener((observable,oldValue,newValue)->{
            filteredList.setPredicate(customer->{
                return filterSearchCustomers(newValue,customer);
            });
        });
        SortedList<Customer> sortedList=new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tblCustomerList.comparatorProperty());
        tblCustomerList.setItems(sortedList);

    }

    private boolean filterSearchCustomers(String newValue, Customer customer) {
        if(newValue==null || newValue.isEmpty()) {
            return true;
        }
        String search=newValue.toLowerCase();
        if(customer.getId().toLowerCase().indexOf(search)>=0){
            return true;
        }else if(customer.getTitle().toLowerCase().indexOf(search)>=0){
            return true;
        }else if(customer.getName().toLowerCase().indexOf(search)>=0){
            return true;
        }else if(customer.getAddress().toLowerCase().indexOf(search)>=0){
            return true;
        }else if(customer.getCity().toLowerCase().indexOf(search)>=0){
            return true;
        }else if(customer.getProvince().toLowerCase().indexOf(search)>=0){
            return true;
        }else if(customer.getPostalCode().toLowerCase().indexOf(search)>=0){
            return true;
        }else{
            return false;
        }
    }

    private void setCustomerId() throws SQLException, ClassNotFoundException {
        txtCustId1.setText(new CustomerManager().getCustomerId());
    }
    private void clearAllFields() {
        txtCustId1.clear();
        txtTitle1.clear();
        txtName1.clear();
        txtAddress1.clear();
        txtCity1.clear();
        txtProvince1.clear();
        txtPostalCode1.clear();
        txtCustId2.clear();
        txtTitle2.clear();
        txtName2.clear();
        txtAddress2.clear();
        txtCity2.clear();
        txtProvince2.clear();
        txtPostalCode2.clear();
    }

    public void addCustomerOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        if(isFilled(1)){
            Customer customer=new Customer(
                    txtCustId1.getText(),
                    txtTitle1.getText(),
                    txtName1.getText(),
                    txtAddress1.getText(),
                    txtCity1.getText(),
                    txtProvince1.getText(),
                    txtPostalCode1.getText()
            );
            if(new CustomerManager().addCustomer(customer)){
                new Alert(Alert.AlertType.CONFIRMATION,"Saved...").show();
                clearAllFields();
                setCustomerId();
                refreshPage();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Fields not filled...").show();
        }

    }
    public void updateCustomerOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        if(isFilled(2)){
            Customer customer=new Customer(
                    txtCustId2.getText(),
                    txtTitle2.getText(),
                    txtName2.getText(),
                    txtAddress2.getText(),
                    txtCity2.getText(),
                    txtProvince2.getText(),
                    txtPostalCode2.getText()
            );
            if(new CustomerManager().updateCustomer(customer)){
                new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                clearAllFields();
                setCustomerId();
                refreshPage();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Fields not filled...").show();
        }

    }
    public void deleteCustomerOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        if(isFilled(2)){
            String customerId=txtCustId2.getText();
            if(new CustomerManager().deleteCustomer(customerId)){
                new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                clearAllFields();
                setCustomerId();
                refreshPage();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,"Fields not filled...").show();
        }

    }

    private boolean isFilled(int i) {
        if(i==1 && !txtCustId1.getText().equals("") && !txtTitle1.getText().equals("") && !txtName1.getText().equals("") && !txtAddress1.getText().equals("") && !txtCity1.getText().equals("") && !txtProvince1.getText().equals("") && !txtPostalCode1.getText().equals("")){
            return true;
        }else if(i==2 && !txtCustId2.getText().equals("") && !txtTitle2.getText().equals("") && !txtName2.getText().equals("") && !txtAddress2.getText().equals("") && !txtCity2.getText().equals("") && !txtProvince2.getText().equals("") && !txtPostalCode2.getText().equals("")){
            return true;
        }
        return false;
    }

    public void viewAllOnAction() throws SQLException, ClassNotFoundException {
        obList.clear();
        ArrayList<Customer> customers=new CustomerManager().getAllCustomers();
        obList.addAll(customers);
        tblCustomerList.setItems(obList);
    }

    private void refreshPage() throws IOException {
        pageContext.getChildren().add(FXMLLoader.load(getClass().getResource("../view/ManageCustomerForm.fxml")));
    }

    public void selectCustomerOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String customerId=txtCustId2.getText();
        Customer customer=new CustomerManager().getCustomer(customerId);
        if (customer!=null) {
            setCustomerData(customer);
        } else {
            new Alert(Alert.AlertType.WARNING, "Empty Result Set").show();
        }
    }
    private void setCustomerData(Customer c) {
        txtCustId2.setText(c.getId());
        txtTitle2.setText(c.getTitle());
        txtName2.setText(c.getName());
        txtAddress2.setText(c.getAddress());
        txtCity2.setText(c.getCity());
        txtProvince2.setText(c.getProvince());
        txtPostalCode2.setText(c.getPostalCode());
    }
}
