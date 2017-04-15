package info.nightscout.androidaps.plugins.DanaRAPS.comm;

import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;

import info.nightscout.androidaps.Config;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.db.TempBasal;
import info.nightscout.androidaps.events.EventTempBasalChange;
import info.nightscout.androidaps.plugins.DanaR.DanaRPlugin;
import info.nightscout.androidaps.plugins.DanaR.DanaRPump;
import info.nightscout.androidaps.plugins.DanaR.comm.MessageBase;

public class MsgStatusAPSTempBasal extends MessageBase {
    private static Logger log = LoggerFactory.getLogger(MsgStatusAPSTempBasal.class);

    public MsgStatusAPSTempBasal() {
        SetCommand(0xE001);
    }

    public void handleMessage(byte[] bytes) {
        double iob = intFromBuff(bytes, 0, 2) / 100d;
        int tempBasalPercent = intFromBuff(bytes, 2, 2);

        DanaRPump pump = DanaRPump.getInstance();
        pump.isTempBasalInProgress = tempBasalPercent != 100;
        pump.tempBasalPercent = tempBasalPercent;
        pump.iob = iob;


        if (Config.logDanaMessageDetail) {
            log.debug("Is APS temp basal running: " + pump.isTempBasalInProgress);
            log.debug("Current APS temp basal percent: " + tempBasalPercent);
            log.debug("Current pump IOB: " + iob);
        }
    }

}
