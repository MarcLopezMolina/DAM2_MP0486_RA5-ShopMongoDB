package model;

import javax.persistence.*;

@Entity
@Table(name = "inventory")
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    // Nuevo atributo requerido por el enunciado
    @Column(name = "wholesalerPrice")
    private double price;

    @Column
    private boolean available;

    @Column
    private int stock;

    // No se persisten, se mantienen por compatibilidad
    @Transient
    private Amount wholesalerPrice;

    @Transient
    private Amount publicPrice;

    @Transient
    private static int totalProducts;

    public final static double EXPIRATION_RATE = 0.60;

    public Product() {}

    public Product(String name, Amount wholesalerPrice, boolean available, int stock)
    {
        this.name = name;
        this.wholesalerPrice = wholesalerPrice;
        this.price = wholesalerPrice.getValue();
        this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
        this.available = available;
        this.stock = stock;
        totalProducts++;
    }

    @PostLoad
    private void syncPrices()
    {
        this.wholesalerPrice = new Amount(price);
        this.publicPrice = new Amount(price * 2);
    }

    @PrePersist
    @PreUpdate
    private void syncDbPrice()
    {
        if (wholesalerPrice != null)
        {
            this.price = wholesalerPrice.getValue();
        }
    }

    public void expire()
    {
        this.publicPrice.setValue(this.publicPrice.getValue() * EXPIRATION_RATE);
    }

    // Getters & setters (TODOS)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Amount getPublicPrice() { return publicPrice; }
    public void setPublicPrice(Amount publicPrice) { this.publicPrice = publicPrice; }

    public Amount getWholesalerPrice() { return wholesalerPrice; }
    public void setWholesalerPrice(Amount wholesalerPrice)
    {
        this.wholesalerPrice = wholesalerPrice;
        this.price = wholesalerPrice.getValue();
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString()
    {
        return "Product [name=" + name +
               ", publicPrice=" + publicPrice +
               ", wholesalerPrice=" + wholesalerPrice +
               ", available=" + available +
               ", stock=" + stock + "]";
    }
}
