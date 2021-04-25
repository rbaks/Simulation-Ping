/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package requests;

import inet.ipaddr.MACAddressString;
import inet.ipaddr.mac.MACAddress;

/**
 *
 * @author dodaa
 */
public class ARPRequest extends Request {

    public static final MACAddress BROADCAST_MAC_ADDRESS = new MACAddressString("FF:FF:FF:FF:FF:FF").getAddress();
    
}
