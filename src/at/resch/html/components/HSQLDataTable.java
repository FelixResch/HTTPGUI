package at.resch.html.components;

import at.resch.html.components.models.SQLTableModel;

public class HSQLDataTable extends HDataTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6771010439661718102L;

	public HSQLDataTable(String id, String sql, String connection) {
		super(id, new SQLTableModel(sql, connection));
	}

}
