package com.arecmetafora.jsdroid.debugger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * The base of all debug messages.
 */
@JsonAdapter(ProtocolMessage.class)
class ProtocolMessage extends TypeAdapter<ProtocolMessage> {

	/**
	 * The message id.
	 */
	private int id;

	/**
	 * The command that the client or server must execute.
	 */
	private DebugCommand command;

	/**
	 * The argument to send / received.
	 */
	private Object arguments;

	/**
	 * Creates a new debug message.
	 *
	 * @param command The debug command.
	 * @param arguments The command arguments.
	 * @param id The message id.
	 */
	ProtocolMessage(DebugCommand command, Object arguments, int id) {
		this.command = command;
		this.arguments = arguments;
		this.id = id;
	}

	/**
	 * @return The message id.
	 */
	int getId() {
		return id;
	}

	/**
	 * @return The argument to send / received.
	 */
	Object getArguments() {
		return this.arguments;
	}

	/**
	 * @return The command that the client or server must execute.
	 */
	DebugCommand getCommand() {
		return this.command;
	}

	@Override
	public ProtocolMessage read(JsonReader in) throws IOException {
		Gson gson = new GsonBuilder().create();

		in.beginObject();

		int id = 0;
		DebugCommand cmd = null;
		Object args = null;

		while(in.hasNext()) {
			switch(in.nextName()) {
				case "id":
					id = in.nextInt();
					break;
				case "command":
					cmd = gson.fromJson(in, DebugCommand.class);
					break;
				case "arguments":
					args = gson.fromJson(in, cmd.getType());
					break;
			}
		}

		in.endObject();

		return new ProtocolMessage(cmd, args, id);
	}

	@Override
	public void write(JsonWriter out, ProtocolMessage msg) throws IOException {
		Gson gson = new GsonBuilder().create();

		out.beginObject();

		out.name("id").value(msg.getId());

		out.name("command");
		gson.toJson(msg.getCommand(), DebugCommand.class, out);

		out.name("arguments");
		gson.toJson(msg.getArguments(), msg.getCommand().getType(), out);

		out.endObject();
	}

}
