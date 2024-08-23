package model.tableModel;

public class CartTM {
    private String code;
    private String description;
    private int quantity;
    private double unitPrice;
    private double discount;
    private double price;

    public CartTM() {  }

    public CartTM(String code, String description, int quantity, double unitPrice, double discount, double price) {
        this.setCode(code);
        this.setDescription(description);
        this.setQuantity(quantity);
        this.setUnitPrice(unitPrice);
        this.setDiscount(discount);
        this.setPrice(price);
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getDiscount() {
        return discount;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "CartTM{" +
                "code='" + getCode() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", quantity='" + getQuantity() + '\'' +
                ", unitPrice=" + getUnitPrice() +
                ", discount=" + getDiscount() +
                ", price=" + getPrice() +
            '}';
    }
}
