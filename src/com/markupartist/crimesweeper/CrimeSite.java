package com.markupartist.crimesweeper;

import javax.xml.parsers.DocumentBuilder;

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
	
	static void GetCrimeSites(int minutes) {
		//new DocumentBuilder()
	}
	
	CrimeSite(String title, int lat, int lng) {
		_title = title;
		_lat = lat;
		_lng = lng;
	}
	
private
	
	String _title;
	int _lat;
	int _lng;
	
}
