package simpledb.buffer;

import simpledb.file.*;
import java.util.Deque;
import java.util.*;
import java.util.HashMap;
import java.util.ArrayList;


public class AdvancedBufferMgr {
    private Buffer[] bufferPool;
    private int numBuffs = 0;
    private int numAvailable = 0;
    private ArrayList<Buffer> freeList;
    private HashMap<Block, Buffer> bufferHash;
    private static HashSet<Integer> buffHashSet;
    private static Deque<Integer> buffDeque;
    private int cacheCapacity;


    AdvancedBufferMgr(int numBuffs) {
        this.bufferPool = new Buffer[numBuffs];
        this.numAvailable = numBuffs;
        this.numBuffs = numBuffs;
        this.bufferHash = new HashMap<>(numBuffs);
        this.freeList = new ArrayList<>();

        //loop through the number of buffers that there will be, add them to the buffer pool then add them to the free list
        for (int i = 0; i < numBuffs; i++) {
            Buffer newBuff = new Buffer();
            bufferPool[i] = newBuff;
            freeList.add(newBuff);
            numAvailable++;
        }
    }

    @Override
    public String toString(){
        return "Number of buffers: " + this.numBuffs + "\n" + "Number of free buffers: " + this.freeList.size();
    }

    /**
     * Flushes the dirty buffers modified by the specified transaction.
     *
     * @param txnum the transaction's id number
     */
    synchronized void flushAll(int txnum) {
        for (Buffer buff : bufferPool) {
            if (buff.isModifiedBy(txnum)) {
                // Add the flushed buffers to the free list
                freeList.add(bufferHash.get(buff.block()));
                buff.flush();
            }
        }
    }

    synchronized Buffer pin(Block blk) {
        //find an unpinned buffer
        //put the block into the buffer hashmap

        Buffer buff = findExistingBuffer(blk);

        if(buff == null){
            buff = chooseUnpinnedBuff();

            if(buff == null){
                return null;
            }
            buff.assignToBlock(blk);
            bufferHash.put(blk, buff);
        }

        if(!buff.isPinned()){
            numAvailable--;
        }
        buff.pin();
        freeList.remove(0);
        return buff;
    }

    synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
        Buffer buff = chooseUnpinnedBuff();
        if (buff == null){
            return null;
        }

        buff.assignToNew(filename, fmtr);
        numAvailable--;
        buff.pin();
        return buff;
    }

    void unpin(Buffer buff) {
        buff.unpin();
        if (!buff.isPinned()) {
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

    Buffer findExistingBuffer(Block blk) {

        if(bufferHash.containsKey(blk)){
            return bufferHash.get(blk);
        }

        else{
            return null;
        }
    }

    private Buffer chooseUnpinnedBuff() {
        if (freeList.isEmpty()) {
            return null;
        } else {
            return freeList.get(0);
        }
    }

    void createNewCache(int capacity){

        buffHashSet = new HashSet<>();
        buffDeque = new LinkedList<>();
        cacheCapacity = capacity;

    }

    public void refer(int x)
    {
        if(buffHashSet.contains(x) && buffDeque.size() == cacheCapacity)
        {
            buffHashSet.remove(buffDeque.removeLast());
        }

        else
        {
            int index =0 , i=0;
            Iterator<Integer> itr = buffDeque.iterator();

            while(itr.hasNext())
            {
                if(itr.next()==x)
                {
                    index = i;
                    break;
                }

                i++;
            }

            buffDeque.remove(index);
        }

        buffDeque.push(x);
        buffHashSet.add(x);

    }
}
