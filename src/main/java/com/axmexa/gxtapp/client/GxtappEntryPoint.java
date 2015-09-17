package com.axmexa.gxtapp.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.axmexa.gxtapp.client.model.Item;
import com.axmexa.gxtapp.client.model.Item.ItemProperties;
import com.axmexa.gxtapp.client.resources.Icons;
import com.axmexa.gxtapp.client.service.GetLinesService;
import com.axmexa.gxtapp.client.service.GetLinesServiceAsync;
import com.axmexa.gxtapp.client.service.TreeLoadService;
import com.axmexa.gxtapp.client.service.TreeLoadServiceAsync;
import com.axmexa.gxtapp.client.service.TreeSaveService;
import com.axmexa.gxtapp.client.service.TreeSaveServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.dnd.core.client.TreeDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.info.DefaultInfoConfig;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.info.InfoConfig.InfoPosition;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GxtappEntryPoint implements EntryPoint, IsWidget {
	
	public static final String FILE_IS_LARGE = "File is larger then 500 kBytes";
	public static final String FILE_IS_WRONG_CHARSET = "File charset must be UTF-8 or Windows-1251";

	private static final Logger logger = Logger.getLogger("GxtappEntryPoint");
	
	private static final TextButton loadDbBtn = new TextButton();
	private static final TextButton saveDbBtn = new TextButton();
	private static final TextButton loadFileBtn = new TextButton();
	private static final TextButton addNewFolderBtn = new TextButton();
	private static final TextButton toRightBtn = new TextButton();
	private static final TextButton toLeftBtn = new TextButton();
	private static final TextButton toLeftAllBtn = new TextButton();
	private SimpleContainer centerPanel;
	private ItemProperties properties;
	private static TreeStore<Item> leftStore;
	private Tree<Item, String> leftTree;
	private static TreeStore<Item> rightStore;
	private Tree<Item, String> rightTree;
	
	static{
		loadFileBtn.addSelectHandler(new SelectHandler() {
			
			final AutoProgressMessageBox progressMessageBox = new AutoProgressMessageBox("Progress", "Loading your data, please wait...");
			GetLinesServiceAsync getLinesService = GWT.create(GetLinesService.class);
			
			@Override
			public void onSelect(SelectEvent event) {
				
				final FormPanel fp = new FormPanel();

			    final FileUploadField fuf = new FileUploadField();
			    fuf.setName("fileUploadField");
			    
			    fuf.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						if (!fuf.getValue().endsWith(".txt")){
							AlertMessageBox alert = new AlertMessageBox("Wrong file type", "Choose any txt file please");
							alert.show();
						}else{
							fp.submit();
						    progressMessageBox.setProgressText("Loading...");
						    progressMessageBox.auto();
						    progressMessageBox.show();
						}
					}
				});

			    VerticalLayoutContainer vlc = new VerticalLayoutContainer();
			    vlc.add(new Label("Please choose any txt file (size < 500 Kb)"), new VerticalLayoutData(1, -1, new Margins(5)));
			    vlc.add(new FieldLabel(fuf, "File"), new VerticalLayoutData(1, -1, new Margins(5)));
			    final Window loadWindow = new Window();

			    fp.setMethod(Method.POST);
			    fp.setEncoding(Encoding.MULTIPART);
//			    fp.setAction("fileupload");
			    fp.setAction("uploadfile");
			    fp.setWidget(vlc);
			    fp.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			      public void onSubmitComplete(SubmitCompleteEvent event) {
			        String resultHtml = event.getResults();
			        if (resultHtml.indexOf(FILE_IS_LARGE) >= 0){
			        	progressMessageBox.hide();
			        	AlertMessageBox alert = new AlertMessageBox("Wrong file", FILE_IS_LARGE);
			        	alert.show();
			        }else if (resultHtml.indexOf(FILE_IS_WRONG_CHARSET) >= 0){
			        	progressMessageBox.hide();
			        	AlertMessageBox alert = new AlertMessageBox("Wrong file", FILE_IS_WRONG_CHARSET);
						alert.show();
			        }else if (resultHtml.indexOf("HTTP ERROR 500") >= 0){
			        	progressMessageBox.hide();
			        	AlertMessageBox alert = new AlertMessageBox("Wrong file", "Some error in file");
						alert.show();
			        }else{
			        	printW(resultHtml);
			        	String[] params = resultHtml.split("=");
			        	printW(params[0] + "  - " + params[1]);
			        	progressMessageBox.setMessage("Processing file...");
			        	getLinesService.getLines(params[0], params[1], new AsyncCallback<List<String>>() {
							
							@Override
							public void onSuccess(List<String> result) {
								printW(result);
								progressMessageBox.hide();
								loadWindow.hide();
								updateTreeStores(result);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								printW(caught.getMessage());
								AlertMessageBox alert = new AlertMessageBox("Cant process the file", "Cant process the file");
								alert.show();
								progressMessageBox.hide();
								loadWindow.hide();
							}
						});
			        }
			      }
			    });

			    loadWindow.setHeadingText("Upload File");
			    loadWindow.setPixelSize(300, 50);
			    loadWindow.setWidget(fp);
			    loadWindow.show();
			}
		});
		
		
		final TreeLoadServiceAsync treeLoadDBService = GWT.create(TreeLoadService.class);
		
		loadDbBtn.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				treeLoadDBService.getItems(new AsyncCallback<Item[]>() {

					@Override
					public void onFailure(Throwable caught) {
						printW("FAIL!");
						printW(caught);
					}

					@Override
					public void onSuccess(Item[] result) {
						printW("succes!");
						removeAll(leftStore);
						removeAll(rightStore);
						rightStore.addSubTree(0, createModels(Arrays.asList(result)));
					}
				});
				
			}
		});
		
		final TreeSaveServiceAsync treeSaveService = GWT.create(TreeSaveService.class);
		
		saveDbBtn.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				treeSaveService.saveTree(rightStore.getAll().toArray(new Item[0]), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						printW("FAIL store!");
						printW(caught);						
					}

					@Override
					public void onSuccess(String result) {
						printW("STORED");
						printW(result);
					}
				});
			}
		});
		
		
	}
	
	private static void updateTreeStores(List<String> list) {
		removeAll(leftStore);
		removeAll(rightStore);
		for (Item item : toItemsList(list)){
			leftStore.add(item);
		}
	}
	
	private static List<? extends Item> toItemsList(List<String> source) {
		Set<String> noDuplicates = new HashSet<String>();
		noDuplicates.addAll(source);
		ArrayList<Item> result = new ArrayList<Item>();
		for (String name : noDuplicates) {
			result.add(new Item(name));
		}
		return result;
	}

	private static void removeAll(TreeStore<Item> store) {
		for (Item item : store.getAll()) {
			store.remove(item);
		}
	}

	@Override
	public Widget asWidget() {

		if (null == centerPanel) {
			centerPanel = new Window();
			((Window)centerPanel).setHeadingText("App");
			centerPanel.setPixelSize(500, 500);
			centerPanel.add(createPanel());
		}
		return centerPanel;
	}

	private Widget createPanel() {
		
		properties = GWT.create(ItemProperties.class);
		
		SimpleContainer panel = new SimpleContainer();
		VerticalLayoutContainer mainVerticalContainer = new VerticalLayoutContainer();
		
		mainVerticalContainer.add(createHeadButtons(), new VerticalLayoutData(1, -1));
		mainVerticalContainer.add(createColumns(), new VerticalLayoutData(1, 1));
		mainVerticalContainer.add(createDownButtons(), new VerticalLayoutData(1, -1));
		panel.add(mainVerticalContainer);
		
		return mainVerticalContainer;
	}


	private IsWidget createColumns() {
		
	    BorderLayoutData left = new BorderLayoutData(.5);
	    left.setMaxSize(800);
	    left.setSplit(true);
	    
	    BorderLayoutData right = new BorderLayoutData(.5);
	    right.setMaxSize(800);
	    right.setSplit(true);
	    
	    ContentPanel leftData = new ContentPanel();
	    leftData.setHeaderVisible(false);
	    leftData.add(createLeftTree());
	    
	    ContentPanel rightData = new ContentPanel();
	    rightData.setHeaderVisible(false);
	    rightData.add(createRightTree());
		
		BorderLayoutContainer blc = new BorderLayoutContainer(); 
		blc.setWestWidget(leftData, left);
		blc.setCenterWidget(rightData, right);
		
		return blc;
	}

	private HBoxLayoutContainer createHeadButtons() {
		
		loadDbBtn.setIcon(Icons.INSTANCE.getFromDBImage());
		loadDbBtn.setToolTip("Load data from Database");
		addNewFolderBtn.setIcon(Icons.INSTANCE.getNewImage());
		loadFileBtn.setIcon(Icons.INSTANCE.getFromFileImage());
		saveDbBtn.setIcon(Icons.INSTANCE.getToDBImage());
		toLeftAllBtn.setIcon(Icons.INSTANCE.getallleftImage());
		toLeftBtn.setIcon(Icons.INSTANCE.gettoleftImage());
		toRightBtn.setIcon(Icons.INSTANCE.gettorightImage());
		
		
		
		BoxLayoutData flex = new BoxLayoutData(new Margins(0, 5, 0, 0));
	    flex.setFlex(1);
	    
	    HBoxLayoutContainer c = new HBoxLayoutContainer();
	    c.setPadding(new Padding(5));
	    c.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
	    
	    
	    
	    c.add(loadFileBtn, new BoxLayoutData(new Margins(0, 5, 0, 0)));
	    c.add(new Label(), flex);
	    c.add(addNewFolderBtn, new BoxLayoutData(new Margins(0, 5, 0, 0)));
	    c.add(loadDbBtn, new BoxLayoutData(new Margins(0)));
	    c.add(saveDbBtn, new BoxLayoutData(new Margins(0)));
	    c.setBorders(true);
	    
		return c;
	}

	
	
	private HBoxLayoutContainer createDownButtons() {
		BoxLayoutData flex = new BoxLayoutData(new Margins(0, 5, 0, 0));
	    flex.setFlex(1);
	    
	    HBoxLayoutContainer c = new HBoxLayoutContainer();
	    c.setPadding(new Padding(5));
	    c.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
	    c.add(toRightBtn, new BoxLayoutData(new Margins(0, 5, 0, 0)));
	    c.add(new Label(), flex);
	    c.add(toLeftBtn, new BoxLayoutData(new Margins(0, 5, 0, 0)));
	    c.add(toLeftAllBtn, new BoxLayoutData(new Margins(0)));
	    c.setBorders(true);
	 
		return c;
	}

	private void collect(TreeNode<? extends Item> node, ArrayList<Item> collectorList) {
		if (node.getData().isDir()){
			for (TreeNode<? extends Item> child : node.getChildren()) {
				collect(child, collectorList);
			}
		}else {
			collectorList.add(node.getData());
		}
	}
	
	private ArrayList<Item> extractItems(List<? extends TreeNode<? extends Item>> children) {
		ArrayList<Item> listAllInnerItems = new ArrayList<>();
		for (TreeNode<? extends Item> node : children) {
			collect(node, listAllInnerItems);
		}
		return listAllInnerItems;
	}
	
	private Tree<Item, String> createLeftTree() {
		
		leftStore = new TreeStore<Item>(properties.key()){
			@Override
			public void addSubTree(int index, List<? extends TreeNode<? extends Item>> children) {
				add(extractItems(children));
				
				
			}
			@Override
			public void addSubTree(Item parent, int index, List<? extends TreeNode<? extends Item>> children) {
				add(extractItems(children));
			}
		};
//		leftStore.addSubTree(0, createModels());
		
		
		leftTree = new Tree<Item, String>(leftStore, properties.name()){
			@Override
			protected boolean hasChildren(Item item) {
				return (item.isDir()) || super.hasChildren(item);
			}
		};
		leftTree.setBorders(true);
		leftTree.setSelectionModel(new TreeSelectionModel<Item>());
		leftTree.getStyle().setLeafIcon(Icons.INSTANCE.getLeafImage());		
		
		leftTree.addDomHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				moveFromLeftToRight();
			}
		}, DoubleClickEvent.getType());
		
		
