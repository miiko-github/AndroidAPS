package info.nightscout.androidaps.plugins.DanaRAPS.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nightscout.androidaps.Config;
import info.nightscout.androidaps.MainApp;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.events.EventRefreshGui;
import info.nightscout.androidaps.interfaces.PluginBase;
import info.nightscout.androidaps.plugins.DanaR.DanaRPlugin;
import info.nightscout.androidaps.plugins.DanaR.DanaRPump;
import info.nightscout.androidaps.plugins.DanaR.comm.MessageBase;
import info.nightscout.androidaps.plugins.DanaRAPS.DanaRAPSPlugin;
import info.nightscout.androidaps.plugins.DanaRKorean.DanaRKoreanPlugin;
import info.nightscout.utils.ToastUtils;

/**
 * Created by mike on 30.06.2016.
 */
public class MsgCheckValue extends MessageBase {
    private static Logger log = LoggerFactory.getLogger(MsgCheckValue.class);

    public MsgCheckValue() {
        SetCommand(0xF0F1);
    }

    @Override
    public void handleMessage(byte[] bytes) {
        DanaRPump pump = DanaRPump.getInstance();

        pump.isNewPump = true;
        log.debug("New firmware confirmed");

        pump.model = intFromBuff(bytes, 0, 1);
        pump.protocol = intFromBuff(bytes, 1, 1);
        pump.productCode = intFromBuff(bytes, 2, 1);
        if (pump.model != DanaRPump.EXPORT_MODEL) {
            ToastUtils.showToastInUiThread(MainApp.instance().getApplicationContext(), MainApp.sResources.getString(R.string.wrongpumpdriverselected), R.raw.error);
            ((DanaRAPSPlugin) MainApp.getSpecificPlugin(DanaRAPSPlugin.class)).doDisconnect("Wrong Model");
            log.debug("Wrong model selected");
        }

        if (pump.protocol != 2) {
            ToastUtils.showToastInUiThread(MainApp.instance().getApplicationContext(),MainApp.sResources.getString(R.string.wrongpumpdriverselected), R.raw.error);
            ((DanaRAPSPlugin)MainApp.getSpecificPlugin(DanaRKoreanPlugin.class)).doDisconnect("Wrong Model");
            log.debug("Wrong model selected. Switching to non APS DanaR");
            ((DanaRAPSPlugin)MainApp.getSpecificPlugin(DanaRPlugin.class)).setFragmentEnabled(PluginBase.PUMP, false);
            ((DanaRAPSPlugin)MainApp.getSpecificPlugin(DanaRPlugin.class)).setFragmentVisible(PluginBase.PUMP, false);
            ((DanaRPlugin)MainApp.getSpecificPlugin(DanaRPlugin.class)).setFragmentEnabled(PluginBase.PUMP, true);
            ((DanaRPlugin)MainApp.getSpecificPlugin(DanaRPlugin.class)).setFragmentVisible(PluginBase.PUMP, true);

            //If profile coming from pump, switch it as well
            if(MainApp.getSpecificPlugin(DanaRAPSPlugin.class).isEnabled(PluginBase.PROFILE)){
                (MainApp.getSpecificPlugin(DanaRAPSPlugin.class)).setFragmentEnabled(PluginBase.PROFILE, false);
                (MainApp.getSpecificPlugin(DanaRPlugin.class)).setFragmentEnabled(PluginBase.PROFILE, true);
            }

            MainApp.getConfigBuilder().storeSettings();
            MainApp.bus().post(new EventRefreshGui(false));
            return;
        }
        if (Config.logDanaMessageDetail) {
            log.debug("Model: " + String.format("%02X ", pump.model));
            log.debug("Protocol: " + String.format("%02X ", pump.protocol));
            log.debug("Product Code: " + String.format("%02X ", pump.productCode));
        }
    }

}
