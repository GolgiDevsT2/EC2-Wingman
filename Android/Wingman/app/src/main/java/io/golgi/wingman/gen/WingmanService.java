/* IS_AUTOGENERATED_SO_ALLOW_AUTODELETE=YES */
/* The previous line is to allow auto deletion */

package io.golgi.wingman.gen;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import com.openmindnetworks.golgi.JavaType;
import com.openmindnetworks.golgi.GolgiPayload;
import com.openmindnetworks.golgi.B64;
import com.openmindnetworks.golgi.api.*;

public interface WingmanService{
    public static class list{
        public interface ResultSender extends ResultSenderReceiver{
            public String getRequestSenderId();
        }

        public interface ResultSenderReceiver{
            public void success(InstanceList result);
            public void failure(GolgiException golgiException);
        }

        public interface ResultReceiver extends ResultSenderReceiver{
            public void failure(GolgiException ex);
        }

        public static class InboundResponseHandler implements GolgiAPIIBResponseHandler{
            private ResultReceiver resultReceiver;
            @Override
            public void error(int errType, String errText){
                GolgiException ex = new GolgiException();
                ex.setErrType(errType);
                ex.setErrText(errText);
                resultReceiver.failure(ex);
            }
            @Override
            public void remoteResponse(String payload){
                Wingman_list_rspArg rsp;
                rsp = new Wingman_list_rspArg(payload);
                if(rsp == null || rsp.isCorrupt()){
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(1)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
                if(rsp.getInternalSuccess_() != 0 && rsp.resultIsSet()){
                    resultReceiver.success(rsp.getResult());
                }
                else if(rsp.golgiExceptionIsSet()){
                    resultReceiver.failure(rsp.getGolgiException());
                }
                else{
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(2)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
            }
            public InboundResponseHandler(ResultReceiver resultReceiver){
                this.resultReceiver = resultReceiver;
            }
        }

        public static void sendTo(ResultReceiver receiver, GolgiTransportOptions cto, String destination){
            GolgiAPI.getInstance().sendRequest(new InboundResponseHandler(receiver), cto, destination, "list.Wingman");
        }

        public static void sendTo(ResultReceiver receiver, String destination){
            sendTo(receiver, null, destination);
        }

        public interface RequestReceiver{
            public void receiveFrom(ResultSender resultSender);
        }

        public static class InboundRequestReceiver implements GolgiAPIRequestReceiver,ResultSender{
            private RequestReceiver requestReceiver;
            private GolgiAPIOBResponseHandler obResponseSender;

            @Override
            public void incomingRequest(GolgiAPIOBResponseHandler handler, String payload){
                this.obResponseSender = handler;

                requestReceiver.receiveFrom(this);
            }

            @Override
            public void success(InstanceList result){
                Wingman_list_rspArg rspArg = new Wingman_list_rspArg(false);
                rspArg.setInternalSuccess_(1);
                rspArg.setResult(result);
                obResponseSender.send(rspArg.serialise().toString());
            }
            @Override
            public String getRequestSenderId(){
                return obResponseSender.getRequestSenderId();
            }
            @Override
            public void failure(GolgiException golgiException){
                Wingman_list_rspArg rspArg = new Wingman_list_rspArg(false);
                rspArg.setInternalSuccess_(0);
                rspArg.setGolgiException(golgiException);
                obResponseSender.send(rspArg.serialise().toString());
            }

            public InboundRequestReceiver(RequestReceiver requestReceiver){
                this.requestReceiver = requestReceiver;
            }
        }

        public static void registerReceiver(RequestReceiver requestReceiver){
           GolgiAPI.getInstance().registerReqReceiver(new InboundRequestReceiver(requestReceiver), "list.Wingman");
        }

    }
    public static class raiseOrangeCpuAlarm{
        public interface ResultSender extends ResultSenderReceiver{
            public String getRequestSenderId();
        }

        public interface ResultSenderReceiver{
            public void success();
        }

        public interface ResultReceiver extends ResultSenderReceiver{
            public void failure(GolgiException ex);
        }

