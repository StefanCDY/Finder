package week3.cdy.finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

	// 获取树节点的文件属性
	public FileEntity getFileEntity(File file) {
		FileEntity entity = new FileEntity();
		entity.setName(file.getName());
		entity.setDirectory(file.isDirectory());
		entity.setHidden(file.isHidden());
		entity.setAbsolutePath(file.getAbsolutePath());
		entity.setLastModifyTime(new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss").format(new Date(file.lastModified())));
		entity.setTotalSpace(formetFileSize(file.getTotalSpace()));
		entity.setFreeSpace(formetFileSize(file.getFreeSpace()));
		if (file.isFile()) {
			entity.setSize(formetFileSize(file.length()));
		}
		entity.setSize(formetFileSize(file.length()));
		return entity;
	}

	// Java文件操作 获取文件扩展名
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return "";
	}

	// Java文件操作 获取不带扩展名的文件名
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return "";
	}

	// 打开文本文件
	public String openFile(File file) {
		if (file.isDirectory()) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer.toString();
	}

	// 文件大小的格式转换
	public String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	// 获得某目录下文件总大小
	public static long getDirSize(File file) {
		// 判断文件是否存在
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				if (children == null) {
					return file.length();
				}
				long size = 0;
				for (File f : children)
					size += getDirSize(f);
				return size;
			} else {// 如果是文件则直接返回其大小,以“兆”为单位
				long size = (long) file.length();
				return size;
			}
		} else {
			System.out.println(file + "文件或者文件夹不存在.");
			return 0;
		}
	}

}
