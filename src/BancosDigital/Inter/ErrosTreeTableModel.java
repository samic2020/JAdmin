/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BancosDigital.Inter;

import java.awt.Color;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author desenvolvimento-pc
 */
public class ErrosTreeTableModel extends AbstractTableModel { 
    private final static String[] COLUMN_NAMES = {"Contrato", "Nome", "Vencimento", "Codigo", "Mensagem"};
    
    private List<BancosErros> bancoBoletaErros;
    
    public ErrosTreeTableModel(List<BancosErros> bancoBoletaErros) {
        super();
        this.bancoBoletaErros = bancoBoletaErros;
        
        UIManager.put("Table.alternateRowColor", new Color(204,204,255));
        UIManager.put("Table.foreground", new Color(0, 0, 0));  
        UIManager.put("Table.selectionBackground", Color.YELLOW);
        UIManager.put("Table.selectionForeground", Color.RED);
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
      return getValueAt(0, column).getClass();
    }

    @Override
    public int getRowCount() {
        return bancoBoletaErros.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String retorno = null;
        switch (columnIndex) {
            case 0: 
                retorno = bancoBoletaErros.get(rowIndex).getContrato().getValue();
                break;
            case 1:
                retorno = bancoBoletaErros.get(rowIndex).getNome().getValue();
                break;
            case 2:
                retorno = bancoBoletaErros.get(rowIndex).getVencimento().getValue();
                break;
            case 3:
                retorno = bancoBoletaErros.get(rowIndex).getCodigo().getValue();
                break;
            case 4:
                retorno = bancoBoletaErros.get(rowIndex).getMessage().getValue();
                break;                
        }
        return retorno;
    }    
}
