package entity;

public class User {


	/*
	private static final int BASIC = 1;
	private static final int SILVER = 2;
	private static final int GOLD = 3;
	*/
	String id;
	String name;
	String password;

	//int level; //LEVEL에 어떤값이든 넣을수 있기 때문에 안전하지 않다(Enum 타입 사용 권장)
	Level level;
	int login; //로그인 횟수
	int recommend; //추천수

	public User(String id, String name, String password, Level level, int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}

	public User(){

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

    public void upgradeLevel() {
		Level nextLevel = this.level.nextLevel();
		if(nextLevel == null){
			throw new IllegalArgumentException(this.level + "은 업그레이드가 불가능 합니다.");
		}else {
			this.level = nextLevel;
		}
    }
}
