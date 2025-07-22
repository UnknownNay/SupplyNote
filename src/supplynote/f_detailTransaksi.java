/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package supplynote;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;
/**
 *
 * @author aido
 */
public class f_detailTransaksi extends javax.swing.JFrame {

    /**
     * Creates new form f_utama
     */
    
    koneksi dbsetting;
    String driver,database, user, pass;
    private String kodeSup;
    
    public f_detailTransaksi() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        tabel_transaksi.setModel(tableModel);
        comboxSupplier();
        comboxTransaksi();
        
        
        //Hide column 1
        tabel_transaksi.getColumnModel().getColumn(0).setMinWidth(0);
        tabel_transaksi.getColumnModel().getColumn(0).setMaxWidth(0);
        //Hide column 2
        tabel_transaksi.getColumnModel().getColumn(1).setMinWidth(0);
        tabel_transaksi.getColumnModel().getColumn(1).setMaxWidth(0);
        //Hide column 3
        tabel_transaksi.getColumnModel().getColumn(2).setMinWidth(0);
        tabel_transaksi.getColumnModel().getColumn(2).setMaxWidth(0);
        //Hide column 4
        tabel_transaksi.getColumnModel().getColumn(3).setMinWidth(0);
        tabel_transaksi.getColumnModel().getColumn(3).setMaxWidth(0);
        
        txt_nominal.setEditable(false);
        txt_total.setEditable(false);
        
