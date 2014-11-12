/* IS_AUTOGENERATED_SO_ALLOW_AUTODELETE=YES */
/* The previous line is to allow auto deletion */

package io.golgi.wingman.gen;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import com.openmindnetworks.golgi.JavaType;
import com.openmindnetworks.golgi.GolgiPayload;
import com.openmindnetworks.golgi.B64;
import com.openmindnetworks.golgi.api.GolgiException;
import com.openmindnetworks.golgi.api.GolgiAPI;
import android.os.Parcel;
import android.os.Parcelable;

public class Wingman_list_rspArg implements Parcelable
{

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(this.serialise().toString());
    }

    public static final Parcelable.Creator<Wingman_list_rspArg> CREATOR = new Parcelable.Creator<Wingman_list_rspArg>() {
        public Wingman_list_rspArg createFromParcel(Parcel in) {
            return new Wingman_list_rspArg(in);
        }
        public Wingman_list_rspArg[] newArray(int size) {
            return new Wingman_list_rspArg[size];
        }
    };
    private Wingman_list_rspArg(Parcel in) {
        this(in.readString());
    }

    private boolean corrupt = false;

    public boolean isCorrupt() {
        return corrupt;
    }

    public void setCorrupt() {
        corrupt = true;
    }

    private boolean internalSuccess_IsSet = false;
    private int internalSuccess_;
    private boolean resultIsSet = false;
    private InstanceList result;
    private boolean golgiExceptionIsSet = false;
    private GolgiException golgiException;

    public int getInternalSuccess_(){
        return internalSuccess_;
    }
    public boolean internalSuccess_IsSet(){
        return internalSuccess_IsSet;
    }
    public void setInternalSuccess_(int internalSuccess_){
        this.internalSuccess_ = internalSuccess_;
        this.internalSuccess_IsSet = true;
    }

    public InstanceList getResult(){
        return result;
    }
    public boolean resultIsSet(){
        return resultIsSet;
    }
    public void setResult(InstanceList result){
        this.result = result;
        this.resultIsSet = true;
    }

    public GolgiException getGolgiException(){
        return golgiException;
    }
    public boolean golgiExceptionIsSet(){
        return golgiExceptionIsSet;
    }
    public void setGolgiException(GolgiException golgiException){
        this.golgiException = golgiException;
        this.golgiExceptionIsSet = true;
    }

    public StringBuffer serialise(){
        return serialise(null);
    }

    public StringBuffer serialise(StringBuffer sb){
        return serialise("", sb);
    }

    public StringBuffer serialise(String prefix, StringBuffer sb){
        if(sb == null){
            sb = new StringBuffer();
        }

        if(this.internalSuccess_IsSet){
            sb.append(prefix + "1: " + this.internalSuccess_+"\n");
        }
        if(this.resultIsSet){
            result.serialise(prefix + "" + 2 + "." , sb);
        }
        if(this.golgiExceptionIsSet){
            golgiException.serialise(prefix + "" + 3 + "." , sb);
        }

        return sb;
    }

    public void deserialise(String str){
        deserialise(JavaType.createPayload(str));
    }

    private void deserialise(GolgiPayload payload){
        if(!isCorrupt() && payload.containsFieldKey("1:")){
            String str = payload.getField("1:");
            try{
                setInternalSuccess_(Integer.valueOf(str));
            }
            catch(NumberFormatException nfe){
                setCorrupt();
            }
        }
        if(!isCorrupt() && payload.containsNestedKey("2")){
            InstanceList inst = new InstanceList(payload.getNested("2"));
            setResult(inst);
        }
        if(!isCorrupt() && payload.containsNestedKey("3")){
            GolgiException inst = new GolgiException(payload.getNested("3"));
            setGolgiException(inst);
        }
    }

    public Wingman_list_rspArg(){
        this(true);
    }

    public Wingman_list_rspArg(boolean isSetDefault){
        super();
        internalSuccess_IsSet = isSetDefault;
        internalSuccess_ = 0;
        resultIsSet = isSetDefault;
        result = new InstanceList(isSetDefault);
        golgiExceptionIsSet = isSetDefault;
        golgiException = new GolgiException(isSetDefault);
    }

    public Wingman_list_rspArg(GolgiPayload payload){
        this(false);
        deserialise(payload);
    }

    public Wingman_list_rspArg(String payload){
        this(JavaType.createPayload(payload));
    }

}