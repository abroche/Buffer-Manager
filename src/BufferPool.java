/**
 * Represents the entire available buffer
 */
public class BufferPool {
    private Frame[] buffers;

    public BufferPool(int bufferSize){
        this.buffers = new Frame[bufferSize];
    }

    /*
    1. build the array given the input argument
    2. go over each frame and initialize this frame (initialize() method of frame)
     */
    public void initialize(){

    }

    public int isInBufferPool(int blockId){
        return -1;
    }

    public String getBlockContent(int blockId){
        return "-1";
    }

    public void bringBlockToBuffer(){

    }

    public int getEmptyFrame(){
        return -1;
    }

    public void returnBlockToDisk(){

    }

}
