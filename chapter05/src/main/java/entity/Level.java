package entity;

public enum Level {
    GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER) ;

    private final int value;
    private final Level next;

    Level(int value, Level next){ // DB에 값을 넣어줄 생성자를 만들어준다.
        this.value = value;
        this.next = next;
    }

    public int intValue(){ //값 가지고 오기
        return value;
    }
    public Level nextLevel(){
        return this.next;
    }

    public static Level valueOf(int value){ //값으로부터 Level 타입 오브젝트를 반환하는 static 메소드
        switch (value){
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default: throw new AssertionError("Unknown value : " + value);
        }
    }
}
