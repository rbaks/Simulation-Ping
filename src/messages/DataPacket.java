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
public abstract class DataPacket {
    private Address source;
    private Address destination;

    public DataPacket(Address source, Address destination) {
        setSource(source);
        setDestination(destination);
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }
}
