package at.ac.fhcampuswien.atom.shared;

public class FileAttributeRepresentation {

	private String value;
	private int split;
	
	public FileAttributeRepresentation(String value) {
		this.value = value;
		split = value.indexOf('[');
	}
	
	public String getFileName() {
		return value.substring(0, split-1);
	}
	
	public String getFileIDString() {
		return value.substring(split+1,value.length()-1);
	}
	
	public int getFileID() {
		return Integer.parseInt(getFileIDString());
	}
}
