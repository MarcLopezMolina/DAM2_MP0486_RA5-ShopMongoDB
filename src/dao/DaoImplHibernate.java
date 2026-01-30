package dao;

import model.Product;
import model.ProductHistory;
import model.Employee;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class DaoImplHibernate implements Dao
{
    private SessionFactory sessionFactory;

    @Override
    public void connect()
    {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
    }

    @Override
    public void disconnect()
    {
        if (sessionFactory != null)
        {
            sessionFactory.close();
        }
    }

    @Override
    public ArrayList<Product> getInventory()
    {
        Session session = sessionFactory.openSession();
        List<Product> list = session.createQuery("from Product", Product.class).list();
        session.close();
        return new ArrayList<>(list);
    }

    @Override
    public boolean writeInventory(ArrayList<Product> inventory)
    {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try
        {
            for (Product p : inventory)
            {
                session.save(new ProductHistory(p));
            }
            tx.commit();
            session.close();
            return true;
        }
        catch (Exception e)
        {
            tx.rollback();
            session.close();
            return false;
        }
    }

    @Override
    public void addProduct(Product product)
    {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(product);
        tx.commit();
        session.close();
    }

    @Override
    public void updateProduct(Product product)
    {
        addProduct(product);
    }

    @Override
    public void deleteProduct(int productId)
    {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Product p = session.get(Product.class, productId);
        if (p != null)
        {
            session.delete(p);
        }
        tx.commit();
        session.close();
    }

    @Override
    public Employee getEmployee(int employeeId, String password)
    {
        return null; // fuera del alcance
    }
}
