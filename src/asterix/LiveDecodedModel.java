package asterix;

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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.swing.Timer;

import com.luciad.format.asterix.TLcdASTERIXLiveDecoder;
import com.luciad.gui.TLcdAWTUtil;
import com.luciad.model.ILcdModel;
import com.luciad.model.ILcdModelContainerListener;
import com.luciad.model.TLcdModelContainerEvent;
import com.luciad.model.TLcdModelList;
import com.luciad.util.logging.ILcdLogger;
import com.luciad.util.logging.TLcdLoggerFactory;

import java.net.*;

/**
* Model list that updates its content using an ASTERIX live decoder.
*/
public class LiveDecodedModel extends TLcdModelList {

 private static final ILcdLogger LOGGER = TLcdLoggerFactory.getLogger(LiveDecodedModel.class);

 // Keep a large track history in memory. Some ASTERIX data is only sometimes available. Having a large track history
 // allows us to find this data in the track history. See for example ASTERIXTrackLabelContentProvider.
 private static final int TRACK_HISTORY = 200;

 private final TransformationProvider fTransformationProvider;
 //private final String fAsterixFile;
 private final ResultCallback fCallback;
 private TLcdASTERIXLiveDecoder liveDecoder;

 private Timer fTimer;

 public LiveDecodedModel( TransformationProvider aTransformationProvider, final ResultCallback aCallback) {
   fTransformationProvider = aTransformationProvider;
   //fAsterixFile = aAsterixFile;
   fCallback = aCallback;
   addModelContainerListener(new ILcdModelContainerListener() {
     @Override
     public void modelContainerStateChanged(final TLcdModelContainerEvent aModelContainerEvent) {
       if ((aModelContainerEvent.getID() & TLcdModelContainerEvent.MODEL_ADDED) != 0) {
         TLcdAWTUtil.invokeAndWait(new Runnable() {
           @Override
           public void run() {
             aCallback.trackModelAdded(LiveDecodedModel.this, aModelContainerEvent.getModel());
             aCallback.update(LiveDecodedModel.this);
           }
         });
       }
     }
   });
 }

 public void startLiveDecoder() throws IOException {
//	startFileLiveDecoder();
	startNetworkLiveDecoder();
 }
 
 /*private void startFileLiveDecoder() {
	  try {
		// Create and initialize the ASTERIX model decoder. For the purpose of this sample,
		// a file containing final ASTERIX data is replayed using the
		// TLcdASTERIXFinalReplayInputStream. This input stream will take the final ASTERIX
		// data and let clients pretend that it is a live ASTERIX stream. In real-world
		// scenarios, this step would be replaced by the preparation of an InputStream that
		// receives data from a radar, for instance, over the network.
		int fileFormat = getFileFormat(fAsterixFile);
		InputStream inputStream = createFinalReplayInputStream(fAsterixFile, fileFormat);
		startLiveDecoder(fTransformationProvider, TRACK_HISTORY, inputStream);
	  }catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
	}
 }*/
 
 private void startNetworkLiveDecoder() {
	// Crea e inicializa un ASTERIX model decoder que decodifica datagramas con paquetes
	// en formato ASTERIX, provenientes de un socket UDP multicast.
	liveDecoder = new TLcdASTERIXLiveDecoder();
	liveDecoder.setHistoryLength(TRACK_HISTORY);
	if (fTransformationProvider != null) {
		  liveDecoder.setTransformationProvider(fTransformationProvider);
	}
	
	Thread asterixThread = new Thread(new ASTERIXListener());
	asterixThread.start();
	
	//Thread navdataThread = new Thread(new NAVDATAListener());
	//navdataThread.start();
 }

 /*private static int getFileFormat(String aAsterixFile) {
   if (aAsterixFile.endsWith(".pcap")) {
     return TLcdASTERIXModelDecoder.PCAP_FORMAT;
   } else {
     return TLcdASTERIXModelDecoder.ASTERIX_FINAL_FORMAT;
   }
 }*/

 @Override
 public void dispose() {
   if (fTimer != null) {
     fTimer.stop();
   }
   super.dispose();
 }

 /*private InputStream createFinalReplayInputStream(String aAsterixFile, int aFileFormat) throws IOException {
   try {
     TLcdInputStreamFactory inputStreamFactory = new TLcdInputStreamFactory();
     InputStream inputStream = inputStreamFactory.createInputStream(aAsterixFile);
     return new TLcdASTERIXFinalReplayInputStream(inputStream, 20, aFileFormat);
   } catch (IOException e) {
     LOGGER.error(TLcdASTERIXFinalReplayInputStream.class.getName() + ".<init>", "The file [" + aAsterixFile + "] could not be found.", e);
     throw e;
   }
 }*/

