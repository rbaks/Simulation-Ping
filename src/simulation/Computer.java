/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.mac.MACAddress;
import messages.EthernetFrame;
import messages.IPDatagram;
import messages.Message;
import requests.ARPRequest;
import static requests.ARPRequest.BROADCAST_MAC_ADDRESS;
import requests.ARPResponse;
import requests.Data;
import requests.PingRequest;
import requests.PingResponse;

/**
 *
 * @author dodaa
 */
public class Computer extends Host {
    private IPAddress defaultGateway;
    private String connectedHostPort;
    private String connectedSwitchPort;
    
    // In memory stored IP Datagram for later use after ARP Table is being filled
    private IPDatagram tempIpDatagram;

     public Computer(String hostName, String ipAddressStr, String macAddressStr) 
            throws AddressStringException {
        super(hostName, ipAddressStr, macAddressStr);
    }

    public IPAddress getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(String defaultGatewayStr) throws AddressStringException {
        this.defaultGateway = new IPAddressString(defaultGatewayStr).getAddress();
    }

    public String getConnectedHostPort() {
        return connectedHostPort;
    }

    public void setConnectedHostPort(String connectedHostPort) {
        this.connectedHostPort = connectedHostPort;
    }

    public String getConnectedSwitchPort() {
        return connectedSwitchPort;
    }

    public void setConnectedSwitchPort(String connectedSwitchPort) {
        this.connectedSwitchPort = connectedSwitchPort;
    }
    
    public void connectTo(Switch sw, String hostPort, String switchPort) throws Exception {
        
        // Simulates ethernet_cable/port connexion by memory address references
        sw.getPortInterfaces().put(switchPort, this);
        this.getPortInterfaces().put(hostPort, sw);
        
        setConnectedHostPort(hostPort);
        setConnectedSwitchPort(switchPort);
    }
    
    public void ping(Computer computer) {
        IPAddress ipAddressDest = computer.getIpAddress();
        System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") wants to ping " + ipAddressDest);
        PingRequest pingRequest = new PingRequest();
        pingRequest.setPayload("Hello Network !");
        sendPacket(pingRequest, ipAddressDest);
    }
    
    public void sendPacket(Data messageData, IPAddress ipDest) {
        if (areInSameNetwork(getIpAddress(), ipDest)) {
            System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") sees " + ipDest + " is in local network");
            Message message = createMessage(messageData, this.getIpAddress(), ipDest);
            // Caused by ARP Request for stoping the current request to fill the ARP table
            if (message == null) return;
            System.out.println(this.getHostName() + " pings " + ipDest + " through port " + this.connectedHostPort);
            sendMessage(message, this.connectedHostPort);
        }
        else {
            System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") sees " + ipDest + " is in external network");
            System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") sends the request to it's default gateway " + this.getDefaultGateway());
            System.out.println();
            System.out.println("projet aboutis en LAN");
            createMessage(messageData, this.getIpAddress(), this.getDefaultGateway());
        }
    }
    
    public Message createMessage(Data messageData, IPAddress source, IPAddress dest) {
        Message message = new Message();
        
        IPDatagram ipDatagram = new IPDatagram(source, dest);
        ipDatagram.setMessage(messageData);
        
        MACAddress destMac = this.getArpTable().get(dest);
        System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") makes a lookup for " + dest + "'s MAC Address in its ARP Table. Found -> " + destMac);
        
        if (destMac == null) {
            System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") sends an arp request to " + dest);
            ARPRequest arpRequest = new ARPRequest();
            this.tempIpDatagram = ipDatagram;
            sendARPRequest(source, dest);
            return null;
        }
        
        EthernetFrame ethernetFrame = new EthernetFrame(this.getMacAddress(), destMac);
        
        message.push(ipDatagram);
        message.push(ethernetFrame);
        return message;
    }
    
    public void sendARPRequest(IPAddress source, IPAddress dest) {
        Message message = new Message();
        
        IPDatagram ipDatagram = new IPDatagram(this.getIpAddress(), dest);
        
        EthernetFrame ethernetFrame = new EthernetFrame(this.getMacAddress(), BROADCAST_MAC_ADDRESS);
        
        message.push(ipDatagram);
        message.push(ethernetFrame);
        sendMessage(message, this.connectedHostPort);
    } 
    
    public void sendARPResponse(IPAddress source, IPAddress dest, MACAddress destMac) {
        Message message = new Message();
        
        ARPResponse arpResponse = new ARPResponse();
        arpResponse.setPayload(this.getMacAddress());
        
        IPDatagram ipDatagram = new IPDatagram(source, dest);
        ipDatagram.setMessage(arpResponse);
        
        EthernetFrame ethernetFrame = new EthernetFrame(this.getMacAddress(), destMac);
        
        message.push(ipDatagram);
        message.push(ethernetFrame);
        sendMessage(message, this.connectedHostPort);
    }
    
    public void sendTempMessage(IPAddress source, IPAddress dest, MACAddress destMac) {
        Message message = new Message();
        EthernetFrame ethernetFrame = new EthernetFrame(this.getMacAddress(), destMac);
        message.push(tempIpDatagram);
        message.push(ethernetFrame);
        sendMessage(message, this.connectedHostPort);
    }

    @Override
    public void sendMessage(Message message, String port) {
        Host connectedSwitch = this.getPortInterfaces().get(port);
        connectedSwitch.receiveMessage(message, this.connectedSwitchPort);
    }

    @Override
    public void receiveMessage(Message message, String port) {
        EthernetFrame frame = (EthernetFrame) message.pop();
        
        if (frame.getDestination().equals(BROADCAST_MAC_ADDRESS)) {
            IPDatagram datagram = (IPDatagram) message.pop();
            if (datagram.getDestination().equals(this.getIpAddress())) {
                System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") receives an ARP request from " + datagram.getSource());
                System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") sends an ARP response to " + datagram.getSource() + " : " + this.getMacAddress());
                sendARPResponse(this.getIpAddress(), (IPAddress) datagram.getDestination(), (MACAddress) frame.getSource());
                return;
            }
        }
        
        if (frame.getDestination().equals(this.getMacAddress())) {
            IPDatagram datagram = (IPDatagram) message.pop();
            IPAddress ip = (IPAddress) datagram.getSource();
            Data receivedMessage = datagram.getMessage();
            
            if (receivedMessage instanceof PingRequest) {
                System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") receives a ping request : " + receivedMessage.getPayload());
            }
            
            if (receivedMessage instanceof ARPResponse) {
                System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") receives an arp response : " + frame.getSource());
                System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") adds the new entry to its ARP Table");
                this.getArpTable().put((IPAddress) datagram.getSource(), (MACAddress) frame.getSource());
                System.out.println(this.getHostName() + " (" + this.getIpAddress() + ") sends original ping request to " + ip);
                sendTempMessage(this.getIpAddress(), ip, (MACAddress) frame.getSource());
            }
        }
    }
}
