package fts.ui.graphics;

import java.util.ArrayList;
import java.util.List;

import fts.core.Log;

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
	
	private Point processAsSingleLines() {
		String parts[] = text.split("\n");
		for(String part : parts) {
			TextMetrics metrics = canvas.getTextSize(part);
			addLine(part, metrics);
			size.x = Math.max(metrics.width, size.x);
		}
		return size;
	}
	
	public Point wrap(int width, int maxLines) {
		lines.clear();
		lineMetrics.clear();
		
		TextMetrics oneLineMetrics = canvas.getTextSize(text);
		if (oneLineMetrics.width <= width) {
			return processAsSingleLines();
		}
		
		position = 0;
		advance  = 0;
		
		String textWrap = "";
		boolean hasMoreLines = false;
		TextMetrics lastSize = null;
		do {
			String nextWord = getNextWord();
			boolean hasEOL = nextWord.endsWith("\n");
			if (hasEOL) nextWord = nextWord.substring(0, nextWord.length()-1);
			
			String nextLine = (textWrap + nextWord).trim();
			TextMetrics nextSize = canvas.getTextSize(nextLine);
			if (nextSize.width > width) { // not enough space, need to wrap
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
					position += advance;
				} else {
					addLine(textWrap, lastSize);
				}
				size.x = Math.max(lastSize.width, size.x);
				lastSize = null;
				textWrap = "";
			} else if (hasEOL) {
				addLine(nextLine, nextSize);
				size.x = Math.max(nextSize.width, size.x);
				lastSize = null;
				textWrap = "";
				position += advance + 1;
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
		Log.d("TEXTWRAP", "add line " + line.trim() + ", height: " + metrics.height + " size.y=" + size.y);
		lines.add(line.trim());
		lineMetrics.add(metrics);
		size.y += metrics.height + (size.y == 0 ? 0 : lineSeparator);
	}
	
	private boolean isSpacer(String s) {
		return s.equals(" ") || s.equals("\t");
	}
	
	private boolean isEOL(String s) {
		return s.equals("\n");
	}
	
	private String getNextWord() {
		advance = 0;
		String word = "";
		while (position + advance < text.length()) {
			int index = position + advance;
			String c = text.substring(index, index+1);
			word += c;
			if (isSpacer(c) || isEOL(c)) {
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
