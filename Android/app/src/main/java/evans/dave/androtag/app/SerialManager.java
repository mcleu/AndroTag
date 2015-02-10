package evans.dave.androtag.app;

/**
 * Created by Dave on 09/02/2015.
 */


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;

import evans.dave.androtag.common.SerialMessage;

import static evans.dave.androtag.common.SerialMessage.*;

public abstract class SerialManager {

    private static LinkedList<Byte> inBuffer = new LinkedList<>();
    private static Context ctx;

    abstract void setShield(int val);
    abstract void tryFire();
    abstract void fireSuccess();
    abstract void setActive(int val);
    abstract void hitBy(int tid, int pid);
    abstract void killedBy(int tid, int pid);

    public SerialManager(Context ctx){
        this.ctx = ctx;
    }

    public void addDataCallback(byte[] data){
        // Add the data
        for (byte b: data){
            if (FLUSH_SERIAL.equals(b))
                inBuffer.clear();
            else
                inBuffer.addLast(b);
        }

        parseMessages();

    }

    private boolean parseMessages(){
        SerialMessage startCode;
        byte[] packet;
        while (!inBuffer.isEmpty()){

            // Check the first byte to see if it's a message
            startCode = SerialMessage.getFromByte(inBuffer.peek());

            if (startCode == null){
                // Not a known message
                // Todo: Throw an exception and flush buffer
                return false;
            }

            switch (startCode){

                // TODO: Many missing here

                case SET_SHIELD:
                    packet = parsePacket(3);
                    if (packet==null)
                        return false; //Queue not empty
                    Toast.makeText(ctx, "SHIELD: "+toInt(packet[1]), Toast.LENGTH_SHORT).show();
                    setShield(toInt(packet[1]));
                    break;

                case SET_ACTIVE:
                    packet = parsePacket(3);
                    if (packet==null)
                        return false; //Queue not empty
                    Toast.makeText(ctx, "SWAP", Toast.LENGTH_SHORT).show();
                    setActive(toInt(packet[1]));
                    break;

                case TRY_RELOAD:
                    packet = parsePacket(2);
                    if (packet==null)
                        return false; //Queue not empty
                    // TODO: Reloading abstract functions
                    break;

                case TRY_FIRE:
                    packet = parsePacket(2);
                    if (packet==null)
                        return false; //Queue not empty
                    tryFire();
                    break;


                case FIRE_SUCCESS:
                    packet = parsePacket(2);
                    if (packet==null)
                        return false; //Queue not empty
                    Toast.makeText(ctx, "FIRE", Toast.LENGTH_SHORT).show();
                    fireSuccess();
                    break;

                case HIT_BY:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    Toast.makeText(ctx, "HIT: "+toInt(packet[2])+" "+toInt(packet[3]), Toast.LENGTH_SHORT).show();
                    hitBy(toInt(packet[2]),toInt(packet[3]));
                    break;

                case KILLED_BY:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    Toast.makeText(ctx, "KILLED: "+toInt(packet[2])+" "+toInt(packet[3]), Toast.LENGTH_SHORT).show();
                    killedBy(toInt(packet[2]), toInt(packet[3]));
                    break;

                case ACK:
                    packet = parsePacket(1);
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    break;

                default:
                    return false;
            }


        }
        return true; //Queue is empty
    }

    private byte[] parsePacket(int num){
        if (inBuffer.size()>=num) {
            byte[] data = new byte[num];

            for (int i = 0; i < num; i++)
                data[i] = inBuffer.removeFirst();
            return data;
        } else {
            return null; // Not enough data, return null
        }
    }

    private int toInt(byte b){
        return (int) b & 0xFF;
    }


}
