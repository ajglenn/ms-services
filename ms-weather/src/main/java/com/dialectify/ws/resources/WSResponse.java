package com.dialectify.ws.resources;

public class WSResponse
{
	private String city;
	private String weather;

	public WSResponse(String city, String weather)
	{
		this.city = city;
		this.weather = weather;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getWeather()
	{
		return weather;
	}

	public void setWeather(String weather)
	{
		this.weather = weather;
	}

}
