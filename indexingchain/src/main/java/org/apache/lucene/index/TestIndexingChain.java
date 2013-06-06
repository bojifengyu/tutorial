package org.apache.lucene.index;

import java.io.IOException;

import org.apache.lucene.index.DocumentsWriterPerThread.IndexingChain;

public class TestIndexingChain extends IndexingChain {

  DocumentsWriterPerThread documentsWriterPerThread = null;
  private DocInverter docInverter = null;
  private DocConsumer docConsumer = null;
  
  private final SimpleIndexSegment indexSegment;
  
  public TestIndexingChain(SimpleIndexSegment indexSegment) {
    this.indexSegment = indexSegment;
  }
  
  @Override
  DocConsumer getChain(DocumentsWriterPerThread documentsWriterPerThread) {
    this.documentsWriterPerThread = documentsWriterPerThread;

    TestInvertedDocConsumer invertedDocConsumer = new TestInvertedDocConsumer(
        documentsWriterPerThread.docState, indexSegment);

    final NormsConsumer normsConsumer = new NormsConsumer();
    
    // we don't yet support doc values
    final StoredFieldsConsumer storedFields = new StoredFieldsConsumer(){

      @Override
      void addField(int docID, IndexableField field, FieldInfo fieldInfo)
          throws IOException {
      }

      @Override
      void flush(SegmentWriteState state) throws IOException {
      }

      @Override
      void abort() throws IOException {
      }

      @Override
      void startDocument() throws IOException {
      }

      @Override
      void finishDocument() throws IOException {
      }
      
    };

    this.docInverter = new DocInverter(documentsWriterPerThread.docState, invertedDocConsumer, normsConsumer);
    this.docConsumer = new DocFieldProcessor(documentsWriterPerThread, this.docInverter, storedFields);
    return this.docConsumer;
  }

}
