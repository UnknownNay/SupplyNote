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
    
    // Di f_detailTransaksi.java
    private String formatRupiah(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        return formatter.format(amount);
    }

    private int parseRupiah(String formattedAmount) throws java.text.ParseException {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        Number number = formatter.parse(formattedAmount.trim()); // Trim whitespace
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
    
    // Dalam f_detailTransaksi.java
    int row = 0;
    private void tampil_field(){
        row = tabel_transaksi.getSelectedRow();

        if (row == -1) {
            // Bersihkan field jika tidak ada baris yang dipilih
            // Asumsi Anda punya method membersihkan_teks() yang mengosongkan txt_kuantitas, txt_nominal, txt_total
            // Jika tidak ada, Anda bisa tambahkan:
            // txt_kuantitas.setText("");
            // txt_nominal.setText("");
            // txt_total.setText("");
            return;
        }

        String kodeSupplierDariTabel = String.valueOf(tableModel.getValueAt(row, 2));
        String kodeBarangDariTabel = String.valueOf(tableModel.getValueAt(row, 3));
        String namaSupplierDariTabel = String.valueOf(tableModel.getValueAt(row, 4));
        String namaBarangDariTabel = String.valueOf(tableModel.getValueAt(row, 5));
        String kuantitasDariTabel = String.valueOf(tableModel.getValueAt(row, 7));
        String totalHargaFormattedDariTabel = String.valueOf(tableModel.getValueAt(row, 8)); // Ini tetap berformat Rupiah dari tabel

        // 1. Set ComboBox Supplier
        combox_supplier.setSelectedItem(namaSupplierDariTabel);

        SwingUtilities.invokeLater(() -> {
            Connection kon = null;
            PreparedStatement pst = null;
            ResultSet res = null;
            try {
                Class.forName(driver);
                kon = DriverManager.getConnection(database, user, pass);

                // 1. Set ComboBox Barang (ini untuk visualisasi di combobox)
                // Ini akan memicu combox_barangActionPerformed, yang akan mengisi txt_nominal dengan format Rupiah.
                // Biarkan combox_barangActionPerformed yang mengisi txt_nominal.
                combox_barang.setSelectedItem(namaBarangDariTabel);

                // TIDAK PERLU lagi ambil harga satuan di sini karena combox_barangActionPerformed sudah melakukannya.
                // Jika Anda tetap khawatir dengan timing, dan ingin mengisi txt_nominal langsung,
                // maka logika pengambilan harga dari DB perlu di sini.
                // Namun, untuk kejelasan dan menghindari duplikasi, kita bergantung pada combox_barangActionPerformed.

                // 2. Set nilai kuantitas
                txt_kuantitas.setText(kuantitasDariTabel);

                // 3. Parse dan set txt_total dari nilai tabel yang sudah berformat
                // INI ADALAH BAGIAN UTAMA UNTUK TXT_TOTAL AGAR TETAP ANGKA BIASA
                try {
                    int totalHargaParsed = parseRupiah(totalHargaFormattedDariTabel);
                    txt_total.setText(String.valueOf(totalHargaParsed)); // Tampilkan sebagai angka biasa
                } catch (java.text.ParseException e) {
                    System.err.println("Error parsing total price from table: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, "Error mengurai total harga dari tabel: " + e.getMessage(), "Error Data", JOptionPane.ERROR_MESSAGE);
                    txt_total.setText("0"); // Set ke "0" jika gagal parse
                }

                // PENTING: JANGAN PANGGIL calculateTotal() di sini.
                // calculateTotal() akan memformat txt_total ke Rupiah, yang tidak Anda inginkan.
                // calculateTotal(); // HAPUS BARIS INI!

            } catch (SQLException ex) {
                System.err.println("SQL Error in tampil_field (SwingUtilities.invokeLater): " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error database saat mengisi detail transaksi: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
                txt_nominal.setText("");
                txt_total.setText("0");
            } catch (ClassNotFoundException ex) {
                System.err.println("Driver database tidak ditemukan in tampil_field (SwingUtilities.invokeLater): " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan.", "Error Driver", JOptionPane.ERROR_MESSAGE);
                txt_nominal.setText("");
                txt_total.setText("0");
            } catch (Exception ex) {
                System.err.println("General error in tampil_field (SwingUtilities.invokeLater): " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan tak terduga saat mengisi detail transaksi: " + ex.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
                txt_nominal.setText("");
                txt_total.setText("0");
            } finally {
                // Tutup resources untuk try-catch yang ada di dalam invokeLater
                // Note: Tidak ada pst dan res di sini karena sudah dipindahkan reliance ke combox_barangActionPerformed
                // Jika Anda memilih untuk mengambil harga nominal langsung di tampil_field, pastikan resources ditutup.
                // Saya asumsikan Anda ingin mengandalkan combox_barangActionPerformed untuk txt_nominal.
                try {
                    if (kon != null) kon.close(); // Tutup koneksi yang dibuka di invokeLater
                } catch (SQLException closeEx) {
                    System.err.println("Error closing DB resources in tampil_field (finally): " + closeEx.getMessage());
                }
            }
        }); // Akhir SwingUtilities.invokeLater
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
                                + "t_detail_transaksi.kode_barang, "
                                + "t_supplier.nama_supplier, "
                                + "t_barang.nama_barang, "
                                + "t_barang.jenis_barang, "
                                + "t_detail_transaksi.quantity, "
                                + "t_detail_transaksi.subtotal "
                                + "FROM "
                                + "t_detail_transaksi "
                                + "INNER JOIN t_barang ON t_detail_transaksi.kode_barang = t_barang.kode_barang "
                                + "INNER JOIN t_supplier ON t_detail_transaksi.kode_supplier = t_supplier.kode_supplier "
                                + "WHERE "
                                + "t_detail_transaksi.id_transaksi = ?";

                pst = kon.prepareStatement(SQL);
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
                    int subtotalValue = resDetail.getInt("subtotal");
                    data[8] = formatRupiah(subtotalValue); // Ini adalah kolom "Total Harga"
                    tableModel.addRow(data);
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } finally {
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

            if (quantity < 0) {
                quantity = 0;
                txt_kuantitas.setText("0");
            }

            // txt_nominal sudah diformat Rupiah dari combox_barangActionPerformed,
            // jadi kita perlu parse kembali ke int
            int price = parseRupiah(txt_nominal.getText()); 
            int total = quantity * price;

            // Cukup set sebagai String dari int, tanpa format Rupiah
            txt_total.setText(String.valueOf(total)); 

        } catch (NumberFormatException e) {
            // Jika kuantitas atau nominal tidak valid (misalnya kosong atau bukan angka)
            txt_total.setText("0"); // Set total ke 0
        } catch (java.text.ParseException e) {
            // Jika txt_nominal tidak bisa di-parse oleh parseRupiah
            System.err.println("Error parsing nominal in calculateTotal: " + e.getMessage());
            txt_total.setText("0");
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
        // Deklarasi variabel di luar try untuk finally block jika diperlukan
        Connection kon = null;
        PreparedStatement pstGetBarangDetails = null;
        ResultSet resBarangDetails = null;
        PreparedStatement pst = null;

        try {
            // Ambil data dari field
            String selectedBarangName = (String) combox_barang.getSelectedItem();
            String quantityText = txt_kuantitas.getText();
            String nominalText = txt_nominal.getText();

            // --- Validasi Input Awal ---
            if (selectedBarangName == null || selectedBarangName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Barang belum dipilih.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Kuantitas belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (nominalText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nominal harga belum diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int quantity = 0;
            try {
                quantity = Integer.parseInt(quantityText.trim()); // Trim untuk hapus spasi
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "Kuantitas harus angka positif.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Kuantitas harus berupa angka valid.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int hargaSatuan = 0;
            try {
                hargaSatuan = parseRupiah(nominalText); // Gunakan parseRupiah untuk nominal
                if (hargaSatuan <= 0) {
                    JOptionPane.showMessageDialog(null, "Nominal harga harus lebih besar dari 0.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(null, "Format nominal (harga) tidak valid. Pastikan formatnya benar (misal: Rp10.000).", "Input Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Parse error for nominal in addDetailTransaksi: " + e.getMessage());
                return;
            }
            // --- Akhir Validasi Input Awal ---

            // Generate UUID
            String idDetailTransaksi = UUID.randomUUID().toString();

            Class.forName(driver);
            kon = DriverManager.getConnection(database, user, pass);

            // 4. Get kode_barang and kode_supplier from selectedBarangName and current kodeSup
            String kodeBarang = "";
            String kodeSupplierFromBarang = ""; // Ini mungkin tidak terlalu penting jika kodeSup sudah valid
            String sqlGetBarangDetails = "SELECT kode_barang, kode_supplier FROM t_barang WHERE nama_barang = ? AND kode_supplier = ?";
            pstGetBarangDetails = kon.prepareStatement(sqlGetBarangDetails);
            pstGetBarangDetails.setString(1, selectedBarangName);
            pstGetBarangDetails.setString(2, kodeSup); // Gunakan kodeSup yang sudah di-set dari combox_supplierActionPerformed
            resBarangDetails = pstGetBarangDetails.executeQuery();
            if (resBarangDetails.next()) {
                kodeBarang = resBarangDetails.getString("kode_barang");
                kodeSupplierFromBarang = resBarangDetails.getString("kode_supplier");
            } else {
                JOptionPane.showMessageDialog(null, "Barang tidak ditemukan untuk supplier ini. Mungkin data tidak sinkron.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Penting untuk return di sini jika data tidak valid
            }

            // 5. Calculate subtotal
            int subtotal = quantity * hargaSatuan;

            // 6. Insert data into t_detail_transaksi
            String SQL = "INSERT INTO t_detail_transaksi (id_detail_transaksi, id_transaksi, kode_barang, kode_supplier, quantity, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pst = kon.prepareStatement(SQL);
            pst.setString(1, idDetailTransaksi);
            pst.setString(2, selectedIdTransaksi);
            pst.setString(3, kodeBarang);
            pst.setString(4, kodeSupplierFromBarang);
            pst.setInt(5, quantity);
            pst.setInt(6, hargaSatuan);
            pst.setInt(7, subtotal);

            // Update total_transaksi di tabel utama
            updateTotal(selectedIdTransaksi, subtotal);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Detail transaksi berhasil ditambahkan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                settableload(); // Refresh the main table
                // membersihkan_teks(); // Panggil ini jika ingin mengosongkan field setelah tambah
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menambahkan detail transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error database saat menambahkan detail transaksi: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("SQL Error in addDetailTransaksi: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan.", "Error Driver", JOptionPane.ERROR_MESSAGE);
            System.err.println("Class Not Found Error in addDetailTransaksi: " + ex.getMessage());
        } catch (Exception e) { // Catch semua exception yang tidak terduga
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan tak terduga saat menambahkan detail transaksi: " + e.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
            System.err.println("General Error in addDetailTransaksi: " + e.getMessage());
        } finally {
            // Pastikan semua resources ditutup
            try {
                if (resBarangDetails != null) resBarangDetails.close();
                if (pstGetBarangDetails != null) pstGetBarangDetails.close();
                if (pst != null) pst.close();
                if (kon != null) kon.close();
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources in addDetailTransaksi: " + closeEx.getMessage());
            }
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
            int hargaSatuan = parseRupiah(txt_nominal.getText()); // Use parseCurrency

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

        jLabel1.setText("Detail Transaksi");
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N

        jLabel2.setText("Nama Barang");
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        btn_ubah.setText("Ubah");
        btn_ubah.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
        btn_tambah.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_hapus.setText("Hapus");
        btn_hapus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
        btn_kembali.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(844, 844, 844)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_ubah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_kembali, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(28, 28, 28)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_transaksi)
                            .addComponent(combox_supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(46, 46, 46)
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
                                .addComponent(txt_total, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_kuantitas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addComponent(btn_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1293, 865));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tabel_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_transaksiMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 1){
            tampil_field();
        }
    }//GEN-LAST:event_tabel_transaksiMouseClicked

    private void combox_barangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combox_barangActionPerformed
        String selectedItem = (String) combox_barang.getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) {
            txt_nominal.setText("");
            txt_kuantitas.setText(""); // Reset kuantitas juga jika barang kosong
            txt_total.setText("");
            return;
        }

        Connection kon = null;
        PreparedStatement pst = null;
        ResultSet res = null;
        try {
            Class.forName(driver); // Pastikan Class.forName dipanggil
            kon = DriverManager.getConnection(database, user, pass);

            String SQL = "SELECT kode_barang, harga FROM t_barang WHERE nama_barang = ? AND kode_supplier = ?";
            pst = kon.prepareStatement(SQL);
            pst.setString(1, selectedItem);
            pst.setString(2, kodeSup); // Gunakan kodeSup yang sudah di-set dari combox_supplierActionPerformed

            res = pst.executeQuery();

            if (res.next()) {
                int harga = res.getInt("harga");
                txt_nominal.setText(formatRupiah(harga)); // Format harga dan set ke txt_nominal
            } else {
                txt_nominal.setText(""); // Kosongkan jika barang tidak ditemukan untuk supplier ini
                System.err.println("Barang '" + selectedItem + "' tidak ditemukan untuk supplier '" + kodeSup + "'.");
            }

            // Panggil calculateTotal() di sini agar total segera diperbarui setelah nominal berubah
            calculateTotal();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error database saat memuat barang: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            System.err.println("SQL Error in combox_barangActionPerformed: " + ex.getMessage());
            txt_nominal.setText("");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Driver database tidak ditemukan.", "Error Driver", JOptionPane.ERROR_MESSAGE);
            System.err.println("Class Not Found Error in combox_barangActionPerformed: " + ex.getMessage());
            txt_nominal.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan tak terduga saat memilih barang: " + e.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
            System.err.println("General Error in combox_barangActionPerformed: " + e.getMessage());
            txt_nominal.setText("");
        } finally {
            try {
                if (res != null) res.close();
                if (pst != null) pst.close();
                if (kon != null) kon.close();
            } catch (SQLException closeEx) {
                System.err.println("Error closing resources in combox_barangActionPerformed: " + closeEx.getMessage());
            }
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
            subtotalToDelete = parseRupiah(String.valueOf(tableModel.getValueAt(selectedRow, 8)));
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
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_kembali;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel_transaksi;
    private javax.swing.JTextField txt_kuantitas;
    private javax.swing.JTextField txt_nominal;
    private javax.swing.JTextField txt_total;
    private javax.swing.JTextField txt_transaksi;
    // End of variables declaration//GEN-END:variables
}
