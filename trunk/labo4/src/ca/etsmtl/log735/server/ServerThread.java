package ca.etsmtl.log735.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

import ca.etsmtl.ca.log735.messages.CreateGroupRequest;
import ca.etsmtl.ca.log735.messages.CreateRoomRequest;
import ca.etsmtl.ca.log735.messages.JoinConversationResponse;
import ca.etsmtl.ca.log735.messages.JoinRoomRequest;
import ca.etsmtl.ca.log735.messages.LoginRefused;
import ca.etsmtl.ca.log735.messages.LoginRequest;
import ca.etsmtl.ca.log735.messages.RefreshUserListResponse;
import ca.etsmtl.ca.log735.messages.RegisterRequest;
import ca.etsmtl.ca.log735.messages.RegisterResponse;
import ca.etsmtl.ca.log735.messages.RoomListResponse;
import ca.etsmtl.ca.log735.messages.ServerMessage;
import ca.etsmtl.log735.model.Group;
import ca.etsmtl.log735.model.Room;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
 ******************************************************/
public class ServerThread extends Thread{

	private Server server;
	private ObjectInputStream clientInputStream;
	private ObjectOutputStream clientOutputStream;
	private InetAddress clientIp;
	private String thisUser;

	public ServerThread(Server server, Socket incomingClient) {
		this.server = server;
		try {
			clientIp = incomingClient.getInetAddress();
			System.out.println("Received client on : " + clientIp);
			clientOutputStream = new ObjectOutputStream(incomingClient.getOutputStream());
			clientInputStream = new ObjectInputStream(incomingClient.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		System.out.println("Attempting to authenticate client... ");
		while(true){
			try {
				Object clientRequest = clientInputStream.readObject();
				resetStreamCaches();//SUPER IMPORTANT
				if(clientRequest instanceof ServerMessage){
					//if the client is not authenticated, we only answer to
					//login or register requests
					boolean requestProcessed = ((ServerMessage) clientRequest).process(server);
					if(clientRequest instanceof LoginRequest){
						if(!requestProcessed){
							clientOutputStream.writeObject(new LoginRefused());
							System.out.println("ServerThread : client couldn't be authenticated, sent LoginRefused()");
						}
						else{
							synchronized(server){
								thisUser = ((LoginRequest) clientRequest).getUsername();
								//for internal processing purposes we add the client output stream to the list
								server.addClientOutputStream(((LoginRequest) clientRequest).getUsername(), clientOutputStream);
								System.out.println("ServerThread (internal) : added client's outputstream to global list of outputstreams.");
								server.getDefaultRoom().getUserlist().add(((LoginRequest) clientRequest).getUsername());
								System.out.println("ServerThread : added " + ((LoginRequest) clientRequest).getUsername() + " to default room.");
								clientOutputStream.writeObject(new JoinConversationResponse(server.getDefaultRoom()));
								Vector<String> usersToNotifyOfAdd = server.getDefaultRoom().getUserlist();
								for(int i = 0 ; i< usersToNotifyOfAdd.size(); i++){
									System.out.println("ServerThread: Notified "+usersToNotifyOfAdd.get(i)+" that default room clients user list ("+server.getDefaultRoom().getUserlist().size()+") has been updated.");
									server.getClientsOutputStreams().get(usersToNotifyOfAdd.get(i)).writeObject(new RefreshUserListResponse(server.getDefaultRoom()));
								}
								clientOutputStream.writeObject(new RoomListResponse(server.getRooms()));
							}
						}
					}
					else if(clientRequest instanceof RegisterRequest){
						if(requestProcessed){
							//send back a RegisterAccepted message providing the input username.
							System.out.println("ServerThread :  Registration successful, sending back confirmation.");
							clientOutputStream.writeObject(new RegisterResponse(((RegisterRequest) clientRequest).getUsername()));
						}
						else{
							//send back a RegisterRefused message if something went wrong.
							System.out.println("ServerThread : Registration failed, sending back null response message.");
							clientOutputStream.writeObject(new RegisterResponse(null));
						}
					}
					else if(clientRequest instanceof CreateRoomRequest){
						if(requestProcessed){
							Room createRoomRequestedRoom = ((CreateRoomRequest) clientRequest).getRoom();
							clientOutputStream.writeObject(new JoinConversationResponse(createRoomRequestedRoom));
							System.out.println("ServerThread : sending back new room " + createRoomRequestedRoom.getName());
							ArrayList<Room> singleNewRoom = new ArrayList<Room>();
							singleNewRoom.add(createRoomRequestedRoom);
							for(String user: server.getClientsOutputStreams().keySet()){
								server.getClientsOutputStreams().get(user).writeObject(new RoomListResponse(singleNewRoom));
							}
						}
						else {
							clientOutputStream.writeObject(new JoinConversationResponse(null));
							System.err.println("ServerThread : create room request failed.");
						}
					}
					else if(clientRequest instanceof CreateGroupRequest){
						if(requestProcessed){
							Group newGroup = ((CreateGroupRequest) clientRequest).getGroup();
							clientOutputStream.writeObject(new JoinConversationResponse(newGroup));
							System.out.println("ServerThread : sending back new group " + newGroup.getName() + " to requester : " + ((CreateGroupRequest) clientRequest).getCreateGroupRequester());
							Vector<String> newGroupMembersToNotify = ((CreateGroupRequest) clientRequest).getUsersAddedToGroup();
							for(int i = 0; i < newGroupMembersToNotify.size(); i++){
								for(String user : server.getClientsOutputStreams().keySet()){
									if(newGroupMembersToNotify.get(i).equalsIgnoreCase(user)){
										server.getClientsOutputStreams().get(user).writeObject(new JoinConversationResponse(newGroup));
										break;
									}
								}
							}
						}
						else {
							clientOutputStream.writeObject(new JoinConversationResponse(null));
							System.out.println("ServerThread : create group request failed.");
						}
					}
					else if(clientRequest instanceof RegisterRequest){
						if(requestProcessed){
							String newUser = ((RegisterRequest) clientRequest).getUsername();
							clientOutputStream.writeObject(new RegisterResponse(newUser));
							System.out.println("ServerThread :  Registration successful, sending back confirmation.");
						}
						else{
							clientOutputStream.writeObject(new RegisterResponse(null));
							System.err.println("ServerThread :  registration failed.");
						}
					}
					else if(clientRequest instanceof JoinRoomRequest){
						if(requestProcessed){
							Room joinedRoom = ((JoinRoomRequest) clientRequest).getRoom();
							clientOutputStream.writeObject(new JoinConversationResponse(joinedRoom));
							System.out.println("ServerThread :  join room successful, sending back confirmation.");
							Vector<String> usersToNotifyOfAdd = joinedRoom.getUserlist();
							for(int i = 0 ; i< usersToNotifyOfAdd.size(); i++){
								for(String user : server.getClientsOutputStreams().keySet()){
									if(usersToNotifyOfAdd.get(i).equalsIgnoreCase(user)){
										UUID randomUUID = UUID.randomUUID();
										System.out.println("ServerThread: Notified "+user+" that room "+joinedRoom.getName()+" clients that this conversation user list has been updated. [UUID:"+randomUUID+"]");
										server.getClientsOutputStreams().get(user).writeObject(new RefreshUserListResponse(joinedRoom));
										break;
									}
								}
							}
						}
						else{
							clientOutputStream.writeObject(new JoinConversationResponse(null));
							System.out.println("ServerThread :  join room failed.");
						}
					}
				}
				else{
					System.err.println("ServerThread : Unsupported request " + clientRequest.getClass().getSimpleName());
				}
			} catch (ClassNotFoundException e) {
				System.err.println("ServerThread exception while reading object.");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("ServerThread exception while reading object.");
				e.printStackTrace();
				System.err.println(">> removing client " + thisUser + "@" + clientIp);
				if(server.getAuthenticatedUsers().contains(thisUser.toLowerCase())){
					System.err.println(">> server, removed " + thisUser + " from authenticated users.");
					server.getAuthenticatedUsers().remove(thisUser.toLowerCase());
				}
				else if(server.getAuthenticatedUsers().contains(thisUser)){
					System.err.println(">> server, removed " + thisUser + " from authenticated users.");
					server.getAuthenticatedUsers().remove(thisUser);
				}
				try {
					if(server.getClientsOutputStreams().containsKey(thisUser)){
						server.getClientsOutputStreams().remove(thisUser);
					}
					if(server.getClientInputStreams().containsKey(thisUser)){
						server.getClientInputStreams().remove(thisUser);
					}
					clientOutputStream.close();
					clientInputStream.close();
				} catch (IOException e1) {
				}
				//on enleve le client des salles / groupes.
				for(int i = 0; i < server.getRooms().size(); i++){
					if(server.getRooms().get(i).getUserlist().contains(thisUser)){
						server.getRooms().get(i).getUserlist().remove(thisUser);
						if(server.getRooms().get(i).getUserlist().size() == 0){
							server.getRooms().remove(i);
						}
					}
				}
				for(Group group : server.getGroupsWithConversations().keySet()){
					if(group.getUserlist().contains(thisUser)){
						group.getUserlist().remove(thisUser);
						if(group.getUserlist().size() == 0){
							server.getGroupsWithConversations().remove(group);
						}
					}
				}

				break;
			}
		}
	}
	//for reset to the streams because otherwise the defualt java behavior for
	//object output streams is to resend a POINTER to that object instead
	//of the actual object, to save on bandwidth, but the receiver never knows
	//the object actually changed...
	//thanks to the internet...
	//ref : http://www.javaspecialists.eu/archive/Issue088.html
	public void resetStreamCaches() throws IOException{
		for(String user : server.getClientsOutputStreams().keySet()){
			server.getClientsOutputStreams().get(user).reset();
		}
	}
}
