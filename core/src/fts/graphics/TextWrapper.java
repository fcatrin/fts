package fts.graphics;

import java.util.ArrayList;
import java.util.List;

public class TextWrapper {
	private Point size = new Point();
	private List<String> lines = new ArrayList<String>();
	
	Canvas canvas;
	String text;
	
	int position = 0;
	int advance  = 0;
	
	public TextWrapper(Canvas canvas, String text) {
		this.canvas = canvas;
		this.text = text;
	}
	
	public Point wrap(int width, int maxLines) {
		String textWrap = "";
		do {
			String nextWord = getNextWord();
			Point nextSize = canvas.getTextSize((textWrap + nextWord).trim());
			if (nextSize.x > width) {
				addLine(textWrap);
				textWrap = "";
			} else {
				textWrap += nextWord;
				Point lastSize = canvas.getTextSize(textWrap.trim());
				size.x = Math.max(lastSize.x, size.x);
				size.y = Math.max(lastSize.y, size.y);
				position += advance + 1;
			}
		} while (position < text.length() && (maxLines < 0 || lines.size() <= maxLines));
		if (!textWrap.trim().isEmpty() && (maxLines < 0 || lines.size() <= maxLines)) {
			addLine(textWrap);
		}
		
		size.y = lines.size() * size.y + 2 * (lines.size() * 2); // TODO get inter-line space
		return size;
	}
	
	private void addLine(String line) {
		lines.add(line.trim());
	}
	
	private boolean isSpacer(String s) {
		return s.equals(" ") || s.equals("\t") || s.equals("\n");
	}
	
	private String getNextWord() {
		advance = 0;
		String word = "";
		while (position + advance < text.length()) {
			int index = position + advance;
			String c = text.substring(index, index+1);
			word += c;
			if (isSpacer(c)) {
				return word;
			}
			advance++;
		}
		return word;
	}
}
