package at.resch.html.components.models;

public class TableRow {

	private TableCell[] cells;

	public TableRow(int columns) {
		cells = new TableCell[columns];
	}

	public TableCell[] getCells() {
		return cells;
	}

}
