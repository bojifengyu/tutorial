package org.apache.lucene.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.SortedMap;

import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.DocumentsWriterPerThread.DocState;
import org.apache.lucene.util.BytesRef;


public class SimpleFieldWriter extends InvertedDocConsumerPerField {

  private final SortedMap<String,LinkedList<Posting>> invertedIndex;
  
  private final FieldInvertState fieldState;
  private final DocState docState;

  protected BytesRef termBytesRef;
  protected TermToBytesRefAttribute termAtt;
  protected PayloadAttribute payloadAtt;

  
  SimpleFieldWriter(TestInvertedDocConsumer docConsumer, SortedMap<String,LinkedList<Posting>> invertedIndex, FieldInvertState fieldState){
    this.invertedIndex = invertedIndex;
    this.fieldState = fieldState;
    this.docState = docConsumer.docState;
  }
  
  @Override
  boolean start(IndexableField[] fields, int count) throws IOException {
    return true;
  }

  @Override
  void start(IndexableField field) {
    termAtt = fieldState.attributeSource.addAttribute(TermToBytesRefAttribute.class);
    payloadAtt = fieldState.attributeSource.addAttribute(PayloadAttribute.class);
    termBytesRef = termAtt.getBytesRef();
  }

  @Override
  void add() throws IOException {
    int docid = docState.docID;
    int position = fieldState.position;
    
    termAtt.fillBytesRef();
    BytesRef term = termAtt.getBytesRef();
    
    String termText = term.utf8ToString();
    LinkedList<Posting> postings = invertedIndex.get(termText);
    if (postings == null) {
      postings = new LinkedList<Posting>();
      invertedIndex.put(termText, postings);
    }
    
    Posting.PositionPayload pos = new Posting.PositionPayload();
    pos.position = position;
    
    Posting posting = postings.isEmpty() ? null : postings.getLast();
    if (posting == null || posting.docid != docid) {
      posting = new Posting();
      posting.docid = docid;
      posting.positionList = new LinkedList<Posting.PositionPayload>();
      postings.add(posting);
    }
    posting.positionList.add(pos);
  }

  @Override
  void finish() throws IOException {
  }

  @Override
  void abort() {
  }

}
