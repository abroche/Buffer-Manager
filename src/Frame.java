
/**
 * Holds one file (one block)
 */
public class Frame {
    private int recordSize;
    private byte[] content;

    private boolean dirty;
    private boolean pinned;
    private int blockId;


    public Frame() {
    }


    /**
     * Initializes a Frame
     */
    public void initialize() {
        content = new byte[100 * recordSize];
        dirty = false;
        pinned = false;
        blockId = -1;
        recordSize = 40;
    }


    /**
     * Gets the specified record
     *
     * @param recordNumber
     * @return
     */
    public String getRecord(int recordNumber) {
        int start = ((recordNumber - 1) % 100) * recordSize; //start location

        // byte array of size recordSize
        byte[] recordBytes = new byte[recordSize];

        System.arraycopy(getContent(), start, recordBytes, 0, recordSize);

        return new String(recordBytes);
    }


    /**
     * Replaces the content of the specified record
     *
     * @param recordNumber
     * @param newContent   is a string of 40 bytes
     */
    public void updateRecord(int recordNumber, String newContent) {
        int start = ((recordNumber - 1) % 100) * recordSize; //start location

        byte[] newBytes = newContent.getBytes();

        for (int i = 0; i < newBytes.length && i < recordSize; i++) {
            content[start + i] = newBytes[i];
        }
        setDirty(true);
    }


    public boolean isDirty() {
        return dirty;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }


    @Override
    public String toString() {
        return "Frame{" +
                //"content=" + Arrays.toString(content) +
                "dirty=" + dirty +
                ", pinned=" + pinned +
                ", blockId=" + blockId +
                '}';
    }
}
