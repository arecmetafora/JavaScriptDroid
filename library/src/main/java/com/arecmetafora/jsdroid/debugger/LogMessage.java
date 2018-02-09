package com.arecmetafora.jsdroid.debugger;

/**
 * Debug Log Message.
 */
class LogMessage {

	/**
	 * Log Type.
	 */
	private byte type;
	/**
	 * Message Text.
	 */
	private String message;
	/**
	 * Message source.
	 */
	private String source;
	/**
	 * Message details.
	 */
	private String details;

	/**
	 * Creates a debug evaluate response.
	 *
	 * @param type Log Type.
	 * @param message Message to Log.
	 * @param source String identifying the source of the message.
	 * @param details Additional details.
	 */
	LogMessage(byte type, String message, String source,
		String details) {
		this.type = type;
		this.message = message;
		this.source = source;
		this.details = details;
	}

	/**
	 * @return message details
	 */
	String getDetails() {
		return this.details;
	}

	/**
	 * @return message
	 */
	String getMessage() {
		return this.message;
	}

	/**
	 * @return source of the message
	 */
	String getSource() {
		return this.source;
	}

	/**
	 * @return type of the message
	 */
	byte getType() {
		return this.type;
	}

	/**
	 * Log Type to be used on Message.
	 */
	enum LogType {
		/**
		 * Indicates a log error.
		 */
		ERROR((byte) 1),
		/**
		 * Indicates a warning log.
		 */
		WARNING((byte) 2),
		/**
		 * Indicates a debug log.
		 */
		DEBUG((byte) 4),
		/**
		 * Indicates a trace log.
		 */
		TRACE((byte) 8),
		/**
		 * Indicates a console log.
		 */
		CONSOLE((byte) 16),
		/**
		 * Indicates a console log.
		 */
		TELEMETRY((byte) 32);

		/**
		 * Byte value of the Enum.
		 */
		private final byte value;

		/**
		 * Constructor.
		 *
		 * @param value Byte value of the Enum.
		 */
		LogType(byte value) {
			this.value = value;
		}

		/**
		 * @return Gets the byte value of this ENUM.
		 */
		public byte getValue() {
			return value;
		}
	}
}