        txt_kuantitas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotal();
            }
        });  
    }
    
    private javax.swing.table.DefaultTableModel tableModel=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {"ID Transaksi","ID Detail Transaksi","Kode Supplier", 
                    "Kode Barang", "Nama Supplier", "Nama Barang", "Jenis Barang", 
                    "Kuantitas", "Total Harga"}
        ){
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return canEdit[columnIndex];
            }
        };
    }
    
    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY); // Use Locale.GERMANY for dot as thousand separator
        return formatter.format(amount);
    }
    
    //---------------------------------COMBOX-----------------------------------
    private void comboxTransaksi() {
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();

            // Query to get the transaction names
            String SQL = "SELECT nama_transaksi FROM t_transaksi";
            ResultSet res = stt.executeQuery(SQL);

            // Clear existing items in the combo box
            combox_transaksi.removeAllItems();

            // Populate the combo box with transaction names
            while (res.next()) {
                String namaTransaksi = res.getString("nama_transaksi");
                combox_transaksi.addItem(namaTransaksi);
            }

            // Close the result set, statement, and connection
            res.close();
            stt.close();
            kon.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            System.err.println(e.getMessage());
        }
    }
    
    private void comboxSupplier(){
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass); // Koneksi ke DB
            Statement stt = kon.createStatement();
            String SQL = "SELECT nama_supplier FROM t_supplier"; // Query ambil data
            ResultSet res = stt.executeQuery(SQL);
            
            while (res.next()) {
                String namaSup = res.getString("nama_supplier");
                combox_supplier.addItem(namaSup);
            }
            
            // Tutup koneksi
            res.close();
            stt.close();
            kon.close();
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                e.getMessage(), "Error",
                JOptionPane.INFORMATION_MESSAGE);
        System.err.println(e.getMessage());
        }
    }
    
    private void comboxBarang(String kodeSupplier) {
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            String SQL = "SELECT nama_barang, harga FROM t_barang WHERE kode_supplier = ?";
            PreparedStatement pst = kon.prepareStatement(SQL);
            pst.setString(1, kodeSupplier);

            ResultSet res = pst.executeQuery();

            // Clear existing items in the barang combo box
            combox_barang.removeAllItems();

            // Populate the barang combo box with items
            while (res.next()) {
                String namaBarang = res.getString("nama_barang");
                combox_barang.addItem(namaBarang);
            }

            res.close();
            pst.close();
            kon.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            System.err.println(e.getMessage());
        }
    }
    
    //--------------------------------------------------------------------------
    
    private String setkodeSup(String namaSup){
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            //ambil kode_supplier berdasarkan nama_supplier
            String sqlCariKode = "SELECT kode_supplier FROM t_supplier WHERE nama_supplier = ?";

            PreparedStatement pst = kon.prepareStatement(sqlCariKode);
            pst.setString(1, namaSup);

            ResultSet resKode = pst.executeQuery();

            String kodeSupplier = "";

            if(resKode.next()){
                kodeSupplier = resKode.getString("kode_supplier");
            }   
           
            return kodeSupplier;

        }catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), "Error", 
                    JOptionPane.INFORMATION_MESSAGE);
            return "";
        }      
    }
    
    int row = 0;
    private void tampil_field(){
        row = tabel_transaksi.getSelectedRow();
        
        combox_supplier.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 4)));
        combox_barang.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
        txt_kuantitas.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        txt_total.setText(String.valueOf(tableModel.getValueAt(row, 8)));
    }
    
    public void membersihkan_teks(){
  
    }
    
    String data []=new String[9];
    private void settableload(){
        String selectedTransaction = (String) combox_transaksi.getSelectedItem();

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            // Query to get the ID of the selected transaction
            String cariIdTrans = "SELECT id_transaksi FROM t_transaksi WHERE nama_transaksi = ?";
            PreparedStatement pst = kon.prepareStatement(cariIdTrans);
            pst.setString(1, selectedTransaction);

            ResultSet res = pst.executeQuery();
            String idTransaksi = "";

            if (res.next()) {
                idTransaksi = res.getString("id_transaksi");
            }
            
            // If the transaction ID is found, query the detail transaction table
            if (!idTransaksi.isEmpty()) {
                String SQL = "SELECT "
                    + "t_detail_transaksi.id_transaksi, "
                    + "t_detail_transaksi.id_detail_transaksi, "
                    + "t_detail_transaksi.kode_supplier, "
                    + "t_barang.kode_barang, "
                    + "t_supplier.nama_supplier, "
                    + "t_barang.nama_barang, "
                    + "t_barang.jenis_barang, "
                    + "t_detail_transaksi.quantity, "
                    + "t_detail_transaksi.subtotal "
                    + "FROM "
                    + "t_detail_transaksi "
                    + "INNER JOIN t_barang ON t_detail_transaksi.kode_barang = t_barang.kode_barang "
                    + "INNER JOIN t_supplier ON t_barang.kode_supplier = t_supplier.kode_supplier "
                    + "WHERE "
                    + "t_detail_transaksi.id_transaksi = ?";
                
                pst = kon.prepareStatement(SQL);
                pst.setString(1, idTransaksi);
                ResultSet resDetail = pst.executeQuery();

                // Clear the existing rows in the table model
                tableModel.setRowCount(0);

                // Populate the table with the details of the selected transaction
                while (resDetail.next()) {
                    data[0] = resDetail.getString("id_transaksi");
                    data[1] = resDetail.getString("id_detail_transaksi");
                    data[2] = resDetail.getString("kode_supplier");
                    data[3] = resDetail.getString("kode_barang");
                    data[4] = resDetail.getString("nama_supplier");
                    data[5] = resDetail.getString("nama_barang");
                    data[6] = resDetail.getString("jenis_barang");
                    data[7] = resDetail.getString("quantity");
                    data[8] = resDetail.getString("subtotal");
                    tableModel.addRow(data);
                }
            resDetail.close();
        }
        // Close the prepared statement and connection
        pst.close();
        kon.close();
            
        }catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), "Error", 
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    
    
    
    // Method to calculate total price
    private void calculateTotal() {
        try {
            int quantity = Integer.parseInt(txt_kuantitas.getText());

            // Ensure quantity is non-negative
            if (quantity < 0) {
                quantity = 0; // Set quantity to 0 if it's negative
                txt_kuantitas.setText("0"); // Update the text field to show 0
            }

            int price = Integer.parseInt(txt_nominal.getText().replace(".", "").replace(",", "")); // Remove formatting for calculation
            int total = quantity * price;

            // Format the total and set it in txt_total
            txt_total.setText(formatCurrency(total));
        } catch (NumberFormatException e) {
            txt_total.setText("0"); // Reset total if input is invalid
        }
    }
    
    private void addDetailTransaksi() {
        try {
            // Generate UUID
            String idDetailTransaksi = UUID.randomUUID().toString();

            // Ngambil data dari field
            String selectedTransactionName = (String) combox_transaksi.getSelectedItem();
            String selectedBarangName = (String) combox_barang.getSelectedItem();
            int quantity = Integer.parseInt(txt_kuantitas.getText());

            // Remove formatting from txt_nominal before parsing
            int hargaSatuan = Integer.parseInt(txt_nominal.getText().replace(".", "").replace(",", ""));

            //Get id_transaksi from selectedTransactionName
            String idTransaksi = "";
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            String sqlGetIdTransaksi = "SELECT id_transaksi FROM t_transaksi WHERE nama_transaksi = ?";
            PreparedStatement pstGetIdTrans = kon.prepareStatement(sqlGetIdTransaksi);
            pstGetIdTrans.setString(1, selectedTransactionName);
            ResultSet resIdTrans = pstGetIdTrans.executeQuery();
            if (resIdTrans.next()) {
                idTransaksi = resIdTrans.getString("id_transaksi");
            } else {
                JOptionPane.showMessageDialog(null, "Transaksi tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                resIdTrans.close();
                pstGetIdTrans.close();
                kon.close();
                return;
            }
            resIdTrans.close();
            pstGetIdTrans.close();

            // 4. Get kode_barang and kode_supplier from selectedBarangName and current kodeSup
            String kodeBarang = "";
            String kodeSupplierFromBarang = ""; // This should ideally be the same as kodeSup
            String sqlGetBarangDetails = "SELECT kode_barang, kode_supplier FROM t_barang WHERE nama_barang = ? AND kode_supplier = ?";
            PreparedStatement pstGetBarangDetails = kon.prepareStatement(sqlGetBarangDetails);
            pstGetBarangDetails.setString(1, selectedBarangName);
            pstGetBarangDetails.setString(2, kodeSup); // Use the currently selected supplier's code
            ResultSet resBarangDetails = pstGetBarangDetails.executeQuery();
            if (resBarangDetails.next()) {
                kodeBarang = resBarangDetails.getString("kode_barang");
                kodeSupplierFromBarang = resBarangDetails.getString("kode_supplier");
            } else {
                JOptionPane.showMessageDialog(null, "Barang tidak ditemukan untuk supplier ini.", "Error", JOptionPane.ERROR_MESSAGE);
                resBarangDetails.close();
                pstGetBarangDetails.close();
                kon.close();
                return;
            }
            resBarangDetails.close();
            pstGetBarangDetails.close();

            // 5. Calculate subtotal
            int subtotal = quantity * hargaSatuan;

            // 6. Insert data into t_detail_transaksi
            String SQL = "INSERT INTO t_detail_transaksi (id_detail_transaksi, id_transaksi, kode_barang, kode_supplier, quantity, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = kon.prepareStatement(SQL);
            pst.setString(1, idDetailTransaksi);
            pst.setString(2, idTransaksi);
            pst.setString(3, kodeBarang);
            pst.setString(4, kodeSupplierFromBarang); // Use the kode_supplier retrieved from t_barang
            pst.setInt(5, quantity);
            pst.setInt(6, hargaSatuan);
            pst.setInt(7, subtotal);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Detail transaksi berhasil ditambahkan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                // Optionally, refresh the table or clear input fields
                 settableload(); // If you have a method to refresh the main table
                // membersihkan_teks(); // If you have a method to clear input fields
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menambahkan detail transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();
            kon.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Kuantitas atau Nominal harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan saat menambahkan detail transaksi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        }
    }
    
    private String getSelectedDetailTransaksiId() {
        // Assuming you have a JTable named detailTransaksiTable
        int selectedRow = tabel_transaksi.getSelectedRow(); // Get the selected row index
        if (selectedRow != -1) { // Memeriksa apakah baris dipilih
            //Kolom kedua (index 1) berisi id_transaksi_detail
            return tabel_transaksi.getValueAt(selectedRow, 1).toString(); 
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih detail transaksi yang ingin diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
    
    private void updateDetailTransaksi(String idDetailTransaksi) {
        try {
            // 1. Retrieve necessary data from form components
            String selectedBarangName = (String) combox_barang.getSelectedItem();
            int quantity = Integer.parseInt(txt_kuantitas.getText());

            // Remove formatting from txt_nominal before parsing
            int hargaSatuan = Integer.parseInt(txt_nominal.getText().replace(".", "").replace(",", ""));

            // 2. Calculate new subtotal
            int subtotal = quantity * hargaSatuan;

            // 3. Update data in t_detail_transaksi
            String SQL = "UPDATE t_detail_transaksi SET quantity = ?, harga_satuan = ?, subtotal = ? WHERE id_detail_transaksi = ?";
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            PreparedStatement pst = kon.prepareStatement(SQL);
            pst.setInt(1, quantity);
            pst.setInt(2, hargaSatuan);
            pst.setInt(3, subtotal);
            pst.setString(4, idDetailTransaksi); // Use the ID of the detail transaction to update

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Detail transaksi berhasil diubah.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                // Optionally, refresh the table or clear input fields
                 settableload(); // If you have a method to refresh the main table
                // membersihkan_teks(); // If you have a method to clear input fields
            } else {
                JOptionPane.showMessageDialog(null, "Gagal mengubah detail transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            pst.close();
            kon.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Kuantitas atau Nominal harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan saat mengubah detail transaksi: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        }
    }



    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btn_barang = new javax.swing.JButton();
        btn_keuangan = new javax.swing.JButton();
        btn_supplier = new javax.swing.JButton();
        btn_keuangan1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_ubah = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_transaksi = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        combox_supplier = new javax.swing.JComboBox<>();
        combox_transaksi = new javax.swing.JComboBox<>();
        txt_kuantitas = new javax.swing.JTextField();
        txt_total = new javax.swing.JTextField();
        txt_nominal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        combox_barang = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));

        btn_barang.setBackground(new java.awt.Color(51, 51, 51));
        btn_barang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_barang.setForeground(new java.awt.Color(255, 255, 255));
        btn_barang.setText("Barang");
        btn_barang.setToolTipText("");
        btn_barang.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btn_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_barangActionPerformed(evt);
            }
        });

        btn_keuangan.setBackground(new java.awt.Color(51, 51, 51));
        btn_keuangan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_keuangan.setForeground(new java.awt.Color(255, 255, 255));
        btn_keuangan.setText("Transaksi");
        btn_keuangan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btn_keuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_keuanganActionPerformed(evt);
            }
        });

        btn_supplier.setBackground(new java.awt.Color(51, 51, 51));
        btn_supplier.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_supplier.setForeground(new java.awt.Color(255, 255, 255));
        btn_supplier.setText("Supplier");
        btn_supplier.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btn_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_supplierActionPerformed(evt);
            }
        });

        btn_keuangan1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_keuangan1.setText("Detail Transaksi");
        btn_keuangan1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_barang, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(btn_keuangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(btn_keuangan1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btn_supplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(173, 173, 173)
                .addComponent(btn_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_keuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_keuangan1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(126, 126, 126)
                    .addComponent(btn_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(637, Short.MAX_VALUE)))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setText("Detail Transaksi");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Nama Barang");

        btn_ubah.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_ubah.setText("Ubah");
        btn_ubah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ubahActionPerformed(evt);
            }
        });

        tabel_transaksi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tabel_transaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_transaksiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_transaksi);

        btn_tambah.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_hapus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Supplier");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Kuantitas");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Transaksi");

        combox_supplier.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combox_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_supplierActionPerformed(evt);
            }
        });

        combox_transaksi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combox_transaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_transaksiActionPerformed(evt);
            }
        });

        txt_kuantitas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_total.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_nominal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("Total");

        combox_barang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combox_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_barangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(combox_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(combox_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txt_nominal, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txt_total, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                        .addComponent(txt_kuantitas, javax.swing.GroupLayout.Alignment.LEADING))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(combox_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nominal, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combox_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kuantitas, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_tambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_ubah, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(102, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1478, 806));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tabel_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_transaksiMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 1){
            tampil_field();
        }
    }//GEN-LAST:event_tabel_transaksiMouseClicked

    private void btn_keuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_keuanganActionPerformed
        // TODO add your handling code here:
        f_transaksi transaksi = new f_transaksi();
        transaksi.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_btn_keuanganActionPerformed

    private void combox_transaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combox_transaksiActionPerformed
        // TODO add your handling code here:
        // Get the selected transaction name
        String selectedTransaction = (String) combox_transaksi.getSelectedItem();

        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            // Query to get the ID of the selected transaction
            String cariIdTrans = "SELECT id_transaksi FROM t_transaksi WHERE nama_transaksi = ?";
            PreparedStatement pst = kon.prepareStatement(cariIdTrans);
            pst.setString(1, selectedTransaction);

            ResultSet res = pst.executeQuery();
            String idTransaksi = "";

            if (res.next()) {
                idTransaksi = res.getString("id_transaksi");
            }
            
            // If the transaction ID is found, query the detail transaction table
            if (!idTransaksi.isEmpty()) {
                String SQL = "SELECT "
                                + "t_detail_transaksi.id_transaksi, "
                                + "t_detail_transaksi.id_detail_transaksi, "
                                + "t_detail_transaksi.kode_supplier, "
                                + "t_barang.kode_barang, "
                                + "t_supplier.nama_supplier, "
                                + "t_barang.nama_barang, "
                                + "t_barang.jenis_barang, "
                                + "t_detail_transaksi.quantity, "
                                + "t_detail_transaksi.subtotal "
                                + "FROM "
                                + "t_detail_transaksi "
                                + "INNER JOIN t_barang ON t_detail_transaksi.kode_barang = t_barang.kode_barang "
                                + "INNER JOIN t_supplier ON t_barang.kode_supplier = t_supplier.kode_supplier "
                                + "WHERE "
                                + "t_detail_transaksi.id_transaksi = ?;";
                pst = kon.prepareStatement(SQL);
                pst.setString(1, idTransaksi);
                ResultSet resDetail = pst.executeQuery();

                // Clear the existing rows in the table model
                tableModel.setRowCount(0);

                // Populate the table with the details of the selected transaction
                while (resDetail.next()) {
                    data[0] = resDetail.getString("id_transaksi");
                    data[1] = resDetail.getString("id_detail_transaksi");
                    data[2] = resDetail.getString("kode_supplier");
                    data[3] = resDetail.getString("kode_barang");
                    data[4] = resDetail.getString("nama_supplier");
                    data[5] = resDetail.getString("nama_barang");
                    data[6] = resDetail.getString("jenis_barang");
                    data[7] = resDetail.getString("quantity");
                    data[8] = resDetail.getString("subtotal");
                    tableModel.addRow(data);
                }
            resDetail.close();
        }
        // Close the prepared statement and connection
        pst.close();
        kon.close();
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, 
                    "Gagal mengubah data: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_combox_transaksiActionPerformed

    private void btn_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_supplierActionPerformed
        // TODO add your handling code here:
        f_supplier supplier = new f_supplier();
        supplier.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_btn_supplierActionPerformed

    private void btn_barangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_barangActionPerformed
        // TODO add your handling code here:
        f_barang barang = new f_barang();
        barang.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_btn_barangActionPerformed

    private void combox_barangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combox_barangActionPerformed
        String selectedItem = (String) combox_barang.getSelectedItem();
    
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            String SQL = "SELECT kode_barang, harga FROM t_barang WHERE nama_barang = ? AND kode_supplier = ?";
            PreparedStatement pst = kon.prepareStatement(SQL);
            pst.setString(1, selectedItem);
            pst.setString(2, kodeSup); // Use the current supplier's code

            ResultSet res = pst.executeQuery();

            if (res.next()) {
                int harga = res.getInt("harga");
                txt_nominal.setText(formatCurrency(harga)); // Format the price
            }

            res.close();
            pst.close();
            kon.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_combox_barangActionPerformed

    private void combox_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combox_supplierActionPerformed
        // TODO add your handling code here:
        String selectedSupplier = (String) combox_supplier.getSelectedItem();
        kodeSup = setkodeSup(selectedSupplier);
        comboxBarang(kodeSup); // Populate barang combo box based on selected supplier
    }//GEN-LAST:event_combox_supplierActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        addDetailTransaksi();
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:
        String idDetailTransaksi = getSelectedDetailTransaksiId(); // Implement this method to get the selected ID
        updateDetailTransaksi(idDetailTransaksi);
    }//GEN-LAST:event_btn_ubahActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        // TODO add your handling code here:
        int selectedRow = tabel_transaksi.getSelectedRow();
        String kodeTrans = tableModel.getValueAt(selectedRow, 1).toString();
        
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            String sql = "DELETE FROM t_detail_transaksi WHERE id_detail_transaksi= ?";
            PreparedStatement pst = kon.prepareStatement(sql);
            pst.setString(1, kodeTrans);
             
            pst.executeUpdate();
            if(!kodeSup.isEmpty()){
                settableload();
            }
            
            pst.close();
            kon.close();
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, 
                    "Gagal mengubah data: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(f_detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(f_detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(f_detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(f_detailTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new f_detailTransaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_barang;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_keuangan;
    private javax.swing.JButton btn_keuangan1;
    private javax.swing.JButton btn_supplier;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox<String> combox_barang;
    private javax.swing.JComboBox<String> combox_supplier;
    private javax.swing.JComboBox<String> combox_transaksi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel_transaksi;
    private javax.swing.JTextField txt_kuantitas;
    private javax.swing.JTextField txt_nominal;
    private javax.swing.JTextField txt_total;
    // End of variables declaration//GEN-END:variables
}
