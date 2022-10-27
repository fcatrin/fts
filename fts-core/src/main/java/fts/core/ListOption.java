package fts.core;

public class ListOption {
	private String code;
	private String text;
	private String value;
	
	public ListOption(String code, String text) {
		this(code, text, null);
	}
	
	public ListOption(String code, String text, String value) {
		this.code = code;
		this.text = text;
		this.value = value;
	}
	
	public boolean is(String code) {
		return code!=null && code.equals(this.code);
	}

	public String getCode() {
		return code;
	}

	public String getText() {
		return text;
	}

	public String getValue() {
		return value;
	}

	
}
