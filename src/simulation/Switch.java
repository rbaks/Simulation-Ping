/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.mac.MACAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import messages.EthernetFrame;
import messages.Message;
import requests.ARPRequest;

/**
 *
 * @author dodaa
 */
public class Switch extends Host {
    private Map<MACAddress, String> macAddressTable;

    public Switch(String HostName) throws AddressStringException {
        super(HostName, null, null);
        macAddressTable = new HashMap<>();
    }
    
    public Map<MACAddress, String> getMacAddressTable() {
        return macAddressTable;
    }

    public void setMacAddressTable(Map<MACAddress, String> macAddressTable) {
        this.macAddressTable = macAddressTable;
    }
    
    public void addMacAddressTable(MACAddress mACAddress, String portString) throws Exception {
        if (!Arrays.asList(this.getPorts()).contains(portString)) throw new Exception("Unknown port interface");
        macAddressTable.put(mACAddress, portString);
    }

    @Override
    public void sendMessage(Message message, String port) {
        Host destinationHost = this.getPortInterfaces().get(port);
        if (destinationHost != null) {
            destinationHost.receiveMessage(message, "no need to know");
        }
    }

    @Override
    public void receiveMessage(Message message, String port) {
        
        System.out.println(this.getHostName() + " receives a packet");
        EthernetFrame ethernetFrame = (EthernetFrame) message.pop();
        System.out.println(this.getHostName() + " looks its CAM Table for updates");
        macAddressTable.put((MACAddress) ethernetFrame.getSource(), port);
        MACAddress destinationMacAddress = (MACAddress) ethernetFrame.getDestination();

        if (destinationMacAddress.equals(ARPRequest.BROADCAST_MAC_ADDRESS)) 
            System.out.println(this.getHostName() + " sees it's an ARP request");
        
        String foundPortInterface = macAddressTable.get(destinationMacAddress);
        if (!destinationMacAddress.equals(ARPRequest.BROADCAST_MAC_ADDRESS)) 
            System.out.println(this.getHostName() + " makes a lookup for " + destinationMacAddress + "'s outbound port in its CAM Table. Found -> " + foundPortInterface);
        
        message.push(ethernetFrame);
        if (
                foundPortInterface == null ||
                destinationMacAddress.equals(ARPRequest.BROADCAST_MAC_ADDRESS) 
            ) {
            if (foundPortInterface == null) System.out.println(this.getHostName() + " broadcasts the message");
            for (String p : this.getPorts()) {
                if (!p.equals(port)) {
                    Message clonedMessage = (Message) message.clone();
                    sendMessage(clonedMessage, p);
                }
            }
        } else {
            System.out.println(this.getHostName() + " bridges the connection through port " + foundPortInterface);
            sendMessage(message, foundPortInterface);
        }
    }

}
