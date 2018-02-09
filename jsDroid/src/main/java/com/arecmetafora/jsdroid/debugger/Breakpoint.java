package com.arecmetafora.jsdroid.debugger;

import com.arecmetafora.jsdroid.Utils;

/**
 * Debug breakpoint representation.
 */
public final class Breakpoint {

	/**
	 * Breakpoint id.
	 */
	private int id;

	/**
	 * Name of the file in which the breakpoint will hit.
	 */
	private String fileName;

	/**
	 * Line number in which the breakpoint will hit.
	 */
	private int line;

	/**
	 * Column number in which the breakpoint will hit.
	 */
	private int column;

	/**
	 * JavaScript expression representing the breakpoint condition.
	 */
	private String condition;

	/**
	 * Number of times the breakpoint should be hit before breaking execution.
	 */
	private int hitCount;

	/**
	 * Creates a new breakpoint.
	 *
	 * @param id Breakpoint id.
	 * @param fileName Name of the file in which the breakpoint will hit.
	 * @param line Line number in which the breakpoint will hit.
	 * @param column Line number in which the breakpoint will hit.
	 * @param condition JavaScript expression representing the breakpoint condition.
	 * @param hitCount Number of times the breakpoint should be hit before breaking execution.
	 */
	public Breakpoint(int id, String fileName, int line, int column, String condition, int hitCount) {
		this.id = id;
		this.fileName = fileName;
		this.line = line;
		this.column = column;
		this.condition = condition;
		this.hitCount = hitCount;
	}

	/**
	 * Sets the breakpoint id.
	 *
	 * @param id The breakpoint id.
	 */
	void setId(int id) {
		this.id = id;
	}

	/**
	 * @return The breakpoint id.
	 */
	int getId() {
		return id;
	}

	/**
	 * @return The name of the file in which the breakpoint will hit.
	 */
	String getFileName() {
		return this.fileName;
	}

	/**
	 * @return The line number in which the breakpoint will hit.
	 */
	int getLine() {
		return this.line;
	}

	/**
	 * @return The column number in which the breakpoint will hit.
	 */
	int getColumn() {
		return column;
	}

	/**
	 * @return The JavaScript expression representing the breakpoint condition.
	 */
	String getCondition() {
		return condition;
	}

	/**
	 * @return The number of times the breakpoint should be hit before breaking execution.
	 */
	int getHitCount() {
		return hitCount;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Breakpoint) {
			Breakpoint b = (Breakpoint)obj;
			return Utils.isEqual(b.fileName, fileName)
					&& b.line == line
					&& b.column == column;
		}
		return false;
	}
}
