package org.apache.lucene.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class SimpleIndexSegment {
  Map<String,SortedMap<String,LinkedList<Posting>>> fieldMap= new HashMap<String,SortedMap<String,LinkedList<Posting>>>();
  List<FieldInfo> finfoList = new LinkedList<FieldInfo>();
  int maxdoc = -1;
  
  public SortedMap<String,LinkedList<Posting>> getInvertedIndex(FieldInfo finfo) {
    String field = finfo.name;
    SortedMap<String,LinkedList<Posting>> invertedIndex = fieldMap.get(field);
    
    if (invertedIndex == null){
      finfoList.add(finfo);
      invertedIndex = new TreeMap<String,LinkedList<Posting>>();
      fieldMap.put(field, invertedIndex);
    }
    return invertedIndex;
  }
  
  @Override
  public String toString() {
    return "maxdoc: "+maxdoc+"\n"+String.valueOf(fieldMap);
  }
  
  public SimpleInMemorySegmentReader newReader() {
    return new SimpleInMemorySegmentReader(this);
  }
  
  public void convertToLuceneSegment(Directory dir) throws IOException{
    IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(Version.LUCENE_43));
    IndexWriter writer = null;
    try {
      writer = new IndexWriter(dir, writerConfig);
      writer.addIndexes(newReader());
      writer.forceMerge(1);
    }
    finally {
      writer.commit();
      writer.close();
    }
  }
  
}
