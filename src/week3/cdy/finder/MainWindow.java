package week3.cdy.finder;

import java.io.File;
import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class MainWindow extends ApplicationWindow {
	
	private String defaultPath = "/";// 默认路径
	private final String[] images = {"jpg", "png", "jpeg"};
	private Image image;
	private Font font = null;
	private Color bgColor = null;
	private Color foreColor = null;
	private Text text;// 路径文本框
	private SourceViewer sourceViewer;// 文件文本框
	private Composite leftComposite;// 左面板(展示树结构)
	private Composite rightComposite;// 右面板(展示内容)
	private StackLayout stackLayout;
	private Composite textComposite;// 右面板(展示文本)
	private Canvas canvas;// 画布(展示图片)
	private Tree tree;// 目录树
	private FileManager fileManager;// 文件处理器
	private TreeManager treeManager;// 树管理器
	private MyTreeListener treeListener;// 树监听器
	
	public MainWindow() {
		// TODO Auto-generated constructor stub
		super(null);
		fileManager = new FileManager();
		treeManager = new TreeManager();
		treeListener = new MyTreeListener();
		stackLayout = new StackLayout();
	}
	
	@Override
	protected void configureShell(Shell shell) {
		// TODO Auto-generated method stub
		super.configureShell(shell);
		shell.setText("简易Finder实例");
		Rectangle rectangle = shell.getMonitor().getClientArea();
		shell.setSize(rectangle.width * 618 /1000, rectangle.height * 618 /1000);
		shell.setLocation((rectangle.width - shell.getSize().x) / 2, (rectangle.height - shell.getSize().y) / 2);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		
		Composite composite1 = new Composite(composite, SWT.BORDER);
		composite1.setLayout(new GridLayout(3, false));
		composite1.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		
		Label label = new Label(composite1, SWT.NONE);
		label.setText("请选择文件:");
		
		text = new Text(composite1, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		text.setText(defaultPath);
		GridData textGrid = new GridData();
		textGrid.horizontalAlignment = SWT.FILL;
		textGrid.grabExcessHorizontalSpace = true;
		text.setLayoutData(textGrid);
		
		Button button = new Button(composite1, SWT.PUSH);
		button.setText("浏览...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				directoryDialog.setMessage("选择文件目录");
				directoryDialog.setMessage("请选择文件目录的路径");
				directoryDialog.setFilterPath(defaultPath);
				String selectPath = directoryDialog.open();
				if (selectPath != null) {
					text.setText(selectPath);
					expandTreeItem(selectPath, tree.getItem(0));
				}
			}
		});
		
		createSashForm(composite);// 创建SashForm
		tree = new Tree(leftComposite, SWT.BORDER | SWT.SINGLE);
		// 注册事件监听器,展开子节点
		tree.addTreeListener(treeListener);
		tree.addSelectionListener(treeListener);
		createLazyTree(); // 创建目录树节点
		
		// 创建堆栈布局面板
		textComposite = new Canvas(rightComposite, SWT.BORDER);
		textComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		textComposite.setLayout(new GridLayout());
		canvas = new Canvas(rightComposite, SWT.BORDER);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		canvas.setLayout(new GridLayout());
		stackLayout.topControl = textComposite;
		
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				// TODO Auto-generated method stub
				if (image != null) {
					if (image.getBounds().width > canvas.getBounds().width && image.getBounds().height > canvas.getBounds().height) {
						e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, canvas.getBounds().width, canvas.getBounds().height);						
					} else if (image.getBounds().width > canvas.getBounds().width && image.getBounds().height < canvas.getBounds().height) {
						int height = (canvas.getBounds().height - image.getBounds().height) / 2;
						e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, height, canvas.getBounds().width, image.getBounds().height);
					} else if (image.getBounds().width < canvas.getBounds().width && image.getBounds().height > canvas.getBounds().height) {
						int width = (canvas.getBounds().width - image.getBounds().width) / 2;
						e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, width, 0, image.getBounds().width, canvas.getBounds().height);
					} else if (image.getBounds().width < canvas.getBounds().width && image.getBounds().height < canvas.getBounds().height) {
						int width = (canvas.getBounds().width - image.getBounds().width) / 2;
						int height = (canvas.getBounds().height - image.getBounds().height) / 2;
						e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, width, height, image.getBounds().width, image.getBounds().height);
					}
					image.dispose();
					image = null;
				}
			}
		});
		
		// 创建工具栏
		createToolbar(textComposite);
		// 创建SourceViewer
		createSourceViewer(textComposite);
		return parent;
	}
	
	// 拓展子节点
	protected void expandTreeItem(String path, TreeItem item) {
		// TODO Auto-generated method stub
		if (!path.startsWith("/")) {
			return;
		}
		int index = path.indexOf("/", 1);
		String name = "";
		if (index == -1) {
			name = path.substring(1);
			TreeItem[] items = item.getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].getText().equals(name)) {
					if (((FileEntity)items[i].getData()).isDirectory()) {
						items[i].removeAll();
						String filePath = ((FileEntity)items[i].getData()).getAbsolutePath();// 获取所选节点的绝对路径
						treeManager.createChildrenTree(items[i], new File(filePath));
						items[i].setExpanded(true);
					} 
					return;
				}
			}
		} else {
			name = path.substring(1, index);
			TreeItem[] items = item.getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].getText().equals(name)) {
					items[i].removeAll();
					String filePath = ((FileEntity)items[i].getData()).getAbsolutePath();// 获取所选节点的绝对路径
					treeManager.createChildrenTree(items[i], new File(filePath));
					items[i].setExpanded(true);
					expandTreeItem(path.substring(index), items[i]);
					return;
				}
			}
		}
	}

	// 创建SourceViewer
	private void createSourceViewer(Composite parent) {
		Document document = new Document();
		CompositeRuler ruler = new CompositeRuler();// 设置垂直行号标注
		LineNumberRulerColumn rulerColumn = new LineNumberRulerColumn();
		rulerColumn.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		ruler.addDecorator(0, rulerColumn);
		sourceViewer = new SourceViewer(parent, ruler, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		sourceViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sourceViewer.setDocument(document);
	}
	
	// 创建SashForm
	private void createSashForm(Composite parent) {
		// 创建SashForm对象
		SashForm sashForm = new SashForm(parent, SWT.SMOOTH | SWT.HORIZONTAL | SWT.BORDER);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		// 创建左窗口面板
		leftComposite = new Composite(sashForm, SWT.BORDER);
		leftComposite.setLayout(new FillLayout());
		// 创建右窗口面板
		rightComposite = new Composite(sashForm, SWT.BORDER);
		rightComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightComposite.setLayout(stackLayout);
		// 设置初始化状态两个面板所占的比例
		sashForm.setWeights(new int[] {20, 80});
	}
	
	// 创建工具栏
	public void createToolbar(Composite parent) {
		ToolBar toolBar = new ToolBar(parent, SWT.RIGHT | SWT.FLAT | SWT.BORDER);
		ToolItem item1 = new ToolItem(toolBar, SWT.PUSH);
		item1.setText("字体");
		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem item2 = new ToolItem(toolBar, SWT.PUSH);
		item2.setText("前景色");
		new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem item3 = new ToolItem(toolBar, SWT.PUSH);
		item3.setText("背景色");
		
		item1.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				FontDialog fontDialog = new FontDialog(getShell(), SWT.NONE);
				FontData fontData = fontDialog.open();
				if (fontData != null) {
					if (font != null) {
						font = null;
					}
					font = new Font(getShell().getDisplay(), fontData);
					sourceViewer.getTextWidget().setFont(font);
				}
			}
		});
		
		item2.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				ColorDialog colorDialog = new ColorDialog(getShell(), SWT.NONE);
				RGB rgb = colorDialog.open();
				if (rgb != null) {
					if (foreColor != null && !foreColor.isDisposed()) {
						foreColor.dispose();
					}
					foreColor = new Color(getShell().getDisplay(), rgb);
					sourceViewer.getTextWidget().setForeground(foreColor);
				}
			}
		});
		
		item3.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				ColorDialog colorDialog = new ColorDialog(getShell(), SWT.NONE);
				RGB rgb = colorDialog.open();
				if (rgb != null) {
					if (bgColor != null && !bgColor.isDisposed()) {
						bgColor.dispose();
					}
					bgColor = new Color(getShell().getDisplay(), rgb);
					sourceViewer.getTextWidget().setBackground(bgColor);
				}
			}
		});
	}
	
	// 创建树， 利用目录结构来初始化树数据
	private void createLazyTree() {
		// TODO Auto-generated method stub
		String path = text.getText();
		if (path == null || path.equals("")) {
			MessageDialog.openError(getShell(), "错误消息提示框", "文件路径出错");
			return;
		}
		tree.removeAll();// 清空原先的树节点
		File root = new File(path);
		treeManager.createTreeItem(tree, root);// 创建树节点
		if (tree.getItemCount() < 2) {
			tree.getItem(0).setExpanded(true);			
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainWindow mainWindow = new MainWindow();
		mainWindow.setBlockOnOpen(true);
		mainWindow.open();
		Display.getDefault().dispose();
		TreeManager.floderIcon.dispose();
		TreeManager.fileIcon.dispose();
	}
	
	public class MyTreeListener implements TreeListener, SelectionListener {

		@Override
		public void treeCollapsed(TreeEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void treeExpanded(TreeEvent e) {
			// TODO Auto-generated method stub
			TreeItem item = (TreeItem) e.item;
			item.removeAll();
			String filePath = ((FileEntity)item.getData()).getAbsolutePath();// 获取所选节点的绝对路径
			treeManager.createChildrenTree(item, new File(filePath));
			text.setText(filePath);// 更新路径文本框
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			TreeItem item = (TreeItem) e.item;
			if (item == null) {
				return;
			}
			FileEntity entity = ((FileEntity)item.getData());
			StyledText content = sourceViewer.getTextWidget();
			content.setText("");
			content.append("文件名称:" + entity.getName() + "\n");
			content.append("绝对路径:" + entity.getAbsolutePath() + "\n");
			content.append("最后修改时间:" + entity.getLastModifyTime() + "\n");
			content.append("是否为目录:" + (entity.isDirectory() ? "是" : "否") + "\n");
			content.append("是否为隐藏文件:" + (entity.isHidden() ? "是" : "否") + "\n");
			content.append("磁盘总空间:" + entity.getTotalSpace() + "\n");
			content.append("磁盘剩余空间:" + entity.getFreeSpace() + "\n");
			if (!entity.isDirectory()) {
				content.append("文件大小:" + entity.getSize() + "\n");				
			}
			stackLayout.topControl = textComposite;
			rightComposite.layout();			
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			TreeItem item = (TreeItem) e.item;
			FileEntity entity = ((FileEntity)item.getData());
			if (entity.isDirectory()) {
				if (item.getExpanded()) {
					item.setExpanded(false);
				} else {
					item.removeAll();
					String filePath = ((FileEntity)item.getData()).getAbsolutePath();// 获取所选节点的绝对路径
					treeManager.createChildrenTree(item, new File(filePath));
					item.setExpanded(true);
					text.setText(filePath);// 更新路径文本框
				}
			} else {// 如果是文件的话，显示内容
				File file = new File(entity.getAbsolutePath());
				if (file.canRead()) {
					String ext = FileManager.getExtensionName(file.getName());
					if (Arrays.asList(images).contains(ext)) {
						image = new Image(getShell().getDisplay(), file.getAbsolutePath());
						canvas.redraw();// 重绘图片
						stackLayout.topControl = canvas;
						rightComposite.layout();
					} else {
						sourceViewer.getTextWidget().setText(fileManager.openFile(file));
						stackLayout.topControl = textComposite;
						rightComposite.layout();
					}
				} else {
					MessageDialog.openError(getShell(), "错误消息对话框", "无法读取文件:" + file.getAbsolutePath() + "\n" + "Permission denied");
				}
			}
		}
	}
}
