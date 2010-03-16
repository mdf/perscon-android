/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/martin/workspace/perscon-android/src/horizon/perscon/IPersconService.aidl
 */
package horizon.perscon;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import horizon.perscon.model.Event;
import horizon.perscon.model.Person;
import horizon.perscon.model.Place;
import horizon.perscon.model.Thing;
import horizon.perscon.model.EventQuery;
public interface IPersconService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements horizon.perscon.IPersconService
{
private static final java.lang.String DESCRIPTOR = "horizon.perscon.IPersconService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IPersconService interface,
 * generating a proxy if needed.
 */
public static horizon.perscon.IPersconService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof horizon.perscon.IPersconService))) {
return ((horizon.perscon.IPersconService)iin);
}
return new horizon.perscon.IPersconService.Stub.Proxy(obj);
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
case TRANSACTION_registerApplication:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.registerApplication(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_addEventListener:
{
data.enforceInterface(DESCRIPTOR);
horizon.perscon.IPersconServiceCallback _arg0;
_arg0 = horizon.perscon.IPersconServiceCallback.Stub.asInterface(data.readStrongBinder());
horizon.perscon.model.EventQuery _arg1;
if ((0!=data.readInt())) {
_arg1 = horizon.perscon.model.EventQuery.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.addEventListener(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_removeEventListener:
{
data.enforceInterface(DESCRIPTOR);
horizon.perscon.IPersconServiceCallback _arg0;
_arg0 = horizon.perscon.IPersconServiceCallback.Stub.asInterface(data.readStrongBinder());
this.removeEventListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_match:
{
data.enforceInterface(DESCRIPTOR);
horizon.perscon.model.EventQuery _arg0;
if ((0!=data.readInt())) {
_arg0 = horizon.perscon.model.EventQuery.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
horizon.perscon.model.Event[] _result = this.match(_arg0);
reply.writeNoException();
reply.writeTypedArray(_result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
return true;
}
case TRANSACTION_add:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
horizon.perscon.model.Person _arg1;
if ((0!=data.readInt())) {
_arg1 = horizon.perscon.model.Person.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
horizon.perscon.model.Place _arg2;
if ((0!=data.readInt())) {
_arg2 = horizon.perscon.model.Place.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
horizon.perscon.model.Thing _arg3;
if ((0!=data.readInt())) {
_arg3 = horizon.perscon.model.Thing.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
horizon.perscon.model.Event _result = this.add(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements horizon.perscon.IPersconService
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
public void registerApplication(java.lang.String applicationId, java.lang.String applicationName, java.lang.String applicationVersion) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(applicationId);
_data.writeString(applicationName);
_data.writeString(applicationVersion);
mRemote.transact(Stub.TRANSACTION_registerApplication, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void addEventListener(horizon.perscon.IPersconServiceCallback callback, horizon.perscon.model.EventQuery template) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
if ((template!=null)) {
_data.writeInt(1);
template.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_addEventListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void removeEventListener(horizon.perscon.IPersconServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeEventListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public horizon.perscon.model.Event[] match(horizon.perscon.model.EventQuery template) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
horizon.perscon.model.Event[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((template!=null)) {
_data.writeInt(1);
template.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_match, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArray(horizon.perscon.model.Event.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public horizon.perscon.model.Event add(java.lang.String applicationId, horizon.perscon.model.Person person, horizon.perscon.model.Place place, horizon.perscon.model.Thing thing) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
horizon.perscon.model.Event _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(applicationId);
if ((person!=null)) {
_data.writeInt(1);
person.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((place!=null)) {
_data.writeInt(1);
place.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((thing!=null)) {
_data.writeInt(1);
thing.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_add, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = horizon.perscon.model.Event.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerApplication = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_addEventListener = (IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_removeEventListener = (IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_match = (IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_add = (IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void registerApplication(java.lang.String applicationId, java.lang.String applicationName, java.lang.String applicationVersion) throws android.os.RemoteException;
public void addEventListener(horizon.perscon.IPersconServiceCallback callback, horizon.perscon.model.EventQuery template) throws android.os.RemoteException;
public void removeEventListener(horizon.perscon.IPersconServiceCallback callback) throws android.os.RemoteException;
public horizon.perscon.model.Event[] match(horizon.perscon.model.EventQuery template) throws android.os.RemoteException;
public horizon.perscon.model.Event add(java.lang.String applicationId, horizon.perscon.model.Person person, horizon.perscon.model.Place place, horizon.perscon.model.Thing thing) throws android.os.RemoteException;
}
