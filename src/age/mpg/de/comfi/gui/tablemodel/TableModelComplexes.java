/*
 * Copyright (C) 2012-2013 Falko Hofmann Max Planck Institute for Biology
 * of Ageing, Cologne (MPI-age)
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package age.mpg.de.comfi.gui.tablemodel;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import age.mpg.de.comfi.utilityobjects.FoundProteinComplex;

//JTableModel to manage the COMFI Results
public class TableModelComplexes extends AbstractTableModel {

	private List<FoundProteinComplex> datalist;
	
	private	final String[] columns = {"Protein Complex","NodeId","# Members", "Homology", "Organism", "Select Node"};
	
	
	public static final int COMPLEX_NAME_COLUMN = 0;
	public static final int COMPLEX_ID_COLUMN = 1;
	public static final int NUMBER_OF_NODES_COLUMN = 2;
	public static final int HOMOLOGY_COLUMN = 3;
	public static final int ORGANISM_COLUMN = 4;
	public static final int SELECT_COLUMN = 5;
	public static final int NUMBER_COLUMNS = 6;

		
	public TableModelComplexes(List<FoundProteinComplex> list){
		this.datalist = list;
	}
	
	@Override
	public int getColumnCount() {
		return NUMBER_COLUMNS;
	}

	@Override
	public String getColumnName(int columnIndex) {
		  return columns[columnIndex];
	}
	
	@Override
	public int getRowCount() {
		return datalist.size();
	}
	
	@Override
	public Class getColumnClass(int columnIndex) {
        return getValueAt(0,columnIndex).getClass();
    }
	
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == SELECT_COLUMN)
			return true;
		else
			return false;
	}
	
	
	public void setValueAt(Object value, int rowIndex, int columnIndex){
		FoundProteinComplex complex = datalist.get(rowIndex);
		if (columnIndex == SELECT_COLUMN){
			complex.setSelect((Boolean) value);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
	
	
	
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		FoundProteinComplex result = datalist.get(rowIndex);	
		 
		switch (columnIndex){
			case COMPLEX_NAME_COLUMN:
				return result.getProteinComplexName();
			case COMPLEX_ID_COLUMN:
				return result.getCyGroupID();
		    case NUMBER_OF_NODES_COLUMN:
		    	return result.getNumberOfMembers();
		    case HOMOLOGY_COLUMN:
		    	return result.isHomologue();
		    case ORGANISM_COLUMN:
		    	return result.getOrganism();
		    case SELECT_COLUMN:
		    	return result.isSelect();
		    default:
		    	return null;
		}
	}
		
}
