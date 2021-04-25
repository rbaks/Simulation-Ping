/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import inet.ipaddr.Address;
/**
 *
 * @author dodaa
 */
public class EthernetFrame extends DataPacket {
    
    public EthernetFrame(Address source, Address destination) {
        super(source, destination);
    }
    
}
