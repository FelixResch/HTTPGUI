package at.resch.html.components.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import at.resch.html.server.Session;

public class SQLTableModel extends TableModel {

	public SQLTableModel(String sql, String connection) {
		super();
		Connection con = (Connection) Session.getCurrent().get(connection);
		try {
			Statement stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet res = stat.executeQuery(sql);
			ResultSetMetaData meta = res.getMetaData();
			ColumnDefinition[] cols = new ColumnDefinition[meta.getColumnCount()];
			for(int i = 0; i < cols.length; i++) {
				cols[i] = new ColumnDefinition(meta.getColumnName(i + 1));
			}
			this.definition = cols;
			res.first();
			do {
				TableRow row = new TableRow(cols.length);
				for(int i = 0; i < cols.length; i++) {
					TableCell cell = new TableCell();
					cell.setColumn(i);
					cell.setRow(res.getRow());
					cell.setValue(res.getString(i + 1));
					row.getCells()[i] = cell;
				}
				this.rows.add(row);
			} while (res.next());
		} catch (SQLException e) {
			Session.logger.warn("Couldn't create Data Table", e);
		}
		
	}

}
