package movie.dto;

public class MovieDTO {
    private int movieId;             // 우리 DB 영화 고유번호
    private String kmdbMovieId;      // KMDb movieId
    private String kmdbMovieSeq;     // KMDb movieSeq

    private String docid;        // 등록ID
    private String title;        // 영화명
    private String directorNm;   // 감독명
    private String actorNm;      // 배우명
    private String company;      // 제작사
    private String plot;         // 줄거리
    private String runtime;      // 대표 상영시간
    private String rating;       // 대표 관람등급
    private String genre;        // 장르
    private String ratingGrade;  // 관람기준
    private String releaseDate;  // 개봉일
    private String keywords;     // 키워드
    private String posterUrl;    // 포스터 이미지 URL
    private String vodUrl;       // 예고편 URL

    public MovieDTO() {
    }

    public MovieDTO(String docid, String title, String directorNm, String actorNm, String company,
                    String plot, String runtime, String rating, String genre, String ratingGrade,
                    String releaseDate, String keywords, String posterUrl, String vodUrl) {
        this.docid = docid;
        this.title = title;
        this.directorNm = directorNm;
        this.actorNm = actorNm;
        this.company = company;
        this.plot = plot;
        this.runtime = runtime;
        this.rating = rating;
        this.genre = genre;
        this.ratingGrade = ratingGrade;
        this.releaseDate = releaseDate;
        this.keywords = keywords;
        this.posterUrl = posterUrl;
        this.vodUrl = vodUrl;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getKmdbMovieId() {
        return kmdbMovieId;
    }

    public void setKmdbMovieId(String kmdbMovieId) {
        this.kmdbMovieId = kmdbMovieId;
    }

    public String getKmdbMovieSeq() {
        return kmdbMovieSeq;
    }

    public void setKmdbMovieSeq(String kmdbMovieSeq) {
        this.kmdbMovieSeq = kmdbMovieSeq;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirectorNm() {
        return directorNm;
    }

    public void setDirectorNm(String directorNm) {
        this.directorNm = directorNm;
    }

    public String getActorNm() {
        return actorNm;
    }

    public void setActorNm(String actorNm) {
        this.actorNm = actorNm;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRatingGrade() {
        return ratingGrade;
    }

    public void setRatingGrade(String ratingGrade) {
        this.ratingGrade = ratingGrade;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getVodUrl() {
        return vodUrl;
    }

    public void setVodUrl(String vodUrl) {
        this.vodUrl = vodUrl;
    }

    @Override
    public String toString() {
        return "MovieDTO [movieId=" + movieId
                + ", kmdbMovieId=" + kmdbMovieId
                + ", kmdbMovieSeq=" + kmdbMovieSeq
                + ", docid=" + docid
                + ", title=" + title
                + ", directorNm=" + directorNm
                + ", actorNm=" + actorNm
                + ", company=" + company
                + ", plot=" + plot
                + ", runtime=" + runtime
                + ", rating=" + rating
                + ", genre=" + genre
                + ", ratingGrade=" + ratingGrade
                + ", releaseDate=" + releaseDate
                + ", keywords=" + keywords
                + ", posterUrl=" + posterUrl
                + ", vodUrl=" + vodUrl
                + "]";
    }
}