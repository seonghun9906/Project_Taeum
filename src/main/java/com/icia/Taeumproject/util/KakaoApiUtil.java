package com.icia.Taeumproject.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icia.Taeumproject.Dto.RouteInfo;
import com.icia.Taeumproject.util.kakao.Document;
import com.icia.Taeumproject.util.kakao.KakaoAddress;
import com.icia.Taeumproject.util.kakao.KakaoDirections;
import com.icia.Taeumproject.util.kakao.KakaoDirections.Route;
import com.icia.Taeumproject.util.kakao.KakaoDirections.Route.Section.Road;
import com.icia.Taeumproject.util.kakao.KakaoDirections.Route.Summary;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KakaoApiUtil {
	private static final String REST_API_KEY = "ef3ca41398cd7425a003013716d178c9";

	/**
	 * 키워드 장소 검색
	 * 
	 * @param address 주소
	 * @return 좌표
	 */
	public static List<Point> getPointByKeyword(String keyword, Point center) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
		url += "?query=" + URLEncoder.encode(keyword, "UTF-8");
		url += "&category_group_code=PM9";
		url += "&x=" + center.getX();
		url += "&y=" + center.getY();
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + REST_API_KEY)//
				.uri(URI.create(url))//
				.GET()//
				.build();

		System.out.println(request.headers());

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();
		System.out.println(responseBody);

		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		List<Document> documents = kakaoAddress.getDocuments();
		if (documents.isEmpty()) {
			return null;
		}

		List<Point> pointList = new ArrayList<>();
		for (Document document : documents) {
			Point point = new Point(document.getX(), document.getY());
      /*
       * point.setName(document.getPlaceName()); point.setPhone(document.getPhone());
       */
			pointList.add(point);
		}
		return pointList;
	}

	/**
	 * 자동차 길찾기
	 * 
	 * @param from 출발지
	 * @param to   도착지
	 * @return 이동 좌표 목록
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static List<Point> getVehiclePaths(Point from, Point to) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://apis-navi.kakaomobility.com/v1/directions";
		url += "?origin=" + from.getX() + "," + from.getY();
		url += "&destination=" + to.getX() + "," + to.getY();
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + REST_API_KEY)//
				.header("Content-Type", "application/json")//
				.uri(URI.create(url))//
				.GET()//
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();

		List<Point> pointList = new ArrayList<Point>();

		KakaoDirections kakaoDirections = new ObjectMapper().readValue(responseBody, KakaoDirections.class);
		List<Road> roads = kakaoDirections.getRoutes().get(0).getSections().get(0).getRoads();
		for (Road road : roads) {
			List<Double> vertexes = road.getVertexes();
			for (int i = 0; i < vertexes.size(); i++) {
				pointList.add(new Point(vertexes.get(i), vertexes.get(++i)));
			}
		}

		return pointList;

	}

	/**
	 * 주소 -> 좌표 변환
	 * 
	 * @param fromAddress 주소
	 * @return 좌표
	 */
	public static Point getPointByAddress(String startAddress) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/address.json";
		url += "?query=" + URLEncoder.encode(startAddress, "UTF-8");
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + REST_API_KEY)//
				.uri(URI.create(url))//
				.GET()//
				.build();

		//System.out.println(request.headers());

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();
		//System.out.println(responseBody);

		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		List<Document> documents = kakaoAddress.getDocuments();
		if (documents.isEmpty()) {
			return null;
		}
		Document document = documents.get(0);
		return new Point(document.getX(), document.getY());
	}
	
	
  /**
   * 출발지와 도착지 사이의 자동차 경로를 가져오는 메서드
   * 
   * @param from 출발지 좌표
   * @param to   도착지 좌표
   * @return 경로 정보 KakaoDirections
   * @throws IOException
   * @throws InterruptedException
   */
  public static KakaoDirections getKakaoDirections(Point from, Point to) throws IOException, InterruptedException {
    // HttpClient 객체를 생성합니다.
    HttpClient client = HttpClient.newHttpClient();
    // 요청할 URL을 생성합니다.
    String url = "https://apis-navi.kakaomobility.com/v1/directions";
    url += "?origin=" + from.getX() + "," + from.getY();
    url += "&destination=" + to.getX() + "," + to.getY();
    // HTTP 요청 객체를 생성합니다.
    HttpRequest request = HttpRequest.newBuilder().header("Authorization", "KakaoAK " + REST_API_KEY)
        .header("Content-Type", "application/json").uri(URI.create(url)).GET().build();
    // HTTP 요청을 보내고 응답을 받습니다.
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    // 응답 바디를 문자열로 가져옵니다.
    String responseBody = response.body();

    // JSON 응답을 KakaoDirections 객체로 변환합니다.
    KakaoDirections kakaoDirections = new ObjectMapper().readValue(responseBody, KakaoDirections.class);

    return kakaoDirections;
  }
  
  public static RouteInfo calculateRouteInfo(Point startPoint, Point endPoint) {
      try {
          KakaoDirections kakaoDirections = getKakaoDirections(startPoint, endPoint);
          List<Route> routes = kakaoDirections.getRoutes();
 
          if (routes == null || routes.isEmpty()) {
              // 경로가 없는 경우 처리
              return new RouteInfo(0, 0); // 거리와 소요시간을 0으로 설정하거나 다른 방법으로 처리
          }

          Route route = routes.get(0);
          Summary summary = route.getSummary();
          Integer distance = summary.getDistance();
          Integer duration = summary.getDuration();

          // 거리와 시간 정보를 객체로 반환
          return new RouteInfo(distance, duration);
      } catch (IOException | InterruptedException e) {
          // 예외 발생 시 처리
          log.error("Error occurred while calculating route info: {}", e.getMessage());
          return new RouteInfo(0, 0); // 예외 발생 시 거리와 소요시간을 0으로 설정하거나 다른 방법으로 처리
      }
  }
	
	
	@Getter
	@Setter
	@ToString
	public static class Point {
		private Double x;
		private Double y;
		
		public Point() {
	    	
	    }
		
		public Point(Double x, Double y) {
			this.x = x;
			this.y = y;
		}

		

	}
}
