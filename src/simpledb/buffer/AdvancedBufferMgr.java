package simpledb.buffer;

import simpledb.file.*;
import java.util.Deque;
import java.util.*;
import java.util.HashMap;
import java.util.ArrayList;


public class AdvancedBufferMgr {
    private Buffer[] bufferPool;
    private int numBuffs;
    private int numAvailable;
    private Stack<Integer> emptyBuffList;
    private Map<Block, Buffer> bufferHash;
    private Set<Buffer> freeBuffHashSet;
    private int cacheCapacity;


    AdvancedBufferMgr(int numBuffs) {
        this.bufferPool = new Buffer[numBuffs];
        this.numAvailable = numBuffs;
        this.numBuffs = numBuffs;
        this.bufferHash = new HashMap<>(numBuffs);
        this.freeBuffHashSet = new HashSet<>();  //Stores indices of unpinned buffers?
        this.emptyBuffList = new Stack<>();

        //loop through the number of buffers that there will be, add them to the buffer pool then add them to the LRU queue
        for (int i = 0; i < numBuffs; i++) {
            Buffer newBuff = new Buffer();
            bufferPool[i] = newBuff;
            //bufferHash.put(newBuff, i);
            emptyBuffList.push(i);
            numAvailable++;
        }
    }

    /**
     * CS4432-Project1: custom toString()
     * @return string describing this buffermgr
     */
    @Override
    public String toString(){
        final StringBuffer sb = new StringBuffer("AdvancedBufferMgr{\nNumber of buffers: ");
        sb.append(this.numBuffs);
        sb.append("\nNumber of free buffers: ");
        sb.append(this.freeBuffHashSet.size());
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Flushes the dirty buffers modified by the specified transaction.
     *
     * @param txnum the transaction's id number
     */
    synchronized void flushAll(int txnum) {
        for (Buffer buff : bufferPool) {
            if (buff.isModifiedBy(txnum)) {
                buff.flush();
            }
        }
    }

    synchronized Buffer pin(Block blk) {
        //find an unpinned buffer
        //put the block into the buffer hashmap
        Buffer buff = findExistingBuffer(blk);

        if(buff == null){
            buff = chooseUnpinnedBuff();  // Found the LRU unpinned buffer

            if(buff == null) {  // Everything was pinned dummy
                return null;  // kill me
            }
            buff.assignToBlock(blk);
        }
        buff.pin();
        if(freeBuffHashSet.contains(buff)){
            numAvailable--;
            freeBuffHashSet.remove(buff);
        }
        bufferHash.put(blk, buff);
        return buff;
    }

    synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
        System.out.println("pinNew() called");
        Buffer buff = chooseUnpinnedBuff();
        if (buff == null){
            return null;
        }

        buff.assignToNew(filename, fmtr);
        buff.pin();
        if(freeBuffHashSet.contains(buff)){
            numAvailable--;
            freeBuffHashSet.remove(buff);
        }
        bufferHash.put(buff.block(), buff);
        return buff;
    }

    void unpin(Buffer buff) {
        buff.unpin();
        if (!buff.isPinned()) {
            if(freeBuffHashSet.contains(buff)){
                freeBuffHashSet.add(buff);
            }
            bufferHash.remove(buff.block());
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

    /**
     * CS4432-Project1: finds an existing buffer
     * @param blk the block that is contained in the buffer we want
     * @return an existing buffer with the mathcing block
     */
    private synchronized Buffer findExistingBuffer(Block blk) {

        if(bufferHash.containsKey(blk)) {
            Buffer found = bufferHash.get(blk);
            System.out.println("found existing buffer" + found);
            return found;
        }else{
            System.out.println("no existing buffer found");
            return null;
        }
    }

    /**
     * CS4432-Project1: choose an unpinned buffer based on LRU replacement policy
     * @return unpinned buffer to use
     */
    private synchronized Buffer chooseUnpinnedBuff() {
        Buffer ret = null;
        for(Buffer buff : bufferPool){
            if(!buff.isPinned()){
                // We haven't found the oldest buffer yet, it must be this one
                if(ret == null){
                    System.out.println("Found oldest buffer: " + buff.getID());
                    ret = buff;
                }
                // replace current buffer with older one
                else if(ret.getLastUsed() > buff.getLastUsed()){
                    System.out.println("found older buffer: " + buff.getID());
                    ret = buff;
                }
            }
        }
        return ret;
    }

    /**
     * CS4432-Project1: Get an array of dirty buffers in this manager
     * Dirty buffers will be written back to disk before being replaced
     *
     * @return Array of dirty buffers.
     */
    public Buffer[] getDirtyBuffers(){
        List<Buffer> dirtyBuffs = new ArrayList<>();
        for(Buffer b : bufferPool){
            if(b.isDirty()){
                dirtyBuffs.add(b);
            }
        }
        return dirtyBuffs.toArray(new Buffer[0]);
    }

    /**
     * CS4432-Project1: Get an array of pinned buffers
     *
     * @return Array of pinned buffers
     */
    public Buffer[] getPinnedBuffers(){
        List<Buffer> pinnedBuffs = new ArrayList<>();
        for(Buffer b : bufferPool){
            if(b.isPinned()){
                pinnedBuffs.add(b);
            }
        }
        return pinnedBuffs.toArray(new Buffer[0]);
    }

}
