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
    public void UNPIN(int blockId) {
        int slot = isInBufferPool(blockId);

        if (slot != -1) { //already in the buffer pool
            if (buffers[slot].isPinned()) {
                buffers[slot].setPinned(false);
            } else {
                System.out.println("Pinned flag was already false");
            }

            System.out.println("Frame #" + (slot + 1) + " was unpinned");
        } else { // not in memory
            System.out.println("“The corresponding block <" + blockId + "> cannot be unpinned because it is not in memory");
        }


    }


    /**
     * Pins the block on the buffer pool
     * @param blockId
     */
    public void PIN(int blockId) {
        int slot = isInBufferPool(blockId);

        if (slot != -1) { //already in the buffer pool
            if (!buffers[slot].isPinned()) {
                buffers[slot].setPinned(true);
            } else {
                System.out.println("Pinned flag was already true");
            }
        } else { // not in memory
            int emptySlot = getEmptyFrame();
            int unpinnedSlot = getUnpinnedFrame();
            if (emptySlot != -1 || unpinnedSlot != -1) { //there is an empty frame or there is an unpinned slot
                GET(blockId * 100); //bring to memory
                slot = isInBufferPool(blockId);
                buffers[slot].setPinned(true);
                System.out.println("Frame #" + (slot + 1) + " was pinned");
            } else {
                System.out.println("The corresponding block <" + blockId + "> cannot be pinned because the memory buffers are full");
            }
        }


    }

    /**
     * Sets the content of the record to the new content
     * @param recordNumber is the specified record to be changed
     * @param newContent is a string of 40 bytes to update the record with
     */
    public void SET(int recordNumber, String newContent) {
        int fileNumber = Math.ceilDiv(recordNumber, 100);

        int slot = isInBufferPool(fileNumber);

        if (slot == -1) { //not in memory
            String res = GET(recordNumber); //load from disk
            if(!res.equals("-1")) { // if has been loaded successfully
                slot = isInBufferPool(fileNumber);
                Frame setFrame = buffers[slot];
                setFrame.updateRecord(recordNumber, newContent);
                System.out.println("Write was successful");
                setFrame.setDirty(true);
            }
        } else { // in memory
            buffers[slot].updateRecord(recordNumber, newContent);
            System.out.println("Write was successful");
            buffers[slot].setDirty(true);
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
            System.out.println("Block #" + fileNumber + " is already in memory in Frame #" + (slot + 1));
            return "Record: " + buffers[slot].getRecord(recordNumber);
        } else { //is not in memory
            System.out.println("Block #" + fileNumber + " is not in memory.");
            byte[] newContent = readFile(fileNumber).getBytes(); //copy new contents
            int emptySlot = getEmptyFrame();

            if (emptySlot != -1) { //there is an empty frame
                System.out.println("Found empty frame at Frame #" + (emptySlot + 1));
                System.out.println("I/O was performed to bring the block to disk");
                buffers[emptySlot].setContent(newContent);
                buffers[emptySlot].setBlockId(fileNumber);
                return "Record: " + buffers[emptySlot].getRecord(recordNumber);
            } else { //no empty frames
                int unpinnedSlot = getUnpinnedFrame();
                if (unpinnedSlot != -1) { //there is an unpinned slot
                    Frame frameToEvict = buffers[unpinnedSlot];
                    if (!frameToEvict.isDirty()) { //if not dirty
                        System.out.println("I/O was performed to bring the block to disk");
                        frameToEvict.setContent(newContent); //just overwrite
                    } else {
                        //write file content back
                        writeFile(frameToEvict.getBlockId(), frameToEvict.getContent());
                        System.out.println("I/O was performed. Wrote Block #" + frameToEvict.getBlockId() + " to the disk before overwriting because dirty flag was true");
                        //overwrite
                        System.out.println("I/O was performed to bring the block to disk");
                        frameToEvict.setContent(newContent);
                        frameToEvict.setDirty(false);
                    }
                    frameToEvict.setBlockId(fileNumber);
                    return "Record: " + buffers[unpinnedSlot].getRecord(recordNumber);

                } else { // no unpinnable slots
                    System.out.println("The corresponding block #" + fileNumber + " cannot be accessed from disk because the memory buffers are full.");
                    return "-1";
                }
            }
        }
    }

    /**
     * Writes the new content to the file
     * @param fileNumber also the blockId
     * @param newContent the new content as a byte array
     */
    public void writeFile(int fileNumber, byte[] newContent) {
        String filename = "Project1-Dataset/Project1/F" + fileNumber + "write.txt";

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
        String filename = "Project1-Dataset/Project1/F" + fileNumber + ".txt";
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
        System.out.println("Starting at: " + startFrame);

        for (int i = startFrame; i < bufferLen + startFrame; i++) {
            int idx = i % bufferLen;
            System.out.println("Index: " + idx);
            if (!buffers[idx].isPinned()) {
                lastEvicted = idx;
                System.out.println("Last evicted: " + lastEvicted);
                return idx;
            }
        }

        return -1;
    }

    @Override
    public String toString() {
        return "BufferPool{" +
                "buffers=" + Arrays.toString(buffers) +
                '}';
    }
}
