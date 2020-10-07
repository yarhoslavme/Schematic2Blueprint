package klaue.mcschematictool.blocktypes;



/**
 * A command block can have a command and a signal strength
 * @author klaue
 *
 */
public class CommandBlock extends Block {
	private String command = null;
	private int signalStrength = 0;
	
	/**
	 * initializes the command block with no command
	 */
	public CommandBlock() {
		this(null, 0);
	}
	
	/**
	 * initializes the command block
	 * @param command 
	 * @param signalStrength the strength of the analog signal output by redstone comparators attached to this command block
	 */
	public CommandBlock(String command, int signalStrength) {
		super((short)137);
		this.type = Type.COMMANDBLOCK;
		
		setCommand(command);
		setSignalStrength(signalStrength);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", strength: " + this.signalStrength + (this.command.isEmpty() ? "" : ", command: " + this.command);
	}

	/**
	 * gets the command of this command block
	 * @return the command
	 */
	public String getCommand() {
		return this.command;
	}

	/**
	 * sets the command of this command block
	 * @param command the command to set or null
	 */
	public void setCommand(String command) {
		this.command = command;
		if (this.command == null) this.command = "";
	}

	/**
	 * gets the signal strength (the strength of the analog signal output by redstone comparators attached to this command block)
	 * @return the signalStrength
	 */
	public int getSignalStrength() {
		return this.signalStrength;
	}

	/**
	 * sets the signal strength (the strength of the analog signal output by redstone comparators attached to this command block)
	 * @param signalStrength the signalStrength to set
	 */
	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}
	
}
