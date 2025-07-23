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
    private String selectedIdTransaksi, selectedNameTransaksi = null;
    
    public f_detailTransaksi() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        tabel_transaksi.setModel(tableModel);
        
        try{
            Class.forName(driver);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            System.err.println(e.getMessage());
        }

        

        hideColumn();
        txt_nominal.setEditable(false);
        txt_total.setEditable(false);
        
        txt_kuantitas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotal();
            }
        });  
    }
    
    public f_detailTransaksi(String idTransaksi, String namaTransaksi) {
        initComponents();
        // Initialize dbsetting and database parameters here as well
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");

        this.selectedIdTransaksi = idTransaksi;
        this.selectedNameTransaksi = namaTransaksi;
        tabel_transaksi.setModel(tableModel); // Set table model
        hideColumn();
        
        comboxSupplier();
        
        txt_transaksi.setText(selectedNameTransaksi);
        txt_transaksi.setEditable(false);
        txt_nominal.setEditable(false);
        txt_total.setEditable(false);
        
        txt_kuantitas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotal();
            }
            
        }); 
        settableload();
    }
    
    private void hideColumn(){
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

    }
    
    private javax.swing.table.DefaultTableModel tableModel=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {"ID Transaksi","ID Detail Transaksi","Kode Supplier", 
                    "Kode Barang", "Nama Supplier", "Nama Barang", "Jenis Barang", 
                    "Kuantitas", "Total Harga"}
        ){
            boolean[] canEdit = new boolean[]{false, false, false, false, 
                false, false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return false;
            }
        };
    }
    
    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY); // Use Locale.GERMANY for dot as thousand separator
        return formatter.format(amount);
    }

    private int parseCurrency(String formattedAmount) throws java.text.ParseException {
        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        Number number = formatter.parse(formattedAmount);
        return number.intValue();
    }
    
    private void comboxBarang(String kodeSupplier) {
        try {
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
    
    private void comboxSupplier(){
        try{
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
    //--------------------------------------------------------------------------
    
    private String setkodeSup(String namaSup){
        try{
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
        
        combox_barang.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
        txt_kuantitas.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        txt_total.setText(String.valueOf(tableModel.getValueAt(row, 8)));
    }
    
    String data []=new String[9];
    private void settableload(){

        Connection kon = null;
        PreparedStatement pst = null;
        ResultSet res = null;
        ResultSet resDetail = null;

        try {
            kon = DriverManager.getConnection(database, user, pass);

            String idTransaksi = selectedIdTransaksi;

            if (!idTransaksi.isEmpty()) {
                String SQL = "SELECT "
                        + "t_detail_transaksi.id_transaksi, "
                        + "t_detail_transaksi.id_detail_transaksi, "
                        + "t_detail_transaksi.kode_supplier, "
                        + "t_detail_transaksi.kode_barang, " // Use kode_barang from detail, not t_barang (join later)
                        + "t_supplier.nama_supplier, "
                        + "t_barang.nama_barang, "
                        + "t_barang.jenis_barang, "
                        + "t_detail_transaksi.quantity, "
                        + "t_detail_transaksi.subtotal "
                        + "FROM "
                        + "t_detail_transaksi "
                        + "INNER JOIN t_barang ON t_detail_transaksi.kode_barang = t_barang.kode_barang "
                        + "INNER JOIN t_supplier ON t_detail_transaksi.kode_supplier = t_supplier.kode_supplier " // Join on detail's kode_supplier
                        + "WHERE "
                        + "t_detail_transaksi.id_transaksi = ?";

                pst = kon.prepareStatement(SQL); // Re-use pst, but better to create new one or null it
                pst.setString(1, idTransaksi);
                resDetail = pst.executeQuery();

                tableModel.setRowCount(0);

                while (resDetail.next()) {
                    data[0] = resDetail.getString("id_transaksi");
                    data[1] = resDetail.getString("id_detail_transaksi");
                    data[2] = resDetail.getString("kode_supplier");
                    data[3] = resDetail.getString("kode_barang");
                    data[4] = resDetail.getString("nama_supplier");
                    data[5] = resDetail.getString("nama_barang");
                    data[6] = resDetail.getString("jenis_barang");
                    data[7] = resDetail.getString("quantity");
                    // Format subtotal as currency for display
                    int subtotalValue = resDetail.getInt("subtotal");
                    data[8] = formatCurrency(subtotalValue);
                    tableModel.addRow(data);
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } finally {
            // Close resources
            try {
                if (res != null) res.close();
                if (resDetail != null) resDetail.close();
                if (pst != null) pst.close();
                if (kon != null) kon.close();
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources in settableload: " + closeEx.getMessage());
            }
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
    
    private void updateTotal(String idTransaksi, int amountChange){
        Connection kon = null;
        PreparedStatement pst = null;
        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);

            // Add or subtract amountChange from total_transaksi
            String SQL = "UPDATE t_transaksi SET total_transaksi = total_transaksi + ? WHERE id_transaksi=?";
            pst = kon.prepareStatement(SQL);

            pst.setInt(1, amountChange);    // This is the difference (new_subtotal - old_subtotal)
            pst.setString(2, idTransaksi);  // This is the id_transaksi

            pst.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("SQL Error in updateTotal: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Gagal memperbarui total transaksi: " + ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver database tidak ditemukan in updateTotal: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan untuk memperbarui total transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            System.err.println("Terjadi kesalahan tak terduga di updateTotal: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat memperbarui total transaksi: " + ex.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (pst != null) pst.close();
                if (kon != null) kon.close();
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources in updateTotal: " + closeEx.getMessage());
            }
        }
    }
    
    private void addDetailTransaksi() {
        try {
            // Generate UUID
            String idDetailTransaksi = UUID.randomUUID().toString();

            // Ngambil data dari field
            String selectedBarangName = (String) combox_barang.getSelectedItem();
            int quantity = Integer.parseInt(txt_kuantitas.getText());

            // Remove formatting from txt_nominal before parsing
            int hargaSatuan = Integer.parseInt(txt_nominal.getText().replace(".", "").replace(",", ""));

            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            // 4. Get kode_barang and kode_supplier from selectedBarangName and current kodeSup
            String kodeBarang = "";
            String kodeSupplierFromBarang = "";
            String sqlGetBarangDetails = "SELECT kode_barang, kode_supplier FROM t_barang WHERE nama_barang = ? AND kode_supplier = ?";
            PreparedStatement pstGetBarangDetails = kon.prepareStatement(sqlGetBarangDetails);
            pstGetBarangDetails.setString(1, selectedBarangName);
            // *** CHANGE THIS LINE ***
            pstGetBarangDetails.setString(2, kodeSup); // Use kodeSup here, which is the supplier's code
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
            pst.setString(2, selectedIdTransaksi);
            pst.setString(3, kodeBarang);
            pst.setString(4, kodeSupplierFromBarang); // Use the kode_supplier retrieved from t_barang
            pst.setInt(5, quantity);
            pst.setInt(6, hargaSatuan);
            pst.setInt(7, subtotal);

            updateTotal(selectedIdTransaksi, subtotal);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Detail transaksi berhasil ditambahkan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                settableload(); // Refresh the main table
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
        Connection kon = null;
        PreparedStatement pstDetailSelect = null;
        PreparedStatement pstDetailUpdate = null;
        ResultSet rsOldDetail = null;

        try {
            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);
            kon.setAutoCommit(false); // Start transaction for atomicity

            // 1. Get old subtotal and id_transaksi before updating
            String sqlSelectOldDetail = "SELECT id_transaksi, subtotal FROM t_detail_transaksi WHERE id_detail_transaksi = ?";
            pstDetailSelect = kon.prepareStatement(sqlSelectOldDetail);
            pstDetailSelect.setString(1, idDetailTransaksi);
            rsOldDetail = pstDetailSelect.executeQuery();

            String idTransaksi = "";
            int oldSubtotal = 0;

            if (rsOldDetail.next()) {
                idTransaksi = rsOldDetail.getString("id_transaksi");
                oldSubtotal = rsOldDetail.getInt("subtotal");
            } else {
                JOptionPane.showMessageDialog(null, "Detail transaksi tidak ditemukan untuk diubah.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                kon.rollback();
                return;
            }
            rsOldDetail.close(); // Close immediately after use

            // 2. Retrieve new data from form components
            String selectedBarangName = (String) combox_barang.getSelectedItem();
            int quantity = Integer.parseInt(txt_kuantitas.getText());
            int hargaSatuan = parseCurrency(txt_nominal.getText()); // Use parseCurrency

            // 3. Calculate new subtotal
            int newSubtotal = quantity * hargaSatuan;

            // 4. Update data in t_detail_transaksi
            String SQL_UPDATE_DETAIL = "UPDATE t_detail_transaksi SET quantity = ?, harga_satuan = ?, subtotal = ? WHERE id_detail_transaksi = ?";
            pstDetailUpdate = kon.prepareStatement(SQL_UPDATE_DETAIL);
            pstDetailUpdate.setInt(1, quantity);
            pstDetailUpdate.setInt(2, hargaSatuan);
            pstDetailUpdate.setInt(3, newSubtotal);
            pstDetailUpdate.setString(4, idDetailTransaksi);

            int rowsAffected = pstDetailUpdate.executeUpdate();

            if (rowsAffected > 0) {
                // 5. Calculate the change in subtotal and update total_transaksi
                int subtotalChange = newSubtotal - oldSubtotal;
                updateTotal(idTransaksi, subtotalChange); // Use the existing updateTotal method

                kon.commit(); // Commit the transaction if both updates are successful
                JOptionPane.showMessageDialog(null, "Detail transaksi berhasil diubah.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                settableload(); // Refresh table
            } else {
                kon.rollback(); // Rollback if detail update fails
                JOptionPane.showMessageDialog(null, "Gagal mengubah detail transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Kuantitas atau Nominal harus berupa angka yang valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Number format error in updateDetailTransaksi: " + e.getMessage());
            try { if(kon != null) kon.rollback(); } catch (SQLException ex) { /* ignore */ }
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(null, "Format nominal tidak valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Parse error for nominal in updateDetailTransaksi: " + e.getMessage());
            try { if(kon != null) kon.rollback(); } catch (SQLException ex) { /* ignore */ }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan database saat mengubah detail transaksi: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            System.err.println("SQL error in updateDetailTransaksi: " + e.getMessage());
            try { if(kon != null) kon.rollback(); } catch (SQLException ex) { /* ignore */ }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Class not found in updateDetailTransaksi: " + e.getMessage());
            try { if(kon != null) kon.rollback(); } catch (SQLException ex) { /* ignore */ }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan tak terduga: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("General error in updateDetailTransaksi: " + e.getMessage());
            try { if(kon != null) kon.rollback(); } catch (SQLException ex) { /* ignore */ }
        } finally {
            // Close resources
            try {
                if (rsOldDetail != null) rsOldDetail.close();
                if (pstDetailSelect != null) pstDetailSelect.close();
                if (pstDetailUpdate != null) pstDetailUpdate.close();
                if (kon != null) kon.close();
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources in updateDetailTransaksi: " + closeEx.getMessage());
            }
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
        txt_kuantitas = new javax.swing.JTextField();
        txt_total = new javax.swing.JTextField();
        txt_nominal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        combox_barang = new javax.swing.JComboBox<>();
        btn_kembali = new javax.swing.JButton();
        txt_transaksi = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));

        btn_barang.setText("Barang");
        btn_barang.setBackground(new java.awt.Color(51, 51, 51));
        btn_barang.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btn_barang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_barang.setForeground(new java.awt.Color(255, 255, 255));
        btn_barang.setToolTipText("");
        btn_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_barangActionPerformed(evt);
            }
        });

        btn_keuangan.setText("Transaksi");
        btn_keuangan.setBackground(new java.awt.Color(51, 51, 51));
        btn_keuangan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btn_keuangan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_keuangan.setForeground(new java.awt.Color(255, 255, 255));
        btn_keuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_keuanganActionPerformed(evt);
            }
        });

        btn_supplier.setText("Supplier");
        btn_supplier.setBackground(new java.awt.Color(51, 51, 51));
        btn_supplier.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btn_supplier.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_supplier.setForeground(new java.awt.Color(255, 255, 255));
        btn_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_supplierActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_barang, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(btn_keuangan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(126, 126, 126)
                    .addComponent(btn_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(637, Short.MAX_VALUE)))
        );

        jLabel1.setText("Detail Transaksi");
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N

        jLabel2.setText("Nama Barang");
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        btn_ubah.setText("Ubah");
        btn_ubah.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        btn_tambah.setText("Tambah");
        btn_tambah.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_hapus.setText("Hapus");
        btn_hapus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        jLabel5.setText("Supplier");
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel3.setText("Kuantitas");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel6.setText("Transaksi");
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        combox_supplier.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combox_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_supplierActionPerformed(evt);
            }
        });

        txt_kuantitas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_total.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txt_nominal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel4.setText("Total");
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        combox_barang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combox_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_barangActionPerformed(evt);
            }
        });

        btn_kembali.setText("Kembali");
        btn_kembali.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kembaliActionPerformed(evt);
            }
        });

        txt_transaksi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

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
                                        .addComponent(jLabel5)
                                        .addGap(20, 20, 20)
                                        .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txt_transaksi)))
                                .addGap(52, 52, 52)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_kembali, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                    .addComponent(txt_nominal, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combox_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGap(173, 173, 173)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_tambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_ubah, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_kembali, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addGap(21, 21, 21))
        );

        setSize(new java.awt.Dimension(1478, 824));
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
        int selectedRow = tabel_transaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Pilih detail transaksi yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the ID of the detail transaction to be deleted
        String idDetailTransaksiToDelete = String.valueOf(tableModel.getValueAt(selectedRow, 1));
        // Also get its associated transaction ID and subtotal to revert the main total
        String idTransaksiAssociated = String.valueOf(tableModel.getValueAt(selectedRow, 0));
        
        int subtotalToDelete = 0;
        try {
            // Parse the formatted subtotal from the table model
            subtotalToDelete = parseCurrency(String.valueOf(tableModel.getValueAt(selectedRow, 8)));
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengurai total harga dari tabel. Tidak dapat menghapus.", "Kesalahan Data", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error parsing subtotal for deletion: " + e.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Apakah Anda yakin ingin menghapus detail transaksi ini?",
                "Konfirmasi Hapus Detail", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection kon = null;
            PreparedStatement pstDeleteDetail = null;
            try {
                Class.forName(driver);
                kon = DriverManager.getConnection(database, user, pass);
                kon.setAutoCommit(false); // Start transaction

                // 1. Delete the detail transaction
                String sqlDeleteDetail = "DELETE FROM t_detail_transaksi WHERE id_detail_transaksi = ?";
                pstDeleteDetail = kon.prepareStatement(sqlDeleteDetail);
                pstDeleteDetail.setString(1, idDetailTransaksiToDelete);

                int rowsAffected = pstDeleteDetail.executeUpdate();

                if (rowsAffected > 0) {
                    // 2. Subtract the subtotal of the deleted detail from the main transaction's total
                    updateTotal(idTransaksiAssociated, -subtotalToDelete); // Subtract the old subtotal

                    kon.commit(); // Commit the transaction
                    JOptionPane.showMessageDialog(null, "Detail transaksi berhasil dihapus.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    settableload(); // Refresh table
                } else {
                    kon.rollback(); // Rollback if detail deletion fails
                    JOptionPane.showMessageDialog(null, "Gagal menghapus detail transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                System.err.println("SQL Error in btn_hapusActionPerformed (detail): " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Gagal menghapus detail transaksi: " + ex.getMessage() + "\nOperasi dibatalkan.", "Error Database", JOptionPane.ERROR_MESSAGE);
                try { if(kon != null) kon.rollback(); } catch (SQLException rollbackEx) { /* ignore */ }
            } catch (ClassNotFoundException ex) {
                System.err.println("Driver database tidak ditemukan in btn_hapusActionPerformed (detail): " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                System.err.println("General error in btn_hapusActionPerformed (detail): " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan tak terduga: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (pstDeleteDetail != null) pstDeleteDetail.close();
                    if (kon != null) kon.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing resources in btn_hapusActionPerformed (detail): " + closeEx.getMessage());
                }
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_kembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kembaliActionPerformed
        // TODO add your handling code here:
        f_transaksi transaksi = new f_transaksi();
        transaksi.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_btn_kembaliActionPerformed

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
    private javax.swing.JButton btn_kembali;
    private javax.swing.JButton btn_keuangan;
    private javax.swing.JButton btn_supplier;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox<String> combox_barang;
    private javax.swing.JComboBox<String> combox_supplier;
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
    private javax.swing.JTextField txt_transaksi;
    // End of variables declaration//GEN-END:variables
}
