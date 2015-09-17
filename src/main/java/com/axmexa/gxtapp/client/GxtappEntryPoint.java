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
import com.google.gwt.resources.client.ImageResource;
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
import com.sencha.gxt.dnd.core.client.DndDropEvent;
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
	private static TreeStore<Item> listStore;
	private Tree<Item, String> list;
	private static TreeStore<Item> treeStore;
	private Tree<Item, String> tree;
	
	static{
		loadFileBtn.addSelectHandler(new SelectHandler() {
			
			final AutoProgressMessageBox progressLoadFileMessageBox = new AutoProgressMessageBox("Progress", "Loading your data, please wait...");
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
						    progressLoadFileMessageBox.setProgressText("Loading file...");
						    progressLoadFileMessageBox.auto();
						    progressLoadFileMessageBox.show();
						}
					}
				});

			    VerticalLayoutContainer vlc = new VerticalLayoutContainer();
			    vlc.add(new Label("Please choose any txt file (size < 500 Kb)"), new VerticalLayoutData(1, -1, new Margins(5)));
			    vlc.add(new FieldLabel(fuf, "File"), new VerticalLayoutData(1, -1, new Margins(5)));
			    final Window loadWindow = new Window();

			    fp.setMethod(Method.POST);
			    fp.setEncoding(Encoding.MULTIPART);
			    fp.setAction("uploadfile");
			    fp.setWidget(vlc);
			    fp.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			      public void onSubmitComplete(SubmitCompleteEvent event) {
			        String resultHtml = event.getResults();
			        if (resultHtml.indexOf(FILE_IS_LARGE) >= 0){
			        	progressLoadFileMessageBox.hide();
			        	AlertMessageBox alert = new AlertMessageBox("Wrong file", FILE_IS_LARGE);
			        	alert.show();
			        }else if (resultHtml.indexOf(FILE_IS_WRONG_CHARSET) >= 0){
			        	progressLoadFileMessageBox.hide();
			        	AlertMessageBox alert = new AlertMessageBox("Wrong file", FILE_IS_WRONG_CHARSET);
						alert.show();
			        }else if (resultHtml.indexOf("HTTP ERROR 500") >= 0){
			        	progressLoadFileMessageBox.hide();
			        	AlertMessageBox alert = new AlertMessageBox("Wrong file", "Some error in file");
						alert.show();
			        }else{
			        	log(resultHtml);
			        	String[] params = resultHtml.split("=");
			        	log(params[0] + "  - " + params[1]);
			        	progressLoadFileMessageBox.setMessage("Processing file...");
			        	getLinesService.getLines(params[0], params[1], new AsyncCallback<List<String>>() {
							
							@Override
							public void onSuccess(List<String> result) {
								log(result);
								progressLoadFileMessageBox.hide();
								loadWindow.hide();
								updateTreeStores(result);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								log(caught);
								AlertMessageBox alert = new AlertMessageBox("Cant process the file", "Can`t process the file");
								alert.show();
								progressLoadFileMessageBox.hide();
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
		final AutoProgressMessageBox progressLoadDBMessageBox = new AutoProgressMessageBox("Progress", "Loading data, please wait...");
		
		loadDbBtn.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				
				progressLoadDBMessageBox.setProgressText("Saving in Database...");
				progressLoadDBMessageBox.auto();
				progressLoadDBMessageBox.show();
				
				treeLoadDBService.getItems(new AsyncCallback<Item[]>() {

					@Override
					public void onFailure(Throwable caught) {
						log("FAIL!");
						log(caught);
						progressLoadDBMessageBox.hide();
					}

					@Override
					public void onSuccess(Item[] result) {
						log("succes!");
						removeAll(listStore);
						removeAll(treeStore);
						treeStore.addSubTree(0, createModels(Arrays.asList(result)));
						progressLoadDBMessageBox.hide();
					}
				});
				
			}
		});
		
		final TreeSaveServiceAsync treeSaveService = GWT.create(TreeSaveService.class);
		final AutoProgressMessageBox progressSaveDBMessageBox = new AutoProgressMessageBox("Progress", "Saving data, please wait...");
		
		saveDbBtn.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				
				progressSaveDBMessageBox.setProgressText("Saving in Database...");
				progressSaveDBMessageBox.auto();
				progressSaveDBMessageBox.show();
				
				treeSaveService.saveTree(treeStore.getRootItems().toArray(new Item[0]), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						log("FAIL store!");
						log(caught);						
						progressSaveDBMessageBox.hide();
					}

					@Override
					public void onSuccess(String result) {
						log("STORED");
						log(result);
						progressSaveDBMessageBox.hide();
						AlertMessageBox messBox = new AlertMessageBox("Result", "Your data succesfully stored in Database");
						messBox.setIcon(Icons.INSTANCE.getToDBImage());
						messBox.show();
					}
				});
			}
		});
		
		
	}
	
	private static void updateTreeStores(List<String> list) {
		removeAll(listStore);
		removeAll(treeStore);
		for (Item item : toItemsList(list)){
			listStore.add(item);
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
	    leftData.add(createList());
	    
	    ContentPanel rightData = new ContentPanel();
	    rightData.setHeaderVisible(false);
	    rightData.add(createTree());
		
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
	
	private Tree<Item, String> createList() {
		
		listStore = new TreeStore<Item>(properties.key()){
			@Override
			public void addSubTree(int index, List<? extends TreeNode<? extends Item>> children) {
				add(extractItems(children));
				
				
			}
			@Override
			public void addSubTree(Item parent, int index, List<? extends TreeNode<? extends Item>> children) {
				add(extractItems(children));
			}
		};
		listStore.addSubTree(0, createModels(createSimpleTree()));
		
		
		list = new Tree<Item, String>(listStore, properties.name()){
			@Override
			protected boolean hasChildren(Item item) {
				return (item.isDir()) || super.hasChildren(item);
			}
		};
		list.setBorders(true);
		list.setSelectionModel(new TreeSelectionModel<Item>());
		list.getStyle().setLeafIcon(Icons.INSTANCE.getLeafImage());		
		
		list.addDomHandler(new DoubleClickHandler() {
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
		new TreeDragSource<Item>(list);
		TreeDropTarget<Item> dropTarget = new TreeDropTarget<Item>(list);
		dropTarget.setFeedback(Feedback.APPEND);
		
		return list;
	}
	
	private void moveFromLeftToRight() {
		List<Item> selectedItemsLeft = list.getSelectionModel().getSelectedItems();
		List<Item> selectedItemsRight = tree.getSelectionModel().getSelectedItems();
		if (selectedItemsRight.size() > 1) {
			return;
		} else if (selectedItemsRight.size() != 1) {
			for (Item item : selectedItemsLeft) {
				treeStore.add(new Item(item));
				listStore.remove(item);
			}
		} else {
			Item itemRight = selectedItemsRight.get(0);
			if (itemRight.isDir()) {
				for (Item item : selectedItemsLeft) {
					treeStore.add(itemRight, new Item(item));
					listStore.remove(item);
				}
			}
		}
	}


	private Tree<Item, String> createTree() {
		
		treeStore = new TreeStore<Item>(properties.key()){
			@Override
			public void add(Item parent, Item child) {
				if (child.isDir()){
					insert(parent, 0, child);
					parent.addItem(child);
				}
				else
					super.add(parent, child);
			}
			@Override
			public void add(Item root) {
				if (root.isDir()){
					insert(0, root);
				}
				else
					super.add(root);
			}
			@Override
			public void addSubTree(Item parent, int index, List<? extends TreeNode<? extends Item>> children) {
				super.addSubTree(parent, index, children);
				for (TreeNode<? extends Item> node : children) {
					parent.addItem(node.getData());
				}
			}
		};
		
		// for tests
//		rightStore.addSubTree(0, createModels(createSimpleTree()));
		
		tree = new Tree<Item, String>(treeStore, properties.name()){
			@Override
			protected boolean hasChildren(Item item) {
				return (item.isDir()) || super.hasChildren(item);
			}
		};
		tree.setBorders(true);
		tree.setSelectionModel(new TreeSelectionModel<Item>());
		tree.getStyle().setLeafIcon(Icons.INSTANCE.getLeafImage());
		
		new TreeDragSource<Item>(tree){
			@Override
			protected void onDragDrop(DndDropEvent event) {
				ArrayList<TreeStore.TreeNode<Item>> nodeList = (ArrayList<TreeStore.TreeNode<Item>>)event.getData();
				for (TreeNode<Item> node : nodeList) {
					Item it = node.getData();
					try {
						treeStore.getParent(it).getItems().remove(it);
					} catch (Exception e) {
						log(e);
					}
				}
				super.onDragDrop(event);
			}
		};
		
		TreeDropTarget<Item> treeDropTarget = new TreeDropTarget<Item>(tree);
		treeDropTarget.setFeedback(Feedback.APPEND);
		treeDropTarget.setAllowSelfAsSource(true);
		
		addNewFolderBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<Item> selectedItems = tree.getSelectionModel().getSelectedItems();
				if (selectedItems.size() > 1) return;
				if (selectedItems.size() == 0){
					final PromptMessageBox messageBox = promptNewDirName(treeStore, null);
					messageBox.show();
				}else{
					final Item selectedItem = selectedItems.get(0);  
					if (null != selectedItem){
						final PromptMessageBox messageBox = promptNewDirName(treeStore, selectedItem);
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
				List<Item> selectedItemsRight = tree.getSelectionModel().getSelectedItems();
				for (Item item : selectedItemsRight) {
					treeStore.getParent(item).getItems().remove(item);
					List<Item> allChildren = treeStore.getAllChildren(item);
					moveFromRightToLeft(allChildren);
					if (item.isDir())
						treeStore.remove(item);
					else
						moveFromRightToLeft(Arrays.asList(item));
				}
			}

		});
		
		toLeftAllBtn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				moveFromRightToLeft(treeStore.getAll());
			}

		});
		
		return tree;
	}
	
	private void moveFromRightToLeft(List<Item> items) {
		for (Item item : items) {
			if (!(item.isDir())) {
				Item root = new Item(item);
				listStore.add(root);
			}
			treeStore.remove(item);
		}
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

		return Arrays.asList(oned, three);

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

	static void log(Object o){
		if (o instanceof Throwable)
			logger.log(Level.WARNING, ((Throwable)o).getMessage(), ((Throwable)o));
		logger.warning(String.valueOf(o));
	}
	
}
