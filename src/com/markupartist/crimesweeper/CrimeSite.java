package com.markupartist.crimesweeper;
import com.google.android.maps.GeoPoint;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

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

import android.util.Log;
import android.location.Location;

import javax.xml.parsers.DocumentBuilder;

public class CrimeSite extends GeoPoint {
	float collisionDistance = 20.0f;
	private static final String TAG = "Collision: ";
    private String _title;
    	
	String getTitle()
	{
		return _title;
	}

	boolean intersectWithPlayer(Location player)
	{
        Location crimeSiteLocation = new Location(player);
        crimeSiteLocation.setLatitude(this.getLatitudeE6() / 1000000.0);
        crimeSiteLocation.setLongitude(this.getLongitudeE6() / 1000000.0);

        float distance = player.distanceTo(crimeSiteLocation);
        return(distance <= this.collisionDistance);
	}
	
	static List<CrimeSite> getCrimeSites(int minutes)
	{
		List<CrimeSite> crimeSites = new ArrayList<CrimeSite>();
		
		try {
			Document xmlDoc = loadDocument("http://brottsplatsstockholm.se/api.php?action=getEvents&period=" + minutes);
			NodeList nodes = getEventNodes(xmlDoc);
			
			// NOTE: We skip every other element because it's a "text" element.
			// TODO: Look into why this is so, and if we're misusing this API somehow :)
			for(int i = 1; i < nodes.getLength(); i+=2)									
				crimeSites.add(new CrimeSite(getValueByElementName(nodes.item(i), "titleCleaned"),
											 getCordinate(nodes.item(i), "lat"),
											 getCordinate(nodes.item(i), "lng")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return crimeSites;
	}

	CrimeSite(String title, int lat, int lng) {
        super(lat, lng);
		_title = title;
	}
	
private

	static NodeList getEventNodes(Document xmlDoc)
	{
		return xmlDoc.getChildNodes().item(0).getChildNodes();
	}

	static Document loadDocument(String url) throws MalformedURLException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
	{
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return docBuilder.parse(new URL(url).openConnection().getInputStream());	
	}

	static int getCordinate(Node node, String name)
	{
		return (int)(Float.valueOf(getValueByElementName(node, name)).doubleValue() * 1000000);
	}

	static String getValueByElementName(Node eventNode, String elementName)
	{
		NodeList eventChildren = eventNode.getChildNodes();
		for(int i = 1; i < eventChildren.getLength(); i += 2)
		{
			Node node = eventChildren.item(i);
			if(node.getNodeName().equals(elementName))
				return getValueForNode(node);
		}
		
		return "";
	}
	
	static String getValueForNode(Node node)
	{
		// TODO: Why do we have to do this? Seems a bit odd.
		NodeList contentNodes = node.getChildNodes();
		Node cdata_node = contentNodes.item(contentNodes.getLength() - 1);
		return cdata_node.getNodeValue();		
	}

	/*
	 * The great circle distance d between two points with coordinates {lat1,lon1} and {lat2,lon2} is given by:
	 * d=acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon1-lon2))
	 */
	double distanceBetweenCoords(int lat1, int lon1, int lat2, int lon2) {
		return Math.acos( 
			Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2)
		);
	}		
}
