package review.dto;

public class ReviewStatDTO {

    private int movieId;
    // 어떤 영화의 통계인지 - DB: MOVIE_ID

    private int totalCount;
    // 해당 영화의 전체 리뷰 수
    // SQL의 COUNT(*) 결과

    private int burstCount;
    // 터졌다(fresh_yn='Y') 리뷰 수
    // SQL의 SUM(CASE WHEN fresh_yn='Y' THEN 1 ELSE 0 END) 결과

    private int notBurstCount;
    // 안터졌다(fresh_yn='N') 리뷰 수
    // Java에서 (totalCount - burstCount)로 계산

    private double burstRate;
    // 터졌다 비율 (%)
    // Java에서 (burstCount * 100.0 / totalCount)로 계산
    // 예: 리뷰 10개 중 터졌다 7개 → 70.0

    // Getter / Setter
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getBurstCount() { return burstCount; }
    public void setBurstCount(int burstCount) { this.burstCount = burstCount; }

    public int getNotBurstCount() { return notBurstCount; }
    public void setNotBurstCount(int notBurstCount) { this.notBurstCount = notBurstCount; }

    public double getBurstRate() { return burstRate; }
    public void setBurstRate(double burstRate) { this.burstRate = burstRate; }
}
