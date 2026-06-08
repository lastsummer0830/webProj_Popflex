package common.paging;

import java.util.ArrayList;

public class PagingDTO {

    // 현재 페이지 번호
    private int currentPage;

    // 한 페이지에 보여줄 데이터 개수
    private int pageSize;

    // 한 번에 보여줄 페이지 번호 개수
    private int pageBlockSize;

    // 전체 데이터 개수
    private int totalCount;

    // 전체 페이지 수
    private int totalPage;

    // 현재 페이지 묶음의 시작 페이지 번호
    private int startPage;

    // 현재 페이지 묶음의 끝 페이지 번호
    private int endPage;

    // 이전 버튼 표시 여부
    private boolean hasPrev;

    // 다음 버튼 표시 여부
    private boolean hasNext;

    // 이전 버튼 클릭 시 이동할 페이지 번호
    private int prevPage;

    // 다음 버튼 클릭 시 이동할 페이지 번호
    private int nextPage;

    // JSP에서 반복 출력할 페이지 번호 목록
    private ArrayList<Integer> pageNumbers;

    // 페이징 계산에 필요한 값을 받아 전체 페이징 정보를 계산하는 생성자
    public PagingDTO(int currentPage, int pageSize, int pageBlockSize, int totalCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.pageBlockSize = pageBlockSize;
        this.totalCount = totalCount;
        this.pageNumbers = new ArrayList<Integer>();

        calculate();
    }

    // 전체 페이지 수, 시작 페이지, 끝 페이지, 이전/다음 버튼 여부를 계산하는 메서드
    private void calculate() {
        if (currentPage < 1) {
            currentPage = 1;
        }

        if (pageSize < 1) {
            pageSize = 12;
        }

        if (pageBlockSize < 1) {
            pageBlockSize = 5;
        }

        if (totalCount > 0) {
            totalPage = (int) Math.ceil((double) totalCount / pageSize);
        } else {
            totalPage = 0;
        }

        if (totalPage > 0 && currentPage > totalPage) {
            currentPage = totalPage;
        }

        if (totalPage > 0) {
            startPage = ((currentPage - 1) / pageBlockSize) * pageBlockSize + 1;
            endPage = startPage + pageBlockSize - 1;

            if (endPage > totalPage) {
                endPage = totalPage;
            }

            for (int i = startPage; i <= endPage; i++) {
                pageNumbers.add(i);
            }
        } else {
            startPage = 1;
            endPage = 0;
        }

        hasPrev = currentPage > 1;
        hasNext = totalPage > 0 && currentPage < totalPage;

        prevPage = currentPage - 1;
        nextPage = currentPage + 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageBlockSize() {
        return pageBlockSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public int getPrevPage() {
        return prevPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public ArrayList<Integer> getPageNumbers() {
        return pageNumbers;
    }
}