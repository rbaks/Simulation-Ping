/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.MACAddressString;
import inet.ipaddr.mac.MACAddress;
import java.util.HashMap;
import java.util.Map;
import simulation.Computer;
import simulation.Switch;

/**
 *
 * @author dodaa
 */
public class Test {
    public static void main(String[] args) {
        try {
            
            Computer c1 = new Computer("C1", "192.168.10.3/8", "00:0a:95:9d:68:16");
            c1.setPorts(new String[] {"f0/0",  "f0/1"});

            Computer c2 = new Computer("C2", "192.168.10.2/8", "00:0a:95:9d:68:15");
            c2.setPorts(new String[] {"f0/0",  "f0/1"});
            
            Computer c3 = new Computer("C3", "192.168.10.4/8", "00:0a:95:9d:68:14");
            c3.setPorts(new String[] {"f0/0",  "f0/1"});
            
            Map<IPAddress, MACAddress> defaultArpTable = new HashMap<>();
            defaultArpTable.put(new IPAddressString("192.168.10.3/8").getAddress(), new MACAddressString("00:0a:95:9d:68:16").getAddress());
            defaultArpTable.put(new IPAddressString("192.168.10.2/8").getAddress(), new MACAddressString("00:0a:95:9d:68:15").getAddress());
            defaultArpTable.put(new IPAddressString("192.168.10.4/8").getAddress(), new MACAddressString("00:0a:95:9d:68:14").getAddress());
            
//            c1.setArpTable(defaultArpTable);
//            c2.setArpTable(defaultArpTable);
//            c3.setArpTable(defaultArpTable);
            
            Switch aswitch = new Switch("S1");
            aswitch.setPorts(new String[] {"g0/0",  "g0/1", "g0/2"});
            
            c1.connectTo(aswitch, "f0/0", "g0/0");
            c2.connectTo(aswitch, "f0/0", "g0/1");
            c3.connectTo(aswitch, "f0/0", "g0/2");
            
            c1.ping(c2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
