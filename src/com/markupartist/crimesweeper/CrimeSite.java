package com.markupartist.crimesweeper;

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
