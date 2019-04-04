package simpledb.buffer;

import simpledb.file.*;
import java.util.HashMap;
import java.util.ArrayList;


public class AdvancedBufferMgr {
    private Buffer[] bufferPool;
    private int numAvailable = 0;
    private ArrayList<Integer> freeList;
    private int nextBuff = 0;
    private HashMap<Block,Integer> BufferHash;



    AdvancedBufferMgr(int numBuffs){
        bufferPool = new Buffer[numBuffs];
        numAvailable = numBuffs;
        BufferHash = new HashMap<>(numBuffs);
        freeList = new ArrayList<>();

        //loop through the number of buffers that there will be, add them to the buffer pool then add them to the free list
        for(int i = 0; i < numBuffs; i++){
            bufferPool[i] = new Buffer();
            freeList.add(i);
            numAvailable++;
        }
    }

     void pin(Block blk){
        //find an unpinned buffer
        //put the block into the buffer hashmap
        int unpinnedBuffNum = ChooseUnpinnedBuff();

        //add the new block buffer combo to the hash map
        BufferHash.put(blk, unpinnedBuffNum);
        //pin the unpinned buffer
        bufferPool[unpinnedBuffNum].pin();
        //add the block to the buffer
         bufferPool[unpinnedBuffNum].assignToBlock(blk);
        //remove the free bufferNumber from the list
        freeList.remove(0);
        numAvailable--;
    }

    void unpin(Buffer buff){
        buff.unpin();
        if(!buff.isPinned()){
            BufferHash.remove(buff.block());
            numAvailable++;
        }
    }

    int findBlock(Block blk){
        if(BufferHash.containsKey(blk)){
            return BufferHash.get(blk);
        }
        else{
            return 0;
        }
    }

    int ChooseUnpinnedBuff(){

        if(freeList.isEmpty()){
            return freeList.get(0); //This is used only to stop the error from being thrown need to be fixed when lru is implemented
        }

        else{
            return freeList.get(0);
        }

    }


}

