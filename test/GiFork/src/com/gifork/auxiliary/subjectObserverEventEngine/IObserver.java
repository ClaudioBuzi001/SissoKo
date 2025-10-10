/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 * 
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 * 
 */
package com.gifork.auxiliary.subjectObserverEventEngine;

import com.gifork.commons.data.IRawData;

/**
 * An asynchronous update interface for receiving notifications
 * about I information as the I is constructed.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public interface IObserver {

   /**
    * This method is called when information about an I
    * which was previously requested using an asynchronous
    * interface becomes available.
    *
    * @param subject Object
    */
   void update(IRawData subject);

}
