package multiCast.client.kernel;

/**
 * Created by augustin on 02/11/14.
 */
public class Message {
    private int clock;
    private int identity;
    private boolean[] ack;
    private String data;

    public Message(int clock, int identity, String data, int numberACK) {
        this.clock = clock;
        this.identity = identity;
        this.data = data;

        if(numberACK <= 0){
            this.ack = null;
        } else {
            this.ack = new boolean[numberACK];
            initAck();
        }
    }

    public int getClock(){
        return this.clock;
    }

    public int getIdentity(){
        return this.identity;
    }

    public void receiveACK(int identityACK){
        System.out.println("identityACK "+identityACK);
        System.out.println("size of tab ack:"+this.ack.length);
        this.ack[identityACK-1] = true;
    }

    public boolean receveidAllACK(){
        boolean result = true;
        int i = 0;
        int size = 0;
        if(ack != null) {
            size = ack.length;
        }

        while((i < size) && (result)){
            result = result && ack[i];
            i++;
        }

        return result;
    }

    @Override
    public String toString(){
        return this.data;
    }

    private void initAck(){
        int i = 0;
        int size = ack.length;

        while(i < size){
            ack[i] = false;
            i++;
        }
    }
}
