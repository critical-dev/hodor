package ca.etsmtl.log735.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
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
				if(!server.getAuthenticatedIps().containsKey(clientIp)){
					//if the client is not authenticated, we only answer to
					//login or register requests
					if(clientRequest instanceof LoginRequest){
						boolean clientSuccessfullyAuthenticated = ((LoginRequest) clientRequest).process(server);
						if(!clientSuccessfullyAuthenticated){
							clientOutputStream.writeObject(new LoginRefused());
							System.out.println("ServerThread : client couldn't be authenticated, sent LoginRefused()");
						}
						else{
							server.getAuthenticatedIps().put(clientIp, ((LoginRequest) clientRequest).getUsername());
							System.out.println("ServerThread : authentication success adding IPs to authenticated IPs list. Sending DefaultRoom to client.");
							server.getDefaultRoom().getUserlist().add(((LoginRequest) clientRequest).getUsername());
							clientOutputStream.writeObject(new JoinConversationResponse(server.getDefaultRoom()));
							Vector<String> usersToNotifyOfAdd = server.getDefaultRoom().getUserlist();
							for(int i = 0 ; i< usersToNotifyOfAdd.size(); i++){
								for(String user : server.getClientsOutputStreams().keySet()){
									if(usersToNotifyOfAdd.get(i).equalsIgnoreCase(user)){
										System.out.println("ServerThread: Notified "+user+" that default room clients that this conversation user list has been updated.");
										server.getClientsOutputStreams().get(user).writeObject(new RefreshUserListResponse(server.getDefaultRoom()));
									}
								}
							}
							System.out.println("ServerThread : added " + ((LoginRequest) clientRequest).getUsername() + " to default room.");
							//for internal processing purposes we add the client output stream to the list
							server.addClientOutputStream(((LoginRequest) clientRequest).getUsername(), clientOutputStream);
							System.out.println("ServerThread (internal) : added client's outputstream to global list of outputstreams.");
							clientOutputStream.writeObject(new RoomListResponse(server.getRooms()));
						}
					}
					else if(clientRequest instanceof RegisterRequest){
						boolean clientSuccessfullyRegistered = ((RegisterRequest) clientRequest).process(server);
						if(clientSuccessfullyRegistered){
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
					else{
						System.err.println("ServerThread : refused "+clientRequest.getClass().getSimpleName() +" request, as client is not authenticated.");
					}
				}
				else if(clientRequest instanceof ServerMessage){
					boolean result = ((ServerMessage) clientRequest).process(server);
					System.out.println("Request from authenticated client : " + server.getAuthenticatedIps().get(clientIp) + "@" + clientIp + " : " + clientRequest.getClass().getSimpleName());
					//Note for leavegroup or leaveroom requests, we do nothing else than process the request.
					if(result){
						//in the case of successful scenarios we send back response messages with the proper info
						if(clientRequest instanceof CreateRoomRequest){
							Room createRoomRequestedRoom = ((CreateRoomRequest) clientRequest).getRoom();
							clientOutputStream.writeObject(new JoinConversationResponse(createRoomRequestedRoom));
							System.out.println("ServerThread : sending back new room " + createRoomRequestedRoom.getName());
							ArrayList<Room> singleNewRoom = new ArrayList<Room>();
							singleNewRoom.add(createRoomRequestedRoom);
							for(String user: server.getClientsOutputStreams().keySet()){
								server.getClientsOutputStreams().get(user).writeObject(new RoomListResponse(singleNewRoom));
							}
						}
						else if(clientRequest instanceof CreateGroupRequest){
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
						else if(clientRequest instanceof RegisterRequest){
							String newUser = ((RegisterRequest) clientRequest).getUsername();
							clientOutputStream.writeObject(new RegisterResponse(newUser));
							System.out.println("ServerThread :  Registration successful, sending back confirmation.");
						}
						else if(clientRequest instanceof JoinRoomRequest){
							Room joinedRoom = ((JoinRoomRequest) clientRequest).getRoom();
							clientOutputStream.writeObject(new JoinConversationResponse(joinedRoom));
							System.out.println("ServerThread :  join room successful, sending back confirmation.");
							Vector<String> usersToNotifyOfAdd = joinedRoom.getUserlist();
							for(int i = 0 ; i< usersToNotifyOfAdd.size(); i++){
								for(String user : server.getClientsOutputStreams().keySet()){
									if(usersToNotifyOfAdd.get(i).equalsIgnoreCase(user)){
										System.out.println("ServerThread: Notified "+user+" that room "+joinedRoom.getName()+" clients that this conversation user list has been updated.");
										server.getClientsOutputStreams().get(user).writeObject(new RefreshUserListResponse(joinedRoom));
									}
								}
							}
						}
					}
					else{
						//in the case of unsucessful scenarios we send back null response messages.
						if(clientRequest instanceof CreateRoomRequest){
							clientOutputStream.writeObject(new JoinConversationResponse(null));
							System.err.println("ServerThread : create room request failed.");
						}
						else if(clientRequest instanceof CreateGroupRequest){
							clientOutputStream.writeObject(new JoinConversationResponse(null));
							System.out.println("ServerThread : create group request failed.");
						}
						else if(clientRequest instanceof RegisterRequest){
							clientOutputStream.writeObject(new RegisterResponse(null));
							System.err.println("ServerThread :  registration failed.");
						}
						else if(clientRequest instanceof JoinRoomRequest){
							clientOutputStream.writeObject(new JoinConversationResponse(null));
							System.out.println("ServerThread :  join room failed.");
						}
					}
				}
				else{
					System.out.println("Unsupported request from authenticated client : " + server.getAuthenticatedIps().get(clientIp) + "@" + clientIp + " : " + clientRequest.getClass().getSimpleName());
				}
			} catch (ClassNotFoundException e) {
				System.err.println("ServerThread exception while reading object.");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("ServerThread exception while reading object.");
				e.printStackTrace();
				System.err.println(">> removing client " + clientIp);
				server.getAuthenticatedIps().remove(clientIp);
				try {
					clientOutputStream.close();
					clientInputStream.close();
					String userToRemove = server.getAuthenticatedIps().get(clientIp);
					if(server.getClientsOutputStreams().containsKey(userToRemove)){
						server.getClientsOutputStreams().remove(userToRemove);
					}
					if(server.getClientInputStreams().containsKey(userToRemove)){
						server.getClientInputStreams().remove(userToRemove);
					}
					if(server.getAuthenticatedUsers().contains(userToRemove))server.getAuthenticatedUsers().remove(userToRemove);
				} catch (IOException e1) {
				}
				
				break;
			}
		}
	}

	
}
