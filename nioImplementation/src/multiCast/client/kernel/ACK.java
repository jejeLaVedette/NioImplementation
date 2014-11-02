package multiCast.client.kernel;

/**
 * Created by augustin on 02/11/14.
 */
public class ACK {
    private int identityMessage;
    private int identityACK;
    private int clock;

    public ACK(int identityMessage, int identityACK, int clock) {
        this.identityMessage = identityMessage;
        this.identityACK = identityACK;
        this.clock = clock;
    }

    public int getIdentityMessage() {
        return identityMessage;
    }

    public int getIdentityACK(){
        return identityACK;
    }

    public int getClock() {
        return clock;
    }
}
