package org.apache.lucene.index;

import java.util.LinkedList;

public class Posting {
  public static class PositionPayload {
    int position;
    
    @Override
    public String toString() {
      return String.valueOf(position);
    }
  }
  
  public int docid;
  public LinkedList<PositionPayload> positionList = new LinkedList<PositionPayload>();
  
  @Override
  public String toString() {
    return docid+": "+positionList;
  }
}