        public static class InboundResponseHandler implements GolgiAPIIBResponseHandler{
            private ResultReceiver resultReceiver;
            @Override
            public void error(int errType, String errText){
                GolgiException ex = new GolgiException();
                ex.setErrType(errType);
                ex.setErrText(errText);
                resultReceiver.failure(ex);
            }
            @Override
            public void remoteResponse(String payload){
                Wingman_raiseOrangeCpuAlarm_rspArg rsp;
                rsp = new Wingman_raiseOrangeCpuAlarm_rspArg(payload);
                if(rsp == null || rsp.isCorrupt()){
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(1)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
                if(rsp.getInternalSuccess_() != 0){
                    resultReceiver.success();
                }
                else{
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(2)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
            }
            public InboundResponseHandler(ResultReceiver resultReceiver){
                this.resultReceiver = resultReceiver;
            }
        }

        public static void sendTo(ResultReceiver receiver, GolgiTransportOptions cto, String destination, String instName, int cpu){
            Wingman_raiseOrangeCpuAlarm_reqArg _arg = new Wingman_raiseOrangeCpuAlarm_reqArg();

            _arg.setInstName(instName);
            _arg.setCpu(cpu);

            StringBuffer sb = _arg.serialise();
            GolgiAPI.getInstance().sendRequest(new InboundResponseHandler(receiver), cto, destination, "raiseOrangeCpuAlarm.Wingman", sb.toString());
        }

        public static void sendTo(ResultReceiver receiver, String destination, String instName, int cpu){
            sendTo(receiver, null, destination, instName, cpu);
        }

        public interface RequestReceiver{
            public void receiveFrom(ResultSender resultSender, String instName, int cpu);
        }

        public static class InboundRequestReceiver implements GolgiAPIRequestReceiver,ResultSender{
            private RequestReceiver requestReceiver;
            private GolgiAPIOBResponseHandler obResponseSender;

            @Override
            public void incomingRequest(GolgiAPIOBResponseHandler handler, String payload){
                this.obResponseSender = handler;

                Wingman_raiseOrangeCpuAlarm_reqArg arg = new Wingman_raiseOrangeCpuAlarm_reqArg(payload);
                if(arg.isCorrupt()){
                    handler.remotePayloadError();
                    return;
                }
                requestReceiver.receiveFrom(this, arg.getInstName(), arg.getCpu());
            }

            @Override
            public void success(){
                Wingman_raiseOrangeCpuAlarm_rspArg rspArg = new Wingman_raiseOrangeCpuAlarm_rspArg(false);
                rspArg.setInternalSuccess_(1);
                obResponseSender.send(rspArg.serialise().toString());
            }
            @Override
            public String getRequestSenderId(){
                return obResponseSender.getRequestSenderId();
            }

            public InboundRequestReceiver(RequestReceiver requestReceiver){
                this.requestReceiver = requestReceiver;
            }
        }

        public static void registerReceiver(RequestReceiver requestReceiver){
           GolgiAPI.getInstance().registerReqReceiver(new InboundRequestReceiver(requestReceiver), "raiseOrangeCpuAlarm.Wingman");
        }

    }
    public static class raiseRedCpuAlarm{
        public interface ResultSender extends ResultSenderReceiver{
            public String getRequestSenderId();
        }

        public interface ResultSenderReceiver{
            public void success();
        }

        public interface ResultReceiver extends ResultSenderReceiver{
            public void failure(GolgiException ex);
        }

