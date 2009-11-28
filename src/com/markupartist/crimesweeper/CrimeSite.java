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
	int _lat;
	int _lng;    
	
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

//    static List<CrimeSite> getCrimeSites(int minutes) {
//        List<CrimeSite> crimeSites = new ArrayList<CrimeSite>();
//        crimeSites.add(new CrimeSite("Grand Theft Auto", 59414207, 18273497));
//        crimeSites.add(new CrimeSite("Murder One", 59514207, 18173497));
//        crimeSites.add(new CrimeSite("Close to me", 59279986, 1808275));
//
//        return crimeSites;
//    }
//	
	CrimeSite(String title, int lat, int lng) {
        super(lat, lng);
		_title = title;
	}

	/*
	 * The great circle distance d between two points with coordinates {lat1,lon1} and {lat2,lon2} is given by:
	 * d=acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon1-lon2))
	 */
	
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
	
	double distanceBetweenCoords(int lat1, int lon1, int lat2, int lon2) {
		return Math.acos( 
			Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2)
		);
	}		
}
