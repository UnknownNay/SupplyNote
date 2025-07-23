/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package supplynote;

import javax.swing.*;

import java.sql.*;
import java.text.NumberFormat;
import java.util.UUID;
import java.time.LocalDate;
import java.util.Locale;
/**
 *
 * @author aido
 */
public class f_transaksi extends javax.swing.JFrame {

    /**
     * Creates new form f_utama
     */
    
    koneksi dbsetting;
    String driver,database, user, pass;

    
    public f_transaksi() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        tabel_transaksi.setModel(tableModel);
        
        txt_keterangan.setLineWrap(true); 
        txt_keterangan.setWrapStyleWord(true);
//        Hide column 1
        tabel_transaksi.getColumnModel().getColumn(0).setMinWidth(0);
        tabel_transaksi.getColumnModel().getColumn(0).setMaxWidth(0);
        settableload();
        loadEnumToComboBox();
    }
    
    private javax.swing.table.DefaultTableModel tableModel=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {"ID Transaksi","Nama Transaksi", "Tanggal Transaksi", 
                    "Keterangan", "Status", "Total Transaski"}
        ){
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return canEdit[columnIndex];
            }
        };
    }
    private String formatRupiah(int amount) {
        // Use Locale.forLanguageTag("id-ID") for Indonesian currency formatting
        // Or Locale.GERMANY if you just want dot as thousands separator and no Rp prefix
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(amount);
    }

    // You might also need a method to parse formatted currency back to an int
    private int parseRupiah(String formattedAmount) throws java.text.ParseException {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        Number number = formatter.parse(formattedAmount);
        return number.intValue();
    }
    
    
    int row = 0;
    private void tampil_field(){
        row = tabel_transaksi.getSelectedRow();

        txt_transaksi.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txt_keterangan.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        String status = String.valueOf(tableModel.getValueAt(row, 4));
        combox_status.setSelectedItem(status);

        String dateString = String.valueOf(tableModel.getValueAt(row, 2));
        try {
            LocalDate date = LocalDate.parse(dateString);
            datepicker.setDate(date);
        } catch (Exception e) {
            System.err.println("Error parsing date from table: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error loading date: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            datepicker.setDate(null);
        }
    }
    
    public void membersihkan_teks(){
        txt_transaksi.setText("");
        txt_keterangan.setText("");
    }
    
    private void loadEnumToComboBox() {
        try {
            Class.forName(driver); // driver biasanya "com.mysql.cj.jdbc.Driver"
            Connection kon = DriverManager.getConnection(database, user, pass);

            Statement st = kon.createStatement();
            String sql = "SELECT SUBSTRING(COLUMN_TYPE, 6, LENGTH(COLUMN_TYPE) - 6) AS enum_values "
                       + "FROM information_schema.COLUMNS "
                       + "WHERE TABLE_NAME = 't_transaksi' AND COLUMN_NAME = 'status'";

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                String enumValues = rs.getString("enum_values");
                // Hilangkan kutip dan pecah menjadi array
                String[] values = enumValues.replace("'", "").split(",");
                for (String value : values) {
                    combox_status.addItem(value.trim()); // Tambahkan ke combobox
                }
            }

            rs.close();
            st.close();
            kon.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat status: " 
                    + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        }
    }
        
    String data []=new String[6];
    private void settableload(){
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT * FROM t_transaksi";
            ResultSet res = stt.executeQuery(SQL);
            
            
            tableModel.setRowCount(0);
            while(res.next()){
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);
                data[3] = res.getString(4);
                data[4] = res.getString(5);
                int totalTransaksiInt = res.getInt(6); 
                data[5] = formatRupiah(totalTransaksiInt);
            
                tableModel.addRow(data);
                
            }
            
            res.close();
            stt.close();
            kon.close();
            
            membersihkan_teks();
            
        }catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), "Error", 
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
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
        btn_transaksi = new javax.swing.JButton();
        btn_supplier = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_ubah = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_transaksi = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txt_transaksi = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_hapus = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_keterangan = new javax.swing.JTextArea();
        combox_status = new javax.swing.JComboBox<>();
        datepicker = new com.github.lgooddatepicker.components.DatePicker();
        btn_detail = new javax.swing.JButton();

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

        btn_transaksi.setText("Transaksi");
        btn_transaksi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btn_transaksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

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
                    .addComponent(btn_transaksi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
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
                .addGap(170, 170, 170)
                .addComponent(btn_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(126, 126, 126)
                    .addComponent(btn_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(637, Short.MAX_VALUE)))
        );

        jLabel1.setText("Transaksi");
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N

        jLabel2.setText("Tanggal");
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        btn_ubah.setText("Ubah");
        btn_ubah.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_ubah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ubahActionPerformed(evt);
            }
        });

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
        tabel_transaksi.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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

        jLabel3.setText("Nama Transkasi");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        txt_transaksi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel4.setText("Status");
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel6.setText("Keterangan");
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        btn_hapus.setText("Hapus");
        btn_hapus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        txt_keterangan.setColumns(20);
        txt_keterangan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_keterangan.setRows(5);
        jScrollPane2.setViewportView(txt_keterangan);

        combox_status.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        datepicker.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btn_detail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_detail.setText("Lihat Detail");
        btn_detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_detailActionPerformed(evt);
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
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addGap(18, 18, 18))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel4)
                                            .addGap(100, 100, 100)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(81, 81, 81)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_transaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                                    .addComponent(combox_status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(datepicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(66, 66, 66)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_detail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(datepicker, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txt_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(89, 89, 89)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combox_status, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_ubah, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_detail, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1478, 823));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:

        String namaTrans = txt_transaksi.getText();
        String Ket = txt_keterangan.getText();
        String status = (String) combox_status.getSelectedItem();
        
        
        //Ambil tanggal
        LocalDate selectedDate = datepicker.getDate();
        
        // Validasi: Nama Transaksi tidak boleh kosong
        if (namaTrans.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama transaksi belum diisi.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi: Tanggal harus dipilih
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(null, "Tanggal transaksi belum dipilih.", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi: Status harus dipilih
        if (status == null || status.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Status transaksi belum dipilih.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);
        
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            
            //generate UUID
            UUID uuid = UUID.randomUUID();
            String idTrans = uuid.toString();
            
            String sql = "INSERT INTO t_transaksi (id_transaksi, nama_transaksi,"
                    + " tanggal_transaksi,"
                    + "keterangan, status, total_transaksi) VALUES (?, ?, ?, ?, ?, ?) ";
            
            PreparedStatement pst = kon.prepareStatement(sql);
            pst.setString(1, idTrans);
            pst.setString(2, namaTrans);
            pst.setDate(3, sqlDate);
            pst.setString(4, Ket);
            pst.setString(5, status);
            pst.setInt(6, 0);
            
            int rowInserted = pst.executeUpdate();
            if (rowInserted > 0) {
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan.");
                settableload();
            }
  
            pst.close();
            kon.close();
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data: " 
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:
//        row = tabel_transaksi.getSelectedRow();
        String namaTrans = txt_transaksi.getText();
        String Ket = txt_keterangan.getText();
        String status = (String) combox_status.getSelectedItem();
        String idTransaksi = String.valueOf(tableModel.getValueAt(row, 0));

        
        
        //Ambil tanggal
        LocalDate selectedDate = datepicker.getDate();
        java.sql.Date sqlDate = java.sql.Date.valueOf(selectedDate);
        
        
        // Validasi: Nama Transaksi tidak boleh kosong
        if (namaTrans.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama transaksi belum diisi.", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi: Tanggal harus dipilih
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(null, "Tanggal transaksi belum dipilih.", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi: Status harus dipilih
        if (status == null || status.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Status transaksi belum dipilih.", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
            
        // Update ke database
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            String sql = "UPDATE t_transaksi SET nama_transaksi=?, tanggal_transaksi=?, "
                    + "keterangan=?, status=? WHERE id_transaksi=?";
            PreparedStatement pst = kon.prepareStatement(sql);

            pst.setString(1, namaTrans); 
            pst.setDate(2, sqlDate);
            pst.setString(3, Ket);
            pst.setString(4, status);
            pst.setString(5, idTransaksi);

            int rowUpdated = pst.executeUpdate();
            if (rowUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Data berhasil diubah.");
                settableload();
            }

            pst.close();
            kon.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, 
                    "Gagal mengubah data: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(ex.getMessage());
        }           
    }//GEN-LAST:event_btn_ubahActionPerformed

    private void tabel_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_transaksiMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 1){
            tampil_field();
        }
    }//GEN-LAST:event_tabel_transaksiMouseClicked

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        int selectedRow = tabel_transaksi.getSelectedRow();
        if (selectedRow == -1) {
            // Show a warning if no row is selected
            JOptionPane.showMessageDialog(null, "Pilih baris transaksi yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idTransaksi = tableModel.getValueAt(selectedRow, 0).toString();

        // Ask for confirmation before deleting
        int confirm = JOptionPane.showConfirmDialog(null,
                            "Apakah Anda yakin ingin menghapus transaksi ini dan semua detail terkaitnya?",
                            "Konfirmasi Hapus Transaksi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection kon = null; // Initialize connection to null for finally block
            PreparedStatement pstDetails = null; // Initialize prepared statements to null
            PreparedStatement pstTransaction = null;

            try {
                Class.forName(driver);
                kon = DriverManager.getConnection(database, user, pass);
                kon.setAutoCommit(false); // Start transaction: important for multi-step operations

                // 1. Delete all related records from the child table (t_detail_transaksi)
                String sqlDeleteDetails = "DELETE FROM t_detail_transaksi WHERE id_transaksi = ?";
                pstDetails = kon.prepareStatement(sqlDeleteDetails);
                pstDetails.setString(1, idTransaksi);
                int detailsDeleted = pstDetails.executeUpdate();
                System.out.println(detailsDeleted + " detail transaksi dihapus."); // For debugging

                // 2. Now, delete the parent record from the t_transaksi table
                String sqlDeleteTransaction = "DELETE FROM t_transaksi WHERE id_transaksi = ?";
                pstTransaction = kon.prepareStatement(sqlDeleteTransaction);
                pstTransaction.setString(1, idTransaksi);
                int transactionDeleted = pstTransaction.executeUpdate();
                System.out.println(transactionDeleted + " transaksi dihapus."); // For debugging

                kon.commit(); // Commit the transaction if both deletions are successful
                JOptionPane.showMessageDialog(null, "Data transaksi dan detail terkait berhasil dihapus.");
                settableload(); // Reload the table to reflect changes
                membersihkan_teks(); // Clear the text fields

            } catch (SQLException ex) {
                // If any error occurs, rollback the transaction
                try {
                    if (kon != null) {
                        kon.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
                JOptionPane.showMessageDialog(null,
                        "Gagal menghapus data: " + ex.getMessage() + "\nOperasi dibatalkan.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("SQL Error: " + ex.getMessage());
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null,
                        "Driver database tidak ditemukan: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Class Not Found Error: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Terjadi kesalahan tak terduga: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("General Error: " + ex.getMessage());
            } finally {
                // Close resources in a finally block to ensure they are always closed
                try {
                    if (pstDetails != null) pstDetails.close();
                    if (pstTransaction != null) pstTransaction.close();
                    if (kon != null) kon.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing resources: " + closeEx.getMessage());
                }
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

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

    private void btn_detailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_detailActionPerformed
        // TODO add your handling code here:
        int selectedRow = tabel_transaksi.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil ID Transaksi dari kolom pertama (index 0) yang tersembunyi
            String idTransaksi = String.valueOf(tableModel.getValueAt(selectedRow, 0));

            // Buka f_detailTransaksi dan kirim ID Transaksi
            f_detailTransaksi detail = new f_detailTransaksi(idTransaksi );
            detail.setVisible(true);
            
            this.setVisible(false);

        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris transaksi terlebih dahulu untuk melihat detail.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btn_detailActionPerformed

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
            java.util.logging.Logger.getLogger(f_transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(f_transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(f_transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(f_transaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new f_transaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_barang;
    private javax.swing.JButton btn_detail;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_supplier;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_transaksi;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox<String> combox_status;
    private com.github.lgooddatepicker.components.DatePicker datepicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tabel_transaksi;
    private javax.swing.JTextArea txt_keterangan;
    private javax.swing.JTextField txt_transaksi;
    // End of variables declaration//GEN-END:variables
}
