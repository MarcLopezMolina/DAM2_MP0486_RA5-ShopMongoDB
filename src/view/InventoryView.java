package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import main.Shop;
import model.Product;

public class InventoryView extends JDialog
{

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTable tableInventory;

	public InventoryView(Shop shop)
	{
		setTitle("Inventario de la Tienda");
		setBounds(100, 100, 800, 400);
		setModal(true);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(10, 10));

		JLabel lblTitle = new JLabel("Inventario de Productos");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblTitle, BorderLayout.NORTH);

		String[] columnNames =
		{ "ID", "Nombre", "Precio Público (€)", "Precio Mayorista (€)", "Disponible", "Stock" };

		DefaultTableModel model = new DefaultTableModel(columnNames, 0)
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		tableInventory = new JTable(model);
		tableInventory.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tableInventory.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		tableInventory.setRowHeight(25);

		ArrayList<Product> products = shop.getInventory();

		for (Product p : products)
		{
			Object[] row =
			{ p.getId(), p.getName(), String.format("%.2f", p.getPublicPrice().getValue()),
					String.format("%.2f", p.getWholesalerPrice().getValue()), p.isAvailable() ? "Sí" : "No",
					p.getStock() };
			model.addRow(row);
		}

		JScrollPane scrollPane = new JScrollPane(tableInventory);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		JLabel lblTotal = new JLabel("Total de productos: " + products.size());
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblTotal, BorderLayout.SOUTH);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton btnBack = new JButton("Volver atrás");
		btnBack.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnBack.addActionListener(e -> dispose());
		buttonPane.add(btnBack);
	}
}
