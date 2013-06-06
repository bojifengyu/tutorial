package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.lucene.index.Posting.PositionPayload;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;

public class SimpleDocsAndPositionsEnum extends DocsAndPositionsEnum {
  private Posting currentPosting = null;
  private int docid = -1;
  private Iterator<Posting> postingIter;
  private Iterator<PositionPayload> posIter = null;
  
  SimpleDocsAndPositionsEnum(LinkedList<Posting> postingList) {
    this.postingIter = postingList.iterator();
  }
  
  @Override
  public int nextPosition() throws IOException {
    if (posIter != null && posIter.hasNext()) {
      return posIter.next().position;
    }
    return -1;
  }

  @Override
  public int startOffset() throws IOException {
    return -1;
  }

  @Override
  public int endOffset() throws IOException {
    return -1;
  }

  @Override
  public BytesRef getPayload() throws IOException {
    return null;
  }

  @Override
  public int freq() throws IOException {
    if (currentPosting.positionList != null) {
      this.posIter = currentPosting.positionList.iterator();
      return currentPosting.positionList.size();
    }
    return 0;
  }

  @Override
  public int docID() {
    return docid;
  }

  @Override
  public int nextDoc() throws IOException {
    if (postingIter.hasNext()) {
      currentPosting = postingIter.next();
      docid = currentPosting.docid;
    }
    else {
      currentPosting = null;
      docid = DocIdSetIterator.NO_MORE_DOCS;
    }
    return docid;
  }

  @Override
  public int advance(int target) throws IOException {
    int doc;
    while ((doc = nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
      if (doc >= target) break;
    }
    return doc;
  }

  @Override
  public long cost() {
    return 0;
  }

}
