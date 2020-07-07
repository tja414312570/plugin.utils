package com.YaNan.frame.utils.beans;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author YaNan
 * @version 20170326
 */
@Deprecated
public class XMLBean {
	private List<File> fileList = new ArrayList<File>();
	//
	private List<String> elementPath = new ArrayList<String>();
	// The xml document deal with node name
	private String nodeName;
	// The type of object to be generated
	private Class<?> beanClass;
	// back object's list
	private List<Object> beanObjectList = new ArrayList<Object>();
	// Name mapping
	private Map<String, String> nameMapping = new HashMap<String, String>();
	// Type mapping
	private Map<String, Class<?>> typeMapping = new HashMap<String, Class<?>>();
	// Set mapping
	private Map<String, Class<?>> mapMapping = new HashMap<String, Class<?>>();
	// remove mapping
	private List<String> removeNodes = new ArrayList<String>();
	private int scanLevel = -1;
	public Class<?> bean;
	/**
	 * get the xml scan level
	 * @return xml scan level
	 */
	public int getScanLevel() {
		return scanLevel;
	}
	/**
	 * she the xml scan max level,if the value is negative ,the until while scan all level,default level is -1,
	 * @param scanLevel scan level
	 */
	public void setScanLevel(int scanLevel) {
		this.scanLevel = scanLevel;
	}
	/**
	 * get current tools mapping class
	 * @return mapping class
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}
	/**
	 * set current tools mapping class
	 * @param beanClass mapping class
	 */
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	/**
	 * get current tools selected node name
	 * @return node name
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * set current tools node name
	 * @param nodeName node name
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * get bean class
	 * @return bean class
	 */
	public Class<?> getBean() {
		return bean;
	}
	/**
	 * set bean class
	 * @param cls bean class
	 */
	public void setBean(Class<?> cls) {
		this.bean = cls;
	}
	/**
	 * get current tools all file as list
	 * @return file list
	 */
	public List<File> getfileList() {
		return fileList;
	}
	/**
	 * set current tools file list
	 * @param fileList file list
	 */
	public void setfileList(List<File> fileList) {
		this.fileList = fileList;
	}
	/**
	 * add file to current tools
	 * @param file file
	 */
	public void addXMLFile(File file) {
		this.fileList.add(file);
	}
	/**
	 * get current tools read element path
	 * @return element path list
	 */
	public List<String> getElementPath() {
		return elementPath;
	}
	/**
	 * add element path
	 * @param elementPath element path
	 */
	public void addElementPath(String elementPath) {
		this.elementPath.add(elementPath);
	}
	/**
	 * execute decode tools for xml mapping to class
	 * @param <T> any type
	 * @return mapping bean
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> execute() {
		// if XML fileList list is zero,throw exception and stop process
		if (this.fileList.size() == 0) {
			this.exception(new Exception("xml fileList is '0'"));
			return null;
		}
		// if bean class is null throw exception and stop process
		if (this.beanClass == null) {
			this.exception(new Exception("bean class is null"));
			return null;
		}
		// traversal XML fileList list
		Iterator<File> iterator = this.fileList.iterator();
		while (iterator.hasNext()) {
			// if XML fileList is not exist,throw error message but continue
			// execute
			File xmlFile = iterator.next();
			if (!xmlFile.exists()) {
				this.warrn("a xml file is not exist,ignore the xml file,at : " + xmlFile.getAbsolutePath());
			} else {
				// read xmlFile and get document,the love always exists;
				SAXReader reader = new SAXReader();
				Document document;
				try {
					document = reader.read(xmlFile);
					// get document or node from ...maybe first to traversal
					// node path
					// if element path is null , throw exception and stop
					// process
					if (this.elementPath.size() == 0) {
						this.warrn("element list is '0'");
						return null;
					}
					// traversal element path
					Iterator<String> pIterator = this.elementPath.iterator();
					while (pIterator.hasNext()) {
						// if element path is not exists or element content is
						// null,throw error message and continue
						String path = pIterator.next();
						Node pNode = document.selectSingleNode(path);
						if (pNode == null) {
							this.warrn("XML Path expression :" + path);
							continue;
						}
						if (!pNode.hasContent()) {
							this.warrn("XML Path has not any content at : " + path);
							continue;
						}
						// if node name is null , throw error and continue
						if (this.nodeName == null) {
							this.warrn("node name is null at node name :" + this.nodeName);
							return null;
						}
						List<?> nList = pNode.selectNodes(this.nodeName);
						if (nList.size() == 0) {
							continue;
						}
						// traversal node list
						Iterator<?> bIterator = nList.iterator();
						rootElement(bIterator, this.beanObjectList);
					}
				} catch (DocumentException e) {
					this.exception(e);
				}

			}
		}
		return (List<T>) this.beanObjectList;

	}
	/*
	 * iterator root element
	 */
	private void rootElement(Iterator<?> eIterator, List<Object> objectList) {
		while (eIterator.hasNext()) {
			// define a object , if the object is error while throw exception
			// and return null
			Object obj;
			try {
				obj = this.beanClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				this.exception(e1);
				return;
			}
			Element beanElement = (Element) eIterator.next();
			// Attribute
			Attribute(beanElement, obj);
			// Element
			Element(beanElement, obj, 0);
			// add object to object list
			objectList.add(obj);
		}
	}