        public static class InboundResponseHandler implements GolgiAPIIBResponseHandler{
            private ResultReceiver resultReceiver;
            @Override
            public void error(int errType, String errText){
                GolgiException ex = new GolgiException();
                ex.setErrType(errType);
                ex.setErrText(errText);
                resultReceiver.failure(ex);
            }
            @Override
            public void remoteResponse(String payload){
                Wingman_raiseRedCpuAlarm_rspArg rsp;
                rsp = new Wingman_raiseRedCpuAlarm_rspArg(payload);
                if(rsp == null || rsp.isCorrupt()){
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(1)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
                if(rsp.getInternalSuccess_() != 0){
                    resultReceiver.success();
                }
                else{
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(2)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
            }
            public InboundResponseHandler(ResultReceiver resultReceiver){
                this.resultReceiver = resultReceiver;
            }
        }

        public static void sendTo(ResultReceiver receiver, GolgiTransportOptions cto, String destination, String instName, int cpu){
            Wingman_raiseRedCpuAlarm_reqArg _arg = new Wingman_raiseRedCpuAlarm_reqArg();

            _arg.setInstName(instName);
            _arg.setCpu(cpu);

            StringBuffer sb = _arg.serialise();
            GolgiAPI.getInstance().sendRequest(new InboundResponseHandler(receiver), cto, destination, "raiseRedCpuAlarm.Wingman", sb.toString());
        }

        public static void sendTo(ResultReceiver receiver, String destination, String instName, int cpu){
            sendTo(receiver, null, destination, instName, cpu);
        }

        public interface RequestReceiver{
            public void receiveFrom(ResultSender resultSender, String instName, int cpu);
        }

        public static class InboundRequestReceiver implements GolgiAPIRequestReceiver,ResultSender{
            private RequestReceiver requestReceiver;
            private GolgiAPIOBResponseHandler obResponseSender;

            @Override
            public void incomingRequest(GolgiAPIOBResponseHandler handler, String payload){
                this.obResponseSender = handler;

                Wingman_raiseRedCpuAlarm_reqArg arg = new Wingman_raiseRedCpuAlarm_reqArg(payload);
                if(arg.isCorrupt()){
                    handler.remotePayloadError();
                    return;
                }
                requestReceiver.receiveFrom(this, arg.getInstName(), arg.getCpu());
            }

            @Override
            public void success(){
                Wingman_raiseRedCpuAlarm_rspArg rspArg = new Wingman_raiseRedCpuAlarm_rspArg(false);
                rspArg.setInternalSuccess_(1);
                obResponseSender.send(rspArg.serialise().toString());
            }
            @Override
            public String getRequestSenderId(){
                return obResponseSender.getRequestSenderId();
            }

            public InboundRequestReceiver(RequestReceiver requestReceiver){
                this.requestReceiver = requestReceiver;
            }
        }

        public static void registerReceiver(RequestReceiver requestReceiver){
           GolgiAPI.getInstance().registerReqReceiver(new InboundRequestReceiver(requestReceiver), "raiseRedCpuAlarm.Wingman");
        }

    }
    public static class raiseStatusCheckAlarm{
        public interface ResultSender extends ResultSenderReceiver{
            public String getRequestSenderId();
        }

        public interface ResultSenderReceiver{
            public void success();
        }

        public interface ResultReceiver extends ResultSenderReceiver{
            public void failure(GolgiException ex);
        }

        public static class InboundResponseHandler implements GolgiAPIIBResponseHandler{
            private ResultReceiver resultReceiver;
            @Override
            public void error(int errType, String errText){
                GolgiException ex = new GolgiException();
                ex.setErrType(errType);
                ex.setErrText(errText);
                resultReceiver.failure(ex);
            }
            @Override
            public void remoteResponse(String payload){
                Wingman_raiseStatusCheckAlarm_rspArg rsp;
                rsp = new Wingman_raiseStatusCheckAlarm_rspArg(payload);
                if(rsp == null || rsp.isCorrupt()){
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(1)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
                if(rsp.getInternalSuccess_() != 0){
                    resultReceiver.success();
                }
                else{
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(2)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
            }
            public InboundResponseHandler(ResultReceiver resultReceiver){
                this.resultReceiver = resultReceiver;
            }
        }

        public static void sendTo(ResultReceiver receiver, GolgiTransportOptions cto, String destination, String instName){
            Wingman_raiseStatusCheckAlarm_reqArg _arg = new Wingman_raiseStatusCheckAlarm_reqArg();

            _arg.setInstName(instName);

            StringBuffer sb = _arg.serialise();
            GolgiAPI.getInstance().sendRequest(new InboundResponseHandler(receiver), cto, destination, "raiseStatusCheckAlarm.Wingman", sb.toString());
        }

        public static void sendTo(ResultReceiver receiver, String destination, String instName){
            sendTo(receiver, null, destination, instName);
        }

        public interface RequestReceiver{
            public void receiveFrom(ResultSender resultSender, String instName);
        }

        public static class InboundRequestReceiver implements GolgiAPIRequestReceiver,ResultSender{
            private RequestReceiver requestReceiver;
            private GolgiAPIOBResponseHandler obResponseSender;

            @Override
            public void incomingRequest(GolgiAPIOBResponseHandler handler, String payload){
                this.obResponseSender = handler;

                Wingman_raiseStatusCheckAlarm_reqArg arg = new Wingman_raiseStatusCheckAlarm_reqArg(payload);
                if(arg.isCorrupt()){
                    handler.remotePayloadError();
                    return;
                }
                requestReceiver.receiveFrom(this, arg.getInstName());
            }

            @Override
            public void success(){
                Wingman_raiseStatusCheckAlarm_rspArg rspArg = new Wingman_raiseStatusCheckAlarm_rspArg(false);
                rspArg.setInternalSuccess_(1);
                obResponseSender.send(rspArg.serialise().toString());
            }
            @Override
            public String getRequestSenderId(){
                return obResponseSender.getRequestSenderId();
            }

            public InboundRequestReceiver(RequestReceiver requestReceiver){
                this.requestReceiver = requestReceiver;
            }
        }

        public static void registerReceiver(RequestReceiver requestReceiver){
           GolgiAPI.getInstance().registerReqReceiver(new InboundRequestReceiver(requestReceiver), "raiseStatusCheckAlarm.Wingman");
        }

    }
    public static class raiseStateChangeAlarm{
        public interface ResultSender extends ResultSenderReceiver{
            public String getRequestSenderId();
        }

