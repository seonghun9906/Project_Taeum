package com.icia.Taeumproject.util.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Document {
	private Double x;
	private Double y;
	@JsonProperty("place_name")
	private String placeName;
	private String phone;
  /*
   * @JsonProperty("address_name") private String address_name;
   */

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}
	
	public String getPlaceName() {
		return placeName;
	}
	public String getPhone() {
		return phone;
	}
	
}