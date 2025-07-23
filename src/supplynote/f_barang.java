/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package supplynote;

import javax.swing.*;

import java.sql.*;
import java.util.UUID;
/**
 *
 * @author aido
 */
public class f_barang extends javax.swing.JFrame {

    /**
     * Creates new form f_utama
     */
    
    koneksi dbsetting;
    String driver,database, user, pass;
    private String kodeSup;
    
    public f_barang() {
        initComponents();
        
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        tabel_barang.setModel(tableModel);
        comboxSupplier();
        
        
//        Hide column 1
        tabel_barang.getColumnModel().getColumn(0).setMinWidth(0);
        tabel_barang.getColumnModel().getColumn(0).setMaxWidth(0);
        //Hide column 1
        tabel_barang.getColumnModel().getColumn(1).setMinWidth(0);
        tabel_barang.getColumnModel().getColumn(1).setMaxWidth(0);
        
        String selectedSupplier = (String) combox_supplier.getSelectedItem();
        kodeSup = setkodeSup(selectedSupplier);
    }
    
    private javax.swing.table.DefaultTableModel tableModel=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {"Kode Supplier","Kode Barang","Nama Supplier", "Nama Barang", 
                    "Jenis Barang", "Harga"}
        ){
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return canEdit[columnIndex];
            }
        };
    }
    
    //Mengambil data nama supplier untuk dimasukkan ke dalam combox
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
    
    int row = 0;
    private void tampil_field(){
        row = tabel_barang.getSelectedRow();
        
        txt_namaBarang.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txt_jenisBarang.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txt_harga.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }
    
    public void membersihkan_teks(){
        txt_namaBarang.setText("");
        txt_jenisBarang.setText("");
        txt_harga.setText("");
    }
    
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
    
    String data []=new String[6];
    private void settableload(){
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT t_supplier.kode_supplier, t_barang.kode_barang, t_supplier.nama_supplier, t_barang.nama_barang, "
                    + "t_barang.jenis_barang, t_barang.harga FROM t_supplier "
                    + "INNER JOIN t_barang ON t_supplier.kode_supplier = t_barang.kode_supplier";
            ResultSet res = stt.executeQuery(SQL);
            
            
            tableModel.setRowCount(0);
            while(res.next()){
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);
                data[3] = res.getString(4);
                data[4] = res.getString(5);
                data[5] = res.getString(6);
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
        }
    }
    
    //Menampilkan data sesuai dengan apa yang dipilih di combo box
    private void filterDataBySupplier(String namaSupplier){
        try{           
            String kodeSupplier = setkodeSup(namaSupplier);
            // Jika kodeSupplier ditemukan, ambil data barang
            if (!kodeSupplier.isEmpty()) {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database, user, pass);
                String SQL = "SELECT t_supplier.kode_supplier,kode_barang, nama_supplier, nama_barang, jenis_barang, "
                        + "harga FROM t_supplier"
                        + " INNER JOIN t_barang ON t_supplier.kode_supplier = "
                        + "t_barang.kode_supplier WHERE t_supplier.kode_supplier = ?";

                
                PreparedStatement pst = kon.prepareStatement(SQL);
                pst.setString(1, kodeSupplier);
                
                ResultSet res = pst.executeQuery();

                tableModel.setRowCount(0);
                while (res.next()) {
                    data[0] = res.getString(1); 
                    data[1] = res.getString(2); 
                    data[2] = res.getString(3); 
                    data[3] = res.getString(4); 
                    data[4] = res.getString(5); 
                    data[5] = res.getString(6);
                    tableModel.addRow(data);
                }
                pst.close();
                kon.close();
                membersihkan_teks();

            }      
            

        }catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), "Error", 
                    JOptionPane.INFORMATION_MESSAGE);
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
        combox_supplier = new javax.swing.JComboBox();
        btn_ubah = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_barang = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txt_namaBarang = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_jenisBarang = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_harga = new javax.swing.JTextField();
        btn_tampil = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));

        btn_barang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_barang.setText("Barang");
        btn_barang.setToolTipText("");
        btn_barang.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setText("Pencatatan Barang");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Nama Supplier");

        combox_supplier.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        combox_supplier.setMinimumSize(new java.awt.Dimension(72, 108));
        combox_supplier.setName(""); // NOI18N
        combox_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_supplierActionPerformed(evt);
            }
        });

        btn_ubah.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_ubah.setText("Ubah");
        btn_ubah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ubahActionPerformed(evt);
            }
        });

        tabel_barang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabel_barang.setModel(new javax.swing.table.DefaultTableModel(
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
        tabel_barang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_barangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_barang);

        btn_tambah.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Nama Barang");

        txt_namaBarang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Jenis Barang");

        txt_jenisBarang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Harga");

        txt_harga.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btn_tampil.setText("Tampilkan Semua");
        btn_tampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilActionPerformed(evt);
            }
        });

        btn_hapus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
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
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_namaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4))
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_jenisBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(txt_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                            .addComponent(btn_tampil, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_namaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txt_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_jenisBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_ubah, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_tampil, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1478, 806));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        // TODO add your handling code here:
        String selectedSupplier = (String) combox_supplier.getSelectedItem();
        String kodeSup = setkodeSup(selectedSupplier);

        // Ambil data dari text field
        String namaBrg = txt_namaBarang.getText();
        String jenisBrg = txt_jenisBarang.getText();
        String harga = txt_harga.getText();
        
        // Validasi: Pastikan semua field tidak kosong
        if (selectedSupplier == null || selectedSupplier.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Supplier belum dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (namaBrg.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama barang belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (jenisBrg.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Jenis barang belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (harga.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harga belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validasi: Harga harus berupa angka
        try {
            double hargaDouble = Double.parseDouble(harga);
            if (hargaDouble <= 0) {
                JOptionPane.showMessageDialog(null, "Harga harus lebih besar dari 0.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Harga harus berupa angka.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            
            //generate UUID
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();
            
            String sql = "INSERT INTO t_barang (kode_supplier, kode_barang, nama_barang,"
                    + "jenis_barang, harga) VALUES (?, ?, ?, ?, ?) ";
            
            PreparedStatement pst = kon.prepareStatement(sql);
            pst.setString(1, kodeSup);
            pst.setString(2, uuidAsString);
            pst.setString(3, namaBrg);
            pst.setString(4, jenisBrg);
            pst.setString(5, harga);
            
            int rowInserted = pst.executeUpdate();
            if (rowInserted > 0) {
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan.");
                filterDataBySupplier(selectedSupplier);
                membersihkan_teks(); // reset form
            }
  
            pst.close();
            kon.close();
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(ex.getMessage());
        }
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void combox_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combox_supplierActionPerformed
        // TODO add your handling code here:
        String selectedSupplier = (String) combox_supplier.getSelectedItem();
        if (selectedSupplier != null) {
            filterDataBySupplier(selectedSupplier);
        }
    }//GEN-LAST:event_combox_supplierActionPerformed

    private void btn_ubahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ubahActionPerformed
        // TODO add your handling code here:
        
        // Ambil Kode Barang dari tabel
        int selectedRow = tabel_barang.getSelectedRow();
        String selectedSupplier = (String) combox_supplier.getSelectedItem();
        String kodeBrg = tableModel.getValueAt(selectedRow, 1).toString();
        
        // Ambil data dari text field
        String namaBrg = txt_namaBarang.getText();
        String jenisBrg = txt_jenisBarang.getText();
        String harga = txt_harga.getText();
        
        // Validasi: Pastikan semua field tidak kosong
        if (selectedSupplier == null || selectedSupplier.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Supplier belum dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (namaBrg.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nama barang belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (jenisBrg.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Jenis barang belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (harga.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harga belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validasi: Harga harus berupa angka
        try {
            double hargaDouble = Double.parseDouble(harga);
            if (hargaDouble <= 0) {
                JOptionPane.showMessageDialog(null, "Harga harus lebih besar dari 0.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Harga harus berupa angka.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Update ke database
        try {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);

            String sql = "UPDATE t_barang SET nama_barang=?, jenis_barang=?, "
                    + "harga=? WHERE kode_barang=?";
            PreparedStatement pst = kon.prepareStatement(sql);

            pst.setString(1, namaBrg);
            pst.setString(2, jenisBrg); 
            pst.setString(3, harga);
            pst.setString(4, kodeBrg);

            int rowUpdated = pst.executeUpdate();
            if (rowUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Data berhasil diubah.");
                filterDataBySupplier(selectedSupplier);
                membersihkan_teks(); // reset form
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

    private void tabel_barangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_barangMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 1){
            tampil_field();
        }
    }//GEN-LAST:event_tabel_barangMouseClicked

    private void btn_tampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilActionPerformed
        // TODO add your handling code here:
        settableload();
    }//GEN-LAST:event_btn_tampilActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        int selectedRow = tabel_barang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Pilih barang yang ingin dihapus dari tabel.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kodeBrg = tableModel.getValueAt(selectedRow, 1).toString();
        String namaBrg = tableModel.getValueAt(selectedRow, 3).toString(); // Get item name for confirmation

        int confirm = JOptionPane.showConfirmDialog(null, 
                "Apakah Anda yakin ingin menghapus barang '" + namaBrg + "'? Ini juga akan menghapus detail transaksi terkait.", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database, user, pass);
                kon.setAutoCommit(false); // Start transaction

                // 1. Delete dependent records from t_detail_transaksi
                String sqlDeleteDetail = "DELETE FROM t_detail_transaksi WHERE kode_barang = ?";
                PreparedStatement pstDetail = kon.prepareStatement(sqlDeleteDetail);
                pstDetail.setString(1, kodeBrg);
                pstDetail.executeUpdate();
                pstDetail.close();

                // 2. Delete the item from t_barang
                String sqlDeleteBarang = "DELETE FROM t_barang WHERE kode_barang = ?";
                PreparedStatement pstBarang = kon.prepareStatement(sqlDeleteBarang);
                pstBarang.setString(1, kodeBrg);

                int rowsAffected = pstBarang.executeUpdate();
                if (rowsAffected > 0) {
                    kon.commit(); // Commit transaction if both deletions are successful
                    JOptionPane.showMessageDialog(null, "Data barang dan detail transaksi terkait berhasil dihapus.");
                    String selectedSupplier = (String) combox_supplier.getSelectedItem();
                    if (selectedSupplier != null && !selectedSupplier.isEmpty()) {
                        filterDataBySupplier(selectedSupplier);
                    } else {
                        settableload(); // Reload all data if no supplier is selected
                    }
                    membersihkan_teks(); // reset form
                } else {
                    kon.rollback(); // Rollback if item deletion fails
                    JOptionPane.showMessageDialog(null, "Gagal menghapus data barang.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                pstBarang.close();
                kon.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, 
                        "Gagal menghapus data: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_supplierActionPerformed
        // TODO add your handling code here:
        f_supplier supplier = new f_supplier();
        
        supplier.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btn_supplierActionPerformed

    private void btn_keuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_keuanganActionPerformed
        // TODO add your handling code here:
        f_transaksi transaksi = new f_transaksi();
        
        transaksi.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btn_keuanganActionPerformed

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
            java.util.logging.Logger.getLogger(f_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(f_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(f_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(f_barang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new f_barang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_barang;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_keuangan;
    private javax.swing.JButton btn_supplier;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_tampil;
    private javax.swing.JButton btn_ubah;
    private javax.swing.JComboBox combox_supplier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel_barang;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_jenisBarang;
    private javax.swing.JTextField txt_namaBarang;
    // End of variables declaration//GEN-END:variables
}
