package docManagment;


public class OwnDocument {
	private long docID;
	private long size;
    private long initialIndex;
    private boolean hasEnding;

	public OwnDocument(){}
    
    public OwnDocument(long id, long size, long index){
    	this.docID = id;
    	this.size = size;
    	this.initialIndex = index;
    	this.hasEnding = true;
    }
    
    public OwnDocument(long id, long size, long index, boolean end){
    	this.docID = id;
    	this.size = size;
    	this.initialIndex = index;
    	this.hasEnding = end;
    }
    
    //Getters and Setters
    public long getDocID() {
        return this.docID;
    }
    public void setDocID(long id) {
        this.docID = id;
    }
    
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    
    public long getInitialIndex() {
        return this.initialIndex;
    }
    public void setInitialIndex(long index) {
        this.initialIndex = index;
    }
}