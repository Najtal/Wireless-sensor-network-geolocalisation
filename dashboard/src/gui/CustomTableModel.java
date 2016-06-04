package gui;

import model.AnchorModel;
import ucc.AnchorDTO;

import java.io.File;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

/**
 * The Class CustomTableModel contains methods to allow the JTable component to
 * get and display data about the files in a specified directory. It represents
 * a table with six columns: filename, size, modification date, plus three
 * columns for flags: directory, readable, writable.
 * 
 * @author ashraf_sarhan
 */
@SuppressWarnings("serial")
public class CustomTableModel extends AbstractTableModel {

	private String[] columnNames = TableColumn.getNames();
	private Class<?>[] columnClasses = Constants.COLUMN_CLASSES;
	private AnchorModel anchorModel;

	// This table model works for any one given directory
	public CustomTableModel(AnchorModel anchorModel) {
		this.anchorModel = anchorModel;
	}

	/**
	 * Returns a constant columns number for this model
 	 */
	public int getColumnCount() {
		return Constants.COLUMN_CLASSES.length;
	}

	/**
	 * Returns the motes of the network
 	 */
	public int getRowCount() {
		return anchorModel.getAnchorBy().size();
	}

	// Returns the name of the given column index
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
	}

	// Returns the value of each cell
	public Object getValueAt(int row, int col) {

		AnchorDTO anchor = anchorModel.getAnchorByPositionFirstToLast(row);

		TableColumn tableColumn = TableColumn.fromIndex(col);
		switch (tableColumn) {
		case TYPE:
			return (anchor.getPosx() == 0 && anchor.getPosy() == 0) ? "GateWay" : "Anchor";
		case NAME:
			return anchor.getId();
		case POSX:
			return anchor.getPosx();
		case POSY:
			return anchor.getPosy();
		default:
			return null;
		}
	}

}
