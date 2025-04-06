package Atm.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class mini extends JFrame implements ActionListener {
    String pin;
    JButton button;
    mini(String pin){
        this.pin = pin;
        getContentPane().setBackground(new Color(255,204,204));
        setSize(400,600);
        setLocation(20,20);
        setLayout(null);

        JLabel label1 = new JLabel();
        label1.setBounds(20,140,400,200);
        add(label1);

        JLabel label2 = new JLabel("Statement");
        label2.setFont(new Font("System", Font.BOLD,15));
        label2.setBounds(150,20,200,20);
        add(label2);

        JLabel label3 = new JLabel();
        label3.setBounds(20,80,300,20);
        add(label3);

        JLabel label4 = new JLabel();
        label4.setBounds(20,400,300,20);
        add(label4);

        try (Connection conn = Connn.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM login WHERE pin = ?")) {
            ps.setString(1, pin);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String cardNo = resultSet.getString("cardno");
                label3.setText("Card Number:  " + cardNo.substring(0,4) + "XXXXXXXX" + cardNo.substring(12));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Connection conn = Connn.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM bank WHERE pin = ?");
            ps.setString(1, pin);
            ResultSet resultSet = ps.executeQuery();
            StringBuilder transactions = new StringBuilder("<html>");
            int transactionCount = 0;
            while (resultSet.next()) {
                transactionCount++;
                transactions.append(resultSet.getString("date"))
                           .append(" - ")
                           .append(resultSet.getString("type"))
                           .append(" - Rs")
                           .append(resultSet.getBigDecimal("amount"))
                           .append("<br>");
            }
            transactions.append("</html>");
            label1.setText(transactions.toString());
            if (transactionCount == 0) {
                label1.setText("No transactions found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        button = new JButton("Exit");
        button.setBounds(20,500,100,25);
        button.addActionListener(this);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        add(button);

        // Add scroll pane for transactions
        JScrollPane scrollPane = new JScrollPane(label1);
        scrollPane.setBounds(20, 140, 350, 250);
        add(scrollPane);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }

    public static void main(String[] args) {
        new mini("");
    }
}
