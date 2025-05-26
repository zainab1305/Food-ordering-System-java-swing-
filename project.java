import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Font;
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
    public static void main(String args[])
    {
        JFrame frame= new JFrame("Food Ordering System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Welcome to Zasha Food Corner", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JLabel infoLabel = new JLabel("Hold ctrl (cmd or mac) to select multiple items", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(infoLabel);
        frame.add(topPanel, BorderLayout.NORTH);
        DefaultListModel<foodItem> listModel=new DefaultListModel<>();
        JList<foodItem> menuList=new JList<>(listModel);
        menuList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        menuList.setVisibleRowCount(10);
        JScrollPane scrollPane = new JScrollPane(menuList);
        // scrollPane.setBounds(20,20,340,250);
        frame.add(scrollPane, BorderLayout.CENTER);
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
        JButton orderButton=new JButton("Place order");
        // orderButton.setBounds(130,290,120,30);
        frame.add(orderButton, BorderLayout.SOUTH);
        orderButton.addActionListener(e ->{
            List<foodItem> selectedItems=menuList.getSelectedValuesList();
            if(selectedItems.isEmpty()){
                JOptionPane.showMessageDialog(frame, "No items selected");
                return;
            }
            String customername=JOptionPane.showInputDialog(frame, "Enter your name: ");
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
            JOptionPane.showMessageDialog(frame, orderSummary.toString());
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
        frame.setVisible(true);
    }
}