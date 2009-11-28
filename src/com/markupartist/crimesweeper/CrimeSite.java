package com.markupartist.crimesweeper;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class CrimeSite {

	String getTitle()
	{
		return _title;
	}
	
	int getLat()
	{
		return _lat;
	}
	
	int getLng()
	{
		return _lng;
	}
	
	static List<CrimeSite> GetCrimeSites(int minutes)
	{
		List<CrimeSite> crimeSites = new ArrayList<CrimeSite>();
		
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			URL url = new URL("http://brottsplatsstockholm.se/api.php?action=getEvents&period=" + minutes);	
			Document xmlDoc = docBuilder.parse(url.openConnection().getInputStream());
			NodeList nodes = xmlDoc.getChildNodes().item(0).getChildNodes();
			//NodeList name = nodes.item(0).getChildNodes();
			for(int i = 1; i < nodes.getLength(); i+=2)
			{
				int lat = (int)(Float.valueOf(getValue(nodes.item(i), "lat")).doubleValue() * 1000000);
				int lng = (int)(Float.valueOf(getValue(nodes.item(i), "lng")).doubleValue() * 1000000);									
				crimeSites.add(new CrimeSite(getValue(nodes.item(i), "titleCleaned"), lat, lng));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return crimeSites;
	}
	
	CrimeSite(String title, int lat, int lng) {
		_title = title;
		_lat = lat;
		_lng = lng;
	}
	
private

	static String getValue(Node eventNode, String elementName) throws Exception
	{
		NodeList eventChildren = eventNode.getChildNodes();
		for(int i = 1; i < eventChildren.getLength(); i += 2)
		{
			Node node = eventChildren.item(i);
			if(node.getNodeName().equals(elementName)) {		
				NodeList contentNodes = node.getChildNodes();
				Node cdata_node = contentNodes.item(contentNodes.getLength() - 1);
				return cdata_node.getNodeValue();
			}
		}
		
		return "";
	}
	
	String _title;
	int _lat;
	int _lng;
	
}
