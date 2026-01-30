package dao;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplFile implements Dao
{

	@Override
	public ArrayList<Product> getInventory()
	{
		ArrayList<Product> list = new ArrayList<>();

		File f = new File(System.getProperty("user.dir") + File.separator + "files" + File.separator + "inputInventory.txt");

		try (BufferedReader br = new BufferedReader(new FileReader(f)))
		{
			String line;

			while ((line = br.readLine()) != null)
			{
				String[] sections = line.split(";");
				String name = "";
				double wholesalerPrice = 0.0;
				int stock = 0;

				for (String section : sections)
				{
					if (section.contains(":"))
					{
						String[] data = section.split(":");
						String key = data[0].trim();
						String value = data[1].trim();

						switch (key)
						{
						case "Product":
							name = value;
							break;
						case "Wholesaler Price":
							wholesalerPrice = Double.parseDouble(value);
							break;
						case "Stock":
							stock = Integer.parseInt(value);
							break;
						}
					}
				}

				list.add(new Product(name, new Amount(wholesalerPrice), true, stock));
			}

		} 
		catch (FileNotFoundException e)
		{
			System.err.println("⚠ Archivo de inventario no encontrado: " + f.getAbsolutePath());
		} 
		catch (IOException e)
		{
			System.err.println("⚠ Error al leer el archivo de inventario.");
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory)
	{
		LocalDate today = LocalDate.now();
		String fileName = "inventory_" + today.toString() + ".txt";
		File f = new File(System.getProperty("user.dir") + File.separator + "files" + File.separator + fileName);

		try (PrintWriter pw = new PrintWriter(new FileWriter(f, false)))
		{
			int counter = 1;
			for (Product product : inventory)
			{
				pw.println(counter + ";Product:" + product.getName() + ";Stock:" + product.getStock() + ";");
				counter++;
			}
			pw.println("Total number of products:" + inventory.size() + ";");
			return true;
		} catch (IOException e)
		{
			System.err.println("⚠ Error al exportar inventario: " + e.getMessage());
			return false;
		}
	}

	//All methods below are implemented because it is required, but not used
	@Override
	public void connect()
	{
	}

	@Override
	public void disconnect()
	{
	}

	@Override
	public Employee getEmployee(int employeeId, String password)
	{
		return null;
	}

	@Override
	public void addProduct(Product product)
	{
	}

	@Override
	public void updateProduct(Product product)
	{
	}

	@Override
	public void deleteProduct(int productId)
	{
	}
}
