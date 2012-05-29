package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

public class Node<T> {

	public T data;
	public List<Node<T>> children;

	public Node() {
		super();
	}

	public Node(T data) {
		this();
		setData(data);
	}

	public List<Node<T>> getChildren() {
		if (this.children == null) {
			return new ArrayList<Node<T>>();
		}
		return this.children;
	}

	public T getChildData(int position) {
		Node<T> nodeNow;
		nodeNow = children.get(position);
		T temp;
		temp = nodeNow.getData();
		return temp;

	}

	public void removeChildren() {
		children.clear();
		children = null;
	}

	public void setChildren(List<Node<T>> children) {
		this.children = children;
	}

	public int getNumberOfChildren() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	public void addChild(Node<T> child) {
		if (children == null) {
			children = new ArrayList<Node<T>>();
		}
		children.add(child);
	}

	public static void recursiveFun(List<Node<HashMap<String, String>>> parent,
			String id, Node<HashMap<String, String>> nodeToAdd, Context ctx) {
		for (int i = 0; i < parent.size(); i++) {
			Node<HashMap<String, String>> nd = parent.get(i);
			if (nd.getData() != null) {
				if (nd.getData().get("name").matches(id)) {

					nd.addChild(nodeToAdd);

				}

			}

			if (nd.getNumberOfChildren() > 0) {
				recursiveFun(nd.getChildren(), id, nodeToAdd, ctx);
			}
		}

	}

	public static void recursiveFunHide(
			List<Node<HashMap<String, String>>> parent, String id,
			Node<HashMap<String, String>> nodeToAdd) {
		for (int i = 0; i < parent.size(); i++) {
			Node<HashMap<String, String>> nd = parent.get(i);
			if (nd.getData() != null) {
				if (nd.getData().get("id").matches(id)) {
					if (nd.getData() != null) {

						if (nd.getNumberOfChildren() > 0) {
							nodeToAdd.setData(nd.getData());
							nodeToAdd.setChildren(nd.getChildren());
							nd.removeChildren();
						}
					}
				}

			}

			if (nd.getNumberOfChildren() > 0) {
				recursiveFunHide(nd.getChildren(), id, nodeToAdd);
			}
		}

	}

	public static List<Node<HashMap<String, String>>> recursiveFunFind(
			List<Node<HashMap<String, String>>> parent, String id,
			Node<HashMap<String, String>> nodeToAdd, Context ctx) {
		for (int i = 0; i < parent.size(); i++) {
			Node<HashMap<String, String>> nd = parent.get(i);
			if (nd.getData() != null) {
				if (nd.getData().get("id").matches(id)) {
					return nd.getChildren();
				}

			}

			if (nd.getNumberOfChildren() > 0) {
				recursiveFunFind(nd.getChildren(), id, nodeToAdd, ctx);
			}
		}
		return null;

	}

	public static void recursiveFunShow(
			List<Node<HashMap<String, String>>> parent, String id,
			List<Node<HashMap<String, String>>> listToAdd) {
		for (int i = 0; i < parent.size(); i++) {
			Node<HashMap<String, String>> nd = parent.get(i);
			if (nd.getData() != null) {
				if (nd.getData().get("id").matches(id)) {
					nd.setChildren(listToAdd);
				}

			}

			if (nd.getNumberOfChildren() > 0) {
				recursiveFunShow(nd.getChildren(), id, listToAdd);
			}
		}

	}

	public void insertChildAt(int index, Node<T> child)
			throws IndexOutOfBoundsException {
		if (index == getNumberOfChildren()) {
			// this is really an append
			addChild(child);
			return;
		} else {
			children.get(index); // just to throw the exception, and stop
			// here
			children.add(index, child);
		}
	}

	public void removeChildAt(int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public T getData() {
		return this.data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(getData().toString()).append(",[");
		int i = 0;
		for (Node<T> e : getChildren()) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(e.getData().toString());
			i++;
		}
		sb.append("]").append("}");
		return sb.toString();
	}
}
