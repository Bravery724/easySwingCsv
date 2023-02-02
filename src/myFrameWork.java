import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import static java.util.Arrays.deepToString;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class myFrameWork {

    //Frame Panel Label Button Settings
    private JFrame myFrame = new JFrame();
    private JPanel northPanel = new JPanel();
    private JPanel northWestPanel = new JPanel();
    private JPanel northEestPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    private JLabel fileName = new JLabel("File name: ");
    private JLabel colRowCount = new JLabel("                       ");
    private JTextField nameText = new JTextField(20);
    private JTable csvTable = new JTable();
    private JButton importFile = new JButton("Choose A file to Read");
    private JButton fileEdit = new JButton("Edit");
    private JButton fileSave = new JButton("Save as CSV");
    private JButton resetTable = new JButton("Reset");
    private JButton confirmTable = new JButton("Confirmed");
    private JButton addRow = new JButton("+row");
    private JButton delRow = new JButton("-row");
    private JButton addCol = new JButton("+col");
    private JButton delCol = new JButton("-col");
    /*
    private boolean editBool = false;
    private boolean chooseBool = true;
    private boolean confirmBool = false;
    private boolean isEditable = false;
    */



    private JFileChooser filechooser = new JFileChooser();

    private DefaultTableModel model = new DefaultTableModel();
    private List<String[]> rows;
    private String[] columnNames;
    private boolean chooseBool;
    private boolean confirmBool;
    private boolean editSetting;
    private boolean displaySetting;
    private boolean resetBool;

    public myFrameWork() {
        //Frame Size Location setting
        myFrame.setBounds(100, 100, 900, 600);
        myFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        myFrame.add(northPanel, BorderLayout.NORTH);
        northPanel.add(fileName, BorderLayout.WEST);
        northPanel.add(nameText, BorderLayout.CENTER);
        northPanel.add(colRowCount, BorderLayout.EAST);
        northPanel.add(addRow, BorderLayout.EAST);
        northPanel.add(delRow, BorderLayout.EAST);
        northPanel.add(addCol, BorderLayout.EAST);
        northPanel.add(delCol, BorderLayout.EAST);
        myFrame.add(csvTable, BorderLayout.CENTER);
        myFrame.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(importFile, BorderLayout.WEST);
        southPanel.add(fileEdit, BorderLayout.CENTER);
        southPanel.add(confirmTable, BorderLayout.EAST);
        southPanel.add(resetTable, BorderLayout.EAST);
        southPanel.add(fileSave, BorderLayout.EAST);
        myFrame.add(new JScrollPane(csvTable), BorderLayout.CENTER);
        Enable(false,true,false,false,false);

        //Button Action
        importFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int result = filechooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    String filePath = file.getAbsolutePath();
                    try {
                        CSVReader reader = new CSVReader(new FileReader(filePath));
                        rows = reader.readAll();
                        columnNames = rows.get(0);
                        rows.remove(0);
                        model = new DefaultTableModel(columnNames, 0);

                        for (String[] row : rows) {
                            model.addRow(row);
                        }

                        nameText.setText(file.getName());

                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (CsvException ex) {
                        throw new RuntimeException(ex);
                    }

                }
                if (result == JFileChooser.CANCEL_OPTION) {
                    nameText.setText("No File Selected");
                    colRowCount.setText("0 rows 0 columns");
                }
                csvTable.setModel(model);
                colRowCount.setText(model.getRowCount() + " rows " +
                        model.getColumnCount() + " columns");
                Enable(false,true,false, true,true);
            }
        });
        fileEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] tableHeader = new String[model.getColumnCount()];
                for(int i = 0; i < model.getColumnCount(); i++){
                    tableHeader[i] = model.getColumnName(i);
                }
                model.insertRow(0,tableHeader);
                Enable(true,false,true,false,true);
            }

        });
        confirmTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                csvTable.removeEditor();
                for(int i = 0; i < model.getColumnCount(); i++){
                    csvTable.getColumnModel().getColumn(i).setHeaderValue(model.getValueAt(0,i));
                }
                model.removeRow(0);
                csvTable.getTableHeader().repaint();




                Enable(false,true,false,true,true);
            }
        });
        fileSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fs = new JFileChooser();
                int result = fs.showSaveDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    File file = fs.getSelectedFile();

                    try{
                        CSVWriter writer = new CSVWriter(new FileWriter(file));
                        List<String[]> writeRows = new ArrayList<String[]>();
                        TableModel modelSave = csvTable.getModel();

                        for (int i = 0; i < modelSave.getRowCount(); i++){
                            String[] writeRow = new String[modelSave.getColumnCount()];
                            String[] initRow = new String[modelSave.getColumnCount()];
                            for(int j = 0; j < writeRow.length; j++){
                                if(i == 0){
                                    initRow[j] = modelSave.getColumnName(j);
                                }
                                writeRow[j] = (String) modelSave.getValueAt(i,j);
                            }
                            if(i == 0){
                                writeRows.add(initRow);
                            }
                            writeRows.add(writeRow);
                        }
                        writer.writeAll(writeRows);
                        writer.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });
        resetTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setColumnCount(0);
                model.setRowCount(0);
                for (String col : columnNames){
                    model.addColumn(col);
                }
                for (String[] row : rows) {
                    model.addRow(row);
                }
                colRowCount.setText(model.getRowCount() + " rows " +
                        model.getColumnCount() + " columns");
            }
        });
        addCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setColumnCount(model.getColumnCount()+1);
                colRowCount.setText(model.getRowCount() + " rows " +
                        model.getColumnCount() + " columns");
            }
        });
        delCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(model.getColumnCount()-1 <0){
                    return;
                }
                model.setColumnCount(model.getColumnCount()-1);
                colRowCount.setText(model.getRowCount() + " rows " +
                        model.getColumnCount() + " columns");
            }
        });
        addRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(model.getRowCount()+1);
                colRowCount.setText(model.getRowCount() + " rows " +
                        model.getColumnCount() + " columns");
            }
        });
        delRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(model.getRowCount()-1 <0){
                    return;
                }
                model.setRowCount(model.getRowCount()-1);
                colRowCount.setText(model.getRowCount() + " rows " +
                        model.getColumnCount() + " columns");
            }
        });
        nameText.setEditable(false);
        myFrame.setVisible(true);
    }
    public void Enable(boolean editSetting, boolean chooseBool, boolean confirmBool,
                       boolean displaySetting, boolean resetBool){

        this.editSetting = editSetting;
        this.chooseBool = chooseBool;
        this.confirmBool = confirmBool;
        this.displaySetting = displaySetting;

        csvTable.setEnabled(editSetting);
        csvTable.getTableHeader().setReorderingAllowed(editSetting);
        importFile.setEnabled(chooseBool);
        fileEdit.setEnabled(displaySetting);
        confirmTable.setEnabled(confirmBool);
        resetTable.setEnabled(resetBool);
        fileSave.setEnabled(displaySetting);
        addCol.setEnabled(editSetting);
        delCol.setEnabled(editSetting);
        addRow.setEnabled(editSetting);
        delRow.setEnabled(editSetting);
    }

    public static void main(String[] args) {
        new myFrameWork();
    }
}