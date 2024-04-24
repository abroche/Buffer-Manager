## Section I: Compilation and Execution Guide

1. **Run the Program**:
   Execute the program in your terminal or command prompt.

2. **Provide Buffer Size from argv**:
   The program will take the buffer size from the command line arguments (argv).

3. **Execute Commands**:
   The program will be ready to accept commands. All commands are case insensitive. 
   - IMPORTANT: Include the record number/blockId with each command (e.g., "get 3" instead of just "get").

   3.1 **GET k**:
       Example: "get 1".

   3.2 **SET k <string of 40 bytes>**:
       Example: "set \<record number\> \<40 byte string\>". Assumes the string is 40 bytes.

   3.3 **PIN blockId**:
       Example: "pin \<blockId\>".

   3.4 **UNPIN blockId**:
       Example: "unpin \<blockId\>".

   3.5 **EXIT**:
       Exits the program.

## Section II: Test Results

A test case will be provided with expected output. 
In the Readme.txt file, state which test case commands are successfully working and which ones are failing.

All test case commands are working successfully.

## Section III: Design Decisions

### BufferPool Class:
- Implemented additional variables:
    - `private int lastEvicted;`: Keeps track of the position of the last evicted block.
    - `private int bufferLen;`: Represents the number of slots in the buffer pool.

- Implemented additional methods:
    - `toString()`: Formats the object to print to the console.
    - `findSlot()`: Finds an available slot in the buffer pool.
    - `loadFrame()`: Loads a frame into the buffer pool.
    - `setLastEvicted()`: Updates the position of the last evicted block.
    - `getEmptyFrame()`: Retrieves an empty frame.
    - `getUnpinnedFrame()`: Retrieves an unpinned frame.

### Frame Class:
- Implemented additional variable:
    - `private int recordSize;`: Represents the size of the record.

- Implemented additional method:
    - `toString()`: Formats the object to print to the console.
