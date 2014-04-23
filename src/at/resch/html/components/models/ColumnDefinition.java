package at.resch.html.components.models;

public class ColumnDefinition {
	private String columnName;
	private ValueConverter converter = new DefaultConverter();

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public ValueConverter getConverter() {
		return converter;
	}

	public void setConverter(ValueConverter converter) {
		this.converter = converter;
	}

	public ColumnDefinition(String columnName, ValueConverter converter) {
		super();
		this.columnName = columnName;
		this.converter = converter;
	}

	public ColumnDefinition(String columnName) {
		super();
		this.columnName = columnName;
	}

}
