package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import manager.CustomerManager;
import manager.ItemManager;
import manager.OrderManager;
import model.Customer;
import model.Item;
import model.tableModel.CustomerIncomeTM;
import model.tableModel.MovableItemTM;

import java.sql.SQLException;
import java.util.ArrayList;

public class ViewReportsFormController {
    public AnchorPane pageContext;

    public Label lblMovableItem;
    public Label lblItemCode;
    public Label lblRemainingCount;
    public Label lblSoldCount;

    public JFXButton btnMostMovable;
    public JFXButton btnLeastMovable;

    public TableView<CustomerIncomeTM> tblCustomerIncomeList;
    public TableColumn colCustomerId;
    public TableColumn colName;
    public TableColumn colTotalIncome;

    public JFXTextField customerSearchBar;

    public ComboBox<Integer> cmbYear1;
    public ComboBox<Integer> cmbMonth1;
    public ComboBox<Integer> cmbDate1;
    public ComboBox<Integer> cmbYear2;
    public ComboBox<Integer> cmbMonth2;
    public ComboBox<Integer> cmbYear3;

    public Label lblDailyIncome;
    public Label lblMonthlyIncome;
    public Label lblAnnualIncome;

    ObservableList<CustomerIncomeTM> obList=FXCollections.observableArrayList();
    MovableItemTM leastMovable;
    MovableItemTM mostMovable;

    int year1;
    int month1;
    int date1;

    int year2;
    int month2;

    int year3;

    public void initialize(){
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("custName"));
        colTotalIncome.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
        clearLabels();
        sortItems();
        viewAllRecords();
        setComboBoxes();

        cmbYear1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            year1=newValue;
        });
        cmbMonth1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            month1=newValue;
        });
        cmbDate1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            date1=newValue;
        });

        cmbYear2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            year2=newValue;
        });
        cmbMonth2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            month2=newValue;
        });

        cmbYear3.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            year3=newValue;
        });
    }

    private void setComboBoxes() {
        for(int i=1;i<=31;i++) {
           cmbDate1.getItems().add(i);
        }
        for(int i=1;i<=12;i++) {
           cmbMonth1.getItems().add(i);
           cmbMonth2.getItems().add(i);
        }
        for(int i=2016;i<=2021;i++) {
           cmbYear1.getItems().add(i);
           cmbYear2.getItems().add(i);
           cmbYear3.getItems().add(i);
        }
    }

    private void clearLabels() {
        lblMovableItem.setText("");
        lblItemCode.setText("");
        lblRemainingCount.setText("");
        lblSoldCount.setText("");
    }

    private void sortItems() {
        try {
            ArrayList<Item> items=new ItemManager().getAllItems();
            ArrayList<MovableItemTM> itemList=new ArrayList<>();
            for(Item item: items){
                itemList.add(
                        new MovableItemTM(
                                item.getCode(),
                                new OrderManager().getItemSoldAmount(item.getCode()),
                                item.getQtyOnHand()
                        )
                );
            }
            int max=itemList.get(0).getSoldCount();
            mostMovable=itemList.get(0);
            int min=itemList.get(0).getSoldCount();
            leastMovable=itemList.get(0);
            for(MovableItemTM item: itemList) {
                if (item.getSoldCount() < min) {
                    min = item.getSoldCount();
                    leastMovable = item;
                }
                if(item.getSoldCount()>max){
                    max=item.getSoldCount();
                    mostMovable=item;
                }
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private void viewAllRecords() {
        try {
            ArrayList<Customer> customers=new CustomerManager().getAllCustomers();
            for(Customer cust: customers){
                obList.add(
                      new CustomerIncomeTM(
                              cust.getId(),
                              cust.getName(),
                              new OrderManager().getCustomerIncome(cust.getId())
                      )
                );
                tblCustomerIncomeList.setItems(obList);
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void selectMostMovableItem(ActionEvent actionEvent) {
        if(mostMovable!=null){
            lblMovableItem.setText("Most Movable Item");
            lblItemCode.setText("Item Code : " + mostMovable.getItemCode());
            lblRemainingCount.setText("Remaining Item Count : " + mostMovable.getRemainingCount());
            lblSoldCount.setText("Sold Item Count : " + mostMovable.getSoldCount());
        }
    }
    public void selectLeastMovableItem(ActionEvent actionEvent) {
        if(leastMovable!=null){
            lblMovableItem.setText("Least Movable Item");
            lblItemCode.setText("Item Code : " + leastMovable.getItemCode());
            lblRemainingCount.setText("Remaining Item Count : " + leastMovable.getRemainingCount());
            lblSoldCount.setText("Sold Item Count : " + leastMovable.getSoldCount());
        }
    }

    public void viewDailyIncome(ActionEvent actionEvent) {
        try {
            if(year1!=0 && month1!=0 && date1!=0){
                //System.out.println(year1+" "+month1+" "+date1);
                double income= 0;
                income = new OrderManager().getDailyIncome(year1,month1,date1);
                lblDailyIncome.setText("Rs. "+income);
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void viewMonthlyIncome(ActionEvent actionEvent) {
        try {
            if(year2!=0 && month2!=0){
                //System.out.println(year2+" "+month2);
                double income=new OrderManager().getMonthlyIncome(year2,month2);
                lblMonthlyIncome.setText("Rs. "+income);
            }
        } catch (SQLException | ClassNotFoundException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
    }

    public void viewAnnualIncome(ActionEvent actionEvent) {
        try {
            if(year3!=0){
                //System.out.println(year3);
                double income=new OrderManager().getAnnualIncome(year3);
                lblAnnualIncome.setText("Rs. "+income);
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}
