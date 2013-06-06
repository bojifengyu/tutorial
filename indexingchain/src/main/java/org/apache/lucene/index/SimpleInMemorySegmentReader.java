package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.util.Bits;

public class SimpleInMemorySegmentReader extends AtomicReader {

  private SimpleIndexSegment segment;
  
  SimpleInMemorySegmentReader(SimpleIndexSegment segment) {
    this.segment = segment;
  }
  
  @Override
  public Fields fields() throws IOException {
    return new Fields(){

      @Override
      public Iterator<String> iterator() {
        return segment.fieldMap.keySet().iterator();
      }

      @Override
      public Terms terms(String field) throws IOException {
        return new SimpleTerms(segment.fieldMap.get(field));
      }

      @Override
      public int size() {
        return segment.fieldMap.size();
      }
      
    };
  }


  @Override
  public FieldInfos getFieldInfos() {
    return new FieldInfos(segment.finfoList.toArray(new FieldInfo[0]));
  }

  @Override
  public Bits getLiveDocs() {
    return null;
  }

  @Override
  public Fields getTermVectors(int docID) throws IOException {
    return null;
  }

  @Override
  public int numDocs() {
    return maxDoc();
  }

  @Override
  public int maxDoc() {
    return segment.maxdoc + 1;
  }

  @Override
  public void document(int docID, StoredFieldVisitor visitor)
      throws IOException {
  }

  @Override
  public boolean hasDeletions() {
    return false;
  }

  @Override
  protected void doClose() throws IOException {

  }

  @Override
  public NumericDocValues getNumericDocValues(String field) throws IOException {
    return null;
  }

  @Override
  public BinaryDocValues getBinaryDocValues(String field) throws IOException {
    return null;
  }

  @Override
  public SortedDocValues getSortedDocValues(String field) throws IOException {
    return null;
  }

  @Override
  public SortedSetDocValues getSortedSetDocValues(String field)
      throws IOException {
    return null;
  }

  @Override
  public NumericDocValues getNormValues(String field) throws IOException {
    return null;
  }

}
