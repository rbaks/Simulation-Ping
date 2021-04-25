/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import inet.ipaddr.Address;
import requests.Data;

/**
 *
 * @author dodaa
 */
public class IPDatagram extends DataPacket {
    // Didn't go further the TCPSegment
    private Data message;

    public Data getMessage() {
        return message;
    }

    public void setMessage(Data message) {
        this.message = message;
    }
    
    public IPDatagram(Address source, Address destination) {
        super(source, destination);
    }
}
