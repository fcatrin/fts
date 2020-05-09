package fts.graphics;

import fts.core.Component;

public abstract class Drawable extends Component {
	private boolean state[] = new boolean[10];
	protected Rectangle padding = new Rectangle();
	protected Rectangle bounds = new Rectangle();
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds.clone();
	}
	
	public void setPadding(Rectangle padding) {
		this.padding = padding.clone();
	}

	public void setState(int index, boolean value) {
		state[index] = value;
	}
	
	protected String createFilterMask() {
		String mask = "";
		for (boolean stateValue : state) {
			mask = mask + (stateValue?"1":0);
		}
		return mask;
	}
	
	protected boolean filterMatch(String filter, String mask) {
		for(int i=0; i<filter.length() && i<mask.length(); i++) {
			char f = filter.charAt(i);
			if (f == '#') continue;
			
			char m = mask.charAt(i);
			if (f!=m) return false;
			
		}
		return true;
	}
	
	public void setState(boolean[] state) {
		for(int i=0; i<this.state.length && i<state.length; i++) {
			this.state[i] = state[i];
		}
	}
	
	public abstract void draw(Canvas canvas);
	public void destroy(){};

}
