package gomoku.dao;

public class user {
	private int id;
	private String name;
	private int score;
	private int winCnt;
	private int loseCnt;
	
	public user(int id, String name, int score, int winCnt, int loseCnt) {
		this.id = id;
		this.name = name;
		this.score = score;
		this.winCnt = winCnt;
		this.loseCnt = loseCnt;
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getWinCnt() {
		return winCnt;
	}
	
	public int getLoseCnt() {
		return loseCnt;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setWinCnt(int winCnt) {
		this.winCnt = winCnt;
	}
	
	public void setLoseCnt(int loseCnt) {
		this.loseCnt = loseCnt;
	}
}
