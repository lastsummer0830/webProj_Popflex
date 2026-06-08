package diary.dto;

/*
  BadgeDTO
  뱃지 하나의 정보를 담는 DTO
  - earned: 달성 여부
  - earnedDate: 달성일 (yyyy.MM.dd, 미달성이면 null)
  - progress: 현재 달성치 (예: 8편)
  - target: 목표치 (예: 10편)
 */
public class BadgeDTO {

    private String code;        // 뱃지 코드 (FIRST_MOVIE 등)
    private String icon;        // 아이콘 이미지 파일명
    private String name;        // 뱃지 이름
    private String desc;        // 달성 조건 설명
    private boolean earned;     // 달성 여부
    private String earnedDate;  // 달성일 (yyyy.MM.dd), 미달성이면 null
    private int progress;       // 현재 달성치
    private int target;         // 목표치

    public BadgeDTO(String code, String icon, String name, String desc, boolean earned,
                    String earnedDate, int progress, int target) {
        this.code = code;
        this.icon = icon;
        this.name = name;
        this.desc = desc;
        this.earned = earned;
        this.earnedDate = earnedDate;
        this.progress = progress;
        this.target = target;
    }

    public String getCode() { return code; }
    public String getIcon() { return icon; }
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public boolean isEarned() { return earned; }
    public String getEarnedDate() { return earnedDate; }
    public int getProgress() { return progress; }
    public int getTarget() { return target; }
}
