/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package supplynote;

import javax.swing.*;

import java.sql.*;
/**
 *
 * @author aido
 */
public class Frame_utama extends javax.swing.JFrame {

    /**
     * Creates new form f_utama
     */
    
    koneksi dbsetting;
    String driver,database, user, pass;
    
    public Frame_utama() {
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
    }
    
    private javax.swing.table.DefaultTableModel tableModel=getDefaultTabelModel();
    private javax.swing.table.DefaultTableModel getDefaultTabelModel(){
        return new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] {"Kode Supplier","Nama Supplier", "Nama Barang", 
                    "Jenis Barang", "Nomor NIB", "Harga"}
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
        
    String data []=new String[6];
    private void settableload(){
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT t_supplier.kode_supplier, t_supplier.nama_supplier, t_barang.nama_barang, "
                    + "t_barang.jenis_barang, t_barang.nomor_nib, t_barang.harga FROM t_supplier "
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
            
        }catch(Exception ex){
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), "Error", 
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    //Menampilkan data sesuai dengan apa yang dipilih di combo box
    private void filterDataBySupplier(String namaSupplier){
        try{
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            
            //ambil kode_supplier berdasarkan nama_supplier
            String sqlCariKode = "SELECT kode_supplier FROM t_supplier WHERE nama_supplier = ?";
            
            PreparedStatement pst = kon.prepareStatement(sqlCariKode);
            pst.setString(1, namaSupplier);
            
            ResultSet resKode = pst.executeQuery();
            
            String kodeSupplier = "";
            if(resKode.next()){
                kodeSupplier = resKode.getString("kode_supplier");
            }
            
            // Hapus semua baris di tableModel
            tableModel.setRowCount(0);
            
            // Jika kodeSupplier ditemukan, ambil data barang
            if (!kodeSupplier.isEmpty()) {
                String SQL = "SELECT t_supplier.kode_supplier, nama_supplier, nama_barang, jenis_barang, "
                        + "nomor_nib, harga FROM t_supplier"
                        + " INNER JOIN t_barang ON t_supplier.kode_supplier = "
                        + "t_barang.kode_supplier WHERE t_supplier.kode_supplier = ?";

                
                pst = kon.prepareStatement(SQL);
                pst.setString(1, kodeSupplier);
                
                ResultSet res = pst.executeQuery();

                while (res.next()) {
                    data[0] = res.getString(1); 
                    data[1] = res.getString(2); 
                    data[2] = res.getString(3); 
                    data[3] = res.getString(4); 
                    data[4] = res.getString(5); 
                    data[5] = res.getString(6); 
                    tableModel.addRow(data);
                }
            }

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        combox_supplier = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_barang = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        btn_detail = new javax.swing.JButton();
        btn_tampilSemua = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));

        jButton4.setText("Supplier dan Barang");

        jButton5.setText("Keuangan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setText("Pencatatan Supplier dan Barang");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Nama Supplier");

        combox_supplier.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        combox_supplier.setMinimumSize(new java.awt.Dimension(72, 108));
        combox_supplier.setName(""); // NOI18N
        combox_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combox_supplierActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setText("Edit Supplier");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tabel_barang.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
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
        jScrollPane1.setViewportView(tabel_barang);

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton2.setText("Tambah Supplier");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btn_detail.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btn_detail.setText("Lihat Detail");
        btn_detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_detailActionPerformed(evt);
            }
        });

        btn_tampilSemua.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btn_tampilSemua.setText("Tampilkan Semua");
        btn_tampilSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilSemuaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btn_detail, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_tampilSemua, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_detail, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tampilSemua, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        Frame_TambahSupplier tambahSup = new Frame_TambahSupplier();
        tambahSup.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btn_detailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_detailActionPerformed

        // Ambil kode supplier dari model tabel
        String kodeSupplier = (String) tableModel.getValueAt(0, 0); // Kolom 0 adalah Kode Supplier

        // Membuka frame Lihat Detail
        Frame_LihatDetail lihatDetail = new Frame_LihatDetail();
        lihatDetail.setKodeSupplier(kodeSupplier); // Set kode supplier

        lihatDetail.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btn_detailActionPerformed

    private void combox_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combox_supplierActionPerformed
        // TODO add your handling code here:
        String selectedSupplier = (String) combox_supplier.getSelectedItem();
        if (selectedSupplier != null) {
            filterDataBySupplier(selectedSupplier);
        }
    }//GEN-LAST:event_combox_supplierActionPerformed

    private void btn_tampilSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilSemuaActionPerformed
        // TODO add your handling code here:
        settableload();
    }//GEN-LAST:event_btn_tampilSemuaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        frame_editSupplier edit_supplier = new frame_editSupplier();
        edit_supplier.setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(Frame_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frame_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frame_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frame_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame_utama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_detail;
    private javax.swing.JButton btn_tampilSemua;
    private javax.swing.JComboBox combox_supplier;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel_barang;
    // End of variables declaration//GEN-END:variables
}
