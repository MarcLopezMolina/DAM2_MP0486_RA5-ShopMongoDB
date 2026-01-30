package model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historical_inventory")
public class ProductHistory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_product")
    private int productId;

    @Column
    private String name;

    @Column
    private double wholesalerPrice;

    @Column
    private boolean available;

    @Column
    private int stock;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ProductHistory() {}

    public ProductHistory(Product product)
    {
        this.productId = product.getId();
        this.name = product.getName();
        this.wholesalerPrice = product.getWholesalerPrice().getValue();
        this.available = product.isAvailable();
        this.stock = product.getStock();
        this.createdAt = LocalDateTime.now();
    }
}
