package fts.graphics;

import java.util.ArrayList;
import java.util.List;

public class TextWrapper {
	private Point size = new Point();
	private List<String> lines = new ArrayList<String>();
	private List<Integer> lineWidths = new ArrayList<Integer>();
	
	Canvas canvas;
	String text;
	
	int position = 0;
	int advance  = 0;
	
	int lineHeight = 0;
	int lineSeparator = 2;
	
	public TextWrapper(Canvas canvas, String text) {
		this.canvas = canvas;
		this.text = text;
	}
	
	public Point wrap(int width, int maxLines) {
		lines.clear();
		
		Point oneLineSize = canvas.getTextSize(text);
		if (oneLineSize.x <= width) {
			addLine(text, oneLineSize.x);
			size = oneLineSize;
			lineHeight = size.y;
			return size;
		}
		
		position = 0;
		advance  = 0;
		
		String textWrap = "";
		int lastWidth = 0;
		do {
			String nextWord = getNextWord();
			Point nextSize = canvas.getTextSize((textWrap + nextWord).trim());
			if (nextSize.x > width) { // TODO handle case where text just don't fit
				addLine(textWrap, lastWidth);
				textWrap = "";
			} else {
				textWrap += nextWord;
				Point lastSize = canvas.getTextSize(textWrap.trim());
				lastWidth = lastSize.x;
				size.x = Math.max(lastSize.x, size.x);
				size.y = Math.max(lastSize.y, size.y);
				position += advance + 1;
			}
		} while (position < text.length() && (maxLines < 0 || lines.size() <= maxLines));
		if (!textWrap.trim().isEmpty() && (maxLines < 0 || lines.size() <= maxLines)) {
			Point nextSize = canvas.getTextSize(textWrap.trim());
			addLine(textWrap, nextSize.x);
		}

		lineHeight = size.y;
		size.y = lines.size() * lineHeight + lineSeparator * (lines.size()-1); // TODO get inter-line space
		return size;
	}
	
	private void addLine(String line, int width) {
		lines.add(line.trim());
		lineWidths.add(width);
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
	
	public Point getSize() {
		return size;
	}
	
	public List<String> getLines() {
		return lines;
	}

	public List<Integer> getLineWidths() {
		return lineWidths;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public int getLineSeparator() {
		return lineSeparator;
	}
	
	
}
