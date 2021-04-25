/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.MACAddressString;
import inet.ipaddr.mac.MACAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import messages.Message;
import requests.Data;

/**
 *
 * @author dodaa
 */
public abstract class Host {
    private String hostName;
    private IPAddress ipAddress;
    private MACAddress macAddress;
    private Map<IPAddress, MACAddress> arpTable;
    private String[] ports;
    // ethernet_cable/port simulation by memory address reference
    private Map<String, Host> portInterfaces;
    
    public Host(String hostName, String ipAddress, String macAddress) throws AddressStringException {
        setHostName(hostName);
        setIpAddress(ipAddress);
        setMacAddress(macAddress);
        setArpTable(new HashMap<>());
        setPortInterfaces(new HashMap<>());
    }
    
    public MACAddress getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddressStr) throws AddressStringException {
        this.macAddress = new MACAddressString(macAddressStr).getAddress();
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddressStr) throws AddressStringException {
        this.ipAddress = new IPAddressString(ipAddressStr).getAddress();
    }

    public Map<IPAddress, MACAddress> getArpTable() {
        return arpTable;
    }

    public void setArpTable(Map<IPAddress, MACAddress> arpTable) {
        this.arpTable = arpTable;
    }

    public String[] getPorts() {
        return ports;
    }

    public void setPorts(String[] ports) {
        this.ports = ports;
    }

    public Map<String, Host> getPortInterfaces() {
        return portInterfaces;
    }

    public void setPortInterfaces(Map<String, Host> portInterfaces) {
        this.portInterfaces = portInterfaces;
    }
    
    public void addPortInterfaces(String port, Host connected) throws Exception {
        if (!Arrays.asList(this.ports).contains(port)) throw new Exception("Unknown port");
        this.portInterfaces.put(port, connected);
    }
    
    public void addArpTable(String ipAddressStr, String macAddressStr) throws AddressStringException {
        IPAddress ipAddress = new IPAddressString(ipAddressStr).getAddress();
        MACAddress macAddress = new MACAddressString(macAddressStr).getAddress();
        arpTable.put(ipAddress, macAddress);
    }
    
    public boolean areInSameNetwork(IPAddress ipA, IPAddress ipB) {
        IPAddress networkAddressA = getNetworkAddress(ipA);
        IPAddress networkAddressB = getNetworkAddress(ipB);
        return networkAddressA.equals(networkAddressB);
    }

    public IPAddress getNetworkAddress(IPAddress ipAddress) {
        IPAddress networkMask = ipAddress.getNetworkMask();
        return ipAddress.mask(networkMask);
    }
    
    public abstract void sendMessage(Message message, String port);
    public abstract void receiveMessage(Message message, String port);
    
    
}
