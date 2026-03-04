package dao;

import java.util.ArrayList;
import java.util.Date;
import org.bson.types.ObjectId;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplMongoDB implements Dao
{
	MongoCollection<Document> collection;
	ObjectId id;
	private MongoDatabase database;
	private MongoCollection<Document> inventoryCollection;
	private MongoCollection<Document> historicalCollection;
	private MongoCollection<Document> usersCollection;
	
	@Override
	public void connect()
	{
	    String uri = "mongodb://localhost:27017";
	    MongoClientURI mongoClientURI = new MongoClientURI(uri);
	    MongoClient mongoClient = new MongoClient(mongoClientURI);

	    database = mongoClient.getDatabase("shop");

	    inventoryCollection = database.getCollection("inventory");
	    historicalCollection = database.getCollection("historical_inventory");
	    usersCollection = database.getCollection("users");
	}
	
	@Override
	public ArrayList<Product> getInventory()
	{
	    ArrayList<Product> products = new ArrayList<>();

	    for (Document doc : inventoryCollection.find())
	    {
	        Document priceDoc = (Document) doc.get("wholesalerPrice");
	        double value = priceDoc.getDouble("value");

	        Product p = new Product(
	                doc.getString("name"),
	                new Amount(value),
	                doc.getBoolean("available"),
	                doc.getInteger("stock")
	        );

	        p.setId(doc.getInteger("id"));

	        products.add(p);
	    }

	    return products;
	}
	
	@Override
	public void addProduct(Product product)
	{
	    Document lastProduct = inventoryCollection
	            .find()
	            .sort(new Document("id", -1))
	            .first();

	    int newId = 1;
	    if (lastProduct != null)
	    {
	        newId = lastProduct.getInteger("id") + 1;
	    }

	    product.setId(newId);

	    Document priceDoc = new Document("value", product.getWholesalerPrice().getValue())
	                            .append("currency", "€");

	    Document doc = new Document("name", product.getName())
	            .append("wholesalerPrice", priceDoc)
	            .append("available", product.isAvailable())
	            .append("stock", product.getStock())
	            .append("id", product.getId());

	    inventoryCollection.insertOne(doc);
	}
	
	@Override
	public void updateProduct(Product product)
	{
	    Document priceDoc = new Document("value", product.getWholesalerPrice().getValue())
	                            .append("currency", "€");

	    Document updatedFields = new Document("name", product.getName())
	            .append("wholesalerPrice", priceDoc)
	            .append("available", product.isAvailable())
	            .append("stock", product.getStock());

	    inventoryCollection.updateOne(
	            new Document("id", product.getId()),
	            new Document("$set", updatedFields)
	    );
	}
	
	@Override
	public void deleteProduct(int productId)
	{
	    inventoryCollection.deleteOne(new Document("id", productId));
	}
	
	@Override
	public boolean writeInventory(ArrayList<Product> inventario)
	{
	    try
	    {
	        Date now = new Date();

	        for (Product p : inventario)
	        {
	            Document priceDoc = new Document("value", p.getWholesalerPrice().getValue())
	                                    .append("currency", "€");

	            Document doc = new Document("id", p.getId())
	                    .append("name", p.getName())
	                    .append("wholesalerPrice", priceDoc)
	                    .append("available", p.isAvailable())
	                    .append("stock", p.getStock())
	                    .append("created_at", now);

	            historicalCollection.insertOne(doc);
	        }

	        return true;
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        return false;
	    }
	}

	@Override
	public Employee getEmployee(int employeeId, String password)
	{
	    Document doc = usersCollection.find
	    (
	        new Document("employeeId", employeeId)
	            .append("password", password)
	    ).first();

	    if (doc != null)
	    {
	        return new Employee
	        (
	            doc.getInteger("employeeId"),
	            doc.getString("name"),
	            doc.getString("password")
	        );
	    }

	    return null;
	}
	
	//The disconnect method is not necessary for this activity
	@Override
	public void disconnect()
	{
	}
}

