package com.axmexa.gxtapp.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.axmexa.gxtapp.client.model.Item;
import com.axmexa.gxtapp.client.service.TreeLoadService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

public class TreeLoadServiceImpl extends RemoteServiceServlet implements
		TreeLoadService {

	private static final Logger logger = Logger.getLogger(TreeLoadServiceImpl.class.getSimpleName());

	@Override
	public Item[] getItems() {

		return createSimpleTree();
	}

	private Item[] createSimpleTree() {

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
		logger.warning("List: " + Arrays.asList(oned, three));

		return new Item[]{oned, three};

	}

	private List<TreeNode<Item>> createModels(List<? extends Item> sourceRootIrems) {
		ArrayList<TreeNode<Item>> rootModels = new ArrayList<TreeNode<Item>>();
		for (Item item : sourceRootIrems) {
			rootModels.add(buildNode(item));
		}
		return rootModels;
	}

	private TreeNode<Item> buildNode(final Item rootItem) {

		return new TreeNode<Item>() {

			@Override
			public List<TreeNode<Item>> getChildren() {
				List<TreeNode<Item>> childs = null;
				if (rootItem.isDir()) {
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
}
