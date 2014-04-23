package at.resch.html.components.models;

import java.util.ArrayList;

import at.resch.html.server.Session;

public class TableModel {

	private ColumnDefinition[] definition;

	private ArrayList<TableRow> rows;

	public TableModel(ColumnDefinition[] definition) {
		this.definition = definition;
		rows = new ArrayList<>();
	}

	public void addRow(Object... params) {
		if (params.length != definition.length) {
			Session.logger
					.warn("Element counts doesn't match column definition");
		}
		TableRow row = new TableRow(definition.length);
		int len = (params.length < definition.length ? params.length
				: definition.length);
		for (int i = 0; i < len; i++) {
			TableCell cell = new TableCell();
			cell.setColumn(i);
			cell.setRow(rows.size());
			cell.setValue(params[i]);
			row.getCells()[i] = cell;
		}
		rows.add(row);
	}

	public String[] getHeaders() {
		String[] headers = new String[definition.length];
		for (int i = 0; i < definition.length; i++) {
			headers[i] = definition[i].getColumnName();
		}
		return headers;
	}

	public String[][] getData() {
		String[][] data = new String[rows.size()][definition.length];
		for (int i = 0; i < rows.size(); i++) {
			TableRow row = rows.get(i);
			for (int j = 0; j < definition.length; j++) {
				TableCell cell = row.getCells()[j];
				ValueConverter converter = definition[j].getConverter();
				if (cell != null) {
					data[i][j] = converter.convert(cell.getValue());
				} else {
					data[i][j] = "-NULL-";
				}
			}
		}
		return data;
	}

}
