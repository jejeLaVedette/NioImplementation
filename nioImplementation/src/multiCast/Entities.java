package multiCast;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by augustin on 01/11/14.
 */
public abstract class Entities{
    protected int clock;
    protected int identity;
    protected HashMap<String,InetAddress> addressHashMap; //correspondance pseudo , ip
    protected HashMap<String,Integer> integerHashMap; // correspondance pseudo , port de connexion pas forcément utile

}
