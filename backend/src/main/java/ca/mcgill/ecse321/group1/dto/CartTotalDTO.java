package ca.mcgill.ecse321.group1.dto;

public class CartTotalDTO {
    private float cartTotal;

    public CartTotalDTO(float total) {
        this.cartTotal = total;
    }

    public float getCartTotal() {
        return this.cartTotal;
    }

    public void setCartTotal(float total) {
        this.cartTotal = total;
    }
}
