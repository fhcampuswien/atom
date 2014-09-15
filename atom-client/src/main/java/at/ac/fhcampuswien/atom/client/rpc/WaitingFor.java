/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.rpc;


public interface WaitingFor<D extends Object> {
    public void recieve(D result);
    public void requestFailed(String reason);
}
