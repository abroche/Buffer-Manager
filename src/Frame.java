/**
 * Holds one file (one block)
 */
public class Frame {
    private byte[] content;

    private boolean dirty;
    private boolean pinned;
    private int blockId;

    private final int recordSize = 40;

    public Frame(){

    }

    public void initialize(byte[] content, boolean dirty, boolean pinned, int blockId){
        setContent(content);
        setDirty(dirty);
        setPinned(pinned);
        setBlockId(blockId);
    }


    public String getRecord(int recordNumber){
        int start = (recordNumber-1) * recordSize;

        byte[] recordBytes = new byte[recordSize];

        System.arraycopy(getContent(), start, recordBytes, 0, recordSize);

        String recordContent = new String(recordBytes);

        return recordContent;
    }

    public void updateRecord(int recordNumber, String newContent){
        //change the dirty flag as well
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


}
