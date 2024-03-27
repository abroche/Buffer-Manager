/**
 * Holds one file (one block)
 */
public class Frame {
    private byte[] content;

    private boolean dirty;
    private boolean pinned;
    private int blockId;

    private int recordSize;

    public Frame(){

    }

    public void initialize(){

    }


    public String getRecord(int recordNumber){
        return "1";
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
