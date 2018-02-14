/*
 *
 * Copyright (c) 1999-2017 Luciad All Rights Reserved.
 *
 * Luciad grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Luciad.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. LUCIAD AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL LUCIAD OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF LUCIAD HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 */
package asterix;

import com.luciad.datamodel.TLcdDataType;

/**
 * Model descriptor for the track models containing the asterix tracks.
 */
public class TrackSimulationModelDescriptor extends SimulationModelDescriptor {

  /**
   * Creates the model descriptor.
   *
   * @param aSourceName  The source name of this model
   * @param aTypeName    The type of this model
   * @param aDisplayName The display name for this model
   * @param aDataType    The <code>TLcdDataType</code> describing the metadata of the tracks in this
   *                     model.
   */
  public TrackSimulationModelDescriptor(String aSourceName, String aTypeName, String aDisplayName, TLcdDataType aDataType) {
    super(aSourceName, aTypeName, aDisplayName, aDataType);
  }
}
