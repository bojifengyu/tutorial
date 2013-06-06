package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedMap;

import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public class SimpleTermsEnum extends TermsEnum {

  private SortedMap<String, LinkedList<Posting>> invertedIndex;
  private Iterator<String> termIter;
  private String[] termsArray;
  SimpleTermsEnum(SortedMap<String, LinkedList<Posting>> invertedIndex) {
    this.invertedIndex = invertedIndex;
    this.termsArray = invertedIndex == null ? null : invertedIndex.keySet().toArray(new String[0]);
    resetIterator(invertedIndex);
  }
  
  private void resetIterator(SortedMap<String, LinkedList<Posting>> invertedIndex) {
    Set<String> keySet = invertedIndex == null ? null : invertedIndex.keySet();
    termIter = keySet == null ? null : keySet.iterator();
  }
  
  private BytesRef current = null;
  private long currentOrd = -1;
  
  @Override
  public BytesRef next() throws IOException {
    if (termIter != null && termIter.hasNext()) {
      current = new BytesRef(termIter.next());
    }
    else {
      current = null;
    }
    return current;
  }

  @Override
  public Comparator<BytesRef> getComparator() {
    return BytesRef.getUTF8SortedAsUnicodeComparator();
  }

  @Override
  public SeekStatus seekCeil(BytesRef text, boolean useCache)
      throws IOException {
    int pos = Arrays.binarySearch(termsArray, text.utf8ToString());
    SeekStatus seekStatus;
    String startingTerm = null;
    int insertionPoint = -1;
    if (pos < 0) {
      insertionPoint = -(pos+1);
      if (insertionPoint == termsArray.length) {
        seekStatus = SeekStatus.END;
      }
      else {
        seekStatus = SeekStatus.NOT_FOUND;
        startingTerm = termsArray[insertionPoint];
      }
    }
    else {
      seekStatus = SeekStatus.FOUND;
      startingTerm = text.utf8ToString();
    }
    
    if (startingTerm != null) {
      resetIterator(invertedIndex.tailMap(startingTerm));
      current = new BytesRef(startingTerm);
      currentOrd = insertionPoint;
    }
    else {
      resetIterator(null);
      current = null;
      currentOrd = -1;
    }
    return seekStatus;
  }

  @Override
  public void seekExact(long ord) throws IOException {
    String seekToTerm = termsArray[(int)ord];
    currentOrd = ord;
    SortedMap<String, LinkedList<Posting>> subTree = invertedIndex.tailMap(seekToTerm);
    resetIterator(subTree);
    current = new BytesRef(seekToTerm);
  }

  @Override
  public BytesRef term() throws IOException {
    return current;
  }

  @Override
  public long ord() throws IOException {
    return currentOrd;
  }

  @Override
  public int docFreq() throws IOException {
    LinkedList<Posting> postingList = invertedIndex.get(current.utf8ToString());
    return postingList == null ? 0 : postingList.size();
  }

  @Override
  public long totalTermFreq() throws IOException {
    LinkedList<Posting> postingList = invertedIndex.get(current.utf8ToString());
    if (postingList == null) {
      return -1;
    }
    long sum = 0;
    
    for (Posting posting : postingList) {
      return sum += posting.positionList == null ? 0 : posting.positionList.size();
    }
    
    return sum;
  }

  @Override
  public DocsEnum docs(Bits liveDocs, DocsEnum reuse, int flags)
      throws IOException {
    return new SimpleDocsAndPositionsEnum(invertedIndex.get(current.utf8ToString()));
  }

  @Override
  public DocsAndPositionsEnum docsAndPositions(Bits liveDocs,
      DocsAndPositionsEnum reuse, int flags) throws IOException {
    return new SimpleDocsAndPositionsEnum(invertedIndex.get(current.utf8ToString()));
  }
}
