package simpledb.buffer;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import simpledb.file.*;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 *
 * @author Edward Sciore
 *
 */
class BasicBufferMgr {

    private Buffer[] bufferpool;
    private int numAvailable;
    private int strategy;
    public int moveCount = 0;

    /**
     * Creates a buffer manager having the specified number of buffer slots.
     * This constructor depends on both the {@link FileMgr} and
     * {@link simpledb.log.LogMgr LogMgr} objects that it gets from the class
     * {@link simpledb.server.SimpleDB}. Those objects are created during system
     * initialization. Thus this constructor cannot be called until
     * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or is called
     * first.
     *
     * @param numbuffs the number of buffer slots to allocate
     */
    BasicBufferMgr(int numbuffs) {
        bufferpool = new Buffer[numbuffs];
        numAvailable = numbuffs;
        for (int i = 0; i < numbuffs; i++) {
            bufferpool[i] = new Buffer();
        }
    }

    /**
     * Flushes the dirty buffers modified by the specified transaction.
     *
     * @param txnum the transaction's id number
     */
    synchronized void flushAll(int txnum) {
        for (Buffer buff : bufferpool) {
            if (buff.isModifiedBy(txnum)) {
                buff.flush();
            }
        }
    }

    /**
     * Pins a buffer to the specified block. If there is already a buffer
     * assigned to that block then that buffer is used; otherwise, an unpinned
     * buffer from the pool is chosen. Returns a null value if there are no
     * available buffers.
     *
     * @param blk a reference to a disk block
     * @return the pinned buffer
     */
    synchronized Buffer pin(Block blk) {
        Buffer buff = findExistingBuffer(blk);
        if (buff == null) {
            buff = chooseUnpinnedBuffer();
            if (buff == null) {
                return null;
            }
            buff.assignToBlock(blk);
        }
        if (!buff.isPinned()) {
            numAvailable--;
        }
        moveCount += 1;

        buff.pin(moveCount);
        return buff;
    }

    /**
     * Allocates a new block in the specified file, and pins a buffer to it.
     * Returns null (without allocating the block) if there are no available
     * buffers.
     *
     * @param filename the name of the file
     * @param fmtr a pageformatter object, used to format the new block
     * @return the pinned buffer
     */
    synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
        Buffer buff = chooseUnpinnedBuffer();
        if (buff == null) {
            return null;
        }
        buff.assignToNew(filename, fmtr);
        numAvailable--;
        buff.pin(this.moveCount);
        return buff;
    }

    /**
     * Unpins the specified buffer.
     *
     * @param buff the buffer to be unpinned
     */
    synchronized void unpin(Buffer buff) {
        moveCount += 1;
        buff.unpin(moveCount);
        if (!buff.isPinned()) {
            numAvailable++;
        }
    }

    /**
     * Returns the number of available (i.e. unpinned) buffers.
     *
     * @return the number of available buffers
     */
    int available() {
        return numAvailable;
    }

    private Buffer findExistingBuffer(Block blk) {
        for (Buffer buff : bufferpool) {
            Block b = buff.block();
            if (b != null && b.equals(blk)) {
                return buff;
            }
        }
        return null;
    }


    public Buffer[] getBuffers() {
        return this.bufferpool;
    }

    public void setStrategy(int strat) {
        this.strategy = strat;
    }

    private Buffer FindLowestTimeReadIn() {
        Buffer winningBuffer = this.bufferpool[0];
        for (Buffer buff : this.bufferpool) {
            if (buff.moveReadIn < winningBuffer.moveReadIn && !buff.isPinned()) {
                winningBuffer = buff;
            }
        }

        return winningBuffer;
    }

        private Buffer FindHighestTimeReadIn() {
        Buffer winningBuffer = this.bufferpool[0];
        for (Buffer buff : this.bufferpool) {
            if (buff.moveReadIn > winningBuffer.moveReadIn) {
                winningBuffer = buff;
            }
        }

        return winningBuffer;
    }
    
    public void PrintContentsOfBuffers() {
        System.out.println("--------------------------------------");

        for (Buffer buff : this.bufferpool) {
            System.out.println("Is pinned: " + buff.isPinned());
            System.out.println("Move read: " + buff.moveReadIn);
            System.out.println("Move unpinned: " + buff.moveUnpinned);
            System.out.println("Contents: " + buff.block().number());
            System.out.println("--------------------------------------");
        }
    }

    private Buffer FindLowestTimeUnpinned() {
        Buffer winningBuffer = this.bufferpool[0];
        for (Buffer buff : this.bufferpool) {
            if (buff.moveUnpinned < winningBuffer.moveUnpinned && !buff.isPinned()) {
                winningBuffer = buff;
            }
        }
        return winningBuffer;
    }

    //0 = Naive, 1 = FIFO, 2 = LRU, 3 = Clock
    private Buffer chooseUnpinnedBuffer() { //currently the Naive method

        switch (this.strategy) {

            case 0:
                return NaiveMethod();

            case 1:
                return FifoMethod();
            case 2:
                return LruMethod();

            case 3:
                return ClockMethod();

            default:
                return null;
        }

    }

    private Buffer NaiveMethod() {
        for (Buffer buff : bufferpool) {
            if (!buff.isPinned()) {
                return buff;
            }
        }
        return null;
    }

    private Buffer FifoMethod() {
        Buffer winningBuff = FindLowestTimeReadIn();
        return winningBuff;
    }

    private Buffer LruMethod() {
        return FindLowestTimeUnpinned();
    }

    private Buffer ClockMethod() {
        //Find the current block
        Buffer cur = this.FindHighestTimeReadIn();
        //Start iteration circle with the block after current
        int curIndex = Arrays.asList(this.bufferpool).indexOf(cur) + 1;
        if (curIndex == this.bufferpool.length) {
            curIndex = 0;
        }
        System.out.println("Current: " + curIndex);
        this.PrintContentsOfBuffers();
        for (int i = 0; i < this.bufferpool.length; i++) {
            //circularly iterate the array starting with the index after current, and find the first unpinned buffer
            Buffer nextBuf = this.bufferpool[(i + curIndex) % this.bufferpool.length];
            if (!nextBuf.isPinned()) {
                return nextBuf;
            }
        }
        return null;
    }

}
