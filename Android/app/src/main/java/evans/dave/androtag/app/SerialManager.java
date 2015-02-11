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

    abstract void setShield(int a, int b, int c, int d);
    abstract void tryFire(int a, int b, int c, int d);
    abstract void fireSuccess(int a, int b, int c, int d);
    abstract void tryReload(int a, int b, int c, int d);
    abstract void reloadSuccess(int a, int b, int c, int d);
    abstract void setActive(int a, int b, int c, int d);
    abstract void hitBy(int a, int b, int c, int d);
    abstract void killedBy(int a, int b, int c, int d);

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
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    setShield(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));;
                    break;

                case SET_ACTIVE:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    setActive(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));
                    break;

                case TRY_RELOAD:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    tryReload(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));
                    break;

                case TRY_FIRE:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    tryFire(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));;
                    break;


                case FIRE_SUCCESS:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    fireSuccess(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));;
                    break;

                case RELOAD_SUCCESS:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    reloadSuccess(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));;
                    break;

                case HIT_BY:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    hitBy(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));
                    break;

                case KILLED_BY:
                    packet = parsePacket(4);
                    if (packet==null)
                        return false; //Queue not empty
                    killedBy(toInt(packet[0]), toInt(packet[1]), toInt(packet[2]), toInt(packet[3]));
                    break;

                case ACK:
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
