package movie.service;

import movie.dto.MovieApiSearchResultDTO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import common.config.AppConfig;
import movie.dto.MovieDTO;

public class MovieApiService {

    public static class MovieSearchResult {
        private ArrayList<MovieDTO> movies;
        private int totalCount;
        private int currentPage;
        private int pageSize;
        private int totalPage;

        public MovieSearchResult(ArrayList<MovieDTO> movies, int totalCount, int currentPage, int pageSize) {
            this.movies = movies;
            this.totalCount = totalCount;
            this.currentPage = currentPage;
            this.pageSize = pageSize;

            if (totalCount > 0) {
                this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
            } else {
                this.totalPage = 0;
            }
        }

        public ArrayList<MovieDTO> getMovies() {
            return movies;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }
    }

    	// 기존 코드 호환용 검색 메서드
    	public ArrayList<MovieDTO> searchMovies(String query) {
    		return searchMovies(query, 1, 12).getMovies();
    	}

    	// 페이징을 적용한 영화 검색 메서드
    	public MovieApiSearchResultDTO searchMovies(String query, int page, int pageSize) {
    ArrayList<MovieDTO> list = new ArrayList<MovieDTO>();
    int totalCount = 0;

    try {
        String apiUrl = AppConfig.getKmdbApiUrl();
        String serviceKey = AppConfig.getKmdbServiceKey();

        if (query == null) {
            query = "";
        }

        query = query.trim();

        if (query.isEmpty()) {
            return new MovieApiSearchResultDTO(list, 0);
        }

        if (page < 1) {
            page = 1;
        }

        if (pageSize < 1) {
            pageSize = 12;
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

        int startCount = (page - 1) * pageSize;

        String requestUrl = apiUrl
                + "?ServiceKey=" + serviceKey
                + "&collection=kmdb_new2"
                + "&query=" + encodedQuery
                + "&detail=Y"
                + "&listCount=" + pageSize
                + "&startCount=" + startCount;

        String json = sendGet(requestUrl);

//        System.out.println(json); 콘솔에 잘 나오는지 확인용 끝나면 지우기

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray dataArr = root.getAsJsonArray("Data");

        if (dataArr != null && dataArr.size() > 0) {
            JsonObject dataObj = dataArr.get(0).getAsJsonObject();

            totalCount = getTotalCount(root, dataObj);

            JsonArray resultArr = getJsonArray(dataObj, "Result");

            if (resultArr != null) {
                for (JsonElement element : resultArr) {
                    JsonObject movieObj = element.getAsJsonObject();
                    MovieDTO movie = parseMovie(movieObj);

                    list.add(movie);
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return new MovieApiSearchResultDTO(list, totalCount);
    	}

    	// 2. 영화 상세 조회
    	public MovieDTO findMovieDetail(String kmdbMovieId, String kmdbMovieSeq) {
        MovieDTO movie = null;

        try {
            String apiUrl = AppConfig.getKmdbApiUrl();
            String serviceKey = AppConfig.getKmdbServiceKey();

            String requestUrl = apiUrl
                    + "?ServiceKey=" + serviceKey
                    + "&collection=kmdb_new2"
                    + "&movieId=" + URLEncoder.encode(kmdbMovieId, StandardCharsets.UTF_8.toString())
                    + "&movieSeq=" + URLEncoder.encode(kmdbMovieSeq, StandardCharsets.UTF_8.toString())
                    + "&detail=Y";

            String json = sendGet(requestUrl);

            // 테스트 끝나면 주석 처리 가능
            System.out.println(json);

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray dataArr = root.getAsJsonArray("Data");

            if (dataArr != null && dataArr.size() > 0) {
                JsonObject dataObj = dataArr.get(0).getAsJsonObject();
                JsonArray resultArr = getJsonArray(dataObj, "Result");

                if (resultArr != null && resultArr.size() > 0) {
                    JsonObject movieObj = resultArr.get(0).getAsJsonObject();

                    movie = parseMovie(movieObj);

                    if (movie.getKmdbMovieId() == null || movie.getKmdbMovieId().isEmpty()) {
                        movie.setKmdbMovieId(kmdbMovieId);
                    }

                    if (movie.getKmdbMovieSeq() == null || movie.getKmdbMovieSeq().isEmpty()) {
                        movie.setKmdbMovieSeq(kmdbMovieSeq);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movie;
    }

    // 공통 GET 요청 메서드
    private String sendGet(String requestUrl) throws Exception {
        StringBuilder result = new StringBuilder();

        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();

        BufferedReader br;

        if (responseCode >= 200 && responseCode < 300) {
            br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );
        } else {
            br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)
            );
        }

        String line;

        while ((line = br.readLine()) != null) {
            result.append(line);
        }

        br.close();
        conn.disconnect();

        return result.toString();
    }

    private String getString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return "";
        }

        return obj.get(key).getAsString();
    }

    // JSON 객체에서 int 값을 안전하게 꺼내는 메서드
private int getInt(JsonObject obj, String key) {
    if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
        return 0;
    }

    try {
        String value = obj.get(key).getAsString();
        value = value.replace(",", "").trim();

        if (value.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(value);
    } catch (Exception e) {
        return 0;
    }
}

// API 응답에서 전체 검색 결과 개수를 찾는 메서드
private int getTotalCount(JsonObject root, JsonObject dataObj) {
    int totalCount = 0;

    totalCount = getInt(dataObj, "TotalCount");
    if (totalCount > 0) return totalCount;

    totalCount = getInt(dataObj, "totalCount");
    if (totalCount > 0) return totalCount;

    totalCount = getInt(dataObj, "TotalCnt");
    if (totalCount > 0) return totalCount;

    totalCount = getInt(dataObj, "totalCnt");
    if (totalCount > 0) return totalCount;

    totalCount = getInt(root, "TotalCount");
    if (totalCount > 0) return totalCount;

    totalCount = getInt(root, "totalCount");
    if (totalCount > 0) return totalCount;

    return 0;
}

// JSON 값이 배열 또는 객체일 때 모두 JsonArray로 맞춰주는 메서드
private JsonArray getJsonArray(JsonObject obj, String key) {
    if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
        return null;
    }

    JsonElement element = obj.get(key);

    if (element.isJsonArray()) {
        return element.getAsJsonArray();
    }

    JsonArray arr = new JsonArray();
    arr.add(element);

    return arr;
}

    private String cleanTitle(String title) {
        if (title == null) {
            return "";
        }

        return title.replace("!HS", "")
                    .replace("!HE", "")
                    .trim();
    }

    private String getFirstPoster(String posters) {
        if (posters == null || posters.trim().isEmpty()) {
            return "";
        }

        String[] arr = posters.split("\\|");

        if (arr.length > 0) {
            return arr[0];
        }

        return "";
    }

    private String getDirectorNames(JsonObject movieObj) {
        if (!movieObj.has("directors")) {
            return "";
        }

        JsonObject directorsObj = movieObj.getAsJsonObject("directors");

        if (directorsObj == null || !directorsObj.has("director")) {
            return "";
        }

        JsonArray directorArr = getJsonArray(directorsObj, "director");

        if (directorArr == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < directorArr.size(); i++) {
            JsonObject director = directorArr.get(i).getAsJsonObject();
            String name = getString(director, "directorNm").trim();

            if (!name.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(name);
            }
        }

        return sb.toString();
    }

    private String getActorNames(JsonObject movieObj) {
        if (!movieObj.has("actors")) {
            return "";
        }

        JsonObject actorsObj = movieObj.getAsJsonObject("actors");

        if (actorsObj == null || !actorsObj.has("actor")) {
            return "";
        }

        JsonArray actorArr = getJsonArray(actorsObj, "actor");

        if (actorArr == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        int count = Math.min(actorArr.size(), 5);

        for (int i = 0; i < count; i++) {
            JsonObject actor = actorArr.get(i).getAsJsonObject();
            String name = getString(actor, "actorNm").trim();

            if (!name.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(name);
            }
        }

        return sb.toString();
    }

    private String getKoreanPlot(JsonObject movieObj) {
        if (!movieObj.has("plots")) {
            return "";
        }

        JsonObject plotsObj = movieObj.getAsJsonObject("plots");

        if (plotsObj == null || !plotsObj.has("plot")) {
            return "";
        }

        JsonArray plotArr = getJsonArray(plotsObj, "plot");

        if (plotArr == null) {
            return "";
        }

        for (int i = 0; i < plotArr.size(); i++) {
            JsonObject plot = plotArr.get(i).getAsJsonObject();

            String plotLang = getString(plot, "plotLang");
            String plotText = getString(plot, "plotText");

            if ("한국어".equals(plotLang)) {
                return plotText;
            }
        }

        if (plotArr.size() > 0) {
            JsonObject firstPlot = plotArr.get(0).getAsJsonObject();
            return getString(firstPlot, "plotText");
        }

        return "";
    }

    private String getRatingGrade(JsonObject movieObj) {
        if (!movieObj.has("ratings")) {
            return "";
        }

        JsonObject ratingsObj = movieObj.getAsJsonObject("ratings");

        if (ratingsObj == null || !ratingsObj.has("rating")) {
            return "";
        }

        JsonArray ratingArr = getJsonArray(ratingsObj, "rating");

        if (ratingArr == null || ratingArr.size() == 0) {
            return "";
        }

        JsonObject rating = ratingArr.get(0).getAsJsonObject();

        String ratingGrade = getString(rating, "ratingGrade");

        if (ratingGrade.contains("||")) {
            return ratingGrade.split("\\|\\|")[0];
        }

        return ratingGrade;
    }

    private String getVodUrl(JsonObject movieObj) {
        if (!movieObj.has("vods")) {
            return "";
        }

        JsonObject vodsObj = movieObj.getAsJsonObject("vods");

        if (vodsObj == null || !vodsObj.has("vod")) {
            return "";
        }

        JsonArray vodArr = getJsonArray(vodsObj, "vod");

        if (vodArr == null || vodArr.size() == 0) {
            return "";
        }

        JsonObject firstVod = vodArr.get(0).getAsJsonObject();

        return getString(firstVod, "vodUrl");
    }

    private MovieDTO parseMovie(JsonObject movieObj) {
        MovieDTO movie = new MovieDTO();

        movie.setDocid(getString(movieObj, "DOCID"));
        movie.setKmdbMovieId(getString(movieObj, "movieId"));
        movie.setKmdbMovieSeq(getString(movieObj, "movieSeq"));

        movie.setTitle(cleanTitle(getString(movieObj, "title")));
        movie.setDirectorNm(getDirectorNames(movieObj));
        movie.setActorNm(getActorNames(movieObj));
        movie.setCompany(getString(movieObj, "company"));

        movie.setPlot(getKoreanPlot(movieObj));
        movie.setRuntime(getString(movieObj, "runtime"));
        movie.setRating(getString(movieObj, "rating"));
        movie.setGenre(getString(movieObj, "genre"));
        movie.setRatingGrade(getRatingGrade(movieObj));

        movie.setReleaseDate(getString(movieObj, "repRlsDate"));
        movie.setKeywords(getString(movieObj, "keywords"));

        String posters = getString(movieObj, "posters");
        movie.setPosterUrl(getFirstPoster(posters));

        movie.setVodUrl(getVodUrl(movieObj));

        return movie;
    }

//    public static void main(String[] args) {
//        MovieApiService service = new MovieApiService();
//
//        MovieSearchResult result = service.searchMovies("파묘", 1, 12);
//
//        System.out.println("총 검색 결과 수: " + result.getTotalCount());
//        System.out.println("총 페이지 수: " + result.getTotalPage());
//
//        for (MovieDTO movie : result.getMovies()) {
//            System.out.println(movie);
//        }
//    }
}