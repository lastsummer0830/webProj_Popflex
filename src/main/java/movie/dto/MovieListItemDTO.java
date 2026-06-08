package movie.dto;

public class MovieListItemDTO {

    // KMDb movieId
    private String kmdbMovieId;

    // KMDb movieSeq
    private String kmdbMovieSeq;

    // 영화 제목
    private String title;

    // 감독명
    private String directorNm;

    // 화면에 보여줄 개봉일
    private String displayReleaseDate;

    // 포스터 이미지 URL
    private String posterUrl;

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

    public String getDisplayReleaseDate() {
        return displayReleaseDate;
    }

    public void setDisplayReleaseDate(String displayReleaseDate) {
        this.displayReleaseDate = displayReleaseDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}