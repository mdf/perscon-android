/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/martin/workspace/perscon-android/src/horizon/perscon/IPersconServiceCallback.aidl
 */
package horizon.perscon;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import horizon.perscon.model.Event;
public interface IPersconServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements horizon.perscon.IPersconServiceCallback
{
private static final java.lang.String DESCRIPTOR = "horizon.perscon.IPersconServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IPersconServiceCallback interface,
 * generating a proxy if needed.
 */
public static horizon.perscon.IPersconServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof horizon.perscon.IPersconServiceCallback))) {
return ((horizon.perscon.IPersconServiceCallback)iin);
}
return new horizon.perscon.IPersconServiceCallback.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_eventAdded:
{
data.enforceInterface(DESCRIPTOR);
horizon.perscon.model.Event _arg0;
if ((0!=data.readInt())) {
_arg0 = horizon.perscon.model.Event.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.eventAdded(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements horizon.perscon.IPersconServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void eventAdded(horizon.perscon.model.Event event) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((event!=null)) {
_data.writeInt(1);
event.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_eventAdded, _data, null, IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_eventAdded = (IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void eventAdded(horizon.perscon.model.Event event) throws android.os.RemoteException;
}
