import java.io.*;
import java.util.Arrays;

/**
 * Represents the entire available buffer
 */
public class BufferPool {

    private Frame[] buffers;
    private int lastEvicted;

    private int bufferLen;

    public BufferPool() {
    }


    /**
     * Initializes the buffer pool
     * @param bufferSize
     */
    public void initialize(int bufferSize) {
        this.buffers = new Frame[bufferSize];
        this.lastEvicted = -1;
        this.bufferLen = bufferSize;

        //Initialize each frame in the buffer
        for (int i = 0; i < buffers.length; i++) {
            Frame f = new Frame();
            f.initialize();
            buffers[i] = f;
        }
    }


    /**
     * Unpins the specified block
     * @param blockId
     */
    public String UNPIN(int blockId) {
        int slot = isInBufferPool(blockId);

        if (slot != -1) { //already in the buffer pool
            if (buffers[slot].isPinned()) {
                buffers[slot].setPinned(false);
                return "File " + blockId + " is unpinned in frame "+ (slot+1) +"; Frame "+ (slot+1) +" was not already unpinned";
            } else {
                return "File "+blockId+" in frame "+(slot+1)+" is unpinned; Frame was already unpinned";
            }
        } else { // not in memory
            return ("The corresponding block " + blockId + " cannot be unpinned because it is not in memory.");
        }
    }


    /**
     * Pins the block on the buffer pool
     * @param blockId
     */
    public String PIN(int blockId) {
        int slot = isInBufferPool(blockId);
        if (slot != -1) { //already in the buffer pool
            if (!buffers[slot].isPinned()) {
                buffers[slot].setPinned(true);
                return "File " + blockId + " pinned in Frame " + (slot+1) + "; Not already pinned";
            } else {
                return "File " + blockId + " pinned in Frame " + (slot+1) + "; Already pinned";
            }
        } else { // not in memory
            int emptySlot = getEmptyFrame();
            int[] res = loadFrame(blockId);
            if (res[0] != -1) { //there is an empty frame or there is an unpinned slot
                slot = res[1]; //frame it was placed in
                int id =  res[2]; //id that was evicted
                buffers[slot].setPinned(true);
                return "File " + blockId + " pinned in Frame " + (slot+1) + "; Not already pinned; Evicted file " + id + " from Frame " + (slot+1);
            } else {
                return "The corresponding block " + blockId + " cannot be pinned because the memory buffers are full";
            }
        }


    }

    /**
     * Sets the content of the record to the new content
     * @param recordNumber is the specified record to be changed
     * @param newContent is a string of 40 bytes to update the record with
     */
    public String SET(int recordNumber, String newContent) {
        int fileNumber = Math.ceilDiv(recordNumber, 100);
        int slot = isInBufferPool(fileNumber);

        if (slot == -1) { //not in memory
            int emptySlot = getEmptyFrame();
            int[] res = loadFrame(fileNumber); //load from disk
            if(res[0] != -1) { // if has been loaded successfully
                slot = res[1]; //slot it was placed in
                int evictId = res[2]; //block that was evicted
                Frame setFrame = buffers[slot];
                setFrame.updateRecord(recordNumber, newContent);
                setFrame.setDirty(true);
                if(emptySlot != -1) {
                    return "Write was successful; Brought File " + fileNumber + " from disk; Placed in Frame " + (slot + 1);
                }
                else {
                    return "Write was successful; Brought File " + fileNumber + " from disk; Placed in Frame " + (slot + 1)+ "; Evicted file " + evictId + " from Frame " + (slot+1);
                }
            }
            return "The corresponding block #1 cannot be accessed from disk because the memory buffers are full; Write was unsuccessful";
        } else { // in memory
            buffers[slot].updateRecord(recordNumber, newContent);
            buffers[slot].setDirty(true);
            return "Write was successful; File "+fileNumber+" already in memory; Located in Frame "+ (slot+1);
        }
    }

    /**
     * Gets the specified record
     * @param recordNumber is the record to be returned
     * @return the contents of the record
     */

