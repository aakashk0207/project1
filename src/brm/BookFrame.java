package brm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class BookFrame {

    Connection con;
    PreparedStatement ps;
    JFrame frame = new JFrame("BRM Project");
    JTabbedPane tabbedPane= new JTabbedPane();
    JPanel insertPanel, viewPanel;
    JLabel l1,l2,l3,l4,l5;
    JTextField t1,t2,t3,t4,t5;
    JButton saveButton, updateButton, deleteButton;
    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel tm;
    String[]colNames={"Book Id","Title","Price","Author","Publisher"};



    public BookFrame(){
        getConnectionFromMySQL();
        initComponents();
    }

    void getConnectionFromMySQL(){
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "Aakash@7024");
            System.out.println("Connection Esablishe");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }


    }
    void initComponents(){
        tm = new DefaultTableModel(colNames, 0); // Initialize the table model
        table = new JTable(tm); // Attach the model to the table

        // Components for insert form
        l1 = new JLabel("Book ID:");
        l2 = new JLabel("Title:");
        l3 = new JLabel("Price:");
        l4 = new JLabel("Author:");
        l5 = new JLabel("Publisher:");

        // Initialize text fields separately
        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        t4 = new JTextField();
        t5 = new JTextField();

        saveButton = new JButton("Save");

        // Set bounds for labels
        l1.setBounds(100, 100, 100, 20);
        l2.setBounds(100, 150, 100, 20);
        l3.setBounds(100, 200, 100, 20);
        l4.setBounds(100, 250, 100, 20);
        l5.setBounds(100, 300, 100, 20);

        // Set bounds for text fields
        t1.setBounds(250, 100, 100, 20);
        t2.setBounds(250, 150, 100, 20);
        t3.setBounds(250, 200, 100, 20);
        t4.setBounds(250, 250, 100, 20);
        t5.setBounds(250, 300, 100, 20);

        saveButton.setBounds(100, 350, 100, 30);
        saveButton.addActionListener(new InsertBookRecord());


        // Insert panel setup
        insertPanel = new JPanel();
        insertPanel.setLayout(null);
        insertPanel.add(l1);
        insertPanel.add(l2);
        insertPanel.add(l3);
        insertPanel.add(l4);
        insertPanel.add(l5);
        insertPanel.add(t1);
        insertPanel.add(t2);
        insertPanel.add(t3);
        insertPanel.add(t4);
        insertPanel.add(t5);
        insertPanel.add(saveButton);
        ArrayList<Book> bookList = fetchBookRecord();
        updateTable(bookList);
        updateButton=new JButton("Update Book");
        updateButton.addActionListener(new updateBookRecord());
        deleteButton=new JButton("Delete Book");
        deleteButton.addActionListener(new DeleteBookRecord());
        viewPanel=new JPanel();
        viewPanel.add(updateButton);
        viewPanel.add(deleteButton);
        scrollPane=new JScrollPane(table);
        viewPanel.add(scrollPane);

        tabbedPane.add(insertPanel);
        tabbedPane.add(viewPanel);
        tabbedPane.addChangeListener(new TabchangeHandler());



        frame.add(tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    void updateTable(ArrayList <Book> booklist)
    {
        Object[][]obj=new Object[booklist.size()][5];
        for(int i=0;i<booklist.size();i++){
            obj[i][0]=booklist.get(i).getBookId();
            obj[i][1]=booklist.get(i).getTitle();
            obj[i][2]=booklist.get(i).getPrice();
            obj[i][3]=booklist.get(i).getAuthor();
            obj[i][4]=booklist.get(i).getPublisher();
        }
       // table  = new JTable();
       // tm=new DefaultTableModel();
       // tm.setColumnCount(5);
        tm.setRowCount(0); // Clear previous rows
        for (Book book : booklist) {
            tm.addRow(new Object[]{
                    book.getBookId(),
                    book.getTitle(),
                    book.getPrice(),
                    book.getAuthor(),
                    book.getPublisher()
            });
        }

        //   tm.setColumnIdentifiers(colNames);
        for(int i=0;i<booklist.size();i++)
        {
            tm.setValueAt(obj[i][0],i,0);
            tm.setValueAt(obj[i][1],i,1);
            tm.setValueAt(obj[i][2],i,2);
            tm.setValueAt(obj[i][3],i,3);
            tm.setValueAt(obj[i][4],i,4);

        }
        table.setModel(tm);

    }

    ArrayList<Book> fetchBookRecord(){
        ArrayList <Book> booklist = new ArrayList <Book>();
        String q ="select* from book";
        try{
            ps = con.prepareStatement(q);
           ResultSet rs= ps.executeQuery();
           while(rs.next()){
               Book b = new Book();
               b.setBookId((rs.getInt(1)));
               b.setTitle((rs.getString(2)));
               b.setPrice(rs.getDouble(3));
               b.setAuthor((rs.getString(4)));
               b.setPublisher(rs.getString(5));
               booklist.add(b);
           }
        }catch (SQLException ex)
        {
            System.out.println("Exception: "+ex.getMessage());
        }
        finally {
            return booklist;
        }
    }
    class InsertBookRecord  implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
         Book b1 = readFromData();
         String q = "insert into Book (bookid, title, price, author, publisher) values(?,?,?,?,?)";

         try{
             ps = con.prepareStatement(q);
             ps.setInt(1,b1.getBookId());
             ps.setString(2,b1.getTitle());
             ps.setDouble(3,b1.getPrice());
             ps.setString(4,b1.getAuthor());
             ps.setString(5,b1.getPublisher());
             ps.execute();
             t1.setText("");
             t2.setText("");
             t3.setText("");
             t4.setText("");
             t5.setText("");
         }
         catch (SQLException ex){
             System.out.println("Exception: "+ex.getMessage());
         }



        }
        Book  readFromData(){
            Book b1 = new Book();
            b1.setBookId(Integer.parseInt(t1.getText()));
            b1.setTitle(t2.getText());
            b1.setPrice(Double.parseDouble(t3.getText()));
            b1.setAuthor(t4.getText());
            b1.setPublisher(t5.getText());
            return b1;
        }
    }
    class TabchangeHandler implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent e) {
            int index=tabbedPane.getSelectedIndex();
            if(index==0){
                System.out.println("Insert");
            }if(index==1){
                ArrayList<Book>bookList=fetchBookRecord();
                updateTable(bookList);

            }
        }
    }
  /*  class updateBookRecord implements  ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Book>bookList=readTableData();

            String q = "UPDATE book SET title = ?, price = ?, author = ?, publisher = ? WHERE bookid = ?";


            try{
                ps=con.prepareStatement(q);
                for(int i=0;i<bookList.size();i++){
                    ps.setString(1,bookList.get(i).getTitle());
                    ps.setDouble(2,bookList.get(i).getPrice());
                    ps.setString(3,bookList.get(i).getAuthor());
                    ps.setString(4,bookList.get(i).getPublisher());
                    ps.setInt(5,bookList.get(i).getBookId());
                    ps.executeUpdate();
                }
            }
            catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
        ArrayList<Book>readTableData() {
            ArrayList<Book> updateBookList = new ArrayList<Book>();
            for(int i=0;i<table.getRowCount();i++){
                Book b =new Book();
                b.setBookId(Integer.parseInt(table.getValueAt(i,0).toString()));
                b.setTitle(table.getValueAt(i,1).toString());
                b.setPrice(Double.parseDouble(table.getValueAt(i,2).toString()));
                b.setAuthor(table.getValueAt(i,3).toString());
                b.setPublisher(table.getValueAt(i,4).toString());
                updateBookList.add(b);
            }
            return updateBookList;
        }

    }*/
  class updateBookRecord implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
          ArrayList<Book> bookList = readTableData();
          String q = "UPDATE book SET title = ?, price = ?, author = ?, publisher = ? WHERE bookid = ?";

          try {
              ps = con.prepareStatement(q);
              for (Book book : bookList) {
                  ps.setString(1, book.getTitle());
                  ps.setDouble(2, book.getPrice());
                  ps.setString(3, book.getAuthor());
                  ps.setString(4, book.getPublisher());
                  ps.setInt(5, book.getBookId());
                  ps.executeUpdate(); // FIXED: Use executeUpdate() instead of execute()
              }
              System.out.println("Books updated successfully.");
              updateTable(fetchBookRecord()); // Refresh table after update
          } catch (SQLException ex) {
              System.out.println("SQL Exception: " + ex.getMessage());
          }
      }

      ArrayList<Book> readTableData() {
          ArrayList<Book> updateBookList = new ArrayList<>();
          for (int i = 0; i < table.getRowCount(); i++) {
              Book b = new Book();
              b.setBookId(Integer.parseInt(table.getValueAt(i, 0).toString()));
              b.setTitle(table.getValueAt(i, 1).toString());
              b.setPrice(Double.parseDouble(table.getValueAt(i, 2).toString()));
              b.setAuthor(table.getValueAt(i, 3).toString());
              b.setPublisher(table.getValueAt(i, 4).toString());
              updateBookList.add(b);
          }
          return updateBookList;
      }
  }



    class DeleteBookRecord implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int rowNo = table.getSelectedRow(); // Get selected row
            if (rowNo != -1) {
                int id = (int) table.getValueAt(rowNo, 0); // Get book ID from selected row
                String q = "DELETE FROM book WHERE bookid=?"; // SQL query
                try {
                    ps = con.prepareStatement(q);
                    ps.setInt(1, id);
                    int rowsAffected = ps.executeUpdate(); // Execute delete query
                    if (rowsAffected > 0) {
                        System.out.println("Book with ID " + id + " deleted successfully.");
                    } else {
                        System.out.println("No book found with ID " + id);
                    }
                } catch (SQLException ex) {
                    System.out.println("SQL Exception: " + ex.getMessage());
                } finally {
                    updateTable(fetchBookRecord()); // Refresh table after deletion
                }
            } else {
                System.out.println("Please select a row to delete.");
            }
        }
    }
    }

