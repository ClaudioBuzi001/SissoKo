/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 *
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 *
 */
package com.view.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.mvvm.ViewModelBase;
import com.leonardo.infrastructure.mvvm.interfaces.IView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;

/**
 * Carica le scene dai file FXML e li associa al ViewModel.
 *
 * @author rbragaglia
 */
public class SceneLoader {

	/**
	 * Load.
	 *
	 * @param sceneName  the scene name
	 * @param controller the controller
	 * @return the optional
	 */
	private static Optional<SceneLoaderData> load(String sceneName, IView controller) {
		try {
			FXMLLoader loader = new FXMLLoader();
			URL resource = new URL(
					"file:" + ResourceManager.getResourcesPath() + File.separator + "scenes/" + sceneName + ".fxml");
			loader.setLocation(resource);
			if (controller != null) {
				loader.setController(controller);
			}
			Node fxml = loader.load();
			patchResources(fxml);
			Object fxmlController = loader.getController();
			fxml.getProperties().put("controller", fxmlController);

			return Optional.of(new SceneLoaderData(fxml, fxmlController));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Optional.empty();

	}

	/**
	 * Load.
	 *
	 * @param sceneName   the scene name
	 * @param dataContext the data context
	 * @param controller  the controller
	 * @return the optional
	 */
	public static Optional<SceneLoaderData> load(String sceneName, ViewModelBase dataContext, IView controller) {
		Optional<SceneLoaderData> sceneData = SceneLoader.load(sceneName, controller);
		sceneData.ifPresent(data -> Generics.ifTryCast(data.FxmlController, IView.class)
				.ifPresent(view -> view.setDataContext(data.Fxml, dataContext)));
		return sceneData;
	}

	/**
	 * Patch resources.
	 *
	 * @param node the node
	 */
	private static void patchResources(Node node) {
		Generics.ifTryCast(node, ImageView.class).ifPresent(iv -> patch(iv));
		for (Node child : getChildren(node)) {
			patchResources(child);
		}
	}

	/**
	 * Patch.
	 *
	 * @param iv the iv
	 */
	private static void patch(ImageView iv) {
		System.out.println(iv.getImage().getUrl());
		iv.setImage(ResourceManager.getImageResource(new File(iv.getImage().getUrl())));
	}

	/**
	 * Gets the children.
	 *
	 * @param node the node
	 * @return the children
	 */
	private static List<Node> getChildren(Node node) {
		List<Node> retList = new ArrayList<>();
		Parent parent = Generics.tryCast(node, Parent.class);
		if (parent != null) {
			retList.addAll(parent.getChildrenUnmodifiable());
		}

		MenuBar menuBar = Generics.tryCast(node, MenuBar.class);
		if (menuBar != null) {
			retList.addAll(getChildren(menuBar));
		}
		ToolBar toolBar = Generics.tryCast(node, ToolBar.class);
		if (toolBar != null) {
			retList.addAll(toolBar.getItems());
		}
		Generics.ifTryCast(node, Button.class).ifPresent(b -> {
			if (b.getGraphic() != null) {
				retList.add(b.getGraphic());
			}
		});
		return retList;
	}

	/**
	 * Gets the children.
	 *
	 * @param menuBar the menu bar
	 * @return the children
	 */
	private static List<Node> getChildren(MenuBar menuBar) {
		List<Node> retList = new ArrayList<>();
		for (Menu menu : menuBar.getMenus()) {
			retList.addAll(getChildren(menu));
		}
		return retList;
	}

	/**
	 * Gets the children.
	 *
	 * @param menuItem the menu item
	 * @return the children
	 */
	private static List<Node> getChildren(MenuItem menuItem) {
		List<Node> retList = new ArrayList<>();
		if (menuItem.getGraphic() != null) {
			retList.add(menuItem.getGraphic());
		}
		if (menuItem instanceof Menu) {
			for (MenuItem subItem : ((Menu) menuItem).getItems()) {
				retList.addAll(getChildren(subItem));
			}
		}
		return retList;
	}

	/**
	 * Contiene gli oggetti della vista necessari alla gestione MVVM.
	 *
	 * @author rbragaglia
	 *
	 */
	public static class SceneLoaderData {

		/** Nodo Jfx root del Form editato con scenebuilder. */
		private final Node Fxml;

		/**
		 * Gets the fxml.
		 *
		 * @return the fxml
		 */
		public Node getFxml() {
			return Fxml;
		}

		/**
		 * Gets the fxml controller.
		 *
		 * @return the fxml controller
		 */
		public Object getFxmlController() {
			return FxmlController;
		}

		/** Relativo controller ( code behind ). */
		private final Object FxmlController;

		/**
		 * Instantiates a new scene loader data.
		 *
		 * @param fxml           the fxml
		 * @param fxmlController the fxml controller
		 */
		SceneLoaderData(Node fxml, Object fxmlController) {
			Fxml = fxml;
			FxmlController = fxmlController;
		}
	}

}
