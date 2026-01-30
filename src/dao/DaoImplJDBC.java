package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplJDBC implements Dao
{

	Connection connection;

	@Override
	public void connect()
	{
		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		
		try
		{
			this.connection = DriverManager.getConnection(url, user, pass);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect()
	{
		try
		{
			if (connection != null)
			{
				connection.close();
			}
				
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Employee getEmployee(int employeeId, String password)
	{
		Employee employee = null;
		String query = "SELECT * FROM employee WHERE employeeId = ? AND password = ?";

		try (PreparedStatement ps = connection.prepareStatement(query))
		{
			ps.setInt(1, employeeId);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();

			if (rs.next())
			{
				employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public ArrayList<Product> getInventory()
	{
		ArrayList<Product> products = new ArrayList<>();

		String query = "SELECT id, name, wholesalerPrice, available, stock FROM inventory";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery())
		{

			while (rs.next())
			{
				Product p = new Product(rs.getString("name"), new Amount(rs.getDouble("wholesalerPrice")), rs.getBoolean("available"), rs.getInt("stock"));
				p.setId(rs.getInt("id"));

				products.add(p);
			}

		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return products;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory)
	{

		String sql = """
				    INSERT INTO historical_inventory
				    (id_product, name, wholesalerPrice, available, stock, created_at)
				    VALUES (?, ?, ?, ?, ?, ?)
					""";

		try (PreparedStatement ps = connection.prepareStatement(sql))
		{

			Timestamp now = Timestamp.valueOf(LocalDateTime.now());

			for (Product product : inventory)
			{
				ps.setInt(1, product.getId());
				ps.setString(2, product.getName());
				ps.setDouble(3, product.getWholesalerPrice().getValue());
				ps.setBoolean(4, product.isAvailable());
				ps.setInt(5, product.getStock());
				ps.setTimestamp(6, now);

				ps.addBatch();
			}

			ps.executeBatch();
			return true;

		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void addProduct(Product product)
	{

		String findSql = "SELECT id, stock FROM inventory WHERE name = ?";
		String insertSql = """
				    		INSERT INTO inventory (name, wholesalerPrice, available, stock)
				    		VALUES (?, ?, ?, ?)
							""";
		String updateSql = """
				    UPDATE inventory
				    SET stock = ?, wholesalerPrice = ?, available = ?
				    WHERE id = ?
						""";

		try (PreparedStatement findPs = connection.prepareStatement(findSql))
		{

			findPs.setString(1, product.getName());
			ResultSet rs = findPs.executeQuery();

			if (rs.next())
			{
				int id = rs.getInt("id");
				int newStock = rs.getInt("stock") + product.getStock();

				try (PreparedStatement updatePs = connection.prepareStatement(updateSql))
				{
					updatePs.setInt(1, newStock);
					updatePs.setDouble(2, product.getWholesalerPrice().getValue());
					updatePs.setBoolean(3, product.isAvailable());
					updatePs.setInt(4, id);
					updatePs.executeUpdate();

					product.setId(id);
					product.setStock(newStock);
				}

			} 
			else
			{
				try (PreparedStatement insertPs = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS))
				{

					insertPs.setString(1, product.getName());
					insertPs.setDouble(2, product.getWholesalerPrice().getValue());
					insertPs.setBoolean(3, product.isAvailable());
					insertPs.setInt(4, product.getStock());

					insertPs.executeUpdate();

					ResultSet keys = insertPs.getGeneratedKeys();
					if (keys.next())
					{
						product.setId(keys.getInt(1));
					}
				}
			}

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void updateProduct(Product product)
	{

		if (product.getId() <= 0)
			return;

		String sql = """
				    UPDATE inventory
				    SET name = ?, wholesalerPrice = ?, available = ?, stock = ?
				    WHERE id = ?
				""";

		try (PreparedStatement ps = connection.prepareStatement(sql))
		{

			ps.setString(1, product.getName());
			ps.setDouble(2, product.getWholesalerPrice().getValue());
			ps.setBoolean(3, product.isAvailable());
			ps.setInt(4, product.getStock());
			ps.setInt(5, product.getId());

			ps.executeUpdate();

		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void deleteProduct(int productId)
	{

		String sql = "DELETE FROM inventory WHERE id = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql))
		{
			ps.setInt(1, productId);
			ps.executeUpdate();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}