        public interface ResultSenderReceiver{
            public void success();
        }

        public interface ResultReceiver extends ResultSenderReceiver{
            public void failure(GolgiException ex);
        }

        public static class InboundResponseHandler implements GolgiAPIIBResponseHandler{
            private ResultReceiver resultReceiver;
            @Override
            public void error(int errType, String errText){
                GolgiException ex = new GolgiException();
                ex.setErrType(errType);
                ex.setErrText(errText);
                resultReceiver.failure(ex);
            }
            @Override
            public void remoteResponse(String payload){
                Wingman_raiseStateChangeAlarm_rspArg rsp;
                rsp = new Wingman_raiseStateChangeAlarm_rspArg(payload);
                if(rsp == null || rsp.isCorrupt()){
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(1)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
                if(rsp.getInternalSuccess_() != 0){
                    resultReceiver.success();
                }
                else{
                    GolgiException ex = new GolgiException();
                    ex.setErrText("corrupt response(2)");
                    ex.setErrType(golgi_message.ERRTYPE_PAYLOAD_MISMATCH);
                    resultReceiver.failure(ex);
                }
            }
            public InboundResponseHandler(ResultReceiver resultReceiver){
                this.resultReceiver = resultReceiver;
            }
        }

        public static void sendTo(ResultReceiver receiver, GolgiTransportOptions cto, String destination, String instName, int oldState, int newState){
            Wingman_raiseStateChangeAlarm_reqArg _arg = new Wingman_raiseStateChangeAlarm_reqArg();

            _arg.setInstName(instName);
            _arg.setOldState(oldState);
            _arg.setNewState(newState);

            StringBuffer sb = _arg.serialise();
            GolgiAPI.getInstance().sendRequest(new InboundResponseHandler(receiver), cto, destination, "raiseStateChangeAlarm.Wingman", sb.toString());
        }

        public static void sendTo(ResultReceiver receiver, String destination, String instName, int oldState, int newState){
            sendTo(receiver, null, destination, instName, oldState, newState);
        }

        public interface RequestReceiver{
            public void receiveFrom(ResultSender resultSender, String instName, int oldState, int newState);
        }

        public static class InboundRequestReceiver implements GolgiAPIRequestReceiver,ResultSender{
            private RequestReceiver requestReceiver;
            private GolgiAPIOBResponseHandler obResponseSender;

            @Override
            public void incomingRequest(GolgiAPIOBResponseHandler handler, String payload){
                this.obResponseSender = handler;

                Wingman_raiseStateChangeAlarm_reqArg arg = new Wingman_raiseStateChangeAlarm_reqArg(payload);
                if(arg.isCorrupt()){
                    handler.remotePayloadError();
                    return;
                }
                requestReceiver.receiveFrom(this, arg.getInstName(), arg.getOldState(), arg.getNewState());
            }

            @Override
            public void success(){
                Wingman_raiseStateChangeAlarm_rspArg rspArg = new Wingman_raiseStateChangeAlarm_rspArg(false);
                rspArg.setInternalSuccess_(1);
                obResponseSender.send(rspArg.serialise().toString());
            }
            @Override
            public String getRequestSenderId(){
                return obResponseSender.getRequestSenderId();
            }

            public InboundRequestReceiver(RequestReceiver requestReceiver){
                this.requestReceiver = requestReceiver;
            }
        }

        public static void registerReceiver(RequestReceiver requestReceiver){
           GolgiAPI.getInstance().registerReqReceiver(new InboundRequestReceiver(requestReceiver), "raiseStateChangeAlarm.Wingman");
        }

    }
}