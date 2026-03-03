import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.*;
import java.util.*;
import javax.swing.*;
class foodItem
{
    int id;
    String name;
    int price;
    foodItem(int id,String name,int price)
    {
        this.id=id;
        this.name=name;
        this.price=price;
    }
    @Override
    public String toString()
    {
        return name+"-Rs."+price;
    }
}
public class project {
    private static JLabel statusLabel;

    public static void main(String args[])
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JFrame frame= new JFrame("Food Ordering System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(560,520);
        frame.setMinimumSize(new Dimension(520, 460));
        frame.setLayout(new BorderLayout(12, 12));

        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Welcome to Zasha Food Corner");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel("Select items from the menu and place your order");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(70, 70, 70));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hintLabel = new JLabel("Tip: Hold Ctrl (Cmd on Mac) to select multiple items");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hintLabel.setForeground(new Color(100, 100, 100));
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(infoLabel);
        topPanel.add(Box.createVerticalStrut(2));
        topPanel.add(hintLabel);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        DefaultListModel<foodItem> listModel=new DefaultListModel<>();
        JList<foodItem> menuList=new JList<>(listModel);
        menuList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        menuList.setVisibleRowCount(12);
        menuList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuList.setFixedCellHeight(30);
        menuList.setSelectionBackground(new Color(33, 111, 219));
        menuList.setSelectionForeground(Color.WHITE);

        menuList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof foodItem) {
                    foodItem item = (foodItem) value;
                    label.setText(item.name + "  •  Rs. " + item.price);
                }
                if (!isSelected) {
                    label.setBackground(index % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(menuList);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        try{
            BufferedReader reader = new BufferedReader(new FileReader("menu.txt"));
            String line;
            while((line = reader.readLine())!=null)
            {
                String[] parts=line.split(",");
                if(parts.length==3)
                {
            int id= Integer.parseInt(parts[0]);
            String name=parts[1];
            int price=Integer.parseInt(parts[2]);
            listModel.addElement(new foodItem (id,name,price));
        }
    }
            reader.close();
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(frame,"Error loading menu:"+e.getMessage());
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("Selected: 0 items | Estimated total: Rs. 0");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(60, 60, 60));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton clearButton = new JButton("Clear Selection");
        JButton orderButton=new JButton("Place Order");

        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orderButton.setFont(new Font("Segoe UI", Font.BOLD, 13));

        actionsPanel.add(clearButton);
        actionsPanel.add(orderButton);

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(actionsPanel, BorderLayout.EAST);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(contentPanel, BorderLayout.CENTER);

        menuList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateStatus(menuList.getSelectedValuesList());
            }
        });

        clearButton.addActionListener(e -> {
            menuList.clearSelection();
            updateStatus(Collections.emptyList());
        });

        orderButton.addActionListener(e ->{
            List<foodItem> selectedItems=menuList.getSelectedValuesList();
            if(selectedItems.isEmpty()){
                JOptionPane.showMessageDialog(frame, "No items selected");
                return;
            }
            String customername=JOptionPane.showInputDialog(frame, "Enter customer name: ");
            if(customername==null||customername.trim().isEmpty()){
                JOptionPane.showMessageDialog(frame, "Name is required to place order");
            }
            StringBuilder orderSummary = new StringBuilder("Zasha Food Corner:\n");
            orderSummary.append("Customer: ").append(customername).append("\n\nYou Ordered:\n");
            int total=0;
            for(foodItem item : selectedItems)
            {
                String input= JOptionPane.showInputDialog(null,"Enter quantity for "+item.name+":");
                if(input==null||input.trim().isEmpty())
                    continue;
                try{
                int quantity=Integer.parseInt(input.trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(frame, "Quantity must be greater than 0 for " + item.name);
                    continue;
                }
                int itemTotal=quantity*item.price;
                orderSummary.append(item.name).append("x").append(quantity).append("-Rs").append(itemTotal).append("\n");
                total+=itemTotal;
                }
                catch(NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(frame,"Invalid Quantity"+item.name);
                }
            }
            orderSummary.append("\nTotal: Rs.").append(total);

            JTextArea summaryArea = new JTextArea(orderSummary.toString());
            summaryArea.setEditable(false);
            summaryArea.setFont(new Font("Consolas", Font.PLAIN, 13));
            summaryArea.setBackground(new Color(250, 250, 250));
            summaryArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JScrollPane summaryScroll = new JScrollPane(summaryArea);
            summaryScroll.setPreferredSize(new Dimension(420, 240));
            JOptionPane.showMessageDialog(frame, summaryScroll, "Order Summary", JOptionPane.INFORMATION_MESSAGE);

            try{
                FileWriter writer=new FileWriter("bill.txt",true);
                writer.write("\n================================\n");
                writer.write(orderSummary.toString());
                writer.write("\n================================\n");
                writer.close();
                JOptionPane.showMessageDialog(frame, "Bill saved");
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(frame, "Error saving bill: "+ ex.getMessage());
            }
        });       
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void updateStatus(List<foodItem> selectedItems) {
        int estimated = 0;
        for (foodItem item : selectedItems) {
            estimated += item.price;
        }
        statusLabel.setText("Selected: " + selectedItems.size() + " items | Estimated total: Rs. " + estimated);
    }
}