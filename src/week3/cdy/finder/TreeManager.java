package week3.cdy.finder;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeManager {
	
	private FileManager fileManager;
	public static Image floderIcon = new Image(Display.getCurrent(), "src/week3/cdy/icon/floderIcon.png");
	public static Image fileIcon = new Image(Display.getCurrent(), "src/week3/cdy/icon/fileIcon.png");
	
	public TreeManager() {
		// TODO Auto-generated constructor stub
		fileManager = new FileManager();
	}
	
	// 创建系统根节点
	public void createTreeItem(Tree tree, File root) {
		File[] fs = null;// 获取系统盘符
		if (root == null) {
			fs = File.listRoots();
		} else {
			fs = new File[] {root};
		}
		for (int i = 0; i < fs.length; i++) {
			// 创建根节点
			TreeItem rootItem = new TreeItem(tree, SWT.NONE);
			rootItem.setText(fs[i].toString());
			rootItem.setData(fileManager.getFileEntity(fs[i]));
			if (fs[i].isDirectory()) {
				rootItem.setImage(floderIcon);
			} else {
				rootItem.setImage(fileIcon);
			}
			// 创建子节点
			createChildrenTree(rootItem, fs[i]);
        }
	}

	// 创建子节点
	public void createChildrenTree(TreeItem parentItem, File file) {
		// TODO Auto-generated method stub
		File[] files = file.listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			try {
				if (!files[i].getAbsolutePath().equals(files[i].getCanonicalPath())) {// 不相等，则是链接文件
					continue;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			item.setText(files[i].getName());
			item.setData(fileManager.getFileEntity(files[i]));
			if (files[i].isDirectory()) {
				item.setItemCount(1);
				item.setImage(floderIcon);
			} else {
				item.setImage(fileIcon);
			}
		}
	}
	
}
