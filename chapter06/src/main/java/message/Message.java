package message;

public class Message {
    String text;
    
    private Message(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    public static Message newMessage(String text){
        //생성자가 아닌 스태틱팩토리 메소드를 통해 인스턴스를 생성하므로 해당 클래스는 직접 스프링 빈으로 등록해서 사용할 수 없다.
        //해당 규약을 지킨채로 오브젝트를 생성해야지 안전하다(리플랙션은 private를 무시하고 빈을 만들수 있기떄문에 스프링에서 빈을 만들수는 있으나 위험하다)
        //
        return  new Message(text); //생성자 대신 사용할 수 있는 스태틱팩토리 메소드 제공
    }
    
        
}
