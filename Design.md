# Design

## Replacement Policies

In this project, we implemented a Least Recently Used policy to evict buffer pages from the buffer pool.

## Data Structures

We used a HashMap for constant time buffer page lookups, and a Queue to keep track of the order that the buffer
pages were added to the pool.  
We also implemented a list containing indices of free buffers in the pool, so we can get the next open spot
in the pool, and if no spots were available, we would evict a page and replace it.

## Key Places

The key places in SimpleDB that require changes is lines 24 and 40-42 of BufferMgr.java:  

```java
// Original
private BasicBufferMgr bufferMgr;

public BufferMgr(int numbuffers {
    bufferMgr = new BasicBufferMgr(numbuffers);
}

// Modified
private AdvancedBufferMgr bufferMgr;

public BufferMgr(int numbuffers {
    bufferMgr = new AdvancedBufferMgr(numbuffers);
}
```

We changed the field and constructor to use our AdvancedBufferMgr implementation.
