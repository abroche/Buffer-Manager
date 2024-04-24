Your name and student ID

Abelardo Broche Ortiz - 701338005

o Section I: section on how to compile and execute your code. Include clear easy-to-follow
step by step that TAs can follow

1. Run the program
2. Program will take the buffer size from argv.
3. Afterwards, the program will be ready for the next command. All commands are case insensitive (e.g., get == GET)
   IMPORTANT: You MUST input the record number/blockId with the command! For example, do "get 3" instead of just "get". Same with other commands.
    3.1 GET k
            For example, you can do "get 1". Otherwise it won't like it.
    3.2 SET k <string of 40 bytes>
            You can do "set <record number> <40 byte string>. Assumes the string is of 40 bytes.
    3.3 PIN blockId
            You can input "pin <blockId>".
    3.4 UNPIN blockId
            Just do "unpin <blockId>"
    3.5 EXIT
            Exits the program.

o Section II: section on test results. A test case will be provided along with the expected output.
This test case will help you testing your code. In the Readme.txt file state which of the test
case commands is successfully working and which ones are failing. This will help TAs to
better test your code and give you fair grades

All of the test case commands are working successfully.


o Section III: section describes any design decisions that you do beyond the design guidelines
given in this document. For example, any additional variables or methods or classes that you
add.

BufferPool Class:
    - implemented additional variables:     private int lastEvicted; //keeps track of position of the last evicted block

                                            private int bufferLen; //number of slots in the buffer pool

    - implemented additional method:        toString() method //formats the object to print to console

                                            findSlot() // Finds an available slot in the buffer pool

                                            loadFrame() // Loads a frame into the buffer pool

                                            setLastEvicted() //updates the lastEvicted

                                            getEmptyFrame()

                                            getUnpinnedFrame() //gets an unpinned frame



Frame Class:
    - implemented additional variable:      private int recordSize; //size of the record

    - implemented additional method:       toString() method //formats the object to the console