 /*private void startLiveDecoder(
     ALcdASTERIXTransformationProvider aTransformationProvider,
     int aHistoryLength,
     final InputStream aInputStream) {
   liveDecoder = new TLcdASTERIXLiveDecoder();
   liveDecoder.setHistoryLength(aHistoryLength);
   if (aTransformationProvider != null) {
     liveDecoder.setTransformationProvider(aTransformationProvider);
   }
   
   liveDecoder.initDecode(aInputStream);
   fTimer = new Timer(1000, new ActionListener() {
     @Override
     public void actionPerformed(ActionEvent e) {
       try {
         int decodedMessages = 0;
         while (liveDecoder.blockAvailable()) {
           liveDecoder.decodeBlockSFCT(ILcdModel.FIRE_LATER, LiveDecodedModel.this);
           decodedMessages++;
         }
         if (decodedMessages > 0) {
           fireCollectedModelChanges();
           fCallback.update(LiveDecodedModel.this);
         }
       } catch (EOFException ex) {
    	   System.out.println("Error");
         ((Timer) e.getSource()).stop();
         fCallback.finished(LiveDecodedModel.this);
       } catch (IOException ex) {
         ((Timer) e.getSource()).stop();
         System.out.println("Error2");
         LOGGER.error(ex.getMessage(), ex);
         fCallback.error(LiveDecodedModel.this);
       }
     }
   });
   fTimer.setCoalesce(true);
   fTimer.setRepeats(true);
   fTimer.start();
 }*/
 
 private class ASTERIXListener implements Runnable{
	  byte[] buffer = new byte[128];
	  int port = 8600;
	  String ip = "239.192.86.0";
     MulticastSocket socket = null;
     DatagramPacket incomingDatagram;
     
	  public void run() {
		  System.out.println("ASTERIXListener iniciado...");
		try {
           // Se crea al grupo que recibirá los paquetes multicast
			InetAddress group = InetAddress.getByName(ip);
           socket = new MulticastSocket(port);
           socket.joinGroup(group);
           while(Boolean.TRUE) {
				incomingDatagram = new DatagramPacket(buffer, buffer.length);
				//System.out.println("ASTERIXListener esperando datos...");
				socket.receive(incomingDatagram);
				if(incomingDatagram.getLength() <= 36) //Evitamos decodificar el paquete que no trae tracks
					continue;
				byte[] pckg_buff = Arrays.copyOfRange(buffer,  0, incomingDatagram.getLength());
				InputStream inputStream = new ByteArrayInputStream( pckg_buff );
				
	        	liveDecoder.decodeSFCT( inputStream, ILcdModel.FIRE_LATER, LiveDecodedModel.this);
	        	
	        	/*ILcdModel lastModel = LiveDecodedModel.this.getModel( LiveDecodedModel.this.modelCount() - 1 );

				//Buscamos y eliminamos la traza anterior
	        	for(int i=0; i<LiveDecodedModel.this.modelCount(); i++) 
	        	{
	        		ILcdModel itModel = LiveDecodedModel.this.getModel(i);
	        		if(i != LiveDecodedModel.this.modelCount() - 1) //Evitamos comparar con él mismo
	        		{
	        			if( itModel.elements().nextElement().toString().equals( lastModel.elements().nextElement().toString() ) ) 
	        			{
	        				itModel.removeAllElements(ILcdModel.FIRE_NOW);
	        				LiveDecodedModel.this.removeModel( itModel );
	        				//System.out.println(lastModel.elements().nextElement().toString()+"->"+lastModel.elements().nextElement().getClass());
	        				//itModel.elementChanged(lastModel.elements().nextElement(), ILcdModel.FIRE_LATER);
	        				break;
	        			}
	        		}
	        	}*/
           }
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	  }
 }
 
 /*private class NAVDATAListener implements Runnable{
	  byte[] buffer = new byte[128];
	  int port = 5079;
	  String ip = "239.192.50.79";
     MulticastSocket socket = null;
     DatagramPacket incomingDatagram;
     
	  public void run() {
		try {
           // Se crea al grupo que recibirá los paquetes multicast
           InetAddress group = InetAddress.getByName(ip);
           socket = new MulticastSocket(port);
           socket.joinGroup(group);

           int tracksCount = 0;
           while(Boolean.TRUE) {
				incomingDatagram = new DatagramPacket(buffer, buffer.length);
				socket.receive(incomingDatagram);
//				if(incomingDatagram.getLength() <= 36) //Evitamos decodificar el paquete que no trae tracks
//					continue;

				byte[] pckg_buff = Arrays.copyOfRange(buffer,  0, incomingDatagram.getLength());
				InputStream inputStream = new ByteArrayInputStream( pckg_buff );
				
				System.out.println("InputStream: " + incomingDatagram);
           }
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
	  }
 }*/

 public interface ResultCallback {

   /**
    * Called when a track model was added to the {@code LiveDecodedModel}.
    * @param aModel the {@code LiveDecodedModel} for which a track model was added
    * @param aTrackModel the track model that was added to {@code aModel}.
    */
   void trackModelAdded(LiveDecodedModel aModel, ILcdModel aTrackModel);

   /**
    * Called when the {@code LiveDecodedModel} was updated.
    * @param aModel the updated model.
    */
   void update(LiveDecodedModel aModel);

   /**
    * Called when the {@code LiveDecodedModel} has finished updating.
    * @param aModel   the model.
    */
   void finished(LiveDecodedModel aModel);

   /**
    * Called when the {@code LiveDecodedModel} has encountered an error during updating.
    * @param aModel   the model.
    */
   void error(LiveDecodedModel aModel);
 }
}

