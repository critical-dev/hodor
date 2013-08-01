package ca.etsmtl.log735.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import ca.etsmtl.ca.log735.messages.CreateGroupRequest;
import ca.etsmtl.ca.log735.messages.CreateGroupResponse;
import ca.etsmtl.ca.log735.messages.CreateRoomRequest;
import ca.etsmtl.ca.log735.messages.CreateRoomResponse;
import ca.etsmtl.ca.log735.messages.JoinGroupRequest;
import ca.etsmtl.ca.log735.messages.JoinGroupResponse;
import ca.etsmtl.ca.log735.messages.JoinRoomRequest;
import ca.etsmtl.ca.log735.messages.JoinRoomResponse;
import ca.etsmtl.ca.log735.messages.LoginRefused;
import ca.etsmtl.ca.log735.messages.LoginRequest;
import ca.etsmtl.ca.log735.messages.RegisterResponse;
import ca.etsmtl.ca.log735.messages.RegisterRequest;
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
							clientOutputStream.writeObject(new JoinRoomResponse(server.getDefaultRoom()));
							server.getDefaultRoom().getUserlist().add(((LoginRequest) clientRequest).getUsername());
							System.out.println("ServerThread : added " + ((LoginRequest) clientRequest).getUsername() + " to default room.");
							//for internal processing purposes we add the client output stream to the list
							server.addClientOutputStream(((LoginRequest) clientRequest).getUsername(), clientOutputStream);
							System.out.println("ServerThread (internal) : added client's outputstream to global list of outputstreams.");
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
						if(clientRequest instanceof JoinRoomRequest){
							Room joinRequestRequestedRoom = ((JoinRoomRequest) clientRequest).getRoom();
							clientOutputStream.writeObject(new JoinRoomResponse(joinRequestRequestedRoom));
							System.out.println("ServerThread : sending room " + joinRequestRequestedRoom.getName() + " back to client.");
						}
						else if(clientRequest instanceof CreateRoomRequest){
							Room createRoomRequestedRoom = ((CreateRoomRequest) clientRequest).getRoom();
							clientOutputStream.writeObject(new CreateRoomResponse(createRoomRequestedRoom));
							System.out.println("ServerThread : sending back new room " + createRoomRequestedRoom.getName());
						}
						else if(clientRequest instanceof CreateGroupRequest){
							Group newGroup = ((CreateGroupRequest) clientRequest).getGroup();
							clientOutputStream.writeObject(new CreateGroupResponse(newGroup));
							System.out.println("ServerThread : sending back new group " + newGroup.getGroupName());
						}
						else if(clientRequest instanceof RegisterRequest){
							String newUser = ((RegisterRequest) clientRequest).getUsername();
							clientOutputStream.writeObject(new RegisterResponse(newUser));
							System.out.println("ServerThread :  Registration successful, sending back confirmation.");
						}
						else if(clientRequest instanceof JoinGroupRequest){
							Group joinedGroup = ((JoinGroupRequest) clientRequest).getGroup();
							clientOutputStream.writeObject(new JoinGroupResponse(joinedGroup));
							System.out.println("ServerThread : Join Group Request successful, sending back confirmation.");
						}
					}
					else{
						//in the case of unsucessful scenarios we send back null response messages.
						if(clientRequest instanceof JoinRoomRequest){
							clientOutputStream.writeObject(new JoinRoomResponse(null));
							System.err.println("ServerThread : join room request failed.");
						}
						else if(clientRequest instanceof CreateRoomRequest){
							clientOutputStream.writeObject(new CreateRoomResponse(null));
							System.err.println("ServerThread : create room request failed.");
						}
						else if(clientRequest instanceof CreateGroupResponse){
							clientOutputStream.writeObject(new CreateGroupResponse(null));
							System.out.println("ServerThread : create group request failed.");
						}
						else if(clientRequest instanceof RegisterRequest){
							clientOutputStream.writeObject(new RegisterResponse(null));
							System.err.println("ServerThread :  registration failed.");
						}
						else if(clientRequest instanceof JoinGroupRequest){
							clientOutputStream.writeObject(new JoinGroupResponse(null));
							System.err.println("ServerThread : Join Group Request failed.");
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
			}
		}
	}

	
}
