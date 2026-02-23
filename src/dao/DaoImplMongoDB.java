package dao;

import java.util.ArrayList;

import model.Employee;
import model.Product;

public class DaoImplMongoDB implements Dao
{

	@Override
	public void connect()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void disconnect()
	{
		// TODO Auto-generated method stub
		
	}
	
	

	@Override
	public ArrayList<Product> getInventory()
	{
		//TODO Implementar lógica del método
		return null;
	}
	
	@Override
	public void addProduct(Product product)
	{
		//TODO Implementar lógica del método
		//TODO HAY QUE HACER LA LÓGICA DE DAO DE MONGODB
		
	}
	
	@Override
	public void updateProduct(Product product)
	{
		// TODO Implementar lógica del método
		
	}
	
	@Override
	public void deleteProduct(int productId)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean writeInventory(ArrayList<Product> inventario)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Employee getEmployee(int employeeId, String password)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
