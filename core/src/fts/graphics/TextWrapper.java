package fts.graphics;

import java.util.ArrayList;
import java.util.List;

public class TextWrapper {
	private Point size = new Point();
	private List<String> lines = new ArrayList<String>();
	private List<TextMetrics> lineMetrics = new ArrayList<TextMetrics>();
	
	Canvas canvas;
	String text;
	
	int position = 0;
	int advance  = 0;
	
	int lineSeparator = 2;
	
	public TextWrapper(Canvas canvas, String text) {
		this.canvas = canvas;
		this.text = text;
	}
	
	public Point wrap(int width, int maxLines) {
		lines.clear();
		lineMetrics.clear();
		
		TextMetrics oneLineMetrics = canvas.getTextSize(text);
		if (oneLineMetrics.width <= width) {
			addLine(text, oneLineMetrics);
			size.x = oneLineMetrics.width;
			size.y = oneLineMetrics.height;
			return size;
		}
		
		position = 0;
		advance  = 0;
		
		String textWrap = "";
		boolean hasMoreLines = false;
		TextMetrics lastSize = null;
		do {
			String nextWord = getNextWord();
			TextMetrics nextSize = canvas.getTextSize((textWrap + nextWord).trim());
			if (nextSize.width > width) { // TODO handle case where text just don't fi
				if (lastSize == null) {
					advance = 0;
					do {
						int index = position + advance;
						String nextChar = text.substring(index, index + 1);
						nextSize = canvas.getTextSize(textWrap + nextChar);
						if (nextSize.width > width) {
							if (lastSize == null) return size; // not enough space for any text, abort
							addLine(textWrap, lastSize);
							break;
						}
						lastSize = nextSize;
						advance++;
					} while (position + advance < text.length());
				} else {
					addLine(textWrap, lastSize);
				}
				size.x = Math.max(lastSize.width, size.x);
				lastSize = null;
				textWrap = "";
			} else {
				textWrap += nextWord;
				lastSize = nextSize;
				position += advance + 1;
			}
			hasMoreLines = maxLines < 0 || lines.size() <= maxLines;
		} while (position < text.length() && hasMoreLines);
		
		if (!textWrap.trim().isEmpty() && hasMoreLines) {
			TextMetrics nextSize = canvas.getTextSize(textWrap.trim());
			addLine(textWrap, nextSize);
		}

		return size;
	}
	
	private void addLine(String line, TextMetrics metrics) {
		lines.add(line.trim());
		lineMetrics.add(metrics);
		size.y += metrics.height + (size.y == 0 ? 0 : lineSeparator);
		System.out.println("add line '" + line.trim()  + "'height: " + metrics.height + " size.y=" + size.y);
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
	
	public List<TextMetrics> getLineMetrics() {
		return lineMetrics;
	}

	public int getLineSeparator() {
		return lineSeparator;
	}
	
	
}