	/*
	 *  Element analysis
	 */
	private void Element(Element beanElement, Object parentObject, int level) {
		// get every element all attribute Iterator
		if (this.scanLevel > -1 && level++ >= this.scanLevel)
			return;
		Iterator<?> eIterator = beanElement.elementIterator();
		while (eIterator.hasNext()) {
			// get element child element as now
			Element element = (Element) eIterator.next();
			// if the element has any attribute,need cast to Element
			Iterator<?> aIterator = element.attributeIterator();
			try {
				String name = element.getName();
				if (this.removeNodes.contains(name))
					continue;
				name = this.nameMapping.containsKey(name) ? this.nameMapping.get(name) : name;
				if (this.mapMapping.containsKey(name)) {
					this.handleMap(element, name, parentObject, level);
					continue;
				}
				Field field = parentObject.getClass().getDeclaredField(name);
				field.setAccessible(true);
				if (aIterator.hasNext()) {
					Class<?> fCls = field.getType();
					Object childObject = fCls.newInstance();
					Attribute(element, childObject);
					field.set(parentObject, childObject);
				} else {
					field.set(parentObject, castType(element.getTextTrim(), field.getType()));
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
					| InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 *  Attribute analysis
	 */
	private void Attribute(Element beanElement, Object object) {
		// if element has not value attribute,default set the value use element
		// content
		if (beanElement.attribute("value") == null && !beanElement.elementIterator().hasNext()) {
			try {
				Field filed = object.getClass().getDeclaredField("value");
				filed.setAccessible(true);
				filed.set(object, beanElement.getTextTrim());
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			}
		}
		// get every element all attribute Iterator
		Iterator<?> aIterator = beanElement.attributeIterator();
		while (aIterator.hasNext()) {
			// get element attribute as now,need cast to Attribute
			Attribute attr = (Attribute) aIterator.next();
			// traversal bean class field ,this is reflect knowledge
			try {
				String name = attr.getName();
				name = this.nameMapping.containsKey(name) ? this.nameMapping.get(name) : name;
				Field field = object.getClass().getDeclaredField(name);
				field.setAccessible(true);

				field.set(object, castType(attr.getValue().trim(), field.getType()));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	// handle map filed
	private void handleMap(Element element, String name, Object parentObject, int level) throws SecurityException {
		Class<?> cls = this.mapMapping.get(name);
		Method method;
		try {
			method = this.beanClass
					.getMethod("add" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length()), cls);
			if (method != null) {
				Object object = this.mapMapping.get(name).newInstance();
				Element(element, object, level);
				Attribute(element, object);
				method.invoke(parentObject, object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/*
	 * support current tools to cast data type
	 */
	@SuppressWarnings("deprecation")
    static Object castType(Object orgin, Class<?> targetType) {
		// 整形
		if (targetType.equals(int.class))
			return Integer.parseInt(("" + orgin).equals("") ? "0" : "" + orgin);
		if (targetType.equals(short.class))
			return Short.parseShort((String) orgin);
		if (targetType.equals(long.class))
			return Long.parseLong((String) orgin);
		if (targetType.equals(byte.class))
			return Byte.parseByte((String) orgin);
		// 浮点
		if (targetType.equals(float.class))
			return Float.parseFloat("" + orgin);
		if (targetType.equals(double.class))
			return Double.parseDouble((String) orgin);
		// 日期
		if (targetType.equals(Date.class))
			return new Date(orgin + "");
		// 布尔型
		if (targetType.equals(boolean.class))
			return Boolean.parseBoolean((String) orgin);
		// char
		if (targetType.equals(char.class))
			return (char) orgin;
		// 没有匹配到返回源数据
		return orgin;
	}

	// Nameing mapping
	/**
	 * Add a named map to the domain of the object corresponding to the name of
	 * the property such as XML document some element name is class but class
	 * file disable use class as filed name,but you can use other's name replace
	 * class,such as CLASS,you can use this method to Bind the class in the XML
	 * document to the CLASS in the class file
	 * 
	 * @param xmlName xml tag name
	 * @param fieldName class filed name
	 */
	public void addNameMaping(String xmlName, String fieldName) {
		this.nameMapping.put(xmlName, fieldName);

	}
	/**
	 * add type mapping to class
	 * @param field field
	 * @param cls class
	 */
	// Type mapping
	public void addTypeMaping(String field, Class<?> cls) {
		this.typeMapping.put(field, cls);
	}

	// Map mapping
	/**
	 * set tag mapping to class
	 * @param name tag
	 * @param cls mapping class
	 */
	public void addMapMapping(String name, Class<?> cls) {
		this.mapMapping.put(name, cls);
	}
	/**
	 * remove node
	 * @param nodeName node name
	 */
	public void removeNode(String nodeName) {
		this.removeNodes.add(nodeName);
	}

    private void exception(Exception exception) {
		exception.printStackTrace();
	}

	private void warrn(String string) {
		throw new RuntimeException(string);
	}
}
