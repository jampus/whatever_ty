/**
 * Copyright (c).
 * All rights reserved.
 * 
 * Created on 2017-1-23
 * Id: ProcessBindReceiver.java,v 1.0 2017-1-23 下午3:12:39 Administrator
 */
package com.whty.smpp.message;

import org.apache.regexp.RESyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whty.smpp.client.SmscMain;
import com.whty.smpp.pdu.BindReceiver;
import com.whty.smpp.pdu.BindReceiverResp;
import com.whty.smpp.pdu.PduConstants;
import com.whty.smpp.service.Smsc;
import com.whty.smpp.service.StandardProtocolHandler;
import com.whty.smpp.util.LoggingUtilities;

/**
 * @ClassName ProcessBindReceiver
 * @author Administrator
 * @date 2017-1-23 下午3:12:39
 * @Description TODO(这里用一句话描述这个类的作用)
 */
public class ProcessBindReceiver implements IMessageResponse {

	Logger logger = LoggerFactory.getLogger(ProcessBindReceiver.class);
	private StandardProtocolHandler handler;
	Smsc smsc = Smsc.getInstance();

	public ProcessBindReceiver(StandardProtocolHandler handler) {
		this.handler = handler;
	}
	
	public boolean authenticate(String systemid, String password) {
		
		for (int i=0;i<SmscMain.getSystemids().length;i++) {
			if (SmscMain.getSystemids()[i].equals(systemid))
				if (SmscMain.getPasswords()[i].equals(password))
					return true;
				else
					return false;
		}
		return false;		
	}
	
	public boolean isValidSystemId(String systemid) {
		
		for (int i=0;i<SmscMain.getSystemids().length;i++) {
			if (SmscMain.getSystemids()[i].equals(systemid))
				return true;
		}
		return false;		
	}

	/**
	 * @author Administrator
	 * @date 2017-1-23
	 * @param message
	 * @param len
	 * @throws Exception
	 * @Description TODO(这里用一句话描述这个方法的作用)
	 * @see com.whty.smpp.message.IMessageResponse#processMessageResponse(byte[],
	 *      int)
	 */
	@Override
	public void processMessageResponse(byte[] message, int len)
			throws Exception {
		LoggingUtilities.hexDump(": BIND_RECEIVER:", message, len);
		byte[] respMessage;
		BindReceiver smppmsg = new BindReceiver();
		smppmsg.demarshall(message);
		LoggingUtilities.logDecodedPdu(smppmsg);
		BindReceiverResp smppresp = new BindReceiverResp(smppmsg, new String(
				Smsc.getSmscId()));

		if (handler.getSession().isBound()) {
			logger.debug("Session is already bound");
			handler.setWasInvalidBindState(true);
			respMessage = smppresp.errorResponse(smppresp.getCmd_id(),
					PduConstants.ESME_RINVBNDSTS, smppresp.getSeq_no());
			LoggingUtilities.hexDump(": BIND_RECEIVER (ESME_RINVBNDSTS),len:{}",respMessage,
					respMessage.length);
			LoggingUtilities.logDecodedPdu(smppresp);
			handler.getConnection().writeResponse(respMessage);
			return;
		}

		handler.getSession().setInterface_version(
				smppmsg.getInterface_version());

		if (authenticate(smppmsg.getSystem_id(), smppmsg.getPassword())) {
			handler.getSession().setBound(true);
			handler.getSession().setTransmitter(false);
			handler.getSession().setReceiver(true);
			handler.setFailedAuthentication(false);
			handler.setWasBindReceiverRequest(true);
			smsc.runningMoService();

			try {
				handler.setAddressRangeRegExp(smppmsg.getAddress_range());
			} catch (RESyntaxException e) {
				logger.debug("Invalid regular expression specified in BIND_RECEIVER address_range attribute");
				e.printStackTrace();
			}
			logger.info("New receiver session bound to SMPPSim");
		} else {
			logger.debug("Bind failed authentication check.");
			handler.setFailedAuthentication(true);
			if (isValidSystemId(smppmsg.getSystem_id())) {
				smppresp.setCmd_status(PduConstants.ESME_RINVPASWD);
			} else {
				smppresp.setCmd_status(PduConstants.ESME_RINVSYSID);
			}
			handler.getSession().setBound(false);
			handler.getSession().setTransmitter(false);
			handler.getSession().setReceiver(false);
		}

		if (!handler.failedAuthentication()) {
			respMessage = smppresp.marshall();
			logger.info("marshall resp message");
		} else {
			respMessage = smppresp.errorResponse(smppresp.getCmd_id(),
					smppresp.getCmd_status(), smppresp.getSeq_no());
			logger.error("fail to get smppresp message");
		}

		LoggingUtilities.hexDump(": BIND_RECEIVER_RESP:len:{}",respMessage, respMessage.length);
		LoggingUtilities.logDecodedPdu(smppresp);

		handler.getConnection().writeResponse(respMessage);
		if (!handler.failedAuthentication()) {
			smsc.getIq().deliverPendingMoMessages();
		}
	}

}
