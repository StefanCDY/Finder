package week3.cdy.finder;

public class FileEntity {
	
	private String name;
	private boolean isDirectory;
	private boolean isHidden;
	private String absolutePath;
	private String size;
	private String lastModifyTime;
	private String totalSpace;
	private String freeSpace;
	
	public FileEntity() {
		// TODO Auto-generated constructor stub
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public String getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(String totalSpace) {
		this.totalSpace = totalSpace;
	}

	public String getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(String freeSpace) {
		this.freeSpace = freeSpace;
	}

	@Override
	public String toString() {
		return "FileEntity [name=" + name + ", isDirectory=" + isDirectory + ", isHidden=" + isHidden
				+ ", absolutePath=" + absolutePath + ", size=" + size + ", lastModifyTime=" + lastModifyTime
				+ ", totalSpace=" + totalSpace + ", freeSpace=" + freeSpace + "]";
	}

}
