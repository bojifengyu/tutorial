package org.apache.lucene.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;

import org.apache.lucene.index.DocumentsWriterPerThread.DocState;

public class TestInvertedDocConsumer extends InvertedDocConsumer {

  DocState docState;
  int firstDocID = -1;
  
  private final SimpleIndexSegment segment;
  
  public TestInvertedDocConsumer(DocState docState, SimpleIndexSegment segment){
    this.docState = docState;
    this.segment = segment;
  }
  
  @Override
  void abort() {
  }

  @Override
  void flush(Map<String, InvertedDocConsumerPerField> fieldsToFlush,
      SegmentWriteState state) throws IOException {

  }

  @Override
  InvertedDocConsumerPerField addField(DocInverterPerField docInverterPerField,
      FieldInfo fieldInfo) {
    if (!fieldInfo.isIndexed()) {
      // Unfortunately Lucene calls this method even if the field's INDEX property is set to NO.
      // We may be able to fix this in Lucene, but until then we just return a dummy consumer
      // here that does nothing.
      return DummyInvertedDocConsumerPerField;
    }

    firstDocID = docState.docID;
    
    SortedMap<String,LinkedList<Posting>> invertedIndex = segment.getInvertedIndex(fieldInfo);
    
    return new SimpleFieldWriter(this, invertedIndex, docInverterPerField.fieldState);
  }

  @Override
  void startDocument() throws IOException {
  }

  @Override
  void finishDocument() throws IOException {
    segment.maxdoc = docState.docID;
  }
  
  private static InvertedDocConsumerPerField DummyInvertedDocConsumerPerField = new InvertedDocConsumerPerField(){

    @Override
    boolean start(IndexableField[] fields, int count) throws IOException { return false; }

    @Override
    void start(IndexableField field) {}

    @Override
    void add() throws IOException {}

    @Override
    void finish() throws IOException {}

    @Override
    void abort() {}
  };
}
