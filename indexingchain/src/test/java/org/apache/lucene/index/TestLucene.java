package org.apache.lucene.index;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DocumentsWriterPerThread.IndexingChain;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class TestLucene {

  public static final String[] testDocs = new String[] {
    "The old night keeper keeps the keep in the town",
    "In the big old house in the big old gown.",
    "The house in the town had the big old keep",
    "Where the old night keeper never did sleep.",
    "The night keeper keeps the keep in the night",
    "And keeps in the dark and sleeps in the light."
  };
  
  static IndexWriter getDefaultLuceneWriter(Directory dir) throws IOException {
    IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_43,new StandardAnalyzer(Version.LUCENE_43));
    return new IndexWriter(dir, conf);
  }
  
  static IndexWriter getSpecialWriter(IndexingChain idxChain) throws IOException {
    IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_43,new StandardAnalyzer(Version.LUCENE_43));
    conf.setIndexingChain(idxChain);
    return new IndexWriter(new RAMDirectory(),conf);
  }
  
  @Test
  public void testIndexing() throws Exception{
    SimpleIndexSegment segment = new SimpleIndexSegment();
    TestIndexingChain idxChain = new TestIndexingChain(segment);
    
    IndexWriter writer = getSpecialWriter(idxChain);
    
    for (String doc : testDocs) {
      Document luceneDoc = new Document();
      luceneDoc.add(new TextField("contents", doc, Store.NO));
      writer.addDocument(luceneDoc);
    }
    writer.commit();
    writer.close();
    
    AtomicReader reader = segment.newReader();
    TestCase.assertEquals(testDocs.length, reader.numDocs());
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception{
    
    SimpleIndexSegment segment = new SimpleIndexSegment();
    TestIndexingChain idxChain = new TestIndexingChain(segment);
    
    IndexWriter writer = getSpecialWriter(idxChain);
    
    for (String doc : testDocs) {
      Document luceneDoc = new Document();
      luceneDoc.add(new TextField("contents", doc, Store.NO));
      writer.addDocument(luceneDoc);
    }
    writer.commit();
    writer.close();
    
    System.out.println(segment);
    
    FSDirectory fsdir = FSDirectory.open(new File("/tmp/johntest"));
    segment.convertToLuceneSegment(fsdir);
  }

}