    public String GET(int recordNumber) {
        int fileNumber = Math.ceilDiv(recordNumber, 100);
        int slot = isInBufferPool(fileNumber); // check if file is in the buffer pool

        if (slot != -1) { //is in memory
            return buffers[slot].getRecord(recordNumber) + "; File " + fileNumber + " already in memory; Located in Frame " + (slot + 1);
        } else { //is not in memory
            int emptySlot = getEmptyFrame();
            int[] res = loadFrame(fileNumber);
            if(res[0] == -1){ // no unpinnable slots
                return "The corresponding block #" + fileNumber + " cannot be accessed from disk because the memory buffers are full";
            }
            int unpinnedSlot = res[1]; // slot it was placed in
            int evictedId = res[2]; // block that was evicted
            if(emptySlot != -1){
                return buffers[unpinnedSlot].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (unpinnedSlot + 1);
            }

            return buffers[unpinnedSlot].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (unpinnedSlot + 1) + "; Evicted file " + evictedId + " from frame " + (unpinnedSlot + 1);

        }
    }


    /**
     * Loads a frame into the buffer pool. This function assumes it is
     * not in memory (i.e., does not check if the block is in memory)
     * @param fileNumber is the blockId
     * @return
     */
    public int[] loadFrame(int fileNumber) {
        int[] res = new int[3];
        int slot = findSlot(); // check if file is in the buffer pool

        if(slot == -1){ //no available slot
            res[0] = -1;
            return res;
        }

        Frame frameToEvict = buffers[slot];
        int evictedId = frameToEvict.getBlockId();


        byte[] newContent = readFile(fileNumber).getBytes(); //copy new contents
        if (frameToEvict.isDirty()) { // if it is dirty
            //write file content back
            writeFile(frameToEvict.getBlockId(), frameToEvict.getContent());
            //overwrite
            frameToEvict.setDirty(false);
        }
        frameToEvict.setContent(newContent);
        frameToEvict.setBlockId(fileNumber);

        res[0] = fileNumber;
        res[1] = slot;
        res[2] = evictedId;
        return res;
    }

    /**
     * Finds an available slot in the buffer pool
     * @return the position of the slot
     */
    public int findSlot(){
        int emptySlot = getEmptyFrame();
        int unpinnedSlot = getUnpinnedFrame();
        if (emptySlot != -1) { //there is an empty frame
            return emptySlot;
        }
        else{
            if(unpinnedSlot != -1){ setLastEvicted(unpinnedSlot); } // no empty frames but there is an unpinned slot
            return unpinnedSlot; //-1 if no unpinned slots
        }
    }


    /**
     * Writes the new content to the file
     * @param fileNumber also the blockId
     * @param newContent the new content as a byte array
     */
    public void writeFile(int fileNumber, byte[] newContent) {
        String filename = "Project1/F" + fileNumber + ".txt";

        try {
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(new String(newContent));
            bufferedWriter.newLine();

            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the specified file
     * @param fileNumber the blockId that is meant to be read
     * @return the contents of the file
     */
    public String readFile(int fileNumber) {
        String filename = "Project1/F" + fileNumber + ".txt";
        String blockContent = null;
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            blockContent = bufferedReader.readLine();

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockContent;
    }


    /**
     * Determines whether blockId is in the buffer pool
     * @param blockId
     * @return slot number in the array holding this block otherwise -1
     */
    public int isInBufferPool(int blockId) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockId() == blockId) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Gets location of empty frame
     * @return the slot of the empty frame
     */
    public int getEmptyFrame() {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockId() == -1) {
                return i;
            }
        }
        return -1;
    }


    /**
     * If the buffer pool is full, then check for unpinned frames
     * starting from the Frame following the last evicted frame
     * Works in a circular fashion
     * @return the slot of the removable frame
     */
    public int getUnpinnedFrame() {
        int startFrame = (lastEvicted+1) % bufferLen;

        for (int i = startFrame; i < bufferLen + startFrame; i++) {
            int idx = i % bufferLen;
            if (!buffers[idx].isPinned()) {
                return idx;
            }
        }
        return -1;
    }


    public void setLastEvicted(int idx){
        lastEvicted = idx;
    }

    @Override
    public String toString() {
        return "BufferPool{" +
                "buffers=" + Arrays.toString(buffers) +
                '}';
    }
}
