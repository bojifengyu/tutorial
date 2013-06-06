package org.apache.lucene.index;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.SortedMap;

import org.apache.lucene.util.BytesRef;

public class SimpleTerms extends Terms {

  private SortedMap<String, LinkedList<Posting>> invertedIndex;
  
  SimpleTerms(SortedMap<String, LinkedList<Posting>> invertedIndex) {
    this.invertedIndex = invertedIndex;
  }
  
  @Override
  public TermsEnum iterator(TermsEnum reuse) throws IOException {
    return new SimpleTermsEnum(invertedIndex);
  }

  @Override
  public Comparator<BytesRef> getComparator() {
    return BytesRef.getUTF8SortedAsUnicodeComparator();
  }

  @Override
  public long size() throws IOException {
    return invertedIndex == null ? 0 : invertedIndex.size();
  }

  @Override
  public long getSumTotalTermFreq() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getSumDocFreq() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getDocCount() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean hasOffsets() {
    return false;
  }

  @Override
  public boolean hasPositions() {
    return true;
  }

  @Override
  public boolean hasPayloads() {
    return false;
  }

}
