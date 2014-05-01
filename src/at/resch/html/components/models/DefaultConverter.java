package at.resch.html.components.models;

public class DefaultConverter implements ValueConverter {

	@Override
	public String convert(Object o) {
		return o == null ? "NULL" : o.toString();
	}

}