package ameer.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;

import ameer.net.impl.rci.BCAST;
import ameer.net.srv.BlockingConnectionHandler;
import ameer.net.srv.bidi.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {
	ConcurrentHashMap<Integer,ConnectionHandler<T>> map=new ConcurrentHashMap<Integer,ConnectionHandler<T>>(10);
	ConcurrentHashMap<String,Integer> names= new ConcurrentHashMap<String,Integer>(10);
	int currentConnectionId =0;
	@Override
	public boolean  send(int connectionId, T msg) {
		ConnectionHandler<T> temp=map.get(connectionId);
		if(temp!=null){
		temp.send(msg);
			return true;
		}
		return false;
	}
	 public int addConnection(ConnectionHandler connectionHandler){
	    	map.put(currentConnectionId, connectionHandler);
	    	return currentConnectionId++;
	    	
	    }
	 public boolean addName(String name,int connectionId){
		 if(!names.containsKey(name)){
			 names.put(name,connectionId);
			 return true;
		}
		 return false;
	 }
	
	
	public ConnectionHandler<T> getHandler(int connectionID){
		  return map.get(connectionID);
	  }


	@Override
	public void broadcast(T msg) {
		for(ConnectionHandler<T> handler:map.values()){
			handler.send(msg);
		}

	}

	public void removeNameAndDisconnect(int connectionId,String name) {
		
	
		if(name!=null){
		names.remove(name);
		}
		map.remove(connectionId);

	}
	
	
	public void disconnect(int connectionId) {
		
//		String name=map.get(connectionId).getName();
//		if(name!=null){
//		names.remove(name);
//		map.remove(connectionId);
//		}

	}


}
