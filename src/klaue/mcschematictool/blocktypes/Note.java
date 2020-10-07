package klaue.mcschematictool.blocktypes;

/**
 * A Note is a block that has a pitch
 * @author klaue
 *
 */
public class Note extends Block {
	/**
	 * Initializes the Note.
	 * @param pitch The pitch (number of strikes) of this note block, max 24
	 */
	public Note(byte pitch) {
		super((short)25, pitch);
		if (pitch > 24 || pitch < 0) {
			throw new IllegalArgumentException("Note blocks can only have a pitch of 0-24, not " + pitch);
		}
		this.type = Type.NOTE;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", pitch " + this.data;
	}

	/**
	 * Gets the pitch, i.e. number of strikes this block has received
	 * @return the pitch
	 */
	public byte getPitch() {
		return this.data;
	}

	/**
	 * Sets the pitch, i.e. number of strikes this block has received
	 * @param pitch the pitch
	 */
	public void setPitch(byte pitch) {
		if (pitch > 24 || pitch < 0) {
			throw new IllegalArgumentException("Note blocks can only have a pitch of 0-24, not " + pitch);
		}
		this.data = pitch;
	}
	
	@Override
	public void setData(byte data) {
		setPitch(data);
	}
}
