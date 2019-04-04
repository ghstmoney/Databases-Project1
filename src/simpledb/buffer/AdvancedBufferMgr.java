package simpledb.buffer;

import simpledb.file.*;

import java.util.HashMap;
import java.util.ArrayList;


public class AdvancedBufferMgr {
    private Buffer[] bufferPool;
    private int numBuffs = 0;
    private int numAvailable = 0;
    private ArrayList<Integer> freeList;
    private int nextBuff = 0;
    private HashMap<Block, Integer> bufferHash;


    AdvancedBufferMgr(int numBuffs) {
        this.bufferPool = new Buffer[numBuffs];
        this.numAvailable = numBuffs;
        this.numBuffs = numBuffs;
        this.bufferHash = new HashMap<>(numBuffs);
        this.freeList = new ArrayList<>();

        //loop through the number of buffers that there will be, add them to the buffer pool then add them to the free list
        for (int i = 0; i < numBuffs; i++) {
            bufferPool[i] = new Buffer();
            freeList.add(i);
            numAvailable++;
        }
    }

    /**
     * Flushes the dirty buffers modified by the specified transaction.
     *
     * @param txnum the transaction's id number
     */
    synchronized void flushAll(int txnum) {
        for (Buffer buff : bufferPool) {
            if (buff.isModifiedBy(txnum)) {
                // Add the index of the flushed buffers to the free list
                freeList.add(bufferHash.get(buff.block()));
                buff.flush();
            }
        }
    }

    void pin(Block blk) {
        //find an unpinned buffer
        //put the block into the buffer hashmap
        int unpinnedBuffNum = chooseUnpinnedBuff();

        //add the new block buffer combo to the hash map
        bufferHash.put(blk, unpinnedBuffNum);
        //pin the unpinned buffer
        bufferPool[unpinnedBuffNum].pin();
        //add the block to the buffer
        bufferPool[unpinnedBuffNum].assignToBlock(blk);
        //remove the free bufferNumber from the list
        freeList.remove(0);
        numAvailable--;
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

    int findBlock(Block blk) {
        return bufferHash.getOrDefault(blk, 0);
//        if (bufferHash.containsKey(blk)) {
//            return bufferHash.get(blk);
//        } else {
//            return 0;
//        }
    }

    private int chooseUnpinnedBuff() {
        if (freeList.isEmpty()) {
            return 0;
        } else {
            return freeList.get(0);
        }
    }
/*
https://www.geeksforgeeks.org/lru-cache-implementation/
This is one of the LRU implementations that I found might work the best
It uses Deque and a HashSet to look up references to indexes of buffers in constant time,
and then Deque the actual index from the LinkedList Queue.
 */
//    // store keys of cache
//    static Deque<Integer> dq;
//    // store references of key in cache
//    static HashSet<Integer> map;
//    //maximum capacity of cache
//    static int csize;
//
//    LRUCache(int n)
//    {
//        dq=new LinkedList<>();
//        map=new HashSet<>();
//        csize=n;
//    }
//
//    /* Refers key x with in the LRU cache */
//    public void refer(int x)
//    {
//        if(!map.contains(x))
//        {
//            if(dq.size()==csize)
//            {
//                int last=dq.removeLast();
//                map.remove(last);
//            }
//        }
//        else
//        {
//            /* The found page may not be always the last element, even if it's an
//               intermediate element that needs to be removed and added to the start
//               of the Queue */
//            int index =0 , i=0;
//            Iterator<Integer> itr = dq.iterator();
//            while(itr.hasNext())
//            {
//                if(itr.next()==x)
//                {
//                    index = i;
//                    break;
//                }
//                i++;
//            }
//            dq.remove(index);
//        }
//        dq.push(x);
//        map.add(x);
//    }
}
