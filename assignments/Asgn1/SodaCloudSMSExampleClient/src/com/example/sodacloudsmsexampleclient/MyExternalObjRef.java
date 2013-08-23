package com.example.sodacloudsmsexampleclient;

import org.magnum.soda.proxy.ObjRef;

public class MyExternalObjRef implements ExternalObjRef {

	private ObjRef ref;
	private String subHost;
	public MyExternalObjRef(ObjRef ref, String subHost){
		this.ref = ref;
		this.subHost = subHost;
	}
	@Override
	public ObjRef getObjRef() {
		return this.ref;
	}

	@Override
	public String getPubSubHost() {
		return this.subHost;
	}
	
	@Override
	public String toString(){
		return getPubSubHost()+"|"+getObjRef().getUri();
	}

}
