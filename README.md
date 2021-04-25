## ETU987

## Exemple Test

Avec default ARP Table pour tous le Computers
```java
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
            
c1.setArpTable(defaultArpTable);
c2.setArpTable(defaultArpTable);
c3.setArpTable(defaultArpTable);
            
Switch aswitch = new Switch("S1");
aswitch.setPorts(new String[] {"g0/0",  "g0/1", "g0/2"});

c1.connectTo(aswitch, "f0/0", "g0/0");
c2.connectTo(aswitch, "f0/0", "g0/1");
c3.connectTo(aswitch, "f0/0", "g0/2");
            
c1.ping(c2);
```
Output :
```
C1 (192.168.10.3/8) wants to ping 192.168.10.2/8
C1 (192.168.10.3/8) sees 192.168.10.2/8 is in local network
C1 (192.168.10.3/8) makes a lookup for 192.168.10.2/8's MAC Address in its ARP Table. Found -> 00:0a:95:9d:68:15
C1 pings 192.168.10.2/8 through port f0/0
S1 receives a packet
S1 looks its CAM Table for updates
S1 makes a lookup for 00:0a:95:9d:68:15's outbound port in its CAM Table. Found -> null
S1 broadcasts the message
C2 (192.168.10.2/8) receives a ping request : Hello Network !
```

Sans default ARP Table pour tous le Computers
```java
Computer c1 = new Computer("C1", "192.168.10.3/8", "00:0a:95:9d:68:16");
c1.setPorts(new String[] {"f0/0",  "f0/1"});

Computer c2 = new Computer("C2", "192.168.10.2/8", "00:0a:95:9d:68:15");
c2.setPorts(new String[] {"f0/0",  "f0/1"});
            
Computer c3 = new Computer("C3", "192.168.10.4/8", "00:0a:95:9d:68:14");
c3.setPorts(new String[] {"f0/0",  "f0/1"});
           
            
Switch aswitch = new Switch("S1");
aswitch.setPorts(new String[] {"g0/0",  "g0/1", "g0/2"});
            
c1.connectTo(aswitch, "f0/0", "g0/0");
c2.connectTo(aswitch, "f0/0", "g0/1");
c3.connectTo(aswitch, "f0/0", "g0/2");
            
c1.ping(c2);
```
Output :
```
C1 (192.168.10.3/8) wants to ping 192.168.10.2/8
C1 (192.168.10.3/8) sees 192.168.10.2/8 is in local network
C1 (192.168.10.3/8) makes a lookup for 192.168.10.2/8's MAC Address in its ARP Table. Found -> null
C1 (192.168.10.3/8) sends an arp request to 192.168.10.2/8
S1 receives a packet
S1 looks its CAM Table for updates
S1 sees it's an ARP request
S1 broadcasts the message
C2 (192.168.10.2/8) receives an ARP request from 192.168.10.3/8
C2 (192.168.10.2/8) sends an ARP response to 192.168.10.3/8 : 00:0a:95:9d:68:15
S1 receives a packet
S1 looks its CAM Table for updates
S1 makes a lookup for 00:0a:95:9d:68:16's outbound port in its CAM Table. Found -> g0/0
S1 bridges the connection through port g0/0
C1 (192.168.10.3/8) receives an arp response : 00:0a:95:9d:68:15
C1 (192.168.10.3/8) adds the new entry to its ARP Table
C1 (192.168.10.3/8) sends original ping request to 192.168.10.2/8
S1 receives a packet
S1 looks its CAM Table for updates
S1 makes a lookup for 00:0a:95:9d:68:15's outbound port in its CAM Table. Found -> g0/1
S1 bridges the connection through port g0/1
C2 (192.168.10.2/8) receives a ping request : Hello Network !
```