//		Conf Button
		toRightBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				moveFromLeftToRight();
			}

			
		});
		
//		Conf DND
		new TreeDragSource<Item>(leftTree);
		TreeDropTarget<Item> dropTarget = new TreeDropTarget<Item>(leftTree);
		dropTarget.setFeedback(Feedback.APPEND);
		
		return leftTree;
	}
	
	private void moveFromLeftToRight() {
		List<Item> selectedItemsLeft = leftTree.getSelectionModel().getSelectedItems();
		List<Item> selectedItemsRight = rightTree.getSelectionModel().getSelectedItems();
		if (selectedItemsRight.size() > 1) {
			return;
		} else if (selectedItemsRight.size() != 1) {
			for (Item item : selectedItemsLeft) {
				rightStore.add(new Item(item));
				leftStore.remove(item);
			}
		} else {
			Item itemRight = selectedItemsRight.get(0);
			printW("selectedItemsRight.get(0): " + itemRight);
			if (itemRight.isDir()) {
				for (Item item : selectedItemsLeft) {
					printW("Start Iter");
					printW(item);
					
					rightStore.add(itemRight, new Item(item));
					leftStore.remove(item);
//					rightStore.add(itemRight, item);
					printW("End   Iter");
				}
			}
		}
	}


	private Tree<Item, String> createRightTree() {
		
		rightStore = new TreeStore<Item>(properties.key()){
			@Override
			public void add(Item parent, Item child) {
				if (child.isDir())
					insert(parent, 0, child);
				else
					super.add(parent, child);
			}
			@Override
			public void add(Item root) {
				if (root.isDir())
					insert(0, root);
				else
					super.add(root);
			}
		};
		
//		rightStore.addSubTree(0, createModels(createSimpleTree()));
//		rightStore.addSubTree(0, createModels(Arrays.asList(new Item("blavla"))));
		
		rightTree = new Tree<Item, String>(rightStore, properties.name()){
			@Override
			protected boolean hasChildren(Item item) {
				return (item.isDir()) || super.hasChildren(item);
			}
		};
		rightTree.setBorders(true);
		rightTree.setSelectionModel(new TreeSelectionModel<Item>());
		rightTree.getStyle().setLeafIcon(Icons.INSTANCE.getLeafImage());
		
		new TreeDragSource<Item>(rightTree);
		
		TreeDropTarget<Item> treeDropTarget = new TreeDropTarget<Item>(rightTree);
		treeDropTarget.setFeedback(Feedback.APPEND);
		treeDropTarget.setAllowSelfAsSource(true);
		
		addNewFolderBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<Item> selectedItems = rightTree.getSelectionModel().getSelectedItems();
				if (selectedItems.size() > 1) return;
				if (selectedItems.size() == 0){
					final PromptMessageBox messageBox = promptNewDirName(rightStore, null);
					messageBox.show();
				}else{
					final Item selectedItem = selectedItems.get(0);  
					if (null != selectedItem){
						final PromptMessageBox messageBox = promptNewDirName(rightStore, selectedItem);
						messageBox.show();
					}
				}
			}

			private PromptMessageBox promptNewDirName(final TreeStore<Item> treeStore, final Item selectedItem) {
				final PromptMessageBox messageBox = new PromptMessageBox("New directory", "Please enter new directory name:");
				messageBox.addDialogHideHandler(new DialogHideHandler() {
					@Override
					public void onDialogHide(DialogHideEvent event) {
						if (event.getHideButton().equals(PredefinedButton.OK)){
							if (null == selectedItem)
								treeStore.add(new Item(messageBox.getValue()).asDir());
							else if (selectedItem.isDir()){
								treeStore.add(selectedItem, new Item(messageBox.getValue()).asDir());
							}
							else
								treeStore.add(treeStore.getParent(selectedItem), new Item(messageBox.getValue()).asDir());
						}
					}
				});
				return messageBox;
			}
		});
		
		toLeftBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<Item> selectedItemsRight = rightTree.getSelectionModel().getSelectedItems();
				for (Item item : selectedItemsRight) {
					List<Item> allChildren = rightStore.getAllChildren(item);
					moveFromRightToLeft(allChildren);
					if (item.isDir())
						rightStore.remove(item);
					else
						moveFromRightToLeft(Arrays.asList(item));
				}
			}

		});
		
		toLeftAllBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				moveFromRightToLeft(rightStore.getAll());
			}

		});
		
		return rightTree;
	}
	
	private void moveFromRightToLeft(List<Item> items) {
		for (Item item : items) {
			if (!(item.isDir())) {
				Item root = new Item(item);
				leftStore.add(root);
			}
			rightStore.remove(item);
		}
	}
	
	
		private void collect(Item item, Iterator iterator) {
		printW("start: " + item);
		if (item.isDir()){
			for (Iterator childIterator = item.getItems().iterator(); childIterator.hasNext();) {
				collect((Item)childIterator.next(), childIterator);
			} 
		}
		printW("iterator.remove();");
		iterator.remove();
		
		printW("rightStore.remove(item);");
		rightStore.remove(item);
		
		if (!(item.isDir())){
			printW("leftStore.add(new Item(item))");
			leftStore.add(new Item(item));
		}
		
		printW("end  : " + item);
	}
	
	private List<? extends Item> initData(){
		
		Item d1 = new Item("One dir").asDir();
		d1.setItems(new ArrayList<Item>(){{
			add(new Item("one1"));
			add(new Item("one2"));
			add(new Item("one3"));
		}});
		final Item d2 = new Item("two dir").asDir();
		d2.setItems(new ArrayList<Item>(){{
			add(new Item("two1"));
			add(new Item("two2"));
			add(new Item("two3"));
		}});
		Item d3 = new Item("tree dir").asDir();
		d3.setItems(new ArrayList<Item>(){{
			add(new Item("tree1"));
			add(new Item("tree2"));
			add(new Item("tree3"));
		}});
		
		return Arrays.asList(d1, d2, d3);
	}
	
	private List<? extends Item> createSimpleTree() {

		Item oned = new Item("one-d").asDir();
		Item one1 = new Item("one-1");
		Item one2 = new Item("one-2");
		Item twod = new Item("two-d").asDir();
		Item two1 = new Item("two-1");
		Item two2 = new Item("two-2");
		Item three = new Item("three");
		oned.addItem(one1);
		oned.addItem(one2);
		oned.addItem(twod);
		twod.addItem(two1);
		twod.addItem(two2);

		// return Arrays.asList(oned, one1, one2, twod, two1, two2);
		return Arrays.asList(oned, /*twod, */three);

	}

	private static List<TreeNode<Item>> createModels(List<? extends Item> sourceRootIrems) {
		ArrayList<TreeNode<Item>> rootModels = new ArrayList<TreeNode<Item>>();
		for (Item item : sourceRootIrems) {
			rootModels.add(buildNode(item));
		}
		return rootModels;
	}

	
	private static TreeNode<Item> buildNode(final Item rootItem) {
		
		return new TreeNode<Item>() {

			@Override
			public List<TreeNode<Item>> getChildren() {
				List<TreeNode<Item>> childs = null;
				if (rootItem.isDir()){
					childs = new ArrayList<TreeNode<Item>>();
					for (Item child : rootItem.getItems()) {
						childs.add(buildNode(child));
					}
				}
				return childs;
			}

			@Override
			public Item getData() {
				return rootItem;
			}
		};
	}

	@Override
	public void onModuleLoad() {

		RootPanel.get().add(this);
	}

	public static void main(String[] args) {
		System.out.println();
	}
	
	static void printW(Object o){
		if (o instanceof Throwable)
			logger.log(Level.WARNING, ((Throwable)o).getMessage(), ((Throwable)o));
		logger.warning(String.valueOf(o));
	}
	
	static void print(Object o){
		DefaultInfoConfig config = new DefaultInfoConfig("LOG", o.toString());
        config.setPosition(InfoPosition.BOTTOM_LEFT);
        config.setWidth(800);
        config.setDisplay(10000);
        Info.display(config);
	}
	
}
